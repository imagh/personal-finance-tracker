package dev.imagh.pft.app.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.MonthSpan
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.query.TransactionQueries
import dev.imagh.pft.ledger.tag.TagService
import dev.imagh.pft.money.Paise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeParseException

/** The years the app reads months in: core's filter bound, so a month's epoch milliseconds cannot overflow. */
private const val MIN_YEAR = 1
private const val MAX_YEAR = 9999
private val YEARS = MIN_YEAR..MAX_YEAR

/**
 * One day's entries on the Transactions tab.
 *
 * @property day the calendar day in the clock's zone.
 * @property entries the day's entries in core's order (newest first).
 * @property net the day's income minus expense (a transfer counts in neither); negative when more went out
 *   than came in, shown with [dev.imagh.pft.app.ui.common.MoneySemantic.Net].
 */
internal data class DayEntries(
    val day: LocalDate,
    val entries: List<LedgerEntry>,
    val net: Paise,
)

/**
 * Everything the Transactions tab shows.
 *
 * @property month the selected month.
 * @property span the months the strip lists: the span of the live entries and this month, widened to include
 *   [month] (a month past today is allowed).
 * @property today the current day in the clock's zone, for "Today" and "Yesterday".
 * @property days the month's days with entries, newest first (core's order); empty for a month with no entries.
 * @property totals the month's expense and income, for the banner; the net is derived.
 * @property names the account, category and tag names the rows use.
 * @property loaded false until [month]'s entries have been read, so neither the rows nor "No transactions"
 *   show for the wrong month.
 */
internal data class TransactionsUiState(
    val month: YearMonth,
    val span: MonthSpan,
    val today: LocalDate,
    val days: List<DayEntries> = emptyList(),
    val totals: Totals = Totals(Paise.ZERO, Paise.ZERO),
    val names: NameLookups = NameLookups(emptyMap(), emptyMap(), emptyMap()),
    val loaded: Boolean = false,
)

/**
 * The Transactions tab's state and month selection (slice C D5, D7): which month is shown, its entries
 * grouped by day with day totals, the banner totals and the months the strip lists. It reads through
 * core only (`TransactionQueries`, and the lists of the account, category and tag services for names),
 * and derives nothing a core read already knows: the banner totals are the month page's totals, a day's
 * net is core's `Totals.of` over that day's entries.
 *
 * - **Read on demand (D2).** The tab reads on construction, after a month change, and when asked
 *   ([reload]; the screen calls [onResume], which skips only the first resume after construction), so nothing
 *   observes the database. Reads run one after another
 *   (a [Mutex]); each reads the month selected **when it runs**, so a quick second tap is never shown as
 *   the first. A read that throws (a stored row core refuses) is not caught: it fails loudly, like
 *   `AccountsViewModel`, rather than show made-up money.
 * - **Zone and "now".** [clock] is asked for on every read, so a device zone change while the app runs
 *   shows up on the next read; its zone chooses today, this month, which month an instant is in and which day.
 * - **The chosen month** lives in [savedState], so it survives rotation and a tab switch (the tab's
 *   back stack entry, and so this ViewModel, is kept). Until the user chooses one the tab shows this
 *   month, which rolls over with the calendar. A stored value that does not read as a month in years 1 to
 *   9999 is ignored (fail closed to this month).
 * - **Not logged.** Nothing about the entries (names, amounts, counts) is logged or shown in an error.
 *
 * @param savedState where the chosen month is kept.
 * @param queries the month list, totals and month span.
 * @param accounts the accounts (names, archived included).
 * @param categories the categories (names, icons, colours, archived included).
 * @param tags the tags (names, archived included).
 * @param clock gives the clock for each read: its instant is "now" and its zone is the user's.
 */
