package dev.imagh.pft.app.data.tag

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import dev.imagh.pft.app.data.common.MetaColumns

/**
 * Table `tag` (schema v4, plan §4): one row per tag, the Room twin of core's `Tag`. A tag is a name
 * and the meta columns.
 *
 * There is deliberately no unique index on `name`: uniqueness is "among live tags, ignoring case", and
 * neither a `NOCASE` index (ASCII only) nor a partial `WHERE deleted_at IS NULL` index can be declared
 * on a Room entity, so either would exist only in migrated databases (plan §2). `TagService` enforces
 * the rule in the write transaction.
 *
 * The meta columns are embedded, and [Entity.primaryKeys] names `id` because Room does not count a
 * key inside an embedded class.
 */
@Entity(tableName = "tag", primaryKeys = ["id"])
data class TagEntity(
    @Embedded val meta: MetaColumns,
    @ColumnInfo(name = "name") val name: String,
)
