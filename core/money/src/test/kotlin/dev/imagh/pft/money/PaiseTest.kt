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

        test("the largest allowed amount is Rs 1e11, i.e. 1e13 paise") {
            Paise.MAX_AMOUNT shouldBe Paise(10_000_000_000_000L)
        }

        test("equal amounts are interchangeable as map keys and set members") {
            setOf(Paise(250), Paise(250), Paise(251)).size shouldBe 2
            mapOf(Paise(250) to "a")[Paise(250)] shouldBe "a"
        }

        test("amounts order by their paise value") {
            (Paise(99) < Paise(100)) shouldBe true
            (Paise(-1) < Paise.ZERO) shouldBe true
            Paise(5).compareTo(Paise(5)) shouldBe 0
            listOf(Paise(30), Paise(-5), Paise(10)).sorted() shouldBe listOf(Paise(-5), Paise(10), Paise(30))
        }

        test("a + b - b == a for any non-overflowing pair") {
            // Bounded to +-1e15 so that a + b can never overflow a Long.
            val bound = 1_000_000_000_000_000L
            checkAll(Arb.long(-bound, bound), Arb.long(-bound, bound)) { a, b ->
                ((Paise(a) + Paise(b)) - Paise(b)) shouldBe Paise(a)
            }
        }
    })
