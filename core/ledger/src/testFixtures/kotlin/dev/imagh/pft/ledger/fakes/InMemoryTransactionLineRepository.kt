package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionLineRepository

/**
 * The in-memory [TransactionLineRepository]: the generic fake plus the line queries. [liveLinesOf]
 * returns the rows **newest first**, deliberately not in insertion or position order: the contract
 * promises no order, so a service test that passes only because the fake happens to return lines in
 * position order is exposed here.
 *
 * The in-use queries join to [transactions] as the SQL does: a line counts only while it is not
 * tombstoned **and** its transaction is not deleted, so a deleted transaction whose lines are still
 * live (delete leaves lines as they were) never blocks.
 */
class InMemoryTransactionLineRepository(
    ledger: InMemoryLedgerStore,
    private val transactions: InMemoryTransactionRepository,
) : InMemoryEntityRepository<TransactionLine, TransactionLineId>(ledger, "transaction_line"),
    TransactionLineRepository {
    override suspend fun liveLinesOf(transactionId: TransactionId): List<TransactionLine> =
        rows.all().filter { it.transactionId == transactionId && !it.meta.isDeleted }.reversed()

    override suspend fun isCategoryInUse(id: CategoryId): Boolean =
        liveLinesOfLiveTransactions().any { it.categoryId == id }

    override suspend fun isTagInUse(id: TagId): Boolean = liveLinesOfLiveTransactions().any { id in it.tagIds }

    private fun liveLinesOfLiveTransactions(): List<TransactionLine> {
        val live =
            transactions
                .all()
                .filterNot { it.meta.isDeleted }
                .map { it.meta.id }
                .toSet()
        return rows.all().filter { !it.meta.isDeleted && it.transactionId in live }
    }
}
