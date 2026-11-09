package dev.imagh.pft.app.data.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Table `app_setting`: one runtime-varying value, keyed by its name. Changes are audited by the
 * core `SettingsService`; `updated_at` is epoch milliseconds UTC.
 */
@Entity(tableName = "app_setting")
data class AppSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,
    @ColumnInfo(name = "value")
    val value: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
