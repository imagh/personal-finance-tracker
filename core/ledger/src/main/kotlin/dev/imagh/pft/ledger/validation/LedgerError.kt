package dev.imagh.pft.ledger.validation

import dev.imagh.pft.money.Paise

/**
 * Every way a ledger operation can be refused for a reason the user can fix or the UI can explain.
 *
 * Services throw these instead of returning error codes, and the ViewModels map them (an
 * exhaustive `when`) to field errors or messages. Slices add their own cases here as they need
 * them (`AccountInUse`, `DepthExceeded`, ...), so the whole vocabulary stays in one place.
 *
 * **Messages never contain user content** (names, amounts, text): errors reach logs and the
 * diagnostics screen, which must stay free of financial data (CLAUDE.md §3.7). They carry the
 * kind of thing and the limit only.
 */
sealed class LedgerError(
    message: String,
) : RuntimeException(message) {
    /** A required name or title was empty or only whitespace. */
    class BlankName : LedgerError("A name is required")

    /** A name or title is longer than [max] characters (after trimming). */
    class NameTooLong(
        val max: Int,
    ) : LedgerError("A name may be at most $max characters")

    /** An amount was zero or negative; transaction amounts are always positive. */
    class AmountNotPositive : LedgerError("The amount must be greater than zero")

    /** An amount is above the allowed [max]. */
    class AmountTooLarge(
        val max: Paise,
    ) : LedgerError("The amount is above the allowed maximum")

    /** The referenced record (described by [what], such as `account`) does not exist. */
    class NotFound(
        val what: String,
    ) : LedgerError("No such $what")

    /** The referenced record ([what]) has been deleted. */
    class Deleted(
        val what: String,
    ) : LedgerError("This $what has been deleted")

    /** The referenced record ([what]) is archived and cannot be used for new entries. */
    class Archived(
        val what: String,
    ) : LedgerError("This $what is archived")

    /** First-run setup was attempted on a device that already has an identity. */
    class AlreadyInitialised : LedgerError("This device is already set up")

    /** An operation needs the current user and device, and first-run setup has not happened. */
    class NotInitialised : LedgerError("This device is not set up yet")

    /**
     * The stored identity is inconsistent (the current device or its user is missing). The app
     * fails closed rather than treating the device as new and creating a second identity.
     */
    class IdentityCorrupt : LedgerError("The stored identity is inconsistent")

    /** A setting key is not a lower-case identifier of the allowed length, or is reserved. */
    class InvalidSettingKey : LedgerError("Invalid setting key")

    /** A setting value is longer than [max] characters. */
    class SettingValueTooLong(
        val max: Int,
    ) : LedgerError("A setting value may be at most $max characters")

    /** An account's last-digits identifier is not exactly four digits `0-9`. */
    class InvalidMaskedIdentifier : LedgerError("The identifier must be exactly four digits")

    /** A sub-category was put under a category that is itself a sub-category: two levels only. */
    class DepthExceeded : LedgerError("Categories can only be nested two levels")

    /** A sub-category is not the same kind (expense or income) as its parent. */
    class KindMismatch : LedgerError("A sub-category must be the same kind as its parent")

    /** A category still has sub-categories, so it cannot be deleted yet. */
    class HasChildren : LedgerError("Delete the sub-categories first")

    /** A reorder named a different set of categories than the ones that are there now. */
    class InvalidOrder : LedgerError("The order does not match the current categories")

    /** A category icon is not a symbol key or an emoji in the allowed form. */
    class InvalidIcon : LedgerError("The icon is not valid")

    /**
     * Another record of the same kind already has this name. [what] is the kind of record (such as
     * `category`), **never the name**: the name is user content and must not reach logs.
     */
    class DuplicateName(
        val what: String,
    ) : LedgerError("A $what with this name already exists")

    /** The category is a system category (such as Loan EMI), which can be renamed but not archived or deleted. */
    class SystemCategoryProtected : LedgerError("This category cannot be archived or deleted")
}
