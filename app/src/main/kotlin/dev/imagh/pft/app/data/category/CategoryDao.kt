package dev.imagh.pft.app.data.category

import androidx.room.Dao
import androidx.room.Query
import dev.imagh.pft.app.data.common.BaseDao

/** Reads for `category`; inserts and updates come from [BaseDao]. No logic: the service sorts and checks. */
@Dao
interface CategoryDao : BaseDao<CategoryEntity> {
    @Query("SELECT * FROM category WHERE id = :id")
    override suspend fun byId(id: String): CategoryEntity?

    /** Every row that is not a tombstone, archived ones included, in no particular order. */
    @Query("SELECT * FROM category WHERE deleted_at IS NULL")
    suspend fun allLive(): List<CategoryEntity>

    /** The row whose `system_key` is [key] (a `SystemCategory` name) unless it is a tombstone, or null. */
    @Query("SELECT * FROM category WHERE system_key = :key AND deleted_at IS NULL")
    suspend fun bySystemKey(key: String): CategoryEntity?
}
