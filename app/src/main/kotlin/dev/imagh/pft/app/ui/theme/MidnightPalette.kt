package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Midnight navy with a brass accent (champagne in dark): an option, never the default (DS6b).
 * Selected through [ThemePalette.MIDNIGHT].
 *
 * **Provenance.** Generated once, offline, on 2026-10-30 with `@material/material-color-utilities`
 * 0.3.0, outside the repository, by the same procedure as [GraphitePalette] (plan §3.1). Per mode:
 * 1. A `DynamicScheme` (tonal-spot roles, contrast 0) built from custom tonal palettes: primary
 *    from the hue and chroma of the mockup `primary` (light #7D5F26: hue 81.2, chroma 29.6; dark
 *    #D4B572: hue 86.9, chroma 29.3), secondary the same hue at half the chroma, tertiary a muted
 *    (chroma 10) hue +60 rotation, and neutral / neutral-variant from the hue and chroma of the
 *    mockup `flat` surface (light: hue 244.6, chroma 5 / 7.5; dark: hue 272.7, chroma 15.5 / 23.5,
 *    so the generated surface ladder is as blue as the pinned navy). The error roles are the stock
 *    M3 ones.
 * 2. The newer surface roles (`surfaceDim`, `surfaceBright`, `surfaceContainer*`) come from the
 *    neutral palette at the M3 1.3 tone ladder: light 87/98/100/96/94/92/90, dark
 *    6/24/4/10/12/17/22 (dim / bright / lowest / low / container / high / highest).
 * 3. The owner-approved mockup values (`.phone[data-pal=midnight]` in
 *    `docs/plans/2026-10-30-m1a-premium-look-mockups.html`) are pinned over the generated roles:
 *
 *    | Mockup token | Role | Light | Dark |
 *    |---|---|---|---|
 *    | `flat` | `surface`, `background` | #F4F5F9 | #0F1424 |
 *    | `flatlow` | `surfaceContainer` | #ECEFF5 | #161C30 |
 *    | `on` | `onSurface`, `onBackground` | #11172A | #E6E8EF |
 *    | `onvar` | `onSurfaceVariant` | #4A5266 | #A5ACBF |
 *    | `line` | `outlineVariant` | #D2D7E3 | #222B42 |
 *    | `primary` | `primary` | #7D5F26 | #D4B572 |
 *    | `canvas` | `canvas` (DS6c) | #E6E9F0 | #0A0E1A |
 *    | `island` | `island` (DS6c) | #F0F2F7 | #121929 |
 *    | `pc` | `wash` (DS6d) | #E9DDC2 | #33291A |
 *
 * 4. `surfaceTint` equals the pinned `primary`, as in M3. `onPrimary` is the primary palette's tone
 *    100 (light) and tone 20 (dark), as generated. No other role was adjusted: the generated ladder
 *    already sits in order around the pinned surface (dark tone 6.6) and container (tone 10.7).
 *
 * No pinned value was tone-adjusted: all of them passed `ColorContrastTest` and `AccentHueTest`
 * as owner-approved. The brass primary is about 45 degrees of CIELAB hue from money red (the
 * tightest margin of any palette) and sits within a few degrees of warning amber, which the hue
 * rule exempts on purpose (plan §3.1 "Accent safety"): a warning pill takes its colours from
 * `MoneyColors`, never from this palette. Light `surfaceContainerLowest` is tone 100, i.e. white:
 * it must not be used as a card, sheet or dialog fill.
 *
 * `wash` (DS6d) is the mockup `pc`, pinned as approved over nothing (M3 has no such role). It passed
 * the header-glow contrast sweep in `ColorContrastTest` (the wash at 72% over the canvas, with
 * `onSurface`, `onSurfaceVariant` and the money tokens at 4.5:1) with no tone change.
 *
 * To change a value, regenerate or edit it, then re-run `ColorContrastTest` and `AccentHueTest`
 * and re-record the goldens.
 */
internal object MidnightPalette {
    /** Light roles. */
    val light: PaletteColors =
        PaletteColors(
            lightColorScheme(
                primary = Color(0xFF7D5F26),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFDEA8),
                onPrimaryContainer = Color(0xFF5D420A),
                inversePrimary = Color(0xFFE8C17E),
                secondary = Color(0xFF6C5C42),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFF6DFBE),
                onSecondaryContainer = Color(0xFF53452C),
                tertiary = Color(0xFF596154),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFDDE5D5),
                onTertiaryContainer = Color(0xFF41493D),
                background = Color(0xFFF4F5F9),
                onBackground = Color(0xFF11172A),
                surface = Color(0xFFF4F5F9),
                onSurface = Color(0xFF11172A),
                surfaceVariant = Color(0xFFDEE3EA),
                onSurfaceVariant = Color(0xFF4A5266),
                surfaceTint = Color(0xFF7D5F26),
                inverseSurface = Color(0xFF2E3134),
                inverseOnSurface = Color(0xFFEFF1F5),
                error = Color(0xFFBA1A1A),
                onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFFFDAD6),
                onErrorContainer = Color(0xFF93000A),
                outline = Color(0xFF72787E),
                outlineVariant = Color(0xFFD2D7E3),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFFF8F9FD),
                surfaceDim = Color(0xFFD8DADE),
                surfaceContainer = Color(0xFFECEFF5),
                surfaceContainerHigh = Color(0xFFE7E8EC),
                surfaceContainerHighest = Color(0xFFE1E2E7),
                surfaceContainerLow = Color(0xFFF2F3F8),
                surfaceContainerLowest = Color(0xFFFFFFFF),
            ),
            canvas = Color(0xFFE6E9F0),
            island = Color(0xFFF0F2F7),
            wash = Color(0xFFE9DDC2),
        )

    /** Dark roles. */
    val dark: PaletteColors =
        PaletteColors(
            darkColorScheme(
                primary = Color(0xFFD4B572),
                onPrimary = Color(0xFF3F2E00),
                primaryContainer = Color(0xFF59440A),
                onPrimaryContainer = Color(0xFFFFDF9B),
                inversePrimary = Color(0xFF735B21),
                secondary = Color(0xFFD6C5A4),
                onSecondary = Color(0xFF3A2F18),
                secondaryContainer = Color(0xFF51452C),
                onSecondaryContainer = Color(0xFFF3E0BE),
                tertiary = Color(0xFFC0C9BB),
                onTertiary = Color(0xFF2A3328),
                tertiaryContainer = Color(0xFF40493E),
                onTertiaryContainer = Color(0xFFDCE5D6),
                background = Color(0xFF0F1424),
                onBackground = Color(0xFFE6E8EF),
                surface = Color(0xFF0F1424),
                onSurface = Color(0xFFE6E8EF),
                surfaceVariant = Color(0xFF3D4664),
                onSurfaceVariant = Color(0xFFA5ACBF),
                surfaceTint = Color(0xFFD4B572),
                inverseSurface = Color(0xFFDEE1F8),
                inverseOnSurface = Color(0xFF2B3041),
                error = Color(0xFFFFB4AB),
                onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A),
                onErrorContainer = Color(0xFFFFDAD6),
                outline = Color(0xFF8790B2),
                outlineVariant = Color(0xFF222B42),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFF34394A),
                surfaceDim = Color(0xFF0E1323),
                surfaceContainer = Color(0xFF161C30),
                surfaceContainerHigh = Color(0xFF24293A),
                surfaceContainerHighest = Color(0xFF2F3446),
                surfaceContainerLow = Color(0xFF161B2B),
                surfaceContainerLowest = Color(0xFF080D1D),
            ),
            canvas = Color(0xFF0A0E1A),
            island = Color(0xFF121929),
            wash = Color(0xFF33291A),
        )
}
