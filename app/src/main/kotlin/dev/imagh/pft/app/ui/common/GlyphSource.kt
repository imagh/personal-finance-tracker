package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Immutable
import dev.imagh.pft.app.ui.theme.PftSymbol

/**
 * What a [CategoryGlyph] draws on its badge: a registry symbol, one emoji, or a letter. It is the
 * UI model of `category.icon_key`; the grammar of that column is parsed once, here, by
 * [fromIconKey].
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
     * One emoji (or emoji sequence), drawn with the system emoji font. [text] is 1 to 16 UTF-16 units
     * with no whitespace or control characters (see [isEmojiValue]).
     *
     * @throws IllegalArgumentException if [text] breaks that grammar.
     */
    data class Emoji(
        val text: String,
    ) : GlyphSource {
        init {
            require(isEmojiValue(text)) { "not a valid emoji value" }
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
        private const val SYMBOL_PREFIX = "sym:"
        private const val EMOJI_PREFIX = "emoji:"

        /**
         * Parses a stored `icon_key` (`sym:<key>` or `emoji:<value>`) for the category called [name].
         * A null, unprefixed, oversized, malformed or unknown key gives the [Letter] fallback for
         * [name]; this never throws.
         */
        fun fromIconKey(
            iconKey: String?,
            name: String,
        ): GlyphSource = parseSymbol(iconKey) ?: parseEmoji(iconKey) ?: letterFor(name)

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

        private fun parseSymbol(iconKey: String?): GlyphSource? {
            val key = payload(iconKey, SYMBOL_PREFIX, MAX_SYMBOL_KEY_LENGTH)
            return if (key != null && isSymbolKey(key)) PftSymbol.fromKey(key)?.let(::Symbol) else null
        }

        private fun parseEmoji(iconKey: String?): GlyphSource? {
            val value = payload(iconKey, EMOJI_PREFIX, MAX_EMOJI_UNITS)
            return if (value != null && isEmojiValue(value)) Emoji(value) else null
        }

        /** What follows [prefix], or null if there is no such prefix or more than [max] units follow it. */
        private fun payload(
            iconKey: String?,
            prefix: String,
            max: Int,
        ): String? =
            // The length is bounded before anything is copied or scanned, so a huge stored value is cheap.
            iconKey?.takeIf { it.startsWith(prefix) && it.length <= prefix.length + max }?.substring(prefix.length)
    }
}

private const val MAX_SYMBOL_KEY_LENGTH = 40
private const val MAX_EMOJI_UNITS = 16
private const val ZERO_WIDTH_JOINER = 0x200D
private const val TAG_RANGE_START = 0xE0020
private const val TAG_RANGE_END = 0xE007F

/** True for a symbol key in the permanent grammar `[a-z0-9_]{1,40}` (existence is [PftSymbol.fromKey]'s job). */
internal fun isSymbolKey(key: String): Boolean =
    key.length in 1..MAX_SYMBOL_KEY_LENGTH && key.all { it in 'a'..'z' || it in '0'..'9' || it == '_' }

/**
 * True for an emoji value in the `icon_key` grammar: 1 to 16 UTF-16 units, well-formed UTF-16 (no
 * unpaired surrogate), no whitespace or control characters. Invisible format characters are also
 * refused (zero-width space, word joiner, byte-order mark, the bidi overrides), because they can
 * hide or reorder neighbouring text; the exceptions are the zero-width joiner and the tag
 * characters, which ZWJ sequences and subdivision flags need. Whether it really is a renderable
 * emoji is the input validator's job when a category is saved (B4), not this check's.
 */
internal fun isEmojiValue(value: String): Boolean =
    value.length in 1..MAX_EMOJI_UNITS && value.codePoints().allMatch(::isAllowedInEmoji)

private fun isAllowedInEmoji(codePoint: Int): Boolean =
    when (Character.getType(codePoint)) {
        Character.CONTROL.toInt(),
        Character.SPACE_SEPARATOR.toInt(),
        Character.LINE_SEPARATOR.toInt(),
        Character.PARAGRAPH_SEPARATOR.toInt(),
        Character.SURROGATE.toInt(),
        -> false
        Character.FORMAT.toInt() -> codePoint == ZERO_WIDTH_JOINER || codePoint in TAG_RANGE_START..TAG_RANGE_END
        else -> true
    }
