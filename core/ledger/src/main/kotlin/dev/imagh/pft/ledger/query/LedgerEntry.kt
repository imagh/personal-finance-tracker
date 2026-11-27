package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType

/**
 * One row of a month list or a search result (slice C D5): a transaction with its lines, or a transfer's
 * two legs shown as one.
 */
sealed interface LedgerEntry {
    /**
     * A transaction on its own.
     *
     * @property transaction the payment facts.
     * @property lines **all** its live lines, by position then id, not only the ones a filter matched
     *   (a Rs 2,000 transaction found by its Groceries line still shows every item).
     */
    data class Single(
        val transaction: LedgerTransaction,
        val lines: List<TransactionLine>,
    ) : LedgerEntry

    /**
     * Both legs of one transfer, shown as one entry.
     *
     * @property outgoing the leg that left the source account.
     * @property incoming the leg that arrived on the target account.
     */
    data class Transfer(
        val outgoing: LedgerTransaction,
        val incoming: LedgerTransaction,
    ) : LedgerEntry
}

/** The pure step that turns the matching transactions into entries. */
object LedgerEntries {
    private val LINE_ORDER: Comparator<TransactionLine> = compareBy({ it.position }, { it.meta.id.value })

    /**
     * Turns [transactions] (already in display order) into entries, keeping that order.
     *
     * With [foldTransfers], the two legs that share a `transferGroupId`, one [Direction.OUT] and one
     * [Direction.IN], become one [LedgerEntry.Transfer] at the place of whichever leg comes first. Without
     * it (an account filter is on, so each leg is shown on its own account) every leg is a
     * [LedgerEntry.Single]. A leg whose group does not have exactly one outgoing and one incoming leg in
     * [transactions] (data damage, or a leg a filter left out) stays a Single: **nothing is dropped**.
     *
     * @param linesOf the live lines by transaction id; a transaction with none gets an empty list.
     */
    fun fold(
        transactions: List<LedgerTransaction>,
        linesOf: Map<TransactionId, List<TransactionLine>>,
        foldTransfers: Boolean,
    ): List<LedgerEntry> {
        val groups =
            if (foldTransfers) {
                transactions.filter { it.transferGroupId != null }.groupBy { it.transferGroupId }
            } else {
                emptyMap()
            }
        val pairs = groups.mapValues { (_, legs) -> pairOf(legs) }
        val done = mutableSetOf<String>()
        return transactions.mapNotNull { t ->
            val pair = pairs[t.transferGroupId]
            when {
                pair == null -> single(t, linesOf)
                done.add(checkNotNull(t.transferGroupId)) -> pair
                else -> null
            }
        }
    }

    private fun single(
        t: LedgerTransaction,
        linesOf: Map<TransactionId, List<TransactionLine>>,
    ) = LedgerEntry.Single(t, linesOf[t.meta.id].orEmpty().sortedWith(LINE_ORDER))

    private fun pairOf(legs: List<LedgerTransaction>): LedgerEntry.Transfer? {
        // A group is a pair only when every row is a transfer leg; a row of another type that carries a group
        // id is damage, so the whole group stays Singles (fail closed, nothing dropped).
        if (legs.any { it.type != TransactionType.TRANSFER }) return null
        // singleOrNull is null for none or several, so exactly one of each direction means exactly two legs.
        val out = legs.singleOrNull { it.direction == Direction.OUT }
        val into = legs.singleOrNull { it.direction == Direction.IN }
        return if (out != null && into != null) LedgerEntry.Transfer(out, into) else null
    }
}
