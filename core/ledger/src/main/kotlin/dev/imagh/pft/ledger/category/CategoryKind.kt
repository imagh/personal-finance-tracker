package dev.imagh.pft.ledger.category

/**
 * Whether a category groups money going out or coming in. Fixed when the category is created (plan
 * D7): a sub-category has the kind of its parent, and the expense and income trees never mix.
 */
enum class CategoryKind {
    /** Money going out. */
    EXPENSE,

    /** Money coming in. */
    INCOME,
}
