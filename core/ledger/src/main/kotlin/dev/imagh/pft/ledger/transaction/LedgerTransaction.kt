package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * One transaction's **payment facts** (PRD L-1, L-9): what moved, how much, when, and on which
 * account. Its classification (category, tags, note) lives on its [TransactionLine]s, never here.
 *
 * **The sum invariant** (hardcoded, CLAUDE.md §3.8): a live transaction has at least one live line,
 * and its live lines' amounts add up exactly to [amount]. [TransactionService] and
 * [TransferService] are the only writers of both, and both write lines through [TransactionLines], which
 * checks the sum before anything is committed ([Breakdown], then a re-read).
 *
 * Built only by [TransactionService] and [TransferService], which validate and trim the values.
 *
 * @param type expense, income or a transfer leg.
 * @param direction [Direction.OUT] for an expense, [Direction.IN] for an income; a transfer leg stores
 *   its own.
 * @param amount positive paise, at most [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_AMOUNT].
 * @param occurredAt when the money moved.
 * @param accountId the account it moved on (L-9).
 * @param title the payee or merchant, trimmed, or null when there is none (slice C Q3): a list row then
 *   shows the category.
 * @param description a longer free-text note, or null.
 * @param state whether the user has confirmed it (L-6): manual entries are verified.
 * @param source where it came from.
 * @param bankRef the bank's reference (UTR, UPI or cheque number), or null (PRD X-1). Sensitive: never
 *   logged.
 * @param sourceMessageId the id of the SMS or email it was parsed from, or null (M1b).
 * @param accountFragment the account digits the message named, or null (M1b). Sensitive: never logged.
 * @param transferGroupId the id both legs of a transfer share, or null for an expense or income.
 */
data class LedgerTransaction(
    override val meta: EntityMeta<TransactionId>,
    val type: TransactionType,
    val direction: Direction,
    val amount: Paise,
    val occurredAt: Instant,
    val accountId: AccountId,
    val title: String?,
    val description: String?,
    val state: TxnState,
    val source: TxnSource,
    val bankRef: String?,
    val sourceMessageId: String?,
    val accountFragment: String?,
    val transferGroupId: String?,
) : LedgerEntity<TransactionId, LedgerTransaction> {
    override val auditType = "ledger_transaction"

    override fun auditFields(): Map<String, String?> =
        mapOf(
            "type" to type.name,
            "direction" to direction.name,
            "amount" to amount.value.toString(),
            "occurred_at" to occurredAt.toString(),
            "account_id" to accountId.value,
            "title" to title,
            "description" to description,
            "state" to state.name,
            "source" to source.name,
            "bank_ref" to bankRef,
            "source_message_id" to sourceMessageId,
            "account_fragment" to accountFragment,
            "transfer_group_id" to transferGroupId,
        )

    override fun withMeta(meta: EntityMeta<TransactionId>) = copy(meta = meta)
}
