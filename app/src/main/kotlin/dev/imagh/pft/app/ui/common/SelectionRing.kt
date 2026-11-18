package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** The thickness of the ring that marks the chosen tile. */
private val RING_WIDTH = 2.dp

/**
 * A primary-coloured ring around the chosen tile of a picker grid, so the choice is visible by a shape
 * and not by a colour change alone (the tile's own colour is a category colour and means nothing here).
 * Not selected, it adds nothing. TalkBack hears the choice from the tile's `selected` semantics, not
 * from this. Named consumers: `CategoryPicker`, the symbol grid and the colour swatches.
 *
 * @param selected whether to draw the ring.
 * @param shape the tile's outline.
 */
@Composable
internal fun Modifier.selectionRing(
    selected: Boolean,
    shape: Shape,
): Modifier = if (selected) border(RING_WIDTH, MaterialTheme.colorScheme.primary, shape) else this
