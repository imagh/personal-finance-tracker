package dev.imagh.pft.app.ui.common

import dev.imagh.pft.ledger.validation.LedgerError

/**
 * The [FieldError] to show under a field for this ledger error, or null if the error is not about
 * a field and the form shows it as a form-level message instead (not set up, corrupt identity,
 * unknown record, and so on).
 *
 * The `when` is exhaustive on purpose, with no `else`: a new [LedgerError] case fails compilation
 * here until someone decides whether it belongs to a field.
 */
fun LedgerError.toFieldError(): FieldError? =
    when (this) {
        is LedgerError.BlankName -> FieldError.Required
        is LedgerError.NameTooLong -> FieldError.TooLong(max)
        is LedgerError.InvalidMaskedIdentifier -> FieldError.InvalidLastFour
        is LedgerError.DuplicateName -> FieldError.Duplicate
        is LedgerError.AmountNotPositive,
        is LedgerError.AmountTooLarge,
        is LedgerError.NotFound,
        is LedgerError.Deleted,
        is LedgerError.Archived,
        is LedgerError.AlreadyInitialised,
        is LedgerError.NotInitialised,
        is LedgerError.IdentityCorrupt,
        is LedgerError.InvalidSettingKey,
        is LedgerError.SettingValueTooLong,
        // Category rules that are not about one typed field: the screen shows them as a form-level message.
        is LedgerError.DepthExceeded,
        is LedgerError.KindMismatch,
        is LedgerError.HasChildren,
        is LedgerError.InvalidOrder,
        is LedgerError.InvalidIcon,
        is LedgerError.SystemCategoryProtected,
        // Delete was refused because transactions use it: a list notice, never a field of a form.
        is LedgerError.InUse,
        // Transaction rules about the items as a whole, not one typed field: a form-level message.
        is LedgerError.BreakdownMismatch,
        is LedgerError.TooManyLines,
        // Not about anything the user typed: a doubled item id, or a transfer sent to the plain
        // transaction calls. No field to mark.
        is LedgerError.DuplicateLine,
        is LedgerError.IsTransfer,
        // A transfer's own rules: the same account on both sides, or a category or items on a transfer.
        // The transfer form (C4b) offers neither a category nor items, so these are form-level, not a field.
        is LedgerError.SameAccount,
        is LedgerError.TransferCannotBeItemised,
        -> null
    }
