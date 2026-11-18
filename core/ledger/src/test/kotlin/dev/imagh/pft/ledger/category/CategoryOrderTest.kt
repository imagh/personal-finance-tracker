package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.matchers.shouldNotHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/** The live siblings' ids in the order the service shows them: `sort_order`, then id (see `CategoryTree.ORDER`). */
private fun CategoryFixture.order(
    parent: Category?,
    kind: CategoryKind = CategoryKind.EXPENSE,
): List<CategoryId> =
    CategoryTree.siblings(stored().filterNot { it.meta.isDeleted }, parent?.meta?.id, kind).map { it.meta.id }

private fun CategoryFixture.sortOrderOf(category: Category): Int =
    stored()
        .single {
            it.meta.id == category.meta.id
        }.sortOrder

/** Reordering siblings and the list a picker offers (plan D7, D8). */
class CategoryOrderTest :
    FunSpec({
        context("reorder") {
            test("the exact sibling set in a new order rewrites sort_order, with one audited UPDATE per moved row") {
                val f = CategoryFixture()
                val me = f.kit.initialise()
                val a = f.top("A")
                val b = f.top("B")
                val c = f.top("C")

                f.service.reorder(null, CategoryKind.EXPENSE, listOf(c.meta.id, a.meta.id, b.meta.id))

                f.order(null) shouldBe listOf(c.meta.id, a.meta.id, b.meta.id)
                listOf(c, a, b).map { f.sortOrderOf(it) } shouldBe listOf(0, 1, 2)
                f.trail.shouldHaveAudited(
                    "category",
                    c.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("sort_order" to FieldChange("2", "0")),
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
                f.trail.shouldHaveAudited(
                    "category",
                    a.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("sort_order" to FieldChange("0", "1")),
                )
                f.trail.shouldHaveAudited(
                    "category",
                    b.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("sort_order" to FieldChange("1", "2")),
                )
            }

            test("a swap writes two rows and leaves the unmoved row alone") {
                val f = CategoryFixture()
                f.kit.initialise()
                val a = f.top("A")
                val b = f.top("B")
                val c = f.top("C")
                val before = f.trail.size

                f.service.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, a.meta.id, c.meta.id))

                f.trail shouldHaveSize before + 2
                f.trail.shouldNotHaveAudited("category", c.meta.id.value, AuditAction.UPDATE)
                f
                    .stored()
                    .single { it.meta.id == c.meta.id }
                    .meta.updatedAt shouldBe c.meta.updatedAt
            }

            test("the existing sort_order values are handed out again, so a gap left by a deleted sibling stays") {
                val f = CategoryFixture()
                f.kit.initialise()
                val a = f.top("A")
                val b = f.top("B")
                val c = f.top("C")
                val d = f.top("D")
                f.service.delete(a.meta.id) // live siblings now have sort_order 1, 2, 3
                val before = f.trail.size

                f.service.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, d.meta.id, c.meta.id))

                listOf(b, d, c).map { f.sortOrderOf(it) } shouldBe listOf(1, 2, 3)
                // b did not move: no UPDATE for it, even though a 0-based renumbering would have changed it.
                f.trail.shouldNotHaveAudited("category", b.meta.id.value, AuditAction.UPDATE)
                f.trail shouldHaveSize before + 2
            }

            test("the order it already has writes nothing and audits nothing, even with a gap in the values") {
                val f = CategoryFixture()
                f.kit.initialise()
                val a = f.top("A")
                val b = f.top("B")
                val c = f.top("C")
                f.service.delete(a.meta.id) // b and c are at 1 and 2
                val rows = f.stored()
                val before = f.trail.size

                f.service.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, c.meta.id))

                f.stored() shouldBe rows
                f.trail shouldHaveSize before
            }

            context("siblings whose sort_order values are equal") {
                /** A, B, C, D where C and D both hold 2: C was deleted, D took the next value, then C was restored. */
                suspend fun CategoryFixture.tied(): List<Category> {
                    val a = top("A")
                    val b = top("B")
                    val c = top("C")
                    service.delete(c.meta.id)
                    val d = top("D")
                    service.restore(c.meta.id)
                    return listOf(a, b, c, d)
                }

                fun CategoryFixture.values(categories: List<Category>) = categories.map { sortOrderOf(it) }

                test("a swap of the tied pair takes effect, with strictly increasing values and one UPDATE") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val tied = f.tied()
                    val a = tied[0]
                    val b = tied[1]
                    val c = tied[2]
                    val d = tied[3]
                    f.sortOrderOf(c) shouldBe f.sortOrderOf(d)
                    val shown = f.service.pickable(CategoryKind.EXPENSE).map { it.meta.id }
                    shown shouldBe listOf(a, b, c, d).map { it.meta.id }
                    val swapped = shown.dropLast(2) + shown.last() + shown[shown.size - 2]
                    val before = f.trail.size

                    f.service.reorder(null, CategoryKind.EXPENSE, swapped)

                    f.service.pickable(CategoryKind.EXPENSE).map { it.meta.id } shouldBe swapped
                    val values = f.values(listOf(a, b, d, c))
                    values.zipWithNext().all { (x, y) -> x < y } shouldBe true
                    // Only the row that needed a new value was written; the others kept theirs.
                    f.trail shouldHaveSize before + 1
                    f.trail.shouldHaveAudited(
                        "category",
                        c.meta.id.value,
                        AuditAction.UPDATE,
                        changes = mapOf("sort_order" to FieldChange("2", "3")),
                    )
                }

                test("re-submitting the order it shows rewrites only the tied row that needs a new value") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val tied = f.tied()
                    val a = tied[0]
                    val b = tied[1]
                    val c = tied[2]
                    val d = tied[3]
                    val shown = f.service.pickable(CategoryKind.EXPENSE).map { it.meta.id }
                    val before = f.trail.size

                    f.service.reorder(null, CategoryKind.EXPENSE, shown)

                    f.service.pickable(CategoryKind.EXPENSE).map { it.meta.id } shouldBe shown
                    f.values(listOf(a, b, c, d)).zipWithNext().all { (x, y) -> x < y } shouldBe true
                    f.trail shouldHaveSize before + 1
                    f.trail.shouldNotHaveAudited("category", a.meta.id.value, AuditAction.UPDATE)
                    f.trail.shouldNotHaveAudited("category", b.meta.id.value, AuditAction.UPDATE)
                    f.trail.shouldNotHaveAudited("category", c.meta.id.value, AuditAction.UPDATE)
                }

                test("values already strictly increasing are never touched, whatever the gaps") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val a = f.top("A")
                    val b = f.top("B")
                    val c = f.top("C")
                    f.service.delete(a.meta.id)
                    val rows = f.stored()

                    f.service.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, c.meta.id))

                    f.stored() shouldBe rows
                }
            }

            test("it reorders one parent's children only, and one kind's top level only") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val home = f.top("Home")
                val salary = f.top("Salary", CategoryKind.INCOME)
                val interest = f.top("Interest", CategoryKind.INCOME)
                val g1 = f.sub(food, "G1")
                val g2 = f.sub(food, "G2")
                val h1 = f.sub(home, "H1")
                val h2 = f.sub(home, "H2")

                f.service.reorder(food.meta.id, CategoryKind.EXPENSE, listOf(g2.meta.id, g1.meta.id))
                f.service.reorder(null, CategoryKind.INCOME, listOf(interest.meta.id, salary.meta.id))

                f.order(food) shouldBe listOf(g2.meta.id, g1.meta.id)
                f.order(null, CategoryKind.INCOME) shouldBe listOf(interest.meta.id, salary.meta.id)
                f.order(home) shouldBe listOf(h1.meta.id, h2.meta.id)
                f.order(null) shouldBe listOf(food.meta.id, home.meta.id)
            }

            test("archived siblings are part of the set") {
                val f = CategoryFixture()
                f.kit.initialise()
                val a = f.top("A")
                val b = f.top("B")
                val c = f.top("C")
                f.archive(b)

                f.service.reorder(null, CategoryKind.EXPENSE, listOf(c.meta.id, b.meta.id, a.meta.id))

                f.order(null) shouldBe listOf(c.meta.id, b.meta.id, a.meta.id)
                f.shouldRefuse<LedgerError.InvalidOrder> {
                    f.service.reorder(null, CategoryKind.EXPENSE, listOf(a.meta.id, c.meta.id))
                }
            }

            context("a set that is not exactly the siblings is refused with InvalidOrder, writing nothing") {
                test("a missing id") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val a = f.top("A")
                    val b = f.top("B")
                    f.top("C")

                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, a.meta.id))
                    }
                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, emptyList())
                    }
                }

                test("an extra id, unknown or from another parent or kind") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val a = f.top("A")
                    val b = f.top("B")
                    val food = f.top("Food")
                    val child = f.sub(food, "Child")
                    val income = f.top("Salary", CategoryKind.INCOME)
                    val all = listOf(a.meta.id, b.meta.id, food.meta.id)

                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, all + CategoryId("unknown"))
                    }
                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, all + child.meta.id)
                    }
                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, all + income.meta.id)
                    }
                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(food.meta.id, CategoryKind.EXPENSE, listOf(child.meta.id, a.meta.id))
                    }
                }

                test("a repeated id, even when the count matches") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val a = f.top("A")
                    val b = f.top("B")
                    val c = f.top("C")

                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, listOf(a.meta.id, a.meta.id, b.meta.id))
                    }
                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, listOf(c.meta.id, c.meta.id, c.meta.id))
                    }
                }

                test("a deleted category's id") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val a = f.top("A")
                    val b = f.top("B")
                    f.service.delete(b.meta.id)

                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(null, CategoryKind.EXPENSE, listOf(b.meta.id, a.meta.id))
                    }
                }

                test("a parent that has no children, given any ids") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    val a = f.top("A")

                    f.shouldRefuse<LedgerError.InvalidOrder> {
                        f.service.reorder(food.meta.id, CategoryKind.EXPENSE, listOf(a.meta.id))
                    }
                }
            }

            test("a failing audit append part-way leaves every sort_order as it was (INT-4)") {
                val f = CategoryFixture()
                f.kit.initialise()
                val a = f.top("A")
                val b = f.top("B")
                val c = f.top("C")
                val rows = f.stored()
                val trailSize = f.trail.size
                // The first UPDATE is audited, the second one's append fails.
                val failing =
                    object : AuditLog {
                        var allowed = 1

                        override suspend fun append(entry: AuditEntry) {
                            check(allowed-- > 0) { "audit store unavailable" }
                            f.kit.store.auditLog
                                .append(entry)
                        }
                    }
                val broken =
                    CategoryService(f.kit.store, f.kit.writerWith(failing), f.kit.categoryRepository, f.kit.ids)

                shouldThrow<IllegalStateException> {
                    broken.reorder(null, CategoryKind.EXPENSE, listOf(c.meta.id, a.meta.id, b.meta.id))
                }

                f.stored() shouldBe rows
                f.trail shouldHaveSize trailSize
            }
        }

        context("pickable") {
            suspend fun CategoryFixture.names(kind: CategoryKind = CategoryKind.EXPENSE) =
                service.pickable(kind).map { it.name }

            test("top-level categories each followed by their children, all in sort_order") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val home = f.top("Home")
                f.sub(home, "Rent")
                f.sub(food, "Groceries")
                f.sub(home, "Repairs")
                f.sub(food, "Eating out")

                f.names() shouldBe listOf("Food", "Groceries", "Eating out", "Home", "Rent", "Repairs")
            }

            test("it follows a reorder, for top-level categories and for children") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val home = f.top("Home")
                val groceries = f.sub(food, "Groceries")
                val eatingOut = f.sub(food, "Eating out")
                f.service.reorder(null, CategoryKind.EXPENSE, listOf(home.meta.id, food.meta.id))
                f.service.reorder(food.meta.id, CategoryKind.EXPENSE, listOf(eatingOut.meta.id, groceries.meta.id))

                f.names() shouldBe listOf("Home", "Food", "Eating out", "Groceries")
            }

            test("only the requested kind") {
                val f = CategoryFixture()
                f.kit.initialise()
                f.top("Food")
                val salary = f.top("Salary", CategoryKind.INCOME)
                f.sub(salary, "Bonus")

                f.names(CategoryKind.EXPENSE) shouldBe listOf("Food")
                f.names(CategoryKind.INCOME) shouldBe listOf("Salary", "Bonus")
            }

            test("an archived category is not offered") {
                val f = CategoryFixture()
                f.kit.initialise()
                f.top("Food")
                val home = f.top("Home")
                val rent = f.sub(home, "Rent")
                f.sub(home, "Repairs")
                f.archive(rent)

                f.names() shouldBe listOf("Food", "Home", "Repairs")
            }

            test("the children of an archived parent are not offered, and come back when it is unarchived") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                f.sub(food, "Groceries")
                val eatingOut = f.sub(food, "Eating out")
                f.archive(eatingOut)
                f.service.archive(food.meta.id)

                f.names().shouldBeEmpty()

                f.service.unarchive(food.meta.id)

                // "Eating out" was archived on its own, so it stays hidden.
                f.names() shouldBe listOf("Food", "Groceries")
            }

            test("a deleted category and the children it could have left are not offered") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val groceries = f.sub(food, "Groceries")
                f.service.delete(groceries.meta.id)
                f.top("Home")

                f.names() shouldBe listOf("Food", "Home")

                f.service.delete(food.meta.id)

                f.names() shouldBe listOf("Home")
            }

            test("nothing is offered before any category exists") {
                val f = CategoryFixture()
                f.kit.initialise()

                f.names().shouldBeEmpty()
            }
        }
    })
