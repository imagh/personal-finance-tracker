package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.MonthSpan
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

/**
 * `recent` and `entryMonthSpan` through the real stack (slice C C3c): live rows only, across months,
 * transfers folded, in `Asia/Kolkata`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionQueriesRecentIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)

    @After
    fun closeDatabase() = database.close()

    private suspend fun recent(limit: Int) = kit.transactionQueries.recent(limit)

    private suspend fun span(zone: ZoneId = kit.zone) = kit.transactionQueries.entryMonthSpan(zone)

    private fun ym(
        year: Int,
        month: Int,
    ) = YearMonth.of(year, month)

    @Test
    fun `recent with no data is empty`() =
        runSuspending {
            w.start()

            recent(5) shouldBe emptyList()
        }

    @Test
    fun `recent returns the newest five live entries across months, newest first`() =
        runSuspending {
            w.start()
            // Stored in no date order, across four months.
            listOf("c" to ist(2026, 9, 2), "h" to ist(2026, 11, 20), "a" to ist(2026, 6, 1), "f" to ist(2026, 10, 3))
                .forEach { (id, at) -> w.row(id, at) }
            listOf("b" to ist(2026, 7, 9), "g" to ist(2026, 11, 2), "d" to ist(2026, 9, 28), "e" to ist(2026, 10, 1))
                .forEach { (id, at) -> w.row(id, at) }

            recent(5).keys() shouldContainExactly listOf("h", "g", "f", "e", "d")
        }

    @Test
    fun `recent leaves out a deleted transaction and fills its place with the next`() =
        runSuspending {
            w.start()
            val newest = w.spend(ist(2026, 11, 20), w.item(rs(5))).id()
            val kept = w.spend(ist(2026, 11, 10), w.item(rs(6))).id()
            val older = w.spend(ist(2026, 10, 10), w.item(rs(7))).id()
            kit.transactions.delete(TransactionId(newest))

            recent(2).keys() shouldContainExactly listOf(kept, older)
        }

    @Test
    fun `recent folds a transfer into one entry that counts once, and never cuts it`() =
        runSuspending {
            w.start()
            w.rowPair("p", ist(2026, 11, 20)) // newest: the first entry is a pair, two rows
            w.row("s1", ist(2026, 11, 10))
            w.row("s2", ist(2026, 11, 5))

            val one = recent(1)
            val two = recent(2)

            one.size shouldBe 1
            (one.single() as LedgerEntry.Transfer)
                .outgoing.meta.id.value shouldBe "p-out"
            (one.single() as LedgerEntry.Transfer)
                .incoming.meta.id.value shouldBe "p-in"
            two.keys() shouldContainExactly listOf("p", "s1")
        }

    @Test
    fun `recent counts entries after folding when the limit lands inside a run of pairs`() =
        runSuspending {
            w.start()
            (1..4).forEach { w.rowPair("p$it", ist(2026, 11, it)) }

            val entries = recent(3)

            entries.keys() shouldContainExactly listOf("p4", "p3", "p2")
            entries.all { it is LedgerEntry.Transfer } shouldBe true
        }

    @Test
    fun `recent orders rows at one instant by the fixed tie-break, whatever order they were stored in`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            listOf("m", "z", "a").forEach { w.row(it, at) }

            recent(3).keys() shouldContainExactly listOf("a", "m", "z")
        }

    @Test
    fun `recent carries each entry's lines`() =
        runSuspending {
            w.start()
            val id = w.spend(ist(2026, 11, 10), w.item(rs(3), w.groceries), w.item(rs(4), w.dining)).id()

            val single = recent(1).single() as LedgerEntry.Single

            single.transaction.meta.id.value shouldBe id
            single.lines.size shouldBe 2
        }

    @Test
    fun `recent refuses a limit below one or above the search cap`() =
        runSuspending {
            w.start()
            w.row("a", ist(2026, 11, 10))

            shouldThrow<LedgerError.InvalidRange> { recent(0) }
            shouldThrow<LedgerError.InvalidRange> { recent(-3) }
            shouldThrow<LedgerError.InvalidRange> { recent(LedgerLimits.MAX_SEARCH_RESULTS + 1) }
            recent(LedgerLimits.MAX_SEARCH_RESULTS).keys() shouldContainExactly listOf("a")
        }

    @Test
    fun `the month span is null with no rows`() =
        runSuspending {
            w.start()

            span() shouldBe null
        }

    @Test
    fun `the month span is null when every row is deleted`() =
        runSuspending {
            w.start()
            val only = w.spend(ist(2026, 11, 10), w.item(rs(5))).id()
            kit.transactions.delete(TransactionId(only))

            span() shouldBe null
        }

    @Test
    fun `the month span covers a back-dated and a future-dated entry and ignores deleted ones`() =
        runSuspending {
            w.start()
            w.row("future", ist(2027, 2, 14))
            w.row("middle", ist(2026, 11, 10))
            w.row("back", ist(2025, 3, 2))
            val gone = w.spend(ist(2019, 1, 5), w.item(rs(5))).id()
            kit.transactions.delete(TransactionId(gone))
            val goneLater = w.spend(ist(2031, 8, 5), w.item(rs(5))).id()
            kit.transactions.delete(TransactionId(goneLater))

            span() shouldBe MonthSpan(ym(2025, 3), ym(2027, 2))
        }

    @Test
    fun `one row is a span of one month, and a transfer's legs count like any rows`() =
        runSuspending {
            w.start()
            w.rowPair("p", ist(2026, 4, 9))

            span() shouldBe MonthSpan(ym(2026, 4), ym(2026, 4))
        }

    @Test
    fun `the month span reads the months in the given zone`() =
        runSuspending {
            w.start()
            // 23:30 UTC on 31 October is 05:00 on 1 November in Kolkata; 18:00 UTC on 30 November is 23:30 IST.
            w.row("early", Instant.parse("2026-10-31T23:30:00Z"))
            w.row("late", Instant.parse("2026-11-30T18:00:00Z"))

            span(ZoneId.of("Asia/Kolkata")) shouldBe MonthSpan(ym(2026, 11), ym(2026, 11))
            span(ZoneId.of("UTC")) shouldBe MonthSpan(ym(2026, 10), ym(2026, 11))
        }
}
