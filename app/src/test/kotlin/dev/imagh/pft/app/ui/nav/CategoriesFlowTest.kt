package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

    @Test
    fun `there is no add control until the form exists`() {
        show()
        openCategories()

        compose.onAllNodesWithContentDescription("Add category").assertCountEquals(0)
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
