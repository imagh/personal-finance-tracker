package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.ids.CategoryId

/** The least width of one cell: four fit a 393dp phone, and a long name wraps to two lines. */
private val CELL_WIDTH = 88.dp

/** The least height of a cell, so a one-line cell is still a 48dp touch target. */
private val CELL_MIN_HEIGHT = 48.dp

/**
 * The sheet that asks "which category?": the item editor of the transaction form (C4b), the budget form
 * (D) and the category form's parent field open it. It is built on [PftBottomSheet] and is stateless: it
 * draws the [categories] it is given and reports what happened. There is no ViewModel here; the
 * consumer's ViewModel loads `CategoryService.pickable(kind)` (live, not archived, not under an archived
 * parent) and passes that list, so **only pickable categories are ever offered and their order is the
 * service's** (each top-level category, then its own sub-categories, by `sort_order`); it is never
 * re-sorted here.
 *
 * - **Layout.** A grid of [CategoryGlyph] badges, each with its name under it. Each top-level category
 *   and its sub-categories form one group that starts on a new row, so a sub-category sits next to its
 *   parent. With [topLevelOnly] the sub-categories are left out and the top-level categories share one
 *   grid (the category form's parent field).
 * - **Telling a sub-category apart.** A sub-category's cell has a second line, "Under <parent>", so two
 *   sub-categories with the same name (a "Misc" under Food and one under Fun) can be told apart on
 *   screen and by ear. A sub-category whose parent is not in [categories] is not shown, because it
 *   could not say whose it is; `pickable` never produces one.
 * - **TalkBack.** The cells are one selectable group (a set of choices) and each is a radio-style
 *   selectable. The badge is decorative (it has no description and is not an image), so a cell speaks
 *   its name once, from its text ("Groceries", then "Under Food & dining" for a sub-category), and the
 *   chosen cell is announced as selected. Each cell is at least 48dp square.
 * - **Selection.** The cell of [selected] has `selected` semantics and a ring. A [selected] id that is
 *   not among [categories] (the item's category was archived since) marks nothing.
 * - **Picking.** A tap calls [onPick] with the category's id, once, and then slides the sheet out and
 *   calls [onDismiss]. Taps while the sheet is sliding out are ignored, so a double tap cannot pick two.
 * - **Empty.** With no categories the sheet says so.
 * - **Dismissing without picking** (Back, the scrim, a swipe down) calls [onDismiss] only.
 *
 * The consumer shows it with `if (open) CategoryPicker(...)` and sets `open = false` in [onDismiss].
 * It logs nothing: a category's name is never logged.
 *
 * @param categories what to offer, in display order.
 * @param selected the id of the category that is chosen now, or null for none.
 * @param onPick called with the tapped category's id.
 * @param onDismiss called once when the sheet has closed, whether or not something was picked.
 * @param topLevelOnly true to offer only top-level categories.
 * @param title the sheet's heading.
 */
@Composable
fun CategoryPicker(
    categories: List<Category>,
    selected: CategoryId?,
    onPick: (CategoryId) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    topLevelOnly: Boolean = false,
    title: String = stringResource(R.string.category_picker_title),
) {
    PftBottomSheet(title = title, onDismiss = onDismiss, modifier = modifier) { close ->
        if (categories.isEmpty()) {
            EmptyState(stringResource(R.string.category_picker_empty))
        } else {
            var picked by remember { mutableStateOf(false) }
            val spacing = PftTheme.spacing
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .selectableGroup()
                    .padding(horizontal = spacing.gutter)
                    .padding(bottom = spacing.lg),
            ) {
                val parentNames = categories.associate { it.meta.id to it.name }
                categories.groups(topLevelOnly).forEach { group ->
                    TileGrid(group, CELL_WIDTH) { category ->
                        CategoryCell(
                            category,
                            parentName = category.parentId?.let(parentNames::get),
                            selected = category.meta.id == selected,
                        ) {
                            if (!picked) {
                                picked = true
                                onPick(category.meta.id)
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * The grids to draw, in order: one per top-level category holding it and its children, or, for
 * [topLevelOnly], one holding every top-level category. Children whose parent is absent are left out.
 */
private fun List<Category>.groups(topLevelOnly: Boolean): List<List<Category>> {
    val tops = filter { it.parentId == null }
    if (topLevelOnly) return listOf(tops)
    val childrenOf = filter { it.parentId != null }.groupBy { it.parentId }
    return tops.map { top -> listOf(top) + childrenOf[top.meta.id].orEmpty() }
}

@Composable
private fun CategoryCell(
    category: Category,
    parentName: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = PftTheme.spacing
    val shape = PftTheme.shapes.glyph
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = CELL_MIN_HEIGHT)
            .selectionRing(selected, shape)
            .clip(shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = spacing.xs, vertical = spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Decorative: the text below says the name, so the badge must not say it again.
        CategoryGlyph(category.iconKey.toGlyphSource(category.name), name = null, colourKey = category.colour.key)
        Text(
            category.name,
            modifier = Modifier.padding(top = spacing.xs),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (parentName != null) {
            Text(
                stringResource(R.string.category_under_parent, parentName),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                // Two lines, so a long parent name still shows what tells two same-named cells apart.
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
