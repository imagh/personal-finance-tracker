package dev.imagh.pft.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The two colours of a category badge: a soft [container] fill and the [content] drawn on it
 * (a symbol, an emoji or the letter fallback). In light the container is about tone 90 and the
 * content about tone 30; dark swaps them. Content is at least 4.5:1 on its container
 * (`ColorContrastTest`), so the letter fallback is legible and a symbol clears the 3:1 it needs.
 */
@Immutable
data class CategoryColors(
    val container: Color,
    val content: Color,
)

/**
 * The 12 named category hues for one theme, looked up by the name persisted in `category.colour`.
 * The names are permanent (like icon keys): add, never rename. The palette is used for glyph
 * badges only (plan §3.1), never for money, which has its own fixed tokens ([MoneyColors]).
 *
 * Read it through `PftTheme.palette`, which follows the resolved light or dark mode. The colours
 * are static code constants, not user settings.
 */
class CategoryPalette internal constructor(
    private val byKey: Map<String, CategoryColors>,
) {
    /**
     * The colours for [key]. An unknown, mis-cased or null key (for example from a newer synced
     * version) gets [FALLBACK_KEY]'s colours.
     */
    operator fun get(key: String?): CategoryColors = byKey[key] ?: byKey.getValue(FALLBACK_KEY)

    companion object {
        /** The key an unknown colour name falls back to: a neutral blue-grey. */
        const val FALLBACK_KEY = "slate"

        /** The 12 persisted names, in display order. */
        val keys: List<String> =
            listOf(
                "rose",
                "coral",
                "amber",
                "olive",
                "emerald",
                "teal",
                "sky",
                "blue",
                "indigo",
                "violet",
                "magenta",
                FALLBACK_KEY,
            )
    }
}

private fun pair(
    container: Long,
    content: Long,
) = CategoryColors(Color(container), Color(content))

// Generated in CIELAB (tone = L*): light container L*90 / content L*30, dark the reverse, at the
// highest chroma each hue holds inside sRGB (up to 30 for containers, 60 for content; slate is
// near-neutral). Hues in CIELAB degrees: rose 355, coral 30, amber 70, olive 105, emerald 150,
// teal 190, sky 230, blue 262, indigo 285, violet 310, magenta 330, slate 250.
private val LightCategoryPalette =
    CategoryPalette(
        mapOf(
            "rose" to pair(0xFFFFD8E5, 0xFF8D0150),
            "coral" to pair(0xFFFEDAD6, 0xFF91071B),
            "amber" to pair(0xFFFFDCBD, 0xFF683D00),
            "olive" to pair(0xFFE9E5AB, 0xFF474A00),
            "emerald" to pair(0xFFB8F0C5, 0xFF005227),
            "teal" to pair(0xFF95F3EB, 0xFF00504C),
            "sky" to pair(0xFFB4EBFF, 0xFF024E5F),
            "blue" to pair(0xFFCFE5FE, 0xFF024B72),
            "indigo" to pair(0xFFDCE1FF, 0xFF004590),
            "violet" to pair(0xFFEEDCFF, 0xFF58318F),
            "magenta" to pair(0xFFFED6F8, 0xFF7C1876),
            "slate" to pair(0xFFD5E5F0, 0xFF3E484F),
        ),
    )

private val DarkCategoryPalette =
    CategoryPalette(
        mapOf(
            "rose" to pair(0xFF742F4C, 0xFFFFD8E5),
            "coral" to pair(0xFF75322E, 0xFFFEDAD6),
            "amber" to pair(0xFF633F13, 0xFFFFDCBD),
            "olive" to pair(0xFF48490F, 0xFFE9E5AB),
            "emerald" to pair(0xFF13512B, 0xFFB8F0C5),
            "teal" to pair(0xFF00504C, 0xFF95F3EB),
            "sky" to pair(0xFF024E5F, 0xFFB4EBFF),
            "blue" to pair(0xFF024B72, 0xFFCFE5FE),
            "indigo" to pair(0xFF2E457A, 0xFFDCE1FF),
            "violet" to pair(0xFF543C6F, 0xFFEEDCFF),
            "magenta" to pair(0xFF673561, 0xFFFED6F8),
            "slate" to pair(0xFF3B4852, 0xFFD5E5F0),
        ),
    )

/** The category palette for the light or dark theme. */
fun categoryPalette(dark: Boolean): CategoryPalette = if (dark) DarkCategoryPalette else LightCategoryPalette

/** Provided by `PftTheme`; read it through `PftTheme.palette`. */
internal val LocalCategoryPalette = staticCompositionLocalOf { LightCategoryPalette }
