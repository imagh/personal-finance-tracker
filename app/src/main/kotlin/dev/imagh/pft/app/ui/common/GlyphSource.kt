package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Immutable
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.category.CategoryIcon

/**
 * What a [CategoryGlyph] draws on its badge: a registry symbol, one emoji, or a letter. It is the
 * UI model of `category.icon_key`, read by [fromIconKey].
 *
 * The **grammar** of the column (symbol keys, which emoji text is allowed) is not defined here: it
 * lives once, in core's [CategoryIcon], which also enforces it when an icon is written; [fromIconKey]
 * delegates to [CategoryIcon.parse]. What stays the app's job is whether a symbol key *exists* in
 * the registry ([PftSymbol.fromKey]) and, later (B4c), whether an emoji renders.
 *
 * `icon_key` is untrusted input at this boundary: a corrupt row or a key written by a newer
 * version synced in M2 must never crash a screen or draw something unexpected. So anything
 * malformed, oversized or unknown becomes the [Letter] fallback, and [Emoji] cannot hold an
 * invalid value at all.
 */
@Immutable
sealed interface GlyphSource {
    /** A symbol from the registry. */
    data class Symbol(
        val symbol: PftSymbol,
    ) : GlyphSource

    /**
     * One emoji (or emoji sequence), drawn with the system emoji font. [text] must be in core's emoji
     * grammar ([CategoryIcon.Emoji]): 1 to 16 UTF-16 units, no whitespace, control or invisible
     * format characters.
     *
     * @throws IllegalArgumentException if [text] breaks that grammar.
     */
    data class Emoji(
        val text: String,
    ) : GlyphSource {
        init {
            require(CategoryIcon.Emoji(text).isValid) { "not a valid emoji value" }
        }
    }

    /**
     * The upper-cased first letter of the name, for a category with no usable icon. It is one Unicode
     * code point held as a string, because a letter outside the basic plane takes two UTF-16 units.
     *
     * @property letter exactly one code point.
     * @throws IllegalArgumentException if [letter] is not exactly one code point.
     */
    data class Letter(
        val letter: String,
    ) : GlyphSource {
        init {
            require(letter.isNotEmpty() && letter.codePointCount(0, letter.length) == 1) {
                "a letter is exactly one code point"
            }
        }

        /** A letter from a basic-plane character. */
        constructor(letter: Char) : this(letter.toString())
    }

    companion object {
        /**
         * Reads a stored `icon_key` (`sym:<key>` or `emoji:<value>`) for the category called [name], using
         * core's grammar ([CategoryIcon.parse]). A null, unprefixed, oversized, malformed or unknown key
         * (one that is well-formed but not in the symbol registry) gives the [Letter] fallback for
         * [name]; this never throws.
         */
        fun fromIconKey(
            iconKey: String?,
            name: String,
        ): GlyphSource =
            when (val icon = CategoryIcon.parse(iconKey)) {
                is CategoryIcon.Symbol -> PftSymbol.fromKey(icon.key)?.let(::Symbol)
                is CategoryIcon.Emoji -> Emoji(icon.text)
                null -> null
            } ?: letterFor(name)

        /**
         * The [Letter] for [name]: its first letter or digit (by code point, so a letter outside the
         * basic plane counts, and a combining mark or unpaired surrogate never does), upper-cased one
         * code point to one code point, or `?` if the name has none.
         */
        fun letterFor(name: String): Letter {
            var index = 0
            while (index < name.length) {
                val codePoint = name.codePointAt(index)
                if (Character.isLetterOrDigit(codePoint)) {
                    return Letter(String(Character.toChars(Character.toUpperCase(codePoint))))
                }
                index += Character.charCount(codePoint)
            }
            return Letter('?')
        }
    }
}
