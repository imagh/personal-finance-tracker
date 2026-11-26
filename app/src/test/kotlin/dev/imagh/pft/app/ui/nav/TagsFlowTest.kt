package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
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
import dev.imagh.pft.ledger.ids.TagId
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
 * The tags screens end to end (PRD L-3, INT-3): the real [AppContainer] over a real Room database,
 * rendered through [PftRoot] and the navigation host, from Home to More to Tags, to the form and back. It
 * asserts what is on screen and what is persisted, never that anything was called.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TagsFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private var activity: ComponentActivity? = null

    @After
    fun closeDatabase() = database.close()

    /** Sets up the identity (Akash on Pixel) so the app starts on Home, then shows the whole app. */
    private fun show(vararg names: String): List<TagId> {
        val ids =
            runBlocking {
                kit.initialise()
                names.map {
                    kit.tags
                        .create(it)
                        .meta.id
                }
            }
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        awaitText(HOME_PLACEHOLDER)
        return ids
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

    private fun openTags() {
        compose.tab("More").performClick()
        awaitText("Tags")
        compose.onNodeWithText("Tags").performClick()
    }

    private fun openAddForm() {
        compose.onNodeWithContentDescription("Add tag").performClick()
        awaitText("New tag")
    }

    private fun chooseFromMenu(
        tag: String,
        action: String,
    ) {
        compose.onNodeWithContentDescription("Actions for $tag").performClick()
        compose.onNodeWithText(action).performClick()
    }

    private fun save() = compose.onNodeWithText("Save").performClick()

    private fun systemBack() {
        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    private fun stored() = runBlocking { kit.tags.lists() }.let { it.active + it.archived }

    @Test
    fun `Home to More to Tags shows the empty state with an Add tag control`() {
        show()

        openTags()
        awaitText("No tags yet")

        compose.onNodeWithText("No tags yet").assertIsDisplayed()
        compose.onNodeWithContentDescription("Add tag").assertIsDisplayed()
        compose.onNodeWithText("Add tag").assertIsDisplayed()
    }

    @Test
    fun `create Travel, rename it to Trips, then archive it`() {
        show()
        openTags()
        awaitText("No tags yet")

        openAddForm()
        compose.onNodeWithText("Name").performTextInput("Travel")
        save()
        awaitGone("New tag")
        awaitText("Travel")

        compose.onAllNodesWithText("No tags yet").fetchSemanticsNodes().size shouldBe 0
        val id = stored().single().meta.id
        stored().single().name shouldBe "Travel"

        compose.onNodeWithText("Travel").performClick()
        awaitText("Edit tag")
        compose.onNode(hasSetTextAction() and hasText("Travel")).performTextReplacement("Trips")
        save()
        awaitGone("Edit tag")
        awaitText("Trips")

        compose.onAllNodesWithText("Travel").fetchSemanticsNodes().size shouldBe 0
        stored().single().name shouldBe "Trips"

        chooseFromMenu("Trips", "Archive")
        awaitText("Archived (1)")

        compose.onAllNodesWithText("Trips").fetchSemanticsNodes().size shouldBe 0
        runBlocking { kit.tags.lists() }.archived.map { it.meta.id } shouldBe listOf(id)
        runBlocking { kit.trailOf("tag", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.UPDATE, AuditAction.ARCHIVE)
    }

    @Test
    fun `an archived tag can be expanded and unarchived`() {
        val (id) = show("Travel")
        openTags()
        awaitText("Travel")
        chooseFromMenu("Travel", "Archive")
        awaitText("Archived (1)")

        compose.onNodeWithText("Archived (1)").performClick()
        awaitText("Travel")
        chooseFromMenu("Travel", "Unarchive")
        awaitGone("Archived (1)")

        compose.onNodeWithText("Travel").assertIsDisplayed()
        runBlocking { kit.tags.lists() }.active.map { it.meta.id } shouldBe listOf(id)
    }

    @Test
    fun `a tag row has no badge description, so its name is spoken once`() {
        show("Travel")

        openTags()
        awaitText("Travel")

        compose.onAllNodesWithText("Travel").fetchSemanticsNodes().size shouldBe 1
        compose.onAllNodesWithContentDescription("Tag").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `delete shows an Undo snackbar, and Undo brings the tag back`() {
        val (id) = show("Travel")
        openTags()
        awaitText("Travel")

        chooseFromMenu("Travel", "Delete")
        awaitText("Tag deleted")

        compose.onNodeWithText("Undo").assertIsDisplayed()
        stored() shouldBe emptyList()

        compose.onNodeWithText("Undo").performClick()
        awaitText("Travel")

        awaitGone("Tag deleted")
        stored().map { it.meta.id } shouldBe listOf(id)
        runBlocking { kit.trailOf("tag", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
    }

    @Test
    fun `a duplicate name shows under the name field and keeps the form open`() {
        show("Travel")
        openTags()
        awaitText("Travel")
        openAddForm()

        compose.onNodeWithText("Name").performTextInput(" travel ")
        save()
        awaitText("Already in use")

        compose.onNodeWithText("Already in use").assertIsDisplayed()
        compose.onNodeWithText("New tag").assertIsDisplayed()
        stored().size shouldBe 1
    }

    @Test
    fun `a blank name shows its error and saves nothing`() {
        show()
        openTags()
        awaitText("No tags yet")
        openAddForm()

        save()
        awaitText("Required")

        stored() shouldBe emptyList()
    }

    @Test
    fun `Back from the form returns to the list without saving`() {
        show("Travel")
        openTags()
        awaitText("Travel")
        compose.onNodeWithText("Travel").performClick()
        awaitText("Edit tag")
        compose.onNode(hasSetTextAction() and hasText("Travel")).performTextReplacement("Changed")

        compose.onNodeWithContentDescription("Back").performClick()
        awaitGone("Edit tag")

        compose.onNodeWithText("Travel").assertIsDisplayed()
        stored().single().name shouldBe "Travel"
    }

    @Test
    fun `a tag deleted elsewhere while the list is shown returns to the list with a no longer exists message`() {
        val (id) = show("Travel")
        openTags()
        awaitText("Travel")
        runBlocking { kit.tags.delete(id) }

        compose.onNodeWithText("Travel").performClick()
        awaitText("That tag no longer exists")

        compose.onAllNodesWithText("Edit tag").fetchSemanticsNodes().size shouldBe 0
        awaitText("No tags yet")
        runBlocking { kit.trailOf("tag", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `Back from Tags returns to More, and from More to Home`() {
        show()
        openTags()
        awaitText("No tags yet")

        compose.onNodeWithContentDescription("Back").performClick()
        awaitGone("No tags yet")
        compose.onNodeWithText("Tags").assertIsDisplayed()

        systemBack()
        awaitText(HOME_PLACEHOLDER)
        checkNotNull(activity).isFinishing shouldBe false
    }
}
