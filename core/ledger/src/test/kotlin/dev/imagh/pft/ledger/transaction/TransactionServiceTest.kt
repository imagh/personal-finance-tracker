package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.NameBasedId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Creating a plain (one-line) transaction and the rules that guard it (slice C C1a; IT §3.1 to §3.3). */
class TransactionServiceTest :
    FunSpec({
        context("create") {
            test("stores the transaction and line 1 for the whole amount, both audited and attributed") {
                val f = TransactionFixture().start()

                val saved = f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))

                val txn = saved.transaction
                val line = saved.lines.single()
                f.storedTransactions() shouldBe listOf(txn)
                f.storedLines() shouldBe listOf(line)
                line.meta.id.value shouldBe NameBasedId.of("transaction-line", txn.meta.id.value, "1")
                line.transactionId shouldBe txn.meta.id
                line.position shouldBe 1
                line.amount shouldBe Paise(50_000)
                line.categoryId shouldBe f.food.meta.id
                line.tagIds shouldBe setOf(f.travel.meta.id)
                listOf(txn.meta, line.meta).forEach {
                    it.createdByUserId shouldBe f.me.user.id
                    it.createdOnDeviceId shouldBe f.me.device.id
                }
                f.trail.shouldHaveAudited(
                    "ledger_transaction",
                    txn.meta.id.value,
                    AuditAction.CREATE,
                    userId = f.me.user.id,
                    deviceId = f.me.device.id,
                    source = AuditSource.MANUAL,
                )
                f.trail.shouldHaveAudited(
                    "transaction_line",
                    line.meta.id.value,
                    AuditAction.CREATE,
                    changes =
                        mapOf(
                            "transaction_id" to FieldChange(null, txn.meta.id.value),
                            "position" to FieldChange(null, "1"),
                            "amount" to FieldChange(null, "50000"),
                            "category_id" to FieldChange(null, f.food.meta.id.value),
                            "tag_ids" to FieldChange(null, f.travel.meta.id.value),
                        ),
                    userId = f.me.user.id,
                    deviceId = f.me.device.id,
                )
            }

            test("line 1's id is pinned: the same transaction id gives the same line id on every device") {
                val f = TransactionFixture(seed = 7).start()

                val saved = f.service.create(f.draft())

                saved.transaction.meta.id.value shouldBe TXN_ID_FOR_SEED_7
                saved.lines
                    .single()
                    .meta.id shouldBe TransactionLineId(LINE_1_ID_FOR_SEED_7)
            }

            test("a manual entry is MANUAL and VERIFIED, and its direction follows its type") {
                val f = TransactionFixture().start()

                val expense = f.service.create(f.draft()).transaction
                val income =
                    f.service
                        .create(
                            f.draft(TransactionType.INCOME, categoryId = f.salary.meta.id),
                        ).transaction

                listOf(expense, income).forEach {
                    it.source shouldBe TxnSource.MANUAL
                    it.state shouldBe TxnState.VERIFIED
                    it.sourceMessageId shouldBe null
                    it.accountFragment shouldBe null
                    it.transferGroupId shouldBe null
                }
                expense.direction shouldBe Direction.OUT
                income.direction shouldBe Direction.IN
            }

            test("stores the payment facts as given, with the texts trimmed") {
                val f = TransactionFixture().start()

                val txn =
                    f.service
                        .create(f.draft(title = "  Grocer ", description = " Weekly shop\n", bankRef = " UTR42 "))
                        .transaction

                txn.type shouldBe TransactionType.EXPENSE
                txn.amount shouldBe Paise(50_000)
                txn.occurredAt shouldBe TransactionFixture.OCCURRED
                txn.accountId shouldBe f.account.meta.id
                txn.title shouldBe "Grocer"
                txn.description shouldBe "Weekly shop"
                f.storedTransactions().single() shouldBe txn
            }

            test("the bank reference is stored and audited (X-1)") {
                val f = TransactionFixture().start()

                val txn = f.service.create(f.draft(bankRef = "UTR42")).transaction

                f.storedTransactions().single().bankRef shouldBe "UTR42"
                f.trail.shouldHaveAudited("ledger_transaction", txn.meta.id.value, AuditAction.CREATE)
                f.trail
                    .single { it.entityId == txn.meta.id.value }
                    .changes["bank_ref"] shouldBe FieldChange(null, "UTR42")
            }

            test("a blank title, description or bank reference is stored as none (Q3)") {
                val f = TransactionFixture().start()

                val txn = f.service.create(f.draft(title = "   ", description = "", bankRef = "\t")).transaction

                txn.title shouldBe null
                txn.description shouldBe null
                txn.bankRef shouldBe null
                f.storedTransactions().single().title shouldBe null
            }

            test("texts of exactly their limits are kept") {
                val f = TransactionFixture().start()
                val draft =
                    f.draft(
                        title = "t".repeat(LedgerLimits.MAX_TITLE_LENGTH),
                        description = "d".repeat(LedgerLimits.MAX_DESCRIPTION_LENGTH),
                        bankRef = "b".repeat(LedgerLimits.MAX_BANK_REF_LENGTH),
                    )

                val txn = f.service.create(draft).transaction

                txn.title?.length shouldBe 200
                txn.description?.length shouldBe 2_000
                txn.bankRef?.length shouldBe 64
            }

            test("a title, description or bank reference over its limit is refused with that limit") {
                val f = TransactionFixture().start()

                f
                    .shouldRefuse<LedgerError.NameTooLong> { f.service.create(f.draft(title = "t".repeat(201))) }
                    .max shouldBe 200
                f
                    .shouldRefuse<LedgerError.NameTooLong> {
                        f.service.create(
                            f.draft(description = "d".repeat(2_001)),
                        )
                    }.max shouldBe 2_000
                f
                    .shouldRefuse<LedgerError.NameTooLong> { f.service.create(f.draft(bankRef = "b".repeat(65))) }
                    .max shouldBe 64
            }
        }

        context("amount") {
            test("a zero amount is refused as such, not as a mismatch with its line") {
                val f = TransactionFixture().start()

                f.shouldRefuse<LedgerError.AmountNotPositive> {
                    f.service.create(
                        f.draft(amount = 0, lineAmount = 100),
                    )
                }
            }

            test("an amount above the limit is refused, even with a line that is within it") {
                val f = TransactionFixture().start()
                val over = LedgerLimits.MAX_AMOUNT.value + 1

                f.shouldRefuse<LedgerError.AmountTooLarge> { f.service.create(f.draft(amount = over, lineAmount = 1)) }
            }

            test("a line that does not match the amount is a mismatch") {
                val f = TransactionFixture().start()

                f.shouldRefuse<LedgerError.BreakdownMismatch> {
                    f.service.create(f.draft(amount = 50_000, lineAmount = 49_999))
                }
            }
        }

        context("references") {
            test("an income category on an expense line is a kind mismatch") {
                val f = TransactionFixture().start()

                f.shouldRefuse<LedgerError.KindMismatch> { f.service.create(f.draft(categoryId = f.salary.meta.id)) }
            }

            test("an expense category on an income line is a kind mismatch") {
                val f = TransactionFixture().start()

                f.shouldRefuse<LedgerError.KindMismatch> {
                    f.service.create(f.draft(TransactionType.INCOME, categoryId = f.food.meta.id))
                }
            }

            test("an uncategorised line is fine") {
                val f = TransactionFixture().start()

                f.service
                    .create(f.draft(categoryId = null))
                    .lines
                    .single()
                    .categoryId shouldBe null
            }

            test("an archived account, category or tag is refused") {
                val f = TransactionFixture().start()
                f.kit.accounts.archive(f.account.meta.id)
                f.kit.categories.archive(f.food.meta.id)
                f.kit.tags.archive(f.travel.meta.id)
                val account = f.kit.accounts.create(AccountDraft("Cash", f.account.type, Paise(0)))
                val food = f.kit.categories.create(null, f.food.kind, CategoryDraft("Dining", f.food.colour))

                f
                    .shouldRefuse<LedgerError.Archived> {
                        f.service.create(f.draft(categoryId = food.meta.id))
                    }.what shouldBe "account"
                f
                    .shouldRefuse<LedgerError.Archived> {
                        f.service.create(f.draft(accountId = account.meta.id))
                    }.what shouldBe "category"
                f
                    .shouldRefuse<LedgerError.Archived> {
                        val tags = setOf(f.travel.meta.id)
                        f.service.create(f.draft(accountId = account.meta.id, categoryId = food.meta.id, tagIds = tags))
                    }.what shouldBe "tag"
            }

            test("a deleted account, category or tag is refused") {
                val f = TransactionFixture().start()
                val account = f.kit.accounts.create(AccountDraft("Cash", f.account.type, Paise(0)))
                val food = f.kit.categories.create(null, f.food.kind, CategoryDraft("Dining", f.food.colour))
                val tag = f.kit.tags.create("Work")
                f.kit.accounts.delete(account.meta.id)
                f.kit.categories.delete(food.meta.id)
                f.kit.tags.delete(tag.meta.id)

                f
                    .shouldRefuse<LedgerError.Deleted> {
                        f.service.create(f.draft(accountId = account.meta.id))
                    }.what shouldBe "account"
                f
                    .shouldRefuse<LedgerError.Deleted> {
                        f.service.create(f.draft(categoryId = food.meta.id))
                    }.what shouldBe "category"
                f
                    .shouldRefuse<LedgerError.Deleted> {
                        f.service.create(f.draft(tagIds = setOf(f.travel.meta.id, tag.meta.id)))
                    }.what shouldBe "tag"
            }

            test("an unknown account is not found") {
                val f = TransactionFixture().start()

                f
                    .shouldRefuse<LedgerError.NotFound> {
                        f.service.create(f.draft(accountId = AccountId("missing")))
                    }.what shouldBe "account"
            }
        }

        context("the shape of the draft") {
            test("a transfer is refused: transfers have their own calls") {
                val f = TransactionFixture().start()

                f.shouldRefuse<LedgerError.IsTransfer> {
                    f.service.create(f.draft(TransactionType.TRANSFER, categoryId = null))
                }
            }

            test("a draft line that names a line id is refused: a new transaction has no lines yet") {
                val f = TransactionFixture().start()

                f
                    .shouldRefuse<LedgerError.NotFound> {
                        f.service.create(f.draft(lineId = TransactionLineId("l-1")))
                    }.what shouldBe "transaction_line"
            }

            test("nothing is written before first-run setup") {
                val f = TransactionFixture().start()
                val unset = TransactionFixture()
                // The account row only, put straight into the store: without an identity nothing else can be.
                unset.kit.accountRepository.insert(f.account)

                unset.shouldRefuse<LedgerError.NotInitialised> {
                    unset.service.create(f.draft())
                }
            }
        }
    }) {
    private companion object {
        /** The first transaction id `Uuid7(TestClock(), Random(7))` hands out. */
        const val TXN_ID_FOR_SEED_7 = "01a1968d-3900-75d8-a377-3417bfc9945a"

        /** `NameBasedId.of("transaction-line", TXN_ID_FOR_SEED_7, "1")`, computed outside the app. */
        const val LINE_1_ID_FOR_SEED_7 = "3f6f07f9-86ee-57a4-b8e7-2685cec36afc"
    }
}
