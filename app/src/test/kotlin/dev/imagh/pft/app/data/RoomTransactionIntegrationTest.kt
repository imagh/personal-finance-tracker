package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagRepository
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionLines
import dev.imagh.pft.ledger.transaction.TransactionService
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/**
 * Transactions and their lines through the real stack (L-1, L-11, INT-2, INT-3, INT-4): `TransactionService`
 * over the Room repositories, `AuditedWriter`, the real audit log and in-memory SQLite. Every scenario ends
 * with the sum invariant counted by [breakdownMismatchCount] (0 when only the service has written), and the
 * count is shown to see a violation planted with raw SQL. The rollback tests compare **every row of the
 * four tables, as text, before and after** a failing call, so a leftover row or audit entry of any kind
 * fails them. Mapper refusals are read back through the repositories.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomTransactionIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val raw: SupportSQLiteDatabase get() = database.openHelper.writableDatabase
    private val at = Instant.parse("2026-10-31T08:30:00.500Z")

    @After
    fun closeDatabase() = database.close()

    private class Tags(
        val work: Tag,
        val trip: Tag,
        val gift: Tag,
    )

    private class World(
        val account: Account,
        val food: Category,
        val fuel: Category,
        val salary: Category,
        tags: Tags,
    ) {
        val work = tags.work
        val trip = tags.trip
        val gift = tags.gift
    }

    private suspend fun world(): World {
        kit.initialise()

        fun named(name: String) = CategoryDraft(name, CategoryColour.CORAL)
        return World(
            account = kit.accounts.create(AccountDraft("Cash", AccountType.CASH, Paise(0))),
            food = kit.categories.create(null, CategoryKind.EXPENSE, named("Food")),
            fuel = kit.categories.create(null, CategoryKind.EXPENSE, named("Fuel")),
            salary = kit.categories.create(null, CategoryKind.INCOME, named("Salary")),
            tags = Tags(kit.tags.create("Work"), kit.tags.create("Trip"), kit.tags.create("Gift")),
        )
    }

    private fun World.line(
        amount: Long,
        category: Category? = food,
        tags: Set<Tag> = emptySet(),
        note: String? = null,
        id: TransactionLineId? = null,
    ) = LineDraft(id, Paise(amount), category?.meta?.id, tags.map { it.meta.id }.toSet(), note)

    private fun World.draft(
        amount: Long,
        vararg lines: LineDraft,
        type: TransactionType = TransactionType.EXPENSE,
    ) = TransactionDraft(type, Paise(amount), at, account.meta.id, title = "Lunch", lines = lines.toList())

    private suspend fun mismatches() = database.transactionLineDao().breakdownMismatchCount()

    private suspend fun trail(
        type: String,
        id: String,
    ) = kit.trailOf(type, id)

    private fun count(table: String): Int =
        raw.query("SELECT COUNT(*) FROM $table").use {
            it.moveToFirst()
            it.getInt(0)
        }

    /** Every row of [table] as text, in insertion order: a before/after comparison sees any difference. */
    private fun dump(table: String): List<String> =
        raw.query("SELECT * FROM $table ORDER BY rowid").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor else null }
                .map { row ->
                    (0 until row.columnCount).joinToString("|") { if (row.isNull(it)) "NULL" else row.getString(it) }
                }.toList()
        }

    private fun everything(): Map<String, List<String>> =
        listOf("ledger_transaction", "transaction_line", "transaction_line_tag", "audit_entry")
            .associateWith(::dump)

    /** Tag ids stored for the line, as stored rows (not through the repository). */
    private fun storedTagIds(lineId: String): Set<String> =
        raw.query("SELECT tag_id FROM transaction_line_tag WHERE line_id = ?", arrayOf(lineId)).use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toSet()
        }

    // Create and read back ------------------------------------------------------------------------

    @Test
    fun `a plain expense is stored with one line and audited as a CREATE of each`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(250_00, w.line(250_00, w.food, setOf(w.work), "Tiffin")))

            val txn = created.transaction
            val line = created.lines.single()
            kit.transactions.get(txn.meta.id) shouldBe created
            kit.transactionRepository.get(txn.meta.id) shouldBe txn
            kit.transactionLineRepository.get(line.meta.id) shouldBe line
            // The kind that NotFound names is the line's audit type, not a hand-typed copy of it.
            kit.transactionLineRepository.entityType shouldBe line.auditType
            line.tagIds shouldBe setOf(w.work.meta.id)
            storedTagIds(line.meta.id.value) shouldBe setOf(w.work.meta.id.value)
            val txnEntry = trail("ledger_transaction", txn.meta.id.value).single()
            txnEntry.action shouldBe AuditAction.CREATE
            txnEntry.changes["amount"] shouldBe FieldChange(null, "25000")
            val lineEntry = trail("transaction_line", line.meta.id.value).single()
            lineEntry.action shouldBe AuditAction.CREATE
            lineEntry.changes["tag_ids"] shouldBe FieldChange(null, w.work.meta.id.value)
            lineEntry.changes["note"] shouldBe FieldChange(null, "Tiffin")
            mismatches() shouldBe 0
        }

    @Test
    fun `an itemised income keeps each line's own category, tags and note apart`() =
        runSuspending {
            val w = world()
            val created =
                kit.transactions.create(
                    w.draft(
                        600_00,
                        w.line(100_00, w.salary, setOf(w.work, w.trip), "Base"),
                        w.line(200_00, w.salary, setOf(w.gift), "Bonus"),
                        w.line(300_00, null, emptySet()),
                        type = TransactionType.INCOME,
                    ),
                )

            val read = kit.transactions.get(created.transaction.meta.id)!!
            read shouldBe created
            read.lines.map { it.position } shouldBe listOf(1, 2, 3)
            // Each line has exactly its own tags: nothing leaks between lines.
            read.lines.map { it.tagIds } shouldBe
                listOf(setOf(w.work.meta.id, w.trip.meta.id), setOf(w.gift.meta.id), emptySet())
            read.lines.map { it.categoryId } shouldBe listOf(w.salary.meta.id, w.salary.meta.id, null)
            count("ledger_transaction") shouldBe 1
            count("transaction_line") shouldBe 3
            count("transaction_line_tag") shouldBe 3
            // The audited tag_ids is the sorted ids joined with a comma (IT §4).
            val expected = listOf(w.work.meta.id.value, w.trip.meta.id.value).sorted().joinToString(",")
            trail(
                "transaction_line",
                read.lines[0]
                    .meta.id.value,
            ).single().changes["tag_ids"] shouldBe
                FieldChange(null, expected)
            // A line with no tags audits tag_ids as unset.
            trail(
                "transaction_line",
                read.lines[2]
                    .meta.id.value,
            ).single().changes.containsKey("tag_ids") shouldBe
                false
            mismatches() shouldBe 0
        }

    @Test
    fun `the transaction table holds no category and no tag column`() =
        runSuspending {
            world()

            val columns =
                raw.query("SELECT name FROM pragma_table_info('ledger_transaction')").use { cursor ->
                    generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toSet()
                }

            // A positive control: a misspelt table would have no columns, and `none` would pass vacuously.
            ("amount" in columns) shouldBe true
            columns.none { it.contains("category") || it.contains("tag") } shouldBe true
        }

    // Edit: itemise, replace tags, remove --------------------------------------------------------

    @Test
    fun `editing replaces a line's tag set, adds and removes lines, and audits the diffs`() =
        runSuspending {
            val w = world()
            val created =
                kit.transactions.create(
                    w.draft(
                        300_00,
                        w.line(100_00, w.food, setOf(w.work, w.trip)),
                        w.line(200_00, w.fuel, setOf(w.gift)),
                    ),
                )
            val (first, second) = created.lines

            val edited =
                kit.transactions.edit(
                    created.transaction.meta.id,
                    w.draft(
                        350_00,
                        // Line 1 keeps one tag and drops the other; line 2 is removed; a new line is added.
                        w.line(150_00, w.food, setOf(w.work), id = first.meta.id),
                        w.line(200_00, w.fuel, setOf(w.trip, w.gift)),
                    ),
                )

            edited.lines.map { it.amount.value } shouldBe listOf(150_00L, 200_00L)
            kit.transactions.get(created.transaction.meta.id) shouldBe edited
            storedTagIds(first.meta.id.value) shouldBe setOf(w.work.meta.id.value)
            val oldTags = listOf(w.work.meta.id.value, w.trip.meta.id.value).sorted().joinToString(",")
            trail("transaction_line", first.meta.id.value).last().changes["tag_ids"] shouldBe
                FieldChange(oldTags, w.work.meta.id.value)
            // The removed line is tombstoned, never hard-deleted, and no longer read.
            kit.transactionLineRepository
                .get(second.meta.id)!!
                .meta.isDeleted shouldBe true
            trail("transaction_line", second.meta.id.value).last().action shouldBe AuditAction.DELETE
            kit.transactionLineRepository
                .liveLinesOf(created.transaction.meta.id)
                .map { it.meta.id }
                .toSet() shouldBe
                edited.lines.map { it.meta.id }.toSet()
            count("transaction_line") shouldBe 3
            mismatches() shouldBe 0
        }

    @Test
    fun `getLive returns a line until it is tombstoned, and get returns it with its tags either way`() =
        runSuspending {
            val w = world()
            val created =
                kit.transactions.create(w.draft(300_00, w.line(100_00, w.food, setOf(w.work)), w.line(200_00)))
            val (first, second) = created.lines
            kit.transactions.edit(
                created.transaction.meta.id,
                w.draft(100_00, w.line(100_00, w.food, setOf(w.work), id = first.meta.id)),
            )

            kit.transactionLineRepository.getLive(first.meta.id) shouldBe first
            kit.transactionLineRepository.getLive(second.meta.id) shouldBe null
            kit.transactionLineRepository
                .get(second.meta.id)!!
                .meta.isDeleted shouldBe true
        }

    @Test
    fun `deleting a transaction leaves its lines and a restore brings back exactly them`() =
        runSuspending {
            val w = world()
            val created =
                kit.transactions.create(
                    w.draft(300_00, w.line(100_00, w.food, setOf(w.work)), w.line(200_00, w.fuel, setOf(w.gift))),
                )
            val id = created.transaction.meta.id

            kit.transactions.delete(id)
            kit.transactions.get(id) shouldBe null
            kit.transactionLineRepository.liveLinesOf(id).size shouldBe 2
            mismatches() shouldBe 0

            kit.transactions.restore(id)
            kit.transactions
                .get(id)!!
                .lines
                .map { it.meta.id } shouldContainExactly created.lines.map { it.meta.id }
            kit.transactions
                .get(id)!!
                .lines
                .map { it.tagIds } shouldContainExactly created.lines.map { it.tagIds }
            mismatches() shouldBe 0
        }

    // INT-4: a foreign key failure on line 3's tag rolls everything back ---------------------------

    private class GhostTags(
        private val real: TagRepository,
        private val ghost: TagId,
        private val template: Tag,
    ) : TagRepository by real {
        /** Reports a live tag for [ghost] that has no row, so core accepts it and only the foreign key refuses. */
        override suspend fun get(id: TagId): Tag? =
            if (id == ghost) template.copy(meta = template.meta.copy(id = ghost)) else real.get(id)
    }

    private suspend fun ghostService(w: World): Pair<TransactionService, TagId> {
        val ghost = TagId("no-such-tag")
        val lines =
            TransactionLines(
                kit.writer,
                kit.transactionLineRepository,
                kit.categoryRepository,
                GhostTags(kit.tagRepository, ghost, w.work),
                kit.ids,
            )
        val service =
            TransactionService(kit.runner, kit.writer, kit.transactionRepository, lines, kit.accountRepository, kit.ids)
        return service to ghost
    }

    @Test
    fun `a foreign key failure on line 3's tag on create leaves every row and audit entry as before`() =
        runSuspending {
            val w = world()
            val (service, ghost) = ghostService(w)
            kit.transactions.create(w.draft(100_00, w.line(100_00, w.food, setOf(w.work)))) // other data must survive
            val before = everything()

            shouldThrow<SQLiteConstraintException> {
                service.create(
                    w.draft(
                        600_00,
                        w.line(100_00, w.food, setOf(w.work)),
                        w.line(200_00, w.fuel, setOf(w.trip)),
                        LineDraft(null, Paise(300_00), w.food.meta.id, setOf(ghost)),
                    ),
                )
            }

            everything() shouldBe before
            mismatches() shouldBe 0
            // One retry with a good tag succeeds with no duplicates.
            service.create(w.draft(600_00, w.line(100_00), w.line(200_00), w.line(300_00, w.food, setOf(w.gift))))
            count("ledger_transaction") shouldBe 2
            count("transaction_line") shouldBe 4
            mismatches() shouldBe 0
        }

    @Test
    fun `a foreign key failure on line 3's tag on edit leaves every row and audit entry as before`() =
        runSuspending {
            val w = world()
            val (service, ghost) = ghostService(w)
            val created =
                kit.transactions.create(
                    w.draft(300_00, w.line(100_00, w.food, setOf(w.work)), w.line(200_00, w.fuel, setOf(w.trip))),
                )
            val (first, second) = created.lines
            val before = everything()

            shouldThrow<SQLiteConstraintException> {
                service.edit(
                    created.transaction.meta.id,
                    w.draft(
                        450_00,
                        // Lines 1 and 2 really change (amount, tags) before line 3 fails.
                        w.line(150_00, w.fuel, setOf(w.gift), id = first.meta.id),
                        w.line(200_00, w.fuel, setOf(w.work), "x", id = second.meta.id),
                        LineDraft(null, Paise(100_00), w.food.meta.id, setOf(ghost)),
                    ),
                )
            }

            everything() shouldBe before
            kit.transactions.get(created.transaction.meta.id) shouldBe created
            mismatches() shouldBe 0
        }

    @Test
    fun `the line repository writes a line and its tags together even outside a service transaction`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00, w.food, setOf(w.work))))
            val template = created.lines.single()
            val before = everything()

            val bad = template.copy(meta = template.meta.copy(id = TransactionLineId("l-new")))
            shouldThrow<SQLiteConstraintException> {
                kit.transactionLineRepository.insert(bad.copy(tagIds = setOf(w.trip.meta.id, TagId("no-such-tag"))))
            }
            everything() shouldBe before

            // An update that fails on a tag keeps the old tag set, not a half-replaced one.
            shouldThrow<SQLiteConstraintException> {
                kit.transactionLineRepository.update(template.copy(tagIds = setOf(TagId("no-such-tag"))))
            }
            everything() shouldBe before
            kit.transactionLineRepository.get(template.meta.id)!!.tagIds shouldBe setOf(w.work.meta.id)
        }

    // The invariant counter ---------------------------------------------------------------------

    private fun plantLine(
        id: String,
        transactionId: String,
        amount: Long,
        deletedAt: Long? = null,
    ) = raw.execSQL(
        "INSERT INTO transaction_line (id, created_at, updated_at, created_by_user_id, created_on_device_id, " +
            "deleted_at, transaction_id, position, amount) VALUES (?, 1, 1, 'u', 'd', ?, ?, 9, ?)",
        arrayOf<Any?>(id, deletedAt, transactionId, amount),
    )

    @Test
    fun `a live line planted by raw SQL makes the mismatch count 1`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            mismatches() shouldBe 0

            plantLine("planted", created.transaction.meta.id.value, 1)

            mismatches() shouldBe 1
        }

    @Test
    fun `lines that add up to less than the amount make the mismatch count 1`() =
        runSuspending {
            val w = world()
            kit.transactions.create(w.draft(100_00, w.line(60_00), w.line(40_00)))
            mismatches() shouldBe 0

            raw.execSQL("UPDATE transaction_line SET amount = amount - 1 WHERE position = 2")

            mismatches() shouldBe 1
        }

    @Test
    fun `a live transaction with no live line counts, whether it has none or only a tombstoned one`() =
        runSuspending {
            val w = world()
            val tombstoned = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            val bare = kit.transactions.create(w.draft(50_00, w.line(50_00)))
            mismatches() shouldBe 0

            // Tombstone the only line by raw SQL: the transaction is live with no live line.
            val line = tombstoned.lines.single()
            raw.execSQL("UPDATE transaction_line SET deleted_at = 5 WHERE id = ?", arrayOf(line.meta.id.value))
            mismatches() shouldBe 1

            // Remove the other's only line outright (it has no tags): a live transaction with no line row.
            val bareId = bare.transaction.meta.id.value
            raw.execSQL("DELETE FROM transaction_line WHERE transaction_id = ?", arrayOf(bareId))
            mismatches() shouldBe 2
        }

    @Test
    fun `a tombstoned line does not count towards the sum and a tombstoned transaction is not counted`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            val id = created.transaction.meta.id.value

            plantLine("gone", id, 7, deletedAt = 5) // tombstoned: ignored by the sum
            mismatches() shouldBe 0

            raw.execSQL("UPDATE transaction_line SET amount = 1 WHERE id = 'gone'")
            raw.execSQL("UPDATE transaction_line SET deleted_at = NULL WHERE id = 'gone'")
            mismatches() shouldBe 1 // live again, and 100_00 + 1 != 100_00

            raw.execSQL("UPDATE ledger_transaction SET deleted_at = 5 WHERE id = ?", arrayOf(id))
            mismatches() shouldBe 0 // a tombstoned transaction is not counted
        }

    // The mappers refuse what the schema cannot --------------------------------------------------

    @Test
    fun `a stored amount of zero is refused on read, naming the column and not the value`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            raw.execSQL("UPDATE ledger_transaction SET amount = 0")

            val message =
                shouldThrow<IllegalStateException> { kit.transactionRepository.get(created.transaction.meta.id) }
                    .message
                    .orEmpty()

            message shouldContain "column amount"
            message shouldNotContain "0"
        }

    @Test
    fun `an unknown stored type is refused on read, naming the column and not the value`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            raw.execSQL("UPDATE ledger_transaction SET type = 'SECRET_KIND'")

            val message =
                shouldThrow<IllegalStateException> { kit.transactions.get(created.transaction.meta.id) }
                    .message
                    .orEmpty()

            message shouldContain "column type"
            message shouldNotContain "SECRET_KIND"
        }

    private suspend fun readLinesFailure(id: TransactionId): String =
        shouldThrow<IllegalStateException> { kit.transactionLineRepository.liveLinesOf(id) }.message.orEmpty()

    @Test
    fun `a stored line position or amount that is not positive is refused on read`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            val id = created.transaction.meta.id
            raw.execSQL("UPDATE transaction_line SET position = 0")

            readLinesFailure(id) shouldContain "column position"
            raw.execSQL("UPDATE transaction_line SET position = 1, amount = -5")
            readLinesFailure(id) shouldContain "column amount"
        }

    @Test
    fun `updating a line that was never stored fails without naming it, and writes nothing`() =
        runSuspending {
            val w = world()
            val created = kit.transactions.create(w.draft(100_00, w.line(100_00)))
            val stored: TransactionLine = created.lines.single()
            val ghost = stored.copy(meta = stored.meta.copy(id = TransactionLineId("secret-line-id")))
            val before = everything()

            val message =
                shouldThrow<IllegalStateException> { kit.transactionLineRepository.update(ghost) }.message.orEmpty()

            message shouldNotContain "secret-line-id"
            everything() shouldBe before
        }
}
