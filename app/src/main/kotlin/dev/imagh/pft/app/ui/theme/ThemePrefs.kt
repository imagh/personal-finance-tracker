package dev.imagh.pft.app.ui.theme

import androidx.compose.runtime.Immutable

/** Whether the app follows the system's light/dark setting or forces one (UI-4). */
enum class ThemeMode {
    /** Follow the system's night mode. */
    SYSTEM,

    /** Always light, whatever the system says. */
    LIGHT,

    /** Always dark, whatever the system says. */
    DARK,
}

/**
 * What the display family is (UI-2, UI-3). [TypePairing] chooses which roles use it: by default
 * only `PftTheme.type.amountHero`, every other text role being the system sans. A custom font file
 * joins this enum with the import feature (AP2).
 */
enum class DisplayFont {
    /** The bundled Newsreader serif (the default). */
    BUNDLED_SERIF,

    /** The platform's serif family. */
    SYSTEM_SERIF,

    /** "Same as text": the hero uses the sans text family too. */
    TEXT,
}

/**
 * Which type roles are set in the display family (UI-15). [DisplayFont] chooses what the display
 * family is; this chooses where it is used, so `displayFont = TEXT` with [EDITORIAL] is all sans.
 *
 * The enum names are persisted by the appearance settings (AP1), so they are permanent. A third
 * value (all serif) can be added later without a migration: an unknown stored value reads as the
 * default, and a new value is simply added.
 */
enum class TypePairing {
    /** Only `amountHero` is in the display family; every other role is the sans text family (the default). */
    HERO_ONLY,

    /**
     * The hero, `amountLarge`, `display*`, `headline*`, `titleLarge` and `titleMedium` (screen,
     * bar and section titles) are in the display family; `titleSmall` (tab labels), `body*`,
     * `label*` and `amountRow` stay sans.
     */
    EDITORIAL,
}

/**
 * The user-chosen appearance. A plain value, not a service: until the appearance settings screen
 * (AP1) the app always passes [Default]. A text-font choice joins this class with custom-font
 * import (AP2), and an accent override only if the owner asks for one (design plan §3.2).
 *
 * @property mode light, dark or follow the system.
 * @property dynamicColor use the system's Material You palette instead of the app's own scheme.
 *   Opt-in: it is off by default (UI-8, revised). Money colours are the same either way.
 * @property lightPalette the colour palette used when the resolved theme is light (UI-11).
 *   Ignored while [dynamicColor] is on.
 * @property darkPalette the colour palette used when the resolved theme is dark. Chosen
 *   independently of [lightPalette]. Ignored while [dynamicColor] is on.
 * @property background tonal or flat screen and card surfaces ([BackgroundStyle]); see [PftSurfaces].
 * @property headerGlow draw a soft tint of the palette's wash behind the header (UI-13). Off by
 *   default; it never reaches the hero row, so money is never drawn on it. See `Modifier.headerGlow`.
 * @property displayFont the display family: the font of the hero number and, under
 *   [TypePairing.EDITORIAL], of the other serif roles; see [DisplayFont].
 * @property typePairing which roles use the display family; see [TypePairing]. [TypePairing.HERO_ONLY]
 *   by default, the look before the pairing existed.
 */
@Immutable
data class ThemePrefs(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val lightPalette: ThemePalette = ThemePalette.GRAPHITE,
    val darkPalette: ThemePalette = ThemePalette.GRAPHITE,
    val background: BackgroundStyle = BackgroundStyle.TONAL,
    val headerGlow: Boolean = false,
    val displayFont: DisplayFont = DisplayFont.BUNDLED_SERIF,
    val typePairing: TypePairing = TypePairing.HERO_ONLY,
) {
    companion object {
        /** System light/dark, dynamic colour off, Graphite in both modes, tonal surfaces, no glow, hero-only type. */
        val Default = ThemePrefs()
    }
}

/** The palette in use for the resolved theme: [ThemePrefs.darkPalette] in dark, else [ThemePrefs.lightPalette]. */
internal fun ThemePrefs.paletteFor(dark: Boolean): ThemePalette = if (dark) darkPalette else lightPalette
