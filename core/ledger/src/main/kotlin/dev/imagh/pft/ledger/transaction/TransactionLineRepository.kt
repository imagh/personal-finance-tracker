package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId

/**
 * Where transaction lines (items) are stored: [EntityRepository]'s read and write plus the query
 * the lines need. Implemented with Room in `app`; holds no business logic. Only
 * [TransactionService] writes lines (through [TransactionLines]), so no write can skip the sum check.
 */
interface TransactionLineRepository : EntityRepository<TransactionLine, TransactionLineId> {
    /**
     * The lines of [transactionId] that are not tombstoned, in no particular order (the service
     * sorts). Whether the transaction itself is deleted does not matter: deleting a transaction
     * leaves its lines as they are.
     */
    suspend fun liveLinesOf(transactionId: TransactionId): List<TransactionLine>
}
