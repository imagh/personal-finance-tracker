package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.ids.TransactionId

/**
 * Where transactions (the payment facts) are stored: the persistence boundary, implemented with
 * Room in `app`. It holds no business logic. Its own queries arrive with their first consumers:
 * the in-use check in C1d and the month and search reads in C3a.
 */
interface TransactionRepository : EntityRepository<LedgerTransaction, TransactionId>
