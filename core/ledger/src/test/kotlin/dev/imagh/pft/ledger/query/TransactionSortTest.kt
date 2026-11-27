package dev.imagh.pft.ledger.query

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Slice C C3c: the default order is the one the month list always had. */
class TransactionSortTest :
    FunSpec({
        test("the default is the payment date, newest first") {
            TransactionSort().key shouldBe TransactionSort.Key.OCCURRED
            TransactionSort().descending shouldBe true
        }

        test("DEFAULT is that default") {
            TransactionSort.DEFAULT shouldBe TransactionSort(TransactionSort.Key.OCCURRED, descending = true)
        }

        test("naming a key alone keeps the descending default; the direction can be flipped") {
            TransactionSort(TransactionSort.Key.TITLE).descending shouldBe true
            TransactionSort(TransactionSort.Key.TITLE, descending = false).descending shouldBe false
        }

        test("there are exactly five keys") {
            TransactionSort.Key.entries
                .map { it.name }
                .toSet() shouldBe
                setOf("OCCURRED", "CREATED", "UPDATED", "AMOUNT", "TITLE")
        }
    })
