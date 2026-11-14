package dev.imagh.pft.ledger.category

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Builds a string from code points, so invisible characters are never typed (or reformatted) in source. */
private fun text(vararg codePoints: Int) = String(codePoints, 0, codePoints.size)

private val burger = text(0x1F354)

/**
 * The `icon_key` grammar (plan D5), moved here from the app's `GlyphSource`: core enforces it when
 * an icon is written, and the app's parser delegates to [CategoryIcon.parse] when one is read.
 */
class CategoryIconTest :
    FunSpec({
        context("symbols") {
            test("a well-formed key parses to a Symbol and round-trips") {
                CategoryIcon.parse("sym:grocery") shouldBe CategoryIcon.Symbol("grocery")
                CategoryIcon.parse("sym:local_cafe") shouldBe CategoryIcon.Symbol("local_cafe")
                CategoryIcon.Symbol("a_0").toStored() shouldBe "sym:a_0"
                CategoryIcon.parse(CategoryIcon.Symbol("account_balance").toStored()) shouldBe
                    CategoryIcon.Symbol("account_balance")
            }

            test("the key grammar is [a-z0-9_]{1,40}") {
                CategoryIcon.parse("sym:" + "a".repeat(40)) shouldBe CategoryIcon.Symbol("a".repeat(40))
                listOf(
                    "sym:",
                    "sym:Grocery",
                    "sym:GROCERY",
                    "sym:" + "a".repeat(41),
                    "sym:a-b",
                    "sym:a b",
                    "sym:a.b",
                    "sym:é",
                    "sym:a/b",
                    "sym:../a",
                    "sym:back ",
                    "sym: back",
                    "sym:back\n",
                ).forEach { CategoryIcon.parse(it) shouldBe null }
            }

            test("a constructed Symbol is valid only for a key in the grammar") {
                CategoryIcon.Symbol("grocery").isValid shouldBe true
                CategoryIcon.Symbol("a".repeat(40)).isValid shouldBe true
                listOf("", "Grocery", "a-b", "a b", "a".repeat(41)).forEach {
                    CategoryIcon.Symbol(it).isValid shouldBe false
                }
            }
        }

        context("prefixes") {
            test("null, a missing or a wrong prefix is not an icon") {
                listOf(
                    "grocery",
                    "GROCERY",
                    "symgrocery",
                    "sym grocery",
                    " sym:grocery",
                    "SYM:grocery",
                    "Sym:grocery",
                    "emoji",
                    "emoji$burger",
                    "icon:grocery",
                    "",
                ).forEach { CategoryIcon.parse(it) shouldBe null }
                CategoryIcon.parse(null) shouldBe null
            }
        }

        context("emoji") {
            test("an emoji parses to an Emoji and round-trips") {
                CategoryIcon.parse("emoji:$burger") shouldBe CategoryIcon.Emoji(burger)
                CategoryIcon.parse("emoji:☕") shouldBe CategoryIcon.Emoji("☕")
                CategoryIcon.Emoji(burger).toStored() shouldBe "emoji:$burger"
                CategoryIcon.parse(CategoryIcon.Emoji("👍🏽").toStored()) shouldBe CategoryIcon.Emoji("👍🏽")
            }

            test("an empty emoji value is not an icon") {
                CategoryIcon.parse("emoji:") shouldBe null
            }

            test("an emoji value is at most 16 UTF-16 units") {
                val sixteen = "😀".repeat(8)
                val seventeen = sixteen + "a"
                sixteen.length shouldBe 16
                CategoryIcon.parse("emoji:$sixteen") shouldBe CategoryIcon.Emoji(sixteen)
                CategoryIcon.parse("emoji:$seventeen") shouldBe null
                CategoryIcon.Emoji(sixteen).isValid shouldBe true
                CategoryIcon.Emoji(seventeen).isValid shouldBe false
            }

            test("surrogate pairs, ZWJ sequences, flags, keycaps and tag sequences are accepted") {
                val england = "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                england.length shouldBe 14
                listOf("👨‍👩‍👧‍👦", "🇮🇳", "1️⃣", england, "👍🏽", "❤️").forEach {
                    CategoryIcon.parse("emoji:$it") shouldBe CategoryIcon.Emoji(it)
                    CategoryIcon.Emoji(it).isValid shouldBe true
                }
            }

            test("whitespace anywhere in an emoji value is rejected") {
                // space, tab, LF, CR, no-break space, em space, ideographic space, line and paragraph separators
                listOf(0x20, 0x09, 0x0A, 0x0D, 0xA0, 0x2003, 0x3000, 0x2028, 0x2029).forEach { cp ->
                    val space = text(cp)
                    listOf(space, burger + space, space + burger, burger + space + burger).forEach {
                        CategoryIcon.parse("emoji:$it") shouldBe null
                        CategoryIcon.Emoji(it).isValid shouldBe false
                    }
                }
                CategoryIcon.parse("emoji:a b") shouldBe null
            }

            test("control characters in an emoji value are rejected") {
                // NUL, BEL, ESC, DEL, NEL and the last C1 control
                listOf(0x00, 0x07, 0x1B, 0x7F, 0x85, 0x9F).forEach { cp ->
                    listOf(text(cp), burger + text(cp)).forEach {
                        CategoryIcon.Emoji(it).isValid shouldBe false
                        CategoryIcon.parse("emoji:$it") shouldBe null
                    }
                }
            }

            test("invisible format characters other than the joiner are rejected") {
                // ZWSP, word joiner, BOM, soft hyphen, LRM, RLM and the bidi embeddings, overrides and isolates
                listOf(0x200B, 0x2060, 0xFEFF, 0xAD, 0x200E, 0x200F, 0x202A, 0x202E, 0x2066, 0x2069).forEach { cp ->
                    CategoryIcon.Emoji(burger + text(cp)).isValid shouldBe false
                    CategoryIcon.parse("emoji:" + burger + text(cp)) shouldBe null
                }
                CategoryIcon.Emoji(text(0x1F468, 0x200D, 0x1F469)).isValid shouldBe true
            }

            test("unpaired surrogates are rejected") {
                // a lone high, a lone low, a reversed pair, and a lone surrogate at either end of a valid emoji
                listOf(
                    text(0xD83D),
                    text(0xDE00),
                    text(0xDE00, 0xD83D),
                    burger + text(0xD83D),
                    text(0xD83D) + "a",
                    text(0xDE00) + burger,
                ).forEach {
                    CategoryIcon.Emoji(it).isValid shouldBe false
                    CategoryIcon.parse("emoji:$it") shouldBe null
                }
            }
        }

        test("an oversized stored value is refused") {
            CategoryIcon.parse("emoji:" + "🍔".repeat(500_000)) shouldBe null
            CategoryIcon.parse("sym:" + "a".repeat(1_000_000)) shouldBe null
            CategoryIcon.parse("x".repeat(1_000_000)) shouldBe null
        }
    })
