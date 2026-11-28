package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.ui.common.FieldCheck
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.checkAmount
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/** The rupee sign [PaiseFormat] puts first; the form shows it as a prefix, not as text. */
private const val RUPEE_SIGN = "₹"

/** The values of a form that passed every check, ready to become a draft. */
internal class Checked(
    val amount: Paise,
    val account: AccountId,
    val toAccount: AccountId?,
    val title: String?,
    val description: String?,
    val bankRef: String?,
)

/** What validation found: the form to show afterwards (with every error) and the values if all were valid. */
internal class Verdict(
    val form: TransactionEditUiState,
    val values: Checked?,
)

/** This form after a save that failed: no longer saving, with the fixed failure message to show. */
internal fun TransactionEditUiState.failedSave(): TransactionEditUiState = copy(saving = false, saveFailed = true)

/** True while the form cannot take a Save: not loaded, saving, saved, or its transaction was not found. */
internal val TransactionEditUiState.blocked: Boolean get() = !loaded || saving || saved || notFound

/**
 * Every field of this form checked with core's rules (the shared amount rule, then positive; the optional-name
 * rule with each text's limit; an account chosen; a transfer's destination chosen and different), with every
 * error set at once. A transfer's bank reference is not used, so it is not checked.
 */
internal fun TransactionEditUiState.validated(): Verdict {
    val transfer = type == TransactionType.TRANSFER
    val paise = amount.checkAmount().positive()
    val titleCheck = title.check { Validation.requireOptionalName(it, LedgerLimits.MAX_TITLE_LENGTH) }
    val noteCheck = description.check { Validation.requireOptionalName(it, LedgerLimits.MAX_DESCRIPTION_LENGTH) }
    val refCheck =
        if (transfer) {
            FieldCheck(null, bankRef)
        } else {
            bankRef.check { Validation.requireOptionalName(it, LedgerLimits.MAX_BANK_REF_LENGTH) }
        }
    val toError = if (transfer) toAccountError() else null
    val shown =
        copy(
            amount = paise.state,
            title = titleCheck.state,
            description = noteCheck.state,
            bankRef = refCheck.state,
            accountError = if (accountId == null) FieldError.Required else null,
            toAccountError = toError,
        )
    // A null value is "no text" for an optional field, so its validity is read from its error.
    val textsValid = listOf(titleCheck.state, noteCheck.state, refCheck.state).all { it.error == null }
    val chosen = accountId
    val money = paise.value
    val ready = textsValid && toError == null
    val values =
        if (ready && money != null && chosen != null) {
            Checked(money, chosen, toAccountId, titleCheck.value, noteCheck.value, refCheck.value)
        } else {
            null
        }
    return Verdict(shown, values)
}

private fun TransactionEditUiState.toAccountError(): FieldError? =
    when (toAccountId) {
        null -> FieldError.Required
        accountId -> FieldError.SameAccount
        else -> null
    }

/** A positive amount: `checkAmount` allows zero, and a transaction of nothing is not one. */
private fun FieldCheck<Paise>.positive(): FieldCheck<Paise> =
    if (value != null && value.value == 0L) FieldCheck(null, state.copy(error = FieldError.InvalidAmount)) else this

/**
 * When the entry happened: [stored]'s instant if [date] is still its day in [zone] (kept to the millisecond, so a
 * clock-change overlap cannot shift it), else [date] at [timeOfDay].
 */
internal fun occurredAt(
    date: LocalDate,
    stored: StoredTransaction?,
    zone: ZoneId,
    timeOfDay: LocalTime,
): Instant {
    val was = stored?.transaction?.occurredAt
    return if (was != null && date == was.atZone(zone).toLocalDate()) {
        was
    } else {
        ZonedDateTime.of(date, timeOfDay, zone).toInstant()
    }
}

/**
 * What this form saves, from the [values] that passed [validated], the instant [at], and [stored] (whose item id and
 * note an edit keeps). A transfer carries the tags only; an expense or income has one item with the category.
 */
internal fun TransactionEditUiState.draftFor(
    values: Checked,
    at: Instant,
    stored: StoredTransaction?,
): EditDraft =
    if (type == TransactionType.TRANSFER) {
        EditDraft.Move(
            TransferDraft(
                values.account,
                checkNotNull(values.toAccount),
                values.amount,
                at,
                values.title,
                values.description,
                tagIds,
            ),
        )
    } else {
        val line = stored?.line
        EditDraft.Plain(
            TransactionDraft(
                type = type,
                amount = values.amount,
                occurredAt = at,
                accountId = values.account,
                title = values.title,
                description = values.description,
                bankRef = values.bankRef,
                lines = listOf(LineDraft(line?.meta?.id, values.amount, categoryId, tagIds, line?.note)),
            ),
        )
    }

/** This form filled from the [stored] transaction, its date read in [zone]. */
internal fun TransactionEditUiState.filledFrom(
    stored: StoredTransaction,
    zone: ZoneId,
): TransactionEditUiState {
    val txn = stored.transaction
    val transfer = txn.type == TransactionType.TRANSFER
    return copy(
        type = txn.type,
        typeOptions = if (transfer) emptyList() else listOf(TransactionType.EXPENSE, TransactionType.INCOME),
        amount = FieldState(PaiseFormat.format(txn.amount).removePrefix(RUPEE_SIGN)),
        accountId = txn.accountId,
        toAccountId = stored.toAccountId,
        date = txn.occurredAt.atZone(zone).toLocalDate(),
        title = FieldState(txn.title.orEmpty()),
        description = FieldState(txn.description.orEmpty()),
        bankRef = FieldState(txn.bankRef.orEmpty()),
        categoryId = stored.line.categoryId,
        tagIds = stored.line.tagIds,
    )
}
