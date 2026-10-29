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
 * Picks the colour scheme for the resolved theme.
 *
 * With [dynamic] on, the scheme comes from the system's Material You palette. That needs API 31
 * and the app's minSdk is 33, so there is no version fallback. With it off, the committed
 * [BrandColors] are used.
 *
 * @param dark the already-resolved dark/light decision (see [ThemeMode]).
 */
fun resolveColorScheme(
    dark: Boolean,
    dynamic: Boolean,
    context: Context,
): ColorScheme =
    when {
        dynamic && dark -> dynamicDarkColorScheme(context)
        dynamic -> dynamicLightColorScheme(context)
        dark -> BrandColors.dark
        else -> BrandColors.light
    }

/**
 * Wraps the app's UI: resolves dark or light from [prefs], picks the colour scheme with
 * [resolveColorScheme], applies it through [MaterialTheme] and provides the extended tokens
 * (read them through the [PftTheme] object). This is the only theme entry point; M3 components
 * are styled from it, never wrapped to restyle them.
 *
 * The M3 type scale is the sans text family throughout; only `PftTheme.type.amountHero` takes the
 * display font ([ThemePrefs.displayFont]). Shapes come from [PftShapes]; the spacing scale is
 * [PftSpacing] (a fixed grid, so its local needs no provider). Motion joins in DS4.
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
    val colorScheme = resolveColorScheme(dark, prefs.dynamicColor, LocalContext.current)
    // The text family is the system font until custom-font import (AP2) adds a choice.
    val textFamily = FontFamily.Default
    val typography = remember(textFamily) { pftMaterialTypography(textFamily) }
    val pftType =
        remember(prefs.displayFont, textFamily) { pftType(displayFamily(prefs.displayFont, textFamily), textFamily) }
    CompositionLocalProvider(LocalMoneyColors provides moneyColors(dark), LocalPftTypography provides pftType) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = PftShapes.material,
            content = content,
        )
    }
}

// A fixed grid: the default is the only value, but a local lets tests and previews override it.
private val LocalPftSpacing = staticCompositionLocalOf { PftSpacing() }

private val LocalPftTypography = staticCompositionLocalOf { pftType(NewsreaderFamily, FontFamily.Default) }

/** Accessors for the extended theme tokens provided by [PftTheme]. */
object PftTheme {
    /** Money colours for the current light or dark theme. */
    val money: MoneyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyColors.current

    /** The amount text styles ([PftTypography]); every other text role is `MaterialTheme.typography`. */
    val type: PftTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPftTypography.current

    /** The shape tokens beyond M3's own (`MaterialTheme.shapes` carries the five M3 sizes). */
    val shapes: PftShapes
        get() = PftShapes

    /** The spacing scale. */
    val spacing: PftSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalPftSpacing.current
}
