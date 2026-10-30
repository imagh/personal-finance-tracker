@file:OptIn(ExperimentalMaterial3Api::class)

package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import kotlin.math.max
import kotlin.math.roundToInt

/** How a [CollapsingHeaderScaffold] header is laid out when expanded. */
enum class HeaderStyle {
    /**
     * A figure-led screen (Home, budget detail): a `titleLarge` title high in the upper band, and
     * the hero number, its caption and the period stepper low in the thumb zone.
     */
    Dashboard,

    /** A list, settings or form screen: a `headlineLarge` title centred in the expanded area, no hero. */
    Standard,
}

/**
 * The period control of a [HeaderStyle.Dashboard] header: the scaffold draws a chevron stepper at the
 * end of the caption row and also steps when the expanded header is swiped sideways.
 *
 * @property onPrevious show the previous period (swipe right in a left-to-right layout, or the left chevron).
 * @property onNext show the next period (swipe left in a left-to-right layout, or the right chevron).
 */
class HeaderPeriod(
    val onPrevious: () -> Unit,
    val onNext: () -> Unit,
)

/** Test hooks: stable tags on the header's parts (semantics only; they change nothing visually). */
internal object HeaderTags {
    const val HEADER = "header"
    const val TITLE_LARGE = "header-title-large"
    const val TITLE_BAR = "header-title-bar"
    const val SUBTITLE = "header-subtitle"
    const val HERO = "header-hero"
    const val LOWER_BAND = "header-lower-band"
    const val PERIOD_STEPPER = "header-period-stepper"
    const val LIST = "header-list"
}

/** The collapsed header: a 64dp bar (plus the status-bar inset above it). */
internal val COLLAPSED_HEADER_HEIGHT: Dp = 64.dp

private val MIN_EXPANDABLE_WINDOW_HEIGHT = 560.dp
private val MIN_EXPANDED_HEIGHT = 200.dp
private val MAX_EXPANDED_HEIGHT = 360.dp
private const val EXPANDED_WINDOW_FRACTION = 0.38f

/** Where a Dashboard title's top edge sits, as a fraction of the expanded height below the status bar. */
private const val DASHBOARD_TITLE_TOP_FRACTION = 0.12f

/**
 * The expanded header height for a window [windowHeight] tall: `clamp(0.38 x window, 200dp, 360dp)`.
 * A window shorter than 560dp (landscape, split screen) has no expanded state, so this returns the
 * collapsed bar height and the header starts and stays collapsed. (At 560dp the rule gives 213dp,
 * so the 200dp floor only matters if the 560dp threshold is ever lowered.)
 */
internal fun expandedHeaderHeight(windowHeight: Dp): Dp =
    if (windowHeight < MIN_EXPANDABLE_WINDOW_HEIGHT) {
        COLLAPSED_HEADER_HEIGHT
    } else {
        (windowHeight * EXPANDED_WINDOW_FRACTION).coerceIn(MIN_EXPANDED_HEIGHT, MAX_EXPANDED_HEIGHT)
    }

private enum class HeaderSlot { Actions, TitleLarge, TitleBar, Subtitle, Hero, Caption }

private enum class HeaderPhase { Expanded, Moving, Collapsed }

