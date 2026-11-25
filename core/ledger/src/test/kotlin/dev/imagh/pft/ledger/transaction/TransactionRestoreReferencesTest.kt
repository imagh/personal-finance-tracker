package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Restoring a deleted transaction whose account, category or tag was deleted since (slice C C1d, D4, Q4).
 * Deleting a transaction frees what it used, so the reference may have been tombstoned meanwhile; restore
 * refuses with `Deleted(what)` and changes nothing, like restoring a sub-category under a deleted parent.
 * Archived references are fine: an archived reference keeps its history.
 */
class TransactionRestoreReferencesTest :
    FunSpec({
        /** A started fixture with one expense (account, Food, Travel) that is then deleted. */
        suspend fun deleted(): Pair<TransactionFixture, TransactionWithLines> {
            val f = TransactionFixture().start()
            val saved = f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))
            f.service.delete(saved.transaction.meta.id)
            return f to saved
        }

        context("a reference deleted since") {
            test("the account: Deleted(account), the transaction stays deleted, no audit entry") {
                val (f, saved) = deleted()
                f.kit.accounts.delete(f.account.meta.id)

                val error = f.shouldRefuse<LedgerError.Deleted> { f.service.restore(saved.transaction.meta.id) }

                error.what shouldBe "account"
                f
                    .storedTransactions()
                    .single()
                    .meta.isDeleted shouldBe true
            }

            test("a line's category: Deleted(category)") {
                val (f, saved) = deleted()
                f.kit.categories.delete(f.food.meta.id)

                val error = f.shouldRefuse<LedgerError.Deleted> { f.service.restore(saved.transaction.meta.id) }

                error.what shouldBe "category"
            }

            test("a line's tag: Deleted(tag)") {
                val (f, saved) = deleted()
                f.kit.tags.delete(f.travel.meta.id)

                f.shouldRefuse<LedgerError.Deleted> { f.service.restore(saved.transaction.meta.id) }.what shouldBe "tag"
            }

            test("restoring the reference first lets the transaction come back") {
                val (f, saved) = deleted()
                f.kit.accounts.delete(f.account.meta.id)
                f.kit.tags.delete(f.travel.meta.id)
                f.shouldRefuse<LedgerError.Deleted> { f.service.restore(saved.transaction.meta.id) }

                f.kit.accounts.restore(f.account.meta.id)
                f.shouldRefuse<LedgerError.Deleted> { f.service.restore(saved.transaction.meta.id) }.what shouldBe "tag"
                f.kit.tags.restore(f.travel.meta.id)

                f.service
                    .restore(saved.transaction.meta.id)
                    .meta.isDeleted shouldBe false
                f.trail.shouldHaveAudited("ledger_transaction", saved.transaction.meta.id.value, AuditAction.RESTORE)
            }
        }

        context("a reference that is only archived, or no longer in use by a live line") {
            test("an archived account, category and tag restore fine") {
                val (f, saved) = deleted()
                f.kit.accounts.archive(f.account.meta.id)
                f.kit.categories.archive(f.food.meta.id)
                f.kit.tags.archive(f.travel.meta.id)

                val restored = f.service.restore(saved.transaction.meta.id)

                restored.meta.isDeleted shouldBe false
                f.service.get(saved.transaction.meta.id)?.lines shouldBe saved.lines
            }

            test("a tombstoned line's deleted category does not block: only live lines are checked") {
                val f = TransactionFixture().start()
                val draft = CategoryDraft("Dining", CategoryColour.CORAL)
                val dining = f.kit.categories.create(null, CategoryKind.EXPENSE, draft)
                val saved =
                    f.service.create(
                        f.draft().copy(
                            lines =
                                listOf(
                                    LineDraft(null, Paise(30_000), f.food.meta.id),
                                    LineDraft(null, Paise(20_000), dining.meta.id, setOf(f.travel.meta.id)),
                                ),
                        ),
                    )
                val kept = saved.lines.first { it.categoryId == f.food.meta.id }
                f.service.edit(saved.transaction.meta.id, f.draft(lineId = kept.meta.id))
                f.service.delete(saved.transaction.meta.id)
                f.kit.categories.delete(dining.meta.id)
                f.kit.tags.delete(f.travel.meta.id)

                f.service
                    .restore(saved.transaction.meta.id)
                    .meta.isDeleted shouldBe false
            }

            test("restoring a transaction that is not deleted is still a no-op") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())

                f.service.restore(saved.transaction.meta.id) shouldBe saved.transaction
            }
        }
    })
