package dev.imagh.pft.app.data.transaction

import dev.imagh.pft.app.data.common.RoomEntityRepository
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionRepository

/**
 * The Room implementation of core's [TransactionRepository]: the shared base, no queries of its own yet
 * (the in-use check arrives in C1d and the month and search reads in C3a).
 */
class RoomTransactionRepository(
    dao: TransactionDao,
) : RoomEntityRepository<LedgerTransaction, LedgerTransactionEntity, TransactionId>(
        dao,
        "ledger_transaction",
        LedgerTransactionMapper,
    ),
    TransactionRepository
