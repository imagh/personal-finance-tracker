package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The line shape of a transfer leg (slice C C2; IT §3.3 rule 2): exactly one line and no category. The transfer
 * service never builds anything else, so this checks the shared line rules directly, on a stored leg.
 */
class TransferLinesTest :
    FunSpec({
        /** A started fixture, a stored transfer leg without lines, and the line rules over the kit's fakes. */
        suspend fun leg(): Triple<TransactionFixture, TransactionLines, LedgerTransaction> {
            val f = TransactionFixture().start()
            val kit = f.kit
            val lines =
                TransactionLines(
                    kit.writer,
                    kit.transactionLineRepository,
                    kit.categoryRepository,
                    kit.tagRepository,
                    kit.ids,
                )
            val seeded = f.seed(type = TransactionType.TRANSFER)
            f.kit.writer.delete(
                f.kit.transactionLineRepository,
                seeded.lines
                    .single()
                    .meta.id,
            )
            return Triple(f, lines, seeded.transaction)
        }

        fun plain(amount: Long) = LineDraft(amount = Paise(amount), categoryId = null)

        test("one uncategorised line is accepted") {
            val (_, lines, leg) = leg()

            lines.create(leg, listOf(plain(2_500))).single().amount shouldBe Paise(2_500)
        }

        test("a category on the line is TransferCannotBeItemised, even a live expense category") {
            val (f, lines, leg) = leg()

            f.shouldRefuse<LedgerError.TransferCannotBeItemised> {
                lines.create(leg, listOf(LineDraft(amount = Paise(2_500), categoryId = f.food.meta.id)))
            }
        }

        test("two lines are TransferCannotBeItemised, though they add up and have no category") {
            val (f, lines, leg) = leg()

            f.shouldRefuse<LedgerError.TransferCannotBeItemised> {
                lines.create(leg, listOf(plain(1_000), plain(1_500)))
            }
        }

        test("no line is TransferCannotBeItemised, not a failed sum check") {
            val (f, lines, leg) = leg()

            f.shouldRefuse<LedgerError.TransferCannotBeItemised> { lines.create(leg, emptyList()) }
        }

        test("reconciling a leg to a category or a second line is refused too") {
            val (f, lines, leg) = leg()
            val line = lines.create(leg, listOf(plain(2_500))).single()

            f.shouldRefuse<LedgerError.TransferCannotBeItemised> {
                lines.reconcile(leg, listOf(LineDraft(line.meta.id, Paise(2_500), f.food.meta.id)))
            }
            f.shouldRefuse<LedgerError.TransferCannotBeItemised> {
                lines.reconcile(leg, listOf(LineDraft(line.meta.id, Paise(1_000), null), plain(1_500)))
            }
        }
    })
