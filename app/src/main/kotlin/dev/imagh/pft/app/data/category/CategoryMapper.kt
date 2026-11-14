package dev.imagh.pft.app.data.category

import dev.imagh.pft.app.data.common.EntityMapper
import dev.imagh.pft.app.data.common.MetaMapper
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * Maps a core [Category] to its [CategoryEntity] row and back.
 *
 * Stored colours and icons are untrusted on read (a corrupt row, or data synced from a newer
 * version in M2), so they **fall back** instead of failing: an unknown colour reads as slate and an
 * icon outside the grammar reads as no icon (a letter badge). `kind` and `system_key` drive rules
 * rather than looks, so an unknown value **fails closed**, as `account.type` does; the messages name
 * the column only, never the stored value (CLAUDE.md §3.7).
 */
object CategoryMapper : EntityMapper<Category, CategoryEntity> {
    override fun toRow(model: Category): CategoryEntity =
        CategoryEntity(
            meta = MetaMapper.toColumns(model.meta),
            parentId = model.parentId?.value,
            kind = model.kind.name,
            name = model.name,
            colour = model.colour.key,
            iconKey = model.iconKey?.toStored(),
            sortOrder = model.sortOrder,
            systemKey = model.systemKey?.name,
        )

    /**
     * The category stored in [row].
     *
     * @throws IllegalStateException if the kind or the system key is not one this build knows.
     */
    override fun toModel(row: CategoryEntity): Category {
        val kind =
            checkNotNull(CategoryKind.entries.firstOrNull { it.name == row.kind }) {
                "A category row has an unknown value in column kind"
            }
        val systemKey =
            row.systemKey?.let { stored ->
                checkNotNull(SystemCategory.entries.firstOrNull { it.name == stored }) {
                    "A category row has an unknown value in column system_key"
                }
            }
        return Category(
            meta = MetaMapper.toMeta(row.meta, ::CategoryId),
            parentId = row.parentId?.let(::CategoryId),
            kind = kind,
            name = row.name,
            colour = CategoryColour.fromStored(row.colour),
            iconKey = CategoryIcon.parse(row.iconKey),
            sortOrder = row.sortOrder,
            systemKey = systemKey,
        )
    }
}
