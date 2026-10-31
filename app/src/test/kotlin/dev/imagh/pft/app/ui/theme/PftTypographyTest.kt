package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
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

/**
 * The type scale under [PftTheme] (UI-2, UI-3, UI-15, owner decision Q10): by default the serif is
 * the hero number only and every M3 role, titles included, is the sans text family; the
 * `EDITORIAL` pairing sets the title and display roles and `amountLarge` in the serif too, each
 * cut for its own size.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftTypographyTest {
    @get:Rule
    val compose = HostedComposeRule()

    private class Resolved(
        val m3: Typography,
        val type: PftTypography,
    )

    private fun resolve(prefs: ThemePrefs = ThemePrefs(dynamicColor = false)): Resolved {
        lateinit var resolved: Resolved
        compose.setContent { PftTheme(prefs) { resolved = Resolved(MaterialTheme.typography, PftTheme.type) } }
        compose.waitForIdle()
        return resolved
    }

    private fun Typography.roles(): Map<String, TextStyle> =
        mapOf(
            "displayLarge" to displayLarge,
            "displayMedium" to displayMedium,
            "displaySmall" to displaySmall,
            "headlineLarge" to headlineLarge,
            "headlineMedium" to headlineMedium,
            "headlineSmall" to headlineSmall,
            "titleLarge" to titleLarge,
            "titleMedium" to titleMedium,
            "titleSmall" to titleSmall,
            "bodyLarge" to bodyLarge,
            "bodyMedium" to bodyMedium,
            "bodySmall" to bodySmall,
            "labelLarge" to labelLarge,
            "labelMedium" to labelMedium,
            "labelSmall" to labelSmall,
        )

    private fun FontFamily?.isBundledNewsreader(): Boolean {
        val fonts = (this as? FontListFontFamily)?.fonts.orEmpty()
        return fonts.isNotEmpty() && fonts.all { it is ResourceFont && it.resId == R.font.newsreader }
    }

    @Test
    fun `amountHero is the bundled Newsreader by default`() {
        assertTrue(
            resolve()
                .type.amountHero.fontFamily
                .isBundledNewsreader(),
        )
    }

    @Test
    fun `the bundled hero font is pinned to weight 400 and the 48sp optical size`() {
        val font = (resolve().type.amountHero.fontFamily as FontListFontFamily).fonts.single() as ResourceFont
        val axes = font.variationSettings.settings.associate { it.axisName to it.toVariationValue(Density(1f)) }
        assertEquals(400f, axes["wght"])
        assertEquals(48f, axes["opsz"])
    }

    /** The `wght` and `opsz` axes pinned on a bundled Newsreader family, read off its font. */
    private fun FontFamily?.newsreaderAxes(): Map<String, Float> {
        val font = (this as FontListFontFamily).fonts.single() as ResourceFont
        return font.variationSettings.settings.associate { it.axisName to it.toVariationValue(Density(1f)) }
    }

    @Test
    fun `newsreader pins the weight axis to the weight and the optical size to the font size`() {
        val axes = newsreader(FontWeight.Medium, 16.sp).newsreaderAxes()
        assertEquals(500f, axes["wght"])
        assertEquals(16f, axes["opsz"])
        assertTrue(newsreader(FontWeight.Medium, 16.sp).isBundledNewsreader())
    }

    @Test
    fun `newsreader clamps the optical size to the font's 6 to 72 range`() {
        assertEquals(6f, newsreader(FontWeight.Normal, 3.sp).newsreaderAxes()["opsz"])
        assertEquals(72f, newsreader(FontWeight.Normal, 110.sp).newsreaderAxes()["opsz"])
        assertEquals(57f, newsreader(FontWeight.Normal, 57.sp).newsreaderAxes()["opsz"])
    }

    @Test
    fun `every M3 role uses the system sans family, titles included`() {
        val roles = resolve().m3.roles()
        assertEquals(15, roles.size)
        roles.forEach { (name, style) -> assertEquals("$name family", FontFamily.Default, style.fontFamily) }
    }

    @Test
    fun `the amount roles other than the hero are sans`() {
        val type = resolve().type
        assertEquals(FontFamily.Default, type.amountLarge.fontFamily)
        assertEquals(FontFamily.Default, type.amountRow.fontFamily)
    }

    @Test
    fun `displayFont TEXT puts the hero in the text family`() {
        val type = resolve(ThemePrefs(displayFont = DisplayFont.TEXT, dynamicColor = false)).type
        assertEquals(FontFamily.Default, type.amountHero.fontFamily)
    }

    @Test
    fun `displayFont SYSTEM_SERIF puts the hero in the system serif`() {
        val type = resolve(ThemePrefs(displayFont = DisplayFont.SYSTEM_SERIF, dynamicColor = false)).type
        assertEquals(FontFamily.Serif, type.amountHero.fontFamily)
    }

    @Test
    fun `the default display font is the bundled serif`() {
        assertEquals(DisplayFont.BUNDLED_SERIF, ThemePrefs.Default.displayFont)
    }

    @Test
    fun `all three amount roles carry tabular figures`() {
        val type = resolve().type
        listOf(type.amountHero, type.amountLarge, type.amountRow).forEach { style ->
            assertTrue("tnum in ${style.fontFeatureSettings}", style.fontFeatureSettings.orEmpty().contains("tnum"))
        }
    }

    @Test
    fun `hero numerals are lining and tabular, set a little looser than the font default`() {
        val hero = resolve().type.amountHero
        assertTrue(hero.fontFeatureSettings.orEmpty().contains("lnum"))
        assertEquals(0.01.em, hero.letterSpacing)
        assertEquals(48.sp, hero.fontSize)
        assertEquals(60.sp, hero.lineHeight)
        assertEquals(FontWeight.Normal, hero.fontWeight)
    }

    @Test
    fun `amountLarge and amountRow follow the scale`() {
        val type = resolve().type
        assertEquals(32.sp, type.amountLarge.fontSize)
        assertEquals(40.sp, type.amountLarge.lineHeight)
        assertEquals(FontWeight.Medium, type.amountLarge.fontWeight)
        assertEquals(16.sp, type.amountRow.fontSize)
        assertEquals(24.sp, type.amountRow.lineHeight)
        assertEquals(FontWeight.Medium, type.amountRow.fontWeight)
    }

    @Test
    fun `header titles use the specified sizes and weights`() {
        val m3 = resolve().m3
        assertEquals(32.sp, m3.headlineLarge.fontSize)
        assertEquals(40.sp, m3.headlineLarge.lineHeight)
        assertEquals(FontWeight.SemiBold, m3.headlineLarge.fontWeight)
        assertEquals(22.sp, m3.titleLarge.fontSize)
        assertEquals(28.sp, m3.titleLarge.lineHeight)
        assertEquals(FontWeight.Medium, m3.titleLarge.fontWeight)
    }

    // --- type pairing (UI-15, DS6g) -------------------------------------------------------

    private fun pairing(
        pairing: TypePairing,
        displayFont: DisplayFont = DisplayFont.BUNDLED_SERIF,
    ) = resolve(ThemePrefs(dynamicColor = false, displayFont = displayFont, typePairing = pairing))

    /** The M3 roles `EDITORIAL` sets in the display family (design plan §3.6); `titleSmall` is a tab label, so sans. */
    private val editorialM3Roles =
        setOf(
            "displayLarge",
            "displayMedium",
            "displaySmall",
            "headlineLarge",
            "headlineMedium",
            "headlineSmall",
            "titleLarge",
            "titleMedium",
        )

    /** One variation axis (`wght` or `opsz`) pinned on this style's Newsreader family. */
    private fun TextStyle.axis(name: String): Float? = fontFamily.newsreaderAxes()[name]

    /** The same roles built straight from the pure functions, for tests that compare several pairings. */
    private fun built(
        pairing: TypePairing,
        displayFont: DisplayFont = DisplayFont.BUNDLED_SERIF,
    ) = Resolved(
        pftMaterialTypography(FontFamily.Default, displayFont, pairing),
        pftType(displayFont, FontFamily.Default, pairing),
    )

    @Test
    fun `HERO_ONLY sets only the hero in Newsreader, at weight 400 and the 48sp cut`() {
        val resolved = pairing(TypePairing.HERO_ONLY)
        assertTrue(
            resolved.type.amountHero.fontFamily
                .isBundledNewsreader(),
        )
        assertEquals(400f, resolved.type.amountHero.axis("wght"))
        assertEquals(48f, resolved.type.amountHero.axis("opsz"))
        assertEquals(FontFamily.Default, resolved.type.amountLarge.fontFamily)
        assertEquals(FontFamily.Default, resolved.type.amountRow.fontFamily)
        resolved.m3.roles().forEach { (name, style) ->
            assertEquals("$name family", FontFamily.Default, style.fontFamily)
        }
    }

    @Test
    fun `EDITORIAL sets the hero, amountLarge and the display, headline and larger title roles in Newsreader`() {
        val resolved = pairing(TypePairing.EDITORIAL)
        assertTrue(
            resolved.type.amountHero.fontFamily
                .isBundledNewsreader(),
        )
        assertTrue(
            resolved.type.amountLarge.fontFamily
                .isBundledNewsreader(),
        )
        resolved.m3.roles().forEach { (name, style) ->
            if (name in editorialM3Roles) {
                assertTrue("$name is Newsreader", style.fontFamily.isBundledNewsreader())
            } else {
                assertEquals("$name family", FontFamily.Default, style.fontFamily)
            }
        }
    }

    @Test
    fun `EDITORIAL keeps tab labels, body, label text and row amounts in the sans text family`() {
        val resolved = pairing(TypePairing.EDITORIAL)
        assertEquals(FontFamily.Default, resolved.type.amountRow.fontFamily)
        listOf(
            resolved.m3.titleSmall,
            resolved.m3.bodyLarge,
            resolved.m3.bodyMedium,
            resolved.m3.bodySmall,
            resolved.m3.labelLarge,
            resolved.m3.labelMedium,
            resolved.m3.labelSmall,
        ).forEach { assertEquals(FontFamily.Default, it.fontFamily) }
    }

    @Test
    fun `each EDITORIAL serif role is cut for its own size and pinned to its own weight`() {
        val resolved = pairing(TypePairing.EDITORIAL)
        val serif: Map<String, TextStyle> =
            resolved.m3.roles().filterKeys { it in editorialM3Roles } +
                mapOf("amountHero" to resolved.type.amountHero, "amountLarge" to resolved.type.amountLarge)
        assertEquals(10, serif.size)
        serif.forEach { (name, style) ->
            assertEquals("$name opsz", style.fontSize.value.coerceIn(6f, 72f), style.axis("opsz"))
            assertEquals("$name wght", style.fontWeight?.weight?.toFloat(), style.axis("wght"))
        }
        // Spot values, so the loop above is not just the implementation restated.
        assertEquals(16f, resolved.m3.titleMedium.axis("opsz"))
        assertEquals(500f, resolved.m3.titleMedium.axis("wght"))
        assertEquals(57f, resolved.m3.displayLarge.axis("opsz"))
        assertEquals(48f, resolved.type.amountHero.axis("opsz"))
        assertEquals(400f, resolved.type.amountHero.axis("wght"))
        assertEquals(32f, resolved.type.amountLarge.axis("opsz"))
        assertEquals(500f, resolved.type.amountLarge.axis("wght"))
    }

    @Test
    fun `the pairing changes only the family, never a size, weight or spacing`() {
        val heroOnly = built(TypePairing.HERO_ONLY)
        val editorial = built(TypePairing.EDITORIAL)

        fun TextStyle.sizing() = listOf(fontSize, lineHeight, fontWeight, letterSpacing, fontFeatureSettings)
        heroOnly.m3.roles().forEach { (name, style) ->
            assertEquals(
                "$name sizing",
                style.sizing(),
                editorial.m3
                    .roles()
                    .getValue(name)
                    .sizing(),
            )
        }
        assertEquals(heroOnly.type.amountHero.sizing(), editorial.type.amountHero.sizing())
        assertEquals(heroOnly.type.amountLarge.sizing(), editorial.type.amountLarge.sizing())
        assertEquals(heroOnly.type.amountRow.sizing(), editorial.type.amountRow.sizing())
    }

    @Test
    fun `displayFont TEXT with EDITORIAL is all sans, with no Newsreader at all`() {
        val resolved = pairing(TypePairing.EDITORIAL, DisplayFont.TEXT)
        val every =
            resolved.m3.roles().values +
                listOf(resolved.type.amountHero, resolved.type.amountLarge, resolved.type.amountRow)
        every.forEach { assertEquals(FontFamily.Default, it.fontFamily) }
    }

    @Test
    fun `displayFont SYSTEM_SERIF with EDITORIAL gives the platform serif with no variation settings`() {
        val resolved = pairing(TypePairing.EDITORIAL, DisplayFont.SYSTEM_SERIF)
        val serif =
            resolved.m3
                .roles()
                .filterKeys { it in editorialM3Roles }
                .values +
                listOf(resolved.type.amountHero, resolved.type.amountLarge)
        serif.forEach {
            assertEquals(FontFamily.Serif, it.fontFamily)
            assertFalse("system serif is not a bundled font list", it.fontFamily is FontListFontFamily)
        }
        assertEquals(FontFamily.Default, resolved.m3.bodyLarge.fontFamily)
    }

    @Test
    fun `tabular figures stay on all three amount roles in every pairing and display font`() {
        TypePairing.entries.forEach { choice ->
            DisplayFont.entries.forEach { font ->
                val type = built(choice, font).type
                listOf(type.amountHero, type.amountLarge, type.amountRow).forEach { style ->
                    assertTrue("tnum in $choice/$font", style.fontFeatureSettings.orEmpty().contains("tnum"))
                }
            }
        }
    }

    @Test
    fun `the hero shrink steps start at the plan's 48, 40, 34, 28sp and only ever go down`() {
        val sizes = HeroSizeSteps.map { it.value }
        assertEquals(listOf(48f, 40f, 34f, 28f), sizes.take(4))
        assertEquals(resolve().type.amountHero.fontSize, HeroSizeSteps.first())
        assertEquals(sizes.sortedDescending(), sizes)
        assertEquals(sizes.distinct(), sizes)
    }
}
