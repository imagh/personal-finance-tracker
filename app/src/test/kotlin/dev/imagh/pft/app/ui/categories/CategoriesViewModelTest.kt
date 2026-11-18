package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.audit.AuditAction
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
import org.robolectric.annotation.Config

/**
 * [CategoriesViewModel] over real in-memory Room through the production wiring ([RoomTestKit]): what
 * the list shows for each kind, which actions each row offers, and what moving, archiving and
 * deleting change and audit. The ViewModel runs on the main dispatcher, so tests wait with [awaitMain].
 * Failures and the undo offer are in [CategoriesActionsTest].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CategoriesViewModelTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun withDefaults() =
        blocking {
            kit.initialise()
            kit.categories.seedDefaults()
        }

    private fun id(seedKey: String) = DefaultCategories.idOf(seedKey)

    private fun viewModel(): CategoriesViewModel {
        val vm = CategoriesViewModel(kit.categories)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun top(
        name: String,
        kind: CategoryKind = CategoryKind.EXPENSE,
    ): Category = blocking { kit.categories.create(null, kind, CategoryDraft(name, CategoryColour.SLATE, null)) }

    private fun CategoriesViewModel.names(list: (CategoriesUiState) -> List<CategoryItem>) =
        list(state.value).map { it.category.name }

    private fun CategoriesViewModel.item(name: String) =
        (state.value.active + state.value.archived).single { it.category.name == name }

    private fun trail(id: CategoryId) = blocking { kit.trailOf("category", id.value) }.map { it.action }

    @Test
    fun `Expense is shown first, parents are followed by their indented children, in sort_order`() {
        withDefaults()

        val vm = viewModel()

        vm.state.value.kind shouldBe CategoryKind.EXPENSE
        vm.names { it.active }.take(10) shouldBe
            listOf(
                "Food & dining",
                "Groceries",
                "Eating out",
                "Food delivery",
                "Transport",
                "Fuel",
                "Cab & auto",
                "Public transport",
                "Shopping",
                "Bills & utilities",
            )
        vm.state.value.active
            .take(5)
            .map { it.indent } shouldBe listOf(false, true, true, true, false)
        vm.names { it.active }.size shouldBe 27
        vm.state.value.archived shouldBe emptyList()
    }

    @Test
    fun `the Income switch shows only income categories, and Expense brings the others back`() {
        withDefaults()
        val vm = viewModel()

        vm.selectKind(CategoryKind.INCOME)
        awaitMain { vm.state.value.kind == CategoryKind.INCOME && vm.state.value.loaded }

        vm.names { it.active } shouldBe listOf("Salary", "Interest", "Refunds & cashback", "Other income")
        vm.state.value.active
            .map { it.indent }
            .toSet() shouldBe setOf(false)

        vm.selectKind(CategoryKind.EXPENSE)
        awaitMain { vm.state.value.kind == CategoryKind.EXPENSE && vm.state.value.loaded }

        vm.names { it.active }.first() shouldBe "Food & dining"
        vm.names { it.active }.contains("Salary") shouldBe false
    }

    @Test
    fun `with no categories it is loaded and both lists are empty`() {
        blocking { kit.initialise() }

        val vm = viewModel()

        vm.state.value.loaded shouldBe true
        vm.state.value.active shouldBe emptyList()
        vm.state.value.archived shouldBe emptyList()
    }

    @Test
    fun `a row offers Move up and Move down except on the first and the last among its siblings`() {
        withDefaults()
        val vm = viewModel()

        vm.item("Food & dining").actions.take(2) shouldBe listOf(CategoryAction.MOVE_DOWN, CategoryAction.ARCHIVE)
        vm.item("Transport").actions.take(2) shouldBe listOf(CategoryAction.MOVE_UP, CategoryAction.MOVE_DOWN)
        vm.item("Groceries").actions.take(1) shouldBe listOf(CategoryAction.MOVE_DOWN)
        vm.item("Eating out").actions.take(2) shouldBe listOf(CategoryAction.MOVE_UP, CategoryAction.MOVE_DOWN)
        vm.item("Food delivery").actions.take(1) shouldBe listOf(CategoryAction.MOVE_UP)
        // Each parent's children are their own group: Fuel is first under Transport, not under Food.
        vm.item("Fuel").actions.contains(CategoryAction.MOVE_UP) shouldBe false
    }

    @Test
    fun `Loan EMI can move but has no Archive or Delete in its menu`() {
        withDefaults()

        val vm = viewModel()

        vm.item("Loan EMI").actions shouldBe listOf(CategoryAction.MOVE_UP)
        vm.item("Gifts & donations").actions shouldBe
            listOf(
                CategoryAction.MOVE_UP,
                CategoryAction.MOVE_DOWN,
                CategoryAction.ARCHIVE,
                CategoryAction.DELETE,
            )
    }

    @Test
    fun `Move down on the first parent puts it after the next one, and audits both rows`() {
        withDefaults()
        val vm = viewModel()

        vm.choose(id("food"), CategoryAction.MOVE_DOWN)
        awaitMain { vm.names { it.active }.first() == "Transport" }

        vm.names { it.active }.take(10) shouldBe
            listOf(
                "Transport",
                "Fuel",
                "Cab & auto",
                "Public transport",
                "Food & dining",
                "Groceries",
                "Eating out",
                "Food delivery",
                "Shopping",
                "Bills & utilities",
            )
        trail(id("food")).last() shouldBe AuditAction.UPDATE
        trail(id("transport")).last() shouldBe AuditAction.UPDATE
        trail(id("shopping")) shouldBe listOf(AuditAction.CREATE)
    }

    @Test
    fun `Move up on a child swaps it with the sibling before it and leaves the other parents alone`() {
        withDefaults()
        val vm = viewModel()

        vm.choose(id("food_eating_out"), CategoryAction.MOVE_UP)
        awaitMain { vm.names { it.active }[1] == "Eating out" }

        vm.names { it.active }.take(5) shouldBe
            listOf("Food & dining", "Eating out", "Groceries", "Food delivery", "Transport")
        vm.item("Eating out").indent shouldBe true
    }

    @Test
    fun `a move skips an archived sibling and leaves it where it was`() {
        blocking { kit.initialise() }
        val a = top("A")
        val b = top("B")
        val c = top("C")
        blocking { kit.categories.archive(b.meta.id) }
        val vm = viewModel()

        vm.item("A").actions.contains(CategoryAction.MOVE_DOWN) shouldBe true
        vm.choose(a.meta.id, CategoryAction.MOVE_DOWN)
        awaitMain { vm.names { it.active } == listOf("C", "A") }

        vm.names { it.archived } shouldBe listOf("B")
        // The archived one kept its place in the full order, so it wrote nothing.
        trail(b.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE)
        blocking { kit.categories.lists(CategoryKind.EXPENSE) }.siblingsOf(null).map { it.meta.id } shouldBe
            listOf(c.meta.id, b.meta.id, a.meta.id)
    }

    @Test
    fun `with an archived sibling before it, the first active row still has no Move up`() {
        blocking { kit.initialise() }
        val a = top("A")
        top("B")
        blocking { kit.categories.archive(a.meta.id) }

        val vm = viewModel()

        vm.item("B").actions shouldBe listOf(CategoryAction.ARCHIVE, CategoryAction.DELETE)
    }

    @Test
    fun `siblings that tie on sort_order still move, and the move shows`() {
        blocking { kit.initialise() }
        top("A")
        top("B")
        val c = top("C")
        blocking { kit.categories.delete(c.meta.id) }
        top("D")
        blocking { kit.categories.restore(c.meta.id) }
        // C and D now share a sort_order: the screen orders them by id, so C is shown before D.
        val vm = viewModel()
        vm.names { it.active } shouldBe listOf("A", "B", "C", "D")

        vm.choose(c.meta.id, CategoryAction.MOVE_DOWN)
        awaitMain { vm.names { it.active } == listOf("A", "B", "D", "C") }

        vm.choose(c.meta.id, CategoryAction.MOVE_UP)
        awaitMain { vm.names { it.active } == listOf("A", "B", "C", "D") }
    }

    @Test
    fun `archive moves a parent and its children to the archived section, children indented`() {
        withDefaults()
        val vm = viewModel()

        vm.choose(id("food"), CategoryAction.ARCHIVE)
        awaitMain {
            vm.state.value.archived
                .isNotEmpty()
        }

        vm.names { it.archived } shouldBe listOf("Food & dining", "Groceries", "Eating out", "Food delivery")
        vm.state.value.archived
            .map { it.indent } shouldBe listOf(false, true, true, true)
        vm.names { it.active }.first() shouldBe "Transport"
        trail(id("food")) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE)
        // Only the parent is archived; its children are hidden with it and offer only Delete.
        trail(id("food_groceries")) shouldBe listOf(AuditAction.CREATE)
        vm.item("Food & dining").actions shouldBe listOf(CategoryAction.UNARCHIVE, CategoryAction.DELETE)
        vm.item("Groceries").actions shouldBe listOf(CategoryAction.DELETE)
    }

    @Test
    fun `an archived sub-category of an active parent is listed as archived and names its parent`() {
        withDefaults()
        blocking { kit.categories.archive(id("food_eating_out")) }

        val vm = viewModel()

        vm.names { it.archived } shouldBe listOf("Eating out")
        vm.item("Eating out").indent shouldBe false
        vm.item("Eating out").parentName shouldBe "Food & dining"
        vm.item("Eating out").actions shouldBe listOf(CategoryAction.UNARCHIVE, CategoryAction.DELETE)
        vm.item("Groceries").parentName shouldBe null
        vm.names { it.active }.take(3) shouldBe listOf("Food & dining", "Groceries", "Food delivery")
    }

    @Test
    fun `unarchive brings a parent and its children back to active, in their place`() {
        withDefaults()
        blocking { kit.categories.archive(id("shopping")) }
        val vm = viewModel()
        vm.names { it.archived } shouldBe listOf("Shopping")

        vm.choose(id("shopping"), CategoryAction.UNARCHIVE)
        awaitMain {
            vm.state.value.archived
                .isEmpty()
        }

        vm.names { it.active }.indexOf("Shopping") shouldBe 8
        trail(id("shopping")) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
    }

    @Test
    fun `reload picks up a category added elsewhere`() {
        withDefaults()
        val vm = viewModel()
        top("Zeta")

        vm.reload()
        awaitMain { vm.names { it.active }.contains("Zeta") }

        vm.names { it.active }.last() shouldBe "Zeta"
    }

    @Test
    fun `a category with an emoji icon keeps it for the row`() {
        blocking { kit.initialise() }
        blocking {
            kit.categories.create(
                null,
                CategoryKind.EXPENSE,
                CategoryDraft("Pets", CategoryColour.AMBER, CategoryIcon.Emoji("🐶")),
            )
        }

        val vm = viewModel()

        vm.item("Pets").category.iconKey shouldBe CategoryIcon.Emoji("🐶")
        vm.item("Pets").category.colour shouldBe CategoryColour.AMBER
    }
}
