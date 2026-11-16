package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.app.ui.common.EntityRow
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.common.RowGlyph
import dev.imagh.pft.app.ui.common.toGlyphSource
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * This category item as a list row (plan §3.3, "entity logic stays in the row mapper"): its name, a
 * badge from its icon and colour (the letter when it has no usable icon), the indent, the parent's
 * name as the second line when there is one, and its menu. `EntityListScreen` stays ignorant of
 * categories.
 *
 * The badge is decorative (a null glyph label): the title already speaks the category's name, so a
 * screen reader reads it once.
 *
 * @param actionLabel the menu entry's text for an action, already translated.
 * @param parentLine the second line for a sub-category listed away from its parent, from the parent's
 *   name ("Under Food & dining"), already translated.
 * @param choose what choosing [CategoryAction] on a category does (the ViewModel's `choose`).
 */
internal fun CategoryItem.toRow(
    actionLabel: (CategoryAction) -> String,
    parentLine: (String) -> String,
    choose: (CategoryId, CategoryAction) -> Unit,
): EntityRow {
    val id = category.meta.id
    return EntityRow(
        key = id.value,
        title = category.name,
        supporting = parentName?.let(parentLine),
        glyph = RowGlyph(category.iconKey.toGlyphSource(category.name), category.colour.key, null),
        indent = indent,
        actions = actions.map { action -> RowAction(actionLabel(action)) { choose(id, action) } },
    )
}
