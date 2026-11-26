package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
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

/**
 * The stateless Home placeholder, rendered from fake state: honest copy and a header with no attribution
 * line (that is the More tab's subtitle now) and no More button (the tab bar is how More is reached).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HomeScreenTest {
    private companion object {
        const val PLACEHOLDER = "Manage your accounts from the Accounts tab. Transactions arrive in the next update"
    }

    @get:Rule
    val compose = HostedComposeRule()

    private fun show(actions: @Composable RowScope.() -> Unit = {}) =
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                HomeScreen(actions = actions)
            }
        }

    @Test
    fun `it shows the title and the placeholder, and no attribution line`() {
        show()

        compose.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty() shouldBe true
        compose.onNodeWithText(PLACEHOLDER).assertIsDisplayed()
        compose.onAllNodesWithText("·", substring = true).fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `there is no More button in the header, because More is a tab`() {
        show()

        compose.onAllNodesWithContentDescription("More").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `header actions given by the caller are drawn in the header`() {
        show { Text("extra action") }

        compose.onNodeWithText("extra action").assertIsDisplayed()
    }
}
