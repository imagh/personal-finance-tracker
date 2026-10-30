package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * One curated palette in one mode. Today it carries the M3 roles only; the tones M3 has no role
 * for (canvas, island, header wash) join this class with the slices that first read them (DS6c,
 * DS6d), so each field arrives with its consumer.
 *
 * @property scheme every M3 colour role. Money colours are deliberately not part of it: see
 *   [MoneyColors].
 */
@Immutable
internal data class PaletteColors(
    val scheme: ColorScheme,
)

/**
 * Graphite + platinum: the default colour scheme (owner decision 2026-10-30; supersedes the ink
 * indigo `BrandColors` of DS1). Used whenever dynamic colour is off, and always in screenshot
 * tests, so goldens are deterministic.
 *
 * **Provenance.** Generated once, offline, on 2026-10-30 with `@material/material-color-utilities`
 * 0.3.0 (the engine behind Material Theme Builder), outside the repository; there is no runtime
 * colour library and no generator in the tree. The procedure, per mode:
 * 1. A `DynamicScheme` (tonal-spot roles, contrast 0) built from custom tonal palettes: primary
 *    from the hue and chroma of the mockup `primary` (light #3B4350: hue 258, chroma 13; dark
 *    #C9CED8: hue 254, chroma 9), secondary the same hue at half the chroma, tertiary a muted
 *    (chroma 10) hue +60 rotation, and neutral / neutral-variant from the mockup canvas hue at
 *    low chroma (light: hue 234, chroma 4 / 6; dark: hue 258, chroma 3 / 5). The error roles are
 *    the stock M3 ones.
 * 2. The newer surface roles (`surfaceDim`, `surfaceBright`, `surfaceContainer*`) come from the
 *    neutral palette at the M3 1.3 tone ladder: light 87/98/100/96/94/92/90, dark
 *    6/24/4/10/12/17/22 (dim / bright / lowest / low / container / high / highest).
 * 3. The owner-approved mockup values (`.phone[data-pal=graphite]` in
 *    `docs/plans/2026-10-30-m1a-premium-look-mockups.html`) are pinned over the generated roles.
 *    These overrides are the exceptions to the generated scheme:
 *
 *    | Mockup token | Role | Light | Dark |
 *    |---|---|---|---|
 *    | `flat` | `surface`, `background` | #F3F4F5 | #101113 |
 *    | `flatlow` | `surfaceContainer` | #EBEDEF | #181A1D |
 *    | `on` | `onSurface`, `onBackground` | #14161A | #E6E7EA |
 *    | `onvar` | `onSurfaceVariant` | #50545C | #A8ACB3 |
 *    | `line` | `outlineVariant` | #D1D4D9 | #25282C |
 *    | `primary` | `primary` | #3B4350 | #C9CED8 |
 *
 * 4. Three follow-on adjustments keep the pinned values coherent (not mockup values, none of them
 *    changes a hue): `surfaceTint` equals the pinned `primary`, as in M3; dark `surfaceDim` equals
 *    the pinned dark `surface` (#101113, tone 5, instead of tone 6); dark `surfaceContainerLow`
 *    is neutral tone 7 (#151618) instead of tone 10, so the dark ladder stays ordered below the
 *    pinned `surfaceContainer` (tone 9). `onPrimary` is the primary palette's tone 100 (light) and
 *    tone 20 (dark), as generated.
 *
 * No pinned value was tone-adjusted: all of them passed `ColorContrastTest` and `AccentHueTest`
 * as owner-approved. Light `surfaceContainerLowest` is tone 100, i.e. white: it must not be used
 * as a card, sheet or dialog fill (the owner rejected white cards).
 *
 * To change a value, regenerate or edit it, then re-run `ColorContrastTest` and `AccentHueTest`
 * and re-record the goldens.
 */
internal object GraphitePalette {
    /** Light roles. */
    val light: PaletteColors =
        PaletteColors(
            lightColorScheme(
                primary = Color(0xFF3B4350),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFDBE3F3),
                onPrimaryContainer = Color(0xFF3F4754),
                inversePrimary = Color(0xFFBFC7D7),
                secondary = Color(0xFF5C5E64),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFE1E2E9),
                onSecondaryContainer = Color(0xFF44474D),
                tertiary = Color(0xFF655B67),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFECDEEC),
                onTertiaryContainer = Color(0xFF4D444F),
                background = Color(0xFFF3F4F5),
                onBackground = Color(0xFF14161A),
                surface = Color(0xFFF3F4F5),
                onSurface = Color(0xFF14161A),
                surfaceVariant = Color(0xFFDFE3E7),
                onSurfaceVariant = Color(0xFF50545C),
                surfaceTint = Color(0xFF3B4350),
                inverseSurface = Color(0xFF2E3133),
                inverseOnSurface = Color(0xFFF0F1F3),
                error = Color(0xFFBA1A1A),
                onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFFFDAD6),
                onErrorContainer = Color(0xFF93000A),
                outline = Color(0xFF73787B),
                outlineVariant = Color(0xFFD1D4D9),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFFF8F9FC),
                surfaceDim = Color(0xFFD9DADC),
                surfaceContainer = Color(0xFFEBEDEF),
                surfaceContainerHigh = Color(0xFFE7E8EA),
                surfaceContainerHighest = Color(0xFFE1E2E5),
                surfaceContainerLow = Color(0xFFF3F3F6),
                surfaceContainerLowest = Color(0xFFFFFFFF),
            ),
        )

    /** Dark roles. */
    val dark: PaletteColors =
        PaletteColors(
            darkColorScheme(
                primary = Color(0xFFC9CED8),
                onPrimary = Color(0xFF2B3138),
                primaryContainer = Color(0xFF42474F),
                onPrimaryContainer = Color(0xFFDEE3ED),
                inversePrimary = Color(0xFF595F67),
                secondary = Color(0xFFC6C6CA),
                onSecondary = Color(0xFF2F3034),
                secondaryContainer = Color(0xFF45474A),
                onSecondaryContainer = Color(0xFFE2E2E6),
                tertiary = Color(0xFFCEC3D1),
                onTertiary = Color(0xFF352E39),
                tertiaryContainer = Color(0xFF4C444F),
                onTertiaryContainer = Color(0xFFEBDFED),
                background = Color(0xFF101113),
                onBackground = Color(0xFFE6E7EA),
                surface = Color(0xFF101113),
                onSurface = Color(0xFFE6E7EA),
                surfaceVariant = Color(0xFF45474B),
                onSurfaceVariant = Color(0xFFA8ACB3),
                surfaceTint = Color(0xFFC9CED8),
                inverseSurface = Color(0xFFE3E2E4),
                inverseOnSurface = Color(0xFF303032),
                error = Color(0xFFFFB4AB),
                onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A),
                onErrorContainer = Color(0xFFFFDAD6),
                outline = Color(0xFF8F9095),
                outlineVariant = Color(0xFF25282C),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFF39393B),
                surfaceDim = Color(0xFF101113),
                surfaceContainer = Color(0xFF181A1D),
                surfaceContainerHigh = Color(0xFF292A2C),
                surfaceContainerHighest = Color(0xFF343537),
                surfaceContainerLow = Color(0xFF151618),
                surfaceContainerLowest = Color(0xFF0D0E10),
            ),
        )
}
