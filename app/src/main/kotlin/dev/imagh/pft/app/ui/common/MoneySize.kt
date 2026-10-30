package dev.imagh.pft.app.ui.common

/** Which amount style [MoneyText] uses; it also decides whether an expense is toned (rows are neutral, Q2). */
enum class MoneySize {
    /** `amountHero`: the display-font figure on dashboards and detail headers. */
    Hero,

    /** `amountLarge`: the amount on add/edit and detail screens. */
    Large,

    /** `amountRow`: an amount in a list row. Expenses are neutral here (Q2). */
    Row,
}
