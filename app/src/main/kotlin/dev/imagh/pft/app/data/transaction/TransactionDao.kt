package dev.imagh.pft.app.data.transaction

import androidx.room.Dao
import androidx.room.Query
import dev.imagh.pft.app.data.common.BaseDao

/**
 * Reads for `ledger_transaction`; inserts and updates come from [BaseDao]. No logic: the service decides
 * what a transaction may be. The month, filter and search reads arrive with their first consumers (C3a).
 */
@Dao
interface TransactionDao : BaseDao<LedgerTransactionEntity> {
    @Query("SELECT * FROM ledger_transaction WHERE id = :id")
    override suspend fun byId(id: String): LedgerTransactionEntity?

    /** True if a transaction on [accountId] is not tombstoned (the in-use check; indexed by `account_id`). */
    @Query("SELECT EXISTS(SELECT 1 FROM ledger_transaction WHERE account_id = :accountId AND deleted_at IS NULL)")
    suspend fun isAccountUsed(accountId: String): Boolean
}
