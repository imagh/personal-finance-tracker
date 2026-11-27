package dev.imagh.pft.ledger.query

/**
 * One month of the ledger (slice C D5): every entry that matches the filter in the month, newest first,
 * with the month's [totals] over the same entries. Never capped.
 *
 * @property entries the matching entries, newest `occurred_at` first; the two legs of a transfer stay together.
 * @property totals expense and income over [entries]; transfers are never in them.
 */
data class MonthPage(
    val entries: List<LedgerEntry>,
    val totals: Totals,
)
