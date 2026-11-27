package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Cobalt: cobalt blue on a cool white canvas, and the same blue lifted to a periwinkle on a blue-tinted
 * ink in dark. The look the owner approved on 2026-11-25 (the Cobalt plan,
 * `docs/plans/2026-11-25-m1a-cobalt-redesign.md`, D1; the mockup is `.cobalt-light` and `.cobalt-dark`
 * in `docs/plans/2026-11-25-home-redesign-mockup.html`).
 *
 * **Provenance.** Generated once, offline, on 2026-11-25 with Graphite's procedure ([GraphitePalette]):
 * a throwaway CAM16 / HCT implementation (the colour space `@material/material-color-utilities` uses),
 * written for this run and outside the repository, built the tonal palettes and the roles below; there
 * is no runtime colour library and no generator in the tree. Per mode:
 * 1. Tonal palettes from the mockup hues: primary from the hue and chroma of the mockup `primary`
 *    (light #2251FF: hue 277.5, chroma 79; dark #9DB1FF: hue 274.5, chroma 44), secondary the same hue at
 *    half the chroma, tertiary a muted (chroma 10) hue +60 rotation, neutral and neutral-variant from the
 *    mockup canvas hue at low chroma (light: hue 239, chroma 5 / 8; dark: hue 271, chroma 8 / 12). The
 *    error roles are the stock M3 ones.
 * 2. The M3 tonal-spot role mapping, and the M3 1.3 surface ladder (see the exception below).
 * 3. The owner-approved mockup values are pinned over the generated roles:
 *
 *    | Mockup token | Role | Light | Dark |
 *    |---|---|---|---|
 *    | `canvas` | `canvas` | #F5F7FB | #0B0E17 |
 *    | `island` | `island` (white in light: D10) | #FFFFFF | #151A27 |
 *    | `on` | `onSurface`, `onBackground` | #0E1B3D | #E8EBF4 |
 *    | `onvar` | `onSurfaceVariant` | #687187 | #8E97AD |
 *    | `line` | `outlineVariant` | #E9ECF3 | #222838 |
 *    | `primary` / `icon` | `primary` | #2251FF | #9DB1FF |
 *    | `primary-soft` | `primaryContainer`, `wash` (dark only: see the exception below) | #E7EDFF | #1D2647 |
 *
 *    `onPrimaryContainer` is the mockup `primary-soft`'s icon colour (`onBrandSoft`: #2251FF, #9DB1FF),
 *    so the scheme and the brand tokens agree.
 *
 * **Two tone adjustments** against the mockup, each the nearest value that passes WCAG AA (owner
 * accepted, Cobalt plan §12 Q3): light `onSurfaceVariant` #687187 (mockup #7A8399 is 3.6:1 on the
 * canvas); dark tab bar [BrandColors.nav] #151A27 (mockup #181D2B gives the active pill 2.95:1; the
 * island tone gives 3.05:1). The mockup's dark `primary` #2251FF fails as text on dark, so the scheme's
 * dark `primary` is the mockup `--icon` #9DB1FF, and the brand fill stays #2251FF in [BrandColors.brand].
 *
 * **The surface ladder is compressed**, which is not a mockup value and not in the plan (finding of K1):
 * `ColorContrastTest` requires `onSurfaceVariant` at 4.5:1 on every surface role, and the pinned light
 * #687187 and dark #8E97AD only reach that at tones 97 and above (light) and 19 and below (dark). So
 * the roles the mockup does not pin sit inside that band: light `surfaceDim` 97, `surfaceContainerHighest`
 * 97, `surfaceContainerHigh` 97.5, `surfaceContainer` and `surface` 98, `surfaceContainerLow` 99 and
 * `surfaceContainerLowest` 100 (white: never a card, sheet or dialog fill); dark `surfaceContainerHighest`
 * 18 and `surfaceBright` 19 (M3 says 22 and 24). The screens read `canvas` and `island`, which are pinned,
 * so only M3's own filled components (text fields, menus, sheets) show the narrower ladder.
 *
 * **The light `wash` is white, not `primary-soft`** (the second K1 finding): the header glow is the wash at
 * 72% over the canvas, and `ColorContrastTest` holds `onSurfaceVariant` to 4.5:1 on it. #687187 on
 * `primary-soft` over the canvas is 4.28:1 (red in `COBALT LIGHT onSurfaceVariant on glow over canvas`),
 * and the only wash that both clears it and is still a visible tint (2 tones off the canvas) is white,
 * a bright sheen. The glow is opt-in ([ThemePrefs.headerGlow], off by default). Dark `wash` is `primary-soft`.
 *
 * To change a value, edit it, then re-run `ColorContrastTest` and `AccentHueTest` and re-record the goldens.
 */
