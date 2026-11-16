package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.category.CategoryIcon
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * The core `CategoryIcon` to `GlyphSource` mapping every category row uses. A stored category's icon
 * is already in core's grammar, so what is left to decide is the app's part: a symbol key must exist
 * in the registry, and anything unusable falls back to the name's letter and never throws.
 */
class CategoryIconGlyphTest {
    private val letterG = GlyphSource.Letter('G')

    private fun glyph(icon: CategoryIcon?) = icon.toGlyphSource("Groceries")

    @Test
    fun `a symbol in the registry is that symbol`() {
        glyph(CategoryIcon.Symbol("grocery")) shouldBe GlyphSource.Symbol(PftSymbol.GROCERY)
    }

    @Test
    fun `an emoji is that emoji`() {
        glyph(CategoryIcon.Emoji("🛒")) shouldBe GlyphSource.Emoji("🛒")
    }

    @Test
    fun `no icon is the first letter of the name`() {
        glyph(null) shouldBe letterG
    }

    @Test
    fun `a well-formed symbol key that the registry does not have is the letter`() {
        glyph(CategoryIcon.Symbol("added_later")) shouldBe letterG
    }

    @Test
    fun `an icon outside the grammar is the letter, and never throws`() {
        // Core's constructors accept any text (the service refuses it on write), so a value can reach here.
        glyph(CategoryIcon.Symbol("Grocery")) shouldBe letterG
        glyph(CategoryIcon.Emoji("a b")) shouldBe letterG
        glyph(CategoryIcon.Emoji("")) shouldBe letterG
    }

    @Test
    fun `the stored text and the parsed icon give the same glyph`() {
        listOf("sym:grocery", "sym:nope", "emoji:🛒", "emoji:a b", "sym:", null, "junk").forEach {
            GlyphSource.fromIconKey(it, "Groceries") shouldBe CategoryIcon.parse(it).toGlyphSource("Groceries")
        }
    }
}
