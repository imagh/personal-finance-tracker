package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * [ThemeMode] against the system's night configuration (UI-4). Dynamic colour is off throughout
 * so the expected values are the committed brand schemes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftThemeModeTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun surfaceUnder(prefs: ThemePrefs): Color {
        var surface = Color.Unspecified
        compose.setContent { PftTheme(prefs) { surface = MaterialTheme.colorScheme.surface } }
        compose.waitForIdle()
        return surface
    }

    private val brandOnly = ThemePrefs(dynamicColor = false)

    @Test
    @Config(qualifiers = "+night")
    fun `SYSTEM follows a dark system`() {
        assertEquals(BrandColors.dark.surface, surfaceUnder(brandOnly.copy(mode = ThemeMode.SYSTEM)))
    }

    @Test
    @Config(qualifiers = "+night")
    fun `LIGHT overrides a dark system`() {
        assertEquals(BrandColors.light.surface, surfaceUnder(brandOnly.copy(mode = ThemeMode.LIGHT)))
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun `SYSTEM follows a light system`() {
        assertEquals(BrandColors.light.surface, surfaceUnder(brandOnly.copy(mode = ThemeMode.SYSTEM)))
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun `DARK overrides a light system`() {
        assertEquals(BrandColors.dark.surface, surfaceUnder(brandOnly.copy(mode = ThemeMode.DARK)))
    }

    @Test
    fun `the default prefs are system mode with dynamic colour off`() {
        assertEquals(ThemeMode.SYSTEM, ThemePrefs.Default.mode)
        assertFalse(ThemePrefs.Default.dynamicColor)
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun `changing the mode recomposes the theme`() {
        var prefs by mutableStateOf(brandOnly.copy(mode = ThemeMode.LIGHT))
        var surface = Color.Unspecified
        compose.setContent { PftTheme(prefs) { surface = MaterialTheme.colorScheme.surface } }
        compose.waitForIdle()
        assertEquals(BrandColors.light.surface, surface)

        prefs = prefs.copy(mode = ThemeMode.DARK)
        compose.waitForIdle()
        assertEquals(BrandColors.dark.surface, surface)
    }

    @Test
    @Config(qualifiers = "+night")
    fun `money tokens follow the resolved mode and ignore dynamic colour`() {
        var prefs by mutableStateOf(ThemePrefs(mode = ThemeMode.SYSTEM, dynamicColor = true))
        lateinit var money: MoneyColors
        compose.setContent { PftTheme(prefs) { money = PftTheme.money } }
        compose.waitForIdle()
        assertEquals(moneyColors(dark = true), money)

        prefs = prefs.copy(dynamicColor = false)
        compose.waitForIdle()
        assertEquals(moneyColors(dark = true), money)

        prefs = prefs.copy(mode = ThemeMode.LIGHT, dynamicColor = true)
        compose.waitForIdle()
        assertEquals(moneyColors(dark = false), money)
    }

    @Test
    @Config(qualifiers = "+night")
    fun `the category palette follows the resolved mode and ignores dynamic colour`() {
        var prefs by mutableStateOf(ThemePrefs(mode = ThemeMode.SYSTEM, dynamicColor = true))
        lateinit var palette: CategoryPalette
        compose.setContent { PftTheme(prefs) { palette = PftTheme.palette } }
        compose.waitForIdle()
        assertEquals(categoryPalette(dark = true)["teal"], palette["teal"])

        prefs = prefs.copy(mode = ThemeMode.LIGHT, dynamicColor = false)
        compose.waitForIdle()
        assertEquals(categoryPalette(dark = false)["teal"], palette["teal"])
    }
}
