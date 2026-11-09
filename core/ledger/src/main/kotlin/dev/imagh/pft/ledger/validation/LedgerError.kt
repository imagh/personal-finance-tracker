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
}
