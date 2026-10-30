package dev.imagh.pft.app.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.HeroSizeSteps
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat

/** U+2212 MINUS SIGN, the typographic minus (never a hyphen-minus). */
private const val MINUS = "−"

/**
 * The sign glyph for [semantic]: `+` for income, [MINUS] for an expense or a negative balance,
 * and nothing for a transfer, a budget figure or a non-negative balance. Transaction amounts are
 * positive magnitudes (the direction is the semantic), so a negative [amount] is only valid for a
 * [MoneySemantic.Balance]; anything else is a bug upstream and is rejected rather than shown with
 * a sign that would misreport the money.
 *
 * @throws IllegalArgumentException if [amount] is negative and [semantic] is not a balance.
 */
internal fun moneySign(
    semantic: MoneySemantic,
    amount: Paise,
): String {
    require(amount.value >= 0 || semantic == MoneySemantic.Balance) {
        "only a balance may be negative, not $semantic"
    }
    return when (semantic) {
        MoneySemantic.Income -> "+"
        MoneySemantic.Expense -> MINUS
        MoneySemantic.Balance -> if (amount.value < 0) MINUS else ""
        MoneySemantic.Transfer, is MoneySemantic.Budget -> ""
    }
}

/**
 * An amount of money, formatted, signed, toned and spoken (UI-5, plan §3.3, §5.2). The figure is
 * `PaiseFormat.format` from `:core:money`, which is sign-free; this prepends the sign glyph
 * ([moneySign]) and never reformats, and money stays integer paise end to end.
 *
 * Colour is never the only cue (WCAG 1.4.1): the tone comes from [moneyTone] mapped to a
 * `PftTheme.money` token (neutral is `onSurface`), every signed figure carries its sign, and the
 * semantics read the direction before the figure ("Expense, ₹250.00"). Do not draw it on
 * `surfaceVariant`: the money tokens do not clear 4.5:1 there.
 *
 * Named consumers: entity lists, Home, transaction detail, budget detail, loans and goals.
 *
 * @param amount the figure; a magnitude, except for a [MoneySemantic.Balance], which may be negative.
 * @param semantic what the figure means, which decides the sign and tone.
 * @param size the amount style; [MoneySize.Row] is the default.
 * @param animate roll the digits to the new figure when [amount] or [semantic] changes (a hero on
 *   Home, for example). Each character is its own slot, aligned from the end so the units digit
 *   keeps its column; the roll uses `PftMotion.smooth` and jumps to the end state under reduced
 *   motion. Assistive technology still sees one string, and the text is correct as soon as the
 *   state changes, never after the animation. Off by default (list rows do not animate).
 * @throws IllegalArgumentException if [amount] is negative and [semantic] is not a balance.
 */
@Composable
fun MoneyText(
    amount: Paise,
    semantic: MoneySemantic,
    modifier: Modifier = Modifier,
    size: MoneySize = MoneySize.Row,
    animate: Boolean = false,
) {
    val figure = PaiseFormat.format(amount)
    val text = moneySign(semantic, amount) + figure
    val description = "${directionLabel(semantic, amount)}, $figure"
    val color = toneColor(moneyTone(semantic, amount, inRow = size == MoneySize.Row))
    // One string for assistive technology. Rolling digits are one Text per character, so they are
    // hidden from semantics and the whole text and description are set on the container instead.
    val semantics =
        if (animate) {
            Modifier.clearAndSetSemantics {
                this.text = AnnotatedString(text)
                contentDescription = description
            }
        } else {
            Modifier.semantics(mergeDescendants = true) { contentDescription = description }
        }
    if (size == MoneySize.Hero) {
        HeroText(text, color, animate, modifier.then(semantics))
    } else {
        val style = if (size == MoneySize.Large) PftTheme.type.amountLarge else PftTheme.type.amountRow
        Figure(text, style.copy(color = color), animate, Int.MAX_VALUE, modifier.then(semantics))
    }
}

/**
 * The hero figure, set on one line at the largest of [HeroSizeSteps] (48sp down to 12sp) that fits
 * the width it is given, so a long amount shrinks instead of wrapping or clipping. If it is
 * measured with an unbounded width it takes the first (largest) step. If even the smallest step
 * does not fit (an extreme amount at 200% font scale on a very narrow window) it wraps there
 * instead: a money figure is never cut off, because a truncated amount would misreport the money.
 */
