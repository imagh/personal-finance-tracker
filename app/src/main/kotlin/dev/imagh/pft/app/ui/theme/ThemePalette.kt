package dev.imagh.pft.app.ui.theme

/**
 * The colour palettes a user can pick (UI-11). Each one has a light and a dark [PaletteColors];
 * the user picks the light one and the dark one independently ([ThemePrefs.lightPalette],
 * [ThemePrefs.darkPalette]).
 *
 * **The names are persisted** (the appearance settings screen, AP1, stores `name`), so they are
 * permanent: never rename or remove an entry, only add. Nothing may depend on the ordinal or on
 * the order of the entries (the UI lists them from [entries], so a new one can go anywhere).
 */
enum class ThemePalette {
    /** Graphite and platinum: the default. */
    GRAPHITE,

    /** Midnight navy with a brass accent (champagne in dark). */
    MIDNIGHT,

    /** Warm ivory paper with an ink-navy accent (pale periwinkle in dark). */
    IVORY,

    /** Deep aubergine with a plum accent (champagne in dark). */
    AUBERGINE,
}

/**
 * The colours of this palette in the given mode. Total over [ThemePalette], so adding an entry
 * without its colours does not compile.
 */
internal fun ThemePalette.colors(dark: Boolean): PaletteColors =
    when (this) {
        ThemePalette.GRAPHITE -> if (dark) GraphitePalette.dark else GraphitePalette.light
        ThemePalette.MIDNIGHT -> if (dark) MidnightPalette.dark else MidnightPalette.light
        ThemePalette.IVORY -> if (dark) IvoryPalette.dark else IvoryPalette.light
        ThemePalette.AUBERGINE -> if (dark) AuberginePalette.dark else AuberginePalette.light
    }
