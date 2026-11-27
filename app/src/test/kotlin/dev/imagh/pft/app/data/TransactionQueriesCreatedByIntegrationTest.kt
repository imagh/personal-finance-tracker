package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionType
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
 * The person filter through the real stack (slice C C3c): `createdBy` narrows `month`, `search` and the
 * totals by the stored `created_by_user_id`, like every other transaction-level filter.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionQueriesCreatedByIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)
    private val november = YearMonth.of(2026, 11)

    @After
    fun closeDatabase() = database.close()

    /** Rows by three people, expense and income, stored in no order that follows the person. */
    private suspend fun seed() {
        w.start()
        w.row("a1", ist(2026, 11, 3), amount = rs(10), by = "asha")
        w.row("r1", ist(2026, 11, 4), amount = rs(20), by = "ravi")
        w.row("a2", ist(2026, 11, 5), amount = rs(40), by = "asha", type = TransactionType.INCOME)
        w.row("m1", ist(2026, 11, 6), amount = rs(80), by = "mira")
        w.row("a3", ist(2026, 11, 7), amount = rs(160), by = "asha", account = w.card)
    }

    private fun by(vararg users: String) = TransactionFilter(createdBy = users.map(::UserId).toSet())

    private suspend fun month(filter: TransactionFilter) = kit.transactionQueries.month(november, kit.zone, filter)

    @Test
    fun `one person's month holds only their rows and the totals follow`() =
        runSuspending {
            seed()

            val page = month(by("asha"))

            page.entries.keys() shouldContainExactly listOf("a3", "a2", "a1")
            page.totals.expense shouldBe Paise(rs(10 + 160))
            page.totals.income shouldBe Paise(rs(40))
        }

    @Test
    fun `two people are either of them`() =
        runSuspending {
            seed()

            month(by("ravi", "mira")).entries.keys() shouldContainExactly listOf("m1", "r1")
        }

    @Test
    fun `no person means everyone`() =
        runSuspending {
            seed()

            month(TransactionFilter()).entries.keys() shouldContainExactly listOf("a3", "m1", "a2", "r1", "a1")
        }

    @Test
    fun `a person nobody matches is an empty month with zero totals`() =
        runSuspending {
            seed()

            val page = month(by("nobody"))

            page.entries shouldBe emptyList()
            page.totals.expense shouldBe Paise(0)
            page.totals.income shouldBe Paise(0)
        }

    @Test
    fun `search honours the person too`() =
        runSuspending {
            seed()

            kit.transactionQueries
                .search(by("mira"), kit.zone)
                .entries
                .keys() shouldContainExactly listOf("m1")
        }

    @Test
    fun `the person is ANDed with the other filters`() =
        runSuspending {
            seed()

            val onCard = by("asha").copy(accountIds = setOf(w.card.meta.id))

            month(onCard).entries.keys() shouldContainExactly listOf("a3")
            month(by("ravi").copy(accountIds = setOf(w.card.meta.id))).entries shouldBe emptyList()
        }

    @Test
    fun `a transfer folds into one entry when its person is chosen`() =
        runSuspending {
            w.start()
            w.row("p-out", ist(2026, 11, 3), w.bank, TransactionType.TRANSFER, Direction.OUT, "p", by = "asha")
            w.row("p-in", ist(2026, 11, 3), w.savings, TransactionType.TRANSFER, Direction.IN, "p", by = "asha")
            w.row("r1", ist(2026, 11, 4), by = "ravi")

            month(by("asha")).entries.keys() shouldContainExactly listOf("p")
        }
}
