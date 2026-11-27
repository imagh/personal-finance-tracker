package dev.imagh.pft.ledger.query

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** A string built from code points: ktlint rewrites non-ASCII and invisible literals, which would change the test. */
private fun cps(vararg codePoints: Int) = String(codePoints, 0, codePoints.size)

/** "किराना" (kirana, a grocery): consonants with the vowel signs i (U+093F) and aa (U+093E), which are marks. */
private val kirana = cps(0x0915, 0x093F, 0x0930, 0x093E, 0x0928, 0x093E)

/**
 * Slice C D6: how typed text becomes FTS4 `MATCH` terms, one per word. The output is the only thing that reaches the
 * database, so every case here is about what can and cannot get through.
 */
class SearchTextTest :
    FunSpec({
        context("a plain word") {
            test("becomes one quoted prefix phrase") {
                SearchText.toMatchTerms("swi") shouldBe listOf("\"swi*\"")
            }
            test("keeps its case, the index folds it") {
                SearchText.toMatchTerms("SwiGGY") shouldBe listOf("\"SwiGGY*\"")
            }
            test("keeps digits") {
                SearchText.toMatchTerms("UTR4521") shouldBe listOf("\"UTR4521*\"")
            }
        }

        context("several words") {
            test("are quoted separately, one term each") {
                SearchText.toMatchTerms("swiggy  order") shouldBe listOf("\"swiggy*\"", "\"order*\"")
            }
            test("split on every character that is not a letter or digit") {
                SearchText.toMatchTerms("a,b;c-d_e/f") shouldBe
                    listOf("\"a*\"", "\"b*\"", "\"c*\"", "\"d*\"", "\"e*\"", "\"f*\"")
            }
        }

        context("hostile text") {
            test("FTS operators, quotes and stars become literal quoted words") {
                SearchText.toMatchTerms("\"a\" OR b* NEAR -c") shouldBe
                    listOf("\"a*\"", "\"OR*\"", "\"b*\"", "\"NEAR*\"", "\"c*\"")
            }
            test("a typed column filter is two literal words, not a filter") {
                SearchText.toMatchTerms("title:secret") shouldBe listOf("\"title*\"", "\"secret*\"")
            }
            test("an SQL quote, a NUL and brackets vanish") {
                SearchText.toMatchTerms("it's\u0000(x)") shouldBe listOf("\"it*\"", "\"s*\"", "\"x*\"")
            }
            test("a lone surrogate vanishes") {
                SearchText.toMatchTerms("a" + 0xD800.toChar() + "b") shouldBe listOf("\"a*\"", "\"b*\"")
            }
            test("nothing in the output but quotes, stars and word characters") {
                val out = SearchText.toMatchTerms("x'\"*()-:^{} NEAR/3 AND NOT \\ ; --").joinToString("")
                out.filterNot { it == '"' || it == '*' || it.isLetterOrDigit() } shouldBe ""
            }
        }

        context("nothing to search") {
            test("empty text has no terms") { SearchText.toMatchTerms("") shouldBe emptyList() }
            test("whitespace only has no terms") { SearchText.toMatchTerms(" \t\n ") shouldBe emptyList() }
            test("punctuation only has no terms") { SearchText.toMatchTerms("\"*()-:'") shouldBe emptyList() }
            test("a combining mark with no letter under it has no terms") {
                SearchText.toMatchTerms(cps(0x0301)) shouldBe emptyList()
            }
        }

        context("the caps") {
            test("ten words keep the first eight") {
                val raw = (1..10).joinToString(" ") { "w$it" }
                SearchText.toMatchTerms(raw) shouldBe (1..8).map { "\"w$it*\"" }
            }
            test("a hundred-character word is cut to 32") {
                SearchText.toMatchTerms("a".repeat(100)) shouldBe listOf("\"${"a".repeat(32)}*\"")
            }
            test("a word of exactly 32 is whole") {
                SearchText.toMatchTerms("b".repeat(32)) shouldBe listOf("\"${"b".repeat(32)}*\"")
            }
            test("a cut never splits a surrogate pair") {
                val pair = cps(0x1D49C) // mathematical script A, two UTF-16 units
                val out = SearchText.toMatchTerms(pair.repeat(40))
                out shouldBe listOf("\"${pair.repeat(32)}*\"")
            }
        }

        context("scripts other than Latin") {
            test("Devanagari letters are kept") {
                SearchText.toMatchTerms(cps(0x0915, 0x0916)) shouldBe listOf("\"${cps(0x0915, 0x0916)}*\"")
            }
            test("a Devanagari word keeps its vowel signs inside one term") {
                SearchText.toMatchTerms(kirana) shouldBe listOf("\"$kirana*\"")
            }
            test("an accented Latin letter is kept") {
                SearchText.toMatchTerms("caf" + cps(0x00E9)) shouldBe listOf("\"caf${cps(0x00E9)}*\"")
            }
        }
    })
