package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.GlyphSize
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.theme.categoryPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `CategoryGlyph` (plan §3.7, §6.1 DS3): the category/account badge. It speaks the category name
 * (never the icon key, the emoji's own name or the fallback letter) and sits on a palette colour.
 * Fake data only. Pixels are the goldens' job (`CategoryGlyphScreenshotTest`).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class CategoryGlyphTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f

    private fun show(
        fontScale: Float = 1f,
        content: @Composable () -> Unit,
    ) {
        compose.setContent {
            density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) { content() }
            }
        }
    }

    private val anyText = SemanticsMatcher.keyIsDefined(SemanticsProperties.Text)

    @Test
    fun `a symbol source speaks the category name, not the key, and draws no text`() {
        show { CategoryGlyph(GlyphSource.Symbol(PftSymbol.SHOPPING_CART), "Groceries", "teal") }

        val node = compose.onNodeWithContentDescription("Groceries")
        node.assertIsDisplayed()
        val description = node.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)
        assertEquals(listOf("Groceries"), description)
        assertEquals(Role.Image, node.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role))
        compose.onAllNodes(anyText, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `an emoji source draws the emoji and speaks the category name`() {
        show { CategoryGlyph(GlyphSource.Emoji("🍔"), "Dining out", "coral") }

        compose.onNodeWithContentDescription("Dining out").assertIsDisplayed()
        compose.onNodeWithText("🍔", useUnmergedTree = true).assertIsDisplayed()
        val description =
            compose
                .onNodeWithContentDescription("Dining out")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.ContentDescription)
        assertEquals(listOf("Dining out"), description)
    }

    @Test
    fun `a letter source draws the letter and speaks the category name`() {
        show { CategoryGlyph(GlyphSource.Letter('G'), "Gifts", "violet") }

        compose.onNodeWithContentDescription("Gifts").assertIsDisplayed()
        compose.onNodeWithText("G", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `a malformed icon key falls back to the first letter of the name, upper-cased`() {
        show { CategoryGlyph(GlyphSource.fromIconKey("sym:../../etc", "dining"), "dining", "teal") }

        compose.onNodeWithText("D", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("dining").assertIsDisplayed()
    }

    @Test
    fun `an unknown symbol key from a newer version never crashes and shows the letter`() {
        show { CategoryGlyph(GlyphSource.fromIconKey("sym:brand_new_icon", "Pets"), "Pets", "olive") }

        compose.onNodeWithText("P", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `the letter is drawn in the palette content colour`() {
        show { CategoryGlyph(GlyphSource.Letter('A'), "Alpha", "teal") }
        val color =
            compose
                .onNodeWithText("A", useUnmergedTree = true)
                .layout()
                .layoutInput.style.color
        assertEquals(categoryPalette(dark = false)["teal"].content, color)
    }

    @Test
    @Config(qualifiers = "+night")
    fun `in the dark theme the letter takes the dark palette`() {
        show { CategoryGlyph(GlyphSource.Letter('A'), "Alpha", "teal") }
        val color =
            compose
                .onNodeWithText("A", useUnmergedTree = true)
                .layout()
                .layoutInput.style.color
        assertEquals(categoryPalette(dark = true)["teal"].content, color)
    }

    @Test
    fun `an unknown colour name uses the slate colours`() {
        show { CategoryGlyph(GlyphSource.Letter('A'), "Alpha", "no-such-hue") }
        val color =
            compose
                .onNodeWithText("A", useUnmergedTree = true)
                .layout()
                .layoutInput.style.color
        assertEquals(categoryPalette(dark = false)["slate"].content, color)
    }

    @Test
    fun `each size is drawn at its dp size`() {
        show {
            Column {
                GlyphSize.entries.forEach { CategoryGlyph(GlyphSource.Letter('A'), it.name, "teal", size = it) }
            }
        }
        for (size in GlyphSize.entries) {
            val bounds = compose.onNodeWithContentDescription(size.name).fetchSemanticsNode().boundsInRoot
            assertEquals(size.name, size.dp.value, bounds.width / density, 0.5f)
            assertEquals(size.name, size.dp.value, bounds.height / density, 0.5f)
        }
    }

    @Test
    fun `the sizes are 28, 40 and 56 dp`() {
        assertEquals(listOf(28f, 40f, 56f), GlyphSize.entries.map { it.dp.value })
    }

    @Test
    fun `at font scale 2_0 the glyph keeps its size and the letter is still shown in full`() {
        show(fontScale = 2f) { CategoryGlyph(GlyphSource.Letter('W'), "Water", "sky") }

        val bounds = compose.onNodeWithContentDescription("Water").fetchSemanticsNode().boundsInRoot
        assertEquals(GlyphSize.Row.dp.value, bounds.width / density, 0.5f)
        compose.onNodeWithText("W", useUnmergedTree = true).layout().assertShownInFull("at 2.0x")
    }

    @Test
    fun `a glyph with no click handler has nothing to click`() {
        show { CategoryGlyph(GlyphSource.Letter('A'), "Alpha", "teal") }
        compose.onAllNodes(hasClickAction()).assertCountEquals(0)
    }

    @Test
    fun `a clickable glyph is a button that invokes its callback once per click`() {
        var clicks = 0
        show { CategoryGlyph(GlyphSource.Letter('A'), "Alpha", "teal", onClick = { clicks++ }) }

        val node = compose.onNodeWithContentDescription("Alpha")
        node.assertHasClickAction().performClick()
        assertEquals(1, clicks)
        assertEquals(Role.Button, node.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role))
    }

    @Test
    fun `a clickable glyph has a touch target of at least 48dp at every size`() {
        show {
            Column {
                GlyphSize.entries.forEach {
                    CategoryGlyph(GlyphSource.Letter('A'), it.name, "teal", size = it, onClick = {})
                }
            }
        }
        for (size in GlyphSize.entries) {
            val touch = compose.onNodeWithContentDescription(size.name).fetchSemanticsNode().touchBoundsInRoot
            val width = touch.width / density
            val height = touch.height / density
            assertTrue(
                "${size.name}: ${width}x${height}dp",
                width >= MIN_TOUCH_DP - 0.5f && height >= MIN_TOUCH_DP - 0.5f,
            )
        }
    }

    private companion object {
        const val MIN_TOUCH_DP = 48f
    }
}
