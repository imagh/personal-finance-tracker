package dev.imagh.pft.money

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class PaiseParserTest :
    FunSpec({
        fun accepted(text: String) = PaiseParser.parse(text)

        context("accepts") {
            test("Indian grouping with decimals (the plan's reference)") {
                accepted("1,23,456.78") shouldBe Paise(12_345_678)
            }
            test("a whole number of rupees") {
                accepted("12") shouldBe Paise(1_200)
            }
            test("one decimal digit means tenths of a rupee") {
                accepted("0.5") shouldBe Paise(50)
                accepted("12.3") shouldBe Paise(1_230)
            }
            test("two decimals, including leading zero paise") {
                accepted("0.05") shouldBe Paise(5)
                accepted("0.00") shouldBe Paise.ZERO
                accepted("7.99") shouldBe Paise(799)
            }
            test("zero") {
                accepted("0") shouldBe Paise.ZERO
            }
            test("a decimal after grouped thousands") {
                accepted("1,000.5") shouldBe Paise(100_050)
            }
            test("the rupee sign with an ungrouped decimal amount") {
                accepted("₹12.34") shouldBe Paise(1_234)
            }
            test("a number that is valid both ways") {
                accepted("12,345") shouldBe Paise(1_234_500)
            }
            test("Western grouping") {
                accepted("123,456.78") shouldBe Paise(12_345_678)
                accepted("1,234,567") shouldBe Paise(123_456_700)
            }
            test("ungrouped digits of any length within the limit") {
                accepted("123456789") shouldBe Paise(12_345_678_900)
            }
            test("grouping boundaries") {
                accepted("999") shouldBe Paise(99_900)
                accepted("1,000") shouldBe Paise(100_000)
                accepted("99,999") shouldBe Paise(9_999_900)
                accepted("1,00,000") shouldBe Paise(10_000_000)
            }
            test("the output of PaiseFormat, including the rupee sign") {
                accepted("₹1,23,456.78") shouldBe Paise(12_345_678)
            }
            test("the exact limit, grouped either way and with decimals") {
                accepted("1,00,00,00,00,000") shouldBe Paise.MAX_AMOUNT
                accepted("100,000,000,000.00") shouldBe Paise.MAX_AMOUNT
                accepted("100000000000") shouldBe Paise.MAX_AMOUNT
            }
            test("leading zeros do not count against the amount") {
                accepted("0".repeat(31) + "5") shouldBe Paise(500)
            }
        }

        context("rejects (null)") {
            val rejected =
                mapOf(
                    "three decimals" to "1.234",
                    "a minus sign" to "-5",
                    "a plus sign" to "+5",
                    "an exponent" to "1e3",
                    "empty" to "",
                    "a single space" to " ",
                    "two dots" to "12..3",
                    "a trailing dot" to "12.",
                    "no integer part" to ".5",
                    "a leading space" to " 12",
                    "a trailing space" to "12 ",
                    "an inner space" to "1 000",
                    "a lone rupee sign" to "₹",
                    "a doubled rupee sign" to "₹₹5",
                    "a trailing rupee sign" to "5₹",
                    "a lone comma" to ",",
                    "a leading comma" to ",100",
                    "a trailing comma" to "100,",
                    "a doubled comma" to "1,,000",
                    "comma in the decimals" to "1.2,3",
                    "comma-separated single digits" to "1,2,3",
                    "a short final group" to "12,34",
                    "a short Indian group" to "1,23,45",
                    "a mix of Indian and Western grouping" to "1,234,56",
                    "a long first group" to "1234,567",
                    "a long middle group" to "1,2345,678",
                    "Devanagari digits" to "१२३",
                    "Arabic-Indic digits" to "٣",
                    "fullwidth digits" to "１２",
                    "an underscore" to "1_000",
                    "a hex literal" to "0x10",
                    "not a number" to "NaN",
                    "letters" to "twelve",
                    "a newline" to "12\n",
                    "a NUL character" to "12\u0000",
                )
            rejected.forEach { (why, text) ->
                test(why) { accepted(text).shouldBeNull() }
            }
        }

        context("limits") {
            test("one paisa over the limit is rejected") {
                accepted("100000000000.01").shouldBeNull()
                accepted("1,00,00,00,00,001").shouldBeNull()
            }
            test("a number with far more digits than a Long holds is rejected without overflow") {
                accepted("99999999999999999999.5").shouldBeNull()
            }
            test("a far-too-large number is rejected, not overflowed") {
                accepted("9".repeat(PaiseParser.MAX_INPUT_LENGTH)).shouldBeNull()
            }
            test("input of exactly the maximum length is parsed") {
                accepted("0".repeat(PaiseParser.MAX_INPUT_LENGTH)) shouldBe Paise.ZERO
            }
            test("input one character over the maximum length is rejected even when its value is small") {
                accepted("0".repeat(PaiseParser.MAX_INPUT_LENGTH + 1)).shouldBeNull()
            }
            test("a huge input is rejected quickly and without error") {
                accepted("1,".repeat(1_000_000)).shouldBeNull()
            }
        }

        context("properties") {
            test("parse(format(x)) == x for every amount from 0 to the limit") {
                checkAll(Arb.long(0L, Paise.MAX_AMOUNT.value)) { x ->
                    PaiseParser.parse(PaiseFormat.format(Paise(x))) shouldBe Paise(x)
                }
            }
            test("the edges of the range round-trip") {
                listOf(0L, 1L, 99L, 100L, 99_999L, 100_000L, Paise.MAX_AMOUNT.value - 1, Paise.MAX_AMOUNT.value)
                    .forEach { PaiseParser.parse(PaiseFormat.format(Paise(it))) shouldBe Paise(it) }
            }
            test("text over the parser's own alphabet never throws; accepted results are in range and round-trip") {
                val alphabet = Arb.element("0123456789,.₹-+ e".map { it.toString() })
                // Skewed towards digits so that valid amounts actually occur (checked below).
                val digits = Arb.element("0123456789".map { it.toString() })
                val chars = Arb.choice(digits, digits, alphabet)
                var accepted = 0
                checkAll(Arb.list(chars, 0..24)) { parts ->
                    val result = PaiseParser.parse(parts.joinToString(""))
                    if (result != null) {
                        accepted++
                        (result >= Paise.ZERO && result <= Paise.MAX_AMOUNT) shouldBe true
                        PaiseParser.parse(PaiseFormat.format(result)) shouldBe result
                    }
                }
                // Guard against the property going vacuous again.
                (accepted > 100) shouldBe true
            }
            test("plain digit strings parse to exactly their value times 100 or reject over the limit") {
                checkAll(Arb.long(0L, Long.MAX_VALUE / 100)) { rupees ->
                    val expected = if (rupees * 100 <= Paise.MAX_AMOUNT.value) Paise(rupees * 100) else null
                    PaiseParser.parse(rupees.toString()) shouldBe expected
                }
            }
        }
    })
