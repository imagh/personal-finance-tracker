package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import dev.imagh.pft.app.ui.theme.PftTheme

/** Test hooks: stable tags on the parts of a `GroupedSection` (semantics only). */
internal object GroupedSectionTags {
    const val DIVIDER = "grouped-section-divider"
    const val ROW = "grouped-section-row"
}

/**
 * The island's fill. A `surfaceContainer` step, one tone off the screen's `surface`: never
 * `surfaceVariant`, because rows hold money text and the money tokens do not clear 4.5:1 there
 * (`ColorContrastTest` sweeps this colour in both themes). A pure function so that test needs no
 * composition.
 */
internal fun groupedSectionColor(scheme: ColorScheme): Color = scheme.surfaceContainer

/** What a `GroupedSection` row looks like to its content; see [GroupedSection]. */
object GroupedSectionDefaults {
    /**
     * M3 `ListItem` colours with a transparent container. A `ListItem` paints its own `surface`
     * container by default, which would hide the island, so pass this to every `ListItem` in a row.
     */
    @Composable
    fun listItemColors(): ListItemColors = ListItemDefaults.colors(containerColor = Color.Transparent)
}

internal class GroupedRow(
    val onClick: (() -> Unit)?,
    val content: @Composable () -> Unit,
)

/** The builder scope of [GroupedSection]: add its rows with [row]. */
class GroupedSectionScope internal constructor() {
    private val collected = mutableListOf<GroupedRow>()
    internal val rows: List<GroupedRow> get() = collected

    /**
     * Adds a row. With an [onClick] the whole row is clickable and at least 56dp tall (the 48dp
     * touch-target floor with room to spare); without one it is plain content.
     */
    fun row(
        onClick: (() -> Unit)? = null,
        content: @Composable () -> Unit,
    ) {
        collected += GroupedRow(onClick, content)
    }
}

/**
 * An inset, rounded "island" of rows separated by hairline dividers: the app's replacement for
 * shadowed cards (UI-4, plan §3.1). It is inset from the screen edges by the 20dp gutter, filled
 * with a surface container ([groupedSectionColor]) and clipped to the large shape. Dividers are
 * drawn between rows only (N rows, N-1 dividers), inset from the start edge so they do not touch
 * the island's side, and are one physical pixel tall (`Dp.Hairline` lays out as zero height, so it
 * could not be measured).
 *
 * Use it inside a scrolling list (`item { GroupedSection { ... } }`). Rows are usually M3
 * `ListItem`s given [GroupedSectionDefaults.listItemColors]. Money in a row is a `MoneyText`.
 *
 * Named consumers: the Home accounts strip, transaction and budget detail, settings, diagnostics
 * and the audit history list.
 *
 * @param rows adds the rows with [GroupedSectionScope.row].
 */
@Composable
fun GroupedSection(
    modifier: Modifier = Modifier,
    rows: GroupedSectionScope.() -> Unit,
) {
    val spacing = PftTheme.spacing
    val items = GroupedSectionScope().apply(rows).rows
    Surface(
        modifier.fillMaxWidth().padding(horizontal = spacing.gutter),
        shape = MaterialTheme.shapes.large,
        color = groupedSectionColor(MaterialTheme.colorScheme),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(
                        Modifier.padding(start = spacing.lg).testTag(GroupedSectionTags.DIVIDER),
                        thickness = Dp(1f / LocalDensity.current.density), // one physical pixel
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                val rowModifier =
                    if (item.onClick != null) {
                        Modifier.heightIn(min = spacing.huge + spacing.sm).clickable(onClick = item.onClick)
                    } else {
                        Modifier
                    }
                Column(rowModifier.fillMaxWidth().testTag(GroupedSectionTags.ROW)) { item.content() }
            }
        }
    }
}
