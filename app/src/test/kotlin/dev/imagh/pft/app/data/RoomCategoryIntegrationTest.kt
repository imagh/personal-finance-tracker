package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.DefaultCategories
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Categories through the real stack (L-2, INT-2, INT-3, INT-4): `CategoryService` over the Room
 * repository, `AuditedWriter`, the real audit log and in-memory SQLite. Every service rule that touches
 * storage runs here, every field round-trips, and each change has its audit entry read back through the
 * DAO and mapper. No test relies on the order Room returns rows in: lists are compared sorted.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomCategoryIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val categories get() = kit.categories

    @After
    fun closeDatabase() = database.close()

    private fun draft(
        name: String = "Groceries",
        colour: CategoryColour = CategoryColour.CORAL,
        icon: CategoryIcon? = CategoryIcon.Symbol("grocery"),
    ) = CategoryDraft(name, colour, icon)

    private suspend fun trail(id: CategoryId) = kit.trailOf("category", id.value)

    private suspend fun top(
        name: String = "Food",
        kind: CategoryKind = CategoryKind.EXPENSE,
    ) = categories.create(null, kind, draft(name))

    private suspend fun sub(
        parent: Category,
        name: String = "Groceries",
    ) = categories.create(parent.meta.id, parent.kind, draft(name))

    /** Every stored row that is not a tombstone, fully sorted (position, then id). */
    private suspend fun live(): List<Category> =
        kit.categoryRepository.allLive().sortedWith(compareBy({ it.sortOrder }, { it.meta.id.value }))

    private suspend fun storedCount(): Int =
        database.openHelper.writableDatabase.query("SELECT COUNT(*) FROM category").use {
            it.moveToFirst()
            it.getInt(0)
        }

    // Create and round-trips ----------------------------------------------------------------------

    @Test
    fun `create stores every field and audits a CREATE attributed to the current identity`() =
        runSuspending {
            val who = kit.initialise()

            val created = top("Food")

            kit.categoryRepository.get(created.meta.id) shouldBe created
            created.parentId shouldBe null
            created.sortOrder shouldBe 0
            created.meta.createdByUserId shouldBe who.user.id
            created.meta.createdOnDeviceId shouldBe who.device.id
            val entry = trail(created.meta.id).single()
            entry.action shouldBe AuditAction.CREATE
            entry.source shouldBe AuditSource.MANUAL
            entry.userId shouldBe who.user.id
            entry.deviceId shouldBe who.device.id
            entry.changes shouldBe
                mapOf(
                    "kind" to FieldChange(null, "EXPENSE"),
                    "name" to FieldChange(null, "Food"),
                    "colour" to FieldChange(null, "CORAL"),
                    "icon_key" to FieldChange(null, "sym:grocery"),
                    "sort_order" to FieldChange(null, "0"),
                )
        }

    @Test
    fun `a sub-category stores its parent id and audits it`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")

            val groceries = sub(food)

            kit.categoryRepository.get(groceries.meta.id) shouldBe groceries
            groceries.parentId shouldBe food.meta.id
            trail(groceries.meta.id).single().changes["parent_id"] shouldBe FieldChange(null, food.meta.id.value)
        }

    @Test
    fun `no icon, an emoji icon, every colour and both kinds round-trip`() =
        runSuspending {
            kit.initialise()

            val plain = categories.create(null, CategoryKind.INCOME, draft("Plain", icon = null))
            val emoji = categories.create(null, CategoryKind.EXPENSE, draft("Cart", icon = CategoryIcon.Emoji("🛒")))
            val coloured =
                CategoryColour.entries.map { colour ->
                    categories.create(null, CategoryKind.EXPENSE, draft("Colour ${colour.key}", colour = colour))
                }

            (listOf(plain, emoji) + coloured).forEach { kit.categoryRepository.get(it.meta.id) shouldBe it }
            kit.categoryRepository.get(plain.meta.id)!!.iconKey shouldBe null
            kit.categoryRepository.get(emoji.meta.id)!!.iconKey shouldBe CategoryIcon.Emoji("🛒")
            coloured.map { kit.categoryRepository.get(it.meta.id)!!.colour } shouldBe CategoryColour.entries
        }

    @Test
    fun `the colour column holds the key while the audit trail names the colour`() =
        runSuspending {
            kit.initialise()
            val created = top("Food")

            val stored =
                database.openHelper.writableDatabase
                    .query("SELECT colour FROM category WHERE id = ?", arrayOf(created.meta.id.value))
                    .use {
                        it.moveToFirst()
                        it.getString(0)
                    }

            stored shouldBe "coral"
            trail(created.meta.id).single().changes["colour"] shouldBe FieldChange(null, "CORAL")
        }

    @Test
    fun `edit stores the change and audits only the changed fields`() =
        runSuspending {
            kit.initialise()
            val created = top("Food")

            val edited = categories.edit(created.meta.id, draft("Food and drink", CategoryColour.SKY, icon = null))

            kit.categoryRepository.get(created.meta.id) shouldBe edited
            val update = trail(created.meta.id).last()
            update.action shouldBe AuditAction.UPDATE
            update.changes shouldBe
                mapOf(
                    "name" to FieldChange("Food", "Food and drink"),
                    "colour" to FieldChange("CORAL", "SKY"),
                    "icon_key" to FieldChange("sym:grocery", null),
                )
        }

    // Rules that read the stored tree ------------------------------------------------------------

    @Test
    fun `a sub-category of a sub-category, or of another kind, is refused and nothing is written`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")
            val groceries = sub(food)
            val rows = storedCount()
            val entries = kit.auditLog.let { trail(food.meta.id).size + trail(groceries.meta.id).size }

            shouldThrow<LedgerError.DepthExceeded> { sub(groceries, "Organic") }
            shouldThrow<LedgerError.KindMismatch> {
                categories.create(food.meta.id, CategoryKind.INCOME, draft("Bonus"))
            }

            storedCount() shouldBe rows
            trail(food.meta.id).size + trail(groceries.meta.id).size shouldBe entries
        }

    @Test
    fun `names are unique among live siblings ignoring case, and a deleted sibling does not block`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")
            val groceries = sub(food, "Groceries")
            sub(top("Home"), "Groceries")

            shouldThrow<LedgerError.DuplicateName> { sub(food, " groceries ") }

            categories.delete(groceries.meta.id)
            val again = sub(food, "groceries")
            again.name shouldBe "groceries"
            shouldThrow<LedgerError.DuplicateName> { categories.restore(groceries.meta.id) }
            kit.categoryRepository
                .get(groceries.meta.id)!!
                .meta.isDeleted shouldBe true
        }

    @Test
    fun `new categories go last among their siblings and each sibling set counts on its own`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")
            val home = top("Home")
            val salary = top("Salary", CategoryKind.INCOME)
            val groceries = sub(food, "Groceries")
            val fuel = sub(food, "Fuel")

            listOf(food, home, salary, groceries, fuel).map { it.sortOrder } shouldBe listOf(0, 1, 0, 0, 1)
            kit.categoryRepository.get(fuel.meta.id)!!.sortOrder shouldBe 1
        }

    @Test
    fun `reorder stores the new order with one UPDATE per moved row and none for the rest`() =
        runSuspending {
            kit.initialise()
            val a = top("A")
            val b = top("B")
            val c = top("C")

            categories.reorder(null, CategoryKind.EXPENSE, listOf(a.meta.id, c.meta.id, b.meta.id))

            live().map { it.name } shouldBe listOf("A", "C", "B")
            live().map { it.sortOrder } shouldBe listOf(0, 1, 2)
            trail(a.meta.id).map { it.action } shouldBe listOf(AuditAction.CREATE)
            trail(b.meta.id).last().changes shouldBe mapOf("sort_order" to FieldChange("1", "2"))
            trail(c.meta.id).last().changes shouldBe mapOf("sort_order" to FieldChange("2", "1"))
            trail(b.meta.id).map { it.action } shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
        }

    @Test
    fun `a reorder that is not exactly the live siblings is refused and writes nothing`() =
        runSuspending {
            kit.initialise()
            val a = top("A")
            val b = top("B")
            val deleted = top("C")
            categories.delete(deleted.meta.id)
            val before = live()

            shouldThrow<LedgerError.InvalidOrder> { categories.reorder(null, CategoryKind.EXPENSE, listOf(a.meta.id)) }
            shouldThrow<LedgerError.InvalidOrder> {
                categories.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, a.meta.id, deleted.meta.id))
            }

            live() shouldBe before
            trail(a.meta.id).map { it.action } shouldBe listOf(AuditAction.CREATE)
        }

    @Test
    fun `archiving hides a category and its children from pickable, and unarchiving brings them back`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")
            val groceries = sub(food, "Groceries")
            val home = top("Home")
            val salary = top("Salary", CategoryKind.INCOME)

            categories.pickable(CategoryKind.EXPENSE).map { it.name } shouldBe listOf("Food", "Groceries", "Home")

            categories.archive(food.meta.id)

            categories.pickable(CategoryKind.EXPENSE).map { it.meta.id } shouldBe listOf(home.meta.id)
            // Still stored and live, only not offered.
            kit.categoryRepository
                .allLive()
                .map { it.meta.id }
                .toSet() shouldBe
                setOf(food.meta.id, groceries.meta.id, home.meta.id, salary.meta.id)

            categories.unarchive(food.meta.id)

            categories.pickable(CategoryKind.EXPENSE).map { it.name } shouldBe listOf("Food", "Groceries", "Home")
            trail(food.meta.id).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
        }

    @Test
    fun `delete is a tombstone excluded from the live list, restore brings it back, both audited`() =
        runSuspending {
            kit.initialise()
            val kept = top("Kept")
            val gone = top("Gone")

            categories.delete(gone.meta.id)

            live().map { it.meta.id } shouldBe listOf(kept.meta.id)
            kit.categoryRepository
                .get(gone.meta.id)!!
                .meta.isDeleted shouldBe true
            kit.categoryRepository.getLive(gone.meta.id) shouldBe null

            categories.restore(gone.meta.id)

            live().map { it.name } shouldBe listOf("Kept", "Gone")
            trail(gone.meta.id).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
        }

    @Test
    fun `a parent with a live child cannot be deleted, and a child of a deleted parent cannot be restored`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")
            val groceries = sub(food)

            shouldThrow<LedgerError.HasChildren> { categories.delete(food.meta.id) }
            kit.categoryRepository.getLive(food.meta.id) shouldBe food

            categories.delete(groceries.meta.id)
            categories.delete(food.meta.id)
            shouldThrow<LedgerError.Deleted> { categories.restore(groceries.meta.id) }
            kit.categoryRepository
                .get(groceries.meta.id)!!
                .meta.isDeleted shouldBe true

            categories.restore(food.meta.id)
            categories.restore(groceries.meta.id)
            live().map { it.name }.toSet() shouldBe setOf("Food", "Groceries")
        }

    // System categories ----------------------------------------------------------------------------

    @Test
    fun `bySystemKey finds the seeded Loan EMI category, which cannot be archived or deleted`() =
        runSuspending {
            kit.initialise()
            kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI) shouldBe null

            categories.seedDefaults()

            val emi = kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI)!!
            emi.name shouldBe "Loan EMI"
            emi.systemKey shouldBe SystemCategory.LOAN_EMI
            emi.meta.id shouldBe DefaultCategories.idOf("loan_emi")
            shouldThrow<LedgerError.SystemCategoryProtected> { categories.archive(emi.meta.id) }
            shouldThrow<LedgerError.SystemCategoryProtected> { categories.delete(emi.meta.id) }
            categories.edit(emi.meta.id, draft("EMI", CategoryColour.INDIGO, icon = null))
            kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI)!!.name shouldBe "EMI"
        }

    @Test
    fun `the unique index refuses a second row for the same system category`() =
        runSuspending {
            kit.initialise()
            categories.seedDefaults()
            val emi = kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI)!!

            shouldThrow<SQLiteConstraintException> {
                kit.writer.create(kit.categoryRepository, CategoryId(kit.ids.next())) { meta ->
                    emi.copy(meta = meta, name = "Loan EMI 2")
                }
            }

            kit.categoryRepository.allLive().count { it.systemKey == SystemCategory.LOAN_EMI } shouldBe 1
        }

    // Seeding ---------------------------------------------------------------------------------------

    @Test
    fun `seeding stores the 31 defaults with their ids and parents, audited as SYSTEM, once only`() =
        runSuspending {
            val who = kit.initialise()

            categories.seedDefaults()
            categories.seedDefaults()

            val stored = kit.categoryRepository.allLive()
            stored shouldHaveSize 31
            val ids = stored.map { it.meta.id }.toSet()
            ids shouldBe DefaultCategories.LIST.map { DefaultCategories.idOf(it.seedKey) }.toSet()
            stored.mapNotNull { it.parentId }.forEach { (it in ids) shouldBe true }
            stored.forEach { category ->
                val entry = trail(category.meta.id).single()
                entry.action shouldBe AuditAction.CREATE
                entry.source shouldBe AuditSource.SYSTEM
                entry.userId shouldBe who.user.id
            }
        }

    // Atomicity and refusals ----------------------------------------------------------------------

    /** An audit log that fails on its [failOn]th append and records the others in the real trail. */
    private fun failingKit(failOn: Int): RoomTestKit {
        val real = kit.auditLog
        var appends = 0
        return RoomTestKit(
            database,
            auditLog =
                object : AuditLog {
                    override suspend fun append(entry: AuditEntry) {
                        appends++
                        if (appends == failOn) error("audit append failed")
                        real.append(entry)
                    }
                },
        )
    }

    @Test
    fun `a failing audit write leaves no category row (INT-4)`() =
        runSuspending {
            kit.initialise()
            val failing = failingKit(failOn = 1)

            shouldThrow<IllegalStateException> { failing.categories.create(null, CategoryKind.EXPENSE, draft()) }

            storedCount() shouldBe 0
            kit.categoryRepository.allLive().shouldBeEmpty()
        }

    @Test
    fun `a failing audit write part-way through seeding leaves no default behind`() =
        runSuspending {
            kit.initialise()
            val failing = failingKit(failOn = 10)

            shouldThrow<IllegalStateException> { failing.categories.seedDefaults() }

            storedCount() shouldBe 0
            database.openHelper.writableDatabase.query("SELECT COUNT(*) FROM audit_entry").use {
                it.moveToFirst()
                it.getInt(0) shouldBe 3
            }
        }

    @Test
    fun `an edit of an unknown or deleted category is refused`() =
        runSuspending {
            kit.initialise()
            val food = top("Food")
            categories.delete(food.meta.id)

            shouldThrow<LedgerError.Deleted> { categories.edit(food.meta.id, draft("Other")) }
            shouldThrow<LedgerError.NotFound> { categories.edit(CategoryId("missing"), draft()) }
        }

    @Test
    fun `before first-run setup nothing is written`() =
        runSuspending {
            shouldThrow<LedgerError.NotInitialised> { top("Food") }
            shouldThrow<LedgerError.NotInitialised> { categories.seedDefaults() }

            storedCount() shouldBe 0
        }
}
