package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/**
 * Money digits are tabular in every bundled font (UI-16): under each amount style the widest and
 * narrowest figure strings of the same shape measure the same, so columns of amounts line up and
 * rolling digits do not jitter. The digits of Figtree and Plus Jakarta Sans are proportional by
 * default and only `tnum` makes them equal, so this checks that the amount styles really apply it;
 * Newsreader's default figures are already tabular and `tnum` keeps that true. Manrope and Bricolage
 * Grotesque (Cobalt plan D2) both carry a `tnum` feature; Bricolage is the hero only, so it is
 * measured through the hero style. Both must also draw the rupee sign and the true minus themselves
 * (see the glyph tests), or amounts would fall back to a system glyph.
 *
 * Measured through Compose's own [TextMeasurer] on the styles `pftType` builds, with the real
 * bundled fonts and Robolectric native graphics, which applies `fontFeatureSettings` (the negative
 * control shows it: without `tnum` the same Figtree and Plus Jakarta Sans strings differ).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TabularFiguresTest {
    @get:Rule
    val compose = HostedComposeRule()

    /** Width in px of each of [texts] set in each of [styles], measured by a real [TextMeasurer]. */
    private fun widths(
        styles: Map<String, TextStyle>,
        vararg texts: String,
    ): Map<String, List<Float>> {
        val result = mutableMapOf<String, List<Float>>()
        compose.setContent {
            val measurer = rememberTextMeasurer()
            styles.forEach { (name, style) ->
                result[name] =
                    texts.map {
                        measurer
                            .measure(it, style)
                            .size.width
                            .toFloat()
                    }
            }
        }
        compose.waitForIdle()
        return result
    }

    private fun PftTypography.all() = mapOf("hero" to amountHero, "large" to amountLarge, "row" to amountRow)

    /** The amount styles as the theme builds them for [display] over the bundled [sans] (Figtree by default). */
    private fun amountStyles(
        display: DisplayFont,
        pairing: TypePairing,
        sans: SansFont = SansFont.FIGTREE,
    ) = pftType(display, sansFamily(sans), pairing)

    private fun assertTabular(widths: Map<String, List<Float>>) {
        widths.forEach { (name, w) ->
            assertEquals("$name: $WIDE vs $NARROW in px", w[0], w[1], TOLERANCE_PX)
            assertTrue("$name measured a real width", w[0] > 0f)
        }
    }

    @Test
    fun `Figtree amounts of the same shape measure the same width under every amount style`() {
        // displayFont TEXT puts the hero and large amount in the sans too, so all three are Figtree.
        val styles = amountStyles(DisplayFont.TEXT, TypePairing.EDITORIAL).all()
        assertEquals(3, styles.size)
        assertTabular(widths(styles, WIDE, NARROW))
    }

    @Test
    fun `Plus Jakarta Sans amounts of the same shape measure the same width under every amount style`() {
        val styles = amountStyles(DisplayFont.TEXT, TypePairing.EDITORIAL, SansFont.PLUS_JAKARTA_SANS).all()
        assertEquals(3, styles.size)
        assertTabular(widths(styles, WIDE, NARROW))
    }

    @Test
    fun `Newsreader amounts of the same shape measure the same width under its amount styles`() {
        // Unlike the sans fonts, this passes even without tnum: Newsreader's default digits are already tabular.
        // The hero (HERO_ONLY and EDITORIAL) and amountLarge (EDITORIAL) are the Newsreader roles.
        val type = amountStyles(DisplayFont.BUNDLED_SERIF, TypePairing.EDITORIAL)
        assertTabular(widths(mapOf("hero" to type.amountHero, "large" to type.amountLarge), WIDE, NARROW))
    }

    @Test
    fun `negative control - Figtree without tnum measures the two strings differently`() {
        val styles =
            amountStyles(DisplayFont.TEXT, TypePairing.EDITORIAL)
                .all()
                .mapValues { (_, style) -> style.copy(fontFeatureSettings = null) }
        val plain = widths(styles, WIDE, NARROW)
        plain.forEach { (name, w) ->
            assertTrue("$name should differ without tnum, got ${w[0]} vs ${w[1]}", abs(w[0] - w[1]) > TOLERANCE_PX)
        }
    }

    @Test
    fun `negative control - Plus Jakarta Sans without tnum measures the two strings differently`() {
        val styles =
            amountStyles(DisplayFont.TEXT, TypePairing.EDITORIAL, SansFont.PLUS_JAKARTA_SANS)
                .all()
                .mapValues { (_, style) -> style.copy(fontFeatureSettings = null) }
        val plain = widths(styles, WIDE, NARROW)
        plain.forEach { (name, w) ->
            assertTrue("$name should differ without tnum, got ${w[0]} vs ${w[1]}", abs(w[0] - w[1]) > TOLERANCE_PX)
        }
    }

    @Test
    fun `Manrope amounts of the same shape measure the same width under every amount style`() {
        // Like Newsreader, this passes even without tnum: Manrope's default digits are already tabular.
        val styles = amountStyles(DisplayFont.TEXT, TypePairing.EDITORIAL, SansFont.MANROPE).all()
        assertEquals(3, styles.size)
        assertTabular(widths(styles, WIDE, NARROW))
    }

    @Test
    fun `Bricolage Grotesque hero amounts of the same shape measure the same width`() {
        val type = amountStyles(DisplayFont.BUNDLED_GROTESQUE, TypePairing.EDITORIAL)
        assertTabular(widths(mapOf("hero" to type.amountHero, "large" to type.amountLarge), WIDE, NARROW))
    }

    @Test
    fun `negative control - Manrope with proportional figures measures the two strings differently`() {
        // Manrope's default digits are already tabular (like Newsreader's), so dropping tnum cannot show
        // the measurement bites; pnum, the opposite feature, does: the same strings must then differ.
        val styles =
            amountStyles(DisplayFont.TEXT, TypePairing.EDITORIAL, SansFont.MANROPE)
                .all()
                .mapValues { (_, style) -> style.copy(fontFeatureSettings = "pnum") }
        val proportional = widths(styles, WIDE, NARROW)
        proportional.forEach { (name, w) ->
            assertTrue("$name should differ under pnum, got ${w[0]} vs ${w[1]}", abs(w[0] - w[1]) > TOLERANCE_PX)
        }
    }

    @Test
    fun `negative control - the Bricolage Grotesque hero without tnum measures the two strings differently`() {
        val hero = amountStyles(DisplayFont.BUNDLED_GROTESQUE, TypePairing.HERO_ONLY).amountHero
        val plain = widths(mapOf("hero" to hero.copy(fontFeatureSettings = null)), WIDE, NARROW)
        plain.forEach { (name, w) ->
            assertTrue("$name should differ without tnum, got ${w[0]} vs ${w[1]}", abs(w[0] - w[1]) > TOLERANCE_PX)
        }
    }

    // --- the font files themselves (Cobalt plan D2 [Verify]) --------------------------------

    private fun facts(resId: Int): FontFileFacts {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return FontFileFacts(context.resources.openRawResource(resId).use { it.readBytes() })
    }

    private val cobaltFonts = mapOf("manrope" to R.font.manrope, "bricolage_grotesque" to R.font.bricolage_grotesque)

    @Test
    fun `Manrope and Bricolage Grotesque each have their own glyph for the rupee sign and the true minus`() {
        cobaltFonts.forEach { (name, id) ->
            val font = facts(id)
            assertTrue("$name has no glyph for U+20B9", font.hasGlyph(0x20B9))
            assertTrue("$name has no glyph for U+2212", font.hasGlyph(0x2212))
            assertTrue("$name has no glyph for the digit 0 (the parser reads the file)", font.hasGlyph('0'.code))
        }
    }

    @Test
    fun `negative control - the glyph reader says no for a character neither font has`() {
        // A CJK ideograph and an astral code point: Paint.hasGlyph said true for the first (system
        // fallback), so the file reader is what proves coverage. This shows it can say no.
        cobaltFonts.forEach { (name, id) ->
            assertFalse("$name should have no U+65E5", facts(id).hasGlyph(0x65E5))
            assertFalse("$name should have no U+1F600", facts(id).hasGlyph(0x1F600))
        }
    }

    @Test
    fun `Manrope and Bricolage Grotesque each offer the tnum feature, and Bricolage the lnum one`() {
        cobaltFonts.forEach { (name, id) -> assertTrue("$name has no tnum", "tnum" in facts(id).gsubFeatures()) }
        assertTrue("lnum" in facts(R.font.bricolage_grotesque).gsubFeatures())
    }

    @Test
    fun `negative control - the feature reader says no for a feature neither font has`() {
        cobaltFonts.forEach { (name, id) -> assertFalse("$name has no smcp", "smcp" in facts(id).gsubFeatures()) }
    }

    private companion object {
        const val WIDE = "₹11,111.11"
        const val NARROW = "₹00,000.00"
        const val TOLERANCE_PX = 0.5f
    }
}
