package dev.imagh.pft.app.ui.theme

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/** The defaults of [ThemePrefs]: what the app looks like before the user chooses anything. */
class ThemePrefsTest {
    @Test
    fun `both palettes default to Graphite`() {
        assertEquals(ThemePalette.GRAPHITE, ThemePrefs().lightPalette)
        assertEquals(ThemePalette.GRAPHITE, ThemePrefs().darkPalette)
        assertEquals(ThemePalette.GRAPHITE, ThemePrefs.Default.lightPalette)
        assertEquals(ThemePalette.GRAPHITE, ThemePrefs.Default.darkPalette)
    }

    @Test
    fun `the background defaults to tonal`() {
        assertEquals(BackgroundStyle.TONAL, ThemePrefs().background)
        assertEquals(BackgroundStyle.TONAL, ThemePrefs.Default.background)
    }

    @Test
    fun `the header glow defaults to off`() {
        assertFalse(ThemePrefs().headerGlow)
        assertFalse(ThemePrefs.Default.headerGlow)
    }

    @Test
    fun `the type pairing defaults to hero only, today's look`() {
        assertEquals(TypePairing.HERO_ONLY, ThemePrefs().typePairing)
        assertEquals(TypePairing.HERO_ONLY, ThemePrefs.Default.typePairing)
    }

    @Test
    fun `the persisted type pairing names are permanent`() {
        // AP1 stores these names; a rename would silently reset every user's choice.
        assertEquals(listOf("HERO_ONLY", "EDITORIAL"), TypePairing.entries.map { it.name })
    }

    @Test
    fun `the sans font defaults to the bundled Figtree`() {
        assertEquals(SansFont.FIGTREE, ThemePrefs().sansFont)
        assertEquals(SansFont.FIGTREE, ThemePrefs.Default.sansFont)
    }

    @Test
    fun `the persisted sans font names are permanent`() {
        // AP1 stores these names; AP2 adds CUSTOM, only ever added.
        // Additive only: MANROPE joined with the Cobalt fonts (K2); the existing names did not change.
        assertEquals(
            setOf("FIGTREE", "PLUS_JAKARTA_SANS", "SYSTEM", "MANROPE"),
            SansFont.entries.map { it.name }.toSet(),
        )
        assertEquals(4, SansFont.entries.size)
    }

    @Test
    fun `the persisted display font names are permanent and the default is still the bundled serif`() {
        // Additive only: BUNDLED_GROTESQUE joined with the Cobalt fonts (K2); K4 changes the default, not K2.
        assertEquals(
            setOf("BUNDLED_SERIF", "SYSTEM_SERIF", "TEXT", "BUNDLED_GROTESQUE"),
            DisplayFont.entries.map { it.name }.toSet(),
        )
        assertEquals(4, DisplayFont.entries.size)
        assertEquals(DisplayFont.BUNDLED_SERIF, ThemePrefs.Default.displayFont)
    }

    @Test
    fun `the other defaults are system mode and dynamic colour off`() {
        assertEquals(ThemeMode.SYSTEM, ThemePrefs.Default.mode)
        assertFalse(ThemePrefs.Default.dynamicColor)
    }

    @Test
    fun `the palette in use is the light one in light and the dark one in dark`() {
        val prefs = ThemePrefs(lightPalette = ThemePalette.IVORY, darkPalette = ThemePalette.AUBERGINE)
        assertEquals(ThemePalette.IVORY, prefs.paletteFor(dark = false))
        assertEquals(ThemePalette.AUBERGINE, prefs.paletteFor(dark = true))
    }
}
