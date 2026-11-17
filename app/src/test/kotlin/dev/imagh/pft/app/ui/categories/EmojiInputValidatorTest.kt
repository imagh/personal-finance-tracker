package dev.imagh.pft.app.ui.categories

import android.graphics.Paint
import dev.imagh.pft.ledger.category.CategoryIcon
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Builds a string from code points, so invisible characters are never typed (or reformatted) in source. */
private fun text(vararg codePoints: Int) = String(codePoints, 0, codePoints.size)

private const val ZWJ = 0x200D
private const val VS16 = 0xFE0F
private val cart = text(0x1F6D2)
private val familyOfThree = text(0x1F468, ZWJ, 0x1F469, ZWJ, 0x1F467)
private val indiaFlag = text(0x1F1EE, 0x1F1F3)
private val englandFlag = text(0x1F3F4, 0xE0067, 0xE0062, 0xE0065, 0xE006E, 0xE0067, 0xE007F)
private val thumbsUpMediumTone = text(0x1F44D, 0x1F3FD)
private val heartWithVs16 = text(0x2764, VS16)

/** A reserved code point inside Extended_Pictographic: it looks like an emoji to Unicode but no font draws it. */
private val reservedPictograph = text(0x1FC00)

/**
 * [EmojiInputValidator] against the real ICU and the real `Paint.hasGlyph` (native graphics, so the
 * answers are the platform's, not a stub's). Every untrusted-input case is also checked against core's
 * `CategoryIcon` grammar, which the validator must never be looser than.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class EmojiInputValidatorTest {
    private fun accepted(text: String): CategoryIcon.Emoji? = EmojiInputValidator.validate(text)

    // --- Evidence that Paint.hasGlyph answers under Robolectric (plan: [Verify])

    @Test
    fun `Paint hasGlyph says yes for a real emoji and no for code points no font draws`() {
        Paint().hasGlyph(cart) shouldBe true
        Paint().hasGlyph(familyOfThree) shouldBe true
        Paint().hasGlyph(text(0x10FFFD)) shouldBe false
        Paint().hasGlyph(text(0x0378)) shouldBe false
        Paint().hasGlyph(reservedPictograph) shouldBe false
    }

    // --- What is accepted

    @Test
    fun `one emoji is accepted, as the emoji value`() {
        accepted(cart) shouldBe CategoryIcon.Emoji(cart)
    }

    @Test
    fun `a ZWJ family is one emoji`() {
        accepted(familyOfThree) shouldBe CategoryIcon.Emoji(familyOfThree)
    }

    @Test
    fun `a country flag and a subdivision flag are one emoji each`() {
        accepted(indiaFlag) shouldBe CategoryIcon.Emoji(indiaFlag)
        accepted(englandFlag) shouldBe CategoryIcon.Emoji(englandFlag)
    }

    @Test
    fun `a skin tone modifier belongs to its emoji`() {
        accepted(thumbsUpMediumTone) shouldBe CategoryIcon.Emoji(thumbsUpMediumTone)
    }

    @Test
    fun `an emoji followed by the emoji presentation selector is accepted`() {
        accepted(heartWithVs16) shouldBe CategoryIcon.Emoji(heartWithVs16)
    }

    // --- What is rejected

    @Test
    fun `two emoji are rejected, and so are two flags`() {
        accepted(cart + cart) shouldBe null
        accepted(indiaFlag + indiaFlag) shouldBe null
        accepted(cart + heartWithVs16) shouldBe null
    }

    @Test
    fun `a letter, a digit and a word are rejected`() {
        accepted("a") shouldBe null
        accepted("7") shouldBe null
        accepted("Food") shouldBe null
        accepted(cart + "a") shouldBe null
        accepted("a" + cart) shouldBe null
    }

    @Test
    fun `nothing and blank text are rejected`() {
        accepted("") shouldBe null
        accepted("   ") shouldBe null
    }

    @Test
    fun `an emoji with a space, a no-break space or a line break around it is rejected, not trimmed`() {
        accepted("$cart ") shouldBe null
        accepted(" $cart") shouldBe null
        accepted(cart + text(0x00A0)) shouldBe null
        accepted("$cart\n") shouldBe null
        accepted("\t$cart") shouldBe null
    }

    @Test
    fun `a zero-width space, a bidi override and a lone surrogate are rejected`() {
        accepted(cart + text(0x200B)) shouldBe null
        accepted(text(0x200B) + cart) shouldBe null
        accepted(text(0x202E) + cart) shouldBe null
        accepted(cart + text(0x2066)) shouldBe null
        accepted(cart + "\uD83D") shouldBe null
        accepted("\uDED2") shouldBe null
    }

    @Test
    fun `a keycap is rejected`() {
        accepted(text('1'.code, VS16, 0x20E3)) shouldBe null
        accepted(text('#'.code, 0x20E3)) shouldBe null
    }

    @Test
    fun `a bare skin tone modifier, a lone flag half and a bare selector are rejected`() {
        accepted(text(0x1F3FD)) shouldBe null
        accepted(text(0x1F1EE)) shouldBe null
        accepted(indiaFlag + text(0x1F1EE)) shouldBe null
        accepted(text(VS16)) shouldBe null
    }

    @Test
    fun `an emoji dressed with a combining mark is rejected`() {
        accepted(cart + text(0x0301)) shouldBe null
    }

    @Test
    fun `a dangling joiner after an emoji is rejected`() {
        accepted(cart + text(ZWJ)) shouldBe null
    }

    @Test
    fun `a pictograph no font on this device draws is rejected, as is a code point that is not an emoji at all`() {
        accepted(reservedPictograph) shouldBe null
        accepted(text(0x10FFFD)) shouldBe null
        accepted(text(0x0378)) shouldBe null
    }

    @Test
    fun `a huge paste is rejected`() {
        accepted(cart.repeat(2_000_000)) shouldBe null
        accepted(text(ZWJ).repeat(2_000_000)) shouldBe null
    }

    @Test
    fun `text-style pictographs are accepted, by the design plan's Extended_Pictographic rule`() {
        accepted(text(0x00A9)) shouldBe CategoryIcon.Emoji(text(0x00A9))
        accepted(text(0x2122)) shouldBe CategoryIcon.Emoji(text(0x2122))
    }

    // --- The ICU half on its own (no Paint), so the render check cannot hide a broken rule

    @Test
    fun `the ICU half accepts one emoji cluster of every accepted kind`() {
        listOf(cart, familyOfThree, indiaFlag, englandFlag, thumbsUpMediumTone, heartWithVs16).forEach {
            EmojiInputValidator.isOneEmojiCluster(it) shouldBe true
        }
    }

    @Test
    fun `the ICU half refuses more than one cluster`() {
        EmojiInputValidator.isOneEmojiCluster(cart + cart) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(indiaFlag + indiaFlag) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(cart + "a") shouldBe false
    }

    @Test
    fun `the ICU half refuses a cluster that is not only emoji parts`() {
        EmojiInputValidator.isOneEmojiCluster(cart + text(0x0301)) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(text(VS16)) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(text(0x1F3FD)) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(text(0x1F1EE)) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(cart + text(ZWJ)) shouldBe false
        EmojiInputValidator.isOneEmojiCluster(text('1'.code, VS16, 0x20E3)) shouldBe false
    }

    // --- Never looser than core's grammar

    @Test
    fun `anything the validator accepts is in core's emoji grammar, and it hands out core's own type`() {
        val candidates =
            listOf(
                cart,
                familyOfThree,
                indiaFlag,
                englandFlag,
                thumbsUpMediumTone,
                heartWithVs16,
                text(0x00A9),
                "$cart ",
                cart + text(0x200B),
                cart + text(ZWJ),
                cart + cart,
                reservedPictograph,
            )
        candidates.mapNotNull(::accepted).forEach {
            it.isValid shouldBe true
            CategoryIcon.parse(it.toStored()) shouldBe it
        }
        candidates.filter { !CategoryIcon.Emoji(it).isValid }.forEach { accepted(it) shouldBe null }
    }

    @Test
    fun `what core's grammar rejects is rejected here too`() {
        val coreRejects =
            listOf(
                "",
                " ",
                "$cart ",
                cart + text(0x200B),
                text(0x202E) + cart,
                cart + "\uD83D",
                cart + text(0x2028),
                cart + text(0x0000),
                cart.repeat(9),
            )
        coreRejects.forEach {
            CategoryIcon.Emoji(it).isValid shouldBe false
            accepted(it) shouldBe null
        }
    }
}
