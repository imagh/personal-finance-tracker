package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

/**
 * Picks the colours for the resolved theme: the M3 scheme plus the canvas, island and wash.
 *
 * With [ThemePrefs.dynamicColor] on (opt-in), the scheme comes from the system's Material You
 * palette, the palette fields are ignored, and the canvas, island and wash are derived from that scheme
 * by [dynamicPaletteColors]. That needs API 31 and the app's minSdk is 33, so there is no version
 * fallback. With it off, the colours are the committed palette the user picked for this mode:
 * [ThemePrefs.darkPalette] in dark, [ThemePrefs.lightPalette] in light.
 *
 * @param dark the already-resolved dark/light decision (see [ThemeMode]).
 */
internal fun resolvePaletteColors(
    prefs: ThemePrefs,
    dark: Boolean,
    context: Context,
): PaletteColors =
    when {
        prefs.dynamicColor && dark -> dynamicPaletteColors(dynamicDarkColorScheme(context), dark = true)
        prefs.dynamicColor -> dynamicPaletteColors(dynamicLightColorScheme(context), dark = false)
        else -> prefs.paletteFor(dark).colors(dark)
    }

/**
 * The colour scheme of [resolvePaletteColors]: what `MaterialTheme` is given. Kept public for the
 * tests and callers that only need the M3 roles.
 */
fun resolveColorScheme(
    prefs: ThemePrefs,
    dark: Boolean,
    context: Context,
): ColorScheme = resolvePaletteColors(prefs, dark, context).scheme

/**
 * The canvas, island and wash for a dynamic [scheme], which has no such roles. The canvas and
 * island are surface container steps, chosen so the island still lifts off the canvas (plan §3.1);
 * the wash is the scheme's own primary container, a tint of the user's wallpaper colour:
 *
 * | Token | Light | Dark |
 * |---|---|---|
 * | canvas | `surfaceContainerHigh` (tone 92) | `surfaceContainerLowest` (tone 4) |
 * | island | `surfaceContainerLow` (tone 96) | `surfaceContainerLow` (tone 10) |
 * | wash | `primaryContainer` (tone 90) | `primaryContainer` (tone 30) |
 *
 * Pure, so the tests can pin it. Light `surfaceContainerLowest` (white) is never used.
 */
internal fun dynamicPaletteColors(
    scheme: ColorScheme,
    dark: Boolean,
): PaletteColors =
    PaletteColors(
        scheme = scheme,
        canvas = if (dark) scheme.surfaceContainerLowest else scheme.surfaceContainerHigh,
        island = scheme.surfaceContainerLow,
        wash = scheme.primaryContainer,
    )

/**
 * Wraps the app's UI: resolves dark or light from [prefs], picks the colour scheme with
 * [resolveColorScheme], applies it through [MaterialTheme] and provides the extended tokens
 * (read them through the [PftTheme] object). This is the only theme entry point; M3 components
 * are styled from it, never wrapped to restyle them.
 *
 * The type scale is the sans text family except the roles [ThemePrefs.typePairing] sets in the
 * display font ([ThemePrefs.displayFont]): `PftTheme.type.amountHero` only (the default), or the
 * editorial set. Shapes come from [PftShapes]; the spacing scale is
 * [PftSpacing] (a fixed grid, so a plain constant, not a CompositionLocal), and so is [PftMotion].
 */
@Composable
fun PftTheme(
    prefs: ThemePrefs = ThemePrefs.Default,
    content: @Composable () -> Unit,
) {
    val dark =
        when (prefs.mode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }
    val palette = resolvePaletteColors(prefs, dark, LocalContext.current)
    val colorScheme = palette.scheme
    // The text family is the system font until custom-font import (AP2) adds a choice.
    val textFamily = FontFamily.Default
    val typography =
        remember(textFamily, prefs.displayFont, prefs.typePairing) {
            pftMaterialTypography(textFamily, prefs.displayFont, prefs.typePairing)
        }
    val pftType =
        remember(textFamily, prefs.displayFont, prefs.typePairing) {
            pftType(prefs.displayFont, textFamily, prefs.typePairing)
        }
    CompositionLocalProvider(
        LocalMoneyColors provides moneyColors(dark),
        LocalCategoryPalette provides categoryPalette(dark),
        LocalPftTypography provides pftType,
        LocalPftSurfaces provides pftSurfaces(palette, prefs.background, dark, prefs.headerGlow),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = PftShapes.material,
            content = content,
        )
    }
}

private val Spacing = PftSpacing()

private val LocalPftTypography =
    staticCompositionLocalOf { pftType(DisplayFont.BUNDLED_SERIF, FontFamily.Default, TypePairing.HERO_ONLY) }

/** Accessors for the extended theme tokens provided by [PftTheme]. */
object PftTheme {
    /** Money colours for the current light or dark theme. */
    val money: MoneyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyColors.current

    /** The category badge colours for the current light or dark theme, by the name persisted in `category.colour`. */
    val palette: CategoryPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalCategoryPalette.current

    /** The canvas, island, island edge and header glow for the current palette, mode and prefs. */
    val surfaces: PftSurfaces
        @Composable
        @ReadOnlyComposable
        get() = LocalPftSurfaces.current

    /** The amount text styles ([PftTypography]); every other text role is `MaterialTheme.typography`. */
    val type: PftTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPftTypography.current

    /** The shape tokens beyond M3's own (`MaterialTheme.shapes` carries the five M3 sizes). */
    val shapes: PftShapes
        get() = PftShapes

    /** The spacing scale: a fixed 4dp grid, the same in every theme. */
    val spacing: PftSpacing
        get() = Spacing

    /** The motion tokens (springs, navigation transitions, press scale): the same in every theme. */
    val motion: PftMotion
        get() = PftMotion
}
