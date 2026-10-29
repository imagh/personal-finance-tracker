package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Every colour role of an M3 [ColorScheme], by name. `ColorScheme` has no `equals`, so the theme
 * tests compare these maps, and the contrast tests iterate over the surface roles in them.
 */
internal fun ColorScheme.roles(): Map<String, Color> =
    linkedMapOf(
        "primary" to primary,
        "onPrimary" to onPrimary,
        "primaryContainer" to primaryContainer,
        "onPrimaryContainer" to onPrimaryContainer,
        "inversePrimary" to inversePrimary,
        "secondary" to secondary,
        "onSecondary" to onSecondary,
        "secondaryContainer" to secondaryContainer,
        "onSecondaryContainer" to onSecondaryContainer,
        "tertiary" to tertiary,
        "onTertiary" to onTertiary,
        "tertiaryContainer" to tertiaryContainer,
        "onTertiaryContainer" to onTertiaryContainer,
        "background" to background,
        "onBackground" to onBackground,
        "surface" to surface,
        "onSurface" to onSurface,
        "surfaceVariant" to surfaceVariant,
        "onSurfaceVariant" to onSurfaceVariant,
        "surfaceTint" to surfaceTint,
        "inverseSurface" to inverseSurface,
        "inverseOnSurface" to inverseOnSurface,
        "error" to error,
        "onError" to onError,
        "errorContainer" to errorContainer,
        "onErrorContainer" to onErrorContainer,
        "outline" to outline,
        "outlineVariant" to outlineVariant,
        "scrim" to scrim,
        "surfaceBright" to surfaceBright,
        "surfaceDim" to surfaceDim,
        "surfaceContainer" to surfaceContainer,
        "surfaceContainerHigh" to surfaceContainerHigh,
        "surfaceContainerHighest" to surfaceContainerHighest,
        "surfaceContainerLow" to surfaceContainerLow,
        "surfaceContainerLowest" to surfaceContainerLowest,
    )

/**
 * The roles money text can sit on: `background`, `surface`, `surfaceDim`/`surfaceBright` and the
 * five `surfaceContainer*` steps (light tones 87-100, dark tones 4-24, plan §3.3).
 * `surfaceVariant` is excluded on purpose: it is a tone-30 fill (dark) for input tracks and
 * outlines whose text is `onSurfaceVariant`, not a surface amounts are drawn on.
 */
internal fun ColorScheme.surfaceRoles(): Map<String, Color> =
    roles().filterKeys {
        it in
            setOf(
                "background",
                "surface",
                "surfaceBright",
                "surfaceDim",
                "surfaceContainer",
                "surfaceContainerHigh",
                "surfaceContainerHighest",
                "surfaceContainerLow",
                "surfaceContainerLowest",
            )
    }
