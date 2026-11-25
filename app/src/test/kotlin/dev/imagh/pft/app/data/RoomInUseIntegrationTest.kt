package dev.imagh.pft.app.data

import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransactionWithLines
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/**
 * The in-use refusals through the real stack (slice C C1d, D4, Q4): the services over the Room repositories
 * and their join queries. A delete is refused while a **live line of a live transaction** (an account: a live
 * transaction) uses the record, and the refusal leaves **every row of every table, as text, exactly as it
 * was**, so no tombstone and no audit entry leaks. Deleting a transaction leaves its lines live, so the
 * count must join to the transaction.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomInUseIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val raw: SupportSQLiteDatabase get() = database.openHelper.writableDatabase
    private val at = Instant.parse("2026-10-31T08:30:00.500Z")

    @After
    fun closeDatabase() = database.close()

    private class World(
        val account: Account,
        val food: Category,
        val fuel: Category,
        val trip: Tag,
    )

    private suspend fun world(): World {
        kit.initialise()
        return World(
            account = kit.accounts.create(AccountDraft("Cash", AccountType.CASH, Paise(0))),
            food = kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft("Food", CategoryColour.CORAL)),
            fuel = kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft("Fuel", CategoryColour.CORAL)),
            trip = kit.tags.create("Trip"),
        )
    }

    private fun World.line(
        amount: Long,
        category: Category? = food,
        tags: Set<Tag> = emptySet(),
    ) = LineDraft(null, Paise(amount), category?.meta?.id, tags.map { it.meta.id }.toSet())

    private suspend fun World.spend(vararg lines: LineDraft): TransactionWithLines =
        kit.transactions.create(
            TransactionDraft(
                TransactionType.EXPENSE,
                Paise(lines.sumOf { it.amount.value }),
                at,
                account.meta.id,
                title = "Lunch",
                lines = lines.toList(),
            ),
        )

    /** The saved transaction as an edit draft with [lines] instead (keeping the amount). */
    private fun TransactionWithLines.reLined(lines: List<LineDraft>) =
        TransactionDraft(
            transaction.type,
            transaction.amount,
            transaction.occurredAt,
            transaction.accountId,
            title = transaction.title,
            lines = lines,
        )

    private fun dump(table: String): List<String> =
        raw.query("SELECT * FROM $table ORDER BY rowid").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor else null }
                .map { row ->
                    (0 until row.columnCount).joinToString("|") { if (row.isNull(it)) "NULL" else row.getString(it) }
                }.toList()
        }

    private fun everything(): Map<String, List<String>> =
        listOf(
            "account",
            "category",
            "tag",
            "ledger_transaction",
            "transaction_line",
            "transaction_line_tag",
            "audit_entry",
        ).associateWith(::dump)

    /** Runs [block], which must be refused with [LedgerError.InUse] for [what], and checks that no row changed. */
    private suspend fun refusedAsInUse(
        what: String,
        block: suspend () -> Any?,
    ) {
        val before = everything()
        shouldThrow<LedgerError.InUse> { block() }.what shouldBe what
        everything() shouldBe before
    }

    private fun count(table: String): Int =
        raw.query("SELECT COUNT(*) FROM $table").use {
            it.moveToFirst()
            it.getInt(0)
        }

    private fun deletedRows(table: String): Int =
        raw.query("SELECT COUNT(*) FROM $table WHERE deleted_at IS NOT NULL").use {
            it.moveToFirst()
            it.getInt(0)
        }

    // The account -------------------------------------------------------------------------------

    @Test
    fun `an account used by a live transaction cannot be deleted, archived or not, and nothing changes`() =
        runSuspending {
            val w = world()
            w.spend(w.line(100_00))

            refusedAsInUse("account") { kit.accounts.delete(w.account.meta.id) }

            kit.accounts
                .archive(w.account.meta.id)
                .meta.isArchived shouldBe true
            refusedAsInUse("account") { kit.accounts.delete(w.account.meta.id) }
            deletedRows("account") shouldBe 0
        }

    @Test
    fun `an account whose only transaction was deleted can be deleted, and not once that transaction is restored`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(100_00))
            kit.transactions.delete(saved.transaction.meta.id)

            kit.accounts
                .delete(w.account.meta.id)
                .meta.isDeleted shouldBe true
            kit.trailOf("account", w.account.meta.id.value).last().action shouldBe AuditAction.DELETE

            kit.accounts.restore(w.account.meta.id)
            kit.transactions.restore(saved.transaction.meta.id)
            refusedAsInUse("account") { kit.accounts.delete(w.account.meta.id) }
        }

    @Test
    fun `an unused account is deleted while another account is in use`() =
        runSuspending {
            val w = world()
            w.spend(w.line(100_00))
            val spare = kit.accounts.create(AccountDraft("Spare", AccountType.CASH, Paise(0)))

            kit.accounts
                .delete(spare.meta.id)
                .meta.isDeleted shouldBe true
        }

    // The category ------------------------------------------------------------------------------

    @Test
    fun `a category used by a live line cannot be deleted, archived or not, and nothing changes`() =
        runSuspending {
            val w = world()
            w.spend(w.line(100_00, w.food))

            refusedAsInUse("category") { kit.categories.delete(w.food.meta.id) }

            kit.categories
                .archive(w.food.meta.id)
                .meta.isArchived shouldBe true
            refusedAsInUse("category") { kit.categories.delete(w.food.meta.id) }
            deletedRows("category") shouldBe 0
        }

    @Test
    fun `a category named only by a deleted transaction's still-live line can be deleted`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(100_00, w.food))
            kit.transactions.delete(saved.transaction.meta.id)
            deletedRows("transaction_line") shouldBe 0

            kit.categories
                .delete(w.food.meta.id)
                .meta.isDeleted shouldBe true
        }

    @Test
    fun `a category named only by a line an edit removed (a tombstoned line of a live transaction) can be deleted`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(30_00, w.food), w.line(20_00, w.fuel))
            refusedAsInUse("category") { kit.categories.delete(w.fuel.meta.id) }
            val kept = saved.lines.first { it.categoryId == w.food.meta.id }
            kit.transactions.edit(
                saved.transaction.meta.id,
                saved.reLined(listOf(LineDraft(kept.meta.id, Paise(50_00), w.food.meta.id))),
            )
            deletedRows("transaction_line") shouldBe 1

            kit.categories
                .delete(w.fuel.meta.id)
                .meta.isDeleted shouldBe true
            refusedAsInUse("category") { kit.categories.delete(w.food.meta.id) }
        }

    @Test
    fun `a category the only line was moved off can be deleted, and the new one cannot`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(50_00, w.food))
            kit.transactions.edit(
                saved.transaction.meta.id,
                saved.reLined(
                    listOf(
                        LineDraft(
                            saved.lines
                                .single()
                                .meta.id,
                            Paise(50_00),
                            w.fuel.meta.id,
                        ),
                    ),
                ),
            )

            kit.categories
                .delete(w.food.meta.id)
                .meta.isDeleted shouldBe true
            refusedAsInUse("category") { kit.categories.delete(w.fuel.meta.id) }
        }

    // The tag ---------------------------------------------------------------------------------

    @Test
    fun `a tag carried by a live line cannot be deleted, archived or not, and nothing changes`() =
        runSuspending {
            val w = world()
            w.spend(w.line(100_00, w.food, setOf(w.trip)))

            refusedAsInUse("tag") { kit.tags.delete(w.trip.meta.id) }

            kit.tags
                .archive(w.trip.meta.id)
                .meta.isArchived shouldBe true
            refusedAsInUse("tag") { kit.tags.delete(w.trip.meta.id) }
            deletedRows("tag") shouldBe 0
        }

    @Test
    fun `a tag carried only by a deleted transaction's still-live line can be deleted`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(100_00, w.food, setOf(w.trip)))
            kit.transactions.delete(saved.transaction.meta.id)
            deletedRows("transaction_line") shouldBe 0

            kit.tags
                .delete(w.trip.meta.id)
                .meta.isDeleted shouldBe true
        }

    @Test
    fun `a tag carried only by a line an edit removed (a tombstoned line) can be deleted`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(30_00, w.food), w.line(20_00, w.food, setOf(w.trip)))
            refusedAsInUse("tag") { kit.tags.delete(w.trip.meta.id) }
            val kept = saved.lines.first { it.tagIds.isEmpty() }
            kit.transactions.edit(
                saved.transaction.meta.id,
                saved.reLined(listOf(LineDraft(kept.meta.id, Paise(50_00), w.food.meta.id))),
            )
            deletedRows("transaction_line") shouldBe 1

            kit.tags
                .delete(w.trip.meta.id)
                .meta.isDeleted shouldBe true
        }

    @Test
    fun `a tag an edit dropped from the line that kept living can be deleted (the join rows were replaced)`() =
        runSuspending {
            val w = world()
            val saved = w.spend(w.line(50_00, w.food, setOf(w.trip)))
            refusedAsInUse("tag") { kit.tags.delete(w.trip.meta.id) }
            kit.transactions.edit(
                saved.transaction.meta.id,
                saved.reLined(
                    listOf(
                        LineDraft(
                            saved.lines
                                .single()
                                .meta.id,
                            Paise(50_00),
                            w.food.meta.id,
                        ),
                    ),
                ),
            )
            deletedRows("transaction_line") shouldBe 0
            count("transaction_line_tag") shouldBe 0

            kit.tags
                .delete(w.trip.meta.id)
                .meta.isDeleted shouldBe true
        }
}
