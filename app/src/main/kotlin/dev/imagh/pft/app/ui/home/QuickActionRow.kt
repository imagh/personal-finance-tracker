package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.theme.BrandColors
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

private val TileSize = 52.dp
private val IconSize = 24.dp
private val LabelGap = 8.dp
private val LineGap = 12.dp

/**
 * One quick action: its words, its icon, what it does and whether it is the emphasised one.
 *
 * @property label the action's name; it is also what a screen reader says, so it is never empty.
 * @property icon the symbol on the tile; decorative, the label names the action.
 * @property onClick what tapping it does.
 * @property primary true for the one action filled with `brand` (add an expense); the rest are soft
 *   tiles.
 */
@Immutable
data class QuickAction(
    val label: String,
    val icon: PftSymbol,
    val onClick: () -> Unit,
    val primary: Boolean = false,
)

/** The tile fill and the icon colour of a quick action. */
internal data class QuickActionColors(
    val fill: Color,
    val icon: Color,
)

/**
 * The colours of a quick action tile: `brand` with `onBrand` for the [primary] one, `brandSoft`
 * with `onBrandSoft` for the others. Pure, so the pairing is tested by value; `ColorContrastTest`
 * pins the contrast of both pairs.
 */
internal fun quickActionColors(
    brand: BrandColors,
    primary: Boolean,
): QuickActionColors =
    if (primary) {
        QuickActionColors(fill = brand.brand, icon = brand.onBrand)
    } else {
        QuickActionColors(fill = brand.brandSoft, icon = brand.onBrandSoft)
    }

/**
 * Home's quick actions (Cobalt plan D6, step K6): a row of tiles, each a rounded icon tile over its
 * label, that renders **exactly** the [actions] it is given, in order. It never decides which
 * actions exist: Budgets is simply not passed until slice D builds its destination, so the row shows
 * three buttons and no dead one. It is inset by the screen gutter like `GroupedSection`. Plain data
 * in, no ViewModel: the samples and the debug catalogue render it until Home is wired (slice C, C4d).
 *
 * **Accessibility.** Each action is one button, named by its label (the icon is decorative), whose
 * whole cell is the target: at least 52dp tall and as wide as its share of the row, so well over
 * 48dp. The label is app text in `onSurface`, never the brand colour. The primary action's emphasis
 * is its fill alone, which is visual: it adds no semantics.
 *
 * **Large text.** The actions share the row in equal columns. When the widest label no longer fits
 * its share (a large font, a narrow window, a fourth action) the row wraps onto further lines with
 * fewer columns, so a label is never broken mid-word or cut; only a label too long for the whole
 * width wraps by words.
 *
 * @param actions the actions to show, in order; an empty list draws nothing.
 */
@Composable
fun QuickActionRow(
    actions: List<QuickAction>,
    modifier: Modifier = Modifier,
) {
    Layout(
        content = { actions.forEach { QuickActionItem(it) } },
        modifier = modifier.fillMaxWidth().padding(horizontal = PftTheme.spacing.gutter),
    ) { measurables, constraints ->
        val room = constraints.maxWidth
        val widest = measurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 1
        // The most columns whose equal share still holds the widest action, and at least one.
        val columns = (room / widest.coerceAtLeast(1)).coerceIn(1, measurables.size.coerceAtLeast(1))
        val columnWidth = room / columns
        val cell = Constraints.fixedWidth(columnWidth).copy(maxHeight = constraints.maxHeight)
        val lineGap = LineGap.roundToPx()
        val lines = measurables.map { it.measure(cell) }.chunked(columns)
        val lineHeights = lines.map { line -> line.maxOf { it.height } }
        val height = lineHeights.sum() + lineGap * (lines.size - 1).coerceAtLeast(0)
        layout(room, height) {
            var y = 0
            lines.forEachIndexed { index, line ->
                line.forEachIndexed { column, placeable -> placeable.place(column * columnWidth, y) }
                y += lineHeights[index] + lineGap
            }
        }
    }
}

/** One tile and its label, as a single button. */
@Composable
private fun QuickActionItem(action: QuickAction) {
    val colours = quickActionColors(PftTheme.brand, action.primary)
    Column(
        Modifier.clickable(role = Role.Button, onClick = action.onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(TileSize).clip(PftTheme.shapes.quickAction).background(colours.fill),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(action.icon.res),
                contentDescription = null,
                tint = colours.icon,
                modifier = Modifier.size(IconSize),
            )
        }
        Text(
            action.label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = LabelGap),
        )
    }
}
