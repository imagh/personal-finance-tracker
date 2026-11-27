package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.validation.LedgerError
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

/**
 * The first and last month, inclusive, of a run of months (slice C C3c): what the month strip can scroll
 * over. [TransactionQueries.entryMonthSpan] returns the span of the live entries; [strip] widens it so it
 * always includes the current month.
 *
 * @throws LedgerError.InvalidRange if [first] is after [last].
 */
data class MonthSpan(
    val first: YearMonth,
    val last: YearMonth,
) {
    init {
        if (first > last) throw LedgerError.InvalidRange()
    }

    /** This span widened to include [month] (`min(first, month)..max(last, month)`). */
    fun including(month: YearMonth): MonthSpan = MonthSpan(minOf(first, month), maxOf(last, month))

    companion object {
        /** The span from instant [firstMs] to instant [lastMs] (epoch milliseconds, `first <= last`) read in [zone]. */
        fun ofInstants(
            firstMs: Long,
            lastMs: Long,
            zone: ZoneId,
        ): MonthSpan = MonthSpan(monthOf(firstMs, zone), monthOf(lastMs, zone))

        /**
         * The months the strip spans: [entries] (the live entries' span, or null when there are none)
         * widened to include [now]; with no entries, just [now].
         */
        fun strip(
            entries: MonthSpan?,
            now: YearMonth,
        ): MonthSpan = entries?.including(now) ?: MonthSpan(now, now)

        private fun monthOf(
            epochMs: Long,
            zone: ZoneId,
        ): YearMonth = YearMonth.from(Instant.ofEpochMilli(epochMs).atZone(zone))
    }
}