internal class TransactionsViewModel(
    private val savedState: SavedStateHandle,
    private val queries: TransactionQueries,
    private val accounts: AccountService,
    private val categories: CategoryService,
    private val tags: TagService,
    private val clock: () -> Clock,
) : ViewModel() {
    private val serial = Mutex()

    /** True until the first [onResume]: the read made on construction stands in for it. */
    private var creationReadCoversResume = true
    private val mutableState: MutableStateFlow<TransactionsUiState>

    /** The screen's state; [TransactionsUiState.loaded] is false until the selected month has been read. */
    val state: StateFlow<TransactionsUiState>

    init {
        val first = clock()
        val today = LocalDate.now(first)
        val month = chosenMonth() ?: YearMonth.from(today)
        mutableState = MutableStateFlow(TransactionsUiState(month, MonthSpan(month, month), today))
        state = mutableState.asStateFlow()
        reload()
    }

    /** Reads the selected month again (the screen calls this when it resumes, after an edit elsewhere). */
    fun reload() {
        viewModelScope.launch { serial.withLock { refresh() } }
    }

    /**
     * The screen resumed: reads the selected month again, **except for the first resume after creation**, which the
     * read made on construction already covers (a second one would queue behind it and read the same rows). Every
     * later resume reads, so a ViewModel kept across a tab switch shows what was written meanwhile. Main thread only.
     */
    fun onResume() {
        if (creationReadCoversResume) creationReadCoversResume = false else reload()
    }

    /**
     * Shows [month] and keeps it as the chosen one. A month outside years 1 to 9999 is ignored. The old rows are
     * hidden at once ([TransactionsUiState.loaded] false) and the new ones read.
     */
    fun selectMonth(month: YearMonth) {
        if (month.year !in YEARS) return
        savedState[KEY_MONTH] = month.toString()
        mutableState.update { it.copy(month = month, span = it.span.including(month), loaded = false) }
        reload()
    }

    /** Shows the month before the selected one. */
    fun previousMonth() = selectMonth(mutableState.value.month.minusMonths(1))

    /** Shows the month after the selected one; past today is allowed (an empty month). */
    fun nextMonth() = selectMonth(mutableState.value.month.plusMonths(1))

    /** The month the user chose, or null when none was chosen or what is stored is not a month. */
    private fun chosenMonth(): YearMonth? {
        val stored = savedState.get<Any?>(KEY_MONTH) as? String
        val month =
            stored?.let {
                try {
                    YearMonth.parse(it)
                } catch (_: DateTimeParseException) {
                    // Stored text is untrusted like any input: not a month means this month, never a crash.
                    null
                }
            }
        return month?.takeIf { it.year in YEARS }
    }

    private suspend fun refresh() {
        val now = clock()
        val zone = now.zone
        val today = LocalDate.now(now)
        val thisMonth = YearMonth.from(today)
        val month = chosenMonth() ?: thisMonth
        val page = queries.month(month, zone)
        val span = MonthSpan.strip(queries.entryMonthSpan(zone), thisMonth).including(month)
        val names = lookups()
        mutableState.value =
            TransactionsUiState(
                month = month,
                span = span,
                today = today,
                days = daysOf(page.entries, zone),
                totals = page.totals,
                names = names,
                loaded = true,
            )
    }

    /** The names of every account, category and tag a row can point at: archived ones too, deleted ones never. */
    private suspend fun lookups(): NameLookups {
        val accountLists = accounts.lists()
        val tagLists = tags.lists()
        val categoryLists = CategoryKind.entries.map { categories.lists(it) }
        return NameLookups(
            accounts = (accountLists.active + accountLists.archived).associate { it.meta.id to it.name },
            categories = categoryLists.flatMap { it.active + it.archived }.associateBy { it.meta.id },
            tags = (tagLists.active + tagLists.archived).associate { it.meta.id to it.name },
        )
    }

    /**
     * [entries] (already in core's display order, newest first) grouped by the day they happened on in [zone]; days
     * come in the order their first entry does, so they are newest first too. A day's net is core's own
     * [Totals.of] over that day's entries, with no filter (a month is read whole, so every day is complete).
     */
    private fun daysOf(
        entries: List<LedgerEntry>,
        zone: ZoneId,
    ): List<DayEntries> =
        entries
            .groupBy { it.occurredAt().atZone(zone).toLocalDate() }
            .map { (day, ofDay) ->
                DayEntries(day, ofDay, Totals.of(ofDay, TransactionFilter(), emptySet()).net)
            }

    private fun LedgerEntry.occurredAt(): Instant =
        when (this) {
            is LedgerEntry.Single -> transaction.occurredAt
            is LedgerEntry.Transfer -> outgoing.occurredAt
        }

    companion object {
        /** The saved-state key of the chosen month, as ISO text (`2026-11`). */
        const val KEY_MONTH = "month"

        /** A factory that builds the ViewModel over [services] and [clock] (there is no DI framework). */
        fun factory(
            services: AppServices,
            clock: () -> Clock,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    TransactionsViewModel(
                        createSavedStateHandle(),
                        services.transactionQueries,
                        services.accounts,
                        services.categories,
                        services.tags,
                        clock,
                    )
                }
            }
    }
}
