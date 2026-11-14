package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.NameBasedId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/** One row of plan §4's default table, written out here independently of the production list. */
private data class Row(
    val seedKey: String,
    val kind: CategoryKind,
    val name: String,
    val parent: String?,
    val symbol: String,
    val colour: CategoryColour,
    val systemKey: SystemCategory? = null,
)

private val EXPENSE = CategoryKind.EXPENSE
private val INCOME = CategoryKind.INCOME

/** Plan 2026-11-10 §4, "Default categories", as accepted by the owner (Q4). Seed keys are permanent. */
private val TABLE =
    listOf(
        Row("food", EXPENSE, "Food & dining", null, "restaurant", CategoryColour.CORAL),
        Row("food_groceries", EXPENSE, "Groceries", "food", "grocery", CategoryColour.CORAL),
        Row("food_eating_out", EXPENSE, "Eating out", "food", "local_cafe", CategoryColour.CORAL),
        Row("food_delivery", EXPENSE, "Food delivery", "food", "fastfood", CategoryColour.CORAL),
        Row("transport", EXPENSE, "Transport", null, "directions_car", CategoryColour.SKY),
        Row("transport_fuel", EXPENSE, "Fuel", "transport", "local_gas_station", CategoryColour.SKY),
        Row("transport_cab", EXPENSE, "Cab & auto", "transport", "local_taxi", CategoryColour.SKY),
        Row("transport_public", EXPENSE, "Public transport", "transport", "train", CategoryColour.SKY),
        Row("shopping", EXPENSE, "Shopping", null, "shopping_bag", CategoryColour.MAGENTA),
        Row("bills", EXPENSE, "Bills & utilities", null, "receipt_long", CategoryColour.AMBER),
        Row("bills_electricity", EXPENSE, "Electricity", "bills", "bolt", CategoryColour.AMBER),
        Row("bills_water", EXPENSE, "Water", "bills", "water_drop", CategoryColour.AMBER),
        Row("bills_internet_mobile", EXPENSE, "Internet & mobile", "bills", "wifi", CategoryColour.AMBER),
        Row("bills_subscriptions", EXPENSE, "Subscriptions", "bills", "subscriptions", CategoryColour.AMBER),
        Row("home", EXPENSE, "Home", null, "home", CategoryColour.OLIVE),
        Row("home_rent", EXPENSE, "Rent", "home", "bed", CategoryColour.OLIVE),
        Row("home_maintenance", EXPENSE, "Maintenance & repairs", "home", "handyman", CategoryColour.OLIVE),
        Row("home_help", EXPENSE, "Household help", "home", "cleaning_services", CategoryColour.OLIVE),
        Row("health", EXPENSE, "Health", null, "local_hospital", CategoryColour.ROSE),
        Row("health_medicines", EXPENSE, "Medicines", "health", "medication", CategoryColour.ROSE),
        Row("health_fitness", EXPENSE, "Fitness", "health", "fitness_center", CategoryColour.ROSE),
        Row("entertainment", EXPENSE, "Entertainment", null, "movie", CategoryColour.VIOLET),
        Row("travel", EXPENSE, "Travel", null, "flight", CategoryColour.TEAL),
        Row("education", EXPENSE, "Education", null, "school", CategoryColour.INDIGO),
        Row("personal_care", EXPENSE, "Personal care", null, "spa", CategoryColour.EMERALD),
        Row("gifts", EXPENSE, "Gifts & donations", null, "redeem", CategoryColour.MAGENTA),
        Row("loan_emi", EXPENSE, "Loan EMI", null, "account_balance", CategoryColour.SLATE, SystemCategory.LOAN_EMI),
        Row("income_salary", INCOME, "Salary", null, "work", CategoryColour.EMERALD),
        Row("income_interest", INCOME, "Interest", null, "savings", CategoryColour.TEAL),
        Row("income_refunds", INCOME, "Refunds & cashback", null, "payments", CategoryColour.SKY),
        Row("income_other", INCOME, "Other income", null, "trending_up", CategoryColour.SLATE),
    )

private fun idOf(seedKey: String) = CategoryId(NameBasedId.of("default_category", seedKey))

