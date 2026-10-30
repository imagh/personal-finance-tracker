package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Deep aubergine with a plum accent (champagne in dark): an option, never the default (DS6b).
 * Selected through [ThemePalette.AUBERGINE].
 *
 * **Provenance.** Generated once, offline, on 2026-10-30 with `@material/material-color-utilities`
 * 0.3.0, outside the repository, by the same procedure as [GraphitePalette] (plan §3.1). Per mode:
 * 1. A `DynamicScheme` (tonal-spot roles, contrast 0) built from custom tonal palettes: primary
 *    from the hue and chroma of the mockup `primary` (light #5B2A66: hue 325.2, chroma 42.5; dark
 *    #D9BE8A: hue 85.3, chroma 23), secondary the same hue at half the chroma, tertiary a muted
 *    (chroma 10) hue +60 rotation, and neutral / neutral-variant from the hue and chroma of the
 *    mockup `flat` surface (light: hue 295.4, chroma 4 / 6; dark: hue 324.3, chroma 9.5 / 14.5).
 *    The error roles are the stock M3 ones.
 * 2. The newer surface roles (`surfaceDim`, `surfaceBright`, `surfaceContainer*`) come from the
 *    neutral palette at the M3 1.3 tone ladder: light 87/98/100/96/94/92/90, dark
 *    6/24/4/10/12/17/22 (dim / bright / lowest / low / container / high / highest).
 * 3. The owner-approved mockup values (`.phone[data-pal=aubergine]` in
 *    `docs/plans/2026-10-30-m1a-premium-look-mockups.html`) are pinned over the generated roles:
 *
 *    | Mockup token | Role | Light | Dark |
 *    |---|---|---|---|
 *    | `flat` | `surface`, `background` | #F7F2F7 | #140D15 |
 *    | `flatlow` | `surfaceContainer` | #F0E9F0 | #1D141E |
 *    | `on` | `onSurface`, `onBackground` | #1D141D | #EDE3EC |
 *    | `onvar` | `onSurfaceVariant` | #594C58 | #B3A5B2 |
 *    | `line` | `outlineVariant` | #DACFD9 | #2E2030 |
 *    | `primary` | `primary` | #5B2A66 | #D9BE8A |
 *
 * 4. Follow-on adjustments keep the pinned dark values coherent (not mockup values, none of them
 *    changes a hue). `surfaceTint` equals the pinned `primary`, as in M3. The pinned dark `surface`
 *    is tone 4.4 and `surfaceContainer` tone 7.7, both below the M3 ladder, so dark `surfaceDim`
 *    equals the pinned dark `surface` (#140D15, instead of tone 6), dark `surfaceContainerLow` is
 *    neutral tone 6 (#181119) instead of tone 10, and dark `surfaceContainerLowest` is neutral
 *    tone 3 (#0F0911) instead of tone 4, so the ladder stays ordered around the pinned values.
 *    `onPrimary` is the primary palette's tone 100 (light) and tone 20 (dark), as generated.
 *
 * No pinned value was tone-adjusted: all of them passed `ColorContrastTest` and `AccentHueTest`
 * as owner-approved. The dark champagne primary sits about 1 degree of CIELAB hue from warning
 * amber, which the hue rule exempts on purpose (plan §3.1 "Accent safety"): a warning pill takes
 * its colours from `MoneyColors`, never from this palette. Light `surfaceContainerLowest` is
 * tone 100, i.e. white: it must not be used as a card, sheet or dialog fill.
 *
 * To change a value, regenerate or edit it, then re-run `ColorContrastTest` and `AccentHueTest`
 * and re-record the goldens.
 */
internal object AuberginePalette {
    /** Light roles. */
    val light: PaletteColors =
        PaletteColors(
            lightColorScheme(
                primary = Color(0xFF5B2A66),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFDD6FF),
                onPrimaryContainer = Color(0xFF64336F),
                inversePrimary = Color(0xFFEFB1F7),
                secondary = Color(0xFF6E5671),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFF8D9F9),
                onSecondaryContainer = Color(0xFF563F58),
                tertiary = Color(0xFF6E5957),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFF9DCD8),
                onTertiaryContainer = Color(0xFF554240),
                background = Color(0xFFF7F2F7),
                onBackground = Color(0xFF1D141D),
                surface = Color(0xFFF7F2F7),
                onSurface = Color(0xFF1D141D),
                surfaceVariant = Color(0xFFE6E1E9),
                onSurfaceVariant = Color(0xFF594C58),
                surfaceTint = Color(0xFF5B2A66),
                inverseSurface = Color(0xFF313033),
                inverseOnSurface = Color(0xFFF4EFF4),
                error = Color(0xFFBA1A1A),
                onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFFFDAD6),
                onErrorContainer = Color(0xFF93000A),
                outline = Color(0xFF79767D),
                outlineVariant = Color(0xFFDACFD9),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFFFDF8FD),
                surfaceDim = Color(0xFFDDD9DD),
                surfaceContainer = Color(0xFFF0E9F0),
                surfaceContainerHigh = Color(0xFFEBE7EC),
                surfaceContainerHighest = Color(0xFFE6E1E6),
                surfaceContainerLow = Color(0xFFF7F2F7),
                surfaceContainerLowest = Color(0xFFFFFFFF),
            ),
        )

    /** Dark roles. */
    val dark: PaletteColors =
        PaletteColors(
            darkColorScheme(
                primary = Color(0xFFD9BE8A),
                onPrimary = Color(0xFF3E2E06),
                primaryContainer = Color(0xFF57441B),
                onPrimaryContainer = Color(0xFFFCDFA8),
                inversePrimary = Color(0xFF705C30),
                secondary = Color(0xFFD4C5AB),
                onSecondary = Color(0xFF382F1E),
                secondaryContainer = Color(0xFF504532),
                onSecondaryContainer = Color(0xFFF1E1C6),
                tertiary = Color(0xFFC0C9BA),
                onTertiary = Color(0xFF2A3328),
                tertiaryContainer = Color(0xFF41493E),
                onTertiaryContainer = Color(0xFFDCE5D6),
                background = Color(0xFF140D15),
                onBackground = Color(0xFFEDE3EC),
                surface = Color(0xFF140D15),
                onSurface = Color(0xFFEDE3EC),
                surfaceVariant = Color(0xFF514252),
                onSurfaceVariant = Color(0xFFB3A5B2),
                surfaceTint = Color(0xFFD9BE8A),
                inverseSurface = Color(0xFFEDDEEB),
                inverseOnSurface = Color(0xFF362E37),
                error = Color(0xFFFFB4AB),
                onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A),
                onErrorContainer = Color(0xFFFFDAD6),
                outline = Color(0xFF9D8B9E),
                outlineVariant = Color(0xFF2E2030),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFF3F3640),
                surfaceDim = Color(0xFF140D15),
                surfaceContainer = Color(0xFF1D141E),
                surfaceContainerHigh = Color(0xFF302730),
                surfaceContainerHighest = Color(0xFF3B323B),
                surfaceContainerLow = Color(0xFF181119),
                surfaceContainerLowest = Color(0xFF0F0911),
            ),
        )
}
