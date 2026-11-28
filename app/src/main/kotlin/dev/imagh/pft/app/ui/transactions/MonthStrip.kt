package dev.imagh.pft.app.ui.transactions

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.query.MonthSpan
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** The smallest a month can be drawn, wide and tall: the accessibility floor for a tap target. */
private val TARGET = 48.dp

/** The size of the selected month's underline. */
private val UNDERLINE_HEIGHT = 2.dp
private val UNDERLINE_WIDTH = 24.dp

/** How many months to keep before the selected one when the strip has to scroll to it. */
private const val LEAD_MONTHS = 2

/** Every month of [span], first to last, in order. */
internal fun monthsIn(span: MonthSpan): List<YearMonth> =
    generateSequence(span.first) { it.plusMonths(1) }.takeWhile { it <= span.last }.toList()

/**
 * Whether the month at [index] in the strip carries its year underneath: the first month shown (so the
 * reader always knows which year the strip starts in) and every January.
 */
internal fun showsYear(
    index: Int,
    month: YearMonth,
): Boolean = index == 0 || month.month == Month.JANUARY

/**
 * The horizontal month strip of the Transactions tab (slice C C4a): one entry per month in [months], the
 * [selected] one underlined and bold, and the year under the first month and each January. Tapping a
 * month calls [onSelect] (also for the month already selected, so the caller decides what that means).
 *
 * The strip scrolls sideways. When the selected month is not fully in view (the first time the strip
 * shows, or the month was changed by a swipe on the list) it is scrolled into view; selecting a month
 * that is already in view does not move the strip under the finger.
 *
 * **Accessibility.** The months are one `selectableGroup` of radio buttons with the selected one marked,
 * each at least 48dp wide and tall. A month speaks its full name and year ("November 2026") whatever it
 * draws, so a screen reader never needs the caption. The underline is not the only cue: the selected
 * month is also bolder.
 *
 * @param months the months to list, in order ([monthsIn]); must contain [selected] to show it.
 * @param selected the month whose entries are on screen.
 * @param onSelect called with the tapped month.
 */
@Composable
internal fun MonthStrip(
    months: List<YearMonth>,
    selected: YearMonth,
    onSelect: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val formats = remember(locale) { MonthFormats(locale) }
    val state = rememberLazyListState()
    val selectedIndex = months.indexOf(selected)
    LaunchedEffect(selected, months) {
        if (selectedIndex >= 0 && !state.isFullyInView(selectedIndex)) {
            state.scrollToItem((selectedIndex - LEAD_MONTHS).coerceAtLeast(0))
        }
    }
    val captionHeight =
        with(LocalDensity.current) {
            MaterialTheme.typography.labelSmall.lineHeight
                .toDp()
        }
    LazyRow(
        modifier.selectableGroup(),
        state = state,
        contentPadding = PaddingValues(horizontal = PftTheme.spacing.gutter),
        horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.xs),
    ) {
        itemsIndexed(months, key = { _, month -> month.toString() }) { index, month ->
            MonthItem(
                month = month,
                showYear = showsYear(index, month),
                selected = month == selected,
                formats = formats,
                captionHeight = captionHeight,
                onClick = { onSelect(month) },
            )
        }
    }
}

/** One month: its short name, the year caption (or the space for it), and the underline (or the space). */
@Composable
@Suppress("LongParameterList") // Private; its parameters are its whole state, a holder would only rename them.
private fun MonthItem(
    month: YearMonth,
    showYear: Boolean,
    selected: Boolean,
    formats: MonthFormats,
    captionHeight: Dp,
    onClick: () -> Unit,
) {
    val spoken = formats.spoken(month)
    val labelStyle = MaterialTheme.typography.labelLarge
    Column(
        Modifier
            .defaultMinSize(minWidth = TARGET, minHeight = TARGET)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            // One name for the whole month: the short name and the year caption are visual only.
            .clearAndSetSemantics { contentDescription = spoken }
            .padding(horizontal = PftTheme.spacing.sm, vertical = PftTheme.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            formats.short(month),
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            style = labelStyle.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
        )
        if (showYear) {
            Text(
                formats.year(month),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        } else {
            Spacer(Modifier.height(captionHeight))
        }
        val line = Modifier.width(UNDERLINE_WIDTH).height(UNDERLINE_HEIGHT)
        Spacer(
            if (selected) {
                line.testTag(MonthStripTags.UNDERLINE).background(PftTheme.brand.brand, PftTheme.shapes.tabBar)
            } else {
                line
            },
        )
    }
}

/** Whether item [index] is drawn completely inside the strip (an item that is not laid out is not in view). */
private fun LazyListState.isFullyInView(index: Int): Boolean {
    val info = layoutInfo
    val item = info.visibleItemsInfo.firstOrNull { it.index == index } ?: return false
    return item.offset >= info.viewportStartOffset && item.offset + item.size <= info.viewportEndOffset
}

/** The month's three spellings in [locale]: short name, year, and the full spoken "November 2026". */
private class MonthFormats(
    private val locale: Locale,
) {
    private val full = DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, "yMMMM"), locale)
    private val yearOnly = DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, "y"), locale)

    fun short(month: YearMonth): String = month.month.getDisplayName(TextStyle.SHORT_STANDALONE, locale)

    fun year(month: YearMonth): String = month.format(yearOnly)

    fun spoken(month: YearMonth): String = month.format(full)
}
