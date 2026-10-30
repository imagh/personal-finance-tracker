package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The three sizes of a category badge (plan §3.3): a small badge inside a chip, the leading badge
 * in a list row, and the large one in a detail hero. The size is a fixed dp, not a text size, so a
 * badge does not grow with the font scale.
 *
 * @property dp the badge's side.
 */
enum class GlyphSize(
    val dp: Dp,
) {
    /** Inside a chip. */
    Chip(28.dp),

    /** The leading badge of a list row. */
    Row(40.dp),

    /** The large badge of a detail screen. */
    Hero(56.dp),
}
