package dev.imagh.pft.app.data.category

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.imagh.pft.app.data.common.MetaColumns

/**
 * Table `category` (schema v3, plan §4): one row per category, the Room twin of core's `Category`.
 *
 * - `parent_id` is null for a top-level category, else the id of its (top-level) parent. It is a
 *   foreign key to `category(id)` with no `onDelete` action (rows are tombstoned, never removed), and
 *   Room needs the [Index] on it. Whether SQLite enforces the key is pinned by `RoomCategoryForeignKeyTest`;
 *   either way the service validates the parent in the write transaction.
 * - `kind` and `system_key` are enum names; `colour` is the palette **key** (`coral`), while the audit
 *   trail names the colour by its enum name (`CORAL`).
 * - `system_key` is unique (SQLite allows many NULLs), so there is at most one row per system category.
 * - There is deliberately no `CHECK` constraint and no partial index (name uniqueness among live
 *   siblings is a core rule): Room cannot declare either on an entity, so they would exist only in
 *   migrated databases (plan §2).
 *
 * The meta columns are embedded, and [Entity.primaryKeys] names `id` because Room does not count a
 * key inside an embedded class.
 */
@Entity(
    tableName = "category",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
        ),
    ],
    indices = [
        Index(value = ["parent_id"]),
        Index(value = ["system_key"], unique = true),
    ],
)
data class CategoryEntity(
    @Embedded val meta: MetaColumns,
    @ColumnInfo(name = "parent_id") val parentId: String?,
    /** The `CategoryKind` name. */
    @ColumnInfo(name = "kind") val kind: String,
    @ColumnInfo(name = "name") val name: String,
    /** The `CategoryColour` key (lower-case), not its enum name. */
    @ColumnInfo(name = "colour") val colour: String,
    /** The stored icon (`sym:<key>` or `emoji:<text>`), or null for none. */
    @ColumnInfo(name = "icon_key") val iconKey: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    /** The `SystemCategory` name, or null for a user category. */
    @ColumnInfo(name = "system_key") val systemKey: String?,
)
