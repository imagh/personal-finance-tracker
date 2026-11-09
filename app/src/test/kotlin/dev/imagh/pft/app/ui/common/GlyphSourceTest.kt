package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.ui.theme.PftSymbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * `GlyphSource.fromIconKey` parses `category.icon_key`, which is untrusted at this boundary
 * (it may be corrupt or written by a newer synced version): anything malformed, oversized or
 * unknown must land on the letter fallback and never throw.
 */
class GlyphSourceTest {
    private fun parse(iconKey: String?) = GlyphSource.fromIconKey(iconKey, "Groceries")

    private val letterG = GlyphSource.Letter('G')

    @Test
    fun `a known symbol key is a Symbol`() {
        assertEquals(GlyphSource.Symbol(PftSymbol.BACK), parse("sym:back"))
        assertEquals(GlyphSource.Symbol(PftSymbol.LOCAL_CAFE), parse("sym:local_cafe"))
    }

    @Test
    fun `an unknown, empty or mis-cased symbol key is the letter fallback`() {
        listOf(
            "sym:",
            "sym:nope",
            "sym:BACK",
            "sym:Back",
            "sym:back ",
            "sym: back",
            "sym:ba-ck",
            "sym:back\n",
        ).forEach {
            assertEquals(letterG, parse(it), "'$it'")
        }
    }

    @Test
    fun `the symbol key grammar stops at 40 characters`() {
        assertTrue(isSymbolKey("a".repeat(40)))
        assertFalse(isSymbolKey("a".repeat(41)))
        assertFalse(isSymbolKey(""))
        assertTrue(isSymbolKey("a_0"))
        listOf("A", "a-b", "a b", "a.b", "é", "a/b", "../a").forEach { assertFalse(isSymbolKey(it), it) }
        assertEquals(letterG, parse("sym:" + "a".repeat(40)))
        assertEquals(letterG, parse("sym:" + "a".repeat(41)))
    }

    @Test
    fun `a missing or wrong prefix is the letter fallback`() {
        listOf(
            "back",
            "BACK",
            "symback",
            "sym back",
            " sym:back",
            "SYM:back",
            "Sym:back",
            "emoji",
            "emoji🍔",
            "icon:back",
            "",
        ).forEach { assertEquals(letterG, parse(it), "'$it'") }
        assertEquals(letterG, parse(null))
    }

    @Test
    fun `an emoji value is an Emoji`() {
        assertEquals(GlyphSource.Emoji("🍔"), parse("emoji:🍔"))
        assertEquals(GlyphSource.Emoji("☕"), parse("emoji:☕"))
    }

    @Test
    fun `an empty emoji value is the letter fallback`() {
        assertEquals(letterG, parse("emoji:"))
    }

    @Test
    fun `an Emoji cannot be built from an invalid value`() {
        listOf("", "a b", "\u0007", "\uD83D", "😀".repeat(9)).forEach {
            assertThrows(IllegalArgumentException::class.java) { GlyphSource.Emoji(it) }
        }
    }

    @Test
    fun `an emoji value is at most 16 UTF-16 units`() {
        val sixteen = "😀".repeat(8)
        val seventeen = sixteen + "a"
        assertEquals(16, sixteen.length)
        assertTrue(isEmojiValue(sixteen))
        assertFalse(isEmojiValue(seventeen))
        assertEquals(GlyphSource.Emoji(sixteen), parse("emoji:$sixteen"))
        assertEquals(letterG, parse("emoji:$seventeen"))
    }

    @Test
    fun `surrogate pairs, ZWJ sequences, flags, keycaps and tag sequences are accepted`() {
        val family = "👨‍👩‍👧‍👦"
        val india = "🇮🇳"
        val keycap = "1️⃣"
        val england = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
        listOf(family, india, keycap, england, "👍🏽", "❤️").forEach {
            assertTrue(isEmojiValue(it), "$it (${it.length} units)")
            assertEquals(GlyphSource.Emoji(it), parse("emoji:$it"))
        }
        assertEquals(14, england.length)
    }

    /** Builds a string from code points, so invisible characters are never typed (or reformatted) in source. */
    private fun text(vararg codePoints: Int) = String(codePoints, 0, codePoints.size)

    private val burger = text(0x1F354)

    private fun describe(value: String) = value.map { it.code.toString(16) }.toString()

