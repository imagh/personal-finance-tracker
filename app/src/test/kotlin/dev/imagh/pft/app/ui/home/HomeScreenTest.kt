package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
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
    @get:Rule
    val compose = HostedComposeRule()

    private fun show(
        state: HomeUiState,
        actions: @Composable RowScope.() -> Unit = {},
    ) = compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { HomeScreen(state, actions = actions) } }

    @Test
    fun `a loaded identity shows the title, the name and phone subtitle and the placeholder`() {
        show(HomeUiState(userName = "Akash", deviceLabel = "Pixel 8", loaded = true))

        compose.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty() shouldBe true
        compose.onNodeWithText("Akash · Pixel 8").assertIsDisplayed()
        compose.onNodeWithText("Accounts and transactions arrive in the next update").assertIsDisplayed()
    }

    @Test
    fun `before the identity is read there is no subtitle`() {
        show(HomeUiState())

        compose.onAllNodesWithText("·", substring = true).fetchSemanticsNodes().size shouldBe 0
        compose.onNodeWithText("Accounts and transactions arrive in the next update").assertIsDisplayed()
    }

    @Test
    fun `header actions given by the caller are drawn in the header`() {
        show(HomeUiState(loaded = true, userName = "A", deviceLabel = "B")) { Text("extra action") }

        compose.onNodeWithText("extra action").assertIsDisplayed()
    }
}
