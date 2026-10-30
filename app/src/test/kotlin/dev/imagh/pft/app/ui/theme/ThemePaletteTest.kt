package dev.imagh.pft.app.ui.theme

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/** [ThemePalette] and [colors]: the choice of palette and mode picks exactly one [PaletteColors]. */
class ThemePaletteTest {
    @Test
    fun `Graphite resolves to the Graphite palette in each mode`() {
        assertEquals(GraphitePalette.light, ThemePalette.GRAPHITE.colors(dark = false))
        assertEquals(GraphitePalette.dark, ThemePalette.GRAPHITE.colors(dark = true))
    }

    @Test
    fun `every palette has a distinct light and dark primary`() {
        for (palette in ThemePalette.entries) {
            assertNotEquals(
                palette.colors(dark = false).scheme.primary,
                palette.colors(dark = true).scheme.primary,
                "$palette light and dark share a primary",
            )
        }
        val lightPrimaries = ThemePalette.entries.map { it.colors(dark = false).scheme.primary }
        assertEquals(lightPrimaries.size, lightPrimaries.toSet().size, "two palettes share a light primary")
        val darkPrimaries = ThemePalette.entries.map { it.colors(dark = true).scheme.primary }
        assertEquals(darkPrimaries.size, darkPrimaries.toSet().size, "two palettes share a dark primary")
    }
}