internal object CobaltPalette {
    /** Light roles. */
    val light: PaletteColors =
        PaletteColors(
            lightColorScheme(
                primary = Color(0xFF2251FF),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFE7EDFF),
                onPrimaryContainer = Color(0xFF2251FF),
                inversePrimary = Color(0xFFB9C3FF),
                secondary = Color(0xFF4E5A99),
                onSecondary = Color(0xFFFFFFFF),
                secondaryContainer = Color(0xFFDDE1FF),
                onSecondaryContainer = Color(0xFF041452),
                tertiary = Color(0xFF695A64),
                onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFF1DDE8),
                onTertiaryContainer = Color(0xFF231820),
                background = Color(0xFFF8F9FD),
                onBackground = Color(0xFF0E1B3D),
                surface = Color(0xFFF8F9FD),
                onSurface = Color(0xFF0E1B3D),
                surfaceVariant = Color(0xFFDDE3EA),
                onSurfaceVariant = Color(0xFF687187),
                surfaceTint = Color(0xFF2251FF),
                inverseSurface = Color(0xFF2D3134),
                inverseOnSurface = Color(0xFFEFF1F4),
                error = Color(0xFFBA1A1A),
                onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFFFDAD6),
                onErrorContainer = Color(0xFF93000A),
                outline = Color(0xFF71787E),
                outlineVariant = Color(0xFFE9ECF3),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFFF8F9FD),
                surfaceDim = Color(0xFFF5F6FA),
                surfaceContainer = Color(0xFFF8F9FD),
                surfaceContainerHigh = Color(0xFFF6F8FC),
                surfaceContainerHighest = Color(0xFFF5F6FA),
                surfaceContainerLow = Color(0xFFFBFCFF),
                surfaceContainerLowest = Color(0xFFFFFFFF),
            ),
            canvas = Color(0xFFF5F7FB),
            island = Color(0xFFFFFFFF),
            wash = Color(0xFFFFFFFF),
            brand =
                BrandColors(
                    brand = Color(0xFF2251FF),
                    onBrand = Color(0xFFFFFFFF),
                    brandSoft = Color(0xFFE7EDFF),
                    onBrandSoft = Color(0xFF2251FF),
                    cardStart = Color(0xFF3463FF),
                    cardMid = Color(0xFF2251FF),
                    cardEnd = Color(0xFF1B44E0),
                    onCard = Color(0xFFFFFFFF),
                    cardGhost = Color(0xFFC7D4FF),
                    cardGhost2 = Color(0xFFE2E9FF),
                    nav = Color(0xFFFFFFFF),
                ),
        )

    /** Dark roles. */
    val dark: PaletteColors =
        PaletteColors(
            darkColorScheme(
                primary = Color(0xFF9DB1FF),
                onPrimary = Color(0xFF122A71),
                primaryContainer = Color(0xFF1D2647),
                onPrimaryContainer = Color(0xFF9DB1FF),
                inversePrimary = Color(0xFF465AA2),
                secondary = Color(0xFFBEC5E7),
                onSecondary = Color(0xFF282F4A),
                secondaryContainer = Color(0xFF3E4562),
                onSecondaryContainer = Color(0xFFDCE1FF),
                tertiary = Color(0xFFD3C2CD),
                onTertiary = Color(0xFF382D36),
                tertiaryContainer = Color(0xFF50434C),
                onTertiaryContainer = Color(0xFFF0DDE9),
                background = Color(0xFF11131A),
                onBackground = Color(0xFFE8EBF4),
                surface = Color(0xFF11131A),
                onSurface = Color(0xFFE8EBF4),
                surfaceVariant = Color(0xFF434654),
                onSurfaceVariant = Color(0xFF8E97AD),
                surfaceTint = Color(0xFF9DB1FF),
                inverseSurface = Color(0xFFE1E2EC),
                inverseOnSurface = Color(0xFF2E3038),
                error = Color(0xFFFFB4AB),
                onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A),
                onErrorContainer = Color(0xFFFFDAD6),
                outline = Color(0xFF8D909F),
                outlineVariant = Color(0xFF222838),
                scrim = Color(0xFF000000),
                surfaceBright = Color(0xFF2C2E36),
                surfaceDim = Color(0xFF11131A),
                surfaceContainer = Color(0xFF1D1F27),
                surfaceContainerHigh = Color(0xFF26282F),
                surfaceContainerHighest = Color(0xFF2A2C33),
                surfaceContainerLow = Color(0xFF191B23),
                surfaceContainerLowest = Color(0xFF0C0E15),
            ),
            canvas = Color(0xFF0B0E17),
            island = Color(0xFF151A27),
            wash = Color(0xFF1D2647),
            brand =
                BrandColors(
                    brand = Color(0xFF2251FF),
                    onBrand = Color(0xFFFFFFFF),
                    brandSoft = Color(0xFF1D2647),
                    onBrandSoft = Color(0xFF9DB1FF),
                    cardStart = Color(0xFF2D5BFF),
                    cardMid = Color(0xFF2251FF),
                    cardEnd = Color(0xFF1A3FCC),
                    onCard = Color(0xFFFFFFFF),
                    cardGhost = Color(0xFF22306A),
                    cardGhost2 = Color(0xFF182148),
                    nav = Color(0xFF151A27),
                ),
        )
}
