@file:OptIn(ExperimentalTextApi::class)

package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.imagh.pft.R

/**
 * The type roles that Material 3 does not have: the three amount styles. All carry `tnum`
 * (tabular figures) so columns of amounts line up and rolling digits do not jitter. Read them
 * through `PftTheme.type`.
 *
 * Which of these (and which M3 roles) are set in the display family depends on
 * [ThemePrefs.typePairing]. By default ([TypePairing.HERO_ONLY], owner decision Q10) the serif is
 * [amountHero] **only**; under [TypePairing.EDITORIAL] [amountLarge] is serif too. [amountRow] is
 * always the sans text family.
 *
 * @property amountHero the big figure on dashboards and reports (month total, budget detail,
 *   account totals): 48/60sp, regular weight (bold 34/42.5sp in [DisplayFont.BUNDLED_GROTESQUE]),
 *   lining + tabular figures, set slightly looser than the font's default (+0.01em, 1.25x line
 *   height; owner decision Q1). Its family follows [ThemePrefs.displayFont]. The amount component
 *   shrinks it in steps (48, 40, 34, 28sp and smaller; see [heroSizeSteps]) to fit.
 * @property amountLarge the amount on add/edit and detail screens: 32/40sp, medium weight; sans,
 *   or the display font under [TypePairing.EDITORIAL].
 * @property amountRow the amount in list rows: 16/24sp, medium weight, always sans.
 */
@Immutable
data class PftTypography(
    val amountHero: TextStyle,
    val amountLarge: TextStyle,
    val amountRow: TextStyle,
)

// Hero spacing, named so it can be tuned on the debug catalogue before goldens are re-recorded
// (design plan §3.3 "Display spacing").
private val HeroFontSize = 48.sp
private val GrotesqueHeroFontSize = 34.sp
private const val HERO_LINE_HEIGHT_RATIO = 1.25f
private val HeroLetterSpacing = 0.01.em
private val AmountLargeSize = 32.sp

/** The hero's size and weight, which depend on the display font (Cobalt plan D2). */
private class HeroSpec(
    val size: TextUnit,
    val weight: FontWeight,
)

/**
 * The hero for [display]: Bricolage Grotesque is bold at 34sp (the Cobalt mockup); every other
 * display font keeps the regular 48sp.
 */
private fun heroSpec(display: DisplayFont): HeroSpec =
    when (display) {
        DisplayFont.BUNDLED_GROTESQUE -> HeroSpec(GrotesqueHeroFontSize, FontWeight.Bold)
        DisplayFont.BUNDLED_SERIF,
        DisplayFont.SYSTEM_SERIF,
        DisplayFont.TEXT,
        -> HeroSpec(HeroFontSize, FontWeight.Normal)
    }

/**
 * Every size the hero amount can step down through to fit its width: 48, 40, 34, 28sp (design plan
 * §3.3), then 24, 20, 16 and 12sp. A given hero uses only the steps at or below its own size, from
 * [heroSizeSteps]: `MoneyText` picks the largest of those that fits, and the first is
 * [amountHero]'s own size (48sp, or 34sp for [DisplayFont.BUNDLED_GROTESQUE]). The plan stopped at
 * 28sp, but its example amount (Rs 1e10) is one digit group shorter than the real limit (Rs 1e11,
 * `Paise.MAX_AMOUNT`): at 200% font scale that amount needs 16sp on a 393dp window and 12sp on a
 * 320dp one (the sp-to-dp scaling is non-linear, so 12sp is still about 24dp tall there). The
 * optical size stays at the hero's own cut (48sp, or 34sp for
 * Bricolage) for every step (DS2 deviations) and the line height scales with the size.
 */
internal val HeroSizeSteps: List<TextUnit> =
    listOf(HeroFontSize, 40.sp, 34.sp, 28.sp, 24.sp, 20.sp, 16.sp, 12.sp)

/**
 * The steps from [HeroSizeSteps] at or below [heroSize], largest first: all eight for the 48sp
 * hero, and 34, 28, 24, 20, 16, 12sp for the 34sp Bricolage hero, so a smaller hero never
 * "shrinks" upward to a larger step.
 */
internal fun heroSizeSteps(heroSize: TextUnit): List<TextUnit> = HeroSizeSteps.filter { it.value <= heroSize.value }

