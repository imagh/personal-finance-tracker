package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryRepository
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.NameBasedId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.tag.TagRepository
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.Validation

/**
 * The line (item) rules that [TransactionService] and, from C2, the transfer service share (slice C
 * D3): writing a transaction's lines and checking what they point at. A concrete class with no
 * interface. Only the services call it: its constructor is public so that `app` can wire it, and
 * every operation is `internal` to `:core:ledger`.
 *
 * **In C1a** it holds what a one-line transaction needs:
 * - line 1 is written with the deterministic id `NameBasedId.of("transaction-line", txnId, "1")`
 *   (IT §3.3 rule 8), so every device that derives the same transaction derives the same line;
 * - **references are checked only where they change** (IT §3.3 rule 3): a new or changed category
 *   and a newly added tag must be live (not deleted, not archived). A line that keeps its category
 *   or tag keeps it even if it was archived since, so editing an old transaction's title never fails;
 * - **the kind always matches**: a line's category must be of the transaction's kind (an expense
 *   category on an expense), whether or not it changed, so a type change cannot leave a mismatch.
 *
 * **C1b adds** the reconciliation of several lines by id (IT §3.3 rule 4), `requireBalanced` (the
 * re-check of the sum before commit, rule 5) and the ids of lines added later.
 *
 * Callers run these inside their own `runner.inTransaction`, together with the transaction's write.
 *
 * @param writer the audited write path.
 * @param lines the line store.
 * @param categories looked up to check a line's category.
 * @param tags looked up to check a line's tags.
 */
class TransactionLines(
    private val writer: AuditedWriter,
    private val lines: TransactionLineRepository,
    private val categories: CategoryRepository,
    private val tags: TagRepository,
) {
    /**
     * Writes [draft] as line 1 of the new [transaction] (audited CREATE), after checking its
     * references: everything on a new line is new, so its category and tags must all be live.
     *
     * @throws LedgerError.NotFound if [draft] names a line id: a new transaction has no lines yet.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for its category or a tag.
     * @throws LedgerError.KindMismatch if its category is not of the transaction's kind.
     */
    internal suspend fun createFirst(
        transaction: LedgerTransaction,
        draft: LineDraft,
    ): TransactionLine {
        if (draft.id != null) throw LedgerError.NotFound(WHAT)
        requireCategory(transaction.type, draft.categoryId, changed = true)
        requireNewTags(draft.tagIds)
        val txnId = transaction.meta.id
        val id = TransactionLineId(NameBasedId.of("transaction-line", txnId.value, FIRST_POSITION.toString()))
        return writer.create(lines, id) { meta ->
            TransactionLine(meta, txnId, FIRST_POSITION, draft.amount, draft.categoryId, draft.note, draft.tagIds)
        }
    }

    /**
     * Replaces the amount, category, tags and note of the one live line of [transaction] with
     * [draft] (audited UPDATE of the changed fields; no change writes nothing), checking references
     * only where they change and the kind always.
     *
     * @throws LedgerError.NotFound unless [draft] names the transaction's one live line.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for a changed
     *   category or an added tag.
     * @throws LedgerError.KindMismatch if the category is not of the transaction's kind.
     */
    internal suspend fun updateOnly(
        transaction: LedgerTransaction,
        draft: LineDraft,
    ): TransactionLine {
        val old =
            lines.liveLinesOf(transaction.meta.id).singleOrNull()?.takeIf { it.meta.id == draft.id }
                ?: throw LedgerError.NotFound(WHAT)
        requireCategory(transaction.type, draft.categoryId, changed = draft.categoryId != old.categoryId)
        requireNewTags(draft.tagIds - old.tagIds)
        return writer.update(lines, old.meta.id) {
            it.copy(amount = draft.amount, categoryId = draft.categoryId, note = draft.note, tagIds = draft.tagIds)
        }
    }

    /** The live lines of [id], by position, then by id, so the order never depends on the store. */
    internal suspend fun liveLinesOf(id: TransactionId): List<TransactionLine> =
        lines.liveLinesOf(id).sortedWith(compareBy({ it.position }, { it.meta.id.value }))

    /**
     * Checks [categoryId] for a line of a [type] transaction: live if [changed], and always of the
     * matching kind. A category the line keeps is read with tombstones, so a deleted one still has
     * its kind; null (uncategorised) needs no check.
     */
    private suspend fun requireCategory(
        type: TransactionType,
        categoryId: CategoryId?,
        changed: Boolean,
    ) {
        if (categoryId == null) return
        val found = categories.get(categoryId)
        val category =
            if (changed) Validation.requireLive(found, CATEGORY) else found ?: throw LedgerError.NotFound(CATEGORY)
        if (category.kind != kindOf(type)) throw LedgerError.KindMismatch()
    }

    /** Refuses any of [added] that is not live: tags a line keeps are not checked again. */
    private suspend fun requireNewTags(added: Set<TagId>) {
        added.forEach { Validation.requireLive(tags.get(it), "tag") }
    }

    private companion object {
        /** The kinds of record, for errors. */
        const val WHAT = "transaction_line"
        const val CATEGORY = "category"

        /** The position of a transaction's first line. */
        const val FIRST_POSITION = 1

        /** The category kind a line of [type] needs; a transfer leg's line has no category. */
        fun kindOf(type: TransactionType): CategoryKind? =
            when (type) {
                TransactionType.EXPENSE -> CategoryKind.EXPENSE
                TransactionType.INCOME -> CategoryKind.INCOME
                TransactionType.TRANSFER -> null
            }
    }
}
