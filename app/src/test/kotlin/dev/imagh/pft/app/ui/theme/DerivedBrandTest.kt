package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/** [derivedBrand]: the brand tokens of every palette that does not hand-pick them (plan 2026-11-25 D1). */
class DerivedBrandTest {
    // Every role gets its own colour, so a swapped or mistyped role changes the result.
    private val scheme =
        lightColorScheme(
            primary = Color(0xFF010203),
            onPrimary = Color(0xFF040506),
            primaryContainer = Color(0xFF070809),
            onPrimaryContainer = Color(0xFF0A0B0C),
            outlineVariant = Color(0xFF0D0E0F),
            surface = Color(0xFF101112),
            surfaceContainer = Color(0xFF131415),
            secondary = Color(0xFF161718),
            tertiary = Color(0xFF191A1B),
        )
    private val island = Color(0xFF1C1D1E)

    @Test
    fun `maps the scheme roles to the brand tokens`() {
        val brand = derivedBrand(scheme, island)
        assertEquals(scheme.primary, brand.brand, "brand")
        assertEquals(scheme.onPrimary, brand.onBrand, "onBrand")
        assertEquals(scheme.primaryContainer, brand.brandSoft, "brandSoft")
        assertEquals(scheme.onPrimaryContainer, brand.onBrandSoft, "onBrandSoft")
        assertEquals(scheme.onPrimary, brand.onCard, "onCard")
        assertEquals(scheme.primaryContainer, brand.cardGhost, "cardGhost")
        assertEquals(scheme.outlineVariant, brand.cardGhost2, "cardGhost2")
        assertEquals(island, brand.nav, "nav is the island that is passed in, not a scheme surface")
    }

    @Test
    fun `the card is a flat primary, three equal stops`() {
        val brand = derivedBrand(scheme, island)
        assertEquals(scheme.primary, brand.cardStart, "cardStart")
        assertEquals(scheme.primary, brand.cardMid, "cardMid")
        assertEquals(scheme.primary, brand.cardEnd, "cardEnd")
    }

    @Test
    fun `reads the scheme it is given, in dark too`() {
        val dark = darkColorScheme(primary = Color(0xFFAA0000), onPrimary = Color(0xFF00AA00))
        val brand = derivedBrand(dark, Color(0xFF123456))
        assertEquals(Color(0xFFAA0000), brand.brand)
        assertEquals(Color(0xFF00AA00), brand.onCard)
        assertNotEquals(derivedBrand(scheme, island), brand)
    }

    @Test
    fun `the four existing palettes carry exactly the derived tokens, no hand-picked values`() {
        for (palette in ThemePalette.entries - ThemePalette.COBALT) {
            for (dark in listOf(false, true)) {
                val colors = palette.colors(dark)
                assertEquals(
                    derivedBrand(colors.scheme, colors.island),
                    colors.brand,
                    "$palette dark=$dark brand is not derived",
                )
            }
        }
    }

    @Test
    fun `dynamic colour carries the derived tokens of its scheme and its island`() {
        for (dark in listOf(false, true)) {
            val system =
                if (dark) {
                    darkColorScheme(primary = Color(0xFF7799FF))
                } else {
                    lightColorScheme(primary = Color(0xFF3355CC))
                }
            val colors = dynamicPaletteColors(system, dark)
            assertEquals(derivedBrand(system, colors.island), colors.brand, "dynamic dark=$dark")
            assertEquals(system.surfaceContainerLow, colors.brand.nav, "dynamic dark=$dark nav is the island")
        }
    }

    @Test
    fun `Cobalt is the one palette whose brand is hand-picked, not derived`() {
        for (dark in listOf(false, true)) {
            val colors = ThemePalette.COBALT.colors(dark)
            assertNotEquals(derivedBrand(colors.scheme, colors.island), colors.brand, "COBALT dark=$dark")
        }
    }
}
