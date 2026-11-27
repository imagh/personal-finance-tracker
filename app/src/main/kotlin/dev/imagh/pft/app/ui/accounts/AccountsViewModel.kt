package dev.imagh.pft.app.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.log.EntityAction
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.validation.LedgerError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Everything the accounts list shows.
 *
 * @property active accounts offered for new transactions, by name ignoring case (core's order).
 * @property archived archived accounts, in the same order.
 * @property loaded false until the first read finishes, so the screen does not flash "No accounts yet".
 * @property undo the account whose delete can still be undone, or null. Only the latest delete is
 *   offered. Never shown or logged: it only identifies the offer.
 * @property failedAction the action that failed unexpectedly and has not been shown yet; the screen
 *   shows a fixed "Couldn't ... Try again." message and calls [AccountsViewModel.failureShown].
 * @property inUse true after a delete was refused because transactions use the account and has not been
 *   shown yet; the screen shows the fixed "In use by transactions. Archive it instead." message and calls
 *   [AccountsViewModel.inUseShown].
 */
data class AccountsUiState(
    val active: List<Account> = emptyList(),
    val archived: List<Account> = emptyList(),
    val loaded: Boolean = false,
    val undo: AccountId? = null,
    val failedAction: EntityAction? = null,
    val inUse: Boolean = false,
)

/**
 * The accounts list's state and actions (plan D2, D11): archive, unarchive, delete and its undo.
 *
 * - **Read on demand (D2).** The lists are read from [AccountService.lists] on construction, after
 *   every action and whenever the screen asks ([reload], when it resumes), so nothing observes the
 *   database. A read that throws (for example a corrupt row, which core refuses) is not caught: it
 *   fails loudly like `LaunchViewModel` rather than showing a made-up list.
 * - **One at a time.** Actions and reads run one after another (a [Mutex]), so a read that started
 *   before an action never overwrites the list with older data.
 * - **Failures (A5 save shape).** Deleting an account that transactions use ([LedgerError.InUse]) is an
 *   answer: [AccountsUiState.inUse] shows a fixed message, nothing is written and nothing is logged. Any
 *   other [LedgerError] (the account was deleted or never existed, for example from another path) is a
 *   stale list, not an error: the list is simply refreshed. Any other
 *   exception leaves the list as it was, sets [AccountsUiState.failedAction] (a fixed message, never
 *   exception text) and logs one enum-only `entity_action_failed` event. A cancellation is rethrown.
 *   Every action is one audited transaction, so a failed one leaves nothing behind and can be retried.
 * - **Undo.** A successful [delete] offers [AccountsUiState.undo]; [undo] restores that account
 *   (an audited RESTORE) and clears the offer, as does [dismissUndo].
 *
 * @param accounts the accounts service (audited writes and the lists).
 */
class AccountsViewModel(
    private val accounts: AccountService,
) : ViewModel() {
    private val mutableState = MutableStateFlow(AccountsUiState())
    private val serial = Mutex()

    /** The screen's state. */
    val state: StateFlow<AccountsUiState> = mutableState.asStateFlow()

    init {
        reload()
    }

    /** Reads the lists again (the screen calls this when it resumes, after an edit elsewhere). */
    fun reload() {
        viewModelScope.launch { serial.withLock { refresh() } }
    }

    /** Archives [id]; it moves to the archived section. */
    fun archive(id: AccountId) = perform(EntityAction.ARCHIVE) { accounts.archive(id) }

    /** Makes the archived account [id] active again. */
    fun unarchive(id: AccountId) = perform(EntityAction.UNARCHIVE) { accounts.unarchive(id) }

    /** Deletes [id] (a tombstone) and offers to undo it. */
    fun delete(id: AccountId) = perform(EntityAction.DELETE, offerUndoFor = id) { accounts.delete(id) }

    /** Restores the account whose delete is on offer; does nothing without an offer. */
    fun undo() {
        val id = mutableState.value.undo ?: return
        perform(EntityAction.RESTORE, consumesOffer = true) { accounts.restore(id) }
    }

    /** The offer ended without Undo (timeout, swipe, or the screen went away): the delete stands. */
    fun dismissUndo() = mutableState.update { it.copy(undo = null) }

    /** The screen showed the failure message. */
    fun failureShown() = mutableState.update { it.copy(failedAction = null) }

    /** The screen showed the in-use message. */
    fun inUseShown() = mutableState.update { it.copy(inUse = false) }

    private suspend fun refresh(
        offerUndoFor: AccountId? = null,
        inUse: Boolean = false,
    ) {
        val lists = accounts.lists()
        mutableState.update {
            it.copy(
                active = lists.active,
                archived = lists.archived,
                loaded = true,
                undo = offerUndoFor ?: it.undo,
                inUse = inUse || it.inUse,
            )
        }
    }

    private fun perform(
        action: EntityAction,
        offerUndoFor: AccountId? = null,
        consumesOffer: Boolean = false,
        write: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            serial.withLock {
                var offer: AccountId? = null
                var blocked = false
                try {
                    write()
                    offer = offerUndoFor
                } catch (_: LedgerError.InUse) {
                    // An answer, not a failure: transactions use it. Nothing was written and nothing is logged.
                    blocked = true
                } catch (_: LedgerError) {
                    // The account is gone or was never there (changed elsewhere): the refresh below shows the truth.
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Deliberately broad: whatever went wrong, the user can retry, and the write is one
                    // transaction so nothing partial exists. The exception is dropped on purpose: logs and
                    // screens must not carry its text (SEC-5).
                    SafeLog.event(LogEvent.EntityActionFailed(EntityKind.ACCOUNT, action))
                    mutableState.update {
                        it.copy(failedAction = action, undo = if (consumesOffer) null else it.undo)
                    }
                    return@withLock
                }
                refresh(offer, blocked)
                if (consumesOffer) mutableState.update { it.copy(undo = null) }
            }
        }
    }

    companion object {
        /** A factory that builds the ViewModel over [services] (there is no DI framework). */
        fun factory(services: AppServices): ViewModelProvider.Factory =
            viewModelFactory { initializer { AccountsViewModel(services.accounts) } }
    }
}
