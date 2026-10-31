package dev.imagh.pft.app

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.ResourceFont
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.BackgroundStyle
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.theme.TypePairing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
            "Type scale: display, headline",
            "Type scale: title, body, label",
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
    fun `the palette and dynamic colour toggles start on Graphite with dynamic colour off`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Light: Graphite").assertIsSelected()
        compose.onNodeWithText("Dark: Graphite").assertIsSelected()
        compose.onNodeWithText("Dynamic off").assertIsSelected()
    }

    @Test
    fun `the light and dark palette rows are independent, and dynamic colour toggles`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Light: Ivory").performClick()
        compose.onNodeWithText("Dark: Midnight").performClick()
        compose.onNodeWithText("Dynamic on").performClick()
        compose.onNodeWithText("Light: Ivory").assertIsSelected()
        compose.onNodeWithText("Dark: Midnight").assertIsSelected()
        compose.onNodeWithText("Dynamic on").assertIsSelected()
        compose.onNodeWithText("Light: Graphite").assertIsNotSelected()
        compose.onNodeWithText("Dark: Graphite").assertIsNotSelected()
    }

    @Test
    fun `the background toggle starts on the default style and selecting the other one moves the selection`() {
        compose.setContent { PftTheme { DevContent() } }
        val default = if (ThemePrefs.Default.background == BackgroundStyle.TONAL) "Tonal" else "Flat"
        val other = if (default == "Tonal") "Flat" else "Tonal"
        compose.onNodeWithText("Background: $default").assertIsSelected()
        compose.onNodeWithText("Background: $other").assertIsNotSelected()

        compose.onNodeWithText("Background: $other").performClick()
        compose.onNodeWithText("Background: $other").assertIsSelected()
        compose.onNodeWithText("Background: $default").assertIsNotSelected()
        compose.onNodeWithText("Background: $default").performClick()
        compose.onNodeWithText("Background: $default").assertIsSelected()
    }

    @Test
    fun `the header glow toggle starts on the default and selecting the other one moves the selection`() {
        compose.setContent { PftTheme { DevContent() } }
        val default = if (ThemePrefs.Default.headerGlow) "Glow on" else "Glow off"
        val other = if (ThemePrefs.Default.headerGlow) "Glow off" else "Glow on"
        compose.onNodeWithText(default).assertIsSelected()
        compose.onNodeWithText(other).assertIsNotSelected()

        compose.onNodeWithText(other).performClick()
        compose.onNodeWithText(other).assertIsSelected()
        compose.onNodeWithText(default).assertIsNotSelected()
        compose.onNodeWithText(default).performClick()
        compose.onNodeWithText(default).assertIsSelected()
    }

    @Test
    fun `the type pairing toggle starts on the default and Editorial sets the title roles in the serif`() {
        compose.setContent { PftTheme { DevContent() } }
        val default = if (ThemePrefs.Default.typePairing == TypePairing.HERO_ONLY) "Hero only" else "Editorial"
        compose.onNodeWithText("Pairing: $default").assertIsSelected()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("titleLarge"))
        assertFalse(compose.onNodeWithText("titleLarge").usesNewsreader())

        compose.onNodeWithText("Pairing: Editorial").performClick()
        compose.onNodeWithText("Pairing: Editorial").assertIsSelected()
        compose.onNodeWithText("Pairing: Hero only").assertIsNotSelected()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("titleLarge"))
        assertTrue(compose.onNodeWithText("titleLarge").usesNewsreader())
        compose.onNodeWithText("Pairing: Hero only").performClick()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("titleLarge"))
        assertFalse(compose.onNodeWithText("titleLarge").usesNewsreader())
    }

    private fun SemanticsNodeInteraction.usesNewsreader(): Boolean {
        val results = mutableListOf<TextLayoutResult>()
        fetchSemanticsNode()
            .config
            .getOrNull(SemanticsActions.GetTextLayoutResult)
            ?.action
            ?.invoke(results)
        val family =
            results
                .single()
                .layoutInput.style.fontFamily
        return (family as? FontListFontFamily)?.fonts.orEmpty().any {
            it is ResourceFont &&
                it.resId == R.font.newsreader
        }
    }

    @Test
    fun `every palette can be picked for light and for dark`() {
        compose.setContent { PftTheme { DevContent() } }
        listOf("Graphite", "Midnight", "Ivory", "Aubergine").forEach { name ->
            compose.onNodeWithText("Light: $name").performClick()
            compose.onNodeWithText("Light: $name").assertIsSelected()
            compose.onNodeWithText("Dark: $name").performClick()
            compose.onNodeWithText("Dark: $name").assertIsSelected()
        }
    }

    @Test
    fun `the Accent section shows an Unverified pill beside a selected navigation item and the FAB`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Accent").performClick()
        compose.onNodeWithText("Accent").assertIsSelected()
        compose.onNodeWithText("Unverified").assertIsDisplayed()
        compose.onNodeWithContentDescription("Add").assertIsDisplayed()
        compose.onNodeWithText("Budgets").assertIsSelected()
        compose.onNodeWithText("Accounts").assertIsNotSelected()
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
    @Config(qualifiers = "+h1000dp")
    fun `the header sections show the real scaffold in each style`() {
        // The toggle rows above the page take about 40% of a phone-height window, and a header in a
        // window under 560dp starts collapsed (no hero or period), so this page is checked on a taller one.
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
        // The toggle rows above the page take most of a phone's height, so the page scrolls to its rows.
        listOf("Everyday account", "Monthly, from the 1st").forEach { text ->
            compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
            compose.onNodeWithText(text).assertIsDisplayed()
        }
    }

    @Test
    fun `the Empty section shows the empty state with its action`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Empty").performClick()
        compose.onNodeWithText("Empty").assertIsSelected()
        // The toggle rows above the page take most of a phone's height, so the page scrolls to its content.
        listOf("Add account", "Nothing matches your search.").forEach { text ->
            compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
            compose.onNodeWithText(text).assertIsDisplayed()
        }
    }

    @Test
    fun `the Glyphs section shows the sources and the symbol grid`() {
        compose.setContent { PftTheme { DevContent() } }
        compose.onNodeWithText("Glyphs").performClick()
        compose.onNodeWithText("Glyphs").assertIsSelected()
        compose.onNodeWithText("Sizes").assertIsDisplayed()
        compose.onNode(hasScrollAction()).performScrollToNode(hasContentDescription("savings"))
        compose.onNodeWithContentDescription("savings").assertIsDisplayed()
    }
}
