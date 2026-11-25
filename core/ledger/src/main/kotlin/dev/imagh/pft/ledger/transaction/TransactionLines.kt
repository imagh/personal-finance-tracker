package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryRepository
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.NameBasedId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tag.TagRepository
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise

/**
 * The line (item) rules that [TransactionService] and, from C2, the transfer service share (slice C
 * D3): writing a transaction's lines and checking what they point at. A concrete class with no
 * interface. Only the services call it: its constructor is public so that `app` can wire it, and
 * every operation is `internal` to `:core:ledger`.
 *
 * - **Create** writes every draft as a new line, in draft order, with the deterministic id
 *   `NameBasedId.of("transaction-line", txnId, position)` (IT §3.3 rule 8), so every device that
 *   derives the same transaction derives the same lines.
 * - **Reconcile** (an edit) matches the drafts to the live lines by id (rule 4): a draft with the id
 *   of a live line of this transaction updates it (a no-op writes nothing); a draft without an id
 *   adds a line with a random time-ordered id; a live line no draft names is tombstoned. Positions
 *   are renumbered 1..n in draft order. A draft id that is not a live line of this transaction
 *   (another transaction's line, a tombstoned one, an unknown one) is `NotFound("transaction_line")`:
 *   a line can never be re-parented or brought back through a draft.
 * - **References are checked only where they change** (rule 3): a new line's category and tags, a
 *   changed category and a newly added tag must be live (not deleted, not archived). A line that
 *   keeps its category or tag keeps it even if it was archived since, so editing an old
 *   transaction's title never fails.
 * - **The kind always matches**: every line's category must be of the transaction's kind (an expense
 *   category on an expense), whether or not it changed, so a type change cannot leave a mismatch.
 * - **The sum is re-checked** before either returns (rule 5): the live lines are read back and, if
 *   they do not add up to the transaction's amount, the call fails closed with
 *   [IllegalStateException], which rolls back the caller's whole transaction. [Breakdown] already
 *   checked the drafts, so this guards against a bug here or in the store.
 *
 * Callers run these inside their own `runner.inTransaction`, together with the transaction's write.
 *
 * @param writer the audited write path.
 * @param lines the line store.
 * @param categories looked up to check a line's category.
 * @param tags looked up to check a line's tags.
 * @param ids source of the ids of lines added by a later edit.
 */
class TransactionLines(
    private val writer: AuditedWriter,
    private val lines: TransactionLineRepository,
    private val categories: CategoryRepository,
    private val tags: TagRepository,
    private val ids: Uuid7,
) {
    /**
     * Writes [drafts] as the lines of the new [transaction], positions 1..n in order (audited
     * CREATE each), after checking their references: everything on a new line is new.
     *
     * @return the lines, by position.
     * @throws LedgerError.NotFound a draft names a line id: a new transaction has no lines yet.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for a category or tag.
     * @throws LedgerError.KindMismatch if a category is not of the transaction's kind.
     * @throws IllegalStateException if the stored lines do not add up to the amount.
     */
    internal suspend fun create(
        transaction: LedgerTransaction,
        drafts: List<LineDraft>,
    ): List<TransactionLine> =
        write(transaction, drafts, existing = emptyList()) { position ->
            NameBasedId.of("transaction-line", transaction.meta.id.value, position.toString())
        }

    /**
     * Makes the live lines of [transaction] match [drafts] (see the class comment): updates, adds and
     * tombstones, audited, positions renumbered in draft order.
     *
     * @return the lines now live, by position.
     * @throws LedgerError.NotFound (`transaction_line`) for a draft id that is not a live line of this transaction.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for a new or
     *   changed category, or an added tag.
     * @throws LedgerError.KindMismatch if a category is not of the transaction's kind.
     * @throws IllegalStateException if the stored lines do not add up to the amount.
     */
    internal suspend fun reconcile(
        transaction: LedgerTransaction,
        drafts: List<LineDraft>,
    ): List<TransactionLine> = write(transaction, drafts, liveLinesOf(transaction.meta.id)) { ids.next() }

    /** The live lines of [id], by position, then by id, so the order never depends on the store. */
    internal suspend fun liveLinesOf(id: TransactionId): List<TransactionLine> =
        lines.liveLinesOf(id).sortedWith(compareBy({ it.position }, { it.meta.id.value }))

    /**
     * Refuses with [LedgerError.Deleted] (`category` or `tag`) if a category or tag of a **live** line of
     * transaction [id] was deleted: the check before a deleted transaction is restored. Archived ones
     * pass, and a tombstoned line is not looked at.
     */
    internal suspend fun requireReferencesNotDeleted(id: TransactionId) {
        liveLinesOf(id).forEach { line ->
            line.categoryId?.let { Validation.requireNotDeleted(categories.get(it), CATEGORY) }
            line.tagIds.forEach { Validation.requireNotDeleted(tags.get(it), TAG) }
        }
    }

    /**
     * The shared body of [create] and [reconcile]: one write per draft in order (an update of the
     * [existing] line it names, or a new line whose id [newId] makes from its position), a
     * tombstone for each of [existing] that no draft names, then the sum check.
     */
    private suspend fun write(
        transaction: LedgerTransaction,
        drafts: List<LineDraft>,
        existing: List<TransactionLine>,
        newId: (position: Int) -> String,
    ): List<TransactionLine> {
        val old = existing.associateBy { it.meta.id }
        val written =
            drafts.mapIndexed { index, draft ->
                val position = index + 1
                val before = draft.id?.let { old[it] ?: throw LedgerError.NotFound(WHAT) }
                requireCategory(transaction.type, draft.categoryId, changed = before?.categoryId != draft.categoryId)
                requireNewTags(draft.tagIds - (before?.tagIds ?: emptySet()))
                if (before == null) {
                    writer.create(lines, TransactionLineId(newId(position))) { meta ->
                        TransactionLine(
                            meta,
                            transaction.meta.id,
                            position,
                            draft.amount,
                            draft.categoryId,
                            draft.note,
                            draft.tagIds,
                        )
                    }
                } else {
                    writer.update(lines, before.meta.id) {
                        it.copy(
                            position = position,
                            amount = draft.amount,
                            categoryId = draft.categoryId,
                            note = draft.note,
                            tagIds = draft.tagIds,
                        )
                    }
                }
            }
        val kept = drafts.mapNotNull { it.id }.toSet()
        existing.filter { it.meta.id !in kept }.forEach { writer.delete(lines, it.meta.id) }
        requireBalanced(transaction)
        return written
    }

    /**
     * Reads the live lines of [transaction] back and fails closed if they do not add up to its
     * amount (IT §3.3 rule 5). The message is fixed text: it carries no amount.
     */
    private suspend fun requireBalanced(transaction: LedgerTransaction) {
        val sum = lines.liveLinesOf(transaction.meta.id).fold(Paise.ZERO) { total, line -> total + line.amount }
        check(sum == transaction.amount) { "The items of a transaction must add up to its amount" }
    }

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
        added.forEach { Validation.requireLive(tags.get(it), TAG) }
    }

    private companion object {
        /** The kinds of record, for errors. */
        const val WHAT = "transaction_line"
        const val CATEGORY = "category"
        const val TAG = "tag"

        /** The category kind a line of [type] needs; a transfer leg's line has no category. */
        fun kindOf(type: TransactionType): CategoryKind? =
            when (type) {
                TransactionType.EXPENSE -> CategoryKind.EXPENSE
                TransactionType.INCOME -> CategoryKind.INCOME
                TransactionType.TRANSFER -> null
            }
    }
}
