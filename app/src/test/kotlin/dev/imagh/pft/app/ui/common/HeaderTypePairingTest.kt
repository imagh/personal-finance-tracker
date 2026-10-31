@file:OptIn(ExperimentalTextApi::class)

package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.DisplayFont
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.theme.TypePairing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The header titles under the `EDITORIAL` type pairing (DS6g, UI-15): screen titles, the Dashboard
 * title and the collapsed bar title are in the display family, each cut for its own size; the
 * subtitle is body text and stays sans. The `HERO_ONLY` side (every title sans) is in
 * `CollapsingHeaderScaffoldTest`, which this class is split from to keep it under detekt's size limit.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HeaderTypePairingTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = Density(1f)

    private fun tag(tag: String) = compose.onNodeWithTag(tag)

    private fun show(
        style: HeaderStyle,
        prefs: ThemePrefs,
        subtitle: String? = null,
        hero: (@Composable () -> Unit)? = null,
    ) {
        compose.setContent {
            density = LocalDensity.current
            PftTheme(prefs) {
                CollapsingHeaderScaffold(
                    title = "Budgets",
                    style = style,
                    subtitle = subtitle,
                    hero = hero,
                    heroCaption = if (hero != null) "spent this month" else null,
                ) {
                    items(60) { index ->
                        Box(Modifier.fillMaxWidth().height(56.dp).testTag("item-$index")) { Text("Row $index") }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
    }

    private fun editorial(displayFont: DisplayFont = DisplayFont.BUNDLED_SERIF) =
        ThemePrefs(dynamicColor = false, typePairing = TypePairing.EDITORIAL, displayFont = displayFont)

    /** Swipes the list up 400dp, collapsing the header to its bar. */
    private fun swipeListUp() {
        val pixels = 400f * density.density
        tag(HeaderTags.LIST).performTouchInput { swipeUp(startY = bottom - 40f, endY = bottom - 40f - pixels) }
        repeat(120) {
            compose.mainClock.advanceTimeByFrame()
            compose.waitForIdle()
        }
    }

    private fun SemanticsNodeInteraction.family(): FontFamily? {
        val results = mutableListOf<TextLayoutResult>()
        fetchSemanticsNode()
            .config
            .getOrNull(SemanticsActions.GetTextLayoutResult)
            ?.action
            ?.invoke(results)
        return results
            .single()
            .layoutInput.style.fontFamily
    }

    /** The `opsz` and `wght` axes of a title in the bundled Newsreader, or null when it is not. */
    private fun SemanticsNodeInteraction.newsreaderAxes(): Map<String, Float>? {
        val font = (family() as? FontListFontFamily)?.fonts?.singleOrNull() as? ResourceFont
        if (font == null || font.resId != R.font.newsreader) return null
        return font.variationSettings.settings.associate { it.axisName to it.toVariationValue(Density(1f)) }
    }

    @Test
    fun `under EDITORIAL a Standard title is headlineLarge in Newsreader at weight 600 and the 32sp cut`() {
        show(HeaderStyle.Standard, editorial(), subtitle = "October - 3 days left")
        val axes = tag(HeaderTags.TITLE_LARGE).newsreaderAxes()
        assertEquals(600f, axes?.get("wght"))
        assertEquals(32f, axes?.get("opsz"))
    }

    @Test
    fun `under EDITORIAL the subtitle stays in the sans text family`() {
        show(HeaderStyle.Standard, editorial(), subtitle = "October - 3 days left")
        assertEquals(FontFamily.Default, tag(HeaderTags.SUBTITLE).family())
    }

    @Test
    fun `under EDITORIAL a Dashboard title is titleLarge in Newsreader at weight 500 and the 22sp cut`() {
        show(HeaderStyle.Dashboard, editorial(), hero = { Text("₹48,250", Modifier.testTag("hero-text")) })
        val axes = tag(HeaderTags.TITLE_LARGE).newsreaderAxes()
        assertEquals(500f, axes?.get("wght"))
        assertEquals(22f, axes?.get("opsz"))
        assertTrue(tag("hero-text").newsreaderAxes() != null)
    }

    @Test
    fun `under EDITORIAL the collapsed bar title is titleLarge in Newsreader`() {
        show(HeaderStyle.Standard, editorial())
        swipeListUp()
        val axes = tag(HeaderTags.TITLE_BAR).newsreaderAxes()
        assertEquals(500f, axes?.get("wght"))
        assertEquals(22f, axes?.get("opsz"))
    }

    @Test
    fun `EDITORIAL with Same as text leaves every title sans`() {
        show(HeaderStyle.Standard, editorial(DisplayFont.TEXT), subtitle = "October - 3 days left")
        assertEquals(FontFamily.Default, tag(HeaderTags.TITLE_LARGE).family())
        assertEquals(FontFamily.Default, tag(HeaderTags.SUBTITLE).family())
        swipeListUp()
        assertEquals(FontFamily.Default, tag(HeaderTags.TITLE_BAR).family())
    }
}