/**
 * The One UI-style screen frame (UI-7): a big header that collapses to a 64dp bar as the list
 * scrolls, so the content starts about 38% down the screen, within thumb reach. Named consumers:
 * setup, every form (`EditFormScaffold`), `EntityListScreen`, Home, budget detail and settings.
 *
 * **Mechanics.** It reuses M3's `TopAppBarState` and `exitUntilCollapsedScrollBehavior` (snap to the
 * nearer end on release, fling), with the snap using `PftTheme.motion.smooth`. There is no custom
 * nested-scroll code. The scaffold owns the `LazyColumn`; when its content cannot scroll the header
 * stays expanded. In a window shorter than 560dp the header starts and stays collapsed. See
 * [expandedHeaderHeight].
 *
 * **Layout (owner decision Q10).** All titles are sans; the serif is the hero number only. The
 * title is read-only, horizontally centred, in the upper band, with an optional one-line
 * [subtitle]. For [HeaderStyle.Dashboard] it sits about 12% down the expanded header, and the
 * [hero], its [heroCaption] and the [period] stepper are in the lower band, just above the content,
 * where the thumb is. For [HeaderStyle.Standard] the larger title is centred in the middle of the
 * expanded area and there is no lower band. The top row holds only the secondary [actions]. On
 * collapse the title cross-fades and scales into the bar's title and the lower band fades out.
 *
 * **Insets.** Edge-to-edge with `WindowInsets.safeDrawing`; the [bottomBar] is `imePadding()`-aware
 * so a Save button rides above the keyboard, and without a bar the list and the FAB are padded by
 * the bottom inset. Nothing waits for an animation to end.
 *
 * **Use one per screen, at the root.** It reads the raw `safeDrawing` insets, so do not nest it
 * under another Scaffold or layout that consumes insets (the header would be padded twice or not
 * at all), and do not put one inside another.
 *
 * **Dashboard slots.** [hero], [heroCaption] and [period] go together: the lower band exists only
 * when a [hero] is given, and without one the caption and the stepper are not drawn and the
 * sideways swipe does nothing. A window shorter than 560dp has no expanded state, so there the
 * Dashboard hero is hidden too (landscape, split screen), as the plan specifies.
 *
 * @param title the screen name (a sans title; one line, ellipsised).
 * @param style how the expanded header is laid out.
 * @param subtitle an optional one-line caption under the title (for example "October - 3 days left").
 * @param hero Dashboard only: the figure, set in the hero style (display font), for example a
 *   `MoneyText`. Ignored for [HeaderStyle.Standard]. Required for the caption and period to show.
 * @param heroCaption Dashboard only: the caption under the hero ("spent this month").
 * @param period Dashboard only: the period control; see [HeaderPeriod].
 * @param actions secondary actions in the top row (back, search, filter, overflow), laid out left
 *   to right across the full width; use a weighted `Spacer` to push some to the end. Add actions
 *   belong in [floatingActionButton], not here.
 * @param bottomBar a `NavigationBar` or a [BottomActionBar], or null for none. With none, the list
 *   and the FAB are padded by the `safeDrawing` bottom inset instead, so they clear the navigation bar.
 * @param floatingActionButton the add action.
 * @param content the scrolling list.
 */
@Composable
fun CollapsingHeaderScaffold(
    title: String,
    modifier: Modifier = Modifier,
    style: HeaderStyle = HeaderStyle.Standard,
    subtitle: String? = null,
    hero: (@Composable () -> Unit)? = null,
    heroCaption: String? = null,
    period: HeaderPeriod? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: (@Composable () -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    val sizes = headerSizes()
    val alwaysCollapsed = sizes.alwaysCollapsed

    val barState = rememberTopAppBarState()
    val limit = -(sizes.expanded - sizes.collapsed)
    SideEffect {
        // Same as M3's own bars: the limit follows the layout. Re-assigning the offset re-clamps it
        // after a rotation or a window change.
        if (barState.heightOffsetLimit != limit) {
            barState.heightOffsetLimit = limit
            barState.heightOffset = barState.heightOffset
        }
    }
    val listState = rememberLazyListState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = barState,
            // A list that fits keeps the header expanded. Once collapsed, stay interactive so the
            // header can always be pulled back open.
            canScroll = {
                !alwaysCollapsed &&
                    (listState.canScrollForward || listState.canScrollBackward || barState.collapsedFraction > 0f)
            },
            snapAnimationSpec = PftTheme.motion.smooth,
        )

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingHeader(barState, sizes, HeaderContent(title, style, subtitle, hero, heroCaption, period, actions))
        },
        // M3's Scaffold pads the list and the FAB by the bottom inset only when this slot emits
        // nothing, so with no bar it must emit nothing (not an empty wrapper).
        bottomBar = { if (bottomBar != null) Box(Modifier.imePadding()) { bottomBar() } },
        floatingActionButton = floatingActionButton,
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().testTag(HeaderTags.LIST),
            state = listState,
            contentPadding = padding,
            content = content,
        )
    }
}

