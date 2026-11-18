package dev.imagh.pft.ledger.account

/** What kind of account it is. Stored by name, so renaming a constant is a migration. */
enum class AccountType {
    /** A bank account (savings, current). */
    BANK,

    /** A credit card; its opening balance is usually negative (money owed). */
    CREDIT_CARD,

    /** Cash in hand. */
    CASH,

    /** A prepaid wallet (a UPI or app wallet). */
    WALLET,
}
