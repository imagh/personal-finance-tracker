package dev.imagh.pft.app.ui.theme

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

/**
 * The first frame (UI-18, plan §3.3): the window paints Graphite's canvas from resources before
 * Compose draws, whatever palette the user picked. A palette whose canvas or flat surface is far
 * from that colour would flash at launch, so each stays within [WINDOW_FLASH_MAX_TONE_DELTA]
 * tones of it in the same mode. (`WindowThemeTest` pins the resources to Graphite's canvas.)
 */
class WindowFlashToneTest {
    private fun windowColour(dark: Boolean) = ThemePalette.GRAPHITE.colors(dark).canvas

    @Test
    fun `every palette's canvas and surface is within the flash bound of the window colour`() {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val colors = palette.colors(dark)
                val window = toneOf(windowColour(dark))
                mapOf("canvas" to colors.canvas, "surface" to colors.scheme.surface).forEach { (name, colour) ->
                    val delta = abs(toneOf(colour) - window)
                    assertTrue(
                        delta <= WINDOW_FLASH_MAX_TONE_DELTA,
                        "$palette dark=$dark $name is ${"%.1f".format(delta)} tones from the window colour",
                    )
                }
            }
        }
    }

    @Test
    fun `the bound separates light from dark - a palette's light canvas is nowhere near the dark window`() {
        // Guards the test above: a light colour measured against the dark window must fail the bound,
        // so a mode mix-up cannot pass it.
        for (palette in ThemePalette.entries) {
            val light = palette.colors(dark = false).canvas
            assertTrue(abs(toneOf(light) - toneOf(windowColour(dark = true))) > WINDOW_FLASH_MAX_TONE_DELTA)
        }
    }
}
