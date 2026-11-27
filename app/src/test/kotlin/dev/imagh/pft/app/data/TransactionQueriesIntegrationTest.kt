package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.YearMonth

/**
 * The month list and its filters through the real stack (slice C C3a): real services write, the Room read
 * path reads, in-memory SQLite, zone `Asia/Kolkata`. Each test seeds so that a naive implementation would
 * return something else: a boundary instant, a tombstoned line that would match, lines that meet the
 * category and the tag filter only on different lines.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionQueriesIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)
    private val october = YearMonth.of(2026, 10)
    private val november = YearMonth.of(2026, 11)

    @After
    fun closeDatabase() = database.close()

    private suspend fun month(
        filter: TransactionFilter = TransactionFilter(),
        month: YearMonth = november,
    ) = kit.transactionQueries.month(month, kit.zone, filter)

    @Test
    fun `the month window includes 23-30 IST on the 31st and excludes 00-10 IST on the 1st`() =
        runSuspending {
            w.start()
            val lastNight = w.spend(ist(2026, 10, 31, 23, 30), w.item(rs(10))).id()
            val firstMorning = w.spend(ist(2026, 11, 1, 0, 10), w.item(rs(20))).id()

            month(month = october).entries.keys() shouldContainExactly listOf(lastNight)
            month(month = november).entries.keys() shouldContainExactly listOf(firstMorning)
        }

    @Test
    fun `the window is half open to the millisecond, start included and end excluded`() =
        runSuspending {
            w.start()
            val lastMillisOfOctober =
                w.spend(ist(2026, 10, 31, 23, 59, millis = 999).plusSeconds(59), w.item(rs(1))).id()
            val firstMillisOfNovember = w.spend(ist(2026, 11, 1, 0, 0), w.item(rs(2))).id()
            val firstMillisOfDecember = w.spend(ist(2026, 12, 1, 0, 0), w.item(rs(3))).id()

            month(month = october).entries.keys() shouldContainExactly listOf(lastMillisOfOctober)
            month(month = november).entries.keys() shouldContainExactly listOf(firstMillisOfNovember)
            month(month = YearMonth.of(2026, 12)).entries.keys() shouldContainExactly listOf(firstMillisOfDecember)
        }

    @Test
    fun `a date range narrows the month and the whole of the last day is included`() =
        runSuspending {
            w.start()
            w.spend(ist(2026, 11, 9, 23, 59), w.item(rs(1)))
            val first = w.spend(ist(2026, 11, 10, 0, 0), w.item(rs(2))).id()
            val last = w.spend(ist(2026, 11, 12, 23, 59), w.item(rs(3))).id()
            w.spend(ist(2026, 11, 13, 0, 0), w.item(rs(4)))

            val filter = TransactionFilter(from = LocalDate.of(2026, 11, 10), to = LocalDate.of(2026, 11, 12))

            month(filter).entries.keys() shouldContainExactly listOf(last, first)
        }

    @Test
    fun `an account filter is any-of and a transaction on another account is left out`() =
        runSuspending {
            w.start()
            val onBank = w.spend(ist(2026, 11, 3), w.item(rs(1)), account = w.bank).id()
            val onCard = w.spend(ist(2026, 11, 4), w.item(rs(2)), account = w.card).id()
            w.spend(ist(2026, 11, 5), w.item(rs(3)), account = w.savings)

            val page = month(TransactionFilter(accountIds = setOf(w.bank.meta.id, w.card.meta.id)))

            page.entries.keys() shouldContainExactly listOf(onCard, onBank)
            page.totals shouldBe Totals(expense = Paise(rs(3)), income = Paise.ZERO)
        }

    @Test
    fun `a parent category matches its children on any live line, once, with the matching total`() =
        runSuspending {
            w.start()
            val itemised =
                w.spend(
                    ist(2026, 11, 3),
                    w.item(rs(1_200), w.groceries),
                    w.item(rs(500), w.travel),
                    w.item(rs(300), w.dining),
                )
            w.spend(ist(2026, 11, 4), w.item(rs(900), w.travel))

            val groceriesOnly = month(TransactionFilter(categoryIds = setOf(w.groceries.meta.id)))
            val parent = month(TransactionFilter(categoryIds = setOf(w.food.meta.id)))

            groceriesOnly.entries.keys() shouldContainExactly listOf(itemised.id())
            groceriesOnly.totals.expense shouldBe Paise(rs(1_200))
            // The parent expands to Groceries and Dining: still one entry, 1,200 + 300.
            parent.entries.keys() shouldContainExactly listOf(itemised.id())
            parent.totals.expense shouldBe Paise(rs(1_500))
            // The entry carries all three live lines, not only the matching ones.
            (groceriesOnly.entries.single() as LedgerEntry.Single).lines.map { it.amount } shouldContainExactly
                listOf(Paise(rs(1_200)), Paise(rs(500)), Paise(rs(300)))
        }

    @Test
    fun `a Rs 2000 transaction with Rs 1200 of groceries comes back once with total 1200 (the plan's case)`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(1_200), w.groceries), w.item(rs(800), w.travel))

            val page = month(TransactionFilter(categoryIds = setOf(w.groceries.meta.id)))

            page.entries.keys() shouldContainExactly listOf(shop.id())
            (page.entries.single() as LedgerEntry.Single).transaction.amount shouldBe Paise(rs(2_000))
            page.totals shouldBe Totals(expense = Paise(rs(1_200)), income = Paise.ZERO)
        }

    @Test
    fun `a tombstoned line is neither matched nor shown nor totalled`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(1_200), w.dining), w.item(rs(800), w.groceries))
            // Edit away the Groceries line: the service tombstones it and Dining takes the whole amount.
            val kept = shop.lines.single { it.categoryId == w.dining.meta.id }
            kit.transactions.edit(
                shop.transaction.meta.id,
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(2_000)),
                    shop.transaction.occurredAt,
                    w.bank.meta.id,
                    lines = listOf(w.item(rs(2_000), w.dining).copy(id = kept.meta.id)),
                ),
            )

            month(TransactionFilter(categoryIds = setOf(w.groceries.meta.id))).entries shouldContainExactly emptyList()
            val dining = month(TransactionFilter(categoryIds = setOf(w.dining.meta.id)))
            dining.totals.expense shouldBe Paise(rs(2_000))
            val shown = dining.entries.single() as LedgerEntry.Single
            shown.lines.map { it.meta.id } shouldContainExactly listOf(kept.meta.id)
        }

    @Test
    fun `a tag filter is any-of and the entry carries each line's tags`() =
        runSuspending {
            w.start()
            val both = w.spend(ist(2026, 11, 3), w.item(rs(100), null, w.trip, w.gift))
            val giftOnly = w.spend(ist(2026, 11, 4), w.item(rs(200), null, w.gift))
            w.spend(ist(2026, 11, 5), w.item(rs(300)))

            val trip = month(TransactionFilter(tagIds = setOf(w.trip.meta.id)))
            val either = month(TransactionFilter(tagIds = setOf(w.trip.meta.id, w.gift.meta.id)))

            trip.entries.keys() shouldContainExactly listOf(both.id())
            val shown = trip.entries.single() as LedgerEntry.Single
            shown.lines.single().tagIds shouldBe setOf(w.trip.meta.id, w.gift.meta.id)
            either.entries.keys() shouldContainExactly listOf(giftOnly.id(), both.id())
        }

    @Test
    fun `category and tag must be met by the same line, not by two different lines`() =
        runSuspending {
            w.start()
            // Groceries on one line and the Trip tag on another: no single line has both.
            w.spend(ist(2026, 11, 3), w.item(rs(500), w.groceries), w.item(rs(500), w.travel, w.trip))
            val same = w.spend(ist(2026, 11, 4), w.item(rs(300), w.groceries, w.trip), w.item(rs(700), w.travel))

            val filter = TransactionFilter(categoryIds = setOf(w.groceries.meta.id), tagIds = setOf(w.trip.meta.id))
            val page = month(filter)

            page.entries.keys() shouldContainExactly listOf(same.id())
            page.totals.expense shouldBe Paise(rs(300))
        }

    @Test
    fun `amount bounds are inclusive and apply to the transaction, not its lines`() =
        runSuspending {
            w.start()
            val low = w.spend(ist(2026, 11, 3), w.item(rs(100)))
            val high = w.spend(ist(2026, 11, 4), w.item(rs(300), w.food), w.item(rs(200), w.travel))
            w.spend(ist(2026, 11, 5), w.item(rs(99)))
            w.spend(ist(2026, 11, 6), w.item(rs(501)))
            // Over the bound as a whole, though each of its lines is inside it: a per-line filter would include it.
            w.spend(ist(2026, 11, 7), w.item(rs(300)), w.item(rs(300)))

            val page = month(TransactionFilter(amountMin = Paise(rs(100)), amountMax = Paise(rs(500))))

            page.entries.keys() shouldContainExactly listOf(high.id(), low.id())
        }

    @Test
    fun `category, tag and account together each narrow, and the totals count only the matching lines`() =
        runSuspending {
            w.start()
            val groceriesOnBank =
                w.spend(
                    ist(2026, 11, 3),
                    w.item(rs(300), w.groceries, w.trip),
                    w.item(rs(200), w.travel),
                )
            val diningOnBank = w.spend(ist(2026, 11, 4), w.item(rs(150), w.dining, w.trip))
            w.spend(ist(2026, 11, 5), w.item(rs(300), w.groceries, w.trip), account = w.card) // wrong account
            w.spend(ist(2026, 11, 6), w.item(rs(300), w.groceries)) // no tag
            w.spend(ist(2026, 11, 7), w.item(rs(300), w.travel, w.trip)) // wrong category
            val filter =
                TransactionFilter(
                    accountIds = setOf(w.bank.meta.id),
                    categoryIds = setOf(w.food.meta.id),
                    tagIds = setOf(w.trip.meta.id),
                )

            val page = month(filter)

            page.entries.keys() shouldContainExactly listOf(diningOnBank.id(), groceriesOnBank.id())
            page.totals shouldBe Totals(expense = Paise(rs(450)), income = Paise.ZERO)
        }

    @Test
    fun `state and type filters each work and combine with AND across dimensions`() =
        runSuspending {
            w.start()
            val unverified = w.spend(ist(2026, 11, 3), w.item(rs(10)))
            kit.writer.update(kit.transactionRepository, unverified.transaction.meta.id) {
                it.copy(state = TxnState.UNVERIFIED)
            }
            val verified = w.spend(ist(2026, 11, 4), w.item(rs(20)))
            val salary = w.earn(rs(5_000), ist(2026, 11, 5))

            month(TransactionFilter(states = setOf(TxnState.UNVERIFIED))).entries.keys() shouldContainExactly
                listOf(unverified.id())
            month(TransactionFilter(types = setOf(TransactionType.INCOME))).entries.keys() shouldContainExactly
                listOf(salary.id())
            month(TransactionFilter(states = setOf(TxnState.VERIFIED), types = setOf(TransactionType.EXPENSE)))
                .entries
                .keys() shouldContainExactly listOf(verified.id())
        }
}
