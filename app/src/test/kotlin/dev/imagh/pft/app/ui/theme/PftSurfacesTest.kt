package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** [pftSurfaces]: the TONAL / FLAT table of plan §3.2, for every palette in both modes. */
class PftSurfacesTest {
    private val modes = listOf(false, true)

    private fun forEveryPalette(check: (palette: ThemePalette, dark: Boolean, colors: PaletteColors) -> Unit) {
        for (palette in ThemePalette.entries) {
            for (dark in modes) {
                check(palette, dark, palette.colors(dark))
            }
        }
    }

    @Test
    fun `TONAL uses the palette canvas and island with the hairline as the edge`() {
        forEveryPalette { palette, dark, colors ->
            val surfaces = pftSurfaces(colors, BackgroundStyle.TONAL, dark)
            assertEquals(colors.canvas, surfaces.canvas, "$palette dark=$dark canvas")
            assertEquals(colors.island, surfaces.island, "$palette dark=$dark island")
            assertEquals(colors.scheme.outlineVariant, surfaces.islandEdge, "$palette dark=$dark edge")
        }
    }

    @Test
    fun `dark TONAL has the 4_5 percent white highlight`() {
        forEveryPalette { palette, dark, colors ->
            if (dark) {
                assertEquals(
                    Color(1f, 1f, 1f, 0.045f),
                    pftSurfaces(colors, BackgroundStyle.TONAL, dark = true).islandHighlight,
                    "$palette dark highlight",
                )
            }
        }
    }

    @Test
    fun `the default prefs give the TONAL surfaces, with the dark highlight and the hairline edge`() {
        for (palette in ThemePalette.entries) {
            for (dark in modes) {
                val prefs = ThemePrefs.Default.copy(lightPalette = palette, darkPalette = palette)
                val colors = prefs.paletteFor(dark).colors(dark)
                val surfaces = pftSurfaces(colors, prefs.background, dark)
                assertEquals(pftSurfaces(colors, BackgroundStyle.TONAL, dark), surfaces, "$palette dark=$dark")
                assertEquals(colors.canvas, surfaces.canvas, "$palette dark=$dark canvas")
                assertNotEquals(Color.Transparent, surfaces.islandEdge, "$palette dark=$dark has an edge")
            }
        }
    }

    @Test
    fun `light TONAL has no highlight`() {
        forEveryPalette { palette, dark, colors ->
            if (!dark) {
                assertEquals(
                    Color.Transparent,
                    pftSurfaces(colors, BackgroundStyle.TONAL, dark = false).islandHighlight,
                    "$palette light highlight",
                )
            }
        }
    }

    @Test
    fun `FLAT reproduces the flat look - surface and surfaceContainer, no edge, no highlight`() {
        forEveryPalette { palette, dark, colors ->
            val surfaces = pftSurfaces(colors, BackgroundStyle.FLAT, dark)
            assertEquals(colors.scheme.surface, surfaces.canvas, "$palette dark=$dark canvas")
            assertEquals(colors.scheme.surfaceContainer, surfaces.island, "$palette dark=$dark island")
            assertEquals(Color.Transparent, surfaces.islandEdge, "$palette dark=$dark edge")
            assertEquals(Color.Transparent, surfaces.islandHighlight, "$palette dark=$dark highlight")
        }
    }

    @Test
    fun `the two styles differ for every palette, so the choice is visible`() {
        forEveryPalette { palette, dark, colors ->
            assertNotEquals(
                pftSurfaces(colors, BackgroundStyle.TONAL, dark).canvas,
                pftSurfaces(colors, BackgroundStyle.FLAT, dark).canvas,
                "$palette dark=$dark canvas is the same in both styles",
            )
        }
    }

    @Test
    fun `the default prefs give no glow, in either style`() {
        forEveryPalette { palette, dark, colors ->
            for (style in BackgroundStyle.entries) {
                val prefs = ThemePrefs.Default
                assertNull(pftSurfaces(colors, style, dark, prefs.headerGlow).glow, "$palette dark=$dark $style")
                assertNull(pftSurfaces(colors, style, dark).glow, "$palette dark=$dark $style (no glow argument)")
            }
        }
    }

    @Test
    fun `with the header glow on the glow is the palette wash, in either style`() {
        forEveryPalette { palette, dark, colors ->
            for (style in BackgroundStyle.entries) {
                val surfaces = pftSurfaces(colors, style, dark, glow = true)
                assertEquals(colors.wash, surfaces.glow, "$palette dark=$dark $style")
            }
        }
    }

    @Test
    fun `turning the glow on changes nothing but the glow`() {
        forEveryPalette { palette, dark, colors ->
            for (style in BackgroundStyle.entries) {
                assertEquals(
                    pftSurfaces(colors, style, dark),
                    pftSurfaces(colors, style, dark, glow = true).copy(glow = null),
                    "$palette dark=$dark $style",
                )
            }
        }
    }

    @Test
    fun `the persisted names are permanent`() {
        // AP1 stores `name` (ui_background), so a rename or removal would silently reset a user's choice.
        val names = BackgroundStyle.entries.map { it.name }
        listOf("TONAL", "FLAT").forEach { assertTrue(it in names, "$it must stay a BackgroundStyle entry") }
    }
}
