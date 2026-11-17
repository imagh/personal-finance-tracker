package dev.imagh.pft.app.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.CategoryGlyph
import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.TileGrid
import dev.imagh.pft.app.ui.common.selectionRing
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon

/** One quick-pick cell's touch target. */
private val CELL = 48.dp

/** The quick-pick emoji's size in dp (converted to sp so a large font scale cannot outgrow the cell). */
private val EMOJI_SIZE = 26.dp

/**
 * The quick-pick of the emoji tab: finance and household emoji, in this explicit display order (never an
 * index into anything, so reordering or adding one is a visible edit). Each is one code point from
 * Unicode 11 or older, so every phone the app runs on (minSdk 33) has a glyph for it, and each is in
 * core's emoji grammar; `EmojiTabTest` checks both, and that the validator accepts every one.
 */
internal val QUICK_PICK_EMOJI: List<String> =
    listOf(
        "💰",
        "💵",
        "💳",
        "🏦",
        "🧾",
        "💼",
        "🛒",
        "🥦",
        "🍔",
        "☕",
        "🎁",
        "🎬",
        "🚗",
        "⛽",
        "🚌",
        "🌴",
        "🏠",
        "💡",
        "📱",
        "💊",
        "🏥",
        "🎓",
        "🐶",
        "👶",
    )

/**
 * The emoji half of the category form's icon choice: a quick-pick of [QUICK_PICK_EMOJI], and a field to
 * type or paste any other emoji (the keyboard's emoji panel puts one there).
 *
 * - **Quick-pick.** One `selectableGroup` of `Role.RadioButton` cells, each a 48dp target showing its emoji
 *   as text. TalkBack reads a visible emoji by its Unicode name, so a cell has **no** description of its
 *   own (that would say it twice); the chosen cell is announced as selected and has a ring.
 * - **The field is a sink.** It always shows nothing: what is typed or pasted is handed to [onInput]
 *   unchanged and is not kept here, so the untrusted text lives nowhere in the UI. The caller judges it
 *   ([EmojiInputValidator], through the ViewModel) and answers with [selected] (an accepted emoji, which
 *   the chosen row then shows) or [rejected].
 * - **The error is fixed.** [rejected] shows one message under the field and marks the field as in error
 *   for TalkBack; it never repeats the text, and the icon stays what it was.
 * - **The chosen emoji** is shown in the category's colour with a "Chosen emoji" caption whenever
 *   [selected] is an emoji (a pasted one is not in the quick-pick, so it would otherwise have no
 *   feedback). The badge is announced by the emoji itself, which TalkBack reads by its Unicode name.
 *
 * @param selected the category's icon now; only an emoji selects a cell or shows the chosen row.
 * @param colour the category's colour, for the chosen row's badge.
 * @param rejected true after a paste was refused, until another icon is chosen.
 * @param onSelect called with the quick-pick emoji that was tapped.
 * @param onInput called with the text typed or pasted into the field, as it came.
 */
@Composable
fun EmojiTab(
    selected: CategoryIcon?,
    colour: CategoryColour,
    rejected: Boolean,
    onSelect: (CategoryIcon) -> Unit,
    onInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    val chosen = (selected as? CategoryIcon.Emoji)?.takeIf { it.isValid }
    Column(modifier.padding(horizontal = spacing.gutter), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        if (chosen != null) ChosenEmoji(chosen, colour)
        TileGrid(QUICK_PICK_EMOJI, CELL, Modifier.selectableGroup()) { emoji ->
            val isSelected = chosen?.text == emoji
            val shape = PftTheme.shapes.glyph
            Box(
                Modifier
                    .size(CELL)
                    .selectionRing(isSelected, shape)
                    .clip(shape)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelect(CategoryIcon.Emoji(emoji)) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val size = with(LocalDensity.current) { EMOJI_SIZE.toSp() }
                Text(emoji, fontSize = size, lineHeight = size, maxLines = 1, softWrap = false)
            }
        }
        OutlinedTextField(
            // A sink: nothing typed is kept here; see the class comment.
            value = "",
            onValueChange = onInput,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.category_emoji_field_label)) },
            isError = rejected,
            supportingText = if (rejected) ({ Text(stringResource(R.string.category_emoji_rejected)) }) else null,
            singleLine = true,
        )
    }
}

/** The "Chosen emoji" row: the caption and the emoji as the category's badge, spoken as one element. */
@Composable
private fun ChosenEmoji(
    emoji: CategoryIcon.Emoji,
    colour: CategoryColour,
) {
    Row(
        Modifier.semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(R.string.category_emoji_chosen),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        // Named by the emoji itself: a screen reader reads an emoji by its Unicode name.
        CategoryGlyph(GlyphSource.Emoji(emoji.text), name = emoji.text, colourKey = colour.key)
    }
}
