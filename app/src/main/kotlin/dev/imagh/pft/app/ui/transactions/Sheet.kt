package dev.imagh.pft.app.ui.transactions

/** Which picker sheet of the transaction form is open; kept across rotation. */
internal enum class Sheet {
    /** None: the form itself is showing. */
    NONE,

    /** The account (a transfer's source). */
    ACCOUNT,

    /** A transfer's destination account. */
    TO_ACCOUNT,

    /** The category. */
    CATEGORY,

    /** The tags. */
    TAGS,
}
