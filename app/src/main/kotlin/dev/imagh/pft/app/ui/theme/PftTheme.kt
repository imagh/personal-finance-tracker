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
import androidx.compose.ui.platform.LocalContext

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
 * Type, shape, spacing and motion tokens join in DS2 and DS4.
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
    CompositionLocalProvider(LocalMoneyColors provides moneyColors(dark)) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

/** Accessors for the extended theme tokens provided by [PftTheme]. */
object PftTheme {
    /** Money colours for the current light or dark theme. */
    val money: MoneyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyColors.current
}
