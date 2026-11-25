package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId

/**
 * Where transactions (the payment facts) are stored: the persistence boundary, implemented with
 * Room in `app`. It holds no business logic. Its other queries arrive with their first consumers
 * (the month and search reads in C3a).
 */
interface TransactionRepository : EntityRepository<LedgerTransaction, TransactionId> {
    /**
     * True if a **live** (not deleted) transaction is on account [id], an archived account included.
     * A deleted transaction does not count, so it never blocks deleting the account. Not an
     * existence check on the account itself.
     */
    suspend fun isAccountInUse(id: AccountId): Boolean
}
