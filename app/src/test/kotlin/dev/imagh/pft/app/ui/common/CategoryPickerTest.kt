package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.CategoryId
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLog

/**
 * [CategoryPicker] fed by the real [dev.imagh.pft.ledger.category.CategoryService.pickable] over
 * in-memory Room (the consumer's ViewModel does the same): which categories are offered and in what
 * order, how a sub-category is told apart, what TalkBack hears, what a tap hands to `onPick`, the
 * selected cell, and the empty state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class CategoryPickerTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)

    private var open by mutableStateOf(true)
    private val picked = mutableListOf<CategoryId>()
    private var dismissals = 0

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun create(
        name: String,
        parent: Category? = null,
        kind: CategoryKind = CategoryKind.EXPENSE,
    ): Category =
        blocking {
            kit.categories.create(
                parent?.meta?.id,
                kind,
                CategoryDraft(name, CategoryColour.AMBER, CategoryIcon.Symbol("pets")),
            )
        }

    /**
     * Seeds a tree whose names are not in alphabetical order, so the order on screen can only be the
     * service's: Zoo (Cages, Feed), Apple (Pips), Mango, Plum. Then it deletes Feed, archives Mango and
     * archives Apple (its child Pips is hidden because its parent is). Returns what the consumer's
     * ViewModel would hand the picker: `pickable(EXPENSE)`, which is Zoo, Cages and Plum.
     */
    private fun seedAndLoadPickable(): List<Category> {
        blocking { kit.initialise() }
        val zoo = create("Zoo")
        create("Cages", zoo)
        val feed = create("Feed", zoo)
        val apple = create("Apple")
        create("Pips", apple)
        val mango = create("Mango")
        create("Plum")
        blocking {
            kit.categories.delete(feed.meta.id)
            kit.categories.archive(mango.meta.id)
            kit.categories.archive(apple.meta.id)
        }
        return blocking { kit.categories.pickable(CategoryKind.EXPENSE) }
    }

    private fun show(
        categories: List<Category>,
        selected: CategoryId? = null,
        topLevelOnly: Boolean = false,
    ) = compose.setContent {
        PftTheme(ThemePrefs(dynamicColor = false)) {
            if (open) {
                CategoryPicker(
                    categories = categories,
                    selected = selected,
                    onPick = { picked += it },
                    onDismiss = {
                        dismissals++
                        open = false
                    },
                    topLevelOnly = topLevelOnly,
                )
            }
        }
    }

    /** The name of each cell, left to right and top to bottom (the first text of its merged node). */
    private fun cellNames(): List<String> =
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config[SemanticsProperties.Text].first().text }

    @Test
    fun `offers only the pickable categories, in the service's order and not alphabetically`() {
        val pickable = seedAndLoadPickable()
        show(pickable)

        cellNames() shouldBe listOf("Zoo", "Cages", "Plum")
        cellNames() shouldBe pickable.map { it.name }
    }

    @Test
    fun `topLevelOnly hides every sub-category`() {
        show(seedAndLoadPickable(), topLevelOnly = true)

        cellNames() shouldBe listOf("Zoo", "Plum")
    }

    @Test
    fun `cells are one radio group, so TalkBack presents them as a set of choices`() {
        show(seedAndLoadPickable())

        compose
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .onChildren()
            .filter(isSelectable())
            .assertCountEquals(3)
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config.getOrNull(SemanticsProperties.Role) } shouldBe List(3) { Role.RadioButton }
    }

    @Test
    fun `a cell speaks its name once, and its badge says nothing`() {
        show(seedAndLoadPickable())

        val zoo = compose.onAllNodes(isSelectable()).fetchSemanticsNodes().first()
        zoo.config[SemanticsProperties.Text].map { it.text } shouldBe listOf("Zoo")
        zoo.config.getOrNull(SemanticsProperties.ContentDescription) shouldBe null
        // The badge inside the cell is its own node only in the unmerged tree: it has no description and is no image.
        val described =
            SemanticsMatcher("has a description or an image role") { node ->
                node.config.getOrNull(SemanticsProperties.ContentDescription) != null ||
                    node.config.getOrNull(SemanticsProperties.Role) == Role.Image
            }
        val inTheGroup = hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        compose.onAllNodes(described and inTheGroup, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `a sub-category says whose it is, so two with the same name can be told apart`() {
        blocking { kit.initialise() }
        val food = create("Food")
        val fun1 = create("Fun")
        create("Misc", food)
        create("Misc", fun1)
        show(blocking { kit.categories.pickable(CategoryKind.EXPENSE) })

        val spoken =
            compose
                .onAllNodes(isSelectable())
                .fetchSemanticsNodes()
                .map { node -> node.config[SemanticsProperties.Text].map { it.text } }
        spoken shouldBe
            listOf(
                listOf("Food"),
                listOf("Misc", "Under Food"),
                listOf("Fun"),
                listOf("Misc", "Under Fun"),
            )
    }

    @Test
    fun `tapping a category hands its id to onPick and dismisses the sheet`() {
        val pickable = seedAndLoadPickable()
        show(pickable)

        compose.onNodeWithText("Plum").performClick()
        compose.waitForIdle()

        picked shouldBe listOf(pickable.single { it.name == "Plum" }.meta.id)
        compose.onNodeWithText("Plum").assertDoesNotExist()
        dismissals shouldBe 1
    }

    @Test
    fun `a second tap while the sheet is closing picks nothing more`() {
        val pickable = seedAndLoadPickable()
        show(pickable)

        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Plum").performClick()
        compose.onNodeWithText("Zoo").performClick()
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()

        picked shouldBe listOf(pickable.single { it.name == "Plum" }.meta.id)
        dismissals shouldBe 1
    }

    @Test
    fun `Back closes the picker without picking`() {
        show(seedAndLoadPickable())

        Espresso.pressBack()
        compose.waitForIdle()

        compose.onNodeWithText("Plum").assertDoesNotExist()
        picked shouldBe emptyList()
        dismissals shouldBe 1
    }

    @Test
    fun `the current selection is marked selected and the others are not`() {
        val pickable = seedAndLoadPickable()
        show(pickable, selected = pickable.single { it.name == "Cages" }.meta.id)

        compose.onNodeWithText("Cages").assertIsSelected()
        compose.onNodeWithText("Zoo").assertIsNotSelected()
        compose.onNodeWithText("Plum").assertIsNotSelected()
    }

    @Test
    fun `a selection that is not offered marks nothing`() {
        val pickable = seedAndLoadPickable()
        val archived = blocking { kit.categories.lists(CategoryKind.EXPENSE) }.archived.first { it.name == "Mango" }
        show(pickable, selected = archived.meta.id)

        compose.onNodeWithText("Zoo").assertIsNotSelected()
        compose.onNodeWithText("Plum").assertIsNotSelected()
    }

    @Test
    fun `every cell is a touch target of at least 48dp`() {
        show(seedAndLoadPickable())

        val cells = compose.onAllNodes(isSelectable()).fetchSemanticsNodes()
        val minimum =
            48f *
                RuntimeEnvironment
                    .getApplication()
                    .resources.displayMetrics.density
        cells.forEach { node ->
            (node.boundsInRoot.width >= minimum) shouldBe true
            (node.boundsInRoot.height >= minimum) shouldBe true
        }
    }

    @Test
    fun `with no categories the sheet says so and offers nothing to pick`() {
        show(emptyList())

        compose.onNodeWithText("No categories to choose from yet").assertIsDisplayed()
        cellNames() shouldBe emptyList()
    }

    @Test
    fun `the picker logs nothing`() {
        show(seedAndLoadPickable())
        ShadowLog.clear()

        compose.onNodeWithText("Zoo").performClick()
        compose.waitForIdle()

        // The framework logs a line of its own while the sheet closes; only the app's lines (tag "pft") count.
        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg } shouldBe emptyList()
    }
}
