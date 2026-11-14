package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.imagh.pft.R

/**
 * Why one form field is not acceptable, as the UI states it (never as free text, so a field error
 * cannot carry what the user typed). Rendered by [message] from string resources, which every field
 * block uses.
 *
 * Later slices add their own cases here when a field kind needs one (amount, date); [message] and
 * `LedgerError.toFieldError()` are exhaustive, so a new case fails compilation until it has both.
 */
sealed interface FieldError {
    /** The field is empty or only whitespace. */
    data object Required : FieldError

    /** The field is longer than [max] characters (after trimming). */
    data class TooLong(
        val max: Int,
    ) : FieldError

    /** The last-four identifier is not exactly four digits. */
    data object InvalidLastFour : FieldError
}

/** The text the user reads for this error, from string resources (never from typed content). */
@Composable
internal fun FieldError.message(): String =
    when (this) {
        FieldError.Required -> stringResource(R.string.field_required)
        is FieldError.TooLong -> stringResource(R.string.field_too_long, max)
        FieldError.InvalidLastFour -> stringResource(R.string.field_invalid_last_four)
    }
