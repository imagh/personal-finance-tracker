package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.matchers.shouldNotHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/** Archive, unarchive, delete and restore of categories, with the rules that guard them (plan D7). */
class CategoryLifecycleTest :
    FunSpec({
        context("archive and unarchive") {
            test("an archived category is hidden but kept, audited, and unarchive reverses it") {
                val f = CategoryFixture()
                val me = f.kit.initialise()
                val food = f.top("Food")

                val archived = f.service.archive(food.meta.id)

                archived.meta.isArchived shouldBe true
                archived.name shouldBe "Food"
                f.stored() shouldBe listOf(archived)
                f.trail.shouldHaveAudited(
                    "category",
                    food.meta.id.value,
                    AuditAction.ARCHIVE,
                    userId = me.user.id,
                    deviceId = me.device.id,
                )

                val back = f.service.unarchive(food.meta.id)

                back.meta.isArchived shouldBe false
                f.trail.shouldHaveAudited("category", food.meta.id.value, AuditAction.UNARCHIVE, userId = me.user.id)
            }

            test("repeating either is a no-op that audits nothing more") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                f.service.unarchive(food.meta.id) // not archived: nothing to do
                f.trail.shouldNotHaveAudited("category", food.meta.id.value, AuditAction.UNARCHIVE)
                f.service.archive(food.meta.id)
                val trailSize = f.trail.size

                f.service.archive(food.meta.id)

                f.trail shouldHaveSize trailSize
            }

            test("archiving a parent leaves its children as they are") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val groceries = f.sub(food, "Groceries")

                f.service.archive(food.meta.id)

                f.stored().single { it.meta.id == groceries.meta.id } shouldBe groceries
            }

            test("the system category cannot be archived") {
                val f = CategoryFixture()
                f.kit.initialise()
                val emi = f.system()

                f.shouldRefuse<LedgerError.SystemCategoryProtected> { f.service.archive(emi.meta.id) }
                f.stored() shouldBe listOf(emi)
            }

            test("an unknown or deleted category cannot be archived") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                f.delete(food)

                f.shouldRefuse<LedgerError.NotFound> { f.service.archive(CategoryId("missing")) }
                f.shouldRefuse<LedgerError.Deleted> { f.service.archive(food.meta.id) }
            }
        }

        context("delete and restore") {
            test("a deleted category is a tombstone, audited, and repeating it is a no-op") {
                val f = CategoryFixture()
                val me = f.kit.initialise()
                val food = f.top("Food")

                val gone = f.service.delete(food.meta.id)

                gone.meta.isDeleted shouldBe true
                f.stored() shouldBe listOf(gone)
                f.trail.shouldHaveAudited(
                    "category",
                    food.meta.id.value,
                    AuditAction.DELETE,
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
                val trailSize = f.trail.size

                f.service.delete(food.meta.id)

                f.trail shouldHaveSize trailSize
            }

            test("restore brings it back with its fields, audited, and not-deleted is a no-op") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                f.service.restore(food.meta.id) // not deleted: nothing to do
                f.trail.shouldNotHaveAudited("category", food.meta.id.value, AuditAction.RESTORE)
                f.service.delete(food.meta.id)

                val restored = f.service.restore(food.meta.id)

                restored.meta.isDeleted shouldBe false
                restored.name shouldBe "Food"
                restored.sortOrder shouldBe food.sortOrder
                f.trail.shouldHaveAudited("category", food.meta.id.value, AuditAction.RESTORE)
            }

            test("a parent with a live child cannot be deleted, an archived child counts, and a deleted one does not") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val groceries = f.sub(food, "Groceries")
                val eatingOut = f.sub(food, "Eating out")
                f.archive(eatingOut)

                f.shouldRefuse<LedgerError.HasChildren> { f.service.delete(food.meta.id) }

                f.service.delete(groceries.meta.id)
                f.shouldRefuse<LedgerError.HasChildren> { f.service.delete(food.meta.id) }

                f.service.delete(eatingOut.meta.id)
                f.service
                    .delete(food.meta.id)
                    .meta.isDeleted shouldBe true
            }

            test("the system category cannot be deleted") {
                val f = CategoryFixture()
                f.kit.initialise()
                val emi = f.system()

                f.shouldRefuse<LedgerError.SystemCategoryProtected> { f.service.delete(emi.meta.id) }
                f.stored() shouldBe listOf(emi)
            }

            test("an unknown category cannot be deleted or restored") {
                val f = CategoryFixture()
                f.kit.initialise()

                f.shouldRefuse<LedgerError.NotFound> { f.service.delete(CategoryId("missing")) }
                f.shouldRefuse<LedgerError.NotFound> { f.service.restore(CategoryId("missing")) }
            }

            test("a child whose parent is deleted cannot be restored until the parent is") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val groceries = f.sub(food, "Groceries")
                f.service.delete(groceries.meta.id)
                f.service.delete(food.meta.id)

                f.shouldRefuse<LedgerError.Deleted> { f.service.restore(groceries.meta.id) }.what shouldBe "category"

                f.service.restore(food.meta.id)
                f.service
                    .restore(groceries.meta.id)
                    .meta.isDeleted shouldBe false
            }

            test("restoring into a name a live sibling now has is refused with DuplicateName") {
                val f = CategoryFixture()
                f.kit.initialise()
                val old = f.top("Food")
                f.service.delete(old.meta.id)
                f.top("food")

                f.shouldRefuse<LedgerError.DuplicateName> { f.service.restore(old.meta.id) }.what shouldBe "category"
            }

            test("a child archived before it was deleted comes back archived, even under an archived parent") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val groceries = f.sub(food, "Groceries")
                f.archive(groceries)
                f.service.delete(groceries.meta.id)
                f.archive(food)

                val restored = f.service.restore(groceries.meta.id)

                restored.meta.isDeleted shouldBe false
                restored.meta.isArchived shouldBe true
            }
        }
    })
