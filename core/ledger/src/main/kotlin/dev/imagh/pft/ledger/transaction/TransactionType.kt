package dev.imagh.pft.ledger.transaction

/**
 * What a transaction is (PRD L-1). Expense and income are classified by their lines' categories;
 * a transfer moves money between two of the user's own accounts and counts as neither (PRD L-5).
 */
enum class TransactionType {
    /** Money spent: its lines carry expense categories, and its direction is [Direction.OUT]. */
    EXPENSE,

    /** Money received: its lines carry income categories, and its direction is [Direction.IN]. */
    INCOME,

    /**
     * One leg of a transfer between two of the user's accounts. Each leg stores its own direction, and
     * legs are written only as a pair: created and edited by [TransferService], deleted and restored by
     * [TransactionService], which acts on both.
     */
    TRANSFER,
}
