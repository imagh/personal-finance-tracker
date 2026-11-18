package dev.imagh.pft.app.data.account

import androidx.room.Dao
import androidx.room.Query
import dev.imagh.pft.app.data.common.BaseDao

/** Reads for `account`; inserts and updates come from [BaseDao]. No logic: the service sorts and splits. */
@Dao
interface AccountDao : BaseDao<AccountEntity> {
    @Query("SELECT * FROM account WHERE id = :id")
    override suspend fun byId(id: String): AccountEntity?

    /** Every row that is not a tombstone, archived ones included, in no particular order. */
    @Query("SELECT * FROM account WHERE deleted_at IS NULL")
    suspend fun allLive(): List<AccountEntity>
}
