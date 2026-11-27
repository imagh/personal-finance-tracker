package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun out(
    id: String,
    group: String? = "g1",
) = tx(id, TransactionType.TRANSFER, Direction.OUT, group = group)

private fun into(
    id: String,
    group: String? = "g1",
) = tx(id, TransactionType.TRANSFER, Direction.IN, group = group)

private fun entryIds(entries: List<LedgerEntry>) =
    entries.map {
        when (it) {
            is LedgerEntry.Single -> it.transaction.meta.id.value
            is LedgerEntry.Transfer -> "${it.outgoing.meta.id.value}+${it.incoming.meta.id.value}"
        }
    }

/** Slice C D5: the pure fold of a transfer's two legs into one entry. */
class LedgerEntryFoldTest :
    FunSpec({
        test("without an account filter the two legs are one Transfer, outgoing first whatever the input order") {
            val folded = LedgerEntries.fold(listOf(into("in"), out("out")), emptyMap(), foldTransfers = true)

            entryIds(folded) shouldBe listOf("out+in")
            (folded.single() as LedgerEntry.Transfer).outgoing.direction shouldBe Direction.OUT
        }

        test("with an account filter each leg stays a Single") {
            val folded = LedgerEntries.fold(listOf(out("out"), into("in")), emptyMap(), foldTransfers = false)

            entryIds(folded) shouldBe listOf("out", "in")
        }

        test("a lone leg (data damage) stays a Single and is never dropped") {
            val folded = LedgerEntries.fold(listOf(out("out")), emptyMap(), foldTransfers = true)

            entryIds(folded) shouldBe listOf("out")
        }

        test("a leg with no group id is a Single") {
            val folded = LedgerEntries.fold(listOf(out("a", group = null), into("b", group = null)), emptyMap(), true)

            entryIds(folded) shouldBe listOf("a", "b")
        }

        test("a damaged group (three legs, or two in the same direction) is kept as Singles, none dropped") {
            val three = LedgerEntries.fold(listOf(out("o"), into("i1"), into("i2")), emptyMap(), true)
            val same = LedgerEntries.fold(listOf(out("o1"), out("o2")), emptyMap(), true)

            entryIds(three) shouldBe listOf("o", "i1", "i2")
            entryIds(same) shouldBe listOf("o1", "o2")
        }

        test("the order of the input is kept, and a pair takes the place of its first leg") {
            val folded =
                LedgerEntries.fold(
                    listOf(tx("e1"), into("in"), tx("e2"), out("out"), tx("e3")),
                    emptyMap(),
                    foldTransfers = true,
                )

            entryIds(folded) shouldBe listOf("e1", "out+in", "e2", "e3")
        }

        test("two groups fold independently, even when their legs interleave") {
            val folded =
                LedgerEntries.fold(
                    listOf(out("o1", "g1"), out("o2", "g2"), into("i1", "g1"), into("i2", "g2")),
                    emptyMap(),
                    true,
                )

            entryIds(folded) shouldBe listOf("o1+i1", "o2+i2")
        }

        test("a Single carries its lines sorted by position then id, and none when it has none") {
            val lines =
                mapOf(
                    TransactionId("e1") to
                        listOf(
                            line("e1", position = 2),
                            line("e1", position = 1, id = "e1-b"),
                            line("e1", position = 1, id = "e1-a"),
                        ),
                )

            val folded = LedgerEntries.fold(listOf(tx("e1"), tx("e2")), lines, foldTransfers = true)

            (folded[0] as LedgerEntry.Single).lines.map { it.meta.id.value } shouldBe listOf("e1-a", "e1-b", "e1-l2")
            (folded[1] as LedgerEntry.Single).lines shouldBe emptyList()
        }

        test("a row that carries a group id but is not a transfer is kept as a Single, never paired") {
            val damaged = tx("x", TransactionType.EXPENSE, Direction.IN, group = "g1")

            val withExpense = LedgerEntries.fold(listOf(out("out"), damaged), emptyMap(), foldTransfers = true)
            val withIncome =
                LedgerEntries.fold(
                    listOf(into("in"), tx("y", TransactionType.INCOME, Direction.OUT, group = "g1")),
                    emptyMap(),
                    foldTransfers = true,
                )

            entryIds(withExpense) shouldBe listOf("out", "x")
            entryIds(withIncome) shouldBe listOf("in", "y")
        }
    })
