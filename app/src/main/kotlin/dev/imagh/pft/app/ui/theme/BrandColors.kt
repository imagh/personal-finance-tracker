package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * The brand fallback colour scheme: used when dynamic colour is off, and always in screenshot
 * tests so goldens are deterministic.
 *
 * Seed "ink indigo" `#4A55B8`, chosen far from money green (~150 degrees) and red (~25 degrees) so
 * the accent can never read as income or expense. Generated once, offline, with the Material
 * Color Utilities "tonal spot" scheme (`@material/material-color-utilities` 0.3.0, the engine
 * behind Material Theme Builder) on 2026-10-29. The M3 1.3 surface roles (`surfaceDim`,
 * `surfaceContainer*`, ...) use the current tone ladder: light 87/98/100/96/94/92/90, dark
 * 6/24/4/10/12/17/22. All roles are committed as constants; there is no runtime colour library.
 * To change the seed, regenerate every role and re-run `ColorContrastTest`.
 *
 * Money colours are deliberately not part of this scheme: see [MoneyColors].
 */
object BrandColors {
    /** Light roles. */
    val light: ColorScheme =
        lightColorScheme(
            primary = Color(0xFF4954B7),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFDFE0FF),
            onPrimaryContainer = Color(0xFF000866),
            inversePrimary = Color(0xFFBDC2FF),
            secondary = Color(0xFF5C5D72),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE1E0F9),
            onSecondaryContainer = Color(0xFF181A2C),
            tertiary = Color(0xFF78536C),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFD7EF),
            onTertiaryContainer = Color(0xFF2E1126),
            background = Color(0xFFFCF8FD),
            onBackground = Color(0xFF1B1B1F),
            surface = Color(0xFFFCF8FD),
            onSurface = Color(0xFF1B1B1F),
            surfaceVariant = Color(0xFFE3E1EC),
            onSurfaceVariant = Color(0xFF46464F),
            surfaceTint = Color(0xFF4954B7),
            inverseSurface = Color(0xFF303034),
            inverseOnSurface = Color(0xFFF3F0F4),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            outline = Color(0xFF777680),
            outlineVariant = Color(0xFFC7C5D0),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFFFCF8FD),
            surfaceDim = Color(0xFFDCD9DE),
            surfaceContainer = Color(0xFFF0EDF1),
            surfaceContainerHigh = Color(0xFFEAE7EC),
            surfaceContainerHighest = Color(0xFFE4E1E6),
            surfaceContainerLow = Color(0xFFF6F2F7),
            surfaceContainerLowest = Color(0xFFFFFFFF),
        )

    /** Dark roles. */
    val dark: ColorScheme =
        darkColorScheme(
            primary = Color(0xFFBDC2FF),
            onPrimary = Color(0xFF152187),
            primaryContainer = Color(0xFF303B9E),
            onPrimaryContainer = Color(0xFFDFE0FF),
            inversePrimary = Color(0xFF4954B7),
            secondary = Color(0xFFC4C4DD),
            onSecondary = Color(0xFF2D2F42),
            secondaryContainer = Color(0xFF444559),
            onSecondaryContainer = Color(0xFFE1E0F9),
            tertiary = Color(0xFFE7B9D6),
            onTertiary = Color(0xFF45263C),
            tertiaryContainer = Color(0xFF5E3C53),
            onTertiaryContainer = Color(0xFFFFD7EF),
            background = Color(0xFF131316),
            onBackground = Color(0xFFE4E1E6),
            surface = Color(0xFF131316),
            onSurface = Color(0xFFE4E1E6),
            surfaceVariant = Color(0xFF46464F),
            onSurfaceVariant = Color(0xFFC7C5D0),
            surfaceTint = Color(0xFFBDC2FF),
            inverseSurface = Color(0xFFE4E1E6),
            inverseOnSurface = Color(0xFF303034),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            outline = Color(0xFF91909A),
            outlineVariant = Color(0xFF46464F),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFF39393C),
            surfaceDim = Color(0xFF131316),
            surfaceContainer = Color(0xFF1F1F23),
            surfaceContainerHigh = Color(0xFF2A2A2D),
            surfaceContainerHighest = Color(0xFF353438),
            surfaceContainerLow = Color(0xFF1B1B1F),
            surfaceContainerLowest = Color(0xFF0E0E11),
        )
}
