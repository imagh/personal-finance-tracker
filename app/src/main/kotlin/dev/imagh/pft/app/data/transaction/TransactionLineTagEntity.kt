package dev.imagh.pft.app.data.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.imagh.pft.app.data.tag.TagEntity

/**
 * Table `transaction_line_tag` (schema v5, slice C plan §4): which tags a line carries. A **join table,
 * not a ledger entity**: it has no meta columns, is not in `AuditedTables.all` and is listed there as a
 * non-entity table. Its content is audited as the owning line's `tag_ids` (the sorted ids joined with
 * `,`), so a tag change shows as a line update with the old and new lists; this is why rows are replaced
 * rather than tombstoned (INT-2, INT-3).
 *
 * The primary key `(line_id, tag_id)` also indexes `line_id`; the extra index on `tag_id` serves the
 * in-use check ("is any line using this tag") of slice C1d. Both are foreign keys with no `onDelete`
 * action.
 */
@Entity(
    tableName = "transaction_line_tag",
    primaryKeys = ["line_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = TransactionLineEntity::class,
            parentColumns = ["id"],
            childColumns = ["line_id"],
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
        ),
    ],
    indices = [Index(value = ["tag_id"])],
)
data class TransactionLineTagEntity(
    @ColumnInfo(name = "line_id") val lineId: String,
    @ColumnInfo(name = "tag_id") val tagId: String,
)
