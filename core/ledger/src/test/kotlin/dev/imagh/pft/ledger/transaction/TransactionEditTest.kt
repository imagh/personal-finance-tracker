package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Duration

/** Editing a plain (one-line) transaction (slice C C1a, D2; IT §3.3 rule 3). */
class TransactionEditTest :
    FunSpec({
        /** A started fixture with one saved expense, and the clock moved on so an edit has a new time. */
        suspend fun saved(): Pair<TransactionFixture, TransactionWithLines> {
            val f = TransactionFixture().start()
            val saved = f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))
            f.kit.clock.advance(Duration.ofMinutes(5))
            return f to saved
        }

        fun TransactionWithLines.line() = lines.single()

        context("the audit entries an edit writes") {
            test("a new title audits exactly the title, and nothing on the line") {
                val (f, saved) = saved()
                val before = f.trail.size

                val edited = f.service.edit(saved.transaction.meta.id, f.draftOf(saved).copy(title = " Market "))

                edited.transaction.title shouldBe "Market"
                val written = f.trail.drop(before)
                written shouldHaveSize 1
                written.single().entityType shouldBe "ledger_transaction"
                written.single().action shouldBe AuditAction.UPDATE
                written.single().changes shouldBe mapOf("title" to FieldChange("Grocer", "Market"))
                f.storedLines() shouldBe saved.lines
            }

            test("a new category audits exactly the line's category, and nothing on the transaction") {
                val (f, saved) = saved()
                val dining = f.kit.categories.create(null, f.food.kind, CategoryDraft("Dining", f.food.colour))
                val before = f.trail.size
                val draft = f.draftOf(saved)

                val edited =
                    f.service.edit(
                        saved.transaction.meta.id,
                        draft.copy(lines = listOf(draft.lines.single().copy(categoryId = dining.meta.id))),
                    )

                edited.line().categoryId shouldBe dining.meta.id
                val written = f.trail.drop(before)
                written shouldHaveSize 1
                written.single().entityType shouldBe "transaction_line"
                written.single().entityId shouldBe
                    saved
                        .line()
                        .meta.id.value
                written.single().changes shouldBe
                    mapOf("category_id" to FieldChange(f.food.meta.id.value, dining.meta.id.value))
                f.storedTransactions() shouldBe listOf(saved.transaction)
            }

            test("a new amount changes the transaction and its line together") {
                val (f, saved) = saved()
                val before = f.trail.size

                val edited =
                    f.service.edit(
                        saved.transaction.meta.id,
                        f.draft(amount = 60_000, lineId = saved.line().meta.id, tagIds = setOf(f.travel.meta.id)),
                    )

                edited.transaction.amount shouldBe Paise(60_000)
                edited.line().amount shouldBe Paise(60_000)
                f.trail.drop(before).associate { it.entityType to it.changes } shouldBe
                    mapOf(
                        "ledger_transaction" to mapOf("amount" to FieldChange("50000", "60000")),
                        "transaction_line" to mapOf("amount" to FieldChange("50000", "60000")),
                    )
            }

            test("a no-op edit writes no audit entry and leaves both rows as they were") {
                val (f, saved) = saved()
                val before = f.trail.size

                val edited = f.service.edit(saved.transaction.meta.id, f.draftOf(saved).copy(title = "  Grocer "))

                edited shouldBe saved
                f.trail shouldHaveSize before
                f.storedTransactions() shouldBe listOf(saved.transaction)
                f.storedLines() shouldBe saved.lines
            }

            test("an auto-captured transaction keeps its state, source and message facts when edited") {
                val f = TransactionFixture().start()
                val seeded = f.seed()

                val edited = f.service.edit(seeded.transaction.meta.id, f.draftOf(seeded).copy(title = "Fuel"))

                edited.transaction shouldBe f.storedTransactions().single()
                edited.transaction.copy(title = null, meta = seeded.transaction.meta) shouldBe seeded.transaction
            }
        }

        context("changing the type") {
            test("expense to income with an income category is allowed") {
                val (f, saved) = saved()
                val draft = f.draftOf(saved)
                val before = f.trail.size

                val edited =
                    f.service.edit(
                        saved.transaction.meta.id,
                        draft.copy(
                            type = TransactionType.INCOME,
                            lines = listOf(draft.lines.single().copy(categoryId = f.salary.meta.id)),
                        ),
                    )

                edited.transaction.type shouldBe TransactionType.INCOME
                edited.transaction.direction shouldBe Direction.IN
                f.trail.drop(before).associate { it.entityType to it.changes } shouldBe
                    mapOf(
                        "ledger_transaction" to
                            mapOf("type" to FieldChange("EXPENSE", "INCOME"), "direction" to FieldChange("OUT", "IN")),
                        "transaction_line" to
                            mapOf("category_id" to FieldChange(f.food.meta.id.value, f.salary.meta.id.value)),
                    )
            }

            test("expense to income keeping the expense category is a kind mismatch") {
                val (f, saved) = saved()

                f.shouldRefuse<LedgerError.KindMismatch> {
                    f.service.edit(saved.transaction.meta.id, f.draftOf(saved).copy(type = TransactionType.INCOME))
                }
            }

            test("the kind check runs even on an unchanged, archived category") {
                val (f, saved) = saved()
                f.kit.categories.archive(f.food.meta.id)

                f.shouldRefuse<LedgerError.KindMismatch> {
                    f.service.edit(saved.transaction.meta.id, f.draftOf(saved).copy(type = TransactionType.INCOME))
                }
            }

            test("to a transfer is refused: delete and re-create it instead") {
                val (f, saved) = saved()
                val draft = f.draftOf(saved)

                f.shouldRefuse<LedgerError.IsTransfer> {
                    f.service.edit(
                        saved.transaction.meta.id,
                        draft.copy(
                            type = TransactionType.TRANSFER,
                            lines = listOf(draft.lines.single().copy(categoryId = null)),
                        ),
                    )
                }
            }

            test("a stored transfer leg is refused: transfers are edited as transfers") {
                val f = TransactionFixture().start()
                val leg = f.seed(type = TransactionType.TRANSFER)
                val draft = f.draftOf(leg)

                f.shouldRefuse<LedgerError.IsTransfer> {
                    f.service.edit(leg.transaction.meta.id, draft.copy(type = TransactionType.EXPENSE, title = "Rent"))
                }
            }
        }

        context("references are checked only where they change (IT §3.3 rule 3)") {
            test("an unchanged archived account, category and tag do not block a title edit") {
                val (f, saved) = saved()
                f.kit.accounts.archive(f.account.meta.id)
                f.kit.categories.archive(f.food.meta.id)
                f.kit.tags.archive(f.travel.meta.id)

                val edited = f.service.edit(saved.transaction.meta.id, f.draftOf(saved).copy(title = "Market"))

                edited.transaction.title shouldBe "Market"
                edited.transaction.accountId shouldBe f.account.meta.id
                edited.line().categoryId shouldBe f.food.meta.id
                edited.line().tagIds shouldBe setOf(f.travel.meta.id)
            }

            test("changing to an archived account is refused") {
                val (f, saved) = saved()
                val cash = f.kit.accounts.create(AccountDraft("Cash", f.account.type, Paise(0)))
                f.kit.accounts.archive(cash.meta.id)

                f
                    .shouldRefuse<LedgerError.Archived> {
                        f.service.edit(saved.transaction.meta.id, f.draftOf(saved).copy(accountId = cash.meta.id))
                    }.what shouldBe "account"
            }

            test("changing to an archived or deleted category is refused") {
                val (f, saved) = saved()
                val dining = f.kit.categories.create(null, f.food.kind, CategoryDraft("Dining", f.food.colour))
                val bills = f.kit.categories.create(null, f.food.kind, CategoryDraft("Bills", f.food.colour))
                f.kit.categories.archive(dining.meta.id)
                f.kit.categories.delete(bills.meta.id)
                val draft = f.draftOf(saved)
                val line = draft.lines.single()

                f
                    .shouldRefuse<LedgerError.Archived> {
                        f.service.edit(
                            saved.transaction.meta.id,
                            draft.copy(lines = listOf(line.copy(categoryId = dining.meta.id))),
                        )
                    }.what shouldBe "category"
                f
                    .shouldRefuse<LedgerError.Deleted> {
                        f.service.edit(
                            saved.transaction.meta.id,
                            draft.copy(lines = listOf(line.copy(categoryId = bills.meta.id))),
                        )
                    }.what shouldBe "category"
            }

            test("adding an archived tag is refused, beside a live tag the line keeps") {
                val (f, saved) = saved()
                val work = f.kit.tags.create("Work")
                f.kit.tags.archive(work.meta.id)
                val draft = f.draftOf(saved)
                val line = draft.lines.single()

                f
                    .shouldRefuse<LedgerError.Archived> {
                        val tags = setOf(f.travel.meta.id, work.meta.id)
                        f.service.edit(saved.transaction.meta.id, draft.copy(lines = listOf(line.copy(tagIds = tags))))
                    }.what shouldBe "tag"
            }

            test("removing a tag needs no check on the tags that stay") {
                val (f, saved) = saved()
                f.kit.tags.archive(f.travel.meta.id)
                val draft = f.draftOf(saved)

                val edited =
                    f.service.edit(
                        saved.transaction.meta.id,
                        draft.copy(lines = listOf(draft.lines.single().copy(tagIds = emptySet()))),
                    )

                edited.line().tagIds shouldBe emptySet()
            }
        }

        context("which transaction and which line") {
            test("a draft line id that is not one of the transaction's live lines is refused") {
                val (f, saved) = saved()
                val other = f.service.create(f.draft(title = "Other"))
                val draft = f.draftOf(saved)
                val line = draft.lines.single()

                f
                    .shouldRefuse<LedgerError.NotFound> {
                        val foreign = other.line().meta.id
                        f.service.edit(saved.transaction.meta.id, draft.copy(lines = listOf(line.copy(id = foreign))))
                    }.what shouldBe "transaction_line"
                f
                    .shouldRefuse<LedgerError.NotFound> {
                        val made = TransactionLineId("l-made-up")
                        f.service.edit(saved.transaction.meta.id, draft.copy(lines = listOf(line.copy(id = made))))
                    }.what shouldBe "transaction_line"
            }

            test("a line that no longer matches the amount is a mismatch, and nothing changes") {
                val (f, saved) = saved()
                val line = saved.line().meta.id

                f.shouldRefuse<LedgerError.BreakdownMismatch> {
                    f.service.edit(
                        saved.transaction.meta.id,
                        f.draft(amount = 60_000, lineAmount = 59_999, lineId = line),
                    )
                }
            }

            test("an unknown or deleted transaction is refused") {
                val (f, saved) = saved()
                val draft = f.draftOf(saved)
                f.service.delete(saved.transaction.meta.id)

                f
                    .shouldRefuse<LedgerError.NotFound> {
                        f.service.edit(TransactionId("missing"), draft)
                    }.what shouldBe "ledger_transaction"
                f
                    .shouldRefuse<LedgerError.Deleted> {
                        f.service.edit(saved.transaction.meta.id, draft.copy(title = "Market"))
                    }.what shouldBe "ledger_transaction"
            }
        }
    })
