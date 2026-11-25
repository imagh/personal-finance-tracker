package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionRepository

/** The in-memory [TransactionRepository]: the generic fake plus the in-use query. */
class InMemoryTransactionRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<LedgerTransaction, TransactionId>(ledger, "ledger_transaction"),
    TransactionRepository {
    override suspend fun isAccountInUse(id: AccountId): Boolean =
        rows.all().any { it.accountId == id && !it.meta.isDeleted }
}
