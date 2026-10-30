package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.imagh.pft.app.ui.common.MoneyTone
import dev.imagh.pft.app.ui.common.chipColors
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.pow

/**
 * WCAG 2.x contrast for every colour pair the design system promises (plan §3.3, UI-5).
 * Pure JVM: [luminance] implements the WCAG relative-luminance formula.
 */
class ColorContrastTest {
    private enum class Theme { LIGHT, DARK }

    private val textMin = 4.5 // WCAG 1.4.3, normal text
    private val nonTextMin = 3.0 // WCAG 1.4.11, UI components and graphics

    private fun contrast(
        a: Color,
        b: Color,
    ): Double {
        val l1 = a.luminance().toDouble()
        val l2 = b.luminance().toDouble()
        return (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
    }

    /** The neutral grey of CIE L* [tone], i.e. what M3 tonal palettes call "tone N" at zero chroma. */
    private fun greyAtTone(tone: Int): Color {
        val fy = (tone + 16.0) / 116.0
        val y = if (tone > 8) fy * fy * fy else tone / 903.2962962962963
        val encoded = if (y <= 0.0031308) 12.92 * y else 1.055 * y.pow(1.0 / 2.4) - 0.055
        return Color(encoded.toFloat(), encoded.toFloat(), encoded.toFloat())
    }

    /**
     * Dynamic surfaces are low-chroma, so they are tested as greys across the tones a surface role
     * can take: light surfaceDim (87) to white (100), dark surfaceContainerLowest (4) to
     * surfaceBright (24).
     */
    private fun surfaceGreys(theme: Theme): Map<String, Color> {
        val tones = if (theme == Theme.LIGHT) 87..100 else 4..24
        return tones.associate { "grey tone $it" to greyAtTone(it) }
    }

    private fun scheme(theme: Theme) = if (theme == Theme.LIGHT) BrandColors.light else BrandColors.dark

    private fun money(theme: Theme) = moneyColors(dark = theme == Theme.DARK)

    private fun assertAtLeast(
        min: Double,
        description: String,
        a: Color,
        b: Color,
    ) {
        val ratio = contrast(a, b)
        assertTrue(ratio >= min, "$description: contrast ${"%.2f".format(ratio)} is below $min")
    }

    @Test
    fun `money text tokens clear 4_5 on every brand surface role and every grey surface tone`() {
        for (theme in Theme.entries) {
            val surfaces = scheme(theme).surfaceRoles() + surfaceGreys(theme)
            val m = money(theme)
            val tokens = mapOf("positive" to m.positive, "negative" to m.negative, "warning" to m.warning)
            for ((tokenName, token) in tokens) {
                for ((surfaceName, surface) in surfaces) {
                    assertAtLeast(textMin, "$theme $tokenName on $surfaceName", token, surface)
                }
            }
        }
    }

    @Test
    fun `every money container pair clears 4_5`() {
        for (theme in Theme.entries) {
            val m = money(theme)
            val pairs =
                mapOf(
                    "positive" to (m.onPositiveContainer to m.positiveContainer),
                    "negative" to (m.onNegativeContainer to m.negativeContainer),
                    "warning" to (m.onWarningContainer to m.warningContainer),
                )
            for ((name, pair) in pairs) {
                assertAtLeast(textMin, "$theme $name on-container on container", pair.first, pair.second)
            }
        }
    }

    @Test
    fun `every StatusChip tone clears 4_5 for its label on its pill, and no pill is surfaceVariant`() {
        for (theme in Theme.entries) {
            for (tone in MoneyTone.entries) {
                val colors = chipColors(tone, money(theme), scheme(theme))
                assertAtLeast(textMin, "$theme $tone chip label on pill", colors.content, colors.container)
                assertNotEquals(
                    scheme(theme).surfaceVariant,
                    colors.container,
                    "$theme $tone chip pill is surfaceVariant",
                )
            }
        }
    }

    @Test
    fun `money text is never drawn on surfaceVariant, and the sweep that proves the tokens excludes it on purpose`() {
        // surfaceVariant is the one scheme surface the money tokens do not clear 4.5:1 on: dark
        // `negative` on it is about 4.23:1. If this stops being true the exclusion could be revisited;
        // until then nothing that carries money text may use it as a background.
        val dark = scheme(Theme.DARK)
        val ratio = contrast(money(Theme.DARK).negative, dark.surfaceVariant)
        assertTrue(
            ratio < textMin,
            "dark negative on surfaceVariant is ${"%.2f".format(ratio)}, so the exclusion is no longer needed",
        )
        for (theme in Theme.entries) {
            assertTrue(
                "surfaceVariant" !in scheme(theme).surfaceRoles().keys,
                "$theme sweep must not include surfaceVariant",
            )
        }
    }

    @Test
    fun `brand scheme text and accent pairs clear their thresholds`() {
        for (theme in Theme.entries) {
            val s = scheme(theme)
            assertAtLeast(textMin, "$theme onPrimary on primary", s.onPrimary, s.primary)
            assertAtLeast(textMin, "$theme onPrimaryContainer on container", s.onPrimaryContainer, s.primaryContainer)
            assertAtLeast(textMin, "$theme onSurface on surface", s.onSurface, s.surface)
            assertAtLeast(textMin, "$theme onSurfaceVariant on surface", s.onSurfaceVariant, s.surface)
            assertAtLeast(nonTextMin, "$theme primary on surface", s.primary, s.surface)
        }
    }

    @Test
    fun `contrast helper reproduces the WCAG reference ratios`() {
        assertTrue(contrast(Color.Black, Color.White) in 20.99..21.01)
        assertTrue(contrast(Color.White, Color.White) in 0.99..1.01)
    }

    @Test
    fun `brand surfaces match the M3 tone ladder the contrast sweep assumes`() {
        // If these drift, the grey sweep above no longer brackets the real surfaces.
        assertTrue(contrast(BrandColors.light.surfaceDim, greyAtTone(87)) < 1.05)
        assertTrue(contrast(BrandColors.dark.surfaceBright, greyAtTone(24)) < 1.05)
    }
}
