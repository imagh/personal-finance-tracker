package dev.imagh.pft.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The debug design catalogue (debug source set only): `DevContent()` shows the token sheet. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
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

    @Test
    fun `the Motion section shows the press target and a Play button for each spring`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Motion").performClick()
        compose.onNodeWithText("Motion").assertIsSelected()
        compose.onNodeWithText("Press me", substring = true).assertIsDisplayed()
        listOf("snappy", "smooth", "gentle", "bouncy").forEach { name ->
            compose.onNode(hasScrollAction()).performScrollToNode(hasTestTag("spring-$name"))
            compose.onNodeWithTag("spring-$name").assertIsDisplayed()
        }
    }

    @Test
    fun `playing a spring moves its dot and playing again moves it back`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Motion").performClick()
        compose.onNode(hasScrollAction()).performScrollToNode(hasTestTag("spring-smooth"))
        val start =
            compose
                .onNodeWithTag("dot-smooth", useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot.left
        compose.onNodeWithTag("spring-smooth").performClick()
        compose.waitForIdle()
        val end =
            compose
                .onNodeWithTag("dot-smooth", useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot.left
        assertTrue("dot moved from $start to $end", end > start + 50f)
        compose.onNodeWithTag("spring-smooth").performClick()
        compose.waitForIdle()
        assertEquals(
            start,
            compose
                .onNodeWithTag("dot-smooth", useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot.left,
            1f,
        )
    }

    @Test
    fun `the header sections show the real scaffold in each style`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Standard header").performClick()
        compose.onNodeWithText("Budgets").assertIsDisplayed()
        compose.onNodeWithText("Dashboard header").performClick()
        compose.onNodeWithText("October 2026").assertIsDisplayed()
        compose.onNodeWithText("spent this month").assertIsDisplayed()
    }

    @Test
    fun `the Bottom bar section shows the action bar variants`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Bottom bar").performClick()
        compose.onNodeWithText("Cancel").assertIsDisplayed()
        compose.onNodeWithText("Continue").assertIsNotEnabled()
    }

    @Test
    fun `the Chips section shows a chip for each tone`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Chips").performClick()
        compose.onNodeWithText("Chips").assertIsSelected()
        listOf("Unverified", "Overdue", "Under", "Paused").forEach { compose.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test
    fun `the Money section shows signed amounts in every size`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Money").performClick()
        compose.onNodeWithText("Money").assertIsSelected()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("−₹48,250.40"))
        compose.onNodeWithText("−₹48,250.40").assertIsDisplayed()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("+₹1,25,000.00"))
        compose.onNodeWithText("+₹1,25,000.00").assertIsDisplayed()
    }

    @Test
    fun `the Sections section shows islands of rows`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Sections").performClick()
        compose.onNodeWithText("Sections").assertIsSelected()
        compose.onNodeWithText("Everyday account").assertIsDisplayed()
        compose.onNodeWithText("Monthly, from the 1st").assertIsDisplayed()
    }
}