/**
 * The sans text family for [choice] (UI-2, UI-16): the bundled Figtree, Plus Jakarta Sans or
 * Manrope, or the platform default.
 *
 * Each bundled sans font is one variable font. Figtree's `wght` axis defaults to 300 and Manrope's
 * to 200, lighter than any role asks for, and Plus Jakarta Sans defaults to 400; either way the
 * axis is set explicitly: one [Font] per weight 400, 500 and 600 (regular text, medium labels and titles,
 * semibold header titles), each pinned to its own `wght`. These are every weight a role in
 * [pftMaterialTypography] or [pftType] uses; a weight not declared here would fall to the nearest
 * one. Declare a new weight in `sansWeights` before using it in a role.
 *
 * Reference each font only as `R.font.<name>`, below: release resource shrinking removes a font
 * that no reachable code names, and a name string or `getIdentifier` would not count.
 */
internal fun sansFamily(choice: SansFont): FontFamily =
    when (choice) {
        SansFont.FIGTREE -> FigtreeFamily
        SansFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFamily
        SansFont.MANROPE -> ManropeFamily
        SansFont.SYSTEM -> FontFamily.Default
    }

private val FigtreeFamily: FontFamily = sansWeights(R.font.figtree)

private val PlusJakartaSansFamily: FontFamily = sansWeights(R.font.plus_jakarta_sans)

private val ManropeFamily: FontFamily = sansWeights(R.font.manrope)

/** A family over the variable font [resId] with a [Font] pinned to each of the weights 400, 500 and 600. */
private fun sansWeights(resId: Int): FontFamily =
    FontFamily(
        listOf(400, 500, 600).map { w ->
            Font(
                resId,
                weight = FontWeight(w),
                variationSettings = FontVariation.Settings(FontWeight(w), FontStyle.Normal),
            )
        },
    )

/** The bundled Newsreader's optical-size axis (`opsz`) runs from 6 to 72. */
private const val NEWSREADER_MIN_OPSZ = 6f
private const val NEWSREADER_MAX_OPSZ = 72f

/**
 * The bundled Newsreader (OFL-1.1, unmodified upstream variable font) pinned to [weight] on its
 * `wght` axis and cut for [size] on its optical-size axis (`opsz`, [size] clamped to the font's 6
 * to 72 range), so a 16sp title gets the sturdier small cut and a 57sp display line the finer
 * large one. Built only for the serif roles that need it; `SYSTEM_SERIF` and "Same as text" get
 * no variation settings.
 *
 * The family holds a single [Font] with one pinned `wght`. A caller that overrides `fontWeight` on
 * a style using it still gets the pinned weight (possibly with synthetic bold); to change the
 * weight, build a new `newsreader(weight, size)`.
 */
internal fun newsreader(
    weight: FontWeight,
    size: TextUnit,
): FontFamily =
    FontFamily(
        Font(
            R.font.newsreader,
            weight = weight,
            variationSettings =
                FontVariation.Settings(
                    weight,
                    FontStyle.Normal,
                    FontVariation.opticalSizing(size.value.coerceIn(NEWSREADER_MIN_OPSZ, NEWSREADER_MAX_OPSZ).sp),
                ),
        ),
    )

/** The bundled Bricolage Grotesque's optical-size axis (`opsz`) runs from 12 to 96. */
private const val BRICOLAGE_MIN_OPSZ = 12f
private const val BRICOLAGE_MAX_OPSZ = 96f

/** Bricolage Grotesque's width axis (`wdth`) runs from 75 to 100; the app always uses the normal width. */
private const val BRICOLAGE_NORMAL_WIDTH = 100f

/**
 * The bundled Bricolage Grotesque (OFL-1.1, unmodified upstream variable font with `opsz`, `wdth`
 * and `wght` axes) pinned to [weight] on `wght`, cut for [size] on `opsz` ([size] clamped to the
 * font's 12 to 96 range) and set at the normal width (`wdth` 100). Its own defaults are the
 * heaviest, widest, largest-optical-size instance (`wght` 800, `opsz` 96), so every axis is set
 * explicitly.
 *
 * Same shape as [newsreader]: a single [Font] with one pinned `wght`; to change the weight, build a
 * new `bricolage(weight, size)`. Reference the font only as `R.font.bricolage_grotesque`, below
 * (release resource shrinking strips a font no reachable code names).
 */
internal fun bricolage(
    weight: FontWeight,
    size: TextUnit,
): FontFamily =
    FontFamily(
        Font(
            R.font.bricolage_grotesque,
            weight = weight,
            variationSettings =
                FontVariation.Settings(
                    weight,
                    FontStyle.Normal,
                    FontVariation.opticalSizing(size.value.coerceIn(BRICOLAGE_MIN_OPSZ, BRICOLAGE_MAX_OPSZ).sp),
                    FontVariation.width(BRICOLAGE_NORMAL_WIDTH),
                ),
        ),
    )

/**
 * The family for a role set in the display font at [weight] and [size]: the bundled Newsreader
 * cut for that weight and size, the platform serif, or [text] ("Same as text"). Only the bundled
 * serif gets variation settings.
 */
