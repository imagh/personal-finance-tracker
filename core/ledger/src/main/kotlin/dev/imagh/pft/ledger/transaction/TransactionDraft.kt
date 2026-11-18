package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * What the user entered for an expense or an income, **untrusted until [TransactionService] has
 * validated it**. The same draft serves create and edit; the ViewModel builds it, with one
 * [LineDraft] for the whole amount on a plain transaction.
 *
 * It carries only what the user can type. State and source are set by the service (a manual entry
 * is [TxnSource.MANUAL] and [TxnState.VERIFIED]), the direction follows the type, and the
 * message-derived fields belong to the slices that capture messages.
 *
 * @param type expense or income; a transfer is refused ([LedgerError.IsTransfer]).
 * @param amount the whole amount; the lines must add up to it.
 * @param occurredAt when the money moved.
 * @param accountId the account it moved on.
 * @param title the payee or merchant; optional, trimmed, blank means none (slice C Q3).
 * @param description a longer note; optional, trimmed, blank means none.
 * @param bankRef the bank's reference (PRD X-1); optional, trimmed, blank means none.
 * @param lines the lines (items) that split [amount].
 */
data class TransactionDraft(
    val type: TransactionType,
    val amount: Paise,
    val occurredAt: Instant,
    val accountId: AccountId,
    val title: String? = null,
    val description: String? = null,
    val bankRef: String? = null,
    val lines: List<LineDraft>,
)

/**
 * This draft with every typed value checked, the texts trimmed (blank becomes null) and the lines
 * checked as a split of the amount ([Breakdown.of]): what [TransactionService] writes. Pure: it
 * looks nothing up, so the account, categories and tags are checked by the service.
 *
 * The amount is checked before the lines, so a bad amount is reported as such and not as a
 * mismatch.
 *
 * @throws LedgerError.IsTransfer for [TransactionType.TRANSFER].
 * @throws LedgerError.AmountNotPositive / [LedgerError.AmountTooLarge] for the amount.
 * @throws LedgerError.NameTooLong for a title, description or bank reference over its limit.
 * @throws LedgerError the errors of [Breakdown.of] for the lines.
 */
internal fun TransactionDraft.validated(): TransactionDraft {
    if (type == TransactionType.TRANSFER) throw LedgerError.IsTransfer()
    Validation.requireAmount(amount)
    return copy(
        title = Validation.requireOptionalName(title, LedgerLimits.MAX_TITLE_LENGTH),
        description = Validation.requireOptionalName(description, LedgerLimits.MAX_DESCRIPTION_LENGTH),
        bankRef = Validation.requireOptionalName(bankRef, LedgerLimits.MAX_BANK_REF_LENGTH),
        lines = Breakdown.of(amount, lines).lines,
    )
}
