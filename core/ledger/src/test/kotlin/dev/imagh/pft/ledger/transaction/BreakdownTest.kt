package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

/** The pure rules a list of lines must pass before anything is written (IT §3.3 rule 1; slice C §2.2 item 4). */
class BreakdownTest :
    FunSpec({
        val food = CategoryId("c-food")

        fun line(
            paise: Long,
            id: TransactionLineId? = null,
            note: String? = null,
        ) = LineDraft(id = id, amount = Paise(paise), categoryId = food, note = note)

        context("valid") {
            test("one line equal to the total is valid and kept as given") {
                val only = LineDraft(amount = Paise(500), categoryId = food, tagIds = setOf(TagId("t-1")))

                val breakdown = Breakdown.of(Paise(500), listOf(only))

                breakdown.total shouldBe Paise(500)
                breakdown.lines shouldBe listOf(only)
            }

            test("several lines that add up to the total are valid, in the order given") {
                val lines = listOf(line(300), line(150), line(50))

                Breakdown.of(Paise(500), lines).lines shouldBe lines
            }

            test("exactly the line limit is valid") {
                val lines = List(LedgerLimits.MAX_LINES_PER_TRANSACTION) { line(2) }

                Breakdown.of(Paise(2L * LedgerLimits.MAX_LINES_PER_TRANSACTION), lines).lines.size shouldBe
                    LedgerLimits.MAX_LINES_PER_TRANSACTION
            }

            test("a line of exactly the amount limit is valid") {
                val largest = LineDraft(amount = LedgerLimits.MAX_AMOUNT, categoryId = null)

                Breakdown
                    .of(LedgerLimits.MAX_AMOUNT, listOf(largest))
                    .lines
                    .single()
                    .amount shouldBe LedgerLimits.MAX_AMOUNT
            }

            test("lines with no id (new lines) are not duplicates of each other") {
                Breakdown.of(Paise(2), listOf(line(1), line(1))).lines.size shouldBe 2
            }
        }

        context("the sum") {
            test("lines that add up to less or more than the total are a mismatch") {
                shouldThrow<LedgerError.BreakdownMismatch> { Breakdown.of(Paise(500), listOf(line(300), line(199))) }
                shouldThrow<LedgerError.BreakdownMismatch> { Breakdown.of(Paise(500), listOf(line(300), line(201))) }
            }

            test("an empty list is a mismatch") {
                shouldThrow<LedgerError.BreakdownMismatch> { Breakdown.of(Paise(500), emptyList()) }
            }

            test("the mismatch message names no amount") {
                val error =
                    shouldThrow<LedgerError.BreakdownMismatch> { Breakdown.of(Paise(98_765), listOf(line(12_345))) }

                error.message shouldBe "The items do not add up to the amount"
            }
        }

        context("the count") {
            test("one line over the limit is refused with the limit") {
                val lines = List(LedgerLimits.MAX_LINES_PER_TRANSACTION + 1) { line(1) }

                // The lines add up, so only the count rule can refuse them.
                val error =
                    shouldThrow<LedgerError.TooManyLines> {
                        Breakdown.of(Paise(LedgerLimits.MAX_LINES_PER_TRANSACTION + 1L), lines)
                    }

                error.max shouldBe 50
            }
        }

        context("each amount") {
            test("a zero line is refused even when the lines add up") {
                shouldThrow<LedgerError.AmountNotPositive> { Breakdown.of(Paise(100), listOf(line(0), line(100))) }
            }

            test("a negative line is refused even when the lines add up") {
                shouldThrow<LedgerError.AmountNotPositive> { Breakdown.of(Paise(100), listOf(line(-50), line(150))) }
            }

            test("a line over the amount limit is refused even when the lines add up") {
                val over = LedgerLimits.MAX_AMOUNT.value + 1

                shouldThrow<LedgerError.AmountTooLarge> { Breakdown.of(Paise(over), listOf(line(over))) }
            }
        }

        context("notes") {
            test("a note is trimmed") {
                Breakdown
                    .of(Paise(1), listOf(line(1, note = "  Milk \n")))
                    .lines
                    .single()
                    .note shouldBe "Milk"
            }

            test("a blank note becomes none") {
                Breakdown
                    .of(Paise(1), listOf(line(1, note = " \t ")))
                    .lines
                    .single()
                    .note shouldBe null
                Breakdown
                    .of(Paise(1), listOf(line(1, note = "")))
                    .lines
                    .single()
                    .note shouldBe null
            }

            test("a note of exactly the limit is kept") {
                val note = "n".repeat(LedgerLimits.MAX_LINE_NOTE_LENGTH)

                Breakdown
                    .of(Paise(1), listOf(line(1, note = note)))
                    .lines
                    .single()
                    .note shouldBe note
            }

            test("a note over the limit, once trimmed, is refused with the limit") {
                val note = " " + "n".repeat(LedgerLimits.MAX_LINE_NOTE_LENGTH + 1) + " "

                val error =
                    shouldThrow<LedgerError.NameTooLong> { Breakdown.of(Paise(1), listOf(line(1, note = note))) }

                error.max shouldBe 200
            }
        }

        context("line ids") {
            test("the same line id twice is refused, even when the lines add up") {
                val id = TransactionLineId("l-1")

                shouldThrow<LedgerError.DuplicateLine> { Breakdown.of(Paise(2), listOf(line(1, id), line(1, id))) }
            }

            test("different line ids are fine") {
                Breakdown
                    .of(Paise(2), listOf(line(1, TransactionLineId("l-1")), line(1, TransactionLineId("l-2"))))
                    .lines
                    .size shouldBe 2
            }
        }

        context("property") {
            // Parts up to Rs 2e9 each, so 50 of them stay within the amount limit.
            val parts = Arb.list(Arb.long(1L..200_000_000_000L), 1..LedgerLimits.MAX_LINES_PER_TRANSACTION)

            test("any partition of a total into 1 to 50 positive parts is valid") {
                checkAll(parts) { amounts ->
                    val total = Paise(amounts.sum())

                    Breakdown.of(total, amounts.map { line(it) }).lines.map { it.amount.value } shouldBe amounts
                }
            }

            test("moving one paisa out of any part makes the partition invalid") {
                checkAll(parts, Arb.int(0 until LedgerLimits.MAX_LINES_PER_TRANSACTION)) { amounts, pick ->
                    val total = Paise(amounts.sum())
                    val index = pick % amounts.size
                    val moved = amounts.mapIndexed { i, paise -> if (i == index) paise - 1 else paise }

                    val error = shouldThrow<LedgerError> { Breakdown.of(total, moved.map { line(it) }) }

                    // A part of one paisa becomes zero, which the amount rule refuses first.
                    if (amounts[index] == 1L) {
                        error::class shouldBe LedgerError.AmountNotPositive::class
                    } else {
                        error::class shouldBe LedgerError.BreakdownMismatch::class
                    }
                }
            }
        }
    })
