package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits

/**
 * The pure rules of `recent` (slice C C3c): how many rows to fetch for [limit] entries, and the cut after
 * folding. Kept out of the Room class so every rule is tested without a database.
 */
object RecentEntries {
    /**
     * The transaction rows to fetch so that the first [limit] entries after folding are whole: an entry is
     * at most two rows (a transfer's legs, adjacent in every order), so `2 * limit` rows hold `limit` whole
     * entries even when the last pair is cut at the edge.
     *
     * @throws dev.imagh.pft.ledger.validation.LedgerError.InvalidRange if [limit] is below 1 or above
     *   `LedgerLimits.MAX_SEARCH_RESULTS`.
     */
    fun fetchRows(limit: Int): Int {
        if (limit !in 1..LedgerLimits.MAX_SEARCH_RESULTS) throw LedgerError.InvalidRange()
        return 2 * limit
    }

    /**
     * The first [limit] of [entries] (folded, newest first). This is the core rule for the cut: the C4a fake
     * of `TransactionQueries.recent` must call it too, not re-implement it.
     */
    fun cut(
        entries: List<LedgerEntry>,
        limit: Int,
    ): List<LedgerEntry> = entries.take(limit)
}
