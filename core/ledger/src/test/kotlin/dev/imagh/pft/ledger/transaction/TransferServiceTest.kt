package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Duration

/** Creating and editing a transfer: two legs, one line each, written together (slice C C2; IT §3.3 rule 2). */
class TransferServiceTest :
    FunSpec({
        /** A started fixture, the default HDFC account as the source and a second bank account as the target. */
        suspend fun started(): Triple<TransactionFixture, Account, Account> {
            val f = TransactionFixture().start()
            return Triple(f, f.account, f.newAccount("SBI"))
        }

        context("create") {
            test("a to b gives two live legs in one group, OUT on a and IN on b, one uncategorised line each") {
                val (f, a, b) = started()
                val before = f.trail.size

                val made =
                    f.transfers.create(
                        f.transferDraft(a.meta.id, b.meta.id, 50_000, tagIds = setOf(f.travel.meta.id)),
                    )

                val out = made.out.transaction
                val into = made.into.transaction
                made.groupId shouldNotBe ""
                out.transferGroupId shouldBe made.groupId
                into.transferGroupId shouldBe made.groupId
                out.meta.id shouldNotBe into.meta.id
                out.type shouldBe TransactionType.TRANSFER
                into.type shouldBe TransactionType.TRANSFER
                out.direction shouldBe Direction.OUT
                out.accountId shouldBe a.meta.id
                into.direction shouldBe Direction.IN
                into.accountId shouldBe b.meta.id
                out.amount shouldBe Paise(50_000)
                into.amount shouldBe Paise(50_000)
                out.occurredAt shouldBe TransactionFixture.OCCURRED
                into.occurredAt shouldBe TransactionFixture.OCCURRED
                listOf(out, into).forEach {
                    it.title shouldBe "Move"
                    it.state shouldBe TxnState.VERIFIED
                    it.source shouldBe TxnSource.MANUAL
                    it.bankRef shouldBe null
                    it.meta.isDeleted shouldBe false
                }
                listOf(made.out, made.into).forEach { leg ->
                    val line = leg.lines.single()
                    line.categoryId shouldBe null
                    line.amount shouldBe Paise(50_000)
                    line.tagIds shouldBe setOf(f.travel.meta.id)
                    line.position shouldBe 1
                }
                f.storedTransactions() shouldContainExactlyInAnyOrder listOf(out, into)
                f.storedLines() shouldContainExactlyInAnyOrder made.out.lines + made.into.lines
                f.kit.transactionRepository.legsOfGroup(made.groupId) shouldContainExactlyInAnyOrder listOf(out, into)
                // Four CREATE entries: two transactions and two lines.
                val written = f.trail.drop(before)
                written shouldHaveSize 4
                written.map { it.action }.toSet() shouldBe setOf(AuditAction.CREATE)
                written.map { it.entityType to it.entityId } shouldContainExactlyInAnyOrder
                    listOf(
                        "ledger_transaction" to out.meta.id.value,
                        "ledger_transaction" to into.meta.id.value,
                        "transaction_line" to
                            made.out.lines
                                .single()
                                .meta.id.value,
                        "transaction_line" to
                            made.into.lines
                                .single()
                                .meta.id.value,
                    )
            }

            test("a card bill payment is a BANK to CREDIT_CARD transfer") {
                val f = TransactionFixture().start()
                val card = f.newAccount("HDFC Card", AccountType.CREDIT_CARD)

                val made = f.transfers.create(f.transferDraft(f.account.meta.id, card.meta.id, 1_200_000))

                f.account.type shouldBe AccountType.BANK
                card.type shouldBe AccountType.CREDIT_CARD
                made.out.transaction.accountId shouldBe f.account.meta.id
                made.out.transaction.direction shouldBe Direction.OUT
                made.into.transaction.accountId shouldBe card.meta.id
                made.into.transaction.direction shouldBe Direction.IN
            }

            test("the title and description are trimmed, blank becomes none, on both legs") {
                val (f, a, b) = started()

                val made =
                    f.transfers.create(
                        f.transferDraft(a.meta.id, b.meta.id, title = "  Rent  ", description = "   "),
                    )

                listOf(made.out, made.into).forEach {
                    it.transaction.title shouldBe "Rent"
                    it.transaction.description shouldBe null
                }
            }

            test("from equal to to is refused with SameAccount and writes nothing") {
                val (f, a, _) = started()

                f.shouldRefuse<LedgerError.SameAccount> { f.transfers.create(f.transferDraft(a.meta.id, a.meta.id)) }
            }

            test("a zero or oversized amount is refused and writes nothing") {
                val (f, a, b) = started()

                f.shouldRefuse<LedgerError.AmountNotPositive> {
                    f.transfers.create(f.transferDraft(a.meta.id, b.meta.id, amount = 0))
                }
                f.shouldRefuse<LedgerError.AmountTooLarge> {
                    f.transfers.create(f.transferDraft(a.meta.id, b.meta.id, amount = Paise.MAX_AMOUNT.value + 1))
                }
            }

            test("an over-long title or description is refused and writes nothing") {
                val (f, a, b) = started()
                val tooLong = "x".repeat(maxOf(LedgerLimits.MAX_TITLE_LENGTH, LedgerLimits.MAX_DESCRIPTION_LENGTH) + 1)

                f.shouldRefuse<LedgerError.NameTooLong> {
                    f.transfers.create(f.transferDraft(a.meta.id, b.meta.id, title = tooLong))
                }
                f.shouldRefuse<LedgerError.NameTooLong> {
                    f.transfers.create(f.transferDraft(a.meta.id, b.meta.id, description = tooLong))
                }
            }

            test("each account must be live: unknown, deleted and archived are refused, nothing is written") {
                val (f, a, b) = started()
                val deleted = f.newAccount("Old")
                val archived = f.newAccount("Dormant")
                f.kit.accounts.delete(deleted.meta.id)
                f.kit.accounts.archive(archived.meta.id)

                f.shouldRefuse<LedgerError.Deleted> {
                    f.transfers.create(f.transferDraft(a.meta.id, deleted.meta.id))
                }
                f.shouldRefuse<LedgerError.Deleted> {
                    f.transfers.create(f.transferDraft(deleted.meta.id, b.meta.id))
                }
                f.shouldRefuse<LedgerError.Archived> {
                    f.transfers.create(f.transferDraft(a.meta.id, archived.meta.id))
                }
                f.shouldRefuse<LedgerError.Archived> {
                    f.transfers.create(f.transferDraft(archived.meta.id, b.meta.id))
                }
                f.shouldRefuse<LedgerError.NotFound> {
                    f.transfers.create(f.transferDraft(a.meta.id, AccountId("missing")))
                }
            }

            test("a tag must be live: deleted and unknown tags are refused, nothing is written") {
                val (f, a, b) = started()
                val gone = f.kit.tags.create("Gone")
                f.kit.tags.delete(gone.meta.id)

                f.shouldRefuse<LedgerError.Deleted> {
                    f.transfers.create(f.transferDraft(a.meta.id, b.meta.id, tagIds = setOf(gone.meta.id)))
                }
                f.shouldRefuse<LedgerError.NotFound> {
                    f.transfers.create(f.transferDraft(a.meta.id, b.meta.id, tagIds = setOf(TagId("missing"))))
                }
            }

            test("a failure on the second leg's line leaves neither leg, no line and no audit entry") {
                val f = TransactionFixture().start()
                val b = f.newAccount("SBI")
                val failing =
                    f.transfersOver(
                        f.kit.transactionRepository,
                        FailingLines(f.kit.transactionLineRepository, failOnInsert = 2),
                    )

                f.shouldRefuse<IllegalStateException> {
                    failing.create(f.transferDraft(f.account.meta.id, b.meta.id, tagIds = setOf(f.travel.meta.id)))
                }

                f.storedTransactions() shouldBe emptyList()
                f.storedLines() shouldBe emptyList()
                // One retry on healthy stores succeeds with no duplicate.
                f.transfers.create(f.transferDraft(f.account.meta.id, b.meta.id))
                f.storedTransactions() shouldHaveSize 2
                f.storedLines() shouldHaveSize 2
            }

            test("a failure on the second leg itself leaves neither leg and no audit entry") {
                val f = TransactionFixture().start()
                val b = f.newAccount("SBI")
                val failing =
                    f.transfersOver(
                        FailingTransactions(f.kit.transactionRepository, failOnInsert = 2),
                        f.kit.transactionLineRepository,
                    )

                f.shouldRefuse<IllegalStateException> { failing.create(f.transferDraft(f.account.meta.id, b.meta.id)) }

                f.storedTransactions() shouldBe emptyList()
            }
        }

        context("edit") {
            /** A saved transfer HDFC to SBI of Rs 500 tagged Travel, and the clock moved on. */
            suspend fun saved(): Pair<TransactionFixture, Transfer> {
                val (f, a, b) = started()
                val made =
                    f.transfers.create(
                        f.transferDraft(a.meta.id, b.meta.id, 50_000, tagIds = setOf(f.travel.meta.id)),
                    )
                f.kit.clock.advance(Duration.ofMinutes(5))
                return f to made
            }

            fun TransactionFixture.draftOf(made: Transfer) =
                transferDraft(
                    made.out.transaction.accountId,
                    made.into.transaction.accountId,
                    made.out.transaction.amount.value,
                    made.out.transaction.title,
                    made.out.transaction.description,
                    made.out.lines
                        .single()
                        .tagIds,
                )

            test("the amount updates both legs and both lines, and the sum holds on each") {
                val (f, made) = saved()
                val before = f.trail.size

                val edited = f.transfers.edit(made.groupId, f.draftOf(made).copy(amount = Paise(75_000)))

                edited.groupId shouldBe made.groupId
                listOf(edited.out, edited.into).forEach { leg ->
                    leg.transaction.amount shouldBe Paise(75_000)
                    leg.lines.single().amount shouldBe Paise(75_000)
                    val stored = f.kit.transactionLineRepository.liveLinesOf(leg.transaction.meta.id)
                    stored.fold(Paise.ZERO) { sum, line -> sum + line.amount } shouldBe leg.transaction.amount
                }
                edited.out.lines
                    .single()
                    .meta.id shouldBe
                    made.out.lines
                        .single()
                        .meta.id
                edited.into.lines
                    .single()
                    .meta.id shouldBe
                    made.into.lines
                        .single()
                        .meta.id
                f.storedLines() shouldContainExactlyInAnyOrder edited.out.lines + edited.into.lines
                val written = f.trail.drop(before)
                written shouldHaveSize 4
                written.map { it.action }.toSet() shouldBe setOf(AuditAction.UPDATE)
                written.single { it.entityId == made.out.transaction.meta.id.value }.changes shouldBe
                    mapOf("amount" to FieldChange("50000", "75000"))
                written
                    .single {
                        it.entityId ==
                            made.into.lines
                                .single()
                                .meta.id.value
                    }.changes shouldBe
                    mapOf("amount" to FieldChange("50000", "75000"))
            }

            test("the date, title and description change on both legs; the group and the directions stay") {
                val (f, made) = saved()
                val later = TransactionFixture.OCCURRED.plusSeconds(3_600)

                val edited =
                    f.transfers.edit(
                        made.groupId,
                        f.draftOf(made).copy(occurredAt = later, title = " Rent ", description = "May"),
                    )

                listOf(edited.out, edited.into).forEach {
                    it.transaction.occurredAt shouldBe later
                    it.transaction.title shouldBe "Rent"
                    it.transaction.description shouldBe "May"
                    it.transaction.transferGroupId shouldBe made.groupId
                }
                edited.out.transaction.direction shouldBe Direction.OUT
                edited.into.transaction.direction shouldBe Direction.IN
            }

            test("new tags are written on both lines") {
                val (f, made) = saved()
                val gift = f.kit.tags.create("Gift")

                val edited = f.transfers.edit(made.groupId, f.draftOf(made).copy(tagIds = setOf(gift.meta.id)))

                edited.out.lines
                    .single()
                    .tagIds shouldBe setOf(gift.meta.id)
                edited.into.lines
                    .single()
                    .tagIds shouldBe setOf(gift.meta.id)
            }

            test("a new account on either side is applied to its own leg, and it must be live") {
                val (f, made) = saved()
                val card = f.newAccount("Card", AccountType.CREDIT_CARD)
                val gone = f.newAccount("Gone")
                f.kit.accounts.delete(gone.meta.id)

                f.shouldRefuse<LedgerError.Deleted> {
                    f.transfers.edit(made.groupId, f.draftOf(made).copy(to = gone.meta.id))
                }
                f.shouldRefuse<LedgerError.Deleted> {
                    f.transfers.edit(made.groupId, f.draftOf(made).copy(from = gone.meta.id))
                }
                val edited = f.transfers.edit(made.groupId, f.draftOf(made).copy(to = card.meta.id))

                edited.out.transaction.accountId shouldBe made.out.transaction.accountId
                edited.into.transaction.accountId shouldBe card.meta.id
            }

            test("from equal to to is refused and writes nothing") {
                val (f, made) = saved()

                f.shouldRefuse<LedgerError.SameAccount> {
                    f.transfers.edit(made.groupId, f.draftOf(made).copy(to = made.out.transaction.accountId))
                }
            }

            test("an invalid amount is refused and writes nothing") {
                val (f, made) = saved()

                f.shouldRefuse<LedgerError.AmountNotPositive> {
                    f.transfers.edit(made.groupId, f.draftOf(made).copy(amount = Paise(0)))
                }
            }

            test("saving it unchanged writes nothing") {
                val (f, made) = saved()
                val before = f.trail.size

                val same = f.transfers.edit(made.groupId, f.draftOf(made))

                same shouldBe made
                f.trail shouldHaveSize before
            }

            test("an unknown group is NotFound and writes nothing") {
                val (f, made) = saved()

                f.shouldRefuse<LedgerError.NotFound> { f.transfers.edit("missing", f.draftOf(made)) }
            }

            test("a deleted transfer cannot be edited, as a deleted transaction cannot") {
                val (f, made) = saved()
                f.service.delete(made.out.transaction.meta.id)

                f.shouldRefuse<LedgerError.Deleted> {
                    f.transfers.edit(made.groupId, f.draftOf(made).copy(amount = Paise(1)))
                }
            }

            test("a failure on the second leg's line leaves both legs and both lines as they were") {
                val (f, made) = saved()
                val failing =
                    f.transfersOver(
                        f.kit.transactionRepository,
                        FailingLines(f.kit.transactionLineRepository, failOnUpdate = 2),
                    )

                f.shouldRefuse<IllegalStateException> {
                    failing.edit(made.groupId, f.draftOf(made).copy(amount = Paise(75_000)))
                }

                f.storedTransactions() shouldContainExactlyInAnyOrder
                    listOf(made.out.transaction, made.into.transaction)
                f.storedLines() shouldContainExactlyInAnyOrder made.out.lines + made.into.lines
                // One retry succeeds.
                f.transfers
                    .edit(made.groupId, f.draftOf(made).copy(amount = Paise(75_000)))
                    .out.transaction.amount shouldBe Paise(75_000)
            }

            test("a leg with two live lines fails closed instead of tombstoning one of them") {
                val (f, made) = saved()
                f.kit.writer.create(f.kit.transactionLineRepository, TransactionLineId(f.kit.ids.next())) { meta ->
                    TransactionLine(meta, made.into.transaction.meta.id, 2, Paise(1), null, null, emptySet())
                }

                f.shouldRefuse<IllegalStateException> {
                    f.transfers.edit(made.groupId, f.draftOf(made).copy(amount = Paise(75_000)))
                }
            }

            test("a group that is not exactly one OUT and one IN leg fails closed") {
                val f = TransactionFixture().start()
                val lone = f.seed(type = TransactionType.TRANSFER)
                val broken =
                    f.kit.writer.update(f.kit.transactionRepository, lone.transaction.meta.id) {
                        it.copy(transferGroupId = "lonely")
                    }
                val b = f.newAccount("SBI")

                f.shouldRefuse<IllegalStateException> {
                    f.transfers.edit("lonely", f.transferDraft(broken.accountId, b.meta.id))
                }
            }
        }
    })