@Composable
private fun headerSizes(): HeaderSizes {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val insets = WindowInsets.safeDrawing
    val topInset = insets.getTop(density)
    val collapsed = with(density) { COLLAPSED_HEADER_HEIGHT.toPx() } + topInset
    val windowHeight = LocalConfiguration.current.screenHeightDp.dp
    val expanded = max(collapsed, with(density) { expandedHeaderHeight(windowHeight).toPx() })
    return HeaderSizes(
        expanded = expanded,
        collapsed = collapsed,
        topInset = topInset,
        startInset = insets.getLeft(density, layoutDirection),
        endInset = insets.getRight(density, layoutDirection),
        alwaysCollapsed = expanded <= collapsed,
    )
}

/** What a header shows, gathered from the scaffold's parameters. */
@Suppress("LongParameterList") // a plain holder: one field per scaffold slot
private class HeaderContent(
    val title: String,
    val style: HeaderStyle,
    val subtitle: String?,
    val hero: (@Composable () -> Unit)?,
    val heroCaption: String?,
    val period: HeaderPeriod?,
    val actions: @Composable RowScope.() -> Unit,
) {
    val dashboard get() = style == HeaderStyle.Dashboard
}

/** The header's sizes in px: [expanded] and [collapsed] heights include the status-bar inset. */
private data class HeaderSizes(
    val expanded: Float,
    val collapsed: Float,
    val topInset: Int,
    val startInset: Int,
    val endInset: Int,
    val alwaysCollapsed: Boolean,
)

@Composable
private fun CollapsingHeader(
    state: TopAppBarState,
    sizes: HeaderSizes,
    content: HeaderContent,
) {
    val typography = MaterialTheme.typography
    val largeStyle = if (content.dashboard) typography.titleLarge else typography.headlineLarge
    // Which parts exist changes only at the ends of the collapse, so composition does not run on
    // every scroll frame; the in-between motion is layout and layer work only.
    val phase by remember(sizes.alwaysCollapsed) {
        derivedStateOf {
            when {
                sizes.alwaysCollapsed || state.collapsedFraction >= 1f -> HeaderPhase.Collapsed
                state.collapsedFraction <= 0f -> HeaderPhase.Expanded
                else -> HeaderPhase.Moving
            }
        }
    }
    val swipe =
        if (content.dashboard && content.hero != null && phase == HeaderPhase.Expanded) {
            Modifier.periodSwipe(content.period)
        } else {
            Modifier
        }
    val policy =
        remember(state, sizes, content.dashboard) {
            HeaderPolicy(
                state,
                sizes,
                content.dashboard,
                typography.titleLarge.fontSize.value / largeStyle.fontSize.value,
            )
        }
    Layout(
        content = { HeaderChildren(content, phase, largeStyle) },
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                ).then(swipe)
                .testTag(HeaderTags.HEADER),
        measurePolicy = policy,
    )
}

