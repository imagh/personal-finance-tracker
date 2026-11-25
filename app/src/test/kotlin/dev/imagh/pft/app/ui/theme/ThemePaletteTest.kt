package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val canvas: Long,
        val island: Long,
        val wash: Long,
    )

    private fun assertPinned(
        palette: ThemePalette,
        dark: Boolean,
        pins: Pins,
    ) {
        val colors = palette.colors(dark)
        val s = colors.scheme
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
        assertEquals(Color(pins.canvas), colors.canvas, "$where canvas")
        assertEquals(Color(pins.island), colors.island, "$where island")
        assertEquals(Color(pins.wash), colors.wash, "$where pc -> wash")
    }

    @Test
    fun `Graphite pins the owner-approved mockup values`() {
        val lightPins =
            Pins(
                0xFFF3F4F5,
                0xFFEBEDEF,
                0xFF14161A,
                0xFF50545C,
                0xFFD1D4D9,
                0xFF3B4350,
                0xFFE4E6E9,
                0xFFEFF0F2,
                0xFFD5DAE3,
            )
        val darkPins =
            Pins(
                0xFF101113,
                0xFF181A1D,
                0xFFE6E7EA,
                0xFFA8ACB3,
                0xFF25282C,
                0xFFC9CED8,
                0xFF0B0C0E,
                0xFF141618,
                0xFF2A2F38,
            )
        assertPinned(ThemePalette.GRAPHITE, dark = false, lightPins)
        assertPinned(ThemePalette.GRAPHITE, dark = true, darkPins)
    }

    @Test
    fun `Midnight pins the owner-approved mockup values`() {
        val lightPins =
            Pins(
                0xFFF4F5F9,
                0xFFECEFF5,
                0xFF11172A,
                0xFF4A5266,
                0xFFD2D7E3,
                0xFF7D5F26,
                0xFFE6E9F0,
                0xFFF0F2F7,
                0xFFE9DDC2,
            )
        val darkPins =
            Pins(
                0xFF0F1424,
                0xFF161C30,
                0xFFE6E8EF,
                0xFFA5ACBF,
                0xFF222B42,
                0xFFD4B572,
                0xFF0A0E1A,
                0xFF121929,
                0xFF33291A,
            )
        assertPinned(ThemePalette.MIDNIGHT, dark = false, lightPins)
        assertPinned(ThemePalette.MIDNIGHT, dark = true, darkPins)
    }

    @Test
    fun `Ivory pins the owner-approved mockup values`() {
        val lightPins =
            Pins(
                0xFFF6F2E9,
                0xFFEFEADF,
                0xFF1F1D18,
                0xFF5A5548,
                0xFFD9D2C2,
                0xFF2E3A59,
                0xFFE9E4D8,
                0xFFF3EFE5,
                0xFFDCD3BC,
            )
        val darkPins =
            Pins(
                0xFF181713,
                0xFF201F1A,
                0xFFECE6D8,
                0xFFAFA896,
                0xFF2B2923,
                0xFFAEB9DA,
                0xFF12110E,
                0xFF1A1915,
                0xFF2A2C38,
            )
        assertPinned(ThemePalette.IVORY, dark = false, lightPins)
        assertPinned(ThemePalette.IVORY, dark = true, darkPins)
    }

    @Test
    fun `Aubergine pins the owner-approved mockup values`() {
        val lightPins =
            Pins(
                0xFFF7F2F7,
                0xFFF0E9F0,
                0xFF1D141D,
                0xFF594C58,
                0xFFDACFD9,
                0xFF5B2A66,
                0xFFEAE3EA,
                0xFFF4EFF4,
                0xFFE2D0E4,
            )
        val darkPins =
            Pins(
                0xFF140D15,
                0xFF1D141E,
                0xFFEDE3EC,
                0xFFB3A5B2,
                0xFF2E2030,
                0xFFD9BE8A,
                0xFF0E080F,
                0xFF181018,
                0xFF3B2540,
            )
        assertPinned(ThemePalette.AUBERGINE, dark = false, lightPins)
        assertPinned(ThemePalette.AUBERGINE, dark = true, darkPins)
    }

    @Test
    fun `Cobalt pins the owner-approved mockup values, with the two tone adjustments`() {
        // docs/plans/2026-11-25-m1a-cobalt-redesign.md D1: light #687187 (mockup #7A8399 fails 4.5:1 text),
        // dark primary #9DB1FF (the mockup --icon; #2251FF fails as text on dark).
        fun assertCobalt(
            dark: Boolean,
            canvas: Long,
            island: Long,
            on: Long,
            onvar: Long,
            line: Long,
            primary: Long,
            soft: Long,
            wash: Long,
        ) {
            val colors = ThemePalette.COBALT.colors(dark)
            val s = colors.scheme
            val where = "COBALT ${if (dark) "dark" else "light"}"
            assertEquals(Color(canvas), colors.canvas, "$where canvas")
            assertEquals(Color(island), colors.island, "$where island")
            assertEquals(Color(on), s.onSurface, "$where on -> onSurface")
            assertEquals(Color(on), s.onBackground, "$where on -> onBackground")
            assertEquals(Color(onvar), s.onSurfaceVariant, "$where onvar -> onSurfaceVariant")
            assertEquals(Color(line), s.outlineVariant, "$where line -> outlineVariant")
            assertEquals(Color(primary), s.primary, "$where primary")
            assertEquals(Color(primary), s.surfaceTint, "$where surfaceTint follows primary")
            assertEquals(Color(soft), s.primaryContainer, "$where primary-soft -> primaryContainer")
            assertEquals(Color(wash), colors.wash, "$where wash")
        }
        assertCobalt(
            dark = false,
            canvas = 0xFFF5F7FB,
            island = 0xFFFFFFFF,
            on = 0xFF0E1B3D,
            onvar = 0xFF687187,
            line = 0xFFE9ECF3,
            primary = 0xFF2251FF,
            soft = 0xFFE7EDFF,
            // Not primary-soft: #687187 over primary-soft at the glow's 72% is 4.28:1 (CobaltPalette KDoc).
            wash = 0xFFFFFFFF,
        )
        assertCobalt(
            dark = true,
            canvas = 0xFF0B0E17,
            island = 0xFF151A27,
            on = 0xFFE8EBF4,
            onvar = 0xFF8E97AD,
            line = 0xFF222838,
            primary = 0xFF9DB1FF,
            soft = 0xFF1D2647,
            wash = 0xFF1D2647,
        )
    }

    @Test
    fun `Cobalt pins the brand tokens, with the dark navigation bar on the island tone`() {
        // D1: dark nav #151A27, not the mockup's #181D2B (the active pill gets 2.95:1 on it, 3.05:1 on the island).
        assertEquals(
            BrandColors(
                brand = Color(0xFF2251FF),
                onBrand = Color(0xFFFFFFFF),
                brandSoft = Color(0xFFE7EDFF),
                onBrandSoft = Color(0xFF2251FF),
                cardStart = Color(0xFF3463FF),
                cardMid = Color(0xFF2251FF),
                cardEnd = Color(0xFF1B44E0),
                onCard = Color(0xFFFFFFFF),
                cardGhost = Color(0xFFC7D4FF),
                cardGhost2 = Color(0xFFE2E9FF),
                nav = Color(0xFFFFFFFF),
            ),
            ThemePalette.COBALT.colors(dark = false).brand,
        )
        assertEquals(
            BrandColors(
                brand = Color(0xFF2251FF),
                onBrand = Color(0xFFFFFFFF),
                brandSoft = Color(0xFF1D2647),
                onBrandSoft = Color(0xFF9DB1FF),
                cardStart = Color(0xFF2D5BFF),
                cardMid = Color(0xFF2251FF),
                cardEnd = Color(0xFF1A3FCC),
                onCard = Color(0xFFFFFFFF),
                cardGhost = Color(0xFF22306A),
                cardGhost2 = Color(0xFF182148),
                nav = Color(0xFF151A27),
            ),
            ThemePalette.COBALT.colors(dark = true).brand,
        )
    }

    @Test
    fun `every palette in both modes has a complete, opaque set of brand tokens`() {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val b = palette.colors(dark).brand
                val tokens =
                    mapOf(
                        "brand" to b.brand,
                        "onBrand" to b.onBrand,
                        "brandSoft" to b.brandSoft,
                        "onBrandSoft" to b.onBrandSoft,
                        "cardStart" to b.cardStart,
                        "cardMid" to b.cardMid,
                        "cardEnd" to b.cardEnd,
                        "onCard" to b.onCard,
                        "cardGhost" to b.cardGhost,
                        "cardGhost2" to b.cardGhost2,
                        "nav" to b.nav,
                    )
                for ((name, token) in tokens) {
                    assertEquals(1f, token.alpha, "$palette dark=$dark $name is not opaque")
                    assertNotEquals(Color.Unspecified, token, "$palette dark=$dark $name is unspecified")
                }
            }
        }
    }

    @Test
    fun `the island is lighter than the canvas in light and in dark in every palette`() {
        // Tonal depth: the card lifts off the screen in both modes (mockup canvas vs island).
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val colors = palette.colors(dark)
                assertTrue(
                    colors.island.luminance() > colors.canvas.luminance(),
                    "$palette dark=$dark island is not lighter than the canvas",
                )
            }
        }
    }

    @Test
    fun `the persisted names are permanent`() {
        // AP1 stores `name`, so a rename or removal would silently reset a user's choice. Adding is fine.
        val names = ThemePalette.entries.map { it.name }
        listOf("GRAPHITE", "MIDNIGHT", "IVORY", "AUBERGINE", "COBALT").forEach {
            assertTrue(it in names, "$it must stay a ThemePalette entry")
        }
    }

    @Test
    fun `the dark surface ladder stays ordered around the pinned surface in every palette`() {
        // Aubergine's pinned dark surface is darker than the M3 ladder assumes, so its low and lowest
        // steps are adjusted; this guards the order for all palettes: lowest < surface < low < container.
        for (palette in ThemePalette.entries) {
            val s = palette.colors(dark = true).scheme
            val luminance =
                listOf(s.surfaceContainerLowest, s.surface, s.surfaceContainerLow, s.surfaceContainer)
                    .map { it.luminance() }
            assertEquals(luminance.sorted(), luminance, "$palette dark ladder is out of order")
            assertTrue(
                s.surfaceContainer.luminance() < s.surfaceContainerHigh.luminance(),
                "$palette container >= high",
            )
            assertTrue(s.surfaceDim.luminance() <= s.surface.luminance(), "$palette dim is lighter than surface")
        }
    }

    @Test
    fun `each palette has its display name`() {
        assertEquals(
            mapOf(
                ThemePalette.GRAPHITE to "Graphite",
                ThemePalette.MIDNIGHT to "Midnight",
                ThemePalette.IVORY to "Ivory",
                ThemePalette.AUBERGINE to "Aubergine",
                ThemePalette.COBALT to "Cobalt",
            ),
            ThemePalette.entries.associateWith { it.displayName },
        )
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
