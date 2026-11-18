package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagLists

/** The least width of one cell: three fit a 393dp phone, and a long name wraps to two lines. */
private val CELL_WIDTH = 110.dp

/** The least height of a cell, so a one-line cell is still a 48dp touch target. */
private val CELL_MIN_HEIGHT = 48.dp

/**
 * The sheet that asks "which tags?": the item editor of the transaction form (C4b) opens it. It is the
 * multi-select picker, built on [PftBottomSheet] and stateless towards its consumer: it draws the [tags]
 * it is given and reports what was chosen. There is no ViewModel; the consumer's ViewModel loads
 * `TagService.lists()` and passes it with the item's current tag ids. Nothing here is wired to a
 * consumer yet.
 *
 * - **What is offered.** Every active tag, in core's order (by name ignoring case), and **only** those: an
 *   archived tag is not offered for new use. **The one exception** is an archived tag that is already in
 *   [selected] (the item carries it): it is still drawn, selected and marked "Archived", so the user sees
 *   it, can deselect it on purpose, and an edit cannot drop it silently. It follows the active tags. A
 *   [selected] id that is no tag any more (deleted since) is not drawn, but is handed back untouched by
 *   [onDone] like every id the user did not change, so the picker never discards what it cannot show.
 * - **Layout.** A grid of cells, each a neutral tag badge (tags have no colour or icon) with the name under
 *   it, wrapping to two lines.
 * - **TalkBack.** The cells are one selectable group (a set of choices) and each is a checkbox-style
 *   selectable. The badge is decorative (no description, not an image), so a cell speaks its name once,
 *   from its text ("Travel", plus "Archived" where that applies), and a chosen cell is announced as
 *   checked. The title is a heading. Each cell and Done are at least 48dp.
 * - **Choosing.** A tap toggles a cell and the sheet stays open. **Done** calls [onDone] once with the ids
 *   chosen (an empty set is a valid choice), then slides the sheet out and calls [onDismiss]. Taps on Done
 *   while the sheet is sliding out are ignored, so a double tap cannot report twice.
 * - **Dismissing without Done** (Back, the scrim, a swipe down) calls [onDismiss] only: the choice is
 *   abandoned and [onDone] is not called.
 * - **Empty.** With nothing to offer the sheet says so and points to More, where tags are added; there is
 *   no Done then.
 *
 * The consumer shows it with `if (open) TagPicker(...)` and sets `open = false` in [onDismiss]. It logs
 * nothing: a tag's name is never logged.
 *
 * @param tags what exists: [TagLists.active] is offered, [TagLists.archived] only where chosen already.
 * @param selected the ids chosen now (the item's tags), archived ones included.
 * @param onDone called with the chosen ids when Done is tapped.
 * @param onDismiss called once when the sheet has closed, whether or not Done was tapped.
 */
@Composable
fun TagPicker(
    tags: TagLists,
    selected: Set<TagId>,
    onDone: (Set<TagId>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(R.string.tag_picker_title)
    PftBottomSheet(title = title, onDismiss = onDismiss, modifier = modifier) { close ->
        val shown = tags.active + tags.archived.filter { it.meta.id in selected }
        if (shown.isEmpty()) {
            EmptyState(stringResource(R.string.tag_picker_empty))
        } else {
            var chosen by remember { mutableStateOf(selected) }
            var finished by remember { mutableStateOf(false) }
            val spacing = PftTheme.spacing
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .selectableGroup()
                    .padding(horizontal = spacing.gutter),
            ) {
                TileGrid(shown, CELL_WIDTH) { tag ->
                    val id = tag.meta.id
                    TagCell(tag, selected = id in chosen) { chosen = if (id in chosen) chosen - id else chosen + id }
                }
            }
            Button(
                onClick = {
                    if (!finished) {
                        finished = true
                        onDone(chosen)
                        close()
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.gutter, vertical = spacing.md)
                        .heightIn(min = spacing.huge + spacing.sm),
            ) {
                Text(stringResource(R.string.tag_picker_done))
            }
        }
    }
}

@Composable
private fun TagCell(
    tag: Tag,
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
            .selectable(selected = selected, role = Role.Checkbox, onClick = onClick)
            .padding(horizontal = spacing.xs, vertical = spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Decorative: the text below says the name, so the badge must not say it again.
        CategoryGlyph(GlyphSource.Symbol(PftSymbol.TAG), name = null, colourKey = CategoryColour.SLATE.key)
        Text(
            tag.name,
            modifier = Modifier.padding(top = spacing.xs),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (tag.meta.isArchived) {
            Text(
                stringResource(R.string.tag_picker_archived),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
