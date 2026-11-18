package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionLineRepository

/**
 * The in-memory [TransactionLineRepository]: the generic fake plus the line query. [liveLinesOf]
 * returns the rows **newest first**, deliberately not in insertion or position order: the contract
 * promises no order, so a service test that passes only because the fake happens to return lines in
 * position order is exposed here.
 */
class InMemoryTransactionLineRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<TransactionLine, TransactionLineId>(ledger, "transaction_line"),
    TransactionLineRepository {
    override suspend fun liveLinesOf(transactionId: TransactionId): List<TransactionLine> =
        rows.all().filter { it.transactionId == transactionId && !it.meta.isDeleted }.reversed()
}
