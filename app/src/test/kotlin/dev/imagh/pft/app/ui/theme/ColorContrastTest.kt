package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.imagh.pft.app.ui.common.MoneyTone
import dev.imagh.pft.app.ui.common.chipColors
import dev.imagh.pft.app.ui.common.groupedSectionColor
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.pow

/**
 * WCAG 2.x contrast for every colour pair the design system promises (plan §3.3, UI-5), checked
 * for every [ThemePalette] in both modes (DS6b). Pure JVM: [luminance] implements the WCAG
 * relative-luminance formula.
 */
class ColorContrastTest {
    private enum class Theme { LIGHT, DARK }

    /** One palette in one mode; its `toString` names it in assertion messages. */
    private data class Case(
        val palette: ThemePalette,
        val theme: Theme,
    ) {
        override fun toString() = "$palette $theme"
    }

    private val cases = ThemePalette.entries.flatMap { palette -> Theme.entries.map { Case(palette, it) } }

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

    /** The tones a surface role can take: light 87 (surfaceDim) to 100, dark 0 to 24 (surfaceBright). */
    private fun sweptTones(theme: Theme) = if (theme == Theme.LIGHT) 87..100 else 0..24

    /**
     * Dynamic surfaces are low-chroma, so they are tested as greys across [sweptTones]. The dark
     * band starts at black because the dark canvas and flat surfaces sit at tones 3 to 8.
     */
    private fun surfaceGreys(case: Case): Map<String, Color> =
        sweptTones(case.theme).associate { "grey tone $it" to greyAtTone(it) }

    private fun scheme(case: Case) = case.palette.colors(dark = case.theme == Theme.DARK).scheme

    private fun money(case: Case) = moneyColors(dark = case.theme == Theme.DARK)

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
    fun `money text tokens clear 4_5 on every surface role and every grey surface tone`() {
        for (case in cases) {
            val surfaces = scheme(case).surfaceRoles() + surfaceGreys(case)
            val m = money(case)
            val tokens = mapOf("positive" to m.positive, "negative" to m.negative, "warning" to m.warning)
            for ((tokenName, token) in tokens) {
                for ((surfaceName, surface) in surfaces) {
                    assertAtLeast(textMin, "$case $tokenName on $surfaceName", token, surface)
                }
            }
        }
    }

    @Test
    fun `every money container pair clears 4_5`() {
        for (case in cases) {
            val m = money(case)
            val pairs =
                mapOf(
                    "positive" to (m.onPositiveContainer to m.positiveContainer),
                    "negative" to (m.onNegativeContainer to m.negativeContainer),
                    "warning" to (m.onWarningContainer to m.warningContainer),
                )
            for ((name, pair) in pairs) {
                assertAtLeast(textMin, "$case $name on-container on container", pair.first, pair.second)
            }
        }
    }

    @Test
    fun `every StatusChip tone clears 4_5 for its label on its pill, and no pill is surfaceVariant`() {
        for (case in cases) {
            for (tone in MoneyTone.entries) {
                val colors = chipColors(tone, money(case), scheme(case))
                assertAtLeast(textMin, "$case $tone chip label on pill", colors.content, colors.container)
                assertNotEquals(
                    scheme(case).surfaceVariant,
                    colors.container,
                    "$case $tone chip pill is surfaceVariant",
                )
            }
        }
    }

    @Test
    fun `the GroupedSection island is a surface the money tokens clear 4_5 on, never surfaceVariant`() {
        for (case in cases) {
            val island = groupedSectionColor(scheme(case))
            assertNotEquals(scheme(case).surfaceVariant, island, "$case island is surfaceVariant")
            assertTrue(
                island in scheme(case).surfaceRoles().values,
                "$case island is not one of the swept surface roles",
            )
            val m = money(case)
            mapOf("positive" to m.positive, "negative" to m.negative, "warning" to m.warning).forEach { (name, token) ->
                assertAtLeast(textMin, "$case $name on the island", token, island)
            }
            assertAtLeast(textMin, "$case onSurface on the island", scheme(case).onSurface, island)
        }
    }

    @Test
    fun `money text is never drawn on surfaceVariant, and the sweep that proves the tokens excludes it on purpose`() {
        // surfaceVariant is the one scheme surface the money tokens do not clear 4.5:1 on: dark
        // `negative` on Graphite's is about 4.23:1. If this stops being true the exclusion could be revisited;
        // until then nothing that carries money text may use it as a background.
        val graphiteDark = Case(ThemePalette.GRAPHITE, Theme.DARK)
        val ratio = contrast(money(graphiteDark).negative, scheme(graphiteDark).surfaceVariant)
        assertTrue(
            ratio < textMin,
            "Graphite dark negative on surfaceVariant is ${"%.2f".format(ratio)}, so the exclusion is no longer needed",
        )
        for (case in cases) {
            assertTrue(
                "surfaceVariant" !in scheme(case).surfaceRoles().keys,
                "$case sweep must not include surfaceVariant",
            )
        }
    }

    @Test
    fun `every category palette content colour clears 4_5 on its container, so the letter fallback is legible`() {
        for (case in cases) {
            val palette = categoryPalette(dark = case.theme == Theme.DARK)
            for (key in CategoryPalette.keys) {
                val colors = palette[key]
                assertAtLeast(textMin, "$case $key content on container", colors.content, colors.container)
            }
        }
    }

    @Test
    fun `onSurface and onSurfaceVariant clear 4_5 on every surface role`() {
        for (case in cases) {
            val s = scheme(case)
            for ((surfaceName, surface) in s.surfaceRoles()) {
                assertAtLeast(textMin, "$case onSurface on $surfaceName", s.onSurface, surface)
                assertAtLeast(textMin, "$case onSurfaceVariant on $surfaceName", s.onSurfaceVariant, surface)
            }
        }
    }

    @Test
    fun `scheme text and accent pairs clear their thresholds`() {
        for (case in cases) {
            val s = scheme(case)
            assertAtLeast(textMin, "$case onPrimary on primary", s.onPrimary, s.primary)
            assertAtLeast(textMin, "$case onPrimaryContainer on container", s.onPrimaryContainer, s.primaryContainer)
            assertAtLeast(textMin, "$case onSurface on surface", s.onSurface, s.surface)
            assertAtLeast(textMin, "$case onSurfaceVariant on surface", s.onSurfaceVariant, s.surface)
            assertAtLeast(nonTextMin, "$case primary on surface", s.primary, s.surface)
        }
    }

    @Test
    fun `contrast helper reproduces the WCAG reference ratios`() {
        assertTrue(contrast(Color.Black, Color.White) in 20.99..21.01)
        assertTrue(contrast(Color.White, Color.White) in 0.99..1.01)
    }

    @Test
    fun `every surface role lies within the swept band`() {
        // Tone is CIE L*, a function of luminance alone, so a role is inside the band exactly
        // when its luminance is between the band's end greys. If one drifts out, the grey sweep
        // above no longer brackets the real surfaces and must be widened.
        for (case in cases) {
            val tones = sweptTones(case.theme)
            val low = greyAtTone(tones.first).luminance()
            val high = greyAtTone(tones.last).luminance()
            for ((name, role) in scheme(case).surfaceRoles()) {
                val l = role.luminance()
                assertTrue(
                    l in (low - 0.002f)..(high + 0.002f),
                    "$case $name luminance $l is outside the swept tones $tones",
                )
            }
        }
    }
}
