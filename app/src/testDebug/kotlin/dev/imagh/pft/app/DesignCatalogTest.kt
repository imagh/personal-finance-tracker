package dev.imagh.pft.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The debug design catalogue (debug source set only): `DevContent()` shows the token sheet. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DesignCatalogTest {
    @get:Rule
    val compose = HostedComposeRule()

    @Test
    fun `DevContent shows every token section by scrolling`() {
        compose.setContent { PftTheme { DevContent() } }
        val scroller = hasScrollAction()
        listOf(
            "Type scale: display, headline (sans)",
            "Type scale: title, body, label (sans)",
            "Amount styles (tabular figures)",
            "Shapes (dp)",
            "Spacing (dp)",
            "Money tokens on surfaces",
        ).forEach { title ->
            compose.onNode(scroller).performScrollToNode(hasText(title))
            compose.onNodeWithText(title).assertIsDisplayed()
        }
    }

    @Test
    fun `the toggles start on system light-dark and the bundled serif, and selecting one moves the selection`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("System").assertIsSelected()
        compose.onNodeWithText("Bundled serif").assertIsSelected()

        compose.onNodeWithText("Dark").performClick()
        compose.onNodeWithText("Dark").assertIsSelected()
        compose.onNodeWithText("Same as text").performClick()
        compose.onNodeWithText("Same as text").assertIsSelected()
    }
}
