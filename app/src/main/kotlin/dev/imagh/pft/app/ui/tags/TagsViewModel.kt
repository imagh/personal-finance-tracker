package dev.imagh.pft.app.ui.tags

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
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagService
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
 * Everything the tags list shows.
 *
 * @property active tags offered for new items, by name ignoring case (core's order).
 * @property archived archived tags, in the same order.
 * @property loaded false until the first read finishes, so the screen does not flash "No tags yet".
 * @property undo the tag whose delete can still be undone, or null. Only the latest delete is offered.
 *   Never shown or logged: it only identifies the offer.
 * @property failedAction the action that failed unexpectedly and has not been shown yet; the screen
 *   shows a fixed "Couldn't ... Try again." message and calls [TagsViewModel.failureShown].
 * @property inUse true after a delete was refused because transactions use the tag and has not been
 *   shown yet; the screen shows the fixed "In use by transactions. Archive it instead." message and calls
 *   [TagsViewModel.inUseShown].
 */
data class TagsUiState(
    val active: List<Tag> = emptyList(),
    val archived: List<Tag> = emptyList(),
    val loaded: Boolean = false,
    val undo: TagId? = null,
    val failedAction: EntityAction? = null,
    val inUse: Boolean = false,
)

/**
 * The tags list's state and actions (plan D2, D11): archive, unarchive, delete and its undo. Tags have
 * no order of their own, so there is no Move up or Move down.
 *
 * - **Read on demand (D2).** The lists are read from [TagService.lists] on construction, after every
 *   action and whenever the screen asks ([reload], when it resumes), so nothing observes the database. A
 *   read that throws (for example a corrupt row, which core refuses) is not caught: it fails loudly like
 *   `LaunchViewModel` rather than showing a made-up list.
 * - **One at a time.** Actions and reads run one after another (a [Mutex]), so a read that started
 *   before an action never overwrites the list with older data.
 * - **Failures (A5 save shape).** Deleting a tag that transactions use ([LedgerError.InUse]) sets
 *   [TagsUiState.inUse], a fixed message. Any other [LedgerError] (the tag was deleted or never existed,
 *   from another path; or an undo whose name was taken meanwhile) is an answer too: nothing is written, the
 *   list is refreshed and nothing is logged. Any other exception leaves the list as it was, sets
 *   [TagsUiState.failedAction] (a fixed message, never exception text) and logs one enum-only
 *   `entity_action_failed` event. A cancellation is rethrown. Every action is one audited transaction, so
 *   a failed one leaves nothing behind and can be retried.
 * - **Undo.** A successful [delete] offers [TagsUiState.undo]; [undo] restores that tag (an audited
 *   RESTORE) and clears the offer, as does [dismissUndo].
 *
 * @param tags the tags service (audited writes and the lists).
 */
class TagsViewModel(
    private val tags: TagService,
) : ViewModel() {
    private val mutableState = MutableStateFlow(TagsUiState())
    private val serial = Mutex()

    /** The screen's state. */
    val state: StateFlow<TagsUiState> = mutableState.asStateFlow()

    init {
        reload()
    }

    /** Reads the lists again (the screen calls this when it resumes, after an edit elsewhere). */
    fun reload() {
        viewModelScope.launch { serial.withLock { refresh() } }
    }

    /** Archives [id]; it moves to the archived section. */
    fun archive(id: TagId) = perform(EntityAction.ARCHIVE) { tags.archive(id) }

    /** Makes the archived tag [id] active again. */
    fun unarchive(id: TagId) = perform(EntityAction.UNARCHIVE) { tags.unarchive(id) }

    /** Deletes [id] (a tombstone) and offers to undo it. */
    fun delete(id: TagId) = perform(EntityAction.DELETE, offerUndoFor = id) { tags.delete(id) }

    /** Restores the tag whose delete is on offer; does nothing without an offer. */
    fun undo() {
        val id = mutableState.value.undo ?: return
        perform(EntityAction.RESTORE, consumesOffer = true) { tags.restore(id) }
    }

    /** The offer ended without Undo (timeout, swipe, or the screen went away): the delete stands. */
    fun dismissUndo() = mutableState.update { it.copy(undo = null) }

    /** The screen showed the failure message. */
    fun failureShown() = mutableState.update { it.copy(failedAction = null) }

    /** The screen showed the in-use message. */
    fun inUseShown() = mutableState.update { it.copy(inUse = false) }

    private suspend fun refresh(
        offerUndoFor: TagId? = null,
        inUse: Boolean = false,
    ) {
        val lists = tags.lists()
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
        offerUndoFor: TagId? = null,
        consumesOffer: Boolean = false,
        write: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            serial.withLock {
                var offer: TagId? = null
                var blocked = false
                try {
                    write()
                    offer = offerUndoFor
                } catch (_: LedgerError.InUse) {
                    // An answer, not a failure: transactions use it. Nothing was written and nothing is logged.
                    blocked = true
                } catch (_: LedgerError) {
                    // The tag is gone, or its name was taken since it was deleted: an answer, not a failure.
                    // The refresh below shows the truth.
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Deliberately broad: whatever went wrong, the user can retry, and the write is one
                    // transaction so nothing partial exists. The exception is dropped on purpose: logs and
                    // screens must not carry its text (SEC-5).
                    SafeLog.event(LogEvent.EntityActionFailed(EntityKind.TAG, action))
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
            viewModelFactory { initializer { TagsViewModel(services.tags) } }
    }
}
