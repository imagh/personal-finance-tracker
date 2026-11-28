package dev.imagh.pft.app.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.ui.common.SaveOutcome
import dev.imagh.pft.app.ui.common.launchSave
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.transaction.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Creates or edits one expense, income or transfer with one item (slice C D8). Its save shape is `launchSave`,
 * shared with the other forms. Add and edit are one ViewModel; the items editor arrives in C4c.
 *
 * - **Validation is core's.** Each text goes through `FieldState.check` with core's own rule
 *   (`Validation.requireOptionalName` with the title, note and bank-reference limits) and the amount through the
 *   shared `checkAmount` (then positive: zero is `InvalidAmount`). An account must be chosen, and a transfer's
 *   destination must be another one. Every invalid field shows its error at once and nothing is saved; the core
 *   services validate again on save.
 * - **Type.** A new form opens as an expense, or as the route's `type` preset (`EXPENSE`, `INCOME`, `TRANSFER`;
 *   anything else, or none, is an expense; ignored when editing). Switching expense and income clears a category
 *   of the other kind and sets [TransactionEditUiState.categoryCleared]; a transfer keeps the category hidden, so
 *   switching to it and back loses nothing. A stored transfer never changes type, and a stored expense or income
 *   never becomes a transfer.
 * - **Date and time (Q6).** A new entry takes the time of day the form was opened at; changing the date keeps the
 *   time of day. An edit whose date is unchanged keeps the stored instant to the millisecond; one whose date
 *   changed keeps the stored time of day. The zone is the clock's, read when the form opens.
 * - **Edit mode** reads the transaction (or, for a transfer, both legs from either leg's id) and prefills every
 *   field. The id is untrusted text: a blank, unknown, deleted or malformed one ends in
 *   [TransactionEditUiState.notFound], and so does an itemised transaction (more than one item), which this form
 *   cannot show and must not flatten. The single item keeps its id and note, so an edit audits only what
 *   changed. An archived category, tag or account already on the transaction stays as stored unless changed.
 * - **One save.** [submit] sets `saving` before it launches the write and ignores calls while saving, once saved,
 *   before the form has loaded and after not-found. It must be called on the main thread (the UI does), which
 *   makes the check-then-set atomic.
 * - **A vanished transaction** (deleted after the form loaded) ends an edit in not-found with nothing logged; a
 *   reference that vanished while creating shows the fixed failure message, also with nothing logged.
 * - **An unexpected failure** saves nothing, resets `saving`, sets `saveFailed` (a fixed message, never exception
 *   text), keeps the typed text and logs one enum-only `entity_save_failed kind=TRANSACTION` line. Retrying is
 *   safe: each write is one audited transaction. No title, note, bank reference, amount or id is ever logged.
 * - **Reading** is not caught: a corrupt row fails loudly, as on the lists.
 *
 * @param store reads the stored transaction and writes what the form made (through the core services).
 * @param lookups the lists the pickers offer and the names shown.
 * @param clock gives the clock for "now" and the zone, read when the form opens.
 * @param request what the route opened the form with (untrusted text).
 */
internal class TransactionEditViewModel(
    private val store: EditStore,
    private val lookups: EditLookups,
    clock: () -> Clock,
    private val request: TransactionEditRequest,
) : ViewModel() {
    private val opened: Clock = clock()
    private val zone: ZoneId = opened.zone
    private val mutableState = MutableStateFlow(TransactionEditUiState())

    /** The screen's state; nothing is drawn until [TransactionEditUiState.loaded]. */
    val state: StateFlow<TransactionEditUiState> = mutableState.asStateFlow()

    /** The time of day an entry has: now for a new one, the stored one for an edit. Main thread only. */
    private var timeOfDay: LocalTime = LocalTime.ofInstant(opened.instant(), zone).truncatedTo(ChronoUnit.MILLIS)

    /** The stored transaction being edited, once read; null when creating. Main thread only. */
    private var stored: StoredTransaction? = null

    init {
        viewModelScope.launch { load() }
    }

    /** The type was chosen (see the class comment for what it clears); a type not on offer is ignored. */
    fun onTypeChange(type: TransactionType) =
        mutableState.update { form ->
            if (type == form.type || type !in form.typeOptions) return@update form
            val kind = categoryKindOf(type)
            val current = form.categoryId
            val mismatch = kind != null && current != null && form.names.categories[current]?.kind != kind
            form.copy(
                type = type,
                categoryId = if (mismatch) null else current,
                categoryCleared = mismatch,
                saveFailed = false,
            )
        }

    /** The text field [field] was edited to [text]; its error, and any save-failed message, clears. */
    fun onTextChange(
        field: EditField,
        text: String,
    ) = mutableState.update { form ->
        when (field) {
            EditField.AMOUNT -> form.copy(amount = form.amount.edit(text))
            EditField.TITLE -> form.copy(title = form.title.edit(text))
            EditField.DESCRIPTION -> form.copy(description = form.description.edit(text))
            EditField.BANK_REF -> form.copy(bankRef = form.bankRef.edit(text))
        }.copy(saveFailed = false)
    }

    /** The account (a transfer's source) was chosen. */
    fun onAccountPick(id: AccountId) =
        mutableState.update { it.copy(accountId = id, accountError = null, saveFailed = false) }

    /** A transfer's destination was chosen. */
    fun onToAccountPick(id: AccountId) =
        mutableState.update { it.copy(toAccountId = id, toAccountError = null, saveFailed = false) }

    /** The day was changed; the time of day stays. */
    fun onDateChange(date: LocalDate) = mutableState.update { it.copy(date = date, saveFailed = false) }

    /** A category was picked; the cleared-category notice goes. */
    fun onCategoryPick(id: CategoryId) =
        mutableState.update { it.copy(categoryId = id, categoryCleared = false, saveFailed = false) }

    /** The tag picker's Done: [ids] is the whole new set (an archived tag the user did not drop is in it). */
    fun onTagsDone(ids: Set<TagId>) = mutableState.update { it.copy(tagIds = ids, saveFailed = false) }

    /**
     * Save was tapped: validates every field and, if all are valid, saves. Errors appear on the field(s) at
     * fault and nothing is saved then. Ignored while saving, once saved, before the form has loaded, and when the
     * transaction was not found.
     */
    fun submit() {
        val current = mutableState.value
        if (current.blocked) return
        val verdict = current.validated()
        val values = verdict.values
        if (values == null) {
            mutableState.value = verdict.form.copy(saveFailed = false)
            return
        }
        mutableState.value = verdict.form.copy(saving = true, saveFailed = false)
        // Built now, so a later edit of the form cannot change what is saved.
        val draft = current.draftFor(values, occurredAt(current.date, stored, zone, timeOfDay), stored)
        val target = stored
        viewModelScope.launchSave(EntityKind.TRANSACTION, ::finished) { store.save(draft, target) }
    }

    /** The save ended as [outcome] (see [launchSave]); the failure has been logged by now. */
    private fun finished(outcome: SaveOutcome) =
        mutableState.update {
            when (outcome) {
                SaveOutcome.SAVED -> it.copy(saving = false, saved = true)
                // An edited transaction that vanished is a stale screen; a reference that vanished while creating
                // is something the user can redo, so it reads as a failed save (nothing logged either way).
                SaveOutcome.VANISHED -> if (it.editing) it.copy(saving = false, notFound = true) else it.failedSave()
                SaveOutcome.FAILED -> it.failedSave()
            }
        }

    /** Reads the lists the pickers offer and, when editing, the transaction; ends in not found if it is unusable. */
    private suspend fun load() {
        val form =
            TransactionEditUiState(
                editing = request.id != null,
                loaded = true,
                choices = lookups.choices(),
                names = lookups.names(),
                date = LocalDate.now(opened),
            )
        val raw = request.id
        val found = raw?.let { store.read(it) }
        stored = found
        found?.let {
            timeOfDay =
                it.transaction.occurredAt
                    .atZone(zone)
                    .toLocalTime()
        }
        mutableState.value =
            when {
                raw == null -> form.copy(type = presetType(request.type), typeOptions = NEW_TYPES)
                found == null -> form.copy(notFound = true)
                else -> form.filledFrom(found, zone)
            }
    }

    companion object {
        /** The types a new transaction can be, in the order shown. */
        private val NEW_TYPES = listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.TRANSFER)

        /** The type the route's preset names; anything else is an expense. Matched by name, never by position. */
        private fun presetType(raw: String?): TransactionType =
            TransactionType.entries.firstOrNull { it.name == raw } ?: TransactionType.EXPENSE

        /** The kind of category a type takes, or null for a transfer (it takes none). */
        private fun categoryKindOf(type: TransactionType): CategoryKind? =
            when (type) {
                TransactionType.EXPENSE -> CategoryKind.EXPENSE
                TransactionType.INCOME -> CategoryKind.INCOME
                TransactionType.TRANSFER -> null
            }

        /**
         * A factory that builds the ViewModel over [services] (there is no DI framework).
         *
         * @param clock gives the clock for "now" and the zone.
         * @param request what the route opened the form with (untrusted).
         */
        fun factory(
            services: AppServices,
            clock: () -> Clock,
            request: TransactionEditRequest,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    TransactionEditViewModel(
                        EditStore(services.transactions, services.transfers),
                        EditLookups(services.accounts, services.categories, services.tags),
                        clock,
                        request,
                    )
                }
            }
    }
}
