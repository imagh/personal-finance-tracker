package dev.imagh.pft.app.ui.common

/**
 * Why one form field is not acceptable, as the UI states it (never as free text, so a field error
 * cannot carry what the user typed). Rendered by [NameField] from string resources.
 *
 * B1 and later slices add their own cases here when a field kind needs one (amount, date).
 */
sealed interface FieldError {
    /** The field is empty or only whitespace. */
    data object Required : FieldError

    /** The field is longer than [max] characters (after trimming). */
    data class TooLong(
        val max: Int,
    ) : FieldError
}
