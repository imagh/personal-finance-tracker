package dev.imagh.pft.ledger.query

/**
 * A half-open span of time, `[startMs, endMs)`, as epoch milliseconds: the unit `occurred_at` is stored in.
 * Built by [TransactionFilter.window], which computes it from a month or a date range in the caller's
 * zone, so no zone arithmetic happens in the data layer. A bound that does not exist is
 * [Long.MIN_VALUE] or [Long.MAX_VALUE]. A window with `startMs >= endMs` holds nothing.
 *
 * @property startMs the first instant inside, inclusive.
 * @property endMs the first instant outside, exclusive.
 */
data class TimeWindow(
    val startMs: Long,
    val endMs: Long,
)
