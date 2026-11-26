package dev.imagh.pft.app.ui.theme

import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** `PftTheme.brand`: the brand tokens of the palette [PftTheme] resolved, in the mode it resolved. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftThemeBrandTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun brandUnder(prefs: ThemePrefs): BrandColors? {
        var brand: BrandColors? = null
        compose.setContent { PftTheme(prefs) { brand = PftTheme.brand } }
        compose.waitForIdle()
        return brand
    }

    @Test
    fun `Cobalt light provides its pinned brand tokens`() {
        val prefs = ThemePrefs(mode = ThemeMode.LIGHT, lightPalette = ThemePalette.COBALT)
        assertEquals(ThemePalette.COBALT.colors(dark = false).brand, brandUnder(prefs))
    }

    @Test
    fun `Cobalt dark provides its own, different brand tokens`() {
        val prefs =
            ThemePrefs(mode = ThemeMode.DARK, lightPalette = ThemePalette.COBALT, darkPalette = ThemePalette.COBALT)
        val dark = brandUnder(prefs)
        assertEquals(ThemePalette.COBALT.colors(dark = true).brand, dark)
        assertNotEquals(ThemePalette.COBALT.colors(dark = false).brand, dark)
    }

    @Test
    fun `the palette picked for the mode wins, not the other one`() {
        val prefs =
            ThemePrefs(mode = ThemeMode.LIGHT, lightPalette = ThemePalette.IVORY, darkPalette = ThemePalette.COBALT)
        assertEquals(ThemePalette.IVORY.colors(dark = false).brand, brandUnder(prefs))
    }

    @Test
    fun `an existing palette provides its derived brand tokens`() {
        val prefs = ThemePrefs(mode = ThemeMode.LIGHT, lightPalette = ThemePalette.GRAPHITE)
        val colors = ThemePalette.GRAPHITE.colors(dark = false)
        assertEquals(derivedBrand(colors.scheme, colors.island), brandUnder(prefs))
    }
}
