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
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/**
 * The stateless Home screen, rendered from fake state: the greeting header (time-of-day word and the
 * user's name, nothing before the profile loads, no bell or gear, a decorative avatar), the caller's
 * header actions, and the placeholder line below.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HomeScreenTest {
    private companion object {
        const val PLACEHOLDER = "Manage your accounts from the Accounts tab. Transactions arrive in the next update"
        val MORNING: Clock = Clock.fixed(Instant.parse("2026-11-26T08:00:00Z"), ZoneId.of("UTC"))
        val EVENING: Clock = Clock.fixed(Instant.parse("2026-11-26T20:00:00Z"), ZoneId.of("UTC"))
    }

    @get:Rule
    val compose = HostedComposeRule()

    private fun show(
        state: HomeUiState = HomeUiState(userName = "Akash Gupta", loaded = true),
        clock: Clock = MORNING,
        actions: @Composable RowScope.() -> Unit = {},
    ) = compose.setContent {
        PftTheme(ThemePrefs(dynamicColor = false)) {
            HomeScreen(state, clock = clock, actions = actions)
        }
    }

    private fun count(text: String) = compose.onAllNodesWithText(text).fetchSemanticsNodes().size

    @Test
    fun `once loaded it shows the greeting and the name, each once, and the placeholder`() {
        show()

        compose.onNodeWithText("Good morning").assertIsDisplayed()
        compose.onNodeWithText("Akash Gupta").assertIsDisplayed()
        count("Good morning") shouldBe 1
        count("Akash Gupta") shouldBe 1
        compose.onNodeWithText(PLACEHOLDER).assertIsDisplayed()
    }

    @Test
    fun `the greeting follows the injected clock`() {
        show(clock = EVENING)

        compose.onNodeWithText("Good evening").assertIsDisplayed()
        count("Good morning") shouldBe 0
    }

    @Test
    fun `before the profile loads nothing of the header shows, not even the greeting or initials`() {
        show(HomeUiState())

        count("Good morning") shouldBe 0
        count("AG") shouldBe 0
        count("Home") shouldBe 0
        compose.onNodeWithText(PLACEHOLDER).assertIsDisplayed()
    }

    @Test
    fun `the avatar initials are decorative, so the name is the only spoken label`() {
        show()

        compose.onNodeWithText("AG", useUnmergedTree = true).assertIsDisplayed()
        // The merged tree has no node with the initials: the avatar is cleared from semantics.
        count("AG") shouldBe 0
    }

    @Test
    fun `there is no bell, no gear, no More button and no attribution line`() {
        show()

        listOf("Notifications", "Settings", "More").forEach {
            compose.onAllNodesWithContentDescription(it).fetchSemanticsNodes().size shouldBe 0
        }
        compose.onAllNodesWithText("·", substring = true).fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `header actions given by the caller are drawn in the header`() {
        show(actions = { Text("extra action") })

        compose.onNodeWithText("extra action").assertIsDisplayed()
    }
}
