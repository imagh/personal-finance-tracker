package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun single(
    id: String,
    amount: Long,
    vararg lines: TransactionLine,
) = typed(id, TransactionType.EXPENSE, amount, *lines)

private fun typed(
    id: String,
    type: TransactionType,
    amount: Long,
    vararg lines: TransactionLine,
) = LedgerEntry.Single(tx(id, type, amount = amount), lines.toList())

private val scope = setOf(CategoryId("food"), CategoryId("groceries"))

/** Slice C D5 and L-5: totals by type, over matching lines under a line filter, never over transfers. */
class TotalsTest :
    FunSpec({
        test("without a line filter the totals are the transaction amounts by type, and net is income minus expense") {
            val entries =
                listOf(
                    single("e1", 300, line("e1", amount = 300)),
                    single("e2", 200, line("e2", amount = 200)),
                    typed("i1", TransactionType.INCOME, 1_000, line("i1", amount = 1_000)),
                )

            val totals = Totals.of(entries, TransactionFilter(), emptySet())

            totals shouldBe Totals(expense = Paise(500), income = Paise(1_000))
            totals.net shouldBe Paise(500)
        }

        test("no entries is zero, and net can be negative") {
            Totals.of(emptyList(), TransactionFilter(), emptySet()) shouldBe Totals(Paise.ZERO, Paise.ZERO)
            Totals(expense = Paise(700), income = Paise(200)).net shouldBe Paise(-500)
        }

        test("a Transfer entry is in neither total") {
            val transfer =
                LedgerEntry.Transfer(
                    tx("o", TransactionType.TRANSFER, Direction.OUT, amount = 9_999),
                    tx("i", TransactionType.TRANSFER, Direction.IN, amount = 9_999),
                )

            Totals.of(listOf(transfer), TransactionFilter(), emptySet()) shouldBe Totals(Paise.ZERO, Paise.ZERO)
        }

        test("a lone transfer leg shown as a Single (an account filter) is in neither total either") {
            val leg = typed("o", TransactionType.TRANSFER, 9_999, line("o", amount = 9_999))

            Totals.of(listOf(leg), TransactionFilter(), emptySet()) shouldBe Totals(Paise.ZERO, Paise.ZERO)
        }

        test("under a category filter only the matching lines count: Rs 2,000 with Rs 1,200 of groceries is 1,200") {
            val itemised =
                single(
                    "e1",
                    2_000_00,
                    line("e1", 1, 1_200_00, category = "groceries"),
                    line("e1", 2, 800_00, category = "travel"),
                )
            val filter = TransactionFilter(categoryIds = setOf(CategoryId("food")))

            Totals.of(listOf(itemised), filter, scope).expense shouldBe Paise(1_200_00)
        }

        test("under a category and a tag filter a line counts only if it has both") {
            val entry =
                single(
                    "e1",
                    1_000,
                    line("e1", 1, 500, category = "food"),
                    line("e1", 2, 300, category = "food", tags = setOf("trip")),
                    line("e1", 3, 200, category = "travel", tags = setOf("trip")),
                )
            val filter = TransactionFilter(categoryIds = setOf(CategoryId("food")), tagIds = setOf(TagId("trip")))

            Totals.of(listOf(entry), filter, scope).expense shouldBe Paise(300)
        }

        test("a tag filter alone sums the tagged lines, split by transaction type") {
            val entries =
                listOf(
                    single("e1", 1_000, line("e1", 1, 600, tags = setOf("trip")), line("e1", 2, 400)),
                    typed("i1", TransactionType.INCOME, 500, line("i1", amount = 500, tags = setOf("trip"))),
                )

            Totals.of(entries, TransactionFilter(tagIds = setOf(TagId("trip"))), emptySet()) shouldBe
                Totals(expense = Paise(600), income = Paise(500))
        }
    })
