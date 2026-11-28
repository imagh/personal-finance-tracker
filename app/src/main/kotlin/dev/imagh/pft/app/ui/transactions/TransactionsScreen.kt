package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.CollapsingHeaderScaffold
import dev.imagh.pft.app.ui.common.DayGroup
import dev.imagh.pft.app.ui.common.EmptyState
import dev.imagh.pft.app.ui.common.HeaderPeriod
import dev.imagh.pft.app.ui.common.HeaderStyle
import dev.imagh.pft.app.ui.common.LocalTabBarClearance
import dev.imagh.pft.app.ui.common.periodSwipe
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.transaction.TransactionType
import java.time.Clock
import java.time.YearMonth

/**
 * The Transactions tab's root (slice C C4a), drawn from [state] alone: a `Standard` "Transactions" header
 * over a month strip, the totals banner for the selected month, and one [DayGroup] per day with entries,
 * newest day first, each with its net total. A month with no entries says so under the banner.
 *
 * - **Loading.** Until [TransactionsUiState.loaded] only the strip shows, so neither the previous month's
 *   rows and totals nor the empty message appear under the new month.
 * - **Swipe.** A sideways swipe anywhere on the list steps the month (toward the end of the line: the
 *   previous one), the same gesture as the dashboard header; the strip scrolls on its own drag.
 * - **Rows** are not tappable and the tab has no add button yet: the form and the detail screen arrive
 *   with their own changes (C4b, C5a), and a control with nowhere to go is never drawn.
 * - **Tab bar.** `LocalTabBarClearance` adds the bar's height below the last row, so the bar covers
 *   nothing.
 *
 * @param state what to show.
 * @param onSelectMonth called with the month the strip's user tapped.
 * @param onPreviousMonth called for a swipe toward the end of the line.
 * @param onNextMonth called for a swipe toward the start of the line.
 */
@Composable
internal fun TransactionsScreen(
    state: TransactionsUiState,
    onSelectMonth: (YearMonth) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    val barClearance = LocalTabBarClearance.current
    val texts = rowTexts()
    val months = remember(state.span) { monthsIn(state.span) }
    val swipe = remember(onPreviousMonth, onNextMonth) { HeaderPeriod(onPreviousMonth, onNextMonth) }
    CollapsingHeaderScaffold(
        title = stringResource(R.string.transactions_title),
        modifier = modifier.periodSwipe(swipe),
        style = HeaderStyle.Standard,
    ) {
        item(key = STRIP_KEY) { MonthStrip(months, state.month, onSelectMonth) }
        if (state.loaded) {
            item(key = BANNER_KEY) { TotalsBanner(state.totals, Modifier.padding(top = spacing.sm)) }
            if (state.days.isEmpty()) {
                item(key = EMPTY_KEY) { EmptyState(stringResource(R.string.transactions_empty)) }
            }
            items(state.days, key = { it.day.toString() }) { day ->
                val rows =
                    remember(day, state.names, texts) {
                        day.entries.map { TransactionRowMapper.toModel(it, state.names, texts) }
                    }
                DayGroup(day.day, state.today, rows, total = day.net)
            }
        }
        // Room below the last row for the floating tab bar (zero, and so no item, wherever it is not drawn).
        if (barClearance > 0.dp) item(key = CLEARANCE_KEY) { Spacer(Modifier.height(barClearance)) }
    }
}

private const val STRIP_KEY = "transactions-strip"
private const val BANNER_KEY = "transactions-banner"
private const val EMPTY_KEY = "transactions-empty"
private const val CLEARANCE_KEY = "transactions-clearance"

/** The row words in the current locale, rebuilt when it changes. */
@Composable
private fun rowTexts(): RowTexts {
    val resources = LocalContext.current.resources
    val labels =
        mapOf(
            TransactionType.EXPENSE to stringResource(R.string.money_expense),
            TransactionType.INCOME to stringResource(R.string.money_income),
            TransactionType.TRANSFER to stringResource(R.string.money_transfer),
        )
    val uncategorised = stringResource(R.string.transaction_uncategorised)
    val unknown = stringResource(R.string.transaction_unknown_account)
    return remember(resources, labels, uncategorised, unknown) {
        RowTexts(
            items = { resources.getString(R.string.transaction_items, it) },
            typeLabel = { labels.getValue(it) },
            uncategorised = uncategorised,
            unknown = unknown,
        )
    }
}

/**
 * The Transactions tab wired to its [TransactionsViewModel]: the only place that touches the ViewModel.
 * `LifecycleResumeEffect` reads the month again whenever the screen resumes (the same on-demand read as the
 * lists in slice B), which is how an entry written on another screen shows on return; the first resume after the
 * ViewModel is created is covered by its creation read ([TransactionsViewModel.onResume]).
 *
 * @param services the open object graph the ViewModel is built over.
 * @param clock gives the clock for each read ("now" and the user's zone).
 */
@Composable
internal fun TransactionsRoute(
    services: AppServices,
    clock: () -> Clock,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { TransactionsViewModel.factory(services, clock) }
    val viewModel: TransactionsViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose {}
    }
    TransactionsScreen(state, viewModel::selectMonth, viewModel::previousMonth, viewModel::nextMonth, modifier)
}
