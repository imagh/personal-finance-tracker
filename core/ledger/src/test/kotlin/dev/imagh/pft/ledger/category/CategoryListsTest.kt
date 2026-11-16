package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import kotlin.random.Random

private val at = Instant.parse("2026-11-15T10:00:00Z")

private fun cat(
    id: String,
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
    name = id,
    colour = CategoryColour.SLATE,
    iconKey = null,
    sortOrder = sortOrder,
    systemKey = null,
)

private fun List<Category>.ids() = map { it.meta.id.value }

/**
 * What a categories list screen shows (plan D11): the live categories of one kind, split into the
 * ones in use and the archived ones, each depth-first. The pure rule is tested on [CategoryTree],
 * the service call over the fakes.
 */
class CategoryListsTest :
    FunSpec({
        context("the split") {
            val tree =
                listOf(
                    cat("food", sortOrder = 0),
                    cat("groceries", parent = "food", sortOrder = 0),
                    cat("eating", parent = "food", sortOrder = 1),
                    cat("home", sortOrder = 1),
                    cat("rent", parent = "home", sortOrder = 0),
                    cat("tied-b", parent = "home", sortOrder = 1),
                    cat("tied-a", parent = "home", sortOrder = 1),
                    cat("salary", kind = CategoryKind.INCOME),
                )

            test("active is depth-first, each top-level category then its own children, and only the asked kind") {
                val lists = CategoryTree.lists(tree, CategoryKind.EXPENSE)

                lists.active.ids() shouldBe listOf("food", "groceries", "eating", "home", "rent", "tied-a", "tied-b")
                lists.archived.ids() shouldBe emptyList()
                CategoryTree.lists(tree, CategoryKind.INCOME).active.ids() shouldBe listOf("salary")
            }

            test("it never depends on the order the repository returned the rows in") {
                val expected = CategoryTree.lists(tree, CategoryKind.EXPENSE)

                repeat(20) { seed ->
                    CategoryTree.lists(tree.shuffled(Random(seed)), CategoryKind.EXPENSE) shouldBe expected
                }
            }

            test("an archived category is archived, and so is every child of an archived parent") {
                val all =
                    listOf(
                        cat("food"),
                        cat("groceries", parent = "food", archived = true),
                        cat("eating", parent = "food", sortOrder = 1),
                        cat("home", sortOrder = 1, archived = true),
                        cat("rent", parent = "home"),
                        cat("repairs", parent = "home", sortOrder = 1, archived = true),
                        cat("shopping", sortOrder = 2),
                    )

                val lists = CategoryTree.lists(all, CategoryKind.EXPENSE)

                lists.active.ids() shouldBe listOf("food", "eating", "shopping")
                lists.archived.ids() shouldBe listOf("groceries", "home", "rent", "repairs")
            }

            test("a child of a parent that is not in the list, or of another kind, is in neither list") {
                val all =
                    listOf(
                        cat("food"),
                        cat("orphan", parent = "gone"),
                        cat("mismatch", parent = "food", kind = CategoryKind.INCOME),
                    )

                CategoryTree.lists(all, CategoryKind.EXPENSE) shouldBe CategoryLists(listOf(cat("food")), emptyList())
                CategoryTree.lists(all, CategoryKind.INCOME) shouldBe CategoryLists(emptyList(), emptyList())
            }
        }

        context("siblings of a parent") {
            val lists =
                CategoryLists(
                    active =
                        listOf(
                            cat("food"),
                            cat("b", parent = "food", sortOrder = 1),
                            cat("shopping", sortOrder = 2),
                        ),
                    archived =
                        listOf(
                            cat("a", parent = "food", sortOrder = 1, archived = true),
                            cat("c", parent = "food", sortOrder = 3, archived = true),
                            cat("home", sortOrder = 1, archived = true),
                            cat("rent", parent = "home"),
                        ),
                )

            test("they are the live categories of both lists under that parent, in sort_order then id") {
                lists.siblingsOf(CategoryId("food")).ids() shouldBe listOf("a", "b", "c")
                lists.siblingsOf(CategoryId("home")).ids() shouldBe listOf("rent")
            }

            test("the top level is the categories with no parent, archived ones included") {
                lists.siblingsOf(null).ids() shouldBe listOf("food", "home", "shopping")
            }

            test("a parent with no children has no siblings") {
                lists.siblingsOf(CategoryId("shopping")) shouldBe emptyList()
            }
        }

        context("the service call") {
            test("it lists one kind with archived ones apart, tombstones left out, and follows a reorder") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val home = f.top("Home")
                val shopping = f.top("Shopping")
                val groceries = f.sub(food, "Groceries")
                f.sub(food, "Eating out")
                f.service.create(null, CategoryKind.INCOME, f.draft("Salary"))
                f.service.archive(shopping.meta.id)
                f.service.archive(groceries.meta.id)
                f.service.delete(home.meta.id)
                f.service.reorder(null, CategoryKind.EXPENSE, listOf(shopping.meta.id, food.meta.id))

                val lists = f.service.lists(CategoryKind.EXPENSE)

                lists.active.map { it.name } shouldBe listOf("Food", "Eating out")
                lists.archived.map { it.name } shouldBe listOf("Shopping", "Groceries")
                f.service
                    .lists(CategoryKind.INCOME)
                    .active
                    .map { it.name } shouldBe listOf("Salary")
            }
        }
    })
