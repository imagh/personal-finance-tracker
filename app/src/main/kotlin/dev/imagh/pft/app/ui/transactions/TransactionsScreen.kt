package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.common.periodSwipe
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.query.LedgerEntry
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
 * - **Rows and add (C4b).** With [onOpen] a row is a button that opens that entry's edit form (the detail screen
 *   replaces it in C5a); with [add] the "+" floating button opens a new transaction. Either left null draws
 *   nothing for it, because a control with nowhere to go is never drawn.
 * - **Tab bar.** `LocalTabBarClearance` lifts the add button and adds the bar's height below the last row (and
 *   room for the button), so neither the bar nor the button covers a row.
 *
 * @param state what to show.
 * @param onSelectMonth called with the month the strip's user tapped.
 * @param onPreviousMonth called for a swipe toward the end of the line.
 * @param onNextMonth called for a swipe toward the start of the line.
 * @param add the add action (its label is the spoken label of the "+"), or null for no button.
 * @param onOpen called with the entry whose row was tapped, or null for rows that are not tappable.
 * @param snackbarHost the host the route shows its messages on.
 */
@Composable
internal fun TransactionsScreen(
    state: TransactionsUiState,
    onSelectMonth: (YearMonth) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
    add: RowAction? = null,
    onOpen: ((LedgerEntry) -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
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
        floatingActionButton = {
            if (add != null) {
                FloatingActionButton(onClick = add.onClick, modifier = Modifier.padding(bottom = barClearance)) {
                    Icon(painterResource(PftSymbol.ADD.res), contentDescription = add.label)
                }
            }
        },
        snackbarHost = snackbarHost,
    ) {
        item(key = STRIP_KEY) { MonthStrip(months, state.month, onSelectMonth) }
        if (state.loaded) {
            item(key = BANNER_KEY) { TotalsBanner(state.totals, Modifier.padding(top = spacing.sm)) }
            if (state.days.isEmpty()) {
                item(key = EMPTY_KEY) { EmptyState(stringResource(R.string.transactions_empty)) }
            }
            items(state.days, key = { it.day.toString() }) { day ->
                val rows =
                    remember(day, state.names, texts, onOpen) {
                        day.entries.map { entry ->
                            TransactionRowMapper.toModel(entry, state.names, texts, onOpen?.let { { it(entry) } })
                        }
                    }
                DayGroup(day.day, state.today, rows, total = day.net)
            }
        }
        // Room below the last row for the add button and the floating tab bar (zero, and so no item, where neither is).
        val roomBelow = (if (add != null) spacing.huge + spacing.huge else 0.dp) + barClearance
        if (roomBelow > 0.dp) item(key = CLEARANCE_KEY) { Spacer(Modifier.height(roomBelow)) }
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
 * Where the Transactions tab can go.
 *
 * @property onAdd opens a new transaction.
 * @property onOpen opens the form for a tapped entry.
 */
internal class TransactionsNavigation(
    val onAdd: () -> Unit,
    val onOpen: (LedgerEntry) -> Unit,
)

/**
 * The Transactions tab wired to its [TransactionsViewModel]: the only place that touches the ViewModel.
 * `LifecycleResumeEffect` reads the month again whenever the screen resumes (the same on-demand read as the
 * lists in slice B), which is how an entry written on another screen shows on return; the first resume after the
 * ViewModel is created is covered by its creation read ([TransactionsViewModel.onResume]).
 *
 * - **Add and open (C4b).** The "+" calls [TransactionsNavigation.onAdd]; a row tap calls
 *   [TransactionsNavigation.onOpen] with its entry.
 * - **Not found.** When the edit form was opened for a transaction that no longer exists, the caller sets
 *   [notFound] and this shows "That transaction no longer exists", then calls [onNotFoundShown].
 *
 * @param services the open object graph the ViewModel is built over.
 * @param clock gives the clock for each read ("now" and the user's zone).
 * @param navigation where the "+" and a row tap go.
 * @param notFound true while a "no longer exists" message is waiting to be shown.
 * @param onNotFoundShown the message was shown; the caller clears [notFound].
 */
@Composable
internal fun TransactionsRoute(
    services: AppServices,
    clock: () -> Clock,
    navigation: TransactionsNavigation,
    notFound: Boolean,
    onNotFoundShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { TransactionsViewModel.factory(services, clock) }
    val viewModel: TransactionsViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbars = remember { SnackbarHostState() }
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose {}
    }
    val notFoundText = stringResource(R.string.transactions_not_found)
    LaunchedEffect(notFound) {
        if (notFound) {
            snackbars.showSnackbar(notFoundText)
            onNotFoundShown()
        }
    }
    val latest by rememberUpdatedState(navigation)
    val add = RowAction(stringResource(R.string.transactions_add)) { latest.onAdd() }
    val open = remember { { entry: LedgerEntry -> latest.onOpen(entry) } }
    TransactionsScreen(
        state,
        viewModel::selectMonth,
        viewModel::previousMonth,
        viewModel::nextMonth,
        modifier,
        add = add,
        onOpen = open,
        snackbarHost = { SnackbarHost(snackbars) },
    )
}
