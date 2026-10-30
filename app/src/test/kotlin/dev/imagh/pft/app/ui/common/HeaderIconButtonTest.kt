package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `HeaderIconButton` (plan §3.5, §6.1 DS6e, UI-14): the only thing a header's `actions` row may
 * hold. It draws a registry symbol, never text, and always speaks a label to TalkBack. Fake data
 * only. Pixels (which symbol, in which colour) are the goldens' job (`HeaderScreenshotTest`):
 * `captureToImage` does not work under Robolectric here. The touch target is read from
 * `touchBoundsInRoot`, because the 40dp semantics bounds do not include the 48dp minimum target.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HeaderIconButtonTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f

    private fun show(
        fontScale: Float = 1f,
        content: @Composable () -> Unit,
    ) {
        compose.setContent {
            density = LocalDensity.current.density
            CompositionLocalProvider(
                LocalDensity provides Density(density, fontScale),
            ) { PftTheme(ThemePrefs(dynamicColor = false)) { content() } }
        }
    }

    private val isImage = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image)
    private val anyText = SemanticsMatcher.keyIsDefined(SemanticsProperties.Text)

    private fun touchTarget(label: String): Rect =
        compose
            .onNodeWithContentDescription(label)
            .fetchSemanticsNode()
            .touchBoundsInRoot
            .inDp()

    private fun Rect.inDp() = Rect(left / density, top / density, right / density, bottom / density)

    @Test
    fun `it is announced as a button named by the label and draws no text`() {
        show { HeaderIconButton(PftSymbol.SEARCH, "Search", onClick = {}) }

        val node = compose.onNodeWithContentDescription("Search")
        node.assertIsDisplayed()
        node.assertHasClickAction()
        val config = node.fetchSemanticsNode().config
        assertEquals(listOf("Search"), config.getOrNull(SemanticsProperties.ContentDescription))
        assertEquals(Role.Button, config.getOrNull(SemanticsProperties.Role))
        compose.onAllNodes(anyText, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `the touch target is at least 48dp square`() {
        show { HeaderIconButton(PftSymbol.FILTER, "Filter", onClick = {}) }
        val bounds = touchTarget("Filter")
        assertTrue("width ${bounds.width}", bounds.width >= 48f - 0.5f)
        assertTrue("height ${bounds.height}", bounds.height >= 48f - 0.5f)
    }

    @Test
    fun `the icon is drawn 24dp square inside the button`() {
        show { HeaderIconButton(PftSymbol.FILTER, "Filter", onClick = {}) }
        val image =
            compose
                .onNode(isImage and hasContentDescription("Filter"), useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot
                .inDp()
        assertEquals("icon width", 24f, image.width, 0.5f)
        assertEquals("icon height", 24f, image.height, 0.5f)
    }

    @Test
    fun `the touch target stays 48dp at 200 percent font scale`() {
        show(fontScale = 2f) { HeaderIconButton(PftSymbol.FILTER, "Filter", onClick = {}) }
        val bounds = touchTarget("Filter")
        assertTrue("width ${bounds.width}", bounds.width >= 48f - 0.5f)
        assertTrue("height ${bounds.height}", bounds.height >= 48f - 0.5f)
    }

    @Test
    fun `a click calls onClick once per tap`() {
        var taps by mutableIntStateOf(0)
        show { HeaderIconButton(PftSymbol.OVERFLOW, "More", onClick = { taps++ }) }

        compose.onNodeWithContentDescription("More").performClick()
        assertEquals(1, taps)
        compose.onNodeWithContentDescription("More").performClick()
        assertEquals(2, taps)
    }

    @Test
    fun `an empty label is rejected`() {
        val failure =
            assertThrows(IllegalArgumentException::class.java) {
                show { HeaderIconButton(PftSymbol.BACK, "", onClick = {}) }
                compose.waitForIdle()
            }
        assertTrue(failure.message.orEmpty().contains("label"))
    }

    @Test
    fun `a whitespace-only label is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            show { HeaderIconButton(PftSymbol.BACK, "   ", onClick = {}) }
            compose.waitForIdle()
        }
    }
}
