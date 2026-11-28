package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.money.Paise
import java.time.LocalDate

/**
 * One day of transactions (Cobalt plan D3): a header (the day, and optionally its net total) over
 * one [GroupedSection] island of [TransactionRow]s with hairline dividers between them.
 *
 * The header reads "Today" or "Yesterday" relative to [today], else the date in the device's
 * locale. [today] is passed in so the caller owns the clock and the zone (a screen reads them from
 * its ViewModel's injected `Clock`); this block never reads the time. The header is a heading.
 *
 * The optional [total] is a [MoneyText] with [MoneySemantic.Net]: signed, spoken as "Net", and
 * neutral whatever its sign.
 *
 * Named consumers: Home's recent transactions, the Transactions tab, search results.
 *
 * @param day the day the rows belong to.
 * @param today the current day, in the user's zone.
 * @param rows the day's rows, in display order; their own `onClick` makes each tappable.
 * @param total the day's net figure (income minus expense), or null to show none.
 * @throws IllegalArgumentException if [rows] is empty: a header over nothing is a caller bug.
 */
@Composable
fun DayGroup(
    day: LocalDate,
    today: LocalDate,
    rows: List<TransactionRowModel>,
    modifier: Modifier = Modifier,
    total: Paise? = null,
) {
    require(rows.isNotEmpty()) { "a day group needs at least one row" }
    val spacing = PftTheme.spacing
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = spacing.gutter + spacing.xs).padding(top = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                dayLabel(day, today),
                Modifier.weight(1f).semantics { heading() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
            if (total != null) MoneyText(total, MoneySemantic.Net)
        }
        GroupedSection(Modifier.padding(top = spacing.xs)) {
            rows.forEach { model -> row { TransactionRow(model) } }
        }
    }
}

/** "Today", "Yesterday" or [day] in the locale's own short date format. */
@Composable
private fun dayLabel(
    day: LocalDate,
    today: LocalDate,
): String {
    val formatter = rememberDateFormatter()
    return when (day) {
        today -> stringResource(R.string.day_today)
        today.minusDays(1) -> stringResource(R.string.day_yesterday)
        else -> day.format(formatter)
    }
}
