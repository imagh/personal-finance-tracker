package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/** [ThemePalette] and [colors]: the choice of palette and mode picks exactly one [PaletteColors]. */
class ThemePaletteTest {
    /**
     * The owner-approved mockup tokens of one palette in one mode (`.phone[data-pal=...]`), pinned
     * over the generated roles.
     */
    private data class Pins(
        val flat: Long,
        val flatlow: Long,
        val on: Long,
        val onvar: Long,
        val line: Long,
        val primary: Long,
    )

    private fun assertPinned(
        palette: ThemePalette,
        dark: Boolean,
        pins: Pins,
    ) {
        val s = palette.colors(dark).scheme
        val where = "$palette ${if (dark) "dark" else "light"}"
        assertEquals(Color(pins.flat), s.surface, "$where flat -> surface")
        assertEquals(Color(pins.flat), s.background, "$where flat -> background")
        assertEquals(Color(pins.flatlow), s.surfaceContainer, "$where flatlow -> surfaceContainer")
        assertEquals(Color(pins.on), s.onSurface, "$where on -> onSurface")
        assertEquals(Color(pins.on), s.onBackground, "$where on -> onBackground")
        assertEquals(Color(pins.onvar), s.onSurfaceVariant, "$where onvar -> onSurfaceVariant")
        assertEquals(Color(pins.line), s.outlineVariant, "$where line -> outlineVariant")
        assertEquals(Color(pins.primary), s.primary, "$where primary")
        assertEquals(Color(pins.primary), s.surfaceTint, "$where surfaceTint follows primary")
    }

    @Test
    fun `Midnight pins the owner-approved mockup values`() {
        val light = Pins(0xFFF4F5F9, 0xFFECEFF5, 0xFF11172A, 0xFF4A5266, 0xFFD2D7E3, 0xFF7D5F26)
        val dark = Pins(0xFF0F1424, 0xFF161C30, 0xFFE6E8EF, 0xFFA5ACBF, 0xFF222B42, 0xFFD4B572)
        assertPinned(ThemePalette.MIDNIGHT, dark = false, light)
        assertPinned(ThemePalette.MIDNIGHT, dark = true, dark)
    }

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
