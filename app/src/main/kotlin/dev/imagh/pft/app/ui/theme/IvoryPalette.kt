package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Warm ivory paper with an ink-navy accent (pale periwinkle in dark): an option, never the
 * default (DS6b). Selected through [ThemePalette.IVORY].
 *
 * **Provenance.** Generated once, offline, on 2026-10-30 with `@material/material-color-utilities`
 * 0.3.0, outside the repository, by the same procedure as [GraphitePalette] (plan §3.1). Per mode:
 * 1. A `DynamicScheme` (tonal-spot roles, contrast 0) built from custom tonal palettes: primary
 *    from the hue and chroma of the mockup `primary` (light #2E3A59: hue 269.7, chroma 24.8; dark
 *    #AEB9DA: hue 268.7, chroma 22.4), secondary the same hue at half the chroma, tertiary a muted
 *    (chroma 10) hue +60 rotation, and neutral / neutral-variant from the hue and chroma of the
 *    mockup `flat` surface (light: hue 109.7, chroma 4 / 6; dark: hue 103.6, chroma 3 / 4.5). The
 *    error roles are the stock M3 ones.
 * 2. The newer surface roles (`surfaceDim`, `surfaceBright`, `surfaceContainer*`) come from the
 *    neutral palette at the M3 1.3 tone ladder: light 87/98/100/96/94/92/90, dark
 *    6/24/4/10/12/17/22 (dim / bright / lowest / low / container / high / highest).
 * 3. The owner-approved mockup values (`.phone[data-pal=ivory]` in
 *    `docs/plans/2026-10-30-m1a-premium-look-mockups.html`) are pinned over the generated roles:
 *
 *    | Mockup token | Role | Light | Dark |
 *    |---|---|---|---|
 *    | `flat` | `surface`, `background` | #F6F2E9 | #181713 |
 *    | `flatlow` | `surfaceContainer` | #EFEADF | #201F1A |
 *    | `on` | `onSurface`, `onBackground` | #1F1D18 | #ECE6D8 |
 *    | `onvar` | `onSurfaceVariant` | #5A5548 | #AFA896 |
 *    | `line` | `outlineVariant` | #D9D2C2 | #2B2923 |
 *    | `primary` | `primary` | #2E3A59 | #AEB9DA |
 *    | `canvas` | `canvas` (DS6c) | #E9E4D8 | #12110E |
 *    | `island` | `island` (DS6c) | #F3EFE5 | #1A1915 |
 *
 * 4. `surfaceTint` equals the pinned `primary`, as in M3. `onPrimary` is the primary palette's tone
 *    100 (light) and tone 20 (dark), as generated. No other role was adjusted: the generated ladder
 *    already sits in order around the pinned surface (dark tone 7.7) and container (tone 11.7).
 *
 * No pinned value was tone-adjusted: all of them passed `ColorContrastTest` and `AccentHueTest`
 * as owner-approved. The pinned light `surfaceContainer` (tone 92.8) sits just above the ladder's
 * `surfaceContainerHigh` (tone 92); the order is kept, the gap is small. Light
 * `surfaceContainerLowest` is tone 100, i.e. white: it must not be used as a card, sheet or
 * dialog fill.
 *
 * To change a value, regenerate or edit it, then re-run `ColorContrastTest` and `AccentHueTest`
 * and re-record the goldens.
 */
internal object IvoryPalette {
    /** Light roles. */
    val light: PaletteColors =
        PaletteColors(
            lightColorScheme(
                primary = Color(0xFF2E3A59),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFDAE2FF),
                onPrimaryContainer = Color(0xFF3A4666),
                inversePrimary = Color(0xFFBAC6EC),
                secondary = Color(0xFF5A5E6C),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFDEE2F3),
                onSecondaryContainer = Color(0xFF424654),
                tertiary = Color(0xFF675B65),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFEFDEEA),
                onTertiaryContainer = Color(0xFF4F434D),
                background = Color(0xFFF6F2E9),
                onBackground = Color(0xFF1F1D18),
                surface = Color(0xFFF6F2E9),
                onSurface = Color(0xFF1F1D18),
                surfaceVariant = Color(0xFFE6E2D5),
                onSurfaceVariant = Color(0xFF5A5548),
                surfaceTint = Color(0xFF2E3A59),
                inverseSurface = Color(0xFF31302B),
                inverseOnSurface = Color(0xFFF4F0E7),
                error = Color(0xFFBA1A1A),
                onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFFFDAD6),
                onErrorContainer = Color(0xFF93000A),
                outline = Color(0xFF79776C),
                outlineVariant = Color(0xFFD9D2C2),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFFFDF9F0),
                surfaceDim = Color(0xFFDDDAD1),
                surfaceContainer = Color(0xFFEFEADF),
                surfaceContainerHigh = Color(0xFFECE8DF),
                surfaceContainerHighest = Color(0xFFE6E2D9),
                surfaceContainerLow = Color(0xFFF7F3EA),
                surfaceContainerLowest = Color(0xFFFFFFFF),
            ),
            canvas = Color(0xFFE9E4D8),
            island = Color(0xFFF3EFE5),
        )

    /** Dark roles. */
    val dark: PaletteColors =
        PaletteColors(
            darkColorScheme(
                primary = Color(0xFFAEB9DA),
                onPrimary = Color(0xFF25304A),
                primaryContainer = Color(0xFF3B4662),
                onPrimaryContainer = Color(0xFFD9E2FF),
                inversePrimary = Color(0xFF535E7B),
                secondary = Color(0xFFC3C6D5),
                onSecondary = Color(0xFF2C303C),
                secondaryContainer = Color(0xFF434653),
                onSecondaryContainer = Color(0xFFDFE2F1),
                tertiary = Color(0xFFD2C2CE),
                onTertiary = Color(0xFF372D37),
                tertiaryContainer = Color(0xFF4F434D),
                onTertiaryContainer = Color(0xFFEFDEEA),
                background = Color(0xFF181713),
                onBackground = Color(0xFFECE6D8),
                surface = Color(0xFF181713),
                onSurface = Color(0xFFECE6D8),
                surfaceVariant = Color(0xFF49473F),
                onSurfaceVariant = Color(0xFFAFA896),
                surfaceTint = Color(0xFFAEB9DA),
                inverseSurface = Color(0xFFE6E2DB),
                inverseOnSurface = Color(0xFF32302C),
                error = Color(0xFFFFB4AB),
                onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A),
                onErrorContainer = Color(0xFFFFDAD6),
                outline = Color(0xFF949088),
                outlineVariant = Color(0xFF2B2923),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFF3B3935),
                surfaceDim = Color(0xFF141310),
                surfaceContainer = Color(0xFF201F1A),
                surfaceContainerHigh = Color(0xFF2B2A26),
                surfaceContainerHighest = Color(0xFF363530),
                surfaceContainerLow = Color(0xFF1D1C18),
                surfaceContainerLowest = Color(0xFF0F0E0B),
            ),
            canvas = Color(0xFF12110E),
            island = Color(0xFF1A1915),
        )
}
