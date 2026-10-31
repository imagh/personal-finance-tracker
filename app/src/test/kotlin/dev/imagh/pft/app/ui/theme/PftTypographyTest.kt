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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The type scale under [PftTheme] (UI-2, UI-3, owner decision Q10): the serif is the hero number
 * only; every M3 role, titles included, is the sans text family.
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

    @Test
    fun `the hero shrink steps start at the plan's 48, 40, 34, 28sp and only ever go down`() {
        val sizes = HeroSizeSteps.map { it.value }
        assertEquals(listOf(48f, 40f, 34f, 28f), sizes.take(4))
        assertEquals(resolve().type.amountHero.fontSize, HeroSizeSteps.first())
        assertEquals(sizes.sortedDescending(), sizes)
        assertEquals(sizes.distinct(), sizes)
    }
}
