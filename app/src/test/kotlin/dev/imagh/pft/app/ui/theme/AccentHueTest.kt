package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The accent must never read as income or expense (UI-11, plan §3.1 "Accent safety"): the primary
 * of every palette sits at least [MIN_ACCENT_HUE_DISTANCE] degrees of CIELAB hue from the money
 * `positive` and `negative`, in each mode. Warning amber is exempt from the hue rule by design
 * (other cues separate it), so it is not tested here.
 */
class AccentHueTest {
    private companion object {
        const val MIN_ACCENT_HUE_DISTANCE = 40.0
    }

    @Test
    fun `every palette primary is at least 40 degrees of CIELAB hue from money positive and negative in each mode`() {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val primary = palette.colors(dark).scheme.primary
                val money = moneyColors(dark)
                for ((name, token) in mapOf("positive" to money.positive, "negative" to money.negative)) {
                    val distance = hueDistance(labHue(primary), labHue(token))
                    assertTrue(
                        distance >= MIN_ACCENT_HUE_DISTANCE,
                        "$palette ${if (dark) "dark" else "light"} primary is ${"%.1f".format(distance)} " +
                            "degrees from money $name, below the minimum",
                    )
                }
            }
        }
    }

    @Test
    fun `labHue reproduces the CIELAB reference hues of the sRGB primaries`() {
        // sRGB red is at about 40 degrees, green at 136, blue at 306 in CIELAB (D65).
        assertEquals(40.0, labHue(Color(0xFFFF0000)), 1.0)
        assertEquals(136.0, labHue(Color(0xFF00FF00)), 1.0)
        assertEquals(306.0, labHue(Color(0xFF0000FF)), 1.0)
    }

    @Test
    fun `hueDistance wraps around 360`() {
        assertEquals(20.0, hueDistance(350.0, 10.0), 1e-9)
        assertEquals(180.0, hueDistance(0.0, 180.0), 1e-9)
    }
}
