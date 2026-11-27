package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Delete, restore, edit and verify on a transfer leg through [TransactionService] (slice C C2, D3): delete and
 * restore act on both legs together, edit and verify are refused (a transfer is edited with
 * [TransferService]; verifying one waits for M1b captures).
 */
class TransferLifecycleTest :
    FunSpec({
        /** A started fixture with a Rs 500 transfer HDFC to SBI tagged Travel. */
        suspend fun made(): Pair<TransactionFixture, Transfer> {
            val f = TransactionFixture().start()
            val b = f.newAccount("SBI")
            val draft = f.transferDraft(f.account.meta.id, b.meta.id, tagIds = setOf(f.travel.meta.id))
            val made = f.transfers.create(draft)
            return f to made
        }

        fun Transfer.legIds(): List<TransactionId> = listOf(out.transaction.meta.id, into.transaction.meta.id)

        fun Transfer.legs() = mapOf("OUT" to out, "IN" to into)

        context("delete") {
            listOf("OUT", "IN").forEach { side ->
                test("deleting the $side leg tombstones both legs and audits a DELETE of each") {
                    val (f, made) = made()
                    val before = f.trail.size
                    val requested =
                        made
                            .legs()
                            .getValue(side)
                            .transaction.meta.id

                    val deleted = f.service.delete(requested)

                    deleted.meta.id shouldBe requested
                    deleted.meta.isDeleted shouldBe true
                    f.storedTransactions().forEach { it.meta.isDeleted shouldBe true }
                    f.storedTransactions() shouldHaveSize 2
                    made.legIds().forEach { f.service.get(it) shouldBe null }
                    f.storedLines() shouldContainExactlyInAnyOrder made.out.lines + made.into.lines
                    val written = f.trail.drop(before)
                    written.map { it.action }.toSet() shouldBe setOf(AuditAction.DELETE)
                    written.map { it.entityType to it.entityId } shouldContainExactlyInAnyOrder
                        made.legIds().map { "ledger_transaction" to it.value }
                }
            }

            test("deleting a transfer that is already deleted, from either leg, writes nothing") {
                val (f, made) = made()
                f.service.delete(made.out.transaction.meta.id)
                val before = f.trail.size
                val stored = f.storedTransactions()

                f.service.delete(made.into.transaction.meta.id)
                f.service.delete(made.out.transaction.meta.id)

                f.trail shouldHaveSize before
                f.storedTransactions() shouldBe stored
            }

            test("a failure on the second leg's tombstone leaves both legs live and no audit entry") {
                val (f, made) = made()
                val failing =
                    f.serviceOver(
                        f.kit.transactionLineRepository,
                        FailingTransactions(f.kit.transactionRepository, failOnUpdate = 2),
                    )

                f.shouldRefuse<IllegalStateException> { failing.delete(made.out.transaction.meta.id) }

                f.storedTransactions().forEach { it.meta.isDeleted shouldBe false }
                // One retry on healthy stores succeeds with no duplicate entry.
                f.service.delete(made.out.transaction.meta.id)
                f.storedTransactions().forEach { it.meta.isDeleted shouldBe true }
            }
        }

        context("a malformed group fails closed and writes nothing") {
            /** A third live leg in the group of [made], a copy of [like], which a transfer never has. */
            suspend fun TransactionFixture.addThirdLeg(like: TransactionWithLines) {
                kit.writer.create(kit.transactionRepository, TransactionId(kit.ids.next())) { meta ->
                    like.transaction.copy(meta = meta)
                }
            }

            listOf("OUT", "IN").forEach { side ->
                test("delete on a group with a second $side leg is IllegalStateException, no leg tombstoned") {
                    val (f, made) = made()
                    f.addThirdLeg(made.legs().getValue(side))

                    made.legIds().forEach { leg ->
                        f.shouldRefuse<IllegalStateException> { f.service.delete(leg) }
                    }
                }
            }

            test("restore on a three-leg group is IllegalStateException and restores no leg") {
                val (f, made) = made()
                f.service.delete(made.out.transaction.meta.id)
                f.addThirdLeg(made.into)

                f.shouldRefuse<IllegalStateException> { f.service.restore(made.out.transaction.meta.id) }
            }

            test("a group whose second row is not a transfer is IllegalStateException on delete") {
                val (f, made) = made()
                f.kit.writer.update(f.kit.transactionRepository, made.into.transaction.meta.id) {
                    it.copy(type = TransactionType.EXPENSE)
                }

                f.shouldRefuse<IllegalStateException> { f.service.delete(made.out.transaction.meta.id) }
            }
        }

        context("restore") {
            listOf("OUT", "IN").forEach { side ->
                test("restoring the $side leg brings both legs back with their lines, auditing a RESTORE of each") {
                    val (f, made) = made()
                    f.service.delete(made.out.transaction.meta.id)
                    val before = f.trail.size
                    val requested =
                        made
                            .legs()
                            .getValue(side)
                            .transaction.meta.id

                    val restored = f.service.restore(requested)

                    restored.meta.id shouldBe requested
                    restored.meta.isDeleted shouldBe false
                    f.storedTransactions().forEach { it.meta.isDeleted shouldBe false }
                    made.legs().values.forEach { leg ->
                        f.service.get(leg.transaction.meta.id)?.lines shouldBe leg.lines
                    }
                    val written = f.trail.drop(before)
                    written.map { it.action }.toSet() shouldBe setOf(AuditAction.RESTORE)
                    written.map { it.entityType to it.entityId } shouldContainExactlyInAnyOrder
                        made.legIds().map { "ledger_transaction" to it.value }
                }
            }

            test("restoring a live transfer writes nothing") {
                val (f, made) = made()
                val before = f.trail.size

                f.service.restore(made.into.transaction.meta.id)

                f.trail shouldHaveSize before
            }

            listOf("from", "to").forEach { side ->
                test("a deleted $side account refuses the whole restore: Deleted(account), neither leg comes back") {
                    val (f, made) = made()
                    f.service.delete(made.out.transaction.meta.id)
                    val leg = if (side == "from") made.out else made.into
                    val account = leg.transaction.accountId
                    f.kit.accounts.delete(account)

                    made.legIds().forEach { leg ->
                        f.shouldRefuse<LedgerError.Deleted> { f.service.restore(leg) }.what shouldBe "account"
                    }

                    f.storedTransactions().forEach { it.meta.isDeleted shouldBe true }
                }
            }

            test("a deleted tag of a leg's line refuses the whole restore: Deleted(tag)") {
                val (f, made) = made()
                f.service.delete(made.out.transaction.meta.id)
                f.kit.tags.delete(f.travel.meta.id)

                f
                    .shouldRefuse<LedgerError.Deleted> {
                        f.service.restore(made.into.transaction.meta.id)
                    }.what shouldBe "tag"
            }

            test("an archived account does not refuse the restore") {
                val (f, made) = made()
                f.service.delete(made.out.transaction.meta.id)
                f.kit.accounts.archive(made.into.transaction.accountId)

                f.service.restore(made.out.transaction.meta.id)

                f.storedTransactions().forEach { it.meta.isDeleted shouldBe false }
            }

            test("a failure on the second leg's restore leaves both legs deleted and no audit entry") {
                val (f, made) = made()
                f.service.delete(made.out.transaction.meta.id)
                val failing =
                    f.serviceOver(
                        f.kit.transactionLineRepository,
                        FailingTransactions(f.kit.transactionRepository, failOnUpdate = 2),
                    )

                f.shouldRefuse<IllegalStateException> { failing.restore(made.out.transaction.meta.id) }

                f.storedTransactions().forEach { it.meta.isDeleted shouldBe true }
                f.service.restore(made.out.transaction.meta.id)
                f.storedTransactions().forEach { it.meta.isDeleted shouldBe false }
            }
        }

        context("edit and verify stay refused on a leg") {
            test("TransactionService.edit on either leg is IsTransfer: transfers are edited as transfers") {
                val (f, made) = made()

                made.legs().values.forEach { leg ->
                    val draft =
                        f.draft(
                            type = TransactionType.EXPENSE,
                            amount = 1_000,
                            accountId = leg.transaction.accountId,
                            categoryId = null,
                        )
                    f.shouldRefuse<LedgerError.IsTransfer> { f.service.edit(leg.transaction.meta.id, draft) }
                }
            }

            test("markVerified on a leg is IsTransfer, though the leg is unverified") {
                val (f, made) = made()
                val unverified =
                    f.kit.writer.update(f.kit.transactionRepository, made.out.transaction.meta.id) {
                        it.copy(state = TxnState.UNVERIFIED)
                    }

                f.shouldRefuse<LedgerError.IsTransfer> { f.service.markVerified(unverified.meta.id) }
            }
        }
    })
