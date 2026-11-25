package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionFixture
import dev.imagh.pft.ledger.transaction.shouldRefuseInUse
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * A category that a live line of a live transaction uses cannot be deleted: archive it instead
 * (slice C C1d, D4, Q4; PRD L-2). A deleted transaction keeps its lines as they were, so the
 * count joins to the transaction.
 */
class CategoryInUseTest :
    FunSpec({
        suspend fun TransactionFixture.expense(name: String) =
            kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft(name, CategoryColour.CORAL))

        context("delete") {
            test("is refused while a live line of a live transaction uses the category: no tombstone, no audit entry") {
                val f = TransactionFixture().start()
                f.service.create(f.draft())

                val error = f.shouldRefuseInUse("category") { f.kit.categories.delete(f.food.meta.id) }

                error.message shouldBe "This category is in use by transactions"
            }

            test("is refused for an archived category, and archiving is still allowed while in use") {
                val f = TransactionFixture().start()
                f.service.create(f.draft())

                f.kit.categories
                    .archive(f.food.meta.id)
                    .meta.isArchived shouldBe true

                f.shouldRefuseInUse("category") { f.kit.categories.delete(f.food.meta.id) }
            }

            test("succeeds when only a deleted transaction's line, still live, names the category") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())
                f.service.delete(saved.transaction.meta.id)
                f
                    .storedLines()
                    .single()
                    .meta.isDeleted shouldBe false

                f.kit.categories
                    .delete(f.food.meta.id)
                    .meta.isDeleted shouldBe true

                f.trail.shouldHaveAudited("category", f.food.meta.id.value, AuditAction.DELETE)
            }

            test("succeeds once an edit moved the only line to another category") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())
                val dining = f.expense("Dining")
                val line =
                    saved.lines
                        .single()
                        .meta.id
                f.service.edit(saved.transaction.meta.id, f.draft(categoryId = dining.meta.id, lineId = line))

                f.kit.categories
                    .delete(f.food.meta.id)
                    .meta.isDeleted shouldBe true
                f.shouldRefuseInUse("category") { f.kit.categories.delete(dining.meta.id) }
            }

            test("succeeds once an edit removed the only line that named it (a tombstoned line)") {
                val f = TransactionFixture().start()
                val dining = f.expense("Dining")
                val saved =
                    f.service.create(
                        f.draft().copy(
                            lines =
                                listOf(
                                    LineDraft(null, Paise(30_000), f.food.meta.id),
                                    LineDraft(null, Paise(20_000), dining.meta.id),
                                ),
                        ),
                    )
                f.shouldRefuseInUse("category") { f.kit.categories.delete(dining.meta.id) }
                val kept = saved.lines.first { it.categoryId == f.food.meta.id }
                f.service.edit(saved.transaction.meta.id, f.draft(categoryId = f.food.meta.id, lineId = kept.meta.id))
                f.storedLines().single { it.meta.isDeleted }.categoryId shouldBe dining.meta.id

                f.kit.categories
                    .delete(dining.meta.id)
                    .meta.isDeleted shouldBe true
            }

            test("a parent with sub-categories reports HasChildren, not InUse, even when a line uses it") {
                val f = TransactionFixture().start()
                val snacks = CategoryDraft("Snacks", CategoryColour.CORAL)
                f.kit.categories.create(f.food.meta.id, CategoryKind.EXPENSE, snacks)
                f.service.create(f.draft())

                shouldThrow<LedgerError.HasChildren> { f.kit.categories.delete(f.food.meta.id) }
                f.kit.categories
                    .delete(f.salary.meta.id)
                    .meta.isDeleted shouldBe true
            }
        }
    })
