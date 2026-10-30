package dev.imagh.pft.app.data.settings

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/** Reads and upserts for `app_setting`. */
@Dao
interface AppSettingDao {
    /** The setting stored under [key], or null. */
    @Query("SELECT * FROM app_setting WHERE `key` = :key")
    suspend fun get(key: String): AppSettingEntity?

    /** Inserts [setting], or replaces the row with the same key. */
    @Upsert
    suspend fun put(setting: AppSettingEntity)
}
