package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import kotlin.random.Random

private val at = Instant.parse("2026-11-14T10:00:00Z")

private fun cat(
    id: String,
    name: String = id,
    parent: String? = null,
    kind: CategoryKind = CategoryKind.EXPENSE,
    sortOrder: Int = 0,
    archived: Boolean = false,
) = Category(
    meta =
        EntityMeta(
            id = CategoryId(id),
            createdAt = at,
            updatedAt = at,
            createdByUserId = UserId("u"),
            createdOnDeviceId = DeviceId("d"),
            archivedAt = if (archived) at else null,
        ),
    parentId = parent?.let(::CategoryId),
    kind = kind,
    name = name,
    colour = CategoryColour.SLATE,
    iconKey = null,
    sortOrder = sortOrder,
    systemKey = null,
)

private fun List<Category>.ids() = map { it.meta.id.value }

/**
 * The plain functions the service builds on, tested without a repository: sorting, the picker's
 * depth-first order, and the sibling checks. Their outcome never depends on the order the repository
 * happened to return rows in.
 */
class CategoryTreeTest :
    FunSpec({
        context("siblings") {
            test("are the categories of one parent and kind, by sort_order") {
                val all =
                    listOf(
                        cat("c", sortOrder = 2),
                        cat("a", sortOrder = 0),
                        cat("b", sortOrder = 1),
                        cat("child", parent = "a"),
                        cat("income", kind = CategoryKind.INCOME),
                    )

                CategoryTree.siblings(all, null, CategoryKind.EXPENSE).ids() shouldBe listOf("a", "b", "c")
                CategoryTree.siblings(all, CategoryId("a"), CategoryKind.EXPENSE).ids() shouldBe listOf("child")
                CategoryTree.siblings(all, null, CategoryKind.INCOME).ids() shouldBe listOf("income")
            }

            test("equal sort_order values are ordered by id, whatever order they arrive in") {
                val all = listOf(cat("b", sortOrder = 1), cat("a", sortOrder = 1), cat("c", sortOrder = 1))

                repeat(10) { seed ->
                    CategoryTree.siblings(all.shuffled(Random(seed)), null, CategoryKind.EXPENSE).ids() shouldBe
                        listOf("a", "b", "c")
                }
            }
        }

        context("pickable") {
            val tree =
                listOf(
                    cat("food", parent = null, sortOrder = 0),
                    cat("groceries", parent = "food", sortOrder = 0),
                    cat("eating", parent = "food", sortOrder = 1),
                    cat("home", parent = null, sortOrder = 1),
                    cat("rent", parent = "home", sortOrder = 0),
                    cat("tied-b", parent = "home", sortOrder = 1),
                    cat("tied-a", parent = "home", sortOrder = 1),
                )
            val expected = listOf("food", "groceries", "eating", "home", "rent", "tied-a", "tied-b")

            test("is depth-first: each top-level category, then its own children") {
                CategoryTree.pickable(tree, CategoryKind.EXPENSE).ids() shouldBe expected
            }

            test("never depends on the order the repository returned the rows in") {
                repeat(20) { seed ->
                    CategoryTree.pickable(tree.shuffled(Random(seed)), CategoryKind.EXPENSE).ids() shouldBe expected
                }
            }

            test("leaves out archived categories and the children of an archived parent") {
                val all =
                    listOf(
                        cat("food"),
                        cat("groceries", parent = "food", archived = true),
                        cat("eating", parent = "food", sortOrder = 1),
                        cat("home", sortOrder = 1, archived = true),
                        cat("rent", parent = "home"),
                    )

                CategoryTree.pickable(all, CategoryKind.EXPENSE).ids() shouldBe listOf("food", "eating")
            }

            test("leaves out a child whose parent is not in the list, and one of a different kind than its parent") {
                val all =
                    listOf(
                        cat("food"),
                        cat("orphan", parent = "gone"),
                        cat("mismatch", parent = "food", kind = CategoryKind.INCOME),
                    )

                CategoryTree.pickable(all, CategoryKind.EXPENSE).ids() shouldBe listOf("food")
                CategoryTree.pickable(all, CategoryKind.INCOME).ids() shouldBe emptyList()
            }
        }

        context("sort order") {
            test("the next one is one after the largest, or 0 for the first") {
                CategoryTree.nextSortOrder(emptyList()) shouldBe 0
                CategoryTree.nextSortOrder(listOf(cat("a", sortOrder = 0), cat("b", sortOrder = 5))) shouldBe 6
            }
        }

        context("names") {
            test("the key trims and lower-cases with Unicode rules, independent of the device locale") {
                CategoryTree.nameKey("  Food & Dining ") shouldBe "food & dining"
                CategoryTree.nameKey("ÉCLAIR") shouldBe "éclair"
                // Locale.ROOT: a Turkish default locale would turn "I" into a dotless i.
                CategoryTree.nameKey("INTEREST") shouldBe "interest"
            }

            test("a name is taken by a sibling with the same key, unless that sibling is the one being renamed") {
                val siblings = listOf(cat("a", name = "Food"), cat("b", name = "Home"))

                CategoryTree.isNameTaken(siblings, " hOME ") shouldBe true
                CategoryTree.isNameTaken(siblings, "Other") shouldBe false
                CategoryTree.isNameTaken(siblings, "FOOD", except = CategoryId("a")) shouldBe false
                CategoryTree.isNameTaken(siblings, "FOOD", except = CategoryId("b")) shouldBe true
            }
        }

        context("a reorder's id list") {
            val siblings = listOf(cat("a"), cat("b"), cat("c"))

            test("matches only when it names every sibling exactly once") {
                CategoryTree.isExactly(siblings, listOf("c", "a", "b").map(::CategoryId)) shouldBe true
                CategoryTree.isExactly(siblings, listOf("a", "b").map(::CategoryId)) shouldBe false
                CategoryTree.isExactly(siblings, listOf("a", "b", "c", "d").map(::CategoryId)) shouldBe false
                CategoryTree.isExactly(siblings, listOf("a", "a", "b").map(::CategoryId)) shouldBe false
                CategoryTree.isExactly(siblings, listOf("a", "b", "x").map(::CategoryId)) shouldBe false
                CategoryTree.isExactly(emptyList(), emptyList()) shouldBe true
            }
        }
    })
