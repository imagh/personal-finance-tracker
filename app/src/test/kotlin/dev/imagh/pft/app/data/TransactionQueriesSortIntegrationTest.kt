package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.SearchPage
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.query.TransactionSort
import dev.imagh.pft.ledger.query.TransactionSort.Key
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Duration
import java.time.Instant
import java.time.YearMonth

/**
 * Sort through the real stack (slice C C3c). Every test seeds so that a naive order differs: rows are
 * stored in an order that disagrees with every expected order, ids disagree with every key, and each key's
 * ranking differs from every other key's (the five rankings below are pairwise neither equal nor reversed),
 * so each of the ten (key, direction) results is decided by that key alone.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionQueriesSortIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)
    private val november = YearMonth.of(2026, 11)
    private val max = LedgerLimits.MAX_SEARCH_RESULTS
    private val base = Instant.parse("2026-11-20T00:00:00Z")

    @After
    fun closeDatabase() = database.close()

    private suspend fun month(
        sort: TransactionSort,
        filter: TransactionFilter = TransactionFilter(),
    ) = kit.transactionQueries.month(november, kit.zone, filter, sort)

    private suspend fun search(
        sort: TransactionSort,
        filter: TransactionFilter = TransactionFilter(),
    ) = kit.transactionQueries.search(filter, kit.zone, sort)

    private fun asc(key: Key) = TransactionSort(key, descending = false)

    private fun desc(key: Key) = TransactionSort(key, descending = true)

    /**
     * Five rows a to e, stored in the order c, e, a, d, b. Ascending rankings:
     * occurred a c e b d, created b e a d c, updated d a c e b, amount c d b a e, title e b d c a
     * (alpha, Bravo, charlie, Delta, echo: an upper-case first letter sorts before every lower-case one in a
     * binary comparison, so only a case-insensitive order gives e b d c a).
     */
    private suspend fun seedFive() {
        w.start()
        val occurred = mapOf("a" to 2, "c" to 4, "e" to 6, "b" to 8, "d" to 10)
        val created = mapOf("b" to 1L, "e" to 2L, "a" to 3L, "d" to 4L, "c" to 5L)
        val updated = mapOf("d" to 1L, "a" to 2L, "c" to 3L, "e" to 4L, "b" to 5L)
        val amount = mapOf("c" to 1L, "d" to 2L, "b" to 3L, "a" to 4L, "e" to 5L)
        val title = mapOf("e" to "alpha", "b" to "Bravo", "d" to "charlie", "c" to "Delta", "a" to "echo")
        listOf("c", "e", "a", "d", "b").forEach { id ->
            w.row(
                id,
                ist(2026, 11, occurred.getValue(id)),
                amount = rs(amount.getValue(id)),
                title = title.getValue(id),
                created = base.plusSeconds(created.getValue(id)),
                updated = base.plusSeconds(updated.getValue(id)),
            )
        }
    }

    private suspend fun order(sort: TransactionSort) = month(sort).entries.keys()

    @Test
    fun `the default sort is the payment date, newest first`() =
        runSuspending {
            seedFive()

            month(TransactionSort.DEFAULT).entries.keys() shouldContainExactly listOf("d", "b", "e", "c", "a")
            kit.transactionQueries
                .month(november, kit.zone)
                .entries
                .keys() shouldContainExactly
                listOf("d", "b", "e", "c", "a")
            kit.transactionQueries
                .search(TransactionFilter(), kit.zone)
                .entries
                .keys() shouldContainExactly
                listOf("d", "b", "e", "c", "a")
        }

    @Test
    fun `OCCURRED orders by the payment date in both directions`() =
        runSuspending {
            seedFive()

            order(asc(Key.OCCURRED)) shouldContainExactly listOf("a", "c", "e", "b", "d")
            order(desc(Key.OCCURRED)) shouldContainExactly listOf("d", "b", "e", "c", "a")
        }

    @Test
    fun `CREATED orders by when the record was created in both directions`() =
        runSuspending {
            seedFive()

            order(asc(Key.CREATED)) shouldContainExactly listOf("b", "e", "a", "d", "c")
            order(desc(Key.CREATED)) shouldContainExactly listOf("c", "d", "a", "e", "b")
        }

    @Test
    fun `UPDATED orders by when the record was last changed in both directions`() =
        runSuspending {
            seedFive()

            order(asc(Key.UPDATED)) shouldContainExactly listOf("d", "a", "c", "e", "b")
            order(desc(Key.UPDATED)) shouldContainExactly listOf("b", "e", "c", "a", "d")
        }

    @Test
    fun `AMOUNT orders by the amount in both directions`() =
        runSuspending {
            seedFive()

            order(asc(Key.AMOUNT)) shouldContainExactly listOf("c", "d", "b", "a", "e")
            order(desc(Key.AMOUNT)) shouldContainExactly listOf("e", "a", "b", "d", "c")
        }

    @Test
    fun `TITLE orders case-insensitively in both directions`() =
        runSuspending {
            seedFive()

            order(asc(Key.TITLE)) shouldContainExactly listOf("e", "b", "d", "c", "a")
            order(desc(Key.TITLE)) shouldContainExactly listOf("a", "c", "d", "b", "e")
        }

    @Test
    fun `entries without a title come last in both directions, in id order`() =
        runSuspending {
            seedFive()
            // Stored y before x, and dated so that both would come first by the payment date.
            w.row("y", ist(2026, 11, 28))
            w.row("x", ist(2026, 11, 27))

            order(asc(Key.TITLE)) shouldContainExactly listOf("e", "b", "d", "c", "a", "x", "y")
            order(desc(Key.TITLE)) shouldContainExactly listOf("a", "c", "d", "b", "e", "x", "y")
        }

    @Test
    fun `titles that differ only in case tie and fall to the id order in both directions`() =
        runSuspending {
            w.start()
            // Binary order puts "Swiggy" (id q) before "swiggy" (p), and "zomato" (s) before "Zomato" (r) when
            // descending; a case-insensitive tie falls to the id, ascending in both directions.
            w.row("q", ist(2026, 11, 5), title = "Swiggy")
            w.row("p", ist(2026, 11, 6), title = "swiggy")
            w.row("s", ist(2026, 11, 7), title = "zomato")
            w.row("r", ist(2026, 11, 8), title = "Zomato")

            order(asc(Key.TITLE)) shouldContainExactly listOf("p", "q", "r", "s")
            order(desc(Key.TITLE)) shouldContainExactly listOf("r", "s", "p", "q")
        }

    @Test
    fun `a tie on the key falls to the group and id tie-breaks, ascending in both directions`() =
        runSuspending {
            w.start()
            // Every key equal for all three rows, so the tie-break alone decides; stored z, m, a.
            listOf("z", "m", "a").forEach { w.row(it, ist(2026, 11, 10), title = "same") }

            Key.entries.forEach { key ->
                order(asc(key)) shouldContainExactly listOf("a", "m", "z")
                order(desc(key)) shouldContainExactly listOf("a", "m", "z")
            }
        }

    @Test
    fun `AMOUNT sorts a transfer by its amount and an itemised transaction by its total, not by its lines`() =
        runSuspending {
            w.start()
            val small =
                w
                    .row("small", ist(2026, 11, 9), amount = rs(100))
                    .meta.id.value
            val mid =
                w
                    .row("mid", ist(2026, 11, 8), amount = rs(500))
                    .meta.id.value
            // Rs 600 in total but Rs 300 on each line: by one line it would rank below mid.
            val itemised = w.spend(ist(2026, 11, 7), w.item(rs(300)), w.item(rs(300))).id()
            // A transfer of Rs 200 (two legs, folded into one entry, shown by its group id).
            w.row("t-out", ist(2026, 11, 5), w.bank, TransactionType.TRANSFER, Direction.OUT, "t", rs(200))
            w.row("t-in", ist(2026, 11, 5), w.savings, TransactionType.TRANSFER, Direction.IN, "t", rs(200))

            order(asc(Key.AMOUNT)) shouldContainExactly listOf(small, "t", mid, itemised)
            order(desc(Key.AMOUNT)) shouldContainExactly listOf(itemised, mid, "t", small)
        }

    @Test
    fun `a pair ranks by its first creation, the same value in both directions`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            w.row("lo", at, created = base.plusSeconds(1))
            w.row("out", at, w.bank, TransactionType.TRANSFER, Direction.OUT, "p", created = base.plusSeconds(2))
            w.row("mid", at, created = base.plusSeconds(3))
            w.row("in", at, w.savings, TransactionType.TRANSFER, Direction.IN, "p", created = base.plusSeconds(4))
            w.row("hi", at, created = base.plusSeconds(5))

            // The pair counts as created at 2 (its first leg), between lo and mid, whichever way it is read.
            order(desc(Key.CREATED)) shouldContainExactly listOf("hi", "mid", "p", "lo")
            order(asc(Key.CREATED)) shouldContainExactly listOf("lo", "p", "mid", "hi")
        }

    @Test
    fun `a pair ranks by its last change, the same value in both directions`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            w.row("lo", at, updated = base.plusSeconds(1))
            w.row("out", at, w.bank, TransactionType.TRANSFER, Direction.OUT, "p", updated = base.plusSeconds(2))
            w.row("mid", at, updated = base.plusSeconds(3))
            w.row("in", at, w.savings, TransactionType.TRANSFER, Direction.IN, "p", updated = base.plusSeconds(4))
            w.row("hi", at, updated = base.plusSeconds(5))

            // The pair counts as changed at 4 (its last leg), between mid and hi, whichever way it is read.
            order(desc(Key.UPDATED)) shouldContainExactly listOf("hi", "p", "mid", "lo")
            order(asc(Key.UPDATED)) shouldContainExactly listOf("lo", "mid", "p", "hi")
        }

    @Test
    fun `a transfer edited on one side only is ranked by its last change and ascending is descending reversed`() =
        runSuspending {
            w.start()
            val draft = TransferDraft(w.bank.meta.id, w.savings.meta.id, Paise(rs(500)), ist(2026, 11, 10))
            val oldest = w.spend(ist(2026, 11, 2), w.item(rs(1))).id()
            kit.clock.advance(Duration.ofDays(1))
            val transfer = kit.transfers.create(draft).groupId
            kit.clock.advance(Duration.ofDays(1))
            val plain = w.spend(ist(2026, 11, 3), w.item(rs(2))).id()
            kit.clock.advance(Duration.ofDays(1))
            // Only the source account changes: the OUT leg's updated_at moves, the IN leg's stays two days back.
            kit.transfers.edit(transfer, draft.copy(from = w.card.meta.id))

            order(desc(Key.UPDATED)) shouldContainExactly listOf(transfer, plain, oldest)
            order(asc(Key.UPDATED)) shouldContainExactly listOf(oldest, plain, transfer)
            // Created, by contrast, did not move: the pair sits where it was first written.
            order(asc(Key.CREATED)) shouldContainExactly listOf(oldest, transfer, plain)
            order(desc(Key.CREATED)) shouldContainExactly listOf(plain, transfer, oldest)
        }

    @Test
    fun `search gives every sort exactly the order the month gives`() =
        runSuspending {
            seedFive()
            w.row("y", ist(2026, 11, 28))
            w.row("x", ist(2026, 11, 27))

            Key.entries.forEach { key ->
                listOf(true, false).forEach { descending ->
                    val sort = TransactionSort(key, descending)
                    search(sort).entries.keys() shouldContainExactly order(sort)
                }
            }
        }

    @Test
    fun `search sorts before it caps, so the kept entries are the first in that order`() =
        runSuspending {
            w.start()
            // Number i: dated later, larger amount, earlier title. So the newest are the biggest and the last by
            // title, and each key's first 500 differ from the default's (newest first).
            (1..max + 10).forEach { i ->
                w.row(
                    "e%04d".format(i),
                    ist(2026, 11, 1).plusSeconds(i.toLong()),
                    amount = rs(i.toLong()),
                    title = "t%04d".format(max + 10 - i),
                )
            }

            val smallest = search(asc(Key.AMOUNT))
            val firstByTitleDescending = search(desc(Key.TITLE))

            smallest.truncated shouldBe true
            smallest.entries.keys() shouldContainExactly (1..max).map { "e%04d".format(it) }
            firstByTitleDescending.truncated shouldBe true
            // The largest titles belong to the smallest numbers: e0001 has title t0509.
            firstByTitleDescending.entries.keys() shouldContainExactly (1..max).map { "e%04d".format(it) }
        }

    @Test
    fun `a capped search under a non-default sort reads each line and tag from the rows it shows`() =
        runSuspending {
            w.start()
            // Stored oldest first and dearest last, so ascending amount keeps the oldest rows and the default
            // order would fetch the newest. More rows than the fetch (1,010 against 1,002).
            (1..max * 2 + 10).forEach { i ->
                w.rowWithLine(
                    "t%04d".format(i),
                    ist(2026, 11, 1).plusSeconds(i.toLong()),
                    amount = rs(i.toLong()),
                    tag = w.trip,
                )
            }

            val page = search(asc(Key.AMOUNT))

            page.entries.size shouldBe max
            page.entries.forEachIndexed { index, entry ->
                val single = entry as LedgerEntry.Single
                single.transaction.meta.id.value shouldBe "t%04d".format(index + 1)
                single.lines.map { it.meta.id.value } shouldContainExactly
                    listOf("${single.transaction.meta.id.value}-line")
                single.lines.single().tagIds shouldBe setOf(w.trip.meta.id)
            }
        }

    @Test
    fun `a transfer's legs stay adjacent at the fetch edge under every sort and both directions`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            // Every sort key is equal on all 1,003 rows, so only the tie-break keeps the pair together.
            w.row("a-leg", at, w.savings, TransactionType.TRANSFER, Direction.IN, "g", title = "same")
            w.row("z-leg", at, w.bank, TransactionType.TRANSFER, Direction.OUT, "g", title = "same")
            (1..max * 2 + 1).forEach { w.row("m%04d".format(it), at, title = "same") }

            Key.entries.forEach { key ->
                listOf(true, false).forEach { descending ->
                    val page = search(TransactionSort(key, descending))

                    page.entries.size shouldBe max
                    page.truncated shouldBe true
                    val first = page.entries.first() as LedgerEntry.Transfer
                    first.outgoing.meta.id.value shouldBe "z-leg"
                    first.incoming.meta.id.value shouldBe "a-leg"
                }
            }
        }

    /** Transfer legs that came back alone: a pair cut at the fetch edge shows as a `Single` with a group id. */
    private fun loneLegs(page: SearchPage) =
        page.entries
            .filterIsInstance<LedgerEntry.Single>()
            .filter { it.transaction.transferGroupId != null }

    @Test
    fun `CREATED keeps a transfer's legs together when they were created at different moments`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            // The legs were created a millisecond either side of 1,001 plain rows (the clock is read per leg).
            // By each leg's own time the plain rows would sit between the two legs. The pair counts as created
            // at its first leg, so ascending it is first and descending it is after every plain row.
            w.row("a-leg", at, w.savings, TransactionType.TRANSFER, Direction.IN, "g", created = base.minusMillis(1))
            w.row("z-leg", at, w.bank, TransactionType.TRANSFER, Direction.OUT, "g", created = base.plusMillis(1))
            (1..max * 2 + 1).forEach { w.row("m%04d".format(it), at, created = base) }

            val ascending = search(asc(Key.CREATED))
            val descending = search(desc(Key.CREATED))

            ascending.entries.size shouldBe max
            (ascending.entries.first() as LedgerEntry.Transfer)
                .outgoing.meta.id.value shouldBe "z-leg"
            descending.entries.size shouldBe max
            loneLegs(descending) shouldBe emptyList()
        }

    @Test
    fun `UPDATED keeps a transfer's legs together when they were updated at different moments`() =
        runSuspending {
            w.start()
            val at = ist(2026, 11, 10)
            // The pair counts as changed at its last leg, so descending it is first and ascending it is after
            // every plain row; by each leg's own time the plain rows would sit between the two legs.
            w.row("a-leg", at, w.savings, TransactionType.TRANSFER, Direction.IN, "g", updated = base.minusMillis(1))
            w.row("z-leg", at, w.bank, TransactionType.TRANSFER, Direction.OUT, "g", updated = base.plusMillis(1))
            (1..max * 2 + 1).forEach { w.row("m%04d".format(it), at, updated = base) }

            val descending = search(desc(Key.UPDATED))
            val ascending = search(asc(Key.UPDATED))

            descending.entries.size shouldBe max
            (descending.entries.first() as LedgerEntry.Transfer)
                .outgoing.meta.id.value shouldBe "z-leg"
            ascending.entries.size shouldBe max
            loneLegs(ascending) shouldBe emptyList()
        }

    @Test
    fun `a sort and a filter work together, the filter narrowing and the sort ordering`() =
        runSuspending {
            seedFive()
            w.row("other", ist(2026, 11, 3), w.card, amount = rs(9))

            val onBank = TransactionFilter(accountIds = setOf(w.bank.meta.id))

            month(desc(Key.AMOUNT), onBank).entries.keys() shouldContainExactly listOf("e", "a", "b", "d", "c")
            month(desc(Key.AMOUNT)).entries.keys().first() shouldBe "other"
        }
}