/** The parts of the header that exist in [phase]; each carries a [HeaderSlot] id for the layout. */
@Composable
private fun HeaderChildren(
    content: HeaderContent,
    phase: HeaderPhase,
    largeStyle: TextStyle,
) {
    val typography = MaterialTheme.typography
    Row(
        Modifier.layoutId(HeaderSlot.Actions),
        verticalAlignment = Alignment.CenterVertically,
        content = content.actions,
    )
    if (phase != HeaderPhase.Collapsed) {
        HeaderTitle(content.title, largeStyle, HeaderSlot.TitleLarge, HeaderTags.TITLE_LARGE)
        if (content.subtitle != null) {
            Text(
                content.subtitle,
                Modifier.layoutId(HeaderSlot.Subtitle).testTag(HeaderTags.SUBTITLE),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
    if (phase != HeaderPhase.Expanded) {
        HeaderTitle(content.title, typography.titleLarge, HeaderSlot.TitleBar, HeaderTags.TITLE_BAR)
    }
    if (content.dashboard && content.hero != null && phase != HeaderPhase.Collapsed) {
        LowerBand(content.hero, content.heroCaption, content.period)
    }
}

/** The thumb-zone band: the hero, then its caption with the period stepper at the end. */
@Composable
private fun LowerBand(
    hero: @Composable () -> Unit,
    caption: String?,
    period: HeaderPeriod?,
) {
    Box(Modifier.layoutId(HeaderSlot.Hero).testTag(HeaderTags.HERO)) {
        // The hero slot defaults to the display font; the serif is for this number only.
        ProvideTextStyle(PftTheme.type.amountHero, hero)
    }
    if (caption != null || period != null) {
        Row(
            Modifier.layoutId(HeaderSlot.Caption).fillMaxWidth().testTag(HeaderTags.LOWER_BAND),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                caption.orEmpty(),
                Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (period != null) PeriodStepper(period)
        }
    }
}

/**
 * Steps the [period] when the header is dragged sideways by more than 48dp: toward the end of the
 * line reveals the previous period, toward the start the next. Vertical drags never reach it. It
 * does nothing when [period] is null.
 */
@Composable
private fun Modifier.periodSwipe(period: HeaderPeriod?): Modifier {
    if (period == null) return this
    val current by rememberUpdatedState(period)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val thresholdPx = with(LocalDensity.current) { PftTheme.spacing.huge.toPx() }
    return pointerInput(rtl) {
        var travelled = 0f
        detectHorizontalDragGestures(
            onDragStart = { travelled = 0f },
            onDragEnd = {
                val towardEnd = if (rtl) -travelled else travelled
                when {
                    towardEnd >= thresholdPx -> current.onPrevious()
                    towardEnd <= -thresholdPx -> current.onNext()
                }
            },
            onHorizontalDrag = { _, delta -> travelled += delta },
        )
    }
}

/**
 * Measures and places the header's parts from the live scroll state. It reads
 * `state.heightOffset` and `collapsedFraction` in the layout phase, so scrolling re-runs only
 * layout and layer work, never composition.
 */
private class HeaderPolicy(
    private val state: TopAppBarState,
    private val sizes: HeaderSizes,
    private val dashboard: Boolean,
    private val barTitleScale: Float,
) : MeasurePolicy {
    /** The measured parts and the spacing in px that placing needs. */
    @Suppress("LongParameterList") // a plain holder: one field per part
    private class Parts(
        val actions: Placeable?,
        val large: Placeable?,
        val bar: Placeable?,
        val sub: Placeable?,
        val hero: Placeable?,
        val caption: Placeable?,
        val bandStartX: Int,
        val bottomPad: Int,
        val subGap: Int,
    )

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val width = constraints.maxWidth
        val fraction = if (sizes.alwaysCollapsed) 1f else state.collapsedFraction.coerceIn(0f, 1f)
        val height =
            if (sizes.alwaysCollapsed) {
                sizes.collapsed
            } else {
                (sizes.expanded + state.heightOffset).coerceIn(sizes.collapsed, sizes.expanded)
            }
        val parts = measureParts(measurables, width)
        return layout(width, height.roundToInt()) {
            parts.actions?.placeRelative(sizes.startInset, sizes.topInset)
            placeTitles(parts, width, fraction)
            placeLowerBand(parts, height.roundToInt(), fraction)
        }
    }

    private fun MeasureScope.measureParts(
        measurables: List<Measurable>,
        width: Int,
    ): Parts {
        val spacing = PftTheme.spacing
        val barPx = (sizes.collapsed - sizes.topInset).roundToInt()
        val inset = max(sizes.startInset, sizes.endInset)
        val titleMax = max(0, width - 2 * (inset + (spacing.huge + spacing.xl).roundToPx()))
        val bandWidth = max(0, width - sizes.startInset - sizes.endInset - 2 * spacing.gutter.roundToPx())

        fun measureSlot(
            slot: HeaderSlot,
            constraints: Constraints,
        ) = measurables.firstOrNull { it.layoutId == slot }?.measure(constraints)
        val title = Constraints(maxWidth = titleMax)
        return Parts(
            actions =
                measureSlot(
                    HeaderSlot.Actions,
                    Constraints.fixed(max(0, width - sizes.startInset - sizes.endInset), barPx),
                ),
            large = measureSlot(HeaderSlot.TitleLarge, title),
            bar = measureSlot(HeaderSlot.TitleBar, title),
            sub = measureSlot(HeaderSlot.Subtitle, title),
            hero = measureSlot(HeaderSlot.Hero, Constraints(maxWidth = bandWidth)),
            caption = measureSlot(HeaderSlot.Caption, Constraints.fixedWidth(bandWidth)),
            bandStartX = sizes.startInset + spacing.gutter.roundToPx(),
            bottomPad = spacing.lg.roundToPx(),
            subGap = spacing.xs.roundToPx(),
        )
    }

    /**
     * The large title travels from its expanded spot (Dashboard: 12% down; Standard: the middle) to
     * the bar's centre, shrinking to the bar title's size while the bar title fades in over it.
     */
    private fun Placeable.PlacementScope.placeTitles(
        parts: Parts,
        width: Int,
        fraction: Float,
    ) {
        val barCentre = sizes.topInset + (sizes.collapsed - sizes.topInset) / 2f
        val large = parts.large
        if (large != null) {
            val gap = if (parts.sub != null) parts.subGap else 0
            val block = large.height + gap + (parts.sub?.height ?: 0)
            val body = sizes.expanded - sizes.topInset
            val expandedTop =
                if (dashboard) {
                    sizes.topInset + DASHBOARD_TITLE_TOP_FRACTION * body
                } else {
                    sizes.topInset + (body - block) / 2f
                }
            val centre = lerp(expandedTop + large.height / 2f, barCentre, fraction)
            val scale = lerp(1f, barTitleScale, fraction)
            large.placeRelativeWithLayer(centredX(width, large), (centre - large.height / 2f).roundToInt()) {
                alpha = 1f - fraction
                scaleX = scale
                scaleY = scale
            }
            parts.sub?.let { sub ->
                val top = centre + large.height * scale / 2f + gap
                sub.placeRelativeWithLayer(centredX(width, sub), top.roundToInt()) { alpha = fadeOut(fraction) }
            }
        }
        parts.bar?.let { bar ->
            bar.placeRelativeWithLayer(centredX(width, bar), (barCentre - bar.height / 2f).roundToInt()) {
                alpha =
                    fraction
            }
        }
    }

    /** The hero and its caption row hang from the header's bottom edge and fade out as it collapses. */
    private fun Placeable.PlacementScope.placeLowerBand(
        parts: Parts,
        height: Int,
        fraction: Float,
    ) {
        val fade = fadeOut(fraction)
        var cursor = height - parts.bottomPad
        parts.caption?.let {
            cursor -= it.height
            it.placeRelativeWithLayer(parts.bandStartX, cursor) { alpha = fade }
        }
        parts.hero?.let {
            cursor -= it.height
            it.placeRelativeWithLayer(parts.bandStartX, cursor) { alpha = fade }
        }
    }

    private fun centredX(
        width: Int,
        part: Placeable,
    ) = ((width - part.width) / 2f).roundToInt()

    /** The lower band and subtitle fade three times faster than the collapse proceeds. */
    private fun fadeOut(fraction: Float) = (1f - LOWER_FADE_RATE * fraction).coerceIn(0f, 1f)
}

private const val LOWER_FADE_RATE = 3f

@Composable
private fun HeaderTitle(
    title: String,
    style: TextStyle,
    slot: HeaderSlot,
    tag: String,
) {
    Text(
        title,
        Modifier.layoutId(slot).testTag(tag).semantics { heading() },
        color = MaterialTheme.colorScheme.onSurface,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
    )
}

/** The previous and next period buttons: chevron symbols ([HeaderIconButton]), labelled for TalkBack. */
@Composable
private fun PeriodStepper(period: HeaderPeriod) {
    Row(Modifier.testTag(HeaderTags.PERIOD_STEPPER)) {
        HeaderIconButton(PftSymbol.CHEVRON_LEFT, stringResource(R.string.header_period_previous), period.onPrevious)
        HeaderIconButton(PftSymbol.CHEVRON_RIGHT, stringResource(R.string.header_period_next), period.onNext)
    }
}
