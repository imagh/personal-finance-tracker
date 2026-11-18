package dev.imagh.pft.ledger.transaction

/**
 * What a transaction is (PRD L-1). Expense and income are classified by their lines' categories;
 * a transfer moves money between two of the user's own accounts and counts as neither (PRD LN-5).
 */
enum class TransactionType {
    /** Money spent: its lines carry expense categories, and its direction is [Direction.OUT]. */
    EXPENSE,

    /** Money received: its lines carry income categories, and its direction is [Direction.IN]. */
    INCOME,

    /**
     * One leg of a transfer between two of the user's accounts. Each leg stores its own direction, and
     * legs are created and edited only as a pair, by the transfer service (slice C2).
     */
    TRANSFER,
}
