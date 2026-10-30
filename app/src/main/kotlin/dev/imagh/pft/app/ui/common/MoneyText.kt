package dev.imagh.pft.app.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import dev.imagh.pft.R
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
 * @throws IllegalArgumentException if [amount] is negative and [semantic] is not a balance.
 */
@Composable
fun MoneyText(
    amount: Paise,
    semantic: MoneySemantic,
    modifier: Modifier = Modifier,
    size: MoneySize = MoneySize.Row,
) {
    val figure = PaiseFormat.format(amount)
    val text = moneySign(semantic, amount) + figure
    val description = "${directionLabel(semantic, amount)}, $figure"
    val color = toneColor(moneyTone(semantic, amount, inRow = size == MoneySize.Row))
    Text(
        text,
        modifier.semantics { contentDescription = description },
        color = color,
        style = style(size),
    )
}

@Composable
private fun style(size: MoneySize): TextStyle =
    when (size) {
        MoneySize.Hero -> PftTheme.type.amountHero
        MoneySize.Large -> PftTheme.type.amountLarge
        MoneySize.Row -> PftTheme.type.amountRow
    }

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
