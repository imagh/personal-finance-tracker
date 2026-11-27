package dev.imagh.pft.app.ui.home

import io.kotest.matchers.shouldBe
import org.junit.Test

/** [initialsOf]: first and last word's first grapheme cluster, upper-cased; blank gives none. */
class InitialsTest {
    @Test
    fun `two words give both initials`() {
        initialsOf("Akash Gupta") shouldBe "AG"
    }

    @Test
    fun `one word gives one letter`() {
        initialsOf("Akash") shouldBe "A"
    }

    @Test
    fun `three words give the first and the last`() {
        initialsOf("Akash Kumar Gupta") shouldBe "AG"
    }

    @Test
    fun `lower case is upper-cased`() {
        initialsOf("akash gupta") shouldBe "AG"
    }

    @Test
    fun `surrounding and repeated whitespace is ignored`() {
        initialsOf("  Akash \t  Gupta\n") shouldBe "AG"
    }

    @Test
    fun `blank gives none`() {
        initialsOf("") shouldBe ""
        initialsOf("   \t ") shouldBe ""
    }

    @Test
    fun `a Devanagari letter with its vowel sign is one cluster, not a lone consonant`() {
        // U+0915 KA + U+093F VOWEL SIGN I is one grapheme but two chars.
        initialsOf("किरण गुप्ता") shouldBe "किगु"
    }

    @Test
    fun `an emoji with a variation selector is one cluster, not a lone symbol`() {
        // U+2764 HEAVY BLACK HEART + U+FE0F VARIATION SELECTOR-16. (Skin-tone and ZWJ sequences need the
        // newer rules Android's ICU has but this JDK 17 lacks, so only the device proves those.)
        initialsOf("\u2764\uFE0F Sam") shouldBe "\u2764\uFE0FS"
    }

    @Test
    fun `a plain emoji is a whole code point, never half a surrogate pair`() {
        initialsOf("😀") shouldBe "😀"
    }
}
