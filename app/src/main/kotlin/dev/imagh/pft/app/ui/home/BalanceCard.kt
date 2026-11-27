package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.MoneySemantic
import dev.imagh.pft.app.ui.common.MoneySize
import dev.imagh.pft.app.ui.common.MoneyText
import dev.imagh.pft.app.ui.theme.BrandColors
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat

/**
 * The alpha of the meter's track: `onCard` over the gradient. With the solid `onCard` fill it keeps
 * the fill at 3:1 or more against the track on every palette and mode (`ColorContrastTest`).
 */
internal const val METER_TRACK_ALPHA = 0.22f

/**
 * The alpha of the card's decorative ring: `onCard` over the gradient, drawn behind the text. The ring
 * sits at the right and reaches back to about a third of the way along the gradient, never the start corner,
 * which is the lightest part. `ColorContrastTest` pins `onCard` over the ringed gradient at 4.5:1 on every
 * palette and mode.
 */
internal const val RING_ALPHA = 0.08f

/**
 * The ring stays on the card's end side of this point of the gradient line (0 is the start corner, 1 the end
 * corner): `BalanceCardTest` probes the start side for it, and `ColorContrastTest` pins `onCard` over the ringed
 * gradient at this point, the worst place the ring can sit under text.
 */
internal const val RING_MIN_LINE_FRACTION = 0.28f

/** The line's reach per axis from the centre: a quarter of `width + height`. */
private const val LINE_QUARTER = 0.25f

/** Where the middle gradient stop sits along the line, as in the mockup (`start 0%, mid 55%, end 100%`). */
internal const val MID_STOP = 0.55f

/**
 * The two ends of the gradient line of a [width] by [height] card for a CSS `135deg` gradient: a
 * 45 degree line through the centre that is `(width + height) / sqrt 2` long, so the corners are the
 * 0% and 100% points exactly. (Compose's default line runs corner to corner, which tilts it on a wide card.)
 */
internal fun cardGradientLine(
    width: Float,
    height: Float,
): Pair<Offset, Offset> {
    val half = (width + height) * LINE_QUARTER
    val centre = Offset(width / 2f, height / 2f)
    return Offset(centre.x - half, centre.y - half) to Offset(centre.x + half, centre.y + half)
}

/**
 * The ring, as the mockup draws it: a 200dp box with a 28dp border added outside it (`*` does not reach
 * `::after`, so its `box-sizing` is content-box), so a 256dp circle whose stroke runs from radius 100 to 128,
 * positioned `right: -60dp; top: -40dp`. Its centre is therefore 68dp in from the card's right edge and 88dp down.
 */
private val RingRadius = 114.dp
private val RingStroke = 28.dp
private val RingFromRight = 68.dp
private val RingFromTop = 88.dp

/** The ghosts peek above the card: the far one by [GhostFarPeek], the near one by [GhostNearPeek]. */
private val GhostFarPeek = 15.dp
private val GhostNearPeek = 8.dp
private val GhostHeight = 30.dp
private val GhostFarInset = 30.dp
private val GhostNearInset = 14.dp
private val GhostShape = RoundedCornerShape(20.dp)

private val CardPadding = 18.dp
private val MeterHeight = 5.dp
private val MeterShape = RoundedCornerShape(3.dp)
private val MeterLabelGap = 6.dp
private val ArrowSize = 16.dp

/**
 * What the meter shows: how much of the budget is used and the words that say so. A meter is drawn
 * only for a budget that exists (Cobalt plan D6), so it is optional on [BalanceCardModel].
 *
 * @property fraction the share of the budget used, from 0 to 1 inclusive; the caller clamps an
 *   over-budget share to 1 and says "over" in [label].
 * @property label the words beside the bar ("64% of ₹75,000.00 budget · 5 days left"), already
 *   formatted by the caller. It is the only thing a screen reader says about the meter.
 * @throws IllegalArgumentException if [fraction] is not a number from 0 to 1.
 */
@Immutable
data class BudgetMeter(
    val fraction: Float,
    val label: String,
) {
    init {
        require(fraction in 0f..1f) { "a meter fraction is a number from 0 to 1" }
    }
}

