package dev.imagh.pft.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * How the screen and its cards are painted (UI-12).
 *
 * **The names are persisted** (the appearance settings screen, AP1, stores `name` under
 * `ui_background`), so they are permanent: never rename or remove an entry, only add.
 */
enum class BackgroundStyle {
    /** A tinted canvas with lighter, hairline-edged islands (the default). */
    TONAL,

    /** The flat look: the screen is `surface` and the cards are `surfaceContainer`, with no edge. */
    FLAT,
}

/**
 * The resolved surface colours for the current theme, from [pftSurfaces]. Read it as
 * `PftTheme.surfaces`; screens never pick `surface` or `surfaceContainer` for a screen background
 * or a card themselves.
 *
 * @property canvas the screen, header and bottom-bar background.
 * @property island the `GroupedSection` and card fill. Never white (the owner rejected white
 *   cards), so never light `surfaceContainerLowest`.
 * @property islandEdge the 1 physical pixel edge around an island; [Color.Transparent] when FLAT.
 * @property islandHighlight a faint line lit along the top of an island, dark TONAL only (a card
 *   catching the light); [Color.Transparent] otherwise.
 * @property glow the tint of the header glow (the palette's wash) while the user has turned the
 *   glow on ([ThemePrefs.headerGlow]); null, the default, means "draw no glow". Whatever the
 *   [BackgroundStyle]. See `Modifier.headerGlow`.
 */
@Immutable
data class PftSurfaces(
    val canvas: Color,
    val island: Color,
    val islandEdge: Color,
    val islandHighlight: Color,
    val glow: Color?,
)

/** Mockup `rgba(255,255,255,.045)`: the dark island's top highlight. */
private const val DARK_HIGHLIGHT_ALPHA = 0.045f

/**
 * The surfaces of [p] in the chosen [style] (plan §3.2):
 *
 * | Style | canvas | island | islandEdge | islandHighlight |
 * |---|---|---|---|---|
 * | `TONAL` | `p.canvas` | `p.island` | `scheme.outlineVariant` | dark: white at 4.5% alpha; light: transparent |
 * | `FLAT` | `scheme.surface` | `scheme.surfaceContainer` | transparent | transparent |
 *
 * `FLAT` reproduces the pre-DS6c look exactly, in the chosen palette.
 *
 * @param dark the resolved dark/light decision (the highlight is dark-only).
 * @param glow whether the header glow is on ([ThemePrefs.headerGlow]): then [PftSurfaces.glow] is
 *   `p.wash`, else null. Defaulted off, so a caller that does not ask for the glow never gets it.
 */
internal fun pftSurfaces(
    p: PaletteColors,
    style: BackgroundStyle,
    dark: Boolean,
    glow: Boolean = false,
): PftSurfaces =
    when (style) {
        BackgroundStyle.TONAL ->
            PftSurfaces(
                canvas = p.canvas,
                island = p.island,
                islandEdge = p.scheme.outlineVariant,
                islandHighlight = if (dark) Color.White.copy(alpha = DARK_HIGHLIGHT_ALPHA) else Color.Transparent,
                glow = p.wash.takeIf { glow },
            )
        BackgroundStyle.FLAT ->
            PftSurfaces(
                canvas = p.scheme.surface,
                island = p.scheme.surfaceContainer,
                islandEdge = Color.Transparent,
                islandHighlight = Color.Transparent,
                glow = p.wash.takeIf { glow },
            )
    }

/** Provided by `PftTheme`; read it through `PftTheme.surfaces`. */
internal val LocalPftSurfaces =
    staticCompositionLocalOf { pftSurfaces(GraphitePalette.light, BackgroundStyle.TONAL, dark = false) }
