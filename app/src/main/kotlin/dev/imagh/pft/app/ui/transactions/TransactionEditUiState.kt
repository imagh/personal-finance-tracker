package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.TagLists
import dev.imagh.pft.ledger.transaction.TransactionType
import java.time.LocalDate

/**
 * What the transaction form was opened with, exactly as it arrived in the route: **untrusted text**, judged
 * by the ViewModel (slice C D7).
 *
 * @property id the transaction to edit (for a transfer, either leg's id), or null to create.
 * @property type the preset type of a new transaction (`EXPENSE`, `INCOME` or `TRANSFER`); anything else, or
 *   none, reads as an expense, and it is ignored when [id] is given.
 */
internal data class TransactionEditRequest(
    val id: String? = null,
    val type: String? = null,
)

/** The form's text fields, so one ViewModel function edits any of them. */
internal enum class EditField {
    /** The amount in rupees. */
    AMOUNT,

    /** The optional title. */
    TITLE,

    /** The optional note. */
    DESCRIPTION,

    /** The optional bank reference. */
    BANK_REF,
}

/**
 * The lists the form's pickers offer, read once when it opens.
 *
 * @property accounts the accounts a new choice can be made from: active ones only, in the account service's
 *   order.
 * @property expenseCategories the categories an expense can take (live, not archived, not under an archived
 *   parent), in core's order.
 * @property incomeCategories the same for an income.
 * @property tags the tags: active ones are offered, an archived one only where the transaction already has it.
 */
internal data class EditChoices(
    val accounts: List<Account> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val tags: TagLists = TagLists(emptyList(), emptyList()),
)

/**
 * Everything the transaction form shows.
 *
 * @property editing true when a stored transaction is being edited, false when one is being created.
 * @property loaded false until the pickers' lists (and, when editing, the transaction) have been read, so the
 *   screen draws no fields and no "not found" before the answer is known.
 * @property notFound true when the transaction asked for does not exist, was deleted, is itemised (this form
 *   edits one item; the items editor arrives in C4c), or the id was not an id at all. The route then leaves
 *   the screen.
 * @property type expense, income or transfer: decides the fields shown and what is saved.
 * @property typeOptions the types the user may switch between, in the order shown: all three when creating,
 *   expense and income when editing one of those, and none when editing a transfer (a transfer never becomes
 *   an expense or the other way round).
 * @property amount the amount in rupees as typed, with its error.
 * @property accountId the account the money moved on (a transfer's source), or null while none is chosen.
 * @property accountError why the account is refused, or null.
 * @property toAccountId a transfer's destination, or null while none is chosen.
 * @property toAccountError why the destination is refused, or null.
 * @property date the calendar day in the device zone; the time of day is kept by the ViewModel.
 * @property title the optional title as typed, with its error.
 * @property description the optional note as typed, with its error.
 * @property bankRef the optional bank reference as typed, with its error; not used by a transfer.
 * @property categoryId the item's category, or null for none; not used by a transfer.
 * @property tagIds the item's tags, an archived one already on it included.
 * @property categoryCleared true after switching expense and income took the category away because it is
 *   of the other kind; the screen says so politely.
 * @property choices what the pickers offer.
 * @property names the names of every account, category and tag the form can show, archived ones included.
 * @property saving true from a valid Save until the save finishes; the screen disables Save.
 * @property saved true once the transaction is saved; the route then leaves the screen.
 * @property saveFailed true after an unexpected failure to save: the screen shows a fixed "try again"
 *   message. It is cleared by editing a field or by submitting again; the typed text is kept.
 */
internal data class TransactionEditUiState(
    val editing: Boolean = false,
    val loaded: Boolean = false,
    val notFound: Boolean = false,
    val type: TransactionType = TransactionType.EXPENSE,
    val typeOptions: List<TransactionType> = emptyList(),
    val amount: FieldState = FieldState(),
    val accountId: AccountId? = null,
    val accountError: FieldError? = null,
    val toAccountId: AccountId? = null,
    val toAccountError: FieldError? = null,
    val date: LocalDate = LocalDate.ofEpochDay(0),
    val title: FieldState = FieldState(),
    val description: FieldState = FieldState(),
    val bankRef: FieldState = FieldState(),
    val categoryId: CategoryId? = null,
    val tagIds: Set<TagId> = emptySet(),
    val categoryCleared: Boolean = false,
    val choices: EditChoices = EditChoices(),
    val names: NameLookups = NameLookups(emptyMap(), emptyMap(), emptyMap()),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val saveFailed: Boolean = false,
)
