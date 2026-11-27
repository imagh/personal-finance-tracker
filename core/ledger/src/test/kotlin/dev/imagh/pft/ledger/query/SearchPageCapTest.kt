package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun entries(count: Int) = (1..count).map { LedgerEntry.Single(tx("e$it"), emptyList()) }

/** Slice C D5 (decision 3): the cap counts entries after folding, and `truncated` means more existed. */
class SearchPageCapTest :
    FunSpec({
        test("fewer entries than the cap are all kept and not truncated") {
            val page = SearchPage.capped(entries(2), max = 3)

            page shouldBe SearchPage(entries(2), truncated = false)
        }

        test("exactly the cap is not truncated") {
            SearchPage.capped(entries(3), max = 3) shouldBe SearchPage(entries(3), truncated = false)
        }

        test("one more than the cap keeps the first entries, in order, and is truncated") {
            SearchPage.capped(entries(4), max = 3) shouldBe SearchPage(entries(3), truncated = true)
        }

        test("no entries is an empty page") {
            SearchPage.capped(emptyList(), max = 3) shouldBe SearchPage(emptyList(), truncated = false)
        }

        test("the default cap is MAX_SEARCH_RESULTS") {
            val page = SearchPage.capped(entries(LedgerLimits.MAX_SEARCH_RESULTS + 1))

            page.entries.size shouldBe LedgerLimits.MAX_SEARCH_RESULTS
            page.truncated shouldBe true
        }

        test("the fetch holds the cap plus one whole entry of two rows each, so a pair is never cut") {
            SearchPage.FETCH_ROWS shouldBe 2 * (LedgerLimits.MAX_SEARCH_RESULTS + 1)
        }
    })
