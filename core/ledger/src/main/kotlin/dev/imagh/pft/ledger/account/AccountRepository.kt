package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.ids.AccountId

/**
 * Where accounts are stored: [EntityRepository]'s read and write plus the one query accounts need.
 * Implemented with Room in `app`; holds no business logic (the service sorts and splits).
 */
interface AccountRepository : EntityRepository<Account, AccountId> {
    /**
     * Every account that is not tombstoned, archived ones included, in no particular order (the
     * service sorts, so implementations need not).
     */
    suspend fun allLive(): List<Account>
}
