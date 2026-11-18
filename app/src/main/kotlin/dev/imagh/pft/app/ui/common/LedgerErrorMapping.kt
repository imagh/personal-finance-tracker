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
        -> null
    }
