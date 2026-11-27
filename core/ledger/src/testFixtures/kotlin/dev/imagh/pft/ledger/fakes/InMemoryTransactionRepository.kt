package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionRepository

/**
 * The in-memory [TransactionRepository]: the generic fake plus the in-use query. [legsOfGroup] returns the
 * rows **newest first**, deliberately not in insertion order: the contract promises no order, so a service
 * test that passes only because the fake lists the OUT leg first is exposed here.
 */
class InMemoryTransactionRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<LedgerTransaction, TransactionId>(ledger, "ledger_transaction"),
    TransactionRepository {
    override suspend fun isAccountInUse(id: AccountId): Boolean =
        rows.all().any { it.accountId == id && !it.meta.isDeleted }

    override suspend fun legsOfGroup(groupId: String): List<LedgerTransaction> =
        rows.all().filter { it.transferGroupId == groupId }.reversed()
}
