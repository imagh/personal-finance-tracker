package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.money.Paise

/**
 * What a list of entries adds up to (slice C D5): [expense] and [income] in paise. A transfer is neither
 * (PRD L-5), so it is in no total, not even when an account filter shows one leg alone.
 *
 * @property expense the sum of expenses.
 * @property income the sum of income.
 */
data class Totals(
    val expense: Paise,
    val income: Paise,
) {
    /** Income minus expense; negative when more went out than came in. */
    val net: Paise get() = income - expense

    companion object {
        /**
         * The totals of [entries]. Without a line filter ([TransactionFilter.hasLineFilter]) each transaction
         * counts its whole amount; with one, only its **matching lines** count ([TransactionFilter.matchesLine]
         * with [categoryScope]), so a Rs 2,000 transaction with Rs 1,200 of Groceries adds Rs 1,200 under a
         * Groceries filter.
         */
        fun of(
            entries: List<LedgerEntry>,
            filter: TransactionFilter,
            categoryScope: Set<CategoryId>,
        ): Totals {
            var expense = Paise.ZERO
            var income = Paise.ZERO
            for (entry in entries) {
                if (entry !is LedgerEntry.Single) continue
                val share =
                    if (filter.hasLineFilter) {
                        entry.lines
                            .filter { filter.matchesLine(it, categoryScope) }
                            .fold(Paise.ZERO) { sum, line -> sum + line.amount }
                    } else {
                        entry.transaction.amount
                    }
                when (entry.transaction.type) {
                    TransactionType.EXPENSE -> expense += share
                    TransactionType.INCOME -> income += share
                    TransactionType.TRANSFER -> Unit
                }
            }
            return Totals(expense, income)
        }
    }
}
