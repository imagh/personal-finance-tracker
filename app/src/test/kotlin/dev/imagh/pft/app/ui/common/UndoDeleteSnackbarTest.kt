package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
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
import org.robolectric.annotation.GraphicsMode

/**
 * `UndoDeleteSnackbar`: while an undo is on offer it shows "<message>" with **Undo**; tapping Undo
 * and letting it time out are the two ways it ends, and each tells the owner of the offer.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class UndoDeleteSnackbarTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var offer by mutableStateOf<String?>(null)
    private val outcomes = mutableListOf<String>()

    private fun show() {
        compose.setContent {
            val host = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Box {
                    UndoDeleteSnackbar(
                        offer = offer,
                        message = "Account deleted",
                        host = host,
                        onUndo = { outcomes += "undo" },
                        onDismissed = { outcomes += "dismissed" },
                    )
                    SnackbarHost(host)
                }
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun `nothing shows while no undo is on offer`() {
        show()

        compose.onAllNodesWithText("Account deleted").assertCountEquals(0)
        compose.onAllNodesWithText("Undo").assertCountEquals(0)
    }

    @Test
    fun `an offer shows the message with an Undo action, and Undo reports the undo`() {
        show()
        offer = "a1"
        compose.waitForIdle()

        compose.onNodeWithText("Account deleted").assertIsDisplayed()
        compose.onNodeWithText("Undo").performClick()
        compose.waitForIdle()

        outcomes shouldBe listOf("undo")
        compose.onAllNodesWithText("Account deleted").assertCountEquals(0)
    }

    @Test
    fun `an offer that times out reports it was dismissed and is not undone`() {
        show()
        offer = "a1"
        compose.waitForIdle()
        compose.onNodeWithText("Account deleted").assertIsDisplayed()

        compose.mainClock.advanceTimeBy(LONG_SNACKBAR_MILLIS + SLACK_MILLIS)
        compose.waitForIdle()

        outcomes shouldBe listOf("dismissed")
        compose.onAllNodesWithText("Account deleted").assertCountEquals(0)
    }

    @Test
    fun `a new offer replaces the one on screen, and only the new one can be undone`() {
        show()
        offer = "a1"
        compose.waitForIdle()

        offer = "a2"
        compose.waitForIdle()
        compose.onNodeWithText("Account deleted").assertIsDisplayed()
        compose.onNodeWithText("Undo").performClick()
        compose.waitForIdle()

        // One snackbar and one undo: the replaced offer reported nothing.
        outcomes shouldBe listOf("undo")
    }

    @Test
    fun `withdrawing the offer removes the snackbar without reporting anything`() {
        show()
        offer = "a1"
        compose.waitForIdle()
        compose.onNodeWithText("Account deleted").assertIsDisplayed()

        offer = null
        compose.waitForIdle()

        compose.onAllNodesWithText("Account deleted").assertCountEquals(0)
        outcomes shouldBe emptyList()
    }

    private companion object {
        /** M3's `SnackbarDuration.Long` is 10 s. */
        const val LONG_SNACKBAR_MILLIS = 10_000L
        const val SLACK_MILLIS = 1_000L
    }
}
