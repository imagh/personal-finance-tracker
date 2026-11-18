package dev.imagh.pft.app.ui.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.CategoryGlyph
import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.TileGrid
import dev.imagh.pft.app.ui.common.selectionRing
import dev.imagh.pft.app.ui.theme.CATEGORY_SYMBOLS
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon

/** One cell's touch target; the badge inside is the row-size badge. */
private val CELL = 48.dp

/**
 * The symbol choices of the category form: a grid of badges in the category's [colour], first a "No
 * symbol" cell (the letter the category gets without one) and then every category symbol of the
 * registry ([CATEGORY_SYMBOLS]). This is the form's symbol side; [EmojiTab] is the emoji side beside it, and
 * [IconChoice] switches between them.
 *
 * **Accessibility.** The cells are one selectable group and each is a radio-style selectable. A cell
 * has no visible text, so it is announced by a spoken name ([spokenName], "Local cafe"; "No symbol" for
 * the first), and the chosen one as selected. The badge inside is decorative so the name is spoken once.
 * The chosen cell also has a ring. Each cell is a 48dp touch target.
 *
 * @param selected the icon the category has now. A symbol selects its cell, null selects "No symbol",
 *   and an emoji (or a symbol this grid does not offer) selects nothing.
 * @param colour the category's colour, which every badge is drawn in so the choice previews as it will look.
 * @param name the category's name as typed; the "No symbol" cell shows its first letter.
 * @param onSelect called with the tapped symbol, or null for "No symbol".
 */
@Composable
fun SymbolGrid(
    selected: CategoryIcon?,
    colour: CategoryColour,
    name: String,
    onSelect: (CategoryIcon?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val noSymbol = stringResource(R.string.category_no_symbol)
    val choices: List<PftSymbol?> = listOf(null) + CATEGORY_SYMBOLS
    TileGrid(choices, CELL, modifier.selectableGroup().padding(horizontal = PftTheme.spacing.gutter)) { symbol ->
        val icon = symbol?.let { CategoryIcon.Symbol(it.key) }
        val isSelected = selected == icon
        val source = if (symbol == null) GlyphSource.letterFor(name) else GlyphSource.Symbol(symbol)
        val spoken = symbol?.spokenName() ?: noSymbol
        val shape = PftTheme.shapes.glyph
        Box(
            Modifier
                .size(CELL)
                .selectionRing(isSelected, shape)
                .clip(shape)
                .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(icon) })
                .semantics { contentDescription = spoken },
            contentAlignment = Alignment.Center,
        ) {
            // Decorative: the cell's description is the name, so the badge must not say it again.
            CategoryGlyph(source, name = null, colourKey = colour.key)
        }
    }
}
