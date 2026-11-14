package dev.imagh.pft.ledger.category

/**
 * The icon of a category: a registry symbol or one emoji, stored in the `icon_key` column as
 * `sym:<key>` or `emoji:<text>` (see [toStored] and [parse]). No icon at all is `null` on the
 * category, which the UI draws as the first letter of the name.
 *
 * This is the one home of the `icon_key` grammar (plan D5): core enforces it when an icon is
 * written ([CategoryService] refuses an invalid one with `InvalidIcon`), and the app's parser reads
 * the column through [parse]. Whether a symbol key *exists* in the app's registry, and whether an
 * emoji actually renders, are the app's checks; this type only fixes the shape.
 *
 * The constructors accept any text so that the service, not an `init` block, decides what to do
 * with a bad one (refuse it with an error the UI can show). Use [isValid] to ask.
 */
sealed interface CategoryIcon {
    /** True if this icon is in the grammar: the only icons the service will store. */
    val isValid: Boolean

    /** The `icon_key` text: `sym:<key>` or `emoji:<text>`. [parse] reads it back. */
    fun toStored(): String

    /**
     * A symbol from the app's registry, by its permanent key.
     *
     * @property key valid when it matches `[a-z0-9_]{1,40}`.
     */
    data class Symbol(
        val key: String,
    ) : CategoryIcon {
        override val isValid: Boolean
            get() = key.length in 1..MAX_SYMBOL_KEY_LENGTH && key.all { it in 'a'..'z' || it in '0'..'9' || it == '_' }

        override fun toStored(): String = SYMBOL_PREFIX + key
    }

    /**
     * One emoji or emoji sequence, drawn with the system emoji font.
     *
     * @property text valid when it is 1 to 16 UTF-16 units of well-formed UTF-16 with no whitespace
     *   or control characters. Invisible format characters are also refused (zero-width space, word
     *   joiner, byte-order mark, the bidi overrides), because they can hide or reorder neighbouring
     *   text; the exceptions are the zero-width joiner and the tag characters, which ZWJ sequences
     *   and subdivision flags need.
     */
    data class Emoji(
        val text: String,
    ) : CategoryIcon {
        override val isValid: Boolean
            get() = text.length in 1..MAX_EMOJI_UNITS && text.codePoints().allMatch(::isAllowedInEmoji)

        override fun toStored(): String = EMOJI_PREFIX + text
    }

    companion object {
        private const val SYMBOL_PREFIX = "sym:"
        private const val EMOJI_PREFIX = "emoji:"
        private const val MAX_SYMBOL_KEY_LENGTH = 40
        private const val MAX_EMOJI_UNITS = 16
        private const val ZERO_WIDTH_JOINER = 0x200D
        private const val TAG_RANGE_START = 0xE0020
        private const val TAG_RANGE_END = 0xE007F

        /**
         * Reads a stored `icon_key`. Returns the icon only if [stored] is in the grammar; null for
         * null, an unprefixed, oversized or malformed value. Never throws: a stored value is
         * untrusted (a corrupt row, or one written by a newer version and synced in M2).
         */
        fun parse(stored: String?): CategoryIcon? =
            when {
                stored == null -> null
                stored.startsWith(SYMBOL_PREFIX) -> payload(stored, SYMBOL_PREFIX, MAX_SYMBOL_KEY_LENGTH)?.let(::Symbol)
                stored.startsWith(EMOJI_PREFIX) -> payload(stored, EMOJI_PREFIX, MAX_EMOJI_UNITS)?.let(::Emoji)
                else -> null
            }?.takeIf { it.isValid }

        /** What follows [prefix], or null if more than [max] units follow it. */
        private fun payload(
            stored: String,
            prefix: String,
            max: Int,
        ): String? =
            // The length is bounded before anything is copied or scanned, so a huge stored value is cheap.
            if (stored.length <= prefix.length + max) stored.substring(prefix.length) else null

        private fun isAllowedInEmoji(codePoint: Int): Boolean =
            when (Character.getType(codePoint)) {
                Character.CONTROL.toInt(),
                Character.SPACE_SEPARATOR.toInt(),
                Character.LINE_SEPARATOR.toInt(),
                Character.PARAGRAPH_SEPARATOR.toInt(),
                Character.SURROGATE.toInt(),
                -> false
                Character.FORMAT.toInt() ->
                    codePoint == ZERO_WIDTH_JOINER || codePoint in TAG_RANGE_START..TAG_RANGE_END
                else -> true
            }
    }
}
