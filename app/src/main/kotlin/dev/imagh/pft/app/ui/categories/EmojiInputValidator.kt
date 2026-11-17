package dev.imagh.pft.app.ui.categories

import android.graphics.Paint
import android.icu.lang.UCharacter
import android.icu.lang.UProperty
import android.icu.text.BreakIterator
import dev.imagh.pft.ledger.category.CategoryIcon
import java.util.Locale

private const val ZERO_WIDTH_JOINER = 0x200D
private const val EMOJI_PRESENTATION_SELECTOR = 0xFE0F
private const val REGIONAL_INDICATOR_FIRST = 0x1F1E6
private const val REGIONAL_INDICATOR_LAST = 0x1F1FF
private const val TAG_FIRST = 0xE0020
private const val TAG_LAST = 0xE007F

/**
 * The UI-boundary check for the emoji a user types or pastes as a category icon (design plan section
 * 3.6, plan D5). Pasted text is **untrusted input**, so it is judged here before anything holds it:
 * [validate] returns a [CategoryIcon.Emoji] only for text that is exactly one emoji that this device can
 * draw, and null for everything else. A caller keeps only what it returns (never the candidate text) and
 * shows a fixed error for null, so the rejected text is never echoed or logged.
 *
 * The checks, cheapest first, all of which must pass:
 * 1. **Core's grammar** (`CategoryIcon.Emoji.isValid`): at most 16 UTF-16 units (so a huge paste is
 *    refused by its length before any ICU work), well-formed, and no whitespace, control or invisible
 *    format characters. This is core's own check run as it is, not a copy of it, so the validator can
 *    never accept what core would refuse to store.
 * 2. **One grapheme cluster** (ICU `BreakIterator`): two emoji, or an emoji and a letter, are refused.
 * 3. **An emoji, and nothing else in the cluster.** Either exactly two regional indicators (a country
 *    flag), or a first code point with ICU's `Extended_Pictographic` property followed only by emoji
 *    building blocks: the emoji presentation selector (U+FE0F), a skin tone modifier, tag characters
 *    (subdivision flags such as England's) and the zero-width joiner with the next pictograph (family
 *    and profession sequences). A combining mark, a bare modifier, a bare selector, a lone flag half, and
 *    a joiner left dangling at the end are refused.
 * 4. **It renders on this device** (`Paint.hasGlyph`): a pictograph the installed emoji font lacks (a
 *    reserved code point, or an emoji newer than the font) would draw as an empty box, so it is refused,
 *    and so is a ZWJ sequence the font has no single glyph for.
 *
 * **Decisions** (each pinned by a test):
 * - *Keycaps* (a digit, U+FE0F, U+20E3) are refused: a digit has no `Extended_Pictographic` property, and
 *   the design plan accepts only pictographs and flags. A digit is better expressed by the letter
 *   fallback or a symbol.
 * - *Skin tones* and *the emoji presentation selector* are accepted, as part of one cluster.
 * - *Text-style pictographs* such as (c) U+00A9 and (tm) U+2122 are accepted: Unicode marks them
 *   `Extended_Pictographic`, the design plan's rule is that property, and the device font draws them. They
 *   are an emoji in the Unicode sense, so the form lets the user use them as an icon.
 * - *Subdivision flags* are accepted (one cluster, 14 units, within core's limit).
 * - *Whitespace* around an emoji is refused, not trimmed: silently editing what was pasted would store
 *   something the user did not paste. The form shows the fixed error and the user fixes the paste.
 * - The text is *not normalised*: what passes is exactly what is stored.
 *
 * It has no state, so it is an object; there is one implementation and nothing to swap (CLAUDE.md
 * section 3). It never blocks.
 */
object EmojiInputValidator {
    /**
     * The emoji [text] is, or null if it is not exactly one emoji that renders on this device. Never
     * throws and never retains [text].
     */
    fun validate(text: String): CategoryIcon.Emoji? {
        val emoji = CategoryIcon.Emoji(text)
        val accepted = emoji.isValid && isOneEmojiCluster(text) && Paint().hasGlyph(text)
        return if (accepted) emoji else null
    }

    /**
     * [text] is one grapheme cluster (ICU) and that cluster is an emoji built only from emoji parts. This is
     * the pure-ICU half of [validate] (no `Paint`), internal so that `EmojiInputValidatorTest` can test it
     * directly: on a real font `Paint.hasGlyph` also refuses two emoji or a combining mark, which would
     * otherwise hide these rules from the tests. [text] must already be within core's length bound.
     */
    internal fun isOneEmojiCluster(text: String): Boolean {
        val breaks = BreakIterator.getCharacterInstance(Locale.ROOT)
        breaks.setText(text)
        breaks.first()
        return breaks.next() == text.length && isEmoji(text.codePoints().toArray())
    }

    /** A country flag, or a pictograph built only from emoji parts. [codePoints] is one cluster. */
    private fun isEmoji(codePoints: IntArray): Boolean =
        when {
            codePoints.size == 2 && codePoints.all { it in REGIONAL_INDICATOR_FIRST..REGIONAL_INDICATOR_LAST } -> true
            codePoints.last() == ZERO_WIDTH_JOINER -> false
            else -> isPictograph(codePoints.first()) && codePoints.all(::isEmojiPart)
        }

    private fun isEmojiPart(codePoint: Int): Boolean =
        when (codePoint) {
            ZERO_WIDTH_JOINER, EMOJI_PRESENTATION_SELECTOR -> true
            in TAG_FIRST..TAG_LAST -> true
            else -> isPictograph(codePoint) || UCharacter.hasBinaryProperty(codePoint, UProperty.EMOJI_MODIFIER)
        }

    private fun isPictograph(codePoint: Int): Boolean =
        UCharacter.hasBinaryProperty(codePoint, UProperty.EXTENDED_PICTOGRAPHIC)
}
