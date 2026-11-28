package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The brand colours M3 has no role for: a filled brand surface, the balance card's gradient and the
 * floating tab bar (Cobalt plan 2026-11-25 D1). Read them as `PftTheme.brand`; a screen never picks
 * `primary` for a brand fill itself.
 *
 * **Blue never for text.** App-drawn text (titles, body, amounts, captions) is never [brand]; this
 * is the *fill* and *icon* colour. The only brand-coloured text is M3's own control labels.
 *
 * @property brand the filled brand surface (the active tab pill, a filled action).
 * @property onBrand text and icons on [brand].
 * @property brandSoft the soft brand tint (an icon tile, a selected chip).
 * @property onBrandSoft the icon colour on [brandSoft]; an icon, so it is held to 3:1, not 4.5:1.
 * @property cardStart the balance card gradient's first stop (top-left).
 * @property cardMid the gradient's middle stop.
 * @property cardEnd the gradient's last stop (bottom-right).
 * @property onCard text on every card stop. Secondary text on the card is this colour at a lower alpha.
 * @property cardGhost the large decorative shape on the card.
 * @property cardGhost2 the second, subtler decorative shape on the card.
 * @property nav the floating tab bar's fill.
 */
@Immutable
data class BrandColors(
    val brand: Color,
    val onBrand: Color,
    val brandSoft: Color,
    val onBrandSoft: Color,
    val cardStart: Color,
    val cardMid: Color,
    val cardEnd: Color,
    val onCard: Color,
    val cardGhost: Color,
    val cardGhost2: Color,
    val nav: Color,
)

/**
 * The brand tokens of a palette that does not hand-pick them: Graphite, Midnight, Ivory, Aubergine
 * and dynamic colour. They are read off [scheme] and [island], so a palette's brand always agrees
 * with its own accent and no value is invented per palette:
 *
 * | Token | From |
 * |---|---|
 * | `brand`, `cardStart`, `cardMid`, `cardEnd` | `primary` (the card is a flat fill: three equal stops) |
 * | `onBrand`, `onCard` | `onPrimary` |
 * | `brandSoft`, `cardGhost` | `primaryContainer` |
 * | `onBrandSoft` | `onPrimaryContainer` |
 * | `cardGhost2` | `outlineVariant` |
 * | `nav` | [island] |
 *
 * Pure, so the tests pin it and sweep it (`ColorContrastTest`, also across the tones M3's dynamic
 * colour can produce). If a derived pair ever fails a contrast test, the fix belongs here, never in
 * an exemption for the palette.
 *
 * @param island the palette's island fill (`PaletteColors.island`): dynamic colour has no `island`
 *   role, so the caller passes the one it derived.
 */
internal fun derivedBrand(
    scheme: ColorScheme,
    island: Color,
): BrandColors =
    BrandColors(
        brand = scheme.primary,
        onBrand = scheme.onPrimary,
        brandSoft = scheme.primaryContainer,
        onBrandSoft = scheme.onPrimaryContainer,
        cardStart = scheme.primary,
        cardMid = scheme.primary,
        cardEnd = scheme.primary,
        onCard = scheme.onPrimary,
        cardGhost = scheme.primaryContainer,
        cardGhost2 = scheme.outlineVariant,
        nav = island,
    )

/** Provided by `PftTheme`; read it through `PftTheme.brand`. */
internal val LocalPftBrand =
    staticCompositionLocalOf { GraphitePalette.light.brand }
