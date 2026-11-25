package dev.imagh.pft.app.data.transaction

import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionLineRepository
import dev.imagh.pft.ledger.tx.TransactionRunner

/**
 * The Room implementation of core's [TransactionLineRepository]. A line is two kinds of rows: its
 * `transaction_line` row and one `transaction_line_tag` row per tag. This repository writes both **in one
 * Room transaction** ([runner]; it nests inside the service's own), so a bad tag id leaves neither the
 * line nor a partial tag set behind, even when a caller uses the repository outside a service transaction.
 * [update] replaces the tag set: it deletes the line's join rows and inserts the new ones, because join
 * rows are not entities (the audited `tag_ids` diff is the record of the change, data-model.md).
 *
 * It does not extend `RoomEntityRepository`: the shared base maps one row to one model, and a line needs
 * its tags to be complete. It keeps the base's no-row guard, [update] failing when no row matched, with
 * a message that names the kind only; the base's kind check is moot here, because the type is fixed.
 *
 * Holds no business logic: the sum rule, the references and the tag rules are core's.
 */
class RoomTransactionLineRepository(
    private val dao: TransactionLineDao,
    private val runner: TransactionRunner,
) : TransactionLineRepository {
    override val entityType = "transaction_line"

    override suspend fun get(id: TransactionLineId): TransactionLine? =
        dao.byId(id.value)?.let { withTags(listOf(it)).single() }

    override suspend fun getLive(id: TransactionLineId): TransactionLine? = get(id)?.takeUnless { it.meta.isDeleted }

    override suspend fun insert(entity: TransactionLine) {
        runner.inTransaction {
            dao.insert(TransactionLineMapper.toRow(entity))
            insertTags(entity)
        }
    }

    override suspend fun update(entity: TransactionLine) {
        runner.inTransaction {
            // The message names the kind only, never the id or any value.
            check(dao.update(TransactionLineMapper.toRow(entity)) == 1) { "No stored $entityType to update" }
            dao.deleteTagsOf(entity.meta.id.value)
            insertTags(entity)
        }
    }

    override suspend fun liveLinesOf(transactionId: TransactionId): List<TransactionLine> =
        withTags(dao.liveOf(transactionId.value))

    private suspend fun insertTags(line: TransactionLine) {
        if (line.tagIds.isEmpty()) return
        dao.insertTags(line.tagIds.map { TransactionLineTagEntity(line.meta.id.value, it.value) })
    }

    /** [rows] as lines, each with its tags from one query (never one per line). */
    private suspend fun withTags(rows: List<TransactionLineEntity>): List<TransactionLine> {
        if (rows.isEmpty()) return emptyList()
        val tagsByLine = dao.tagsOf(rows.map { it.meta.id }).groupBy({ it.lineId }, { TagId(it.tagId) })
        return rows.map { TransactionLineMapper.toModel(it, tagsByLine[it.meta.id].orEmpty().toSet()) }
    }
}
