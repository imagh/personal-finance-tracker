package dev.imagh.pft.app.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.SaveOutcome
import dev.imagh.pft.app.ui.common.checkAmount
import dev.imagh.pft.app.ui.common.launchSave
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountRules
import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Everything the account form shows.
 *
 * @property editing true when an existing account is being edited, false when one is being created.
 * @property loaded false until the account being edited has been read, so the screen draws no fields
 *   and no "not found" before the answer is known. Always true when creating.
 * @property notFound true when the account asked for does not exist, was deleted, or the id was not
 *   an id at all (the id is an untrusted navigation argument). The route then leaves the screen.
 * @property name the account's name, as typed, with its error.
 * @property type the kind of account; it decides the amount's label and sign.
 * @property lastFour the optional last four digits, as typed, with its error.
 * @property issuer the optional bank or provider, as typed, with its error.
 * @property amount the opening balance as a positive amount, as typed ("Current balance", or "Amount
 *   owed" for a credit card), with its error. Its sign is decided by [type] when saving.
 * @property saving true from a valid Save until the save finishes; the screen disables Save.
 * @property saved true once the account is saved; the route then leaves the screen. A one-shot
 *   outcome held as state, not a channel.
 * @property saveFailed true after an unexpected failure to save: the screen shows a fixed "try again"
 *   message. It is cleared by editing a field or by submitting again; the typed text is kept.
 */
data class AccountEditUiState(
    val editing: Boolean = false,
    val loaded: Boolean = true,
    val notFound: Boolean = false,
    val name: FieldState = FieldState(),
    val type: AccountType = AccountType.BANK,
    val lastFour: FieldState = FieldState(),
    val issuer: FieldState = FieldState(),
    val amount: FieldState = FieldState(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val saveFailed: Boolean = false,
)

/**
 * Creates or edits one account (plan D4, D12, Q3). Its save shape is [launchSave], shared with the
 * category form (extracted in B4b once the second form proved it the same, D12); `SetupViewModel` keeps
 * its own, which differs in the event it logs and has no vanished case.
 *
 * - **Validation is core's.** Each field goes through `FieldState.check` with core's own rule
 *   (`Validation.requireName`, `AccountRules.requireMaskedIdentifier`, `Validation.requireOptionalName`)
 *   and the shared amount rule (`checkAmount`, which wraps `PaiseParser`). Every invalid field shows
 *   its own error at once and nothing is saved; `AccountService` validates again on save.
 * - **The balance sign follows the type (Q3).** The amount is typed as a positive number. A credit
 *   card stores it negated ("Amount owed"); bank, cash and wallet store it as entered ("Current
 *   balance"). The sign is applied when saving, so switching the type after typing flips the stored
 *   sign and never touches the text.
 * - **Edit mode** reads the account on construction ([AccountService.all], so an archived account can
 *   be edited too) and prefills every field; a negative card balance is shown as a positive amount.
 *   An unknown, deleted or malformed id ends in [AccountEditUiState.notFound]. **An untouched balance
 *   is saved as it was stored**: if the type and the amount text are as prefilled, the stored value is
 *   kept, so editing the name of an account whose sign does not follow its type (an overdrawn bank
 *   account, created by core or an import) cannot silently flip it. Changing either applies the rule.
 *   An edit that changes nothing writes no audit entry (the core service sees an identical value).
 * - **One save.** [submit] sets [AccountEditUiState.saving] before it launches the save and ignores
 *   calls while saving, once saved, before an edit is loaded, or after not-found. It must be called on
 *   the main thread (the UI does), which makes the check-then-set atomic.
 * - **A vanished account** (deleted after the form loaded) ends in [AccountEditUiState.notFound]
 *   with nothing logged: it is a stale screen, not an error.
 * - **An unexpected failure** (anything else, not a cancellation) saves nothing, resets
 *   [AccountEditUiState.saving], sets [AccountEditUiState.saveFailed] (a fixed message, never exception
 *   text), keeps the typed text, and logs one enum-only `entity_save_failed` event. Retrying is safe: the
 *   write is one audited transaction, so a failed attempt leaves nothing behind. A cancellation is
 *   rethrown, never reported.
 * - **Reading** the account is not caught: a corrupt row fails loudly, as on the list.
 *
 * @param accounts the accounts service.
 * @param accountId the id of the account to edit as it arrived (untrusted text), or null to create.
 */
class AccountEditViewModel(
    private val accounts: AccountService,
    accountId: String?,
) : ViewModel() {
    private val mutableState =
        MutableStateFlow(AccountEditUiState(editing = accountId != null, loaded = accountId == null))

    /** The screen's state. */
    val state: StateFlow<AccountEditUiState> = mutableState.asStateFlow()

    /** The account being edited, once read; null when creating. Main thread only. */
    private var original: Account? = null

    /** The amount text the form was prefilled with, to tell an untouched balance from an edited one. */
    private var prefilledAmount: String? = null

    init {
        if (accountId != null) {
            viewModelScope.launch { load(accountId) }
        }
    }

    /** The name field was edited to [text]; its error, and any save-failed message, clears. */
    fun onNameChange(text: String) = mutableState.update { it.copy(name = it.name.edit(text), saveFailed = false) }

    /** The type was chosen; the amount's label and sign follow it, its text is left alone. */
    fun onTypeChange(type: AccountType) = mutableState.update { it.copy(type = type, saveFailed = false) }

    /** The last-four field was edited to [text]; its error, and any save-failed message, clears. */
    fun onLastFourChange(text: String) =
        mutableState.update { it.copy(lastFour = it.lastFour.edit(text), saveFailed = false) }

    /** The issuer field was edited to [text]; its error, and any save-failed message, clears. */
    fun onIssuerChange(text: String) =
        mutableState.update { it.copy(issuer = it.issuer.edit(text), saveFailed = false) }

    /** The amount field was edited to [text]; its error, and any save-failed message, clears. */
    fun onAmountChange(text: String) =
        mutableState.update { it.copy(amount = it.amount.edit(text), saveFailed = false) }

    /**
     * Save was tapped: validates every field and, if all are valid, saves. Errors appear on the
     * field(s) at fault; nothing is saved then. Ignored while saving, once saved, before an edit has
     * loaded, and when the account is not found.
     */
    fun submit() {
        val current = mutableState.value
        if (current.busy || current.closed) return
        val name = current.name.check { Validation.requireName(it, LedgerLimits.MAX_NAME_LENGTH) }
        val lastFour = current.lastFour.check { AccountRules.requireMaskedIdentifier(it) }
        val issuer = current.issuer.check { Validation.requireOptionalName(it, LedgerLimits.MAX_NAME_LENGTH) }
        val amount = current.amount.checkAmount()
        val checked =
            current.copy(name = name.state, lastFour = lastFour.state, issuer = issuer.state, amount = amount.state)
        // A null value is "no text" for the two optional fields, so their validity is read from the error.
        val optionalsValid = lastFour.state.error == null && issuer.state.error == null
        val validName = name.value
        val validAmount = amount.value
        if (validName == null || validAmount == null || !optionalsValid) {
            mutableState.value = checked.copy(saveFailed = false)
            return
        }
        val draft = AccountDraft(validName, current.type, balance(current, validAmount), lastFour.value, issuer.value)
        mutableState.value = checked.copy(saving = true, saveFailed = false)
        save(draft)
    }

    /** Reads the account asked for and prefills the form, or ends in not found. */
    private suspend fun load(rawId: String) {
        // Compared as text, never turned into an id first: the argument is untrusted.
        val account = accounts.all().firstOrNull { it.meta.id.value == rawId }
        if (account == null) {
            mutableState.update { it.copy(loaded = true, notFound = true) }
            return
        }
        original = account
        val amountText = PaiseFormat.format(account.openingBalance).removePrefix(RUPEE_SIGN)
        prefilledAmount = amountText
        mutableState.update {
            it.copy(
                loaded = true,
                name = FieldState(account.name),
                type = account.type,
                lastFour = FieldState(account.maskedIdentifier.orEmpty()),
                issuer = FieldState(account.issuer.orEmpty()),
                amount = FieldState(amountText),
            )
        }
    }

    /** The opening balance to store: the stored value if untouched, else [magnitude] signed by the type. */
    private fun balance(
        form: AccountEditUiState,
        magnitude: Paise,
    ): Paise {
        val untouched = original?.takeIf { it.type == form.type && form.amount.text == prefilledAmount }
        return when {
            untouched != null -> untouched.openingBalance
            form.type == AccountType.CREDIT_CARD -> Paise(-magnitude.value)
            else -> magnitude
        }
    }

    private fun save(draft: AccountDraft) =
        viewModelScope.launchSave(EntityKind.ACCOUNT, ::finished) {
            val target = original
            if (target == null) accounts.create(draft) else accounts.edit(target.meta.id, draft)
        }

    /** The save ended as [outcome] (see [launchSave]); the failure has been logged by now. */
    private fun finished(outcome: SaveOutcome) =
        mutableState.update {
            when (outcome) {
                SaveOutcome.SAVED -> it.copy(saving = false, saved = true)
                // The account vanished since the form loaded: a stale screen, not an error.
                SaveOutcome.VANISHED -> it.copy(saving = false, notFound = true)
                SaveOutcome.FAILED -> it.copy(saving = false, saveFailed = true)
            }
        }

    companion object {
        /** The rupee sign [PaiseFormat] puts first; the form shows it as a prefix, not as text. */
        private const val RUPEE_SIGN = "₹"

        /**
         * A factory that builds the ViewModel over [services] (there is no DI framework).
         *
         * @param accountId the account to edit as it arrived (untrusted), or null to create.
         */
        fun factory(
            services: AppServices,
            accountId: String?,
        ): ViewModelProvider.Factory =
            viewModelFactory { initializer { AccountEditViewModel(services.accounts, accountId) } }
    }
}

/** The form cannot take a Save right now: a save is running, or an edit has not loaded yet. */
private val AccountEditUiState.busy: Boolean get() = saving || !loaded

/** The form's work is over: it saved, or the account is gone. */
private val AccountEditUiState.closed: Boolean get() = saved || notFound
