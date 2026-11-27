package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.validation.LedgerLimits
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
 * Order and the search cap through the real stack (slice C C3a, decision 3). Rows are written with **chosen
 * ids** straight through the repository, so the order the store holds them in (insertion) disagrees with the
 * order wanted (newest first, then id), and a pair straddling the cap is built on purpose.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionQueriesOrderingIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)
    private val max = LedgerLimits.MAX_SEARCH_RESULTS
    private val base = ist(2026, 11, 1, 6)

    @After
    fun closeDatabase() = database.close()

    private suspend fun search(filter: TransactionFilter = TransactionFilter()) =
        kit.transactionQueries.search(filter, kit.zone)

    /** [count] plain rows `e0001`... each a second apart starting at [from], so a higher number is newer. */
    private suspend fun plain(
        count: Int,
        from: Int = 1,
    ) = (from until from + count).forEach { w.row("e%04d".format(it), base.plusSeconds(it.toLong())) }

    @Test
    fun `entries come newest first`() =
        runSuspending {
            w.start()
            val old = w.spend(ist(2026, 11, 3), w.item(rs(1))).id()
            val new = w.spend(ist(2026, 11, 20), w.item(rs(2))).id()
            val mid = w.spend(ist(2026, 11, 10), w.item(rs(3))).id()

            val page = kit.transactionQueries.month(YearMonth.of(2026, 11), kit.zone, TransactionFilter())

            page.entries.keys() shouldContainExactly listOf(new, mid, old)
        }

    @Test
    fun `rows at the same instant are ordered by id, not by the order they were stored in`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            listOf("m", "z", "a").forEach { w.row(it, at) }

            search().entries.keys() shouldContainExactly listOf("a", "m", "z")
        }

    @Test
    fun `an account filter naming both sides shows each leg on its own, in a fixed order by id`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            // Stored the other way round to the id order, so only the id tie-break can produce a-leg, z-leg.
            w.row("z-leg", at, w.bank, TransactionType.TRANSFER, Direction.OUT, group = "g")
            w.row("a-leg", at, w.savings, TransactionType.TRANSFER, Direction.IN, group = "g")

            val entries = search(TransactionFilter(accountIds = setOf(w.bank.meta.id, w.savings.meta.id))).entries

            entries.map {
                (it as LedgerEntry.Single)
                    .transaction.meta.id.value
            } shouldContainExactly
                listOf("a-leg", "z-leg")
        }

    @Test
    fun `a transfer's legs stay adjacent among a thousand rows at the same instant, so the cap sees the pair whole`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            // By id alone the legs would be the first and the last of 1,003 rows; the group id puts them together.
            w.row("a-leg", at, w.savings, TransactionType.TRANSFER, Direction.IN, group = "g")
            w.row("z-leg", at, w.bank, TransactionType.TRANSFER, Direction.OUT, group = "g")
            (1..max * 2 + 1).forEach { w.row("m%04d".format(it), at) }

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe true
            val first = page.entries.first() as LedgerEntry.Transfer
            first.outgoing.meta.id.value shouldBe "z-leg"
            first.incoming.meta.id.value shouldBe "a-leg"
        }

    @Test
    fun `exactly the cap is not truncated`() =
        runSuspending {
            w.start()
            plain(max)

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe false
        }

    @Test
    fun `one more than the cap is truncated and the oldest is the one left out`() =
        runSuspending {
            w.start()
            plain(max + 1)

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe true
            val ids = page.entries.keys()
            ids.first() shouldBe "e%04d".format(max + 1)
            ids.last() shouldBe "e0002"
            ids.contains("e0001") shouldBe false
        }

    @Test
    fun `a transfer straddling the cap is never cut in half`() =
        runSuspending {
            w.start()
            plain(max - 1, from = 10)
            w.rowPair("p", base) // older than every plain row: entry number `max`, legs are rows `max` and `max + 1`
            w.row("older", base.minusSeconds(5)) // entry number `max + 1`

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe true
            val last = page.entries.last()
            (last is LedgerEntry.Transfer) shouldBe true
            (last as LedgerEntry.Transfer)
                .incoming.meta.id.value shouldBe "p-in"
        }

    @Test
    fun `a transfer that is the last entry makes exactly the cap, which is not truncated, though it is one row over`() =
        runSuspending {
            w.start()
            plain(max - 1, from = 10)
            w.rowPair("p", base)

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe false
            (page.entries.last() as LedgerEntry.Transfer)
                .outgoing.meta.id.value shouldBe "p-out"
        }

    @Test
    fun `the cap counts entries after folding, not rows, so many transfers fit`() =
        runSuspending {
            w.start()
            (1..max).forEach { w.rowPair("p%04d".format(it), base.plusSeconds(it.toLong())) }

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe false
            page.entries.all { it is LedgerEntry.Transfer } shouldBe true
        }

    @Test
    fun `one more transfer than the cap is truncated at the cap, so the fetch holds a whole extra pair`() =
        runSuspending {
            w.start()
            (1..max + 1).forEach { w.rowPair("p%04d".format(it), base.plusSeconds(it.toLong())) }

            val page = search()

            page.entries.size shouldBe max
            page.truncated shouldBe true
            page.entries.all { it is LedgerEntry.Transfer } shouldBe true
        }

    @Test
    fun `a capped search reads each line from the same newest rows it shows, whatever order they were stored in`() =
        runSuspending {
            w.start()
            // Stored oldest first, so the store's own order is the opposite of the display order. More rows
            // than the fetch (1,002), so the lines come from a different set if their sub-query is not ordered.
            (1..max * 2 + 10).forEach { w.rowWithLine("t%04d".format(it), base.plusSeconds(it.toLong())) }

            val page = search()

            page.entries.size shouldBe max
            page.entries.forEach { entry ->
                val single = entry as LedgerEntry.Single
                single.lines.map { it.meta.id.value } shouldContainExactly
                    listOf("${single.transaction.meta.id.value}-line")
            }
        }

    @Test
    fun `a month is never capped and its totals cover every entry`() =
        runSuspending {
            w.start()
            (1..max + 1).forEach { w.row("e%04d".format(it), base.plusSeconds(it.toLong()), amount = rs(2)) }

            val page = kit.transactionQueries.month(YearMonth.of(2026, 11), kit.zone, TransactionFilter())

            page.entries.size shouldBe max + 1
            page.totals.expense shouldBe Paise(rs(2) * (max + 1))
        }

    @Test
    fun `search spans months and honours the date range in the zone`() =
        runSuspending {
            w.start()
            val oct =
                w
                    .row("oct", ist(2026, 10, 31, 23, 30))
                    .meta.id.value
            val nov =
                w
                    .row("nov", ist(2026, 11, 1, 0, 10))
                    .meta.id.value
            w.row("dec", ist(2026, 12, 15))

            val upToTheFirst = TransactionFilter(to = LocalDate.of(2026, 11, 1))
            val november = TransactionFilter(from = LocalDate.of(2026, 11, 1), to = LocalDate.of(2026, 11, 30))

            search(upToTheFirst).entries.keys() shouldContainExactly listOf(nov, oct)
            search(november).entries.keys() shouldContainExactly listOf(nov)
        }
}
