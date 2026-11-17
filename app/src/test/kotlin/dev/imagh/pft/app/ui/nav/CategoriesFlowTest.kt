package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.PftRoot
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.DefaultCategories
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The categories list end to end (PRD L-2, INT-3): the real [AppContainer] over a real Room database
 * with the default categories, rendered through [PftRoot] and the navigation host, from Home to More
 * to Categories. It asserts what is on screen and what is persisted, never that anything was called.
 *
 * The window is 3000dp tall so that all 31 rows are on screen at once: a row's menu is a popup, and
 * Robolectric places a popup wrongly for an anchor at the very edge of the window, so these tests
 * need no scrolling.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h3000dp-xxhdpi")
class CategoriesFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private var activity: ComponentActivity? = null

    @After
    fun closeDatabase() = database.close()

    private fun id(seedKey: String) = DefaultCategories.idOf(seedKey)

    /** Sets up the identity (Akash on Pixel) and the default categories, then shows the app on Home. */
    private fun show() {
        runBlocking {
            kit.initialise()
            kit.categories.seedDefaults()
        }
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        awaitText("Akash · Pixel")
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun awaitGone(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }

    private fun openCategories() {
        compose.onNodeWithContentDescription("More").performClick()
        awaitText("Categories")
        compose.onNodeWithText("Categories").performClick()
        awaitText("Food & dining")
    }

    private fun chooseFromMenu(
        category: String,
        action: String,
    ) {
        compose.onNodeWithContentDescription("Actions for $category").performClick()
        compose.onNodeWithText(action).performClick()
    }

    /** Where a row's text starts from the top of the window (unlike its bounds, valid for a row out of view). */
    private fun top(text: String) =
        compose
            .onNodeWithText(text)
            .fetchSemanticsNode()
            .positionInRoot.y

    private fun auditRows(): Long = database.rowCount("audit_entry")

    @Test
    fun `More to Categories shows Food and dining with Groceries under it, indented`() {
        show()

        openCategories()

        compose.onNodeWithText("Expense").assertIsDisplayed()
        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onNodeWithText("Groceries").assertIsDisplayed()
        (top("Groceries") > top("Food & dining")) shouldBe true
        (top("Groceries") < top("Transport")) shouldBe true
        val parentStart =
            compose
                .onNodeWithText(
                    "Food & dining",
                    useUnmergedTree = true,
                ).fetchSemanticsNode()
                .boundsInRoot.left
        val childStart =
            compose
                .onNodeWithText(
                    "Groceries",
                    useUnmergedTree = true,
                ).fetchSemanticsNode()
                .boundsInRoot.left
        (childStart > parentStart) shouldBe true
        compose.onAllNodesWithText("Salary").assertCountEquals(0)
    }

    @Test
    fun `Move down on Food and dining puts it after Transport and its sub-categories, and persists`() {
        show()
        openCategories()

        chooseFromMenu("Food & dining", "Move down")
        awaitMain {
            compose.waitForIdle()
            top("Transport") < top("Food & dining")
        }

        (top("Public transport") < top("Food & dining")) shouldBe true
        (top("Food & dining") < top("Groceries")) shouldBe true
        (top("Groceries") < top("Shopping")) shouldBe true
        runBlocking { kit.categories.pickable(CategoryKind.EXPENSE) }.take(2).map { it.name } shouldBe
            listOf("Transport", "Fuel")
        runBlocking { kit.trailOf("category", id("food").value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `archiving Shopping moves it under Archived, where it can be unarchived`() {
        show()
        openCategories()

        chooseFromMenu("Shopping", "Archive")
        awaitText("Archived (1)")

        compose.onAllNodesWithText("Shopping").assertCountEquals(0)
        runBlocking { kit.categories.lists(CategoryKind.EXPENSE) }.archived.map { it.meta.id } shouldBe
            listOf(id("shopping"))

        compose.onNodeWithText("Archived (1)").performClick()
        chooseFromMenu("Shopping", "Unarchive")
        awaitGone("Archived (1)")

        compose.onNodeWithText("Shopping").assertIsDisplayed()
        runBlocking { kit.trailOf("category", id("shopping").value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
    }

    @Test
    fun `the Income switch shows Salary and hides the expense categories`() {
        show()
        openCategories()

        compose.onNodeWithText("Income").performClick()
        awaitText("Salary")

        compose.onNodeWithText("Salary").assertIsDisplayed()
        compose.onNodeWithText("Other income").assertIsDisplayed()
        compose.onAllNodesWithText("Food & dining").assertCountEquals(0)

        compose.onNodeWithText("Expense").performClick()
        awaitText("Food & dining")
        compose.onAllNodesWithText("Salary").assertCountEquals(0)
    }

    @Test
    fun `deleting a category shows an Undo snackbar, and Undo brings it back`() {
        show()
        openCategories()

        chooseFromMenu("Gifts & donations", "Delete")
        awaitText("Category deleted")

        compose.onNodeWithText("Undo").assertIsDisplayed()
        compose.onAllNodesWithText("Gifts & donations").assertCountEquals(0)

        compose.onNodeWithText("Undo").performClick()
        awaitText("Gifts & donations")

        awaitGone("Category deleted")
        runBlocking { kit.trailOf("category", id("gifts").value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
    }

    @Test
    fun `deleting a parent that has sub-categories says so and changes nothing`() {
        show()
        openCategories()
        val rows = auditRows()

        chooseFromMenu("Food & dining", "Delete")
        awaitText("Delete its sub-categories first")

        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onNodeWithText("Groceries").assertIsDisplayed()
        compose.onAllNodesWithText("Category deleted").assertCountEquals(0)
        auditRows() shouldBe rows
    }

    @Test
    fun `Loan EMI offers no Archive or Delete`() {
        show()
        openCategories()

        compose.onNodeWithContentDescription("Actions for Loan EMI").performClick()

        compose.onNodeWithText("Move up").assertIsDisplayed()
        compose.onAllNodesWithText("Archive").assertCountEquals(0)
        compose.onAllNodesWithText("Delete").assertCountEquals(0)
    }

    // --- Create and edit (B4b)

    private fun openAddForm() {
        compose.onNodeWithContentDescription("Add category").performClick()
        awaitText("New category")
    }

    private fun type(
        label: String,
        text: String,
    ) = compose.onNodeWithText(label).performTextInput(text)

    private fun save() = compose.onNodeWithText("Save").performClick()

    private fun storedByName(name: String) =
        CategoryKind.entries
            .flatMap { runBlocking { kit.categories.lists(it) }.let { lists -> lists.active + lists.archived } }
            .single { it.name == name }

    private fun left(text: String) =
        compose
            .onNodeWithText(text, useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot.left

    @Test
    fun `the add control opens the form for a new category of the kind on show`() {
        show()
        openCategories()

        openAddForm()

        compose.onNodeWithText("Expense").assertIsDisplayed()
        compose.onNodeWithText("None (top level)").assertIsDisplayed()
        compose.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun `creating Pets with the pets symbol and amber lists it, and Vet under it is shown indented`() {
        show()
        openCategories()

        openAddForm()
        type("Name", "Pets")
        compose.onNodeWithContentDescription("Amber").performClick()
        compose.onNodeWithContentDescription("Pets").performClick()
        save()
        awaitGone("New category")
        awaitText("Pets")

        val pets = storedByName("Pets")
        pets.colour shouldBe CategoryColour.AMBER
        pets.iconKey shouldBe CategoryIcon.Symbol("pets")
        pets.kind shouldBe CategoryKind.EXPENSE
        pets.parentId shouldBe null
        runBlocking { kit.trailOf("category", pets.meta.id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE)
        // A new category goes last among its siblings.
        (top("Pets") > top("Loan EMI")) shouldBe true

        openAddForm()
        type("Name", "Vet")
        compose.onNodeWithText("Parent category").performClick()
        awaitText("Choose parent category")
        compose.onNodeWithText("Pets").performClick()
        awaitGone("Choose parent category")
        save()
        awaitGone("New category")
        awaitText("Vet")

        val vet = storedByName("Vet")
        vet.parentId shouldBe pets.meta.id
        vet.kind shouldBe CategoryKind.EXPENSE
        (top("Vet") > top("Pets")) shouldBe true
        (left("Vet") > left("Pets")) shouldBe true
    }

    private fun storedIconText(name: String): String? =
        database.openHelper.readableDatabase.query("SELECT icon_key FROM category WHERE name = ?", arrayOf(name)).use {
            it.moveToFirst()
            it.getString(0)
        }

    private fun pasteEmoji(text: String) =
        compose.onNode(hasSetTextAction() and hasText("Type or paste an emoji")).performTextInput(text)

    @Test
    fun `creating Pets with a quick-pick emoji stores the emoji, audited, and lists it`() {
        show()
        openCategories()

        openAddForm()
        type("Name", "Pets")
        compose.onNodeWithText("Emoji").performClick()
        compose.onNodeWithText("🐶").performClick()
        save()
        awaitGone("New category")
        awaitText("Pets")

        val pets = storedByName("Pets")
        pets.iconKey shouldBe CategoryIcon.Emoji("🐶")
        storedIconText("Pets") shouldBe "emoji:🐶"
        runBlocking { kit.trailOf("category", pets.meta.id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE)
    }

    @Test
    fun `a pasted text that is not one emoji shows a fixed message and the symbol already chosen is saved`() {
        show()
        openCategories()
        openAddForm()
        type("Name", "Pets")
        compose.onNodeWithContentDescription("Pets").performClick()
        compose.onNodeWithText("Emoji").performClick()

        pasteEmoji("not an emoji")
        awaitText("Enter just one emoji that this phone can show")
        compose.onAllNodesWithText("not an emoji", substring = true).assertCountEquals(0)
        save()
        awaitGone("New category")
        awaitText("Pets")

        storedByName("Pets").iconKey shouldBe CategoryIcon.Symbol("pets")
    }

    @Test
    fun `a pasted emoji is saved, and editing the category reopens on the Emoji side with it chosen`() {
        show()
        openCategories()
        openAddForm()
        type("Name", "Pets")
        compose.onNodeWithText("Emoji").performClick()
        pasteEmoji("not an emoji")
        awaitText("Enter just one emoji that this phone can show")

        pasteEmoji("🦄")
        awaitGone("Enter just one emoji that this phone can show")
        awaitText("Chosen emoji")
        save()
        awaitGone("New category")
        awaitText("Pets")

        storedIconText("Pets") shouldBe "emoji:🦄"
        compose.onNodeWithText("Pets").performClick()
        awaitText("Edit category")
        awaitText("Chosen emoji")
        compose.onNodeWithText("Emoji").assertIsSelected()
        compose.onAllNodesWithText("Type or paste an emoji").assertCountEquals(1)
    }

    @Test
    fun `the parent picker offers only top-level categories of the kind, never a sub-category`() {
        show()
        openCategories()
        openAddForm()

        compose.onNodeWithText("Parent category").performClick()
        awaitText("Choose parent category")

        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onNodeWithText("Transport").assertIsDisplayed()
        compose.onAllNodesWithText("Groceries").assertCountEquals(0)
        compose.onAllNodesWithText("Salary").assertCountEquals(0)
    }

    @Test
    fun `an income category is created under Income, and not under Expense`() {
        show()
        openCategories()
        compose.onNodeWithText("Income").performClick()
        awaitText("Salary")

        openAddForm()
        compose.onNodeWithText("Income").assertIsDisplayed()
        type("Name", "Dividends")
        save()
        awaitGone("New category")
        awaitText("Dividends")

        storedByName("Dividends").kind shouldBe CategoryKind.INCOME
        compose.onNodeWithText("Expense").performClick()
        awaitText("Food & dining")
        compose.onAllNodesWithText("Dividends").assertCountEquals(0)
    }

    @Test
    fun `a name another category already has is refused under the name field, and nothing is saved`() {
        show()
        openCategories()
        openAddForm()
        val rows = auditRows()

        type("Name", "food & DINING")
        save()
        awaitText("Already in use")

        compose.onNodeWithText("Already in use").assertIsDisplayed()
        compose.onNodeWithText("Save").assertIsDisplayed()
        auditRows() shouldBe rows
    }

    @Test
    fun `tapping a row opens it for editing with its type and parent fixed, and a new name shows in the list`() {
        show()
        openCategories()

        compose.onNodeWithText("Groceries").performClick()
        // The form draws nothing until the category has been read.
        awaitText("Colour")

        compose.onNode(hasSetTextAction() and hasText("Groceries")).assertIsDisplayed()
        compose.onAllNodesWithText("Can't be changed after the category is created").assertCountEquals(2)
        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("Groceries")).performTextReplacement("Supermarket")
        compose.onNodeWithContentDescription("Teal").performClick()
        save()
        awaitGone("Edit category")
        awaitText("Supermarket")

        compose.onAllNodesWithText("Groceries").assertCountEquals(0)
        val stored = storedByName("Supermarket")
        stored.meta.id shouldBe id("food_groceries")
        stored.colour shouldBe CategoryColour.TEAL
        stored.parentId shouldBe id("food")
        runBlocking { kit.trailOf("category", id("food_groceries").value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `Back from the form returns to the list without saving`() {
        show()
        openCategories()
        openAddForm()
        type("Name", "Unsaved")
        val rows = auditRows()

        compose.onNodeWithContentDescription("Back").performClick()
        awaitGone("New category")

        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onAllNodesWithText("Unsaved").assertCountEquals(0)
        auditRows() shouldBe rows
    }

    @Test
    fun `a category deleted elsewhere while the list is shown returns to the list with a no longer exists message`() {
        show()
        openCategories()
        runBlocking { kit.categories.delete(id("gifts")) }

        compose.onNodeWithText("Gifts & donations").performClick()
        awaitText("That category no longer exists")

        compose.onAllNodesWithText("Edit category").assertCountEquals(0)
        awaitGone("Gifts & donations")
        runBlocking { kit.trailOf("category", id("gifts").value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `Back from Categories returns to More, and from More to Home`() {
        show()
        openCategories()

        compose.onNodeWithContentDescription("Back").performClick()
        awaitGone("Food & dining")
        compose.onNodeWithText("Categories").assertIsDisplayed()

        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        awaitText("Akash · Pixel")
        checkNotNull(activity).isFinishing shouldBe false
    }
}
