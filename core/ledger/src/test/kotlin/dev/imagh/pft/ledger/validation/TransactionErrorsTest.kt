package dev.imagh.pft.ledger.validation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The error cases the transactions add (slice C, C1a). Their messages are fixed text: an error reaches
 * logs and the diagnostics screen, so it carries a limit at most, never an amount, a name or a note.
 */
class TransactionErrorsTest :
    FunSpec({
        test("each message is fixed text") {
            LedgerError.BreakdownMismatch().message shouldBe "The items do not add up to the amount"
            LedgerError.DuplicateLine().message shouldBe "An item appears more than once"
            LedgerError.IsTransfer().message shouldBe "Transfers are created and edited as transfers"
        }

        test("TooManyLines carries the limit and names it") {
            val error = LedgerError.TooManyLines(50)

            error.max shouldBe 50
            error.message shouldBe "A transaction may have at most 50 items"
        }
    })
