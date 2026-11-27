package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Duration

/** Delete, restore, mark verified and read (slice C C1a; IT §3.3 rule 7; PRD L-6). */
class TransactionLifecycleTest :
    FunSpec({
        context("delete and restore") {
            test("delete tombstones the transaction only: its line stays as it was") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())
                f.kit.clock.advance(Duration.ofMinutes(1))
                val before = f.trail.size

                val deleted = f.service.delete(saved.transaction.meta.id)

                deleted.meta.isDeleted shouldBe true
                f
                    .storedTransactions()
                    .single()
                    .meta.isDeleted shouldBe true
                f.storedLines() shouldBe saved.lines
                val written = f.trail.drop(before)
                written shouldHaveSize 1
                written.single().entityType shouldBe "ledger_transaction"
                written.single().action shouldBe AuditAction.DELETE
                f.service.get(saved.transaction.meta.id) shouldBe null
            }

            test("restore brings the transaction back with the same line") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))
                f.service.delete(saved.transaction.meta.id)

                val restored = f.service.restore(saved.transaction.meta.id)

                restored.meta.isDeleted shouldBe false
                f.trail.shouldHaveAudited("ledger_transaction", saved.transaction.meta.id.value, AuditAction.RESTORE)
                val read = f.service.get(saved.transaction.meta.id)
                read?.transaction shouldBe restored
                read?.lines shouldBe saved.lines
            }

            test("deleting twice, or restoring a live one, writes nothing") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())
                val afterCreate = f.trail.size

                f.service.restore(saved.transaction.meta.id) shouldBe saved.transaction
                f.trail shouldHaveSize afterCreate

                f.service.delete(saved.transaction.meta.id)
                val afterDelete = f.trail.size
                val deleted = f.storedTransactions().single()

                f.service.delete(saved.transaction.meta.id)

                f.trail shouldHaveSize afterDelete
                f.storedTransactions().single() shouldBe deleted
            }

            test("deleting a transfer leg with no group id is refused: its other leg cannot be found") {
                val f = TransactionFixture().start()
                val leg = f.seed(type = TransactionType.TRANSFER)

                f.shouldRefuse<LedgerError.IsTransfer> { f.service.delete(leg.transaction.meta.id) }
            }

            test("restoring a transfer leg with no group id is refused: its other leg cannot be found") {
                val f = TransactionFixture().start()
                val leg = f.seed(type = TransactionType.TRANSFER)
                f.kit.writer.delete(f.kit.transactionRepository, leg.transaction.meta.id)

                f.shouldRefuse<LedgerError.IsTransfer> { f.service.restore(leg.transaction.meta.id) }
            }

            test("an unknown transaction is not found") {
                val f = TransactionFixture().start()

                f.shouldRefuse<LedgerError.NotFound> { f.service.delete(TransactionId("missing")) }
                f.shouldRefuse<LedgerError.NotFound> { f.service.restore(TransactionId("missing")) }
            }
        }

        context("mark verified (L-6)") {
            test("an unverified transaction becomes verified, audited as a state change") {
                val f = TransactionFixture().start()
                val seeded = f.seed(state = TxnState.UNVERIFIED)
                val before = f.trail.size

                val verified = f.service.markVerified(seeded.transaction.meta.id)

                verified.state shouldBe TxnState.VERIFIED
                f.storedTransactions().single().state shouldBe TxnState.VERIFIED
                val written = f.trail.drop(before)
                written shouldHaveSize 1
                written.single().action shouldBe AuditAction.UPDATE
                written.single().changes shouldBe mapOf("state" to FieldChange("UNVERIFIED", "VERIFIED"))
            }

            test("a verified transaction is left as it is, with no audit entry") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())
                val before = f.trail.size

                val again = f.service.markVerified(saved.transaction.meta.id)

                again shouldBe saved.transaction
                f.storedTransactions() shouldBe listOf(saved.transaction)
                f.trail shouldHaveSize before
            }

            test("an unverified transfer leg is refused: verifying a transfer waits for the M1b captures") {
                val f = TransactionFixture().start()
                val leg = f.seed(type = TransactionType.TRANSFER, state = TxnState.UNVERIFIED)

                f.shouldRefuse<LedgerError.IsTransfer> { f.service.markVerified(leg.transaction.meta.id) }
            }

            test("a deleted transaction cannot be marked verified") {
                val f = TransactionFixture().start()
                val seeded = f.seed()
                f.service.delete(seeded.transaction.meta.id)

                f.shouldRefuse<LedgerError.Deleted> { f.service.markVerified(seeded.transaction.meta.id) }
            }
        }

        context("get") {
            test("an unknown transaction is null") {
                val f = TransactionFixture().start()

                f.service.get(TransactionId("missing")) shouldBe null
            }

            test("returns the live lines by position, then by id, whatever order the store gives") {
                val f = TransactionFixture().start()
                val seeded = f.seed()
                val id = seeded.transaction.meta.id

                // Written so the fake (newest first) returns them in neither position nor id order, and so
                // that id order alone (third's "a-3" sorts before "l-a") would not be position order.
                suspend fun line(
                    lineId: String,
                    position: Int,
                ) = f.kit.writer.create(f.kit.transactionLineRepository, TransactionLineId(lineId)) { meta ->
                    TransactionLine(meta, id, position, Paise(1), null, null, emptySet())
                }
                val third = line("a-3", 3)
                val secondA = line("l-a", 2)
                val secondB = line("l-b", 2)
                val gone = line("l-0", 2)
                f.kit.writer.delete(f.kit.transactionLineRepository, gone.meta.id)

                val ids =
                    f.service
                        .get(id)
                        ?.lines
                        ?.map { it.meta.id }

                ids shouldBe
                    listOf(
                        seeded.lines
                            .single()
                            .meta.id,
                        secondA.meta.id,
                        secondB.meta.id,
                        third.meta.id,
                    )
            }
        }
    })
