package dev.imagh.pft.app.ui.common

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `EntityListScreen` (plan §3.2 D11, step B2a): active rows in order, a collapsed "Archived (n)"
 * section, the empty state with its action, the add FAB, each row's overflow menu and the indent. It
 * renders fake rows through the real scaffold; what is asserted is what is on screen and what a tap
 * does to the consumer's state, never that a callback was reached.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class EntityListScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f

    /** What the consumer saw: the labels of the actions invoked, and the Back and add taps. */
    private val invoked = mutableListOf<String>()

    private fun row(
        title: String,
        actions: List<String> = emptyList(),
        indent: Boolean = false,
    ) = EntityRow(
        key = title,
        title = title,
        supporting = "About $title",
        indent = indent,
        actions = actions.map { label -> RowAction(label) { invoked += "$label $title" } },
    )

    private fun show(
        active: List<EntityRow> = emptyList(),
        archived: List<EntityRow> = emptyList(),
        add: RowAction? = RowAction("Add account") { invoked += "add" },
        loaded: Boolean = true,
    ) {
        compose.setContent {
            density = LocalDensity.current.density
            val snackbars = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EntityListScreen(
                    title = "Accounts",
                    active = active,
                    archived = archived,
                    emptyMessage = "No accounts yet",
                    snackbarHost = { SnackbarHost(snackbars) },
                    onBack = { invoked += "back" },
                    add = add,
                    loaded = loaded,
                )
            }
        }
        compose.waitForIdle()
    }

    private fun SemanticsNodeInteraction.bounds(): Rect = fetchSemanticsNode().boundsInRoot

    private fun menuItems(): List<String> =
        compose
            .onAllNodes(hasAnyAncestor(isPopup()) and hasClickAction())
            .fetchSemanticsNodes()
            .map { node ->
                node.config
                    .getOrNull(SemanticsProperties.Text)
                    .orEmpty()
                    .joinToString("") { it.text }
            }

    @Test
    fun `active rows render in the order given, each with its title and supporting text`() {
        show(active = listOf(row("Everyday"), row("Savings"), row("Wallet")))

        val tops = listOf("Everyday", "Savings", "Wallet").map { compose.onNodeWithText(it).bounds().top }
        assertTrue("rows are not in order: $tops", tops == tops.sorted() && tops.toSet().size == 3)
        compose.onNodeWithText("About Savings").assertIsDisplayed()
    }

    @Test
    fun `archived rows sit under a collapsed Archived header that expands on tap and collapses again`() {
        show(active = listOf(row("Everyday")), archived = listOf(row("Old card"), row("Old wallet")))

        compose.onNodeWithText("Archived (2)").assertIsDisplayed()
        compose.onAllNodesWithText("Old card").assertCountEquals(0)

        compose.onNodeWithText("Archived (2)").performClick()
        compose.onNodeWithText("Old card").assertIsDisplayed()
        compose.onNodeWithText("Old wallet").assertIsDisplayed()
        val headerTop = compose.onNodeWithText("Archived (2)").bounds().top
        assertTrue(compose.onNodeWithText("Old card").bounds().top > headerTop)

        compose.onNodeWithText("Archived (2)").performClick()
        compose.onAllNodesWithText("Old card").assertCountEquals(0)
    }

    @Test
    fun `the Archived header says whether it is expanded`() {
        show(archived = listOf(row("Old card")))

        fun state() =
            compose
                .onNodeWithText("Archived (1)")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.StateDescription)

        state() shouldBe "Collapsed"
        compose.onNodeWithText("Archived (1)").performClick()
        state() shouldBe "Expanded"
    }

    @Test
    fun `while not loaded, neither the rows nor the empty state show, but the header and Back do`() {
        show(active = listOf(row("Everyday")), archived = listOf(row("Old card")), loaded = false)

        compose.onAllNodesWithText("Everyday").assertCountEquals(0)
        compose.onAllNodesWithText("Archived", substring = true).assertCountEquals(0)
        compose.onAllNodesWithText("No accounts yet").assertCountEquals(0)
        compose.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun `while not loaded, an empty list does not flash the empty message`() {
        show(loaded = false)

        compose.onAllNodesWithText("No accounts yet").assertCountEquals(0)
        compose.onAllNodesWithText("Add account").assertCountEquals(0) // the empty state's button
    }

    @Test
    fun `with archived rows only there is no empty state, just the header`() {
        show(archived = listOf(row("Old card")))

        compose.onAllNodesWithText("No accounts yet").assertCountEquals(0)
        compose.onNodeWithText("Archived (1)").assertIsDisplayed()
    }

    @Test
    fun `with no rows at all the empty message and its action show, and the action adds`() {
        show()

        compose.onNodeWithText("No accounts yet").assertIsDisplayed()
        compose.onNodeWithText("Add account").performClick()
        invoked shouldBe listOf("add")
        compose.onAllNodesWithText("Archived", substring = true).assertCountEquals(0)
    }

    @Test
    fun `the floating action button is labelled with the add label and adds`() {
        show(active = listOf(row("Everyday")))

        compose.onNodeWithContentDescription("Add account").assertIsDisplayed().performClick()
        invoked shouldBe listOf("add")
    }

    @Test
    fun `without an add action there is no floating action button and the empty state has no button`() {
        show(add = null)

        compose.onNodeWithText("No accounts yet").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Add account").assertCountEquals(0)
        compose.onAllNodesWithText("Add account").assertCountEquals(0)
    }

    @Test
    fun `Back is a labelled header button and goes back`() {
        show()

        compose.onNodeWithContentDescription("Back").performClick()
        invoked shouldBe listOf("back")
    }

    @Test
    fun `a row's overflow menu lists exactly its actions and invokes the chosen one`() {
        show(active = listOf(row("Everyday", actions = listOf("Archive", "Delete")), row("Savings")))

        compose.onNodeWithContentDescription("Actions for Everyday").performClick()
        menuItems() shouldBe listOf("Archive", "Delete")

        compose.onNodeWithText("Delete").performClick()
        invoked shouldBe listOf("Delete Everyday")
        menuItems() shouldBe emptyList()
    }

    @Test
    fun `a row with no actions has no overflow button`() {
        show(active = listOf(row("Everyday", actions = listOf("Archive")), row("Savings")))

        compose.onAllNodesWithContentDescription("Actions for Everyday").assertCountEquals(1)
        compose.onAllNodesWithContentDescription("Actions for Savings").assertCountEquals(0)
    }

    @Test
    fun `an archived row's menu works after the section is expanded`() {
        show(archived = listOf(row("Old card", actions = listOf("Unarchive"))))

        compose.onNodeWithText("Archived (1)").performClick()
        compose.onNodeWithContentDescription("Actions for Old card").performClick()
        compose.onNodeWithText("Unarchive").performClick()
        invoked shouldBe listOf("Unarchive Old card")
    }

    @Test
    fun `an indented row starts further from the start edge than its parent by the indent step`() {
        show(active = listOf(row("Food"), row("Groceries", indent = true)))

        // The title text's own node: a row's merged node spans the whole row, padding included.
        fun titleLeft(title: String) = compose.onNodeWithText(title, useUnmergedTree = true).bounds().left
        val step = titleLeft("Groceries") - titleLeft("Food")
        // The indent is the 24dp spacing step.
        assertTrue("indent was ${step / density}dp", Math.abs(step - 24f * density) < 1f)
    }

    @Test
    fun `a row's glyph is described by the row's own label, not by repeating the title`() {
        compose.setContent {
            val snackbars = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EntityListScreen(
                    title = "Accounts",
                    active =
                        listOf(
                            EntityRow(
                                key = "a",
                                title = "Everyday",
                                glyph = RowGlyph(GlyphSource.Symbol(PftSymbol.ACCOUNT_BALANCE), "blue", "Bank"),
                            ),
                        ),
                    archived = emptyList(),
                    emptyMessage = "No accounts yet",
                    snackbarHost = { SnackbarHost(snackbars) },
                    onBack = {},
                )
            }
        }

        compose.onAllNodesWithContentDescription("Bank").assertCountEquals(1)
        compose.onNodeWithText("Everyday").assertIsDisplayed()
    }
}
