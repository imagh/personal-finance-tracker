package dev.imagh.pft.app.ui.tags

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.ids.TagId
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [TagsScreen] drawn from fake state: no empty message before the first read, the row menus (Archive and
 * Delete for an active tag, Unarchive and Delete for an archived one, and never Move up or Move down, since
 * tags have no order), what each entry and the add button and a row tap report.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1100dp-xxhdpi")
class TagsScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val calls = mutableListOf<String>()

    private fun show(state: TagsUiState) =
        compose.setContent {
            val snackbars = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                TagsScreen(
                    state = state,
                    actions =
                        TagsActions(
                            archive = { calls += "archive ${it.value}" },
                            unarchive = { calls += "unarchive ${it.value}" },
                            delete = { calls += "delete ${it.value}" },
                            add = { calls += "add" },
                            edit = { calls += "edit ${it.value}" },
                        ),
                    snackbars = snackbars,
                    onBack = {},
                )
            }
        }

    @Test
    fun `before the first load the empty message does not show, and once loaded and empty it does`() {
        show(TagsUiState(loaded = false))
        compose.onAllNodesWithText("No tags yet").assertCountEquals(0)
    }

    @Test
    fun `once loaded with no tags the empty message and an Add tag button show`() {
        show(TagsUiState(loaded = true))

        compose.onNodeWithText("No tags yet").assertIsDisplayed()
        compose.onNodeWithText("Add tag").performClick()
        calls shouldBe listOf("add")
    }

    @Test
    fun `an active tag's menu offers Archive and Delete, and no Move`() {
        show(sampleTagsState())

        compose.onNodeWithContentDescription("Actions for Travel").performClick()

        compose.onNodeWithText("Archive").assertIsDisplayed()
        compose.onNodeWithText("Delete").assertIsDisplayed()
        compose.onAllNodesWithText("Unarchive").assertCountEquals(0)
        compose.onAllNodesWithText("Move up").assertCountEquals(0)
        compose.onAllNodesWithText("Move down").assertCountEquals(0)

        compose.onNodeWithText("Archive").performClick()
        calls shouldBe listOf("archive travel")
    }

    @Test
    fun `an archived tag's menu offers Unarchive and Delete`() {
        show(sampleTagsState())
        compose.onNodeWithText("Archived (1)").performClick()

        compose.onNodeWithContentDescription("Actions for Old trip").performClick()

        compose.onNodeWithText("Unarchive").assertIsDisplayed()
        compose.onNodeWithText("Delete").assertIsDisplayed()
        compose.onAllNodesWithText("Archive").assertCountEquals(0)

        compose.onNodeWithText("Delete").performClick()
        calls shouldBe listOf("delete old-trip")
    }

    @Test
    fun `tapping a row, active or archived, opens its form`() {
        show(sampleTagsState())

        compose.onNodeWithText("Bills").performClick()
        compose.onNodeWithText("Archived (1)").performClick()
        compose.onNodeWithText("Old trip").performClick()

        calls shouldBe listOf("edit ${TagId("bills").value}", "edit old-trip")
    }
}
