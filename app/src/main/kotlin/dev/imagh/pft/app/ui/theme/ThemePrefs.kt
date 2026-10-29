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
 * The user-chosen appearance. A plain value, not a service: until the appearance settings screen
 * (AP1) the app always passes [Default]. The font choices join this class in DS2, and an accent
 * override only if the owner asks for one (design plan §3.2).
 *
 * @property mode light, dark or follow the system.
 * @property dynamicColor use the system's Material You palette for the accent. Off falls back to
 *   [BrandColors]. Money colours are the same either way.
 */
@Immutable
data class ThemePrefs(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
) {
    companion object {
        /** System light/dark, dynamic colour on. */
        val Default = ThemePrefs()
    }
}
