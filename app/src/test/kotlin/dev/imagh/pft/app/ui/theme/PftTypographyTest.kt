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

    /** The default sans (UI-2): the bundled Figtree. */
    private val figtree = sansFamily(SansFont.FIGTREE)

    private class Resolved(
        val m3: Typography,
        val type: PftTypography,
    )

    /** The look before Cobalt became the default (Figtree sans, Newsreader hero): what these tests pin. */
    private val legacy =
        ThemePrefs(dynamicColor = false, sansFont = SansFont.FIGTREE, displayFont = DisplayFont.BUNDLED_SERIF)

    private fun resolve(prefs: ThemePrefs = legacy): Resolved {
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
    fun `amountHero is the bundled Newsreader under the pre-Cobalt prefs`() {
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
    fun `every M3 role uses the bundled Figtree, titles included`() {
        val roles = resolve().m3.roles()
        assertEquals(15, roles.size)
        roles.forEach { (name, style) -> assertEquals("$name family", figtree, style.fontFamily) }
    }

    @Test
    fun `the amount roles other than the hero are Figtree`() {
        val type = resolve().type
        assertEquals(figtree, type.amountLarge.fontFamily)
        assertEquals(figtree, type.amountRow.fontFamily)
    }

    @Test
    fun `displayFont TEXT puts the hero in Figtree`() {
        val type = resolve(legacy.copy(displayFont = DisplayFont.TEXT, dynamicColor = false)).type
        assertEquals(figtree, type.amountHero.fontFamily)
    }

    @Test
    fun `displayFont SYSTEM_SERIF puts the hero in the system serif`() {
        val type = resolve(legacy.copy(displayFont = DisplayFont.SYSTEM_SERIF, dynamicColor = false)).type
        assertEquals(FontFamily.Serif, type.amountHero.fontFamily)
    }

    @Test
    fun `the default display font is the bundled grotesque`() {
        assertEquals(DisplayFont.BUNDLED_GROTESQUE, ThemePrefs.Default.displayFont)
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
    ) = resolve(legacy.copy(dynamicColor = false, displayFont = displayFont, typePairing = pairing))

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
        pftMaterialTypography(figtree, displayFont, pairing),
        pftType(displayFont, figtree, pairing),
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
        assertEquals(figtree, resolved.type.amountLarge.fontFamily)
        assertEquals(figtree, resolved.type.amountRow.fontFamily)
        resolved.m3.roles().forEach { (name, style) ->
            assertEquals("$name family", figtree, style.fontFamily)
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
                assertEquals("$name family", figtree, style.fontFamily)
            }
        }
    }

    @Test
    fun `EDITORIAL keeps tab labels, body, label text and row amounts in Figtree`() {
        val resolved = pairing(TypePairing.EDITORIAL)
        assertEquals(figtree, resolved.type.amountRow.fontFamily)
        listOf(
            resolved.m3.titleSmall,
            resolved.m3.bodyLarge,
            resolved.m3.bodyMedium,
            resolved.m3.bodySmall,
            resolved.m3.labelLarge,
            resolved.m3.labelMedium,
            resolved.m3.labelSmall,
        ).forEach { assertEquals(figtree, it.fontFamily) }
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
        every.forEach { assertEquals(figtree, it.fontFamily) }
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
        assertEquals(figtree, resolved.m3.bodyLarge.fontFamily)
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

    // --- the sans family (UI-2, UI-16, DS6h) ------------------------------------------------

    /** The `wght` axis pinned on one bundled [ResourceFont]. */
    private fun ResourceFont.wght(): Float? =
        variationSettings.settings.associate { it.axisName to it.toVariationValue(Density(1f)) }["wght"]

    @Test
    fun `FIGTREE declares one font per weight 400, 500 and 600, each pinned to its own wght axis`() {
        val fonts = (sansFamily(SansFont.FIGTREE) as FontListFontFamily).fonts.map { it as ResourceFont }
        assertEquals(listOf(400, 500, 600), fonts.map { it.weight.weight })
        // Figtree's own wght default is 300, so an undeclared axis would render every role too light.
        assertEquals(listOf(400f, 500f, 600f), fonts.map { it.wght() })
        fonts.forEach { assertEquals(R.font.figtree, it.resId) }
    }

    @Test
    fun `PLUS_JAKARTA_SANS declares one font per weight 400, 500 and 600, each pinned to its own wght axis`() {
        val fonts = (sansFamily(SansFont.PLUS_JAKARTA_SANS) as FontListFontFamily).fonts.map { it as ResourceFont }
        assertEquals(listOf(400, 500, 600), fonts.map { it.weight.weight })
        // The font's wght default is 400 but the axis is still set, so each weight really is its own cut.
        assertEquals(listOf(400f, 500f, 600f), fonts.map { it.wght() })
        fonts.forEach { assertEquals(R.font.plus_jakarta_sans, it.resId) }
    }

    @Test
    fun `each SansFont entry maps to its own family`() {
        fun FontFamily.resIds() = (this as FontListFontFamily).fonts.map { (it as ResourceFont).resId }.toSet()
        SansFont.entries.forEach { choice ->
            val family = sansFamily(choice)
            when (choice) {
                SansFont.FIGTREE -> assertEquals(setOf(R.font.figtree), family.resIds())
                SansFont.PLUS_JAKARTA_SANS -> assertEquals(setOf(R.font.plus_jakarta_sans), family.resIds())
                SansFont.MANROPE -> assertEquals(setOf(R.font.manrope), family.resIds())
                SansFont.SYSTEM -> assertEquals(FontFamily.Default, family)
            }
        }
        assertEquals(
            SansFont.entries.size,
            SansFont.entries
                .map { sansFamily(it) }
                .toSet()
                .size,
        )
    }

    @Test
    fun `SansFont PLUS_JAKARTA_SANS puts every sans role in Plus Jakarta Sans, the serif roles stay Newsreader`() {
        val jakarta = sansFamily(SansFont.PLUS_JAKARTA_SANS)
        val resolved =
            resolve(
                legacy.copy(
                    dynamicColor = false,
                    sansFont = SansFont.PLUS_JAKARTA_SANS,
                    typePairing = TypePairing.EDITORIAL,
                ),
            )
        resolved.m3.roles().forEach { (name, style) ->
            if (name in editorialM3Roles) {
                assertTrue("$name stays Newsreader", style.fontFamily.isBundledNewsreader())
            } else {
                assertEquals("$name family", jakarta, style.fontFamily)
            }
        }
        assertEquals(jakarta, resolved.type.amountRow.fontFamily)
        assertTrue(
            resolved.type.amountHero.fontFamily
                .isBundledNewsreader(),
        )
        assertTrue(
            resolved.type.amountLarge.fontFamily
                .isBundledNewsreader(),
        )
    }

    @Test
    fun `SYSTEM is the platform default family`() {
        assertEquals(FontFamily.Default, sansFamily(SansFont.SYSTEM))
    }

    @Test
    fun `SansFont SYSTEM puts every sans role in the platform default, the serif roles unchanged`() {
        val resolved =
            resolve(legacy.copy(dynamicColor = false, sansFont = SansFont.SYSTEM, typePairing = TypePairing.EDITORIAL))
        resolved.m3.roles().forEach { (name, style) ->
            if (name in editorialM3Roles) {
                assertTrue("$name stays Newsreader", style.fontFamily.isBundledNewsreader())
            } else {
                assertEquals("$name family", FontFamily.Default, style.fontFamily)
            }
        }
        assertEquals(FontFamily.Default, resolved.type.amountRow.fontFamily)
        assertTrue(
            resolved.type.amountHero.fontFamily
                .isBundledNewsreader(),
        )
    }

    @Test
    fun `displayFont TEXT with SYSTEM sans puts every role, the hero included, in the platform default`() {
        val resolved =
            resolve(
                legacy.copy(
                    dynamicColor = false,
                    sansFont = SansFont.SYSTEM,
                    displayFont = DisplayFont.TEXT,
                    typePairing = TypePairing.EDITORIAL,
                ),
            )
        val every =
            resolved.m3.roles().values +
                listOf(resolved.type.amountHero, resolved.type.amountLarge, resolved.type.amountRow)
        every.forEach { assertEquals(FontFamily.Default, it.fontFamily) }
    }

    @Test
    fun `every weight a role asks of the sans is one each bundled sans declares`() {
        listOf(SansFont.FIGTREE, SansFont.PLUS_JAKARTA_SANS, SansFont.MANROPE).forEach { choice ->
            val sans = sansFamily(choice)
            val declared = (sans as FontListFontFamily).fonts.map { it.weight }.toSet()
            TypePairing.entries.forEach { pairing ->
                val m3 = pftMaterialTypography(sans, DisplayFont.TEXT, pairing)
                val type = pftType(DisplayFont.TEXT, sans, pairing)
                (m3.roles().values + listOf(type.amountHero, type.amountLarge, type.amountRow)).forEach { style ->
                    val weight = style.fontWeight ?: FontWeight.Normal
                    assertTrue("$weight is declared by $choice ($pairing)", weight in declared)
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

    // --- Manrope and Bricolage Grotesque (Cobalt plan D2, K2) -------------------------------

    @Test
    fun `MANROPE declares one font per weight 400, 500 and 600, each pinned to its own wght axis`() {
        val fonts = (sansFamily(SansFont.MANROPE) as FontListFontFamily).fonts.map { it as ResourceFont }
        assertEquals(listOf(400, 500, 600), fonts.map { it.weight.weight })
        // Manrope's own wght default is 200, so an undeclared axis would render every role far too light.
        assertEquals(listOf(400f, 500f, 600f), fonts.map { it.wght() })
        fonts.forEach { assertEquals(R.font.manrope, it.resId) }
    }

    @Test
    fun `SansFont MANROPE puts every sans role in Manrope, and the hero stays Newsreader`() {
        val manrope = sansFamily(SansFont.MANROPE)
        val resolved =
            resolve(legacy.copy(dynamicColor = false, sansFont = SansFont.MANROPE, typePairing = TypePairing.EDITORIAL))
        resolved.m3.roles().forEach { (name, style) ->
            if (name in editorialM3Roles) {
                assertTrue("$name stays Newsreader", style.fontFamily.isBundledNewsreader())
            } else {
                assertEquals("$name family", manrope, style.fontFamily)
            }
        }
        assertEquals(manrope, resolved.type.amountRow.fontFamily)
        assertTrue(
            resolved.type.amountHero.fontFamily
                .isBundledNewsreader(),
        )
    }

    @Test
    fun `by default every Manrope role, titles and amountLarge included, is Manrope`() {
        val manrope = sansFamily(SansFont.MANROPE)
        val resolved = resolve(legacy.copy(dynamicColor = false, sansFont = SansFont.MANROPE))
        assertEquals(15, resolved.m3.roles().size)
        resolved.m3.roles().forEach { (name, style) -> assertEquals("$name family", manrope, style.fontFamily) }
        assertEquals(manrope, resolved.type.amountLarge.fontFamily)
        assertEquals(manrope, resolved.type.amountRow.fontFamily)
    }

    private fun FontFamily?.isBundledBricolage(): Boolean {
        val fonts = (this as? FontListFontFamily)?.fonts.orEmpty()
        return fonts.isNotEmpty() && fonts.all { it is ResourceFont && it.resId == R.font.bricolage_grotesque }
    }

    private fun TextStyle.bricolageAxes(): Map<String, Float> = fontFamily.newsreaderAxes()

    private fun grotesque(pairing: TypePairing = TypePairing.HERO_ONLY) =
        resolve(legacy.copy(dynamicColor = false, displayFont = DisplayFont.BUNDLED_GROTESQUE, typePairing = pairing))

    @Test
    fun `BUNDLED_GROTESQUE makes the hero Bricolage at weight 700, 34sp, opsz 34, wdth 100, tnum and lnum`() {
        val hero = grotesque().type.amountHero
        assertTrue(hero.fontFamily.isBundledBricolage())
        val axes = hero.bricolageAxes()
        assertEquals(700f, axes["wght"])
        assertEquals(34f, axes["opsz"])
        assertEquals(100f, axes["wdth"])
        assertEquals(FontWeight.Bold, hero.fontWeight)
        assertEquals(34.sp, hero.fontSize)
        assertEquals(42.5.sp, hero.lineHeight)
        assertEquals("tnum, lnum", hero.fontFeatureSettings)
    }

    @Test
    fun `BUNDLED_GROTESQUE sets only the hero in Bricolage under HERO_ONLY`() {
        val resolved = grotesque()
        assertEquals(figtree, resolved.type.amountLarge.fontFamily)
        assertEquals(figtree, resolved.type.amountRow.fontFamily)
        resolved.m3.roles().forEach { (name, style) -> assertEquals("$name family", figtree, style.fontFamily) }
    }

    @Test
    fun `bricolage pins weight, cuts opsz for the size clamped to 12 to 96, and keeps width 100`() {
        val axes = bricolage(FontWeight.Medium, 16.sp).newsreaderAxes()
        assertEquals(500f, axes["wght"])
        assertEquals(16f, axes["opsz"])
        assertEquals(100f, axes["wdth"])
        assertTrue(bricolage(FontWeight.Medium, 16.sp).isBundledBricolage())
        assertEquals(12f, bricolage(FontWeight.Normal, 3.sp).newsreaderAxes()["opsz"])
        assertEquals(96f, bricolage(FontWeight.Normal, 140.sp).newsreaderAxes()["opsz"])
        assertEquals(57f, bricolage(FontWeight.Normal, 57.sp).newsreaderAxes()["opsz"])
    }

    @Test
    fun `EDITORIAL with BUNDLED_GROTESQUE sets the display roles in Bricolage, each at its own size`() {
        val resolved = grotesque(TypePairing.EDITORIAL)
        val bricolageRoles: Map<String, TextStyle> =
            resolved.m3.roles().filterKeys { it in editorialM3Roles } +
                mapOf("amountHero" to resolved.type.amountHero, "amountLarge" to resolved.type.amountLarge)
        assertEquals(10, bricolageRoles.size)
        bricolageRoles.forEach { (name, style) ->
            assertTrue("$name is Bricolage", style.fontFamily.isBundledBricolage())
            assertEquals("$name opsz", style.fontSize.value.coerceIn(12f, 96f), style.bricolageAxes()["opsz"])
            assertEquals("$name wght", style.fontWeight?.weight?.toFloat(), style.bricolageAxes()["wght"])
        }
        assertEquals(16f, resolved.m3.titleMedium.bricolageAxes()["opsz"])
        assertEquals(500f, resolved.m3.titleMedium.bricolageAxes()["wght"])
        assertEquals(figtree, resolved.type.amountRow.fontFamily)
        assertEquals(figtree, resolved.m3.bodyLarge.fontFamily)
    }

    @Test
    fun `the hero shrink steps are the steps at or below the hero's own size`() {
        assertEquals(HeroSizeSteps, heroSizeSteps(48.sp))
        assertEquals(listOf(34f, 28f, 24f, 20f, 16f, 12f), heroSizeSteps(34.sp).map { it.value })
        // A size that is not itself a step starts at the next step below it.
        assertEquals(listOf(28f, 24f, 20f, 16f, 12f), heroSizeSteps(30.sp).map { it.value })
        // Every display font's hero is its own first step.
        DisplayFont.entries.forEach { font ->
            val hero = built(TypePairing.HERO_ONLY, font).type.amountHero.fontSize
            assertEquals("$font", hero, heroSizeSteps(hero).first())
        }
    }

    @Test
    fun `the defaults are Cobalt, Manrope and Bricolage, hero only`() {
        assertEquals(ThemePalette.COBALT, ThemePrefs.Default.lightPalette)
        assertEquals(ThemePalette.COBALT, ThemePrefs.Default.darkPalette)
        assertEquals(SansFont.MANROPE, ThemePrefs.Default.sansFont)
        assertEquals(DisplayFont.BUNDLED_GROTESQUE, ThemePrefs.Default.displayFont)
        assertEquals(TypePairing.HERO_ONLY, ThemePrefs.Default.typePairing)
        val type = resolve(ThemePrefs.Default.copy(dynamicColor = false)).type
        assertEquals(sansFamily(SansFont.MANROPE), type.amountRow.fontFamily)
        assertTrue(!type.amountHero.fontFamily.isBundledNewsreader())
    }
}
