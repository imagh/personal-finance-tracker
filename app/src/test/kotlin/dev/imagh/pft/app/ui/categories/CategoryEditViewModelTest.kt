package dev.imagh.pft.app.ui.categories

import android.os.Looper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.DefaultCategories
import dev.imagh.pft.ledger.ids.CategoryId
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * [CategoryEditViewModel] over real in-memory Room through the production wiring ([RoomTestKit]): what
 * it offers as a parent, what it refuses, what it saves and audits, how edit mode prefills and what it
 * leaves alone (kind and parent), how untrusted navigation arguments are handled, and what a failure
 * does. The ViewModel runs on the main dispatcher, so tests wait with [awaitMain]. A cancelled save is not
 * tested here: the shared `launchSave` rethrows it, and `LaunchSaveTest` pins that deterministically
 * (`job.join()` and `job.isCancelled`).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CategoryEditViewModelTest {
    private val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val kit = RoomTestKit(database, auditLog = audit)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    /** The real audit log until a test sets [failure]; then every append throws it, so a write rolls back. */
    private class FailsOnDemand(
        private val real: AuditLog,
    ) : AuditLog {
        var failure: Throwable? = null
        var failedAppends = 0
            private set

        override suspend fun append(entry: AuditEntry) {
            failure?.let {
                failedAppends++
                throw it
            }
            real.append(entry)
        }
    }

    private fun failWith(failure: Throwable = IllegalStateException("secret detail Pets Vet")) {
        audit.failure = failure
    }

    private fun auditRowCount(): Long =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM audit_entry").use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    private fun ensureIdentity() {
        blocking { if (kit.identity.currentIdentity() == null) kit.initialise() }
    }

    private fun seed(
        name: String,
        parent: Category? = null,
        kind: CategoryKind = CategoryKind.EXPENSE,
        colour: CategoryColour = CategoryColour.CORAL,
        icon: CategoryIcon? = CategoryIcon.Symbol("restaurant"),
    ): Category {
        ensureIdentity()
        return blocking { kit.categories.create(parent?.meta?.id, kind, CategoryDraft(name, colour, icon)) }
    }

    private fun stored(): List<Category> =
        blocking { CategoryKind.entries.flatMap { kit.categories.lists(it).let { l -> l.active + l.archived } } }

    private fun trail(id: CategoryId) = blocking { kit.trailOf("category", id.value) }.map { it.action }

    /** A create form, waited until its parent choices have loaded. */
    private fun createForm(
        kind: String? = null,
        parent: String? = null,
    ): CategoryEditViewModel {
        ensureIdentity()
        val vm = CategoryEditViewModel(kit.categories, null, parent, kind)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    /** An edit form for [id], waited until it has loaded (or been found missing). */
    private fun editForm(id: String): CategoryEditViewModel {
        ensureIdentity()
        val vm = CategoryEditViewModel(kit.categories, id, null, null)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun CategoryEditViewModel.saveAndWait() {
        submit()
        awaitMain { state.value.saved }
    }

    // --- Create a top-level category

    @Test
    fun `a new form is empty, for an Expense category with no parent, slate and no symbol`() {
        val state = createForm().state.value

        state.editing shouldBe false
        state.loaded shouldBe true
        state.notFound shouldBe false
        state.kind shouldBe CategoryKind.EXPENSE
        state.name.text shouldBe ""
        state.colour shouldBe CategoryColour.SLATE
        state.icon shouldBe null
        state.parent shouldBe null
        state.saving shouldBe false
        state.saved shouldBe false
        state.saveFailed shouldBe false
    }

    @Test
    fun `a new form is not loaded until its parent choices are read`() {
        ensureIdentity()
        val vm = CategoryEditViewModel(kit.categories, null, null, null)

        vm.state.value.loaded shouldBe false
        // A save before the form is ready is ignored: it would validate a blank form.
        vm.submit()
        vm.state.value.name.error shouldBe null
        awaitMain { vm.state.value.loaded }
    }

    @Test
    fun `a valid create saves a top-level Expense category with its colour and symbol, and audits a CREATE`() {
        val vm = createForm()
        vm.onNameChange("  Pets ")
        vm.onColourChange(CategoryColour.AMBER)
        vm.onIconChange(CategoryIcon.Symbol("pets"))
        ShadowLog.clear()

        vm.saveAndWait()

        val category = stored().single()
        category.name shouldBe "Pets"
        category.colour shouldBe CategoryColour.AMBER
        category.iconKey shouldBe CategoryIcon.Symbol("pets")
        category.kind shouldBe CategoryKind.EXPENSE
        category.parentId shouldBe null
        trail(category.meta.id) shouldBe listOf(AuditAction.CREATE)
        vm.state.value.saving shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }

    @Test
    fun `with no symbol chosen the category is saved with no icon`() {
        val vm = createForm()
        vm.onNameChange("Pets")

        vm.saveAndWait()

        stored().single().iconKey shouldBe null
        stored().single().colour shouldBe CategoryColour.SLATE
    }

    @Test
    fun `the kind in the route is the kind created, and a new category goes last among its siblings`() {
        seed("Salary", kind = CategoryKind.INCOME)
        val vm = createForm(kind = "INCOME")
        vm.state.value.kind shouldBe CategoryKind.INCOME
        vm.onNameChange("Interest")

        vm.saveAndWait()

        val income = blocking { kit.categories.lists(CategoryKind.INCOME) }.active
        income.map { it.name } shouldBe listOf("Salary", "Interest")
        income.map { it.kind }.toSet() shouldBe setOf(CategoryKind.INCOME)
    }

    // --- The parent field

    @Test
    fun `only live top-level categories of the same kind are offered as a parent`() {
        val food = seed("Food")
        seed("Groceries", parent = food)
        val old = seed("Old")
        val gone = seed("Gone")
        seed("Salary", kind = CategoryKind.INCOME)
        seed("Transport")
        blocking {
            kit.categories.archive(old.meta.id)
            kit.categories.delete(gone.meta.id)
        }

        val options = createForm().state.value.parentOptions

        options.map { it.name } shouldBe listOf("Food", "Transport")
        createForm(kind = "INCOME")
            .state.value.parentOptions
            .map { it.name } shouldBe listOf("Salary")
    }

    @Test
    fun `choosing a parent creates a sub-category of the parent's kind, and choosing none makes it top-level again`() {
        val pets = seed("Pets")
        val vm = createForm()
        vm.onNameChange("Vet")

        vm.onParentChange(pets.meta.id)
        vm.state.value.parent
            ?.name shouldBe "Pets"
        vm.onParentChange(null)
        vm.state.value.parent shouldBe null
        vm.onParentChange(pets.meta.id)
        vm.saveAndWait()

        val vet = stored().single { it.name == "Vet" }
        vet.parentId shouldBe pets.meta.id
        vet.kind shouldBe CategoryKind.EXPENSE
        trail(vet.meta.id) shouldBe listOf(AuditAction.CREATE)
    }

    @Test
    fun `a parent in the route is chosen already`() {
        val pets = seed("Pets")

        val vm = createForm(kind = "EXPENSE", parent = pets.meta.id.value)

        vm.state.value.parent
            ?.meta
            ?.id shouldBe pets.meta.id
        vm.state.value.notFound shouldBe false
    }

    @Test
    fun `a parent that is not a live top-level category of that kind ends in not found and never crashes`() {
        val pets = seed("Pets")
        val vet = seed("Vet", parent = pets)
        val old = seed("Old")
        val salary = seed("Salary", kind = CategoryKind.INCOME)
        blocking { kit.categories.archive(old.meta.id) }
        listOf(
            vet.meta.id.value, // a sub-category cannot be a parent
            old.meta.id.value, // archived
            salary.meta.id.value, // the other kind
            "00000000-0000-7000-8000-000000000000",
            "",
            "../../etc/passwd",
            "x'); DROP TABLE category; --",
            "a".repeat(10_000),
        ).forEach { raw ->
            val vm = createForm(kind = "EXPENSE", parent = raw)

            vm.state.value.notFound shouldBe true
            vm.state.value.saved shouldBe false
        }
        stored().size shouldBe 4
    }

    @Test
    fun `an unknown kind in the route ends in not found`() {
        listOf("", "expense", "EXPENSE ", "TRANSFER", "x'); DROP TABLE category; --", "A".repeat(10_000)).forEach {
            val vm = createForm(kind = it)

            vm.state.value.notFound shouldBe true
            vm.state.value.loaded shouldBe true
        }
    }

    // --- Refusals

    @Test
    fun `a blank or over-long name shows its error under the name field and saves nothing`() {
        val vm = createForm()
        vm.onNameChange("   ")
        vm.submit()
        vm.state.value.name.error shouldBe FieldError.Required
        vm.state.value.name.text shouldBe "   "

        vm.onNameChange("n".repeat(101))
        vm.submit()

        vm.state.value.name.error shouldBe FieldError.TooLong(100)
        vm.state.value.saved shouldBe false
        vm.state.value.saving shouldBe false
        stored() shouldBe emptyList()
        auditRowCount() shouldBe 3 // the setup identity only
    }

    @Test
    fun `a name another live sibling has, ignoring case, shows Duplicate under the name field`() {
        seed("Food")
        val vm = createForm()
        vm.onNameChange(" food ")

        vm.submit()
        awaitMain { !vm.state.value.saving }

        vm.state.value.name.error shouldBe FieldError.Duplicate
        vm.state.value.name.text shouldBe " food "
        vm.state.value.saved shouldBe false
        vm.state.value.saveFailed shouldBe false
        stored().size shouldBe 1
        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg } shouldBe emptyList()
    }

    @Test
    fun `the same name under another parent is fine, and typing clears the Duplicate error`() {
        val food = seed("Food")
        val fun1 = seed("Fun")
        seed("Misc", parent = food)
        val vm = createForm()
        vm.onNameChange("Misc")
        vm.onParentChange(fun1.meta.id)
        vm.saveAndWait()
        stored().count { it.name == "Misc" } shouldBe 2

        val again = createForm()
        again.onNameChange("Food")
        again.submit()
        awaitMain { again.state.value.name.error != null }
        again.state.value.name.error shouldBe FieldError.Duplicate

        again.onNameChange("Food 2")

        again.state.value.name.error shouldBe null
    }

    // --- One save

    @Test
    fun `a second submit while saving is ignored, so one category and one audit entry are written`() {
        val vm = createForm()
        vm.onNameChange("Pets")

        vm.submit()
        vm.state.value.saving shouldBe true
        // The name is now invalid: without the guard this second submit would validate it and show Required.
        vm.onNameChange("")
        vm.submit()
        awaitMain { vm.state.value.saved }

        vm.state.value.name.error shouldBe null
        stored().single().name shouldBe "Pets"
        auditRowCount() shouldBe 4 // the identity's three and the one CREATE
    }

    @Test
    fun `submitting again once saved changes nothing`() {
        val vm = createForm()
        vm.onNameChange("Pets")
        vm.saveAndWait()

        vm.submit()
        repeat(5) { shadowOf(Looper.getMainLooper()).idle() }

        stored().size shouldBe 1
        auditRowCount() shouldBe 4
    }

    // --- Edit mode

    @Test
    fun `edit mode prefills name, colour, symbol and the fixed kind and parent`() {
        val food = seed("Food & dining")
        val groceries =
            seed("Groceries", parent = food, colour = CategoryColour.OLIVE, icon = CategoryIcon.Symbol("grocery"))

        val state = editForm(groceries.meta.id.value).state.value

        state.editing shouldBe true
        state.notFound shouldBe false
        state.name.text shouldBe "Groceries"
        state.colour shouldBe CategoryColour.OLIVE
        state.icon shouldBe CategoryIcon.Symbol("grocery")
        state.kind shouldBe CategoryKind.EXPENSE
        state.parent?.name shouldBe "Food & dining"
    }

    @Test
    fun `an income category and an archived one are found and prefilled too`() {
        val salary = seed("Salary", kind = CategoryKind.INCOME)
        val old = seed("Old")
        blocking { kit.categories.archive(old.meta.id) }

        editForm(salary.meta.id.value).state.value.kind shouldBe CategoryKind.INCOME
        editForm(old.meta.id.value)
            .state.value.name.text shouldBe "Old"
    }

    @Test
    fun `an edit changes the name, colour and symbol, and audits one UPDATE`() {
        val pets = seed("Pets", colour = CategoryColour.AMBER, icon = CategoryIcon.Symbol("pets"))
        val vm = editForm(pets.meta.id.value)
        vm.onNameChange("Pet care")
        vm.onColourChange(CategoryColour.TEAL)
        vm.onIconChange(CategoryIcon.Symbol("favorite"))

        vm.saveAndWait()

        val category = stored().single()
        category.name shouldBe "Pet care"
        category.colour shouldBe CategoryColour.TEAL
        category.iconKey shouldBe CategoryIcon.Symbol("favorite")
        trail(pets.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `an edit that changes nothing writes no audit entry`() {
        val pets = seed("Pets")
        val vm = editForm(pets.meta.id.value)
        val before = auditRowCount()

        vm.saveAndWait()

        trail(pets.meta.id) shouldBe listOf(AuditAction.CREATE)
        auditRowCount() shouldBe before
    }

    @Test
    fun `the symbol can be cleared, which falls back to the name's letter`() {
        val pets = seed("Pets")
        val vm = editForm(pets.meta.id.value)
        vm.onIconChange(null)

        vm.saveAndWait()

        stored().single().iconKey shouldBe null
    }

    @Test
    fun `renaming a category whose icon is an emoji keeps the emoji`() {
        val pets = seed("Pets", icon = CategoryIcon.Emoji("🐶"))
        val vm = editForm(pets.meta.id.value)
        vm.state.value.icon shouldBe CategoryIcon.Emoji("🐶")
        vm.onNameChange("Pet care")

        vm.saveAndWait()

        stored().single().iconKey shouldBe CategoryIcon.Emoji("🐶")
    }

    @Test
    fun `kind and parent cannot be changed when editing`() {
        val food = seed("Food")
        val other = seed("Other")
        val groceries = seed("Groceries", parent = food)
        val vm = editForm(groceries.meta.id.value)
        vm.onParentChange(other.meta.id)
        vm.onParentChange(null)
        vm.onNameChange("Shop")

        vm.saveAndWait()

        vm.state.value.parent
            ?.meta
            ?.id shouldBe food.meta.id
        val saved = stored().single { it.meta.id == groceries.meta.id }
        saved.parentId shouldBe food.meta.id
        saved.kind shouldBe CategoryKind.EXPENSE
        saved.sortOrder shouldBe groceries.sortOrder
        saved.name shouldBe "Shop"
    }

    @Test
    fun `the system Loan EMI category can be renamed`() {
        ensureIdentity()
        blocking { kit.categories.seedDefaults() }
        val loan = DefaultCategories.idOf("loan_emi")
        val vm = editForm(loan.value)
        vm.onNameChange("EMIs")

        vm.saveAndWait()

        stored().single { it.meta.id == loan }.name shouldBe "EMIs"
    }

    @Test
    fun `renaming into a sibling's name shows Duplicate and leaves the stored category as it was`() {
        seed("Food")
        val fun1 = seed("Fun")
        val vm = editForm(fun1.meta.id.value)
        vm.onNameChange("FOOD")

        vm.submit()
        awaitMain { !vm.state.value.saving }

        vm.state.value.name.error shouldBe FieldError.Duplicate
        stored().single { it.meta.id == fun1.meta.id }.name shouldBe "Fun"
        trail(fun1.meta.id) shouldBe listOf(AuditAction.CREATE)
    }

    @Test
    fun `a case-only rename of its own name is allowed`() {
        val food = seed("Food")
        val vm = editForm(food.meta.id.value)
        vm.onNameChange("FOOD")

        vm.saveAndWait()

        stored().single().name shouldBe "FOOD"
    }

    // --- Unknown, deleted and malformed ids (untrusted navigation arguments)

    @Test
    fun `an id that does not exist, is deleted, or is not an id ends in not found and never crashes`() {
        val deleted = seed("Gone")
        blocking { kit.categories.delete(deleted.meta.id) }
        listOf(
            "00000000-0000-7000-8000-000000000000",
            deleted.meta.id.value,
            "",
            "   ",
            "../../etc/passwd",
            "x'); DROP TABLE category; --",
            "a".repeat(10_000),
        ).forEach { raw ->
            val vm = editForm(raw)

            vm.state.value.notFound shouldBe true
            vm.state.value.editing shouldBe true
            vm.state.value.name.text shouldBe ""
            vm.submit()
            vm.state.value.saved shouldBe false
        }
        stored() shouldBe emptyList()
    }

    @Test
    fun `before the load finishes an edit form is neither loaded nor not found, and Save is ignored`() {
        val pets = seed("Pets")
        val vm = CategoryEditViewModel(kit.categories, pets.meta.id.value, null, null)

        vm.state.value.loaded shouldBe false
        vm.state.value.notFound shouldBe false
        vm.state.value.editing shouldBe true
        vm.submit()
        vm.state.value.name.error shouldBe null
        awaitMain { vm.state.value.loaded }
        vm.state.value.saved shouldBe false
        auditRowCount() shouldBe 4
    }

    @Test
    fun `a category deleted after the form loaded ends in not found on save, with nothing logged`() {
        val pets = seed("Pets")
        val vm = editForm(pets.meta.id.value)
        blocking { kit.categories.delete(pets.meta.id) }
        vm.onNameChange("Renamed")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.notFound }

        vm.state.value.saved shouldBe false
        vm.state.value.saving shouldBe false
        vm.state.value.saveFailed shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
        trail(pets.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    // --- An unexpected failure (the A5 save shape)

    @Test
    fun `a failing audit keeps the text, resets saving, sets saveFailed and logs only the enum`() {
        val vm = createForm()
        vm.onNameChange("  Pets ")
        vm.onColourChange(CategoryColour.AMBER)
        failWith()
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        val state = vm.state.value
        audit.failedAppends shouldBe 1
        state.saving shouldBe false
        state.saved shouldBe false
        state.name.text shouldBe "  Pets "
        state.colour shouldBe CategoryColour.AMBER
        state.name.error shouldBe null
        stored() shouldBe emptyList()
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("entity_save_failed kind=CATEGORY")
    }

    @Test
    fun `after a failure the user can just retry, which saves once`() {
        val vm = createForm()
        vm.onNameChange("Pets")
        failWith()
        vm.submit()
        awaitMain { vm.state.value.saveFailed }
        audit.failure = null

        vm.submit()
        awaitMain { vm.state.value.saved }

        vm.state.value.saveFailed shouldBe false
        stored().size shouldBe 1
        auditRowCount() shouldBe 4
    }

    @Test
    fun `editing a field, the colour or the symbol clears the failure message`() {
        listOf<(CategoryEditViewModel) -> Unit>(
            { it.onNameChange("Pets 2") },
            { it.onColourChange(CategoryColour.TEAL) },
            { it.onIconChange(CategoryIcon.Symbol("pets")) },
        ).forEach { edit ->
            val vm = createForm()
            vm.onNameChange("Pets")
            failWith()
            vm.submit()
            awaitMain { vm.state.value.saveFailed }
            audit.failure = null

            edit(vm)

            vm.state.value.saveFailed shouldBe false
        }
    }

    @Test
    fun `a failing audit on an edit keeps the stored category as it was`() {
        val pets = seed("Pets")
        val vm = editForm(pets.meta.id.value)
        vm.onNameChange("Renamed")
        failWith()
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        vm.state.value.name.text shouldBe "Renamed"
        stored().single().name shouldBe "Pets"
        trail(pets.meta.id) shouldBe listOf(AuditAction.CREATE)
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("entity_save_failed kind=CATEGORY")
    }
}
