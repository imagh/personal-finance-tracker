package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import dev.imagh.pft.app.ui.common.MoneyTone
import dev.imagh.pft.app.ui.common.chipColors
import dev.imagh.pft.app.ui.common.groupedSectionColors
import dev.imagh.pft.app.ui.home.METER_TRACK_ALPHA
import dev.imagh.pft.app.ui.home.MID_STOP
import dev.imagh.pft.app.ui.home.RING_ALPHA
import dev.imagh.pft.app.ui.home.RING_MIN_LINE_FRACTION
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

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

    /**
     * Dynamic surfaces are low-chroma, so they are tested as greys across [sweptTones]. The dark
     * band starts at black because the dark canvas and flat surfaces sit at tones 3 to 8.
     */
    private fun surfaceGreys(case: Case): Map<String, Color> =
        sweptTones(case.theme == Theme.DARK).associate { "grey tone $it" to greyAtTone(it) }

    private fun scheme(case: Case) = case.palette.colors(dark = case.theme == Theme.DARK).scheme

    /** The palette's tonal screen background and card fill (DS6c), named like the scheme's surface roles. */
    private fun tonalSurfaces(case: Case): Map<String, Color> {
        val colors = case.palette.colors(dark = case.theme == Theme.DARK)
        return mapOf("canvas" to colors.canvas, "island" to colors.island)
    }

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
            val surfaces = scheme(case).surfaceRoles() + tonalSurfaces(case) + surfaceGreys(case)
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
            val dark = case.theme == Theme.DARK
            for (style in BackgroundStyle.entries) {
                val island = groupedSectionColors(pftSurfaces(case.palette.colors(dark), style, dark)).fill
                assertNotEquals(scheme(case).surfaceVariant, island, "$case $style island is surfaceVariant")
                assertTrue(
                    island in scheme(case).surfaceRoles().values + tonalSurfaces(case).values,
                    "$case $style island is not one of the swept surfaces",
                )
                val m = money(case)
                val tokens = mapOf("positive" to m.positive, "negative" to m.negative, "warning" to m.warning)
                tokens.forEach { (name, token) ->
                    assertAtLeast(textMin, "$case $style $name on the island", token, island)
                }
                assertAtLeast(textMin, "$case $style onSurface on the island", scheme(case).onSurface, island)
            }
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
        // The category palette depends on the mode only, never on the chosen ThemePalette.
        for (theme in Theme.entries) {
            val palette = categoryPalette(dark = theme == Theme.DARK)
            for (key in CategoryPalette.keys) {
                val colors = palette[key]
                assertAtLeast(textMin, "$theme $key content on container", colors.content, colors.container)
            }
        }
    }

    @Test
    fun `onSurface and onSurfaceVariant clear 4_5 on every surface role`() {
        for (case in cases) {
            val s = scheme(case)
            for ((surfaceName, surface) in s.surfaceRoles() + tonalSurfaces(case)) {
                assertAtLeast(textMin, "$case onSurface on $surfaceName", s.onSurface, surface)
                assertAtLeast(textMin, "$case onSurfaceVariant on $surfaceName", s.onSurfaceVariant, surface)
            }
        }
    }

    /** The strongest tinted header of [case]: the wash at the glow's cap over the tonal canvas and the flat surface. */
    private fun glowTops(case: Case): Map<String, Color> {
        val colors = case.palette.colors(dark = case.theme == Theme.DARK)
        return mapOf(
            "glow over canvas" to washTop(colors.wash, colors.canvas),
            "glow over flat surface" to washTop(colors.wash, colors.scheme.surface),
        )
    }

    @Test
    fun `the title, the actions and money clear 4_5 on the strongest point of the header glow`() {
        // Contrast, not the tone band: Ivory and Aubergine light fall to tone 86.5 to 86.9 under the glow.
        for (case in cases) {
            val s = scheme(case)
            val m = money(case)
            val texts =
                mapOf(
                    "onSurface" to s.onSurface,
                    "onSurfaceVariant" to s.onSurfaceVariant,
                    "positive" to m.positive,
                    "negative" to m.negative,
                    "warning" to m.warning,
                )
            for ((glowName, glow) in glowTops(case)) {
                for ((textName, text) in texts) {
                    assertAtLeast(textMin, "$case $textName on $glowName", text, glow)
                }
            }
        }
    }

    @Test
    fun `the text and money tokens clear 4_5 on the dynamic glow at its strongest, for the tones M3 maps`() {
        // The dynamic wash is `primaryContainer` (M3: tone 90 in light, tone 30 in dark), so it is swept
        // as a grey across five tones either side, over the derived canvas (light 92, dark 4) and the
        // flat surface (light 98, dark 6). Robolectric's synthetic system palette puts primaryContainer
        // at tone 49 to 60, which no device does, so the real dynamic scheme cannot be used here.
        for (theme in Theme.entries) {
            val dark = theme == Theme.DARK
            val money = moneyColors(dark)
            val texts =
                mapOf(
                    "onSurface" to greyAtTone(if (dark) 90 else 10),
                    "onSurfaceVariant" to greyAtTone(if (dark) 80 else 30),
                    "positive" to money.positive,
                    "negative" to money.negative,
                    "warning" to money.warning,
                )
            val bases = listOf(greyAtTone(if (dark) 4 else 92), greyAtTone(if (dark) 6 else 98))
            val glows = (if (dark) 25..35 else 85..95).flatMap { tone -> bases.map { washTop(greyAtTone(tone), it) } }
            for (glow in glows) {
                for ((name, text) in texts) assertAtLeast(textMin, "dynamic $theme $name on glow $glow", text, glow)
            }
        }
    }

    @Test
    fun `the glow is a visible tint of the canvas in every palette and mode`() {
        // Guards the sweep above: a wash equal to the canvas would pass it and show nothing.
        for (case in cases) {
            val colors = case.palette.colors(dark = case.theme == Theme.DARK)
            assertTrue(colors.wash != colors.canvas, "$case wash equals the canvas")
            assertTrue(
                abs(toneOf(washTop(colors.wash, colors.canvas)) - toneOf(colors.canvas)) >= 2.0,
                "$case glow top is less than 2 tones from the canvas",
            )
        }
    }

    @Test
    fun `the accent clears 3 to 1 on the canvas and the island`() {
        for (case in cases) {
            for ((name, surface) in tonalSurfaces(case)) {
                assertAtLeast(nonTextMin, "$case primary on $name", scheme(case).primary, surface)
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

    /** The card's secondary text (the decimals and captions) is `onCard` at this alpha (plan 2026-11-25 D4). */
    private val fadedCardAlpha = 0.68f

    /**
     * Every pair the brand tokens promise (plan 2026-11-25 D1): filled brand with its text, the card's
     * text solid and faded on every gradient stop, the brand fill as a graphic on the screen, the cards and
     * the tab bar, the soft brand fill with its icon, and the secondary text on the tab bar.
     */
    private fun assertBrandPairs(
        where: String,
        brand: BrandColors,
        canvas: Color,
        island: Color,
        onSurfaceVariant: Color,
    ) {
        assertAtLeast(textMin, "$where onBrand on brand", brand.onBrand, brand.brand)
        val stops = mapOf("start" to brand.cardStart, "mid" to brand.cardMid, "end" to brand.cardEnd)
        for ((stopName, stop) in stops) {
            assertAtLeast(textMin, "$where onCard on card $stopName", brand.onCard, stop)
            val faded = brand.onCard.copy(alpha = fadedCardAlpha).compositeOver(stop)
            assertAtLeast(nonTextMin, "$where faded onCard on card $stopName", faded, stop)
            // The ring (K6): onCard at RING_ALPHA is drawn over the gradient under the text. It never reaches the start
            // corner, which would fall to 4.2:1 in Cobalt light, so the stops it covers wholly are mid and end.
            if (stopName != "start") {
                val ringed = brand.onCard.copy(alpha = RING_ALPHA).compositeOver(stop)
                assertAtLeast(textMin, "$where onCard on the ring over card $stopName", brand.onCard, ringed)
                // The hero's faded decimals can sit on the ring too (the ring crosses the hero row from about 43% on).
                val fadedOnRing = brand.onCard.copy(alpha = fadedCardAlpha).compositeOver(ringed)
                assertAtLeast(nonTextMin, "$where faded onCard on the ring over card $stopName", fadedOnRing, ringed)
            }
            // The budget meter (K6, D4): the solid onCard fill against its track, a 3:1 graphic.
            val track = brand.onCard.copy(alpha = METER_TRACK_ALPHA).compositeOver(stop)
            assertAtLeast(nonTextMin, "$where meter fill vs its track on card $stopName", brand.onCard, track)
        }
        // ...and where the ring reaches furthest toward the start, the gradient is a blend of start and mid.
        val reach = lerp(brand.cardStart, brand.cardMid, RING_MIN_LINE_FRACTION / MID_STOP)
        val ringedReach = brand.onCard.copy(alpha = RING_ALPHA).compositeOver(reach)
        assertAtLeast(textMin, "$where onCard on the ring at its reach toward the start", brand.onCard, ringedReach)
        mapOf("nav" to brand.nav, "canvas" to canvas, "island" to island).forEach { (name, surface) ->
            assertAtLeast(nonTextMin, "$where brand on $name", brand.brand, surface)
        }
        assertAtLeast(nonTextMin, "$where onBrandSoft on brandSoft", brand.onBrandSoft, brand.brandSoft)
        assertAtLeast(textMin, "$where onSurfaceVariant on nav", onSurfaceVariant, brand.nav)
    }

    @Test
    fun `the brand tokens clear their pairs in every palette and mode`() {
        for (case in cases) {
            val colors = case.palette.colors(dark = case.theme == Theme.DARK)
            assertBrandPairs("$case", colors.brand, colors.canvas, colors.island, colors.scheme.onSurfaceVariant)
        }
    }

    /**
     * A synthetic dynamic scheme of greys at the M3 mapping, with [primaryTone] and [containerTone] swept (light:
     * onPrimary 100, onPrimaryContainer 10, outlineVariant 80, onSurfaceVariant 30, canvas 92, island 96; dark: 20,
     * 90, 30, 80, 4 and 10).
     */
    private fun dynamicGreyScheme(
        dark: Boolean,
        primaryTone: Int,
        containerTone: Int,
    ) = if (dark) {
        darkColorScheme(
            primary = greyAtTone(primaryTone),
            onPrimary = greyAtTone(20),
            primaryContainer = greyAtTone(containerTone),
            onPrimaryContainer = greyAtTone(90),
            outlineVariant = greyAtTone(30),
            onSurfaceVariant = greyAtTone(80),
            surfaceContainerLowest = greyAtTone(4),
            surfaceContainerLow = greyAtTone(10),
        )
    } else {
        lightColorScheme(
            primary = greyAtTone(primaryTone),
            onPrimary = greyAtTone(100),
            primaryContainer = greyAtTone(containerTone),
            onPrimaryContainer = greyAtTone(10),
            outlineVariant = greyAtTone(80),
            onSurfaceVariant = greyAtTone(30),
            surfaceContainerHigh = greyAtTone(92),
            surfaceContainerLow = greyAtTone(96),
        )
    }

    @Test
    fun `the derived brand tokens clear their pairs for the dynamic tones M3 maps and five tones either side`() {
        // Same grey-tone approach as the dynamic glow above: primary and its container are swept.
        for (dark in listOf(false, true)) {
            val primaryTones = if (dark) 75..85 else 35..45
            val containerTones = if (dark) 25..35 else 85..95
            for ((primaryTone, containerTone) in primaryTones.flatMap { p -> containerTones.map { p to it } }) {
                val scheme = dynamicGreyScheme(dark, primaryTone, containerTone)
                val colors = dynamicPaletteColors(scheme, dark)
                assertBrandPairs(
                    "dynamic dark=$dark primary $primaryTone container $containerTone",
                    colors.brand,
                    colors.canvas,
                    colors.island,
                    scheme.onSurfaceVariant,
                )
            }
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
            val tones = sweptTones(case.theme == Theme.DARK)
            val low = greyAtTone(tones.first).luminance()
            val high = greyAtTone(tones.last).luminance()
            for ((name, role) in scheme(case).surfaceRoles() + tonalSurfaces(case)) {
                val l = role.luminance()
                assertTrue(
                    l in (low - 0.002f)..(high + 0.002f),
                    "$case $name luminance $l is outside the swept tones $tones",
                )
            }
        }
    }
}