@Composable
private fun HeroText(
    text: String,
    color: Color,
    animate: Boolean,
    modifier: Modifier,
) {
    val measurer = rememberTextMeasurer()
    val base = PftTheme.type.amountHero
    val density = LocalDensity.current
    BoxWithConstraints(modifier) {
        val available = if (constraints.hasBoundedWidth) constraints.maxWidth else Int.MAX_VALUE
        val fit = remember(text, available, base, density) { fitHero(measurer, text, base, available) }
        Figure(text, fit.style.copy(color = color), animate, if (fit.fits) 1 else Int.MAX_VALUE, Modifier)
    }
}

/** The figure itself: plain text, or rolling digits. */
@Composable
private fun Figure(
    text: String,
    style: TextStyle,
    animate: Boolean,
    maxLines: Int,
    modifier: Modifier,
) {
    if (animate) {
        RollingDigits(text, style, modifier)
    } else {
        Text(text, modifier, style = style, maxLines = maxLines)
    }
}

/**
 * [text] with each character in its own slot that rolls to its new value: a digit that goes up
 * slides in from below and one that goes down from above, with the leaving character fading out.
 * Slots are keyed from the end of the string, so the units digit keeps its column when the length
 * changes and a new leading character simply appears. Tabular figures (`tnum`) keep the columns
 * from jittering; a custom font without them may shift slightly (design plan §3.3).
 */
@Composable
private fun RollingDigits(
    text: String,
    style: TextStyle,
    modifier: Modifier,
) {
    val motion = PftTheme.motion
    Row(modifier) {
        text.forEachIndexed { index, character ->
            key(text.length - index) {
                AnimatedContent(
                    targetState = character,
                    transitionSpec = {
                        val rollUp = targetState > initialState
                        val sign = if (rollUp) 1 else -1
                        (
                            slideInVertically(motion.smoothOffset) { sign * it } +
                                fadeIn(motion.smooth)
                        ) togetherWith (
                            slideOutVertically(motion.smoothOffset) { -sign * it } +
                                fadeOut(motion.smooth)
                        )
                    },
                    label = "digit",
                ) { shown -> Text(shown.toString(), style = style, maxLines = 1) }
            }
        }
    }
}

/** The hero style chosen for a width, and whether [style] really fits it on one line. */
private class HeroFit(
    val style: TextStyle,
    val fits: Boolean,
)

/** [base] at the largest step whose single-line [text] is at most [maxWidthPx] wide, else the smallest step. */
private fun fitHero(
    measurer: TextMeasurer,
    text: String,
    base: TextStyle,
    maxWidthPx: Int,
): HeroFit {
    val candidates = HeroSizeSteps.map { base.atSize(it) }
    val fitting =
        candidates.firstOrNull {
            measurer.measure(text, it, softWrap = false, maxLines = 1).size.width <= maxWidthPx
        }
    return HeroFit(fitting ?: candidates.last(), fits = fitting != null)
}

/** The style at [size], with the line height scaled in proportion (the letter spacing is in em and scales itself). */
private fun TextStyle.atSize(size: TextUnit): TextStyle =
    copy(fontSize = size, lineHeight = lineHeight * (size.value / fontSize.value))

/** The colour of [tone]: a `PftTheme.money` token, or `onSurface` for neutral. Never derived from the accent. */
@Composable
private fun toneColor(tone: MoneyTone): Color =
    when (tone) {
        MoneyTone.Neutral -> MaterialTheme.colorScheme.onSurface
        MoneyTone.Positive -> PftTheme.money.positive
        MoneyTone.Negative -> PftTheme.money.negative
        MoneyTone.Warning -> PftTheme.money.warning
    }

/** The spoken word that says what the figure is, said before it. */
@Composable
private fun directionLabel(
    semantic: MoneySemantic,
    amount: Paise,
): String =
    stringResource(
        when (semantic) {
            MoneySemantic.Income -> R.string.money_income
            MoneySemantic.Expense -> R.string.money_expense
            MoneySemantic.Transfer -> R.string.money_transfer
            MoneySemantic.Balance ->
                if (amount.value < 0) R.string.money_balance_negative else R.string.money_balance
            is MoneySemantic.Budget ->
                when (semantic.state) {
                    BudgetState.UNDER -> R.string.money_budget_under
                    BudgetState.AT_THRESHOLD -> R.string.money_budget_near_limit
                    BudgetState.OVER -> R.string.money_budget_over
                }
        },
    )
