package dev.imagh.pft.app.data.transaction

import dev.imagh.pft.app.data.common.RoomEntityRepository
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionRepository

/**
 * The Room implementation of core's [TransactionRepository]: the shared base plus the in-use query and
 * the legs of a transfer group. The month and search reads are [RoomTransactionQueries].
 */
class RoomTransactionRepository(
    private val transactionDao: TransactionDao,
) : RoomEntityRepository<LedgerTransaction, LedgerTransactionEntity, TransactionId>(
        transactionDao,
        "ledger_transaction",
        LedgerTransactionMapper,
    ),
    TransactionRepository {
    override suspend fun isAccountInUse(id: AccountId): Boolean = transactionDao.isAccountUsed(id.value)

    override suspend fun legsOfGroup(groupId: String): List<LedgerTransaction> =
        transactionDao.byTransferGroup(groupId).map(LedgerTransactionMapper::toModel)
}