internal fun displayFamily(
    choice: DisplayFont,
    text: FontFamily,
    weight: FontWeight,
    size: TextUnit,
): FontFamily =
    when (choice) {
        DisplayFont.BUNDLED_SERIF -> newsreader(weight, size)
        DisplayFont.BUNDLED_GROTESQUE -> bricolage(weight, size)
        DisplayFont.SYSTEM_SERIF -> FontFamily.Serif
        DisplayFont.TEXT -> text
    }

/**
 * The three amount styles. `amountHero` is always in the [display] font (regular 48sp, or bold 34sp
 * for [DisplayFont.BUNDLED_GROTESQUE]; see [heroSpec]); `amountLarge` is too
 * under [TypePairing.EDITORIAL] (medium weight, cut for 32sp); `amountRow` and, under
 * [TypePairing.HERO_ONLY], `amountLarge` are in the [text] family. All carry `tnum`.
 */
internal fun pftType(
    display: DisplayFont,
    text: FontFamily,
    pairing: TypePairing,
): PftTypography {
    val hero = heroSpec(display)
    return PftTypography(
        amountHero =
            TextStyle(
                fontFamily = displayFamily(display, text, hero.weight, hero.size),
                fontWeight = hero.weight,
                fontSize = hero.size,
                lineHeight = hero.size * HERO_LINE_HEIGHT_RATIO,
                letterSpacing = HeroLetterSpacing,
                fontFeatureSettings = "tnum, lnum",
            ),
        amountLarge =
            TextStyle(
                fontFamily =
                    if (pairing == TypePairing.EDITORIAL) {
                        displayFamily(display, text, FontWeight.Medium, AmountLargeSize)
                    } else {
                        text
                    },
                fontWeight = FontWeight.Medium,
                fontSize = AmountLargeSize,
                lineHeight = 40.sp,
                fontFeatureSettings = "tnum, lnum",
            ),
        amountRow =
            TextStyle(
                fontFamily = text,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFeatureSettings = "tnum",
            ),
    )
}

/**
 * The M3 type scale. Sizes are the M3 defaults except the two header titles: `headlineLarge`
 * (the title of a `Standard` header) is 32/40 semibold and `titleLarge` (the title of a
 * `Dashboard` header and of the collapsed bar) is 22/28 medium.
 *
 * Under [TypePairing.HERO_ONLY] every role is the [text] family (never the serif, Q10). Under
 * [TypePairing.EDITORIAL] `display*`, `headline*`, `titleLarge` and `titleMedium` are in the
 * [display] font, each cut for its own size and weight (`titleMedium` is 16sp at weight 500);
 * `titleSmall` (tab labels), `body*` and `label*` stay [text]. With [DisplayFont.TEXT] that is
 * all sans.
 */
internal fun pftMaterialTypography(
    text: FontFamily,
    display: DisplayFont,
    pairing: TypePairing,
): Typography {
    val base = Typography()
    val sans =
        base.copy(
            displayLarge = base.displayLarge.copy(fontFamily = text),
            displayMedium = base.displayMedium.copy(fontFamily = text),
            displaySmall = base.displaySmall.copy(fontFamily = text),
            headlineLarge = base.headlineLarge.copy(fontFamily = text, fontWeight = FontWeight.SemiBold),
            headlineMedium = base.headlineMedium.copy(fontFamily = text),
            headlineSmall = base.headlineSmall.copy(fontFamily = text),
            titleLarge = base.titleLarge.copy(fontFamily = text, fontWeight = FontWeight.Medium),
            titleMedium = base.titleMedium.copy(fontFamily = text),
            titleSmall = base.titleSmall.copy(fontFamily = text),
            bodyLarge = base.bodyLarge.copy(fontFamily = text),
            bodyMedium = base.bodyMedium.copy(fontFamily = text),
            bodySmall = base.bodySmall.copy(fontFamily = text),
            labelLarge = base.labelLarge.copy(fontFamily = text),
            labelMedium = base.labelMedium.copy(fontFamily = text),
            labelSmall = base.labelSmall.copy(fontFamily = text),
        )
    if (pairing == TypePairing.HERO_ONLY) return sans

    fun TextStyle.serif() = copy(fontFamily = displayFamily(display, text, fontWeight ?: FontWeight.Normal, fontSize))
    return sans.copy(
        displayLarge = sans.displayLarge.serif(),
        displayMedium = sans.displayMedium.serif(),
        displaySmall = sans.displaySmall.serif(),
        headlineLarge = sans.headlineLarge.serif(),
        headlineMedium = sans.headlineMedium.serif(),
        headlineSmall = sans.headlineSmall.serif(),
        titleLarge = sans.titleLarge.serif(),
        titleMedium = sans.titleMedium.serif(),
    )
}
