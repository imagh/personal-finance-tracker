package dev.imagh.pft.app.data.tag

import androidx.room.Dao
import androidx.room.Query
import dev.imagh.pft.app.data.common.BaseDao

/** Reads for `tag`; inserts and updates come from [BaseDao]. No logic: the service sorts and checks names. */
@Dao
interface TagDao : BaseDao<TagEntity> {
    @Query("SELECT * FROM tag WHERE id = :id")
    override suspend fun byId(id: String): TagEntity?

    /** Every row that is not a tombstone, archived ones included, in no particular order. */
    @Query("SELECT * FROM tag WHERE deleted_at IS NULL")
    suspend fun allLive(): List<TagEntity>
}
