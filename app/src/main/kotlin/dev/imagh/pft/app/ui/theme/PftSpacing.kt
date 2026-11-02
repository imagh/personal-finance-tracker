package dev.imagh.pft.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The spacing scale: a 4dp grid plus a 2dp hairline step. Use these for padding and gaps instead
 * of raw `dp` values (raw design values live only in `ui/theme`). Read it through
 * `PftTheme.spacing`.
 *
 * Related sizes, owned by the components rather than this scale: touch targets are at least 48dp,
 * rows are 64dp (two lines) or 56dp (one line), and glyph badges are 40dp in rows, 56dp in the
 * detail hero and 28dp in chips.
 *
 * @property xxs 2dp, hairline gaps.
 * @property xs 4dp, tight gaps inside a control.
 * @property sm 8dp, gaps between related items.
 * @property md 12dp, gaps between a glyph and its text.
 * @property lg 16dp, the default content padding.
 * @property gutter 20dp, the screen's left and right edge.
 * @property xl 24dp, between groups.
 * @property xxl 32dp, between sections.
 * @property huge 48dp, large breaks such as above an empty-state glyph.
 */
@Immutable
data class PftSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val gutter: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val huge: Dp = 48.dp,
)
