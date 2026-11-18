package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.NameBasedId

/**
 * One category in the starter set that first-run setup creates.
 *
 * @param seedKey a permanent, unique key. It feeds the category's id ([DefaultCategories.idOf]), so
 *   **it must never change** once released: changing it would make a second copy of the category on
 *   every device that seeds later. (Names are editable by the user afterwards; seed keys are not.)
 * @param kind expense or income.
 * @param name the display name the category starts with.
 * @param parentSeedKey the [seedKey] of the top-level category this one sits under, or null.
 * @param symbolKey the key of the symbol it starts with (a permanent key of the app's symbol registry).
 * @param colour the colour it starts with.
 * @param systemKey which system category this is, or null.
 */
data class DefaultCategory(
    val seedKey: String,
    val kind: CategoryKind,
    val name: String,
    val parentSeedKey: String?,
    val symbolKey: String,
    val colour: CategoryColour,
    val systemKey: SystemCategory? = null,
)

/**
 * The starter set of categories (plan B3, accepted by the owner as a starting point: the user creates
 * their own through [CategoryService.create], and renames, archives or deletes these freely, except the
 * Loan EMI system category, which can be edited but not archived or deleted).
 * [CategoryService.seedDefaults] creates them once, during first-run setup.
 *
 * Parents come **before** their children in [LIST], and the order within each set of siblings is the
 * order the categories are shown in (it becomes their `sort_order`).
 */
object DefaultCategories {
    /** The 31 default categories: 27 expense (including the Loan EMI system category) and 4 income. */
    val LIST: List<DefaultCategory> =
        listOf(
            expense("food", "Food & dining", null, "restaurant", CategoryColour.CORAL),
            expense("food_groceries", "Groceries", "food", "grocery", CategoryColour.CORAL),
            expense("food_eating_out", "Eating out", "food", "local_cafe", CategoryColour.CORAL),
            expense("food_delivery", "Food delivery", "food", "fastfood", CategoryColour.CORAL),
            expense("transport", "Transport", null, "directions_car", CategoryColour.SKY),
            expense("transport_fuel", "Fuel", "transport", "local_gas_station", CategoryColour.SKY),
            expense("transport_cab", "Cab & auto", "transport", "local_taxi", CategoryColour.SKY),
            expense("transport_public", "Public transport", "transport", "train", CategoryColour.SKY),
            expense("shopping", "Shopping", null, "shopping_bag", CategoryColour.MAGENTA),
            expense("bills", "Bills & utilities", null, "receipt_long", CategoryColour.AMBER),
            expense("bills_electricity", "Electricity", "bills", "bolt", CategoryColour.AMBER),
            expense("bills_water", "Water", "bills", "water_drop", CategoryColour.AMBER),
            expense("bills_internet_mobile", "Internet & mobile", "bills", "wifi", CategoryColour.AMBER),
            expense("bills_subscriptions", "Subscriptions", "bills", "subscriptions", CategoryColour.AMBER),
            expense("home", "Home", null, "home", CategoryColour.OLIVE),
            expense("home_rent", "Rent", "home", "bed", CategoryColour.OLIVE),
            expense("home_maintenance", "Maintenance & repairs", "home", "handyman", CategoryColour.OLIVE),
            expense("home_help", "Household help", "home", "cleaning_services", CategoryColour.OLIVE),
            expense("health", "Health", null, "local_hospital", CategoryColour.ROSE),
            expense("health_medicines", "Medicines", "health", "medication", CategoryColour.ROSE),
            expense("health_fitness", "Fitness", "health", "fitness_center", CategoryColour.ROSE),
            expense("entertainment", "Entertainment", null, "movie", CategoryColour.VIOLET),
            expense("travel", "Travel", null, "flight", CategoryColour.TEAL),
            expense("education", "Education", null, "school", CategoryColour.INDIGO),
            expense("personal_care", "Personal care", null, "spa", CategoryColour.EMERALD),
            expense("gifts", "Gifts & donations", null, "redeem", CategoryColour.MAGENTA),
            expense("loan_emi", "Loan EMI", null, "account_balance", CategoryColour.SLATE, SystemCategory.LOAN_EMI),
            income("income_salary", "Salary", "work", CategoryColour.EMERALD),
            income("income_interest", "Interest", "savings", CategoryColour.TEAL),
            income("income_refunds", "Refunds & cashback", "payments", CategoryColour.SKY),
            income("income_other", "Other income", "trending_up", CategoryColour.SLATE),
        )

    /**
     * The id of the default category with [seedKey]: the same on every device, so synced devices never
     * see two copies.
     */
    fun idOf(seedKey: String): CategoryId = CategoryId(NameBasedId.of("default_category", seedKey))

    private fun expense(
        seedKey: String,
        name: String,
        parentSeedKey: String?,
        symbolKey: String,
        colour: CategoryColour,
        systemKey: SystemCategory? = null,
    ) = DefaultCategory(seedKey, CategoryKind.EXPENSE, name, parentSeedKey, symbolKey, colour, systemKey)

    private fun income(
        seedKey: String,
        name: String,
        symbolKey: String,
        colour: CategoryColour,
    ) = DefaultCategory(seedKey, CategoryKind.INCOME, name, null, symbolKey, colour)
}
