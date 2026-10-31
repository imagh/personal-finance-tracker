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
 *   account totals): 48/60sp, regular weight, lining + tabular figures, set slightly looser than
 *   the font's default (+0.01em, 1.25x line height; owner decision Q1). Its family follows
 *   [ThemePrefs.displayFont]. The amount component shrinks it in steps (48, 40, 34, 28sp) to fit.
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
private const val HERO_LINE_HEIGHT_RATIO = 1.25f
private val HeroLetterSpacing = 0.01.em
private val AmountLargeSize = 32.sp

/**
 * The sizes the hero amount steps down through to fit its width: 48, 40, 34, 28sp (design plan
 * §3.3), then 24, 20, 16 and 12sp. `MoneyText` picks the largest that fits; the first is
 * [amountHero]'s own size. The plan stopped at 28sp, but its example amount (Rs 1e10) is one digit
 * group shorter than the real limit (Rs 1e11, `Paise.MAX_AMOUNT`): at 200% font scale that amount
 * needs 16sp on a 393dp window and 12sp on a 320dp one (the sp-to-dp scaling is non-linear, so
 * 12sp is still about 24dp tall there). The optical size stays at the 48sp display cut for every
 * step (DS2 deviations) and the line height scales with the size.
 */
internal val HeroSizeSteps: List<TextUnit> =
    listOf(HeroFontSize, 40.sp, 34.sp, 28.sp, 24.sp, 20.sp, 16.sp, 12.sp)

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
        DisplayFont.SYSTEM_SERIF -> FontFamily.Serif
        DisplayFont.TEXT -> text
    }

/**
 * The three amount styles. `amountHero` is always in the [display] font; `amountLarge` is too
 * under [TypePairing.EDITORIAL] (medium weight, cut for 32sp); `amountRow` and, under
 * [TypePairing.HERO_ONLY], `amountLarge` are in the [text] family. All carry `tnum`.
 */
internal fun pftType(
    display: DisplayFont,
    text: FontFamily,
    pairing: TypePairing,
): PftTypography =
    PftTypography(
        amountHero =
            TextStyle(
                fontFamily = displayFamily(display, text, FontWeight.Normal, HeroFontSize),
                fontWeight = FontWeight.Normal,
                fontSize = HeroFontSize,
                lineHeight = HeroFontSize * HERO_LINE_HEIGHT_RATIO,
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
