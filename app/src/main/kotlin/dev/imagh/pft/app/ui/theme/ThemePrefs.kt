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
 * Where the hero number's font comes from (UI-2, UI-3). Only `PftTheme.type.amountHero` is set in
 * the display font; every other text role is the system sans. A custom font file joins this enum
 * with the import feature (AP2).
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
 * The user-chosen appearance. A plain value, not a service: until the appearance settings screen
 * (AP1) the app always passes [Default]. A text-font choice joins this class with custom-font
 * import (AP2), and an accent override only if the owner asks for one (design plan §3.2).
 *
 * @property mode light, dark or follow the system.
 * @property dynamicColor use the system's Material You palette instead of the app's own scheme.
 *   Opt-in: it is off by default (UI-8, revised). Money colours are the same either way.
 * @property displayFont the font of the hero number (`amountHero`); see [DisplayFont].
 */
@Immutable
data class ThemePrefs(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val displayFont: DisplayFont = DisplayFont.BUNDLED_SERIF,
) {
    companion object {
        /** System light/dark, dynamic colour off. */
        val Default = ThemePrefs()
    }
}