/**
 * Everything [BalanceCard] shows, as plain data: no entity and no month logic. Home's data slice
 * builds it from the month's totals.
 *
 * @property title the card's heading ("Household · November"), already composed by the caller.
 * @property expense the month's spend: the hero, and the "Out" figure. A magnitude.
 * @property income the month's income: the "In" figure. A magnitude.
 * @property budget the budget meter, or null for none: the card then draws no bar and no label.
 * @throws IllegalArgumentException if [expense] or [income] is negative.
 */
@Immutable
data class BalanceCardModel(
    val title: String,
    val expense: Paise,
    val income: Paise,
    val budget: BudgetMeter? = null,
) {
    init {
        require(expense.value >= 0) { "a month's spend is a magnitude" }
        require(income.value >= 0) { "a month's income is a magnitude" }
    }
}

/**
 * Home's balance card (Cobalt plan D4, step K6): the month's spend as a hero on the brand gradient
 * (`cardStart` at 0%, `cardMid` at 55%, `cardEnd` at 100% on a true 135 degree line, as the mockup's CSS),
 * a decorative ring at the right, two decorative slabs (`cardGhost2`, `cardGhost`) peeking above it, the
 * words "Spent this month", "In" and "Out" and, only when
 * [BalanceCardModel.budget] is given, the budget meter (hidden until slice D, D6). It is inset by
 * the screen gutter like `GroupedSection`, and its top edge is the far ghost's, so it needs no
 * extra top spacing. Plain data in, no entity and no ViewModel: the samples and the debug catalogue
 * render it until Home is wired to the month's totals (slice C, C4d).
 *
 * **Contrast (D4).** Every text on the card is solid `onCard`, never alpha-faded; the hierarchy is
 * size and weight. The hero is a [MoneyText] with `onColor = onCard` and `fadeDecimals`, so its
 * decimals fade only while it is at a large-text step. `ColorContrastTest` pins `onCard` on every
 * gradient stop in every palette and mode. The ring (a 28dp stroke of `onCard` at [RING_ALPHA], 8%, behind
 * the text, clipped by the card) lightens what is under some text, so the same test pins `onCard` over the
 * ringed mid and end stops and over the ringed gradient at the ring's furthest reach toward the start
 * ([RING_MIN_LINE_FRACTION]); the ring never reaches the start corner, where it would fall below 4.5:1 in Cobalt
 * light. It is drawn, not a node: no semantics.
 *
 * **Meaning is words, never colour.** "Spent this month" captions the hero (an expense, spoken
 * "Expense, ₹48,250.40"); "In" and "Out" name the two figures and show them unsigned. [MoneyText]
 * cannot draw income on the blue (green on blue is refused), so these two figures are plain text in
 * `PaiseFormat`'s output and the `amountRow` style (tabular digits). Each of "In" and "Out" is one
 * merged node that says its word and its figure once; the arrows, the ghosts and the meter bar have
 * no semantics, so the meter's label is said once.
 *
 * **Large text.** The hero shrinks and never clips ([MoneyText]). "In" and "Out" sit side by side
 * when both fit the card's width and one under the other when they do not, so a long amount is never
 * cut; the title and the labels wrap.
 *
 * @param model what to show.
 */
@Composable
fun BalanceCard(
    model: BalanceCardModel,
    modifier: Modifier = Modifier,
) {
    val brand = PftTheme.brand
    Box(modifier.fillMaxWidth().padding(horizontal = PftTheme.spacing.gutter)) {
        Ghost(brand.cardGhost2, GhostFarInset, top = 0.dp)
        Ghost(brand.cardGhost, GhostNearInset, top = GhostFarPeek - GhostNearPeek)
        Column(
            Modifier
                .padding(top = GhostFarPeek)
                .fillMaxWidth()
                .clip(PftTheme.shapes.card)
                .drawBehind { drawCardBackground(brand) }
                .padding(CardPadding),
        ) {
            CardContent(model)
        }
    }
}

/**
 * The card's gradient (stops at 0, 55% and 100% on the 135 degree line) and, over it but under the
 * content, the decorative ring: a 28dp stroke of `onCard` at [RING_ALPHA], clipped by the card.
 */
private fun DrawScope.drawCardBackground(brand: BrandColors) {
    val (start, end) = cardGradientLine(size.width, size.height)
    drawRect(
        Brush.linearGradient(
            0f to brand.cardStart,
            MID_STOP to brand.cardMid,
            1f to brand.cardEnd,
            start = start,
            end = end,
        ),
    )
    drawCircle(
        brand.onCard.copy(alpha = RING_ALPHA),
        radius = RingRadius.toPx(),
        center = Offset(size.width - RingFromRight.toPx(), RingFromTop.toPx()),
        style = Stroke(RingStroke.toPx()),
    )
}

