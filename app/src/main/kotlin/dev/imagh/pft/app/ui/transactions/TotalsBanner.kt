package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.GroupedSection
import dev.imagh.pft.app.ui.common.MoneySemantic
import dev.imagh.pft.app.ui.common.MoneyText
import dev.imagh.pft.app.ui.common.spokenAmount
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat

/** From this font scale up a line puts its amount under the word, not beside it: the amount needs the width. */
private const val STACK_FONT_SCALE = 1.5f

/** The drawn size of a line's arrow (the Material Symbols optical size). */
private val ARROW_SIZE = 20.dp

/**
 * The month's totals under the month strip (slice C C4a): three lines in one island, Expense (an arrow
 * out), Income (an arrow in) and Net (an equals sign), each a word and a [MoneyText]. Expense carries a minus
 * and income a plus; both come from core's `Totals` as magnitudes, and the net is `Totals.net` shown as a
 * [MoneySemantic.Net]: signed, and neutral whether it is negative or not (a loss is not an alarm, plan D5).
 * Only income is green.
 *
 * **Accessibility.** Each line is one node that speaks its figure the way [MoneyText] does, word first
 * ("Expense, ₹2,050.00", "Net, minus ₹300.00"): the visible word and the arrow are decorative. It is
 * information, not a control.
 *
 * **Text fit.** At a font scale of [STACK_FONT_SCALE] and up each line puts its amount under its word, so
 * the largest amount ([Paise.MAX_AMOUNT]) stays whole on a 320dp screen at 200%.
 *
 * @param totals the expense and income of the month under the current filter.
 */
@Composable
internal fun TotalsBanner(
    totals: Totals,
    modifier: Modifier = Modifier,
) {
    val stacked = LocalDensity.current.fontScale >= STACK_FONT_SCALE
    GroupedSection(modifier) {
        row {
            Line(
                Mark.Arrow(PftSymbol.NORTH_EAST),
                R.string.money_expense,
                totals.expense,
                MoneySemantic.Expense,
                stacked,
            )
        }
        row {
            Line(
                Mark.Arrow(PftSymbol.SOUTH_WEST),
                R.string.money_income,
                totals.income,
                MoneySemantic.Income,
                stacked,
            )
        }
        row { Line(Mark.Equals, R.string.money_net, totals.net, MoneySemantic.Net, stacked) }
    }
}

/** What a line draws before its word. */
private sealed interface Mark {
    /** An arrow symbol. */
    data class Arrow(
        val symbol: PftSymbol,
    ) : Mark

    /** An equals sign: the net is what the two lines above come to. */
    data object Equals : Mark
}

/** One line: [mark], the word [label] and the figure, as a single node that speaks the figure. */
@Composable
private fun Line(
    mark: Mark,
    label: Int,
    amount: Paise,
    semantic: MoneySemantic,
    stacked: Boolean,
) {
    val spacing = PftTheme.spacing
    val spoken = spokenAmount(semantic, amount, PaiseFormat.format(amount))
    val frame =
        Modifier
            .fillMaxWidth()
            .heightIn(min = spacing.huge)
            .clearAndSetSemantics { contentDescription = spoken }
            .padding(horizontal = spacing.lg, vertical = spacing.sm)
    val word = @Composable { Word(mark, stringResource(label)) }
    if (stacked) {
        Column(frame, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            word()
            MoneyText(amount, semantic)
        }
    } else {
        Row(frame, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            word()
            MoneyText(amount, semantic)
        }
    }
}

/** The decorative mark and the word beside it. */
@Composable
private fun Word(
    mark: Mark,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm),
    ) {
        Box(Modifier.size(ARROW_SIZE), contentAlignment = Alignment.Center) {
            when (mark) {
                is Mark.Arrow ->
                    Icon(
                        painterResource(mark.symbol.res),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                Mark.Equals ->
                    Text(
                        "=",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                    )
            }
        }
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}