    @Test
    fun `whitespace anywhere in an emoji value is rejected`() {
        // space, tab, LF, CR, no-break space, em space, ideographic space, line and paragraph separators
        listOf(0x20, 0x09, 0x0A, 0x0D, 0xA0, 0x2003, 0x3000, 0x2028, 0x2029).forEach { cp ->
            val space = text(cp)
            listOf(space, burger + space, space + burger, burger + space + burger).forEach {
                assertFalse(isEmojiValue(it), describe(it))
                assertEquals(letterG, parse("emoji:$it"), describe(it))
            }
        }
    }

    @Test
    fun `control characters in an emoji value are rejected`() {
        // NUL, BEL, ESC, DEL, NEL and the last C1 control
        listOf(0x00, 0x07, 0x1B, 0x7F, 0x85, 0x9F).forEach { cp ->
            listOf(text(cp), burger + text(cp)).forEach { assertFalse(isEmojiValue(it), describe(it)) }
        }
    }

    @Test
    fun `invisible format characters other than the joiner are rejected`() {
        // ZWSP, word joiner, BOM, soft hyphen, LRM, RLM and the bidi embeddings, overrides and isolates
        listOf(0x200B, 0x2060, 0xFEFF, 0xAD, 0x200E, 0x200F, 0x202A, 0x202E, 0x2066, 0x2069).forEach { cp ->
            assertFalse(isEmojiValue(burger + text(cp)), describe(text(cp)))
        }
        assertTrue(isEmojiValue(text(0x1F468, 0x200D, 0x1F469)))
    }

    @Test
    fun `unpaired surrogates are rejected`() {
        // a lone high, a lone low, a reversed pair, and a lone surrogate at either end of a valid emoji
        listOf(
            text(0xD83D),
            text(0xDE00),
            text(0xDE00, 0xD83D),
            burger + text(0xD83D),
            text(0xD83D) + "a",
            text(0xDE00) + burger,
        ).forEach { assertFalse(isEmojiValue(it), describe(it)) }
    }

    @Test
    fun `an oversized value is rejected without being scanned`() {
        assertEquals(letterG, parse("emoji:" + "🍔".repeat(500_000)))
        assertEquals(letterG, parse("sym:" + "a".repeat(1_000_000)))
        assertEquals(letterG, parse("x".repeat(1_000_000)))
    }

    @Test
    fun `the letter is the first letter or digit of the name, upper-cased`() {
        fun letter(name: String) = GlyphSource.fromIconKey(null, name)
        assertEquals(GlyphSource.Letter('G'), letter("groceries"))
        assertEquals(GlyphSource.Letter('T'), letter("  tea"))
        assertEquals(GlyphSource.Letter('B'), letter("🍔 Burgers"))
        assertEquals(GlyphSource.Letter('1'), letter("123 Club"))
        assertEquals(GlyphSource.Letter('Ñ'), letter("ñandú"))
        assertEquals(GlyphSource.Letter('?'), letter(""))
        assertEquals(GlyphSource.Letter('?'), letter("  --- "))
    }

    @Test
    fun `a first letter outside the basic plane is the letter, not skipped`() {
        // U+1D49C mathematical script capital A, and U+10428 Deseret small long I (upper-cases to U+10400)
        assertEquals(GlyphSource.Letter(text(0x1D49C)), GlyphSource.fromIconKey(null, text(0x1D49C) + "pple"))
        assertEquals(GlyphSource.Letter(text(0x10400)), GlyphSource.fromIconKey(null, text(0x10428) + "bc"))
        assertEquals(GlyphSource.Letter(text(0x1D49C)), GlyphSource.letterFor(burger + " " + text(0x1D49C)))
    }

    @Test
    fun `a combining mark is never the letter, alone or leading`() {
        val acute = text(0x0301)
        assertEquals(GlyphSource.Letter('A'), GlyphSource.fromIconKey(null, acute + "apple"))
        assertEquals(GlyphSource.Letter('E'), GlyphSource.fromIconKey(null, "e" + acute + "cole"))
        assertEquals(GlyphSource.Letter('?'), GlyphSource.fromIconKey(null, acute + acute))
    }

    @Test
    fun `an unpaired surrogate in a name is skipped, not drawn`() {
        assertEquals(GlyphSource.Letter('B'), GlyphSource.fromIconKey(null, text(0xD83D) + "bank"))
    }

    @Test
    fun `a Letter holds exactly one code point`() {
        listOf("", "ab", text(0x1D49C, 0x1D49C)).forEach {
            assertThrows(IllegalArgumentException::class.java) { GlyphSource.Letter(it) }
        }
    }
}
