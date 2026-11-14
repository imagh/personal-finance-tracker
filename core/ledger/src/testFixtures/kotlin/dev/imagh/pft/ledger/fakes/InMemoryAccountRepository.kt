package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.ids.AccountId

/**
 * The in-memory [AccountRepository]: the generic fake plus the account query. [allLive] returns the
 * rows **newest first**, deliberately not in insertion order: the contract promises no order (Room
 * returns none without an `ORDER BY`), so a service test that passes only because the fake happens
 * to return insertion order is exposed here.
 */
class InMemoryAccountRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<Account, AccountId>(ledger, "account"),
    AccountRepository {
    override suspend fun allLive(): List<Account> = rows.all().filterNot { it.meta.isDeleted }.reversed()
}
