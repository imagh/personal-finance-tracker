package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * A group for the items of a transaction (PRD L-2): "Food & dining", with sub-categories such as
 * "Groceries". Two levels only: a category is top-level ([parentId] null) or the child of a
 * top-level category of the same [kind].
 *
 * Built only by [CategoryService], which validates and trims the values; [kind] and [parentId] never
 * change after creation. Names are unique among live siblings, ignoring case.
 *
 * @param name what the user calls it, trimmed, 1 to [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_NAME_LENGTH]
 *   characters.
 * @param parentId the top-level category this one sits under, or null for a top-level category.
 * @param kind expense or income.
 * @param colour one of the 12 palette colours.
 * @param iconKey the icon, or null for none. The property is named after its column (`icon_key`) so
 *   that [auditFields] and the table line up one to one; it is stored as [CategoryIcon.toStored].
 * @param sortOrder its position among its siblings (same parent and kind), ascending. Values may
 *   have gaps (a deleted sibling leaves one), so only the order of the values matters.
 * @param systemKey which system category this is, or null for a user category.
 */
data class Category(
    override val meta: EntityMeta<CategoryId>,
    val parentId: CategoryId?,
    val kind: CategoryKind,
    val name: String,
    val colour: CategoryColour,
    val iconKey: CategoryIcon?,
    val sortOrder: Int,
    val systemKey: SystemCategory?,
) : LedgerEntity<CategoryId, Category> {
    override val auditType = "category"

    // The colour is audited by its enum name like every other enum (kind, system_key); the colour
    // column itself will hold the key ("rose"), which is the persistence layer's concern.
    override fun auditFields(): Map<String, String?> =
        mapOf(
            "parent_id" to parentId?.value,
            "kind" to kind.name,
            "name" to name,
            "colour" to colour.name,
            "icon_key" to iconKey?.toStored(),
            "sort_order" to sortOrder.toString(),
            "system_key" to systemKey?.name,
        )

    override fun withMeta(meta: EntityMeta<CategoryId>) = copy(meta = meta)
}
