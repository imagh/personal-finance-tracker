package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** `EmptyState` (plan §3.4, §6.1 DS5): a glyph, a message and an optional action for a list with nothing in it. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class EmptyStateTest {
    @get:Rule
    val compose = HostedComposeRule()

    @Test
    fun `it shows the message`() {
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { EmptyState("No accounts yet") } }
        compose.onNodeWithText("No accounts yet").assertIsDisplayed()
    }

    @Test
    fun `the action shows its label and invokes its callback once per click`() {
        var clicks = 0
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EmptyState("No accounts yet", actionLabel = "Add account", onAction = { clicks++ })
            }
        }
        compose.onNodeWithText("Add account").assertHasClickAction().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun `with no action there is nothing to click`() {
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { EmptyState("Nothing matches") } }
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
    }

    @Test
    fun `the glyph slot is shown above the message`() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EmptyState("No accounts yet", glyph = { Text("₹", Modifier.testTag("glyph")) })
            }
        }
        val glyph = compose.onNodeWithTag("glyph").fetchSemanticsNode().boundsInRoot
        val message = compose.onNodeWithText("No accounts yet").fetchSemanticsNode().boundsInRoot
        assertTrue("glyph bottom ${glyph.bottom} is below the message top ${message.top}", glyph.bottom <= message.top)
    }

    @Test
    fun `the action has a touch target at least 48dp tall`() {
        var density = 1f
        compose.setContent {
            density = LocalDensity.current.density
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EmptyState("No accounts yet", actionLabel = "Add account", onAction = {})
            }
        }
        val height =
            compose
                .onNodeWithText("Add account")
                .fetchSemanticsNode()
                .touchBoundsInRoot.height / density
        assertTrue("touch target is ${height}dp tall", height >= 48f)
    }

    @Test
    fun `the message and action are centred horizontally`() {
        var density = 1f
        compose.setContent {
            density = LocalDensity.current.density
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EmptyState("No accounts yet", actionLabel = "Add account", onAction = {})
            }
        }
        listOf("No accounts yet", "Add account").forEach { text ->
            val centre =
                compose
                    .onNodeWithText(text)
                    .fetchSemanticsNode()
                    .boundsInRoot.center.x / density
            assertEquals(text, 393f / 2f, centre, 1f)
        }
    }

    @Test
    fun `an action label without a callback is rejected`() {
        val failure =
            runCatching {
                compose.setContent {
                    PftTheme(
                        ThemePrefs(dynamicColor = false),
                    ) { EmptyState("x", actionLabel = "Add") }
                }
            }.exceptionOrNull()
        assertTrue("$failure", failure is IllegalArgumentException)
    }
}
