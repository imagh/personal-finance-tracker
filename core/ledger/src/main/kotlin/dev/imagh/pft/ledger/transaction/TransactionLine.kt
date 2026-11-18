package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.money.Paise

/**
 * One line (item) of a transaction: a share of its amount with its own **classification** (PRD L-1,
 * L-11). A plain transaction has one line for the whole amount; an itemised one has several.
 *
 * A ledger entity in its own right (own meta, attribution, tombstone and audit entries; IT §3.2),
 * but written only by [TransactionService], in the same transaction as its parent, so no write can
 * skip the sum check. A line's `archived_at` is never set; the column exists for uniformity.
 *
 * @param transactionId the transaction it belongs to.
 * @param position its place in the transaction's list, from 1 (display order only).
 * @param amount positive paise; the live lines of a transaction add up to its amount.
 * @param categoryId its category, or null for uncategorised (and for a transfer leg).
 * @param note a short note, trimmed, or null.
 * @param tagIds its tags, in no order. Audited as one `tag_ids` text value (see [auditFields]).
 */
data class TransactionLine(
    override val meta: EntityMeta<TransactionLineId>,
    val transactionId: TransactionId,
    val position: Int,
    val amount: Paise,
    val categoryId: CategoryId?,
    val note: String?,
    val tagIds: Set<TagId>,
) : LedgerEntity<TransactionLineId, TransactionLine> {
    override val auditType = "transaction_line"

    /**
     * Every field under its column name. `tag_ids` is the tag ids sorted by their text and joined with
     * `,` (IT §4), so the same set always audits the same way and a change shows the old and new lists;
     * a line with no tags audits it as null, like any unset field.
     */
    override fun auditFields(): Map<String, String?> =
        mapOf(
            "transaction_id" to transactionId.value,
            "position" to position.toString(),
            "amount" to amount.value.toString(),
            "category_id" to categoryId?.value,
            "note" to note,
            "tag_ids" to
                tagIds
                    .map { it.value }
                    .sorted()
                    .joinToString(",")
                    .ifEmpty { null },
        )

    override fun withMeta(meta: EntityMeta<TransactionLineId>) = copy(meta = meta)
}
