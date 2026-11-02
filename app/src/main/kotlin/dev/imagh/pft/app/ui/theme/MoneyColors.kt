package dev.imagh.pft.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The colours that carry financial meaning. They are fixed tokens: the same in the dynamic and
 * the Graphite scheme, and **never derived from the accent**, so a wallpaper that happens to be
 * green or red cannot make chrome look like income or expense (UI-5, UI-8).
 *
 * Colour is never the only cue (WCAG 1.4.1): the light [positive] and [negative] are nearly equal
 * in luminance, so every amount also carries a sign and every status pill a word. Positive
 * account balances stay neutral; only negative ones use [negative] (owner decision Q3).
 *
 * Never draw money text on `surfaceVariant` (dark `negative` there is only 4.23:1): use `background`,
 * `surface` or a `surfaceContainer*` step.
 *
 * Every text token clears 4.5:1 on every surface either scheme can produce, and every
 * on-container colour clears 4.5:1 on its container; `ColorContrastTest` pins both.
 *
 * @property positive income and under-budget text.
 * @property negative expense totals, over-budget and negative-balance text.
 * @property warning text for a budget at or above its alert threshold.
 */
@Immutable
data class MoneyColors(
    val positive: Color,
    val negative: Color,
    val warning: Color,
    val positiveContainer: Color,
    val onPositiveContainer: Color,
    val negativeContainer: Color,
    val onNegativeContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

private val LightMoneyColors =
    MoneyColors(
        positive = Color(0xFF13653C),
        negative = Color(0xFFA3261D),
        warning = Color(0xFF734900),
        positiveContainer = Color(0xFFDDF4E5),
        onPositiveContainer = Color(0xFF0B4A2A),
        negativeContainer = Color(0xFFFCE3E0),
        onNegativeContainer = Color(0xFF7A1B14),
        warningContainer = Color(0xFFFCEFD2),
        onWarningContainer = Color(0xFF5C3A00),
    )

private val DarkMoneyColors =
    MoneyColors(
        positive = Color(0xFF6FD99E),
        negative = Color(0xFFFF8F85),
        warning = Color(0xFFF2C14E),
        positiveContainer = Color(0xFF123C27),
        onPositiveContainer = Color(0xFFB5EFCB),
        negativeContainer = Color(0xFF4A1712),
        onNegativeContainer = Color(0xFFFFD3CD),
        warningContainer = Color(0xFF3F2E05),
        onWarningContainer = Color(0xFFF8DFA2),
    )

/**
 * The money colour tokens for the light or dark theme. Takes no colour scheme on purpose: money
 * colours must not depend on the accent.
 */
fun moneyColors(dark: Boolean): MoneyColors = if (dark) DarkMoneyColors else LightMoneyColors

/** Provided by `PftTheme`; read it through `PftTheme.money`. */
internal val LocalMoneyColors = staticCompositionLocalOf { LightMoneyColors }
