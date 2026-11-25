package dev.imagh.pft.app.data.transaction

import dev.imagh.pft.app.data.common.MetaMapper
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.money.Paise

/**
 * Maps a core [TransactionLine] to its [TransactionLineEntity] row and back. A line's tags are not a
 * column: [toRow] leaves them out and [RoomTransactionLineRepository] writes them as
 * `transaction_line_tag` rows, and [toModel] takes the tags the repository read. It therefore does not
 * implement the one-argument `EntityMapper`, which could not return a complete line.
 *
 * Reading **fails closed** on a position or an amount that is not positive: the schema has no `CHECK`
 * (Room cannot declare one, slice C plan §4), so this is its only guard. The messages name the column
 * only, never the stored value (CLAUDE.md §3.7).
 */
object TransactionLineMapper {
    /** The row for [model], without its tags. */
    fun toRow(model: TransactionLine): TransactionLineEntity =
        TransactionLineEntity(
            meta = MetaMapper.toColumns(model.meta),
            transactionId = model.transactionId.value,
            position = model.position,
            amount = model.amount.value,
            categoryId = model.categoryId?.value,
            note = model.note,
        )

    /**
     * The line stored in [row], carrying [tagIds] (the ids read from `transaction_line_tag`).
     *
     * @throws IllegalStateException if the position or the amount is not positive.
     */
    fun toModel(
        row: TransactionLineEntity,
        tagIds: Set<TagId>,
    ): TransactionLine {
        check(row.position > 0) { "A transaction line row has a non-positive value in column position" }
        check(row.amount > 0) { "A transaction line row has a non-positive value in column amount" }
        return TransactionLine(
            meta = MetaMapper.toMeta(row.meta, ::TransactionLineId),
            transactionId = TransactionId(row.transactionId),
            position = row.position,
            amount = Paise(row.amount),
            categoryId = row.categoryId?.let(::CategoryId),
            note = row.note,
            tagIds = tagIds,
        )
    }
}
