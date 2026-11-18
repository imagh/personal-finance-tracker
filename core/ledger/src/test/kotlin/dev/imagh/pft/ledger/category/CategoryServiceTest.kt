package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Duration

class CategoryServiceTest :
    FunSpec({
        context("create") {
            test("a top-level category stores its trimmed name, colour and icon, audited with who and where") {
                val f = CategoryFixture()
                val me = f.kit.initialise()

                val food =
                    f.service.create(
                        null,
                        CategoryKind.EXPENSE,
                        f.draft(
                            name = "  Food & dining ",
                            colour = CategoryColour.TEAL,
                            icon = CategoryIcon.Emoji("🍔"),
                        ),
                    )

                f.stored() shouldBe listOf(food)
                food.name shouldBe "Food & dining"
                food.kind shouldBe CategoryKind.EXPENSE
                food.parentId shouldBe null
                food.colour shouldBe CategoryColour.TEAL
                food.iconKey shouldBe CategoryIcon.Emoji("🍔")
                food.sortOrder shouldBe 0
                food.systemKey shouldBe null
                food.meta.isActive shouldBe true
                food.meta.createdByUserId shouldBe me.user.id
                food.meta.createdOnDeviceId shouldBe me.device.id
                f.trail.shouldHaveAudited(
                    "category",
                    food.meta.id.value,
                    AuditAction.CREATE,
                    changes =
                        mapOf(
                            "kind" to FieldChange(null, "EXPENSE"),
                            "name" to FieldChange(null, "Food & dining"),
                            "colour" to FieldChange(null, "TEAL"),
                            "icon_key" to FieldChange(null, "emoji:🍔"),
                            "sort_order" to FieldChange(null, "0"),
                        ),
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
            }

            test("a sub-category is stored under its parent with the parent's kind") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")

                val groceries = f.sub(food, "Groceries")

                groceries.parentId shouldBe food.meta.id
                groceries.kind shouldBe CategoryKind.EXPENSE
                f.trail.shouldHaveAudited(
                    "category",
                    groceries.meta.id.value,
                    AuditAction.CREATE,
                    changes =
                        mapOf(
                            "parent_id" to FieldChange(null, food.meta.id.value),
                            "kind" to FieldChange(null, "EXPENSE"),
                            "name" to FieldChange(null, "Groceries"),
                            "colour" to FieldChange(null, "CORAL"),
                            "icon_key" to FieldChange(null, "sym:grocery"),
                            "sort_order" to FieldChange(null, "0"),
                        ),
                )
            }

            test("a category may have no icon") {
                val f = CategoryFixture()
                f.kit.initialise()

                val plain = f.service.create(null, CategoryKind.INCOME, f.draft(name = "Salary", icon = null))

                plain.iconKey shouldBe null
                plain.kind shouldBe CategoryKind.INCOME
            }

            context("hierarchy: two levels, same kind, a live parent") {
                test("a sub-category of a sub-category is refused with DepthExceeded") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    val groceries = f.sub(food, "Groceries")

                    f.shouldRefuse<LedgerError.DepthExceeded> {
                        f.service.create(groceries.meta.id, CategoryKind.EXPENSE, f.draft("Organic"))
                    }
                }

                test("an income sub-category under an expense parent is refused with KindMismatch") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food", CategoryKind.EXPENSE)

                    f.shouldRefuse<LedgerError.KindMismatch> {
                        f.service.create(food.meta.id, CategoryKind.INCOME, f.draft("Refund"))
                    }
                }

                test("an archived parent is refused with Archived") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    f.kit.writer.archive(f.kit.categoryRepository, food.meta.id)

                    f
                        .shouldRefuse<LedgerError.Archived> {
                            f.service.create(food.meta.id, CategoryKind.EXPENSE, f.draft("Groceries"))
                        }.what shouldBe "category"
                }

                test("a deleted parent is refused with Deleted") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    f.kit.writer.delete(f.kit.categoryRepository, food.meta.id)

                    f
                        .shouldRefuse<LedgerError.Deleted> {
                            f.service.create(food.meta.id, CategoryKind.EXPENSE, f.draft("Groceries"))
                        }.what shouldBe "category"
                }

                test("an unknown parent is refused with NotFound") {
                    val f = CategoryFixture()
                    f.kit.initialise()

                    f
                        .shouldRefuse<LedgerError.NotFound> {
                            f.service.create(CategoryId("missing"), CategoryKind.EXPENSE, f.draft("Groceries"))
                        }.what shouldBe "category"
                }
            }

            context("the name") {
                test("a blank name is refused with BlankName") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    f.shouldRefuse<LedgerError.BlankName> {
                        f.service.create(
                            null,
                            CategoryKind.EXPENSE,
                            f.draft("   "),
                        )
                    }
                }

                test("a name over the limit is refused with NameTooLong, and one of exactly the limit is stored") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val max = LedgerLimits.MAX_NAME_LENGTH

                    f
                        .shouldRefuse<LedgerError.NameTooLong> {
                            f.service.create(null, CategoryKind.EXPENSE, f.draft("a".repeat(max + 1)))
                        }.max shouldBe max
                    f.service
                        .create(null, CategoryKind.EXPENSE, f.draft("a".repeat(max)))
                        .name.length shouldBe max
                }

                test("a name already used by a live sibling is refused with DuplicateName, ignoring case and spacing") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    f.sub(food, "Groceries")

                    listOf("Groceries", "groceries", "GROCERIES", "  groceries  ").forEach { name ->
                        f
                            .shouldRefuse<LedgerError.DuplicateName> {
                                f.service.create(food.meta.id, CategoryKind.EXPENSE, f.draft(name))
                            }.what shouldBe "category"
                    }
                }

                test("the comparison is Unicode-aware, not ASCII-only") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    f.top("Éclair")

                    f.shouldRefuse<LedgerError.DuplicateName> {
                        f.service.create(null, CategoryKind.EXPENSE, f.draft("éCLAIR"))
                    }
                }

                test("top-level categories of the same kind are siblings too") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    f.top("Food")

                    f.shouldRefuse<LedgerError.DuplicateName> { f.top("food") }
                }

                test("the same name is fine under another parent, in the other kind, or as a top-level and a sub") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    val home = f.top("Home")
                    f.sub(food, "Other")

                    f.sub(home, "Other")
                    f.top("Food", CategoryKind.INCOME)
                    f.top("Other")

                    f.stored() shouldHaveSize 6
                }

                test("an archived sibling still blocks the name") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    f.kit.writer.archive(f.kit.categoryRepository, food.meta.id)

                    f.shouldRefuse<LedgerError.DuplicateName> { f.top("FOOD") }
                }

                test("a deleted sibling does not block the name") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    f.kit.writer.delete(f.kit.categoryRepository, food.meta.id)

                    f.top("Food").name shouldBe "Food"
                }
            }

            context("the icon") {
                test("an icon outside the grammar is refused with InvalidIcon") {
                    val f = CategoryFixture()
                    f.kit.initialise()

                    listOf(
                        CategoryIcon.Symbol("Grocery"),
                        CategoryIcon.Symbol(""),
                        CategoryIcon.Symbol("a".repeat(41)),
                        CategoryIcon.Emoji("a b"),
                        CategoryIcon.Emoji(""),
                        CategoryIcon.Emoji("😀".repeat(9)),
                    ).forEach { icon ->
                        f.shouldRefuse<LedgerError.InvalidIcon> {
                            f.service.create(null, CategoryKind.EXPENSE, f.draft(icon = icon))
                        }
                    }
                }
            }

            context("the sort order") {
                test("a new category goes last among its live siblings") {
                    val f = CategoryFixture()
                    f.kit.initialise()

                    val order = listOf("A", "B", "C").map { f.top(it).sortOrder }

                    order shouldBe listOf(0, 1, 2)
                }

                test("siblings are counted per parent and per kind") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    val food = f.top("Food")
                    val home = f.top("Home")
                    f.top("Salary", CategoryKind.INCOME).sortOrder shouldBe 0

                    listOf(f.sub(food, "A"), f.sub(food, "B"), f.sub(home, "C")).map { it.sortOrder } shouldBe
                        listOf(0, 1, 0)
                }

                test("a deleted sibling leaves a gap and the next one still goes after the last live one") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    f.top("A")
                    val b = f.top("B")
                    f.top("C")
                    f.kit.writer.delete(f.kit.categoryRepository, b.meta.id)

                    f.top("D").sortOrder shouldBe 3
                }

                test("an archived sibling still counts") {
                    val f = CategoryFixture()
                    f.kit.initialise()
                    f.top("A")
                    val b = f.top("B")
                    f.kit.writer.archive(f.kit.categoryRepository, b.meta.id)

                    f.top("C").sortOrder shouldBe 2
                }
            }
        }

        context("edit") {
            test("audits an UPDATE listing only the fields that changed, and keeps kind, parent and position") {
                val f = CategoryFixture()
                val me = f.kit.initialise()
                val food = f.top("Food")
                f.top("Home")
                val groceries = f.sub(food, "Groceries")
                f.kit.clock.advance(Duration.ofMinutes(5))

                val edited =
                    f.service.edit(
                        groceries.meta.id,
                        f.draft(name = "Supermarket", colour = CategoryColour.OLIVE, icon = CategoryIcon.Emoji("🛒")),
                    )

                edited.name shouldBe "Supermarket"
                edited.colour shouldBe CategoryColour.OLIVE
                edited.iconKey shouldBe CategoryIcon.Emoji("🛒")
                edited.parentId shouldBe food.meta.id
                edited.kind shouldBe CategoryKind.EXPENSE
                edited.sortOrder shouldBe groceries.sortOrder
                edited.meta.createdAt shouldBe groceries.meta.createdAt
                (edited.meta.updatedAt > groceries.meta.updatedAt) shouldBe true
                f.stored().single { it.meta.id == groceries.meta.id } shouldBe edited
                f.trail.shouldHaveAudited(
                    "category",
                    groceries.meta.id.value,
                    AuditAction.UPDATE,
                    changes =
                        mapOf(
                            "name" to FieldChange("Groceries", "Supermarket"),
                            "colour" to FieldChange("CORAL", "OLIVE"),
                            "icon_key" to FieldChange("sym:grocery", "emoji:🛒"),
                        ),
                    userId = me.user.id,
                )
            }

            test("renaming alone audits only the name") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")

                f.service.edit(food.meta.id, f.draft(name = "Dining"))

                f.trail.shouldHaveAudited(
                    "category",
                    food.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("name" to FieldChange("Food", "Dining")),
                )
            }

            test("recolouring alone audits only the colour") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")

                f.service.edit(food.meta.id, f.draft(name = "Food", colour = CategoryColour.INDIGO))

                f.trail.shouldHaveAudited(
                    "category",
                    food.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("colour" to FieldChange("CORAL", "INDIGO")),
                )
            }

            test("the icon can be cleared, and audits only the icon") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")

                val edited = f.service.edit(food.meta.id, f.draft(name = "Food", icon = null))

                edited.iconKey shouldBe null
                f.trail.shouldHaveAudited(
                    "category",
                    food.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("icon_key" to FieldChange("sym:grocery", null)),
                )
            }

            test("an identical edit writes nothing and audits nothing") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val trailSize = f.trail.size

                val same = f.service.edit(food.meta.id, f.draft(name = "Food"))

                same shouldBe food
                f.stored() shouldBe listOf(food)
                f.trail shouldHaveSize trailSize
            }

            test("a name that differs only in case or spacing from its own is accepted") {
                val f = CategoryFixture()
                f.kit.initialise()
                f.top("Other")
                val food = f.top("Food")

                f.service.edit(food.meta.id, f.draft(name = "  FOOD ")).name shouldBe "FOOD"
            }

            test("renaming to a sibling's name is refused with DuplicateName and changes nothing") {
                val f = CategoryFixture()
                f.kit.initialise()
                f.top("Home")
                val food = f.top("Food")

                f
                    .shouldRefuse<LedgerError.DuplicateName> { f.service.edit(food.meta.id, f.draft(name = "hOME")) }
                    .what shouldBe "category"
            }

            test("a sub-category may take a name that another parent's sub-category has") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                val home = f.top("Home")
                f.sub(food, "Other")
                val maintenance = f.sub(home, "Maintenance")

                f.service.edit(maintenance.meta.id, f.draft(name = "Other")).name shouldBe "Other"
            }

            test("an invalid draft is refused and the category is untouched") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")

                f.shouldRefuse<LedgerError.BlankName> { f.service.edit(food.meta.id, f.draft(name = " ")) }
                f.shouldRefuse<LedgerError.NameTooLong> {
                    f.service.edit(food.meta.id, f.draft(name = "a".repeat(LedgerLimits.MAX_NAME_LENGTH + 1)))
                }
                f.shouldRefuse<LedgerError.InvalidIcon> {
                    f.service.edit(food.meta.id, f.draft(name = "Dining", icon = CategoryIcon.Symbol("Bad Key")))
                }
                f.stored() shouldBe listOf(food)
            }

            test("an archived category can still be edited") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                f.archive(food)

                f.service.edit(food.meta.id, f.draft(name = "Dining")).name shouldBe "Dining"
            }

            test("a deleted category cannot be edited, and an unknown id is not found") {
                val f = CategoryFixture()
                f.kit.initialise()
                val food = f.top("Food")
                f.delete(food)

                f
                    .shouldRefuse<LedgerError.Deleted> { f.service.edit(food.meta.id, f.draft(name = "Other")) }
                    .what shouldBe "category"
                f
                    .shouldRefuse<LedgerError.NotFound> { f.service.edit(CategoryId("missing"), f.draft()) }
                    .what shouldBe "category"
            }

            test("the system category can be renamed, recoloured and re-iconed") {
                val f = CategoryFixture()
                f.kit.initialise()
                val emi = f.system()

                val edited =
                    f.service.edit(
                        emi.meta.id,
                        f.draft(name = "EMIs", colour = CategoryColour.BLUE, icon = CategoryIcon.Symbol("payments")),
                    )

                edited.name shouldBe "EMIs"
                edited.colour shouldBe CategoryColour.BLUE
                edited.iconKey shouldBe CategoryIcon.Symbol("payments")
                edited.systemKey shouldBe SystemCategory.LOAN_EMI
            }
        }
    })
