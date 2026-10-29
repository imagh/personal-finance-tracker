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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.imagh.pft.R

/**
 * The type roles that Material 3 does not have: the three amount styles. All carry `tnum`
 * (tabular figures) so columns of amounts line up and rolling digits do not jitter. Read them
 * through `PftTheme.type`.
 *
 * The serif is used for [amountHero] **only** (owner decision Q10): every M3 role, titles
 * included, is the sans text family, and so are [amountLarge] and [amountRow].
 *
 * @property amountHero the big figure on dashboards and reports (month total, budget detail,
 *   account totals): 48/60sp, regular weight, lining + tabular figures, set slightly looser than
 *   the font's default (+0.01em, 1.25x line height; owner decision Q1). Its family follows
 *   [ThemePrefs.displayFont]. The amount component shrinks it in steps (48, 40, 34, 28sp) to fit.
 * @property amountLarge the amount on add/edit and detail screens: 32/40sp, medium weight, sans.
 * @property amountRow the amount in list rows: 16/24sp, medium weight, sans.
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

// opsz is Newsreader's optical-size axis (6 to 72). The hero is a display size, so use the display cut.
private val HeroOpticalSize = HeroFontSize

/**
 * The bundled Newsreader (OFL-1.1, unmodified upstream variable font), pinned to regular weight
 * and the 48sp optical size. Used only for `amountHero`.
 */
internal val NewsreaderFamily: FontFamily =
    FontFamily(
        Font(
            R.font.newsreader,
            weight = FontWeight.Normal,
            variationSettings =
                FontVariation.Settings(
                    FontWeight.Normal,
                    FontStyle.Normal,
                    FontVariation.opticalSizing(HeroOpticalSize),
                ),
        ),
    )

/** Chooses the hero's family for [choice]; [text] is the sans family used for "Same as text". */
internal fun displayFamily(
    choice: DisplayFont,
    text: FontFamily,
): FontFamily =
    when (choice) {
        DisplayFont.BUNDLED_SERIF -> NewsreaderFamily
        DisplayFont.SYSTEM_SERIF -> FontFamily.Serif
        DisplayFont.TEXT -> text
    }

/** The three amount styles with the hero in [display] and the others in [text]. */
internal fun pftType(
    display: FontFamily,
    text: FontFamily,
): PftTypography =
    PftTypography(
        amountHero =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.Normal,
                fontSize = HeroFontSize,
                lineHeight = HeroFontSize * HERO_LINE_HEIGHT_RATIO,
                letterSpacing = HeroLetterSpacing,
                fontFeatureSettings = "tnum, lnum",
            ),
        amountLarge =
            TextStyle(
                fontFamily = text,
                fontWeight = FontWeight.Medium,
                fontSize = 32.sp,
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
 * The M3 type scale with every role set in the [text] family (never the serif, Q10). Sizes are
 * the M3 defaults except the two header titles: `headlineLarge` (the title of a `Standard`
 * header) is 32/40 semibold and `titleLarge` (the title of a `Dashboard` header and of the
 * collapsed bar) is 22/28 medium.
 */
internal fun pftMaterialTypography(text: FontFamily): Typography {
    val base = Typography()
    return base.copy(
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
}
