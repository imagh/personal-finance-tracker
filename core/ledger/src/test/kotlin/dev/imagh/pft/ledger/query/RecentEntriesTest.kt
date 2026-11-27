package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Slice C C3c: the limit of `recent` is checked in core (fail closed) and counts entries after folding. */
class RecentEntriesTest :
    FunSpec({
        test("a limit below one is refused") {
            shouldThrow<LedgerError.InvalidRange> { RecentEntries.fetchRows(0) }
            shouldThrow<LedgerError.InvalidRange> { RecentEntries.fetchRows(-1) }
            shouldThrow<LedgerError.InvalidRange> { RecentEntries.fetchRows(Int.MIN_VALUE) }
        }

        test("a limit above the search cap is refused, the cap itself is allowed") {
            shouldThrow<LedgerError.InvalidRange> { RecentEntries.fetchRows(LedgerLimits.MAX_SEARCH_RESULTS + 1) }
            shouldThrow<LedgerError.InvalidRange> { RecentEntries.fetchRows(Int.MAX_VALUE) }
            RecentEntries.fetchRows(LedgerLimits.MAX_SEARCH_RESULTS) shouldBe 2 * LedgerLimits.MAX_SEARCH_RESULTS
        }

        test("one entry is at most two rows, so the fetch is twice the limit") {
            RecentEntries.fetchRows(1) shouldBe 2
            RecentEntries.fetchRows(5) shouldBe 10
        }

        test("the cut keeps the first entries in order") {
            val all = (1..4).map { LedgerEntry.Single(tx("e$it"), emptyList()) }

            RecentEntries.cut(all, 3) shouldBe all.take(3)
        }

        test("fewer entries than the limit are all kept") {
            val all = (1..2).map { LedgerEntry.Single(tx("e$it"), emptyList()) }

            RecentEntries.cut(all, 5) shouldBe all
        }
    })
