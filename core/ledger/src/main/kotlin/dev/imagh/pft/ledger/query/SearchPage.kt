package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.validation.LedgerLimits

/**
 * The result of a search (slice C D5): at most [LedgerLimits.MAX_SEARCH_RESULTS] entries, newest first.
 *
 * @property entries the matching entries, newest `occurred_at` first.
 * @property truncated true only when more entries matched than are in [entries].
 */
data class SearchPage(
    val entries: List<LedgerEntry>,
    val truncated: Boolean,
) {
    companion object {
        /**
         * How many transaction rows a search reads: an entry is at most two rows (a transfer's legs), so
         * this many rows always hold `MAX_SEARCH_RESULTS + 1` whole entries. That is what lets [capped]
         * tell "exactly the cap" from "more than the cap" without ever cutting a pair in half. It relies on
         * a pair's legs being adjacent in the read order, which holds because both legs share one
         * `occurred_at` (`TransferService` writes them from one draft) and the order then keeps the group
         * together.
         */
        const val FETCH_ROWS: Int = 2 * (LedgerLimits.MAX_SEARCH_RESULTS + 1)

        /**
         * [entries] (already folded, newest first, read with [FETCH_ROWS]) cut to [max], with
         * [SearchPage.truncated] true only if there were more than [max]. The cap counts entries, not rows,
         * so a pair is one.
         */
        fun capped(
            entries: List<LedgerEntry>,
            max: Int = LedgerLimits.MAX_SEARCH_RESULTS,
        ): SearchPage = SearchPage(entries.take(max), truncated = entries.size > max)
    }
}
