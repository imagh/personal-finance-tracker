package dev.imagh.pft.app.ui.common

/** Which amount style [MoneyText] uses. The size never changes the tone: an expense is neutral at every size. */
enum class MoneySize {
    /** `amountHero`: the display-font figure on dashboards and detail headers. */
    Hero,

    /** `amountLarge`: the amount on add/edit and detail screens. */
    Large,

    /** `amountRow`: an amount in a list row. */
    Row,
}
