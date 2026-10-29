package dev.imagh.pft.money

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PaiseFormatTest :
    FunSpec({
        test("formats the plan's reference amount with Indian grouping") {
            PaiseFormat.format(Paise(12_345_678)) shouldBe "₹1,23,456.78"
        }

        test("always shows two decimals and pads small amounts") {
            PaiseFormat.format(Paise.ZERO) shouldBe "₹0.00"
            PaiseFormat.format(Paise(1)) shouldBe "₹0.01"
            PaiseFormat.format(Paise(99)) shouldBe "₹0.99"
            PaiseFormat.format(Paise(100)) shouldBe "₹1.00"
            PaiseFormat.format(Paise(25_000)) shouldBe "₹250.00"
        }

        test("groups the last three digits, then pairs, at every boundary") {
            PaiseFormat.format(Paise(99_900)) shouldBe "₹999.00"
            PaiseFormat.format(Paise(100_000)) shouldBe "₹1,000.00"
            PaiseFormat.format(Paise(9_999_900)) shouldBe "₹99,999.00"
            PaiseFormat.format(Paise(10_000_000)) shouldBe "₹1,00,000.00"
            PaiseFormat.format(Paise(999_999_900)) shouldBe "₹99,99,999.00"
            PaiseFormat.format(Paise(1_000_000_000)) shouldBe "₹1,00,00,000.00"
        }

        test("formats the maximum amount") {
            PaiseFormat.format(Paise.MAX_AMOUNT) shouldBe "₹1,00,00,00,00,000.00"
        }

        // format() never emits a sign: the caller (MoneyText) prepends U+2212 or '+'. For a negative
        // Paise it formats the magnitude, so a signed balance can be passed straight in.
        test("never emits a sign: a negative amount formats as its magnitude") {
            PaiseFormat.format(Paise(-12_345_678)) shouldBe "₹1,23,456.78"
            PaiseFormat.format(Paise(-1)) shouldBe "₹0.01"
        }

        test("formats the most negative Long without overflowing") {
            // |Long.MIN_VALUE| does not fit in a Long; the magnitude must still come out right.
            PaiseFormat.format(Paise(Long.MIN_VALUE)) shouldBe "₹92,23,37,20,36,85,47,758.08"
        }

        test("output never contains a minus sign or a plus sign") {
            listOf(Paise(-5), Paise(Long.MIN_VALUE), Paise(5), Paise.ZERO).forEach {
                PaiseFormat.format(it).any { c -> c == '-' || c == '−' || c == '+' } shouldBe false
            }
        }
    })
