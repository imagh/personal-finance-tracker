package dev.imagh.pft.app.ui.transactions

import android.os.Looper
import androidx.lifecycle.SavedStateHandle
import dev.imagh.pft.app.data.QueryWorld
import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.MonthPage
import dev.imagh.pft.ledger.query.MonthSpan
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.query.TransactionQueries
import dev.imagh.pft.ledger.query.TransactionSort
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/**
 * [TransactionsViewModel] over real in-memory Room through the production wiring (slice C C4a): which
 * month it shows (in the clock's zone), what it loads for it, the totals and day totals it derives from
 * core's reads, the month span the strip covers, and that the chosen month survives a recreated
 * ViewModel. The ViewModel runs on the main dispatcher, so tests wait with [awaitMain]. "Now" is
 * 2026-11-15 10:00 in `Asia/Kolkata`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionsViewModelTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val world = QueryWorld(kit)
    private val handle = SavedStateHandle()
    private val now = Clock.fixed(ist(2026, 11, 15, 10), RoomTestKit.ZONE)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun viewModel(
        saved: SavedStateHandle = handle,
        clock: Clock = now,
    ): TransactionsViewModel {
        val vm = TransactionsViewModel(saved, kit.transactionQueries, kit.accounts, kit.categories, kit.tags) { clock }
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun TransactionsViewModel.await(condition: (TransactionsUiState) -> Boolean) =
        awaitMain {
            condition(state.value)
        }

    private fun TransactionsViewModel.select(month: YearMonth) {
        selectMonth(month)
        await { it.loaded && it.month == month }
    }

    private fun month(
        year: Int,
        month: Int,
    ) = YearMonth.of(year, month)

    // --- Which month

    @Test
    fun `it opens on this month in the clock's zone`() {
        blocking { world.start() }

        val vm = viewModel()

        vm.state.value.month shouldBe month(2026, 11)
        vm.state.value.today shouldBe LocalDate.of(2026, 11, 15)
    }

    @Test
    fun `at 23 30 on the last day of the month in India it is still that month, though UTC has moved on`() {
        blocking { world.start() }
        val lateOnTheThirtieth = Clock.fixed(ist(2026, 11, 30, 23, 30), RoomTestKit.ZONE)

        val vm = viewModel(clock = lateOnTheThirtieth)

        vm.state.value.month shouldBe month(2026, 11)
        vm.state.value.today shouldBe LocalDate.of(2026, 11, 30)
    }

    @Test
    fun `an entry just after midnight on the first of the month belongs to that month, not the one before`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 1, 0, 30), world.item(rs(10)))
            world.spend(ist(2026, 10, 31, 23, 30), world.item(rs(20)))
        }

        val vm = viewModel()

        vm.state.value.days
            .flatMap { it.entries }
            .size shouldBe 1
        vm.state.value.totals shouldBe Totals(Paise(rs(10)), Paise.ZERO)
    }

    @Test
    fun `selecting a month loads that month and previous and next step it`() {
        blocking {
            world.start()
            world.spend(ist(2026, 10, 20), world.item(rs(300)))
            world.spend(ist(2026, 11, 5), world.item(rs(100)))
        }
        val vm = viewModel()
        vm.state.value.totals.expense shouldBe Paise(rs(100))

        vm.previousMonth()
        vm.await { it.loaded && it.month == month(2026, 10) }
        vm.state.value.totals.expense shouldBe Paise(rs(300))

        vm.nextMonth()
        vm.await { it.loaded && it.month == month(2026, 11) }
        vm.state.value.totals.expense shouldBe Paise(rs(100))

        vm.select(month(2026, 10))
        vm.state.value.totals.expense shouldBe Paise(rs(300))
    }

    @Test
    fun `next is allowed past today, shows an empty month and the strip grows to include it`() {
        blocking { world.start() }
        val vm = viewModel()

        vm.nextMonth()
        vm.await { it.loaded && it.month == month(2026, 12) }

        vm.state.value.days shouldBe emptyList()
        vm.state.value.span shouldBe MonthSpan(month(2026, 11), month(2026, 12))
    }

    @Test
    fun `while a month loads the screen is told it has not loaded, so old rows never sit under the new month`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 5), world.item(rs(100)))
        }
        val vm = viewModel()

        vm.selectMonth(month(2026, 10))

        vm.state.value.month shouldBe month(2026, 10)
        vm.state.value.loaded shouldBe false
        vm.await { it.loaded }
        vm.state.value.days shouldBe emptyList()
    }

    @Test
    fun `selecting a month past the strip widens the strip at once, before its entries are read`() {
        blocking { world.start() }
        val vm = viewModel()

        vm.selectMonth(month(2027, 3))

        vm.state.value.loaded shouldBe false
        vm.state.value.span shouldBe MonthSpan(month(2026, 11), month(2027, 3))
    }

    @Test
    fun `the chosen month survives a recreated ViewModel built over the same saved state`() {
        blocking { world.start() }
        val first = viewModel()
        first.select(month(2026, 8))

        val second = viewModel()

        second.state.value.month shouldBe month(2026, 8)
    }

    @Test
    fun `a saved month that does not read as a month falls back to this month`() {
        blocking { world.start() }
        val tampered = SavedStateHandle(mapOf(TransactionsViewModel.KEY_MONTH to "not-a-month"))

        viewModel(tampered).state.value.month shouldBe month(2026, 11)
    }

    @Test
    fun `a saved month of the wrong type falls back to this month`() {
        blocking { world.start() }
        val tampered = SavedStateHandle(mapOf(TransactionsViewModel.KEY_MONTH to 202611))

        viewModel(tampered).state.value.month shouldBe month(2026, 11)
    }

    @Test
    fun `a saved month in a year core cannot read falls back to this month`() {
        blocking { world.start() }
        val tampered = SavedStateHandle(mapOf(TransactionsViewModel.KEY_MONTH to "+99999-01"))

        viewModel(tampered).state.value.month shouldBe month(2026, 11)
    }

    @Test
    fun `selecting a month beyond year 9999 or before year 1 is ignored and nothing is stored`() {
        blocking { world.start() }
        val vm = viewModel()

        vm.selectMonth(YearMonth.of(10_000, 1))
        vm.selectMonth(YearMonth.of(0, 12))

        vm.state.value.month shouldBe month(2026, 11)
        vm.state.value.loaded shouldBe true
        handle.get<String>(TransactionsViewModel.KEY_MONTH) shouldBe null
    }

    @Test
    fun `stepping next from December 9999 stays on that month`() {
        blocking { world.start() }
        val vm = viewModel()
        vm.select(YearMonth.of(9999, 12))

        vm.nextMonth()

        vm.state.value.month shouldBe YearMonth.of(9999, 12)
    }

    // --- The strip's span

    @Test
    fun `with no transactions the strip is just this month`() {
        blocking { world.start() }

        viewModel().state.value.span shouldBe MonthSpan(month(2026, 11), month(2026, 11))
    }

    @Test
    fun `the strip runs from the first month with an entry to this month when all entries are in the past`() {
        blocking {
            world.start()
            world.spend(ist(2026, 7, 9), world.item(rs(10)))
            world.spend(ist(2026, 9, 9), world.item(rs(10)))
        }

        viewModel().state.value.span shouldBe MonthSpan(month(2026, 7), month(2026, 11))
    }

    @Test
    fun `the strip runs to the last month with an entry when one is in the future`() {
        blocking {
            world.start()
            world.spend(ist(2026, 9, 9), world.item(rs(10)))
            world.spend(ist(2027, 2, 9), world.item(rs(10)))
        }

        viewModel().state.value.span shouldBe MonthSpan(month(2026, 9), month(2027, 2))
    }

    // --- The totals

    @Test
    fun `the banner totals equal the seeded sums and the net is income minus expense`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 3), world.item(rs(1_200), world.groceries), world.item(rs(800), world.travel))
            world.spend(ist(2026, 11, 4), world.item(rs(50)))
            world.earn(rs(30_000), ist(2026, 11, 5))
        }

        val totals = viewModel().state.value.totals

        totals shouldBe Totals(expense = Paise(rs(2_050)), income = Paise(rs(30_000)))
        totals.net shouldBe Paise(rs(27_950))
    }

    @Test
    fun `the net is negative when more went out than came in`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 3), world.item(rs(500)))
            world.earn(rs(200), ist(2026, 11, 5))
        }

        viewModel()
            .state.value.totals.net shouldBe Paise(-rs(300))
    }

    @Test
    fun `a transfer is in a day's entries but in no total`() {
        blocking {
            world.start()
            kit.transfers.create(
                TransferDraft(world.bank.meta.id, world.savings.meta.id, Paise(rs(5_000)), ist(2026, 11, 4)),
            )
            world.spend(ist(2026, 11, 4, 9), world.item(rs(100)))
        }

        val state = viewModel().state.value

        state.days
            .single()
            .entries.size shouldBe 2
        state.totals shouldBe Totals(Paise(rs(100)), Paise.ZERO)
        state.days.single().net shouldBe Paise(-rs(100))
    }

    // --- The days

    @Test
    fun `entries are grouped by their day in the clock's zone, newest day first, with each day's net`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 3, 9), world.item(rs(100)))
            world.spend(ist(2026, 11, 3, 23, 50), world.item(rs(40)))
            world.earn(rs(1_000), ist(2026, 11, 3, 15))
            world.spend(ist(2026, 11, 12, 0, 10), world.item(rs(70)))
            world.earn(rs(25), ist(2026, 11, 8))
        }

        val days = viewModel().state.value.days

        days.map { it.day } shouldContainExactly
            listOf(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 3))
        days.map { it.net } shouldContainExactly listOf(Paise(-rs(70)), Paise(rs(25)), Paise(rs(860)))
        days.last().entries.size shouldBe 3
    }

    @Test
    fun `the days' nets add up to the banner net`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 3), world.item(rs(100)))
            world.earn(rs(1_000), ist(2026, 11, 3, 15))
            world.spend(ist(2026, 11, 9), world.item(rs(70)))
        }

        val state = viewModel().state.value

        state.days.fold(Paise.ZERO) { sum, day -> sum + day.net } shouldBe state.totals.net
    }

    @Test
    fun `within a day the entries keep core's newest first order`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 3, 8), world.item(rs(1)))
            world.spend(ist(2026, 11, 3, 20), world.item(rs(2)))
            world.spend(ist(2026, 11, 3, 14), world.item(rs(3)))
        }

        val amounts =
            viewModel().state.value.days.single().entries.map {
                (it as LedgerEntry.Single).transaction.amount
            }

        amounts shouldContainExactly listOf(Paise(rs(2)), Paise(rs(3)), Paise(rs(1)))
    }

    // --- The names and reloading

    @Test
    fun `the names include archived accounts, categories and tags that a transaction still points at`() {
        blocking {
            world.start()
            world.spend(ist(2026, 11, 3), world.item(rs(100), world.groceries, world.trip))
            kit.accounts.archive(world.bank.meta.id)
            kit.categories.archive(world.groceries.meta.id)
            kit.tags.archive(world.trip.meta.id)
        }

        val names = viewModel().state.value.names

        names.accounts[world.bank.meta.id] shouldBe "HDFC"
        names.categories[world.groceries.meta.id]?.name shouldBe "Groceries"
        names.tags[world.trip.meta.id] shouldBe "Trip"
    }

    @Test
    fun `reload picks up a transaction written elsewhere`() {
        blocking { world.start() }
        val vm = viewModel()
        vm.state.value.days shouldBe emptyList()

        blocking { world.spend(ist(2026, 11, 3), world.item(rs(100))) }
        vm.reload()
        vm.await { it.days.isNotEmpty() }

        vm.state.value.totals.expense shouldBe Paise(rs(100))
    }

    // --- Resume

    /** Counts the month reads that reach core, so a test can tell one read from two without waiting. */
    private class CountingQueries(
        private val real: TransactionQueries,
    ) : TransactionQueries by real {
        var monthReads = 0
            private set

        override suspend fun month(
            month: YearMonth,
            zone: ZoneId,
            filter: TransactionFilter,
            sort: TransactionSort,
        ): MonthPage {
            monthReads++
            return real.month(month, zone, filter, sort)
        }
    }

    @Test
    fun `the first resume after creation does not read again, and each later resume does`() {
        blocking { world.start() }
        val queries = CountingQueries(kit.transactionQueries)
        val vm = TransactionsViewModel(handle, queries, kit.accounts, kit.categories, kit.tags) { now }

        vm.onResume()
        awaitMain { vm.state.value.loaded }
        // A read queued behind the first one starts when the main looper next runs, and is counted on entry.
        shadowOf(Looper.getMainLooper()).idle()

        queries.monthReads shouldBe 1

        vm.onResume()
        awaitMain { queries.monthReads == 2 && vm.state.value.loaded }

        queries.monthReads shouldBe 2
    }
}
