package dev.imagh.pft.app.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.common.TileGrid
import dev.imagh.pft.app.ui.common.selectionRing
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.CategoryColour

/** One swatch's touch target; the drawn disc is smaller. */
private val SWATCH_TARGET = 48.dp

/** The drawn disc inside the target. */
private val SWATCH_DISC = 36.dp

/** The check mark inside the chosen disc. */
private val SWATCH_CHECK = 20.dp

/** The thin outline that keeps a pale disc visible on a light surface. */
private val SWATCH_OUTLINE = 1.dp

/**
 * The 12 category colours as round swatches, in the palette's order, for the category form. Each swatch
 * is a disc in the colour's own badge fill (what a category badge of that colour looks like in the
 * current light or dark theme) with an outline in the badge's content colour.
 *
 * **Accessibility.** The swatches are one selectable group and each is a radio-style selectable; a
 * colour carries no other meaning, so each is announced by its name ("Amber", [spokenName]) and the
 * chosen one as selected. The chosen swatch also shows a check mark and a ring, so the choice does not
 * depend on telling colours apart. Each swatch is a 48dp touch target.
 *
 * @param selected the chosen colour.
 * @param onSelect called with the tapped colour.
 */
@Composable
fun ColourSwatches(
    selected: CategoryColour,
    onSelect: (CategoryColour) -> Unit,
    modifier: Modifier = Modifier,
) {
    TileGrid(
        CategoryColour.entries,
        SWATCH_TARGET,
        modifier.selectableGroup().padding(horizontal = PftTheme.spacing.gutter),
    ) { colour ->
        val colours = PftTheme.palette[colour.key]
        val isSelected = colour == selected
        val name = colour.spokenName()
        Box(
            Modifier
                .size(SWATCH_TARGET)
                .selectionRing(isSelected, CircleShape)
                .clip(CircleShape)
                .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(colour) })
                .semantics { contentDescription = name },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(SWATCH_DISC)
                    .clip(CircleShape)
                    .background(colours.container)
                    .border(SWATCH_OUTLINE, colours.content, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        painterResource(PftSymbol.CHECK.res),
                        contentDescription = null,
                        modifier = Modifier.size(SWATCH_CHECK),
                        tint = colours.content,
                    )
                }
            }
        }
    }
}
