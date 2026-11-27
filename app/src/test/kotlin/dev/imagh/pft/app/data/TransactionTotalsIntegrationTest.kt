package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.YearMonth

/**
 * Totals, transfers and tombstones through the real stack (slice C C3a, PRD L-5): a transfer must never
 * inflate a total, and a deleted transaction, whose lines stay live, must vanish from every read.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionTotalsIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)
    private val november = YearMonth.of(2026, 11)

    @After
    fun closeDatabase() = database.close()

    private suspend fun month(filter: TransactionFilter = TransactionFilter()) =
        kit.transactionQueries.month(november, kit.zone, filter)

    private suspend fun transfer(
        paise: Long,
        at: java.time.Instant,
    ) = kit.transfers.create(
        TransferDraft(w.bank.meta.id, w.savings.meta.id, Paise(paise), at, tagIds = setOf(w.trip.meta.id)),
    )

    @Test
    fun `totals are expense and income by type over whole amounts, itemised or not`() =
        runSuspending {
            w.start()
            w.spend(ist(2026, 11, 3), w.item(rs(1_200), w.groceries), w.item(rs(800), w.travel))
            w.spend(ist(2026, 11, 4), w.item(rs(50)))
            w.earn(rs(30_000), ist(2026, 11, 5))

            val totals = month().totals

            totals shouldBe Totals(expense = Paise(rs(2_050)), income = Paise(rs(30_000)))
            totals.net shouldBe Paise(rs(27_950))
        }

    @Test
    fun `a transfer is one entry and is in neither total, however large`() =
        runSuspending {
            w.start()
            val groceries = w.spend(ist(2026, 11, 3), w.item(rs(100), w.groceries)).id()
            val moved = transfer(rs(5_000), ist(2026, 11, 4))

            val page = month()

            page.entries.keys() shouldContainExactly listOf(moved.groupId, groceries)
            val shown = page.entries.first() as LedgerEntry.Transfer
            shown.outgoing.meta.id shouldBe moved.out.transaction.meta.id
            shown.incoming.meta.id shouldBe moved.into.transaction.meta.id
            page.totals shouldBe Totals(expense = Paise(rs(100)), income = Paise.ZERO)
        }

    @Test
    fun `with an account filter the leg on that account is its own entry and still not in the totals`() =
        runSuspending {
            w.start()
            val moved = transfer(rs(5_000), ist(2026, 11, 4))
            w.spend(ist(2026, 11, 3), w.item(rs(100)), account = w.bank)

            val onBank = month(TransactionFilter(accountIds = setOf(w.bank.meta.id)))
            val onSavings = month(TransactionFilter(accountIds = setOf(w.savings.meta.id)))

            val leg = onBank.entries.first() as LedgerEntry.Single
            leg.transaction.meta.id shouldBe moved.out.transaction.meta.id
            onBank.totals shouldBe Totals(expense = Paise(rs(100)), income = Paise.ZERO)
            val incoming = onSavings.entries.single() as LedgerEntry.Single
            incoming.transaction.meta.id shouldBe moved.into.transaction.meta.id
            onSavings.totals shouldBe Totals(Paise.ZERO, Paise.ZERO)
        }

    @Test
    fun `a tag filter that matches a transfer's lines folds the pair and still leaves the totals alone`() =
        runSuspending {
            w.start()
            val moved = transfer(rs(5_000), ist(2026, 11, 4))
            val tagged = w.spend(ist(2026, 11, 3), w.item(rs(70), null, w.trip))

            val page = month(TransactionFilter(tagIds = setOf(w.trip.meta.id)))

            page.entries.keys() shouldContainExactly listOf(moved.groupId, tagged.id())
            page.totals shouldBe Totals(expense = Paise(rs(70)), income = Paise.ZERO)
        }

    @Test
    fun `a deleted transaction leaves its lines live yet is in no entry and no total, and restore brings it back`() =
        runSuspending {
            w.start()
            val gone = w.spend(ist(2026, 11, 3), w.item(rs(900), w.groceries, w.trip))
            val kept = w.spend(ist(2026, 11, 4), w.item(rs(100), w.groceries, w.trip))
            kit.transactions.delete(gone.transaction.meta.id)
            // The premise of the name: deleting the transaction left its line live.
            val line = kit.transactionLineRepository.liveLinesOf(gone.transaction.meta.id).single()
            line.meta.deletedAt shouldBe null
            val filters =
                listOf(
                    TransactionFilter(),
                    TransactionFilter(categoryIds = setOf(w.groceries.meta.id)),
                    TransactionFilter(tagIds = setOf(w.trip.meta.id)),
                )

            filters.forEach { filter ->
                val page = month(filter)
                page.entries.keys() shouldContainExactly listOf(kept.id())
                page.totals.expense shouldBe Paise(rs(100))
            }

            kit.transactions.restore(gone.transaction.meta.id)
            month().entries.keys() shouldContainExactly listOf(kept.id(), gone.id())
            month().totals.expense shouldBe Paise(rs(1_000))
        }

    @Test
    fun `a deleted transfer disappears as a whole`() =
        runSuspending {
            w.start()
            val moved = transfer(rs(5_000), ist(2026, 11, 4))
            kit.transactions.delete(moved.out.transaction.meta.id)

            month().entries shouldContainExactly emptyList()
            month(TransactionFilter(accountIds = setOf(w.savings.meta.id))).entries shouldContainExactly emptyList()
        }
}
