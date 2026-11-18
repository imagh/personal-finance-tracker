package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The stateless Home placeholder, rendered from fake state: honest copy and the attribution subtitle. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HomeScreenTest {
    private companion object {
        const val PLACEHOLDER = "Open More to manage your accounts. Transactions arrive in the next update"
    }

    @get:Rule
    val compose = HostedComposeRule()

    private var moreOpened = 0

    private fun show(
        state: HomeUiState,
        actions: @Composable RowScope.() -> Unit = {},
    ) = compose.setContent {
        PftTheme(ThemePrefs(dynamicColor = false)) {
            HomeScreen(state, onOpenMore = { moreOpened++ }, actions = actions)
        }
    }

    @Test
    fun `a loaded identity shows the title, the name and phone subtitle and the placeholder`() {
        show(HomeUiState(userName = "Akash", deviceLabel = "Pixel 8", loaded = true))

        compose.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty() shouldBe true
        compose.onNodeWithText("Akash · Pixel 8").assertIsDisplayed()
        compose.onNodeWithText(PLACEHOLDER).assertIsDisplayed()
    }

    @Test
    fun `before the identity is read there is no subtitle`() {
        show(HomeUiState())

        compose.onAllNodesWithText("·", substring = true).fetchSemanticsNodes().size shouldBe 0
        compose.onNodeWithText(PLACEHOLDER).assertIsDisplayed()
    }

    @Test
    fun `the More header button is labelled for TalkBack, is a button and opens More`() {
        show(HomeUiState(loaded = true, userName = "A", deviceLabel = "B"))

        compose
            .onNodeWithContentDescription("More")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        moreOpened shouldBe 1
    }

    @Test
    fun `the More button is there before the identity has loaded`() {
        show(HomeUiState())

        compose.onNodeWithContentDescription("More").assertIsDisplayed()
    }

    @Test
    fun `header actions given by the caller are drawn in the header`() {
        show(HomeUiState(loaded = true, userName = "A", deviceLabel = "B")) { Text("extra action") }

        compose.onNodeWithText("extra action").assertIsDisplayed()
    }
}
