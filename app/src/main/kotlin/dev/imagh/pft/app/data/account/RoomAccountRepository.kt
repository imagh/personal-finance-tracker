package dev.imagh.pft.app.data.account

import dev.imagh.pft.app.data.common.RoomEntityRepository
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.ids.AccountId

/** The Room implementation of core's [AccountRepository]: the shared base plus the account query. */
class RoomAccountRepository(
    private val dao: AccountDao,
) : RoomEntityRepository<Account, AccountEntity, AccountId>(dao, "account", AccountMapper),
    AccountRepository {
    override suspend fun allLive(): List<Account> = dao.allLive().map(AccountMapper::toModel)
}
