package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * [items] as rows of equal-width tiles, as many to a row as fit [tileWidth] (at least one), the last
 * row padded so its tiles line up with the rows above. It is a plain `Column` of `Row`s, so it can sit
 * inside any scrolling container (a form's list item, a bottom sheet's scrolling column) and needs no
 * experimental layout API. Named consumers: `CategoryPicker`, the category form's symbol grid and
 * colour swatches.
 *
 * It adds no semantics: the caller makes each tile selectable and puts `selectableGroup()` on the
 * container, because only the caller knows what the tiles mean.
 *
 * @param items what to lay out, in reading order.
 * @param tileWidth the least width of one tile; the row's width is shared out equally among the tiles
 *   that fit, so a tile is never narrower than this.
 * @param tile draws one item, centred at the top of its share of the row.
 */
@Composable
internal fun <T> TileGrid(
    items: List<T>,
    tileWidth: Dp,
    modifier: Modifier = Modifier,
    tile: @Composable (T) -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = (maxWidth / tileWidth).toInt().coerceAtLeast(1)
        Column {
            items.chunked(columns).forEach { rowItems ->
                Row(Modifier.fillMaxWidth()) {
                    rowItems.forEach { item ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) { tile(item) }
                    }
                    repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
