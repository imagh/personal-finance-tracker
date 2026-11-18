package dev.imagh.pft.ledger.transaction

/**
 * A transaction and its live lines (items), sorted by position, then by id: what a detail screen or
 * an edit form shows.
 *
 * @property transaction the payment facts.
 * @property lines the live lines; they add up to the transaction's amount.
 */
data class TransactionWithLines(
    val transaction: LedgerTransaction,
    val lines: List<TransactionLine>,
)