/** A decorative slab behind the card, narrower than it by [inset] on each side: drawn, never spoken. */
@Composable
private fun Ghost(
    colour: Color,
    inset: Dp,
    top: Dp,
) {
    Box(
        Modifier
            .padding(start = inset, top = top, end = inset)
            .fillMaxWidth()
            .height(GhostHeight)
            .background(colour, GhostShape),
    )
}

/** The title, the caption, the hero, the optional meter and "In" and "Out". */
@Composable
private fun CardContent(model: BalanceCardModel) {
    val onCard = PftTheme.brand.onCard
    Text(model.title, color = onCard, style = MaterialTheme.typography.labelLarge)
    Text(
        stringResource(R.string.balance_card_caption),
        color = onCard,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = PftTheme.spacing.md),
    )
    MoneyText(
        model.expense,
        MoneySemantic.Expense,
        size = MoneySize.Hero,
        onColor = onCard,
        fadeDecimals = true,
    )
    model.budget?.let { Meter(it) }
    InOut(model.income, model.expense, Modifier.padding(top = PftTheme.spacing.md))
}

/** The budget bar and its words. The bar has no semantics: [BudgetMeter.label] is what is spoken. */
@Composable
private fun Meter(meter: BudgetMeter) {
    val onCard = PftTheme.brand.onCard
    Box(
        Modifier
            .padding(top = PftTheme.spacing.md)
            .fillMaxWidth()
            .height(MeterHeight)
            .clip(MeterShape)
            .background(onCard.copy(alpha = METER_TRACK_ALPHA)),
    ) {
        Box(Modifier.fillMaxWidth(meter.fraction).fillMaxHeight().background(onCard))
    }
    Text(
        meter.label,
        color = onCard,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = MeterLabelGap),
    )
}

/**
 * "In" and "Out": side by side, Out at the end, when both fit on the card's width; one under the
 * other when they do not. The choice reads each item's natural width, so a long amount or a large
 * font stacks them instead of wrapping a figure.
 */
@Composable
private fun InOut(
    income: Paise,
    expense: Paise,
    modifier: Modifier = Modifier,
) {
    val gap = PftTheme.spacing.md
    Layout(
        content = {
            Flow(stringResource(R.string.balance_card_in), PftSymbol.SOUTH_WEST, income, arrowFirst = true)
            Flow(stringResource(R.string.balance_card_out), PftSymbol.NORTH_EAST, expense, arrowFirst = false)
        },
        modifier = modifier.fillMaxWidth(),
    ) { measurables, constraints ->
        val gapPx = gap.roundToPx()
        val (first, second) = measurables
        val room = constraints.maxWidth
        val natural = first.maxIntrinsicWidth(constraints.maxHeight) + second.maxIntrinsicWidth(constraints.maxHeight)
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val a = first.measure(loose)
        val b = second.measure(loose)
        if (natural + gapPx <= room) {
            val height = maxOf(a.height, b.height)
            layout(room, height) {
                a.place(0, (height - a.height) / 2)
                b.place(room - b.width, (height - b.height) / 2)
            }
        } else {
            layout(room, a.height + gapPx + b.height) {
                a.place(0, 0)
                b.place(0, a.height + gapPx)
            }
        }
    }
}

/** "In" or "Out": a decorative arrow, the word and the unsigned figure, merged into one node. */
@Composable
private fun Flow(
    word: String,
    symbol: PftSymbol,
    amount: Paise,
    arrowFirst: Boolean,
) {
    val onCard = PftTheme.brand.onCard
    Row(
        Modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.xs),
    ) {
        if (arrowFirst) Arrow(symbol)
        Text(word, color = onCard, style = MaterialTheme.typography.bodySmall)
        Text(PaiseFormat.format(amount), color = onCard, style = PftTheme.type.amountRow)
        if (!arrowFirst) Arrow(symbol)
    }
}

/** A decorative arrow: the word beside it says the same. */
@Composable
private fun Arrow(symbol: PftSymbol) {
    Icon(
        painterResource(symbol.res),
        contentDescription = null,
        tint = PftTheme.brand.onCard,
        modifier = Modifier.size(ArrowSize),
    )
}
