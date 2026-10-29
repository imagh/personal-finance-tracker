package dev.imagh.pft.money

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class PaiseTest :
    FunSpec({
        test("addition and subtraction are exact") {
            (Paise(1_050) + Paise(250)) shouldBe Paise(1_300)
            (Paise(1_050) - Paise(250)) shouldBe Paise(800)
        }

        test("subtraction may go negative because balances are signed") {
            (Paise(100) - Paise(250)) shouldBe Paise(-150)
        }

        test("ZERO is the additive identity") {
            (Paise(777) + Paise.ZERO) shouldBe Paise(777)
            Paise.ZERO.value shouldBe 0L
        }

        test("addition overflow throws instead of wrapping") {
            shouldThrow<ArithmeticException> { Paise(Long.MAX_VALUE) + Paise(1) }
        }

        test("subtraction overflow throws instead of wrapping") {
            shouldThrow<ArithmeticException> { Paise(Long.MIN_VALUE) - Paise(1) }
        }

        test("adding to the most negative amount underflows and throws") {
            shouldThrow<ArithmeticException> { Paise(Long.MIN_VALUE) + Paise(-1) }
        }

        test("the largest allowed amount is Rs 1e11, i.e. 1e13 paise") {
            Paise.MAX_AMOUNT.value shouldBe 100_000_000_000L * 100
        }

        test("amounts order by their paise value") {
            (Paise(99) < Paise(100)) shouldBe true
            (Paise(-1) < Paise.ZERO) shouldBe true
            Paise(5).compareTo(Paise(5)) shouldBe 0
            listOf(Paise(30), Paise(-5), Paise(10)).sorted() shouldBe listOf(Paise(-5), Paise(10), Paise(30))
        }

        test("a + b - b == a for any non-overflowing pair") {
            // Each operand is within half the Long range, so a + b can never overflow.
            checkAll(
                Arb.long(Long.MIN_VALUE / 2, Long.MAX_VALUE / 2),
                Arb.long(Long.MIN_VALUE / 2, Long.MAX_VALUE / 2),
            ) { a, b ->
                ((Paise(a) + Paise(b)) - Paise(b)) shouldBe Paise(a)
            }
        }
    })
