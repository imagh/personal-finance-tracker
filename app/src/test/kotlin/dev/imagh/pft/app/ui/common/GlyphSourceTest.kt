package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.ui.theme.PftSymbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * `GlyphSource.fromIconKey` reads `category.icon_key`, which is untrusted at this boundary (it may be
 * corrupt or written by a newer synced version): anything malformed, oversized or unknown must land
 * on the letter fallback and never throw. The grammar itself (what a valid symbol key or emoji is)
 * lives in core's `CategoryIcon` and is pinned exhaustively by its own `CategoryIconTest`; these
 * tests cover what is the app's: that the parser delegates to it, that a symbol key must also exist
 * in the registry, and the letter rules.
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
    fun `a well-formed symbol key that is not in the registry is the letter fallback`() {
        // Existence is the app's job (PftSymbol.fromKey): core accepts any key in the grammar, 40 characters included.
        assertEquals(letterG, parse("sym:" + "a".repeat(40)))
        assertEquals(letterG, parse("sym:a_0"))
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
    fun `an emoji value of 16 UTF-16 units is an Emoji and 17 is the letter fallback`() {
        val sixteen = "😀".repeat(8)
        assertEquals(16, sixteen.length)
        assertEquals(GlyphSource.Emoji(sixteen), parse("emoji:$sixteen"))
        assertEquals(letterG, parse("emoji:${sixteen}a"))
    }

    @Test
    fun `ZWJ sequences, flags, keycaps and tag sequences are Emoji values`() {
        val family = "👨‍👩‍👧‍👦"
        val india = "🇮🇳"
        val keycap = "1️⃣"
        val england = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
        listOf(family, india, keycap, england, "👍🏽", "❤️").forEach {
            assertEquals(GlyphSource.Emoji(it), parse("emoji:$it"))
        }
    }

    /** Builds a string from code points, so invisible characters are never typed (or reformatted) in source. */
    private fun text(vararg codePoints: Int) = String(codePoints, 0, codePoints.size)

    private val burger = text(0x1F354)

    @Test
    fun `a malformed emoji value is the letter fallback`() {
        // whitespace, a control character, a zero-width space, a bidi override and a lone surrogate
        listOf(" ", burger + " ", "a b", text(0x07), burger + text(0x200B), burger + text(0x202E), text(0xD83D))
            .forEach { assertEquals(letterG, parse("emoji:$it"), it.map { c -> c.code.toString(16) }.toString()) }
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