class DefaultCategoriesTest :
    FunSpec({
        context("the list itself") {
            test("is exactly the default table: names, kinds, parents, icons, colours and the one system category") {
                DefaultCategories.LIST.map {
                    Row(it.seedKey, it.kind, it.name, it.parentSeedKey, it.symbolKey, it.colour, it.systemKey)
                } shouldBe TABLE
                DefaultCategories.LIST shouldHaveSize 31
            }

            test("has unique seed keys, exactly one LOAN_EMI, and no padded names") {
                DefaultCategories.LIST.map { it.seedKey }.toSet() shouldHaveSize DefaultCategories.LIST.size
                DefaultCategories.LIST.filter { it.systemKey != null }.map { it.seedKey to it.systemKey } shouldBe
                    listOf("loan_emi" to SystemCategory.LOAN_EMI)
                DefaultCategories.LIST.forEach { it.name shouldBe it.name.trim() }
            }

            test("every parent exists earlier in the list, is top-level, and has the child's kind") {
                val seen = mutableMapOf<String, DefaultCategory>()
                DefaultCategories.LIST.forEach { entry ->
                    entry.parentSeedKey?.let { parentKey ->
                        val parent = seen[parentKey] ?: error("parent '$parentKey' is not an earlier entry")
                        parent.parentSeedKey shouldBe null
                        parent.kind shouldBe entry.kind
                    }
                    seen[entry.seedKey] = entry
                }
            }

            test("no two siblings share a name, ignoring case") {
                DefaultCategories.LIST
                    .groupBy { it.parentSeedKey to it.kind }
                    .values
                    .forEach { siblings ->
                        siblings.map { CategoryTree.nameKey(it.name) }.toSet() shouldHaveSize siblings.size
                    }
            }

            test("every symbol key is in the icon grammar") {
                DefaultCategories.LIST.forEach { CategoryIcon.Symbol(it.symbolKey).isValid shouldBe true }
            }

            test("the ids derive from the seed keys, and are the same on every device") {
                DefaultCategories.idOf("food_groceries") shouldBe idOf("food_groceries")
                DefaultCategories.LIST.map { DefaultCategories.idOf(it.seedKey) }.toSet() shouldHaveSize 31
            }
        }

        context("seedDefaults") {
            test("creates the whole list with the deterministic ids, attributed to the current user and device") {
                val kit = LedgerTestKit()
                val me = kit.initialise()

                kit.categories.seedDefaults()

                val stored = kit.categoryRepository.all().associateBy { it.meta.id }
                stored.keys shouldBe TABLE.map { idOf(it.seedKey) }.toSet()
                TABLE.forEach { row ->
                    val category = stored.getValue(idOf(row.seedKey))
                    category.name shouldBe row.name
                    category.kind shouldBe row.kind
                    category.parentId shouldBe row.parent?.let(::idOf)
                    category.iconKey shouldBe CategoryIcon.Symbol(row.symbol)
                    category.colour shouldBe row.colour
                    category.systemKey shouldBe row.systemKey
                    category.meta.isActive shouldBe true
                    category.meta.createdByUserId shouldBe me.user.id
                    category.meta.createdOnDeviceId shouldBe me.device.id
                    kit.store.auditLog.entries.shouldHaveAudited(
                        "category",
                        category.meta.id.value,
                        AuditAction.CREATE,
                        userId = me.user.id,
                        deviceId = me.device.id,
                        source = AuditSource.SYSTEM,
                    )
                }
            }

            test("audits exactly one CREATE per default, all from the system and none manual") {
                val kit = LedgerTestKit()
                kit.initialise()

                kit.categories.seedDefaults()

                val audited =
                    kit.store.auditLog.entries
                        .filter { it.entityType == "category" }
                audited shouldHaveSize 31
                audited.all { it.action == AuditAction.CREATE && it.source == AuditSource.SYSTEM } shouldBe true
                audited.map { it.entityId }.toSet() shouldHaveSize 31
            }

            test("sort_order follows the table order within each set of siblings") {
                val kit = LedgerTestKit()
                kit.initialise()

                kit.categories.seedDefaults()

                fun order(
                    parent: String?,
                    kind: CategoryKind,
                ) = kit.categoryRepository
                    .all()
                    .filter { it.parentId == parent?.let(::idOf) && it.kind == kind }
                    .sortedBy { it.sortOrder }
                    .map { it.name }
                order("food", EXPENSE) shouldBe listOf("Groceries", "Eating out", "Food delivery")
                order(null, EXPENSE) shouldBe
                    listOf(
                        "Food & dining",
                        "Transport",
                        "Shopping",
                        "Bills & utilities",
                        "Home",
                        "Health",
                        "Entertainment",
                        "Travel",
                        "Education",
                        "Personal care",
                        "Gifts & donations",
                        "Loan EMI",
                    )
                order(null, INCOME) shouldBe listOf("Salary", "Interest", "Refunds & cashback", "Other income")
                kit.categoryRepository
                    .all()
                    .filter { it.parentId == null && it.kind == EXPENSE }
                    .map { it.sortOrder } shouldBe
                    (0..11).toList()
            }

            test("the seeded tree is what the picker offers, depth-first") {
                val kit = LedgerTestKit()
                kit.initialise()
                kit.categories.seedDefaults()

                val names = kit.categories.pickable(EXPENSE).map { it.name }

                names shouldHaveSize 27
                names.take(6) shouldBe
                    listOf("Food & dining", "Groceries", "Eating out", "Food delivery", "Transport", "Fuel")
                names.last() shouldBe "Loan EMI"
                kit.categories.pickable(INCOME).map { it.name } shouldBe
                    listOf("Salary", "Interest", "Refunds & cashback", "Other income")
            }

            test("a second call creates nothing and audits nothing") {
                val kit = LedgerTestKit()
                kit.initialise()
                kit.categories.seedDefaults()
                val rows = kit.categoryRepository.all()
                val trailSize = kit.store.auditLog.entries.size

                kit.categories.seedDefaults()

                kit.categoryRepository.all() shouldBe rows
                kit.store.auditLog.entries shouldHaveSize trailSize
            }

            test("a default the user deleted is not created again, and edits to the others are kept") {
                val kit = LedgerTestKit()
                kit.initialise()
                kit.categories.seedDefaults()
                kit.categories.delete(idOf("food_groceries"))
                kit.categories.edit(idOf("food"), CategoryDraft("Eating", CategoryColour.BLUE, null))
                kit.categories.archive(idOf("travel"))
                val rows = kit.categoryRepository.all()
                val trailSize = kit.store.auditLog.entries.size

                kit.categories.seedDefaults()

                kit.categoryRepository.all() shouldBe rows
                kit.categoryRepository
                    .all()
                    .single { it.meta.id == idOf("food_groceries") }
                    .meta.isDeleted shouldBe
                    true
                kit.store.auditLog.entries shouldHaveSize trailSize
            }

            test("two devices seed the same ids") {
                val first = LedgerTestKit()
                val second = LedgerTestKit()
                first.initialise()
                second.initialise()

                first.categories.seedDefaults()
                second.categories.seedDefaults()

                first.categoryRepository
                    .all()
                    .map { it.meta.id }
                    .toSet() shouldBe
                    second.categoryRepository
                        .all()
                        .map { it.meta.id }
                        .toSet()
            }

            test("the seeded Loan EMI is found by its system key and cannot be archived or deleted") {
                val kit = LedgerTestKit()
                kit.initialise()
                kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI) shouldBe null

                kit.categories.seedDefaults()

                val emi = kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI)
                emi?.meta?.id shouldBe idOf("loan_emi")
                shouldThrow<LedgerError.SystemCategoryProtected> { kit.categories.archive(idOf("loan_emi")) }
                shouldThrow<LedgerError.SystemCategoryProtected> { kit.categories.delete(idOf("loan_emi")) }
                kit.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI) shouldBe emi
            }

            test("before first-run setup it is refused, writing nothing") {
                val kit = LedgerTestKit()

                shouldThrow<LedgerError.NotInitialised> { kit.categories.seedDefaults() }

                kit.categoryRepository.all().shouldBeEmpty()
                kit.store.auditLog.entries
                    .shouldBeEmpty()
            }

            test("a failing audit append part-way leaves no default behind (INT-4)") {
                val kit = LedgerTestKit()
                kit.initialise()
                val trailSize = kit.store.auditLog.entries.size
                val failing =
                    object : AuditLog {
                        var allowed = 6

                        override suspend fun append(entry: AuditEntry) {
                            check(allowed-- > 0) { "audit store unavailable" }
                            kit.store.auditLog.append(entry)
                        }
                    }
                val broken = CategoryService(kit.store, kit.writerWith(failing), kit.categoryRepository, kit.ids)

                shouldThrow<IllegalStateException> { broken.seedDefaults() }

                kit.categoryRepository.all().shouldBeEmpty()
                kit.store.auditLog.entries shouldHaveSize trailSize

                // Nothing was half-done, so a retry seeds everything.
                kit.categories.seedDefaults()
                kit.categoryRepository.all() shouldHaveSize 31
            }
        }
    })
