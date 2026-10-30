package dev.imagh.pft.app.data.common

import androidx.room.ColumnInfo

/**
 * The columns every ledger entity table shares, `@Embedded` into each Room row (plan §3.4) and
 * mapped to core's `EntityMeta` by [MetaMapper]. Times are epoch milliseconds UTC.
 *
 * Column names are explicit snake_case so they match the keys of `auditFields()` (the Room-side
 * audit-columns check in the tests relies on it).
 *
 * Room does not count a `@PrimaryKey` inside an `@Embedded` class, so **every entity using this
 * declares `@Entity(tableName = "...", primaryKeys = ["id"])`** itself.
 */
data class MetaColumns(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "created_by_user_id")
    val createdByUserId: String,
    @ColumnInfo(name = "created_on_device_id")
    val createdOnDeviceId: String,
    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long?,
    @ColumnInfo(name = "archived_at")
    val archivedAt: Long?,
)
