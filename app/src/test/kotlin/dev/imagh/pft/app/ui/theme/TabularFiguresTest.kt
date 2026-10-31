package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
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
 * rolling digits do not jitter. Figtree's digits are proportional by default and only `tnum` makes
 * them equal, so this checks that the amount styles really apply it; Newsreader's default figures
 * are already tabular and `tnum` keeps that true.
 *
 * Measured through Compose's own [TextMeasurer] on the styles `pftType` builds, with the real
 * bundled fonts and Robolectric native graphics, which applies `fontFeatureSettings` (the negative
 * control shows it: without `tnum` the same Figtree strings differ).
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

    /** The amount styles as the theme builds them for [display] over the bundled Figtree. */
    private fun amountStyles(
        display: DisplayFont,
        pairing: TypePairing,
    ) = pftType(display, sansFamily(SansFont.FIGTREE), pairing)

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
    fun `Newsreader amounts of the same shape measure the same width under its amount styles`() {
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

    private companion object {
        const val WIDE = "₹11,111.11"
        const val NARROW = "₹00,000.00"
        const val TOLERANCE_PX = 0.5f
    }
}
