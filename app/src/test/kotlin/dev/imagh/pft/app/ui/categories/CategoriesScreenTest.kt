package dev.imagh.pft.app.ui.categories

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.CategoryId
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [CategoriesScreen] drawn from a state alone: the Expense/Income switch, the rows with their menus,
 * the Archived section and the empty state. It renders [sampleCategoriesState] through the real
 * scaffold and asserts what is on screen and what a tap leads to, never that a callback ran.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1400dp-xxhdpi")
class CategoriesScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    /** What the user asked for, as the ViewModel would receive it. */
    private val asked = mutableListOf<String>()
    private var state by mutableStateOf(sampleCategoriesState())

    private fun show(shown: CategoriesUiState = state) {
        state = shown
        compose.setContent {
            val snackbars = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                CategoriesScreen(
                    state = state,
                    actions =
                        CategoriesActions(
                            choose = { id: CategoryId, action -> asked += "${action.name} ${id.value}" },
                            selectKind = { asked += "kind ${it.name}" },
                            add = { asked += "add ${it.name}" },
                            edit = { asked += "edit ${it.value}" },
                        ),
                    snackbars = snackbars,
                    onBack = { asked += "back" },
                )
            }
        }
        compose.waitForIdle()
    }

    private fun menuLabels(row: String): List<String> {
        compose.onNodeWithContentDescription("Actions for $row").performClick()
        return compose
            .onAllNodes(hasAnyAncestor(isPopup()) and hasClickAction())
            .fetchSemanticsNodes()
            .map { node ->
                node.config[androidx.compose.ui.semantics.SemanticsProperties.Text].joinToString("") { it.text }
            }
    }

    @Test
    fun `Expense is selected and Income is not, and choosing Income asks for it`() {
        show()

        compose.onNodeWithText("Expense").assertIsSelected()
        compose.onNodeWithText("Income").assertIsNotSelected().performClick()

        asked shouldBe listOf("kind INCOME")
    }

    @Test
    fun `the switch follows the state`() {
        show(sampleCategoriesState().copy(kind = CategoryKind.INCOME))

        compose.onNodeWithText("Income").assertIsSelected()
        compose.onNodeWithText("Expense").assertIsNotSelected()
    }

    @Test
    fun `the switch shows before the first load, with no rows and no empty message`() {
        show(CategoriesUiState())

        compose.onNodeWithText("Expense").assertIsDisplayed()
        compose.onAllNodesWithText("No categories yet").assertCountEquals(0)
    }

    @Test
    fun `parents and their sub-categories show in order, with the sub-categories inset`() {
        show()

        val tops =
            listOf("Food & dining", "Groceries", "Eating out", "Transport", "Pets", "Miscellaneous", "Loan EMI")
                .map {
                    compose
                        .onNodeWithText(it)
                        .fetchSemanticsNode()
                        .boundsInRoot.top
                }
        (tops == tops.sorted() && tops.toSet().size == tops.size) shouldBe true
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
    }

    @Test
    fun `each row's menu lists exactly what its item offers, in order`() {
        show()

        menuLabels("Food & dining") shouldBe listOf("Move down", "Archive", "Delete")
    }

    @Test
    fun `Loan EMI's menu has no Archive and no Delete`() {
        show()

        menuLabels("Loan EMI") shouldBe listOf("Move up")
    }

    @Test
    fun `choosing Move down from a row's menu asks for that move on that category`() {
        show()

        compose.onNodeWithContentDescription("Actions for Transport").performClick()
        compose.onNodeWithText("Move down").performClick()

        asked shouldBe listOf("MOVE_DOWN transport")
    }

    private fun scrollTo(text: String) = compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))

    @Test
    fun `archived rows sit under a collapsed header, and a sub-category listed there names its parent`() {
        show()

        scrollTo("Archived (2)")
        compose.onNodeWithText("Archived (2)").assertIsDisplayed()
        compose.onAllNodesWithText("Shopping").assertCountEquals(0)

        compose.onNodeWithText("Archived (2)").performClick()
        scrollTo("Shopping")
        compose.onNodeWithText("Shopping").assertIsDisplayed()
        scrollTo("Food delivery")
        compose.onNodeWithText("Food delivery").assertIsDisplayed()
        compose.onNodeWithText("Under Food & dining").assertIsDisplayed()
        menuLabels("Shopping") shouldBe listOf("Unarchive", "Delete")
    }

    @Test
    fun `a category row speaks its name once, whatever its icon, because the badge is decorative`() {
        show()

        listOf("Food & dining", "Pets", "Miscellaneous", "Loan EMI").forEach { name ->
            // Merged tree (what TalkBack reads): the row's own text and descriptions hold the name once.
            val row = compose.onNode(hasText(name) and !hasClickAction()).fetchSemanticsNode().config
            val spoken =
                row.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } +
                    row.getOrNull(SemanticsProperties.ContentDescription).orEmpty()
            assertEquals(name, listOf(name), spoken.filter { it == name })
            // Unmerged tree: no badge carries the name as a description or as an image.
            compose.onAllNodesWithContentDescription(name, useUnmergedTree = true).assertCountEquals(0)
        }
        // The only images left are the header's Back and each row's overflow button, never a badge.
        val images =
            compose
                .onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .flatMap { it.config.getOrNull(SemanticsProperties.ContentDescription).orEmpty() }
        assertEquals(
            emptyList<String>(),
            images.filterNot {
                it == "Back" ||
                    it == "Add category" ||
                    it.startsWith("Actions for ")
            },
        )
    }

    @Test
    fun `once loaded with nothing at all, it says there are no categories and offers Add category`() {
        show(CategoriesUiState(loaded = true))

        compose.onNodeWithText("No categories yet").assertIsDisplayed()
        compose.onNodeWithContentDescription("Add category").assertIsDisplayed()
        compose.onNodeWithText("Add category").performClick()

        asked shouldBe listOf("add EXPENSE")
    }

    @Test
    fun `the add button asks to add a category of the kind on show`() {
        show(sampleCategoriesState().copy(kind = CategoryKind.INCOME))

        compose.onNodeWithContentDescription("Add category").performClick()

        asked shouldBe listOf("add INCOME")
    }

    @Test
    fun `the add button is there before the first load finishes, with no empty message`() {
        show(CategoriesUiState())

        compose.onNodeWithContentDescription("Add category").assertIsDisplayed()
        compose.onAllNodesWithText("No categories yet").assertCountEquals(0)
    }

    @Test
    fun `tapping a row, active or archived, asks to edit that category`() {
        show()

        compose.onNodeWithText("Groceries").performClick()
        compose.onNodeWithText("Archived (2)").performClick()
        compose.onNodeWithText("Shopping").performClick()

        asked shouldBe listOf("edit groceries", "edit shopping")
    }

    @Test
    fun `Back is a labelled header button and goes back`() {
        show()

        compose.onNodeWithContentDescription("Back").performClick()

        asked shouldBe listOf("back")
    }
}
