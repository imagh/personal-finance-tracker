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
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.TagLists
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
 * [TagPicker] fed by the real [dev.imagh.pft.ledger.tag.TagService.lists] over in-memory Room (the
 * consumer's ViewModel does the same): which tags are offered and in what order, the archived-but-chosen
 * rule, what TalkBack hears, how toggling marks cells, what Done hands to `onDone`, and the empty state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TagPickerTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)

    private var open by mutableStateOf(true)
    private val done = mutableListOf<Set<TagId>>()
    private var dismissals = 0

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    /**
     * Seeds tags whose names do not sort like their creation order: Zoo, travel, Bills, Old trip, Gone. Then
     * it archives Old trip and deletes Gone, and returns what the consumer's ViewModel would hand the
     * picker: `lists()`, with Bills, travel and Zoo active (ignoring case) and Old trip archived.
     */
    private fun seedAndLoad(): TagLists {
        blocking { kit.initialise() }
        val names = listOf("Zoo", "travel", "Bills", "Old trip", "Gone")
        val tags = names.associateWith { blocking { kit.tags.create(it) } }
        blocking {
            kit.tags.archive(tags.getValue("Old trip").meta.id)
            kit.tags.delete(tags.getValue("Gone").meta.id)
        }
        return blocking { kit.tags.lists() }
    }

    private fun TagLists.id(name: String): TagId = (active + archived).single { it.name == name }.meta.id

    private fun show(
        tags: TagLists,
        selected: Set<TagId> = emptySet(),
    ) = compose.setContent {
        PftTheme(ThemePrefs(dynamicColor = false)) {
            if (open) {
                TagPicker(
                    tags = tags,
                    selected = selected,
                    onDone = { done += it },
                    onDismiss = {
                        dismissals++
                        open = false
                    },
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
    fun `offers the active tags in the service's order, ignoring case, and neither archived nor deleted ones`() {
        show(seedAndLoad())

        cellNames() shouldBe listOf("Bills", "travel", "Zoo")
    }

    @Test
    fun `cells are one group of checkboxes, so TalkBack presents them as a set of choices`() {
        show(seedAndLoad())

        compose
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .onChildren()
            .filter(isSelectable())
            .assertCountEquals(3)
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config.getOrNull(SemanticsProperties.Role) } shouldBe List(3) { Role.Checkbox }
    }

    @Test
    fun `a cell speaks its name once, and its badge says nothing`() {
        show(seedAndLoad())

        val bills = compose.onAllNodes(isSelectable()).fetchSemanticsNodes().first()
        bills.config[SemanticsProperties.Text].map { it.text } shouldBe listOf("Bills")
        bills.config.getOrNull(SemanticsProperties.ContentDescription) shouldBe null
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
    fun `the tags selected on entry are marked selected and the others are not`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("travel")))

        compose.onNodeWithText("travel").assertIsSelected()
        compose.onNodeWithText("Bills").assertIsNotSelected()
        compose.onNodeWithText("Zoo").assertIsNotSelected()
    }

    @Test
    fun `tapping a cell toggles it, and a second tap clears it, without closing the sheet`() {
        show(seedAndLoad())

        compose.onNodeWithText("Bills").performClick()
        compose.onNodeWithText("Bills").assertIsSelected()
        compose.onNodeWithText("Zoo").performClick()
        compose.onNodeWithText("Zoo").assertIsSelected()
        compose.onNodeWithText("Bills").performClick()

        compose.onNodeWithText("Bills").assertIsNotSelected()
        compose.onNodeWithText("Zoo").assertIsSelected()
        compose.onNodeWithText("travel").assertIsNotSelected()
        done shouldBe emptyList()
        dismissals shouldBe 0
    }

    @Test
    fun `Done hands over exactly the chosen ids, once, and dismisses the sheet`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("travel")))

        compose.onNodeWithText("Zoo").performClick()
        compose.onNodeWithText("travel").performClick()
        compose.onNodeWithText("Bills").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        done shouldBe listOf(setOf(tags.id("Zoo"), tags.id("Bills")))
        dismissals shouldBe 1
        compose.onNodeWithText("Zoo").assertDoesNotExist()
    }

    @Test
    fun `Done with nothing changed hands back the selection it was given`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("Bills"), tags.id("Zoo")))

        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        done shouldBe listOf(setOf(tags.id("Bills"), tags.id("Zoo")))
    }

    @Test
    fun `clearing every tag is a valid choice, and Done hands back the empty set`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("Bills")))

        compose.onNodeWithText("Bills").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        done shouldBe listOf(emptySet())
    }

    @Test
    fun `a second tap on Done while the sheet is closing hands over nothing more`() {
        val tags = seedAndLoad()
        show(tags)
        compose.onNodeWithText("Bills").performClick()

        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Done").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()

        done shouldBe listOf(setOf(tags.id("Bills")))
        dismissals shouldBe 1
    }

    @Test
    fun `Back closes the picker without handing anything over`() {
        show(seedAndLoad())
        compose.onNodeWithText("Bills").performClick()

        Espresso.pressBack()
        compose.waitForIdle()

        compose.onNodeWithText("Zoo").assertDoesNotExist()
        done shouldBe emptyList()
        dismissals shouldBe 1
    }

    // --- The archived-but-chosen rule (C4 must not silently drop a tag an item already carries)

    @Test
    fun `an archived tag that is already selected is shown, selected and marked Archived`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("Old trip")))

        cellNames() shouldBe listOf("Bills", "travel", "Zoo", "Old trip")
        compose.onNodeWithText("Old trip").assertIsSelected()
        compose.onNodeWithText("Archived").assertIsDisplayed()
    }

    @Test
    fun `an archived tag that is not selected stays out, and so is no Archived label drawn`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("Bills")))

        compose.onNodeWithText("Old trip").assertDoesNotExist()
        compose.onNodeWithText("Archived").assertDoesNotExist()
    }

    @Test
    fun `Done keeps a selected archived tag unless the user deselects it`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("Old trip")))

        compose.onNodeWithText("Bills").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        done shouldBe listOf(setOf(tags.id("Old trip"), tags.id("Bills")))
    }

    @Test
    fun `a selected archived tag can be deselected, and then Done drops it`() {
        val tags = seedAndLoad()
        show(tags, selected = setOf(tags.id("Old trip")))

        compose.onNodeWithText("Old trip").performClick()
        compose.onNodeWithText("Old trip").assertIsNotSelected()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        done shouldBe listOf(emptySet())
    }

    @Test
    fun `a selected id that is no tag any more is not drawn but is handed back untouched`() {
        val tags = seedAndLoad()
        val gone = TagId("deleted-since")
        show(tags, selected = setOf(gone, tags.id("Bills")))

        cellNames() shouldBe listOf("Bills", "travel", "Zoo")
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        done shouldBe listOf(setOf(gone, tags.id("Bills")))
    }

    // --- Layout and the empty state

    @Test
    fun `every cell and Done is a touch target of at least 48dp`() {
        show(seedAndLoad())

        val cells = compose.onAllNodes(isSelectable()).fetchSemanticsNodes()
        val done = compose.onNodeWithText("Done").fetchSemanticsNode()
        val minimum =
            48f *
                RuntimeEnvironment
                    .getApplication()
                    .resources.displayMetrics.density
        (cells + done).forEach { node ->
            (node.boundsInRoot.width >= minimum) shouldBe true
            (node.boundsInRoot.height >= minimum) shouldBe true
        }
    }

    @Test
    fun `the title is a heading`() {
        show(seedAndLoad())

        compose
            .onNodeWithText("Choose tags")
            .fetchSemanticsNode()
            .config
            .contains(SemanticsProperties.Heading) shouldBe true
    }

    @Test
    fun `with no tags the sheet says so and offers nothing to pick or confirm`() {
        show(TagLists(emptyList(), emptyList()))

        compose.onNodeWithText("No tags yet. Add one from More.").assertIsDisplayed()
        cellNames() shouldBe emptyList()
        compose.onNodeWithText("Done").assertDoesNotExist()
    }

    @Test
    fun `the picker logs nothing`() {
        show(seedAndLoad())
        ShadowLog.clear()

        compose.onNodeWithText("Zoo").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        // The framework logs a line of its own while the sheet closes; only the app's lines (tag "pft") count.
        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg } shouldBe emptyList()
    }
}
