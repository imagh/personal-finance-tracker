package dev.imagh.pft.app.ui.common

import dev.imagh.pft.ledger.validation.LedgerError

/**
 * The state of one text field in a form: what the user has typed and the error to show under it.
 *
 * The [text] is exactly what was typed (never trimmed, clipped or otherwise altered); validation
 * reads it through [check] and the validated value is a separate result. An error clears as soon
 * as the field is edited ([edit]). Plain data that a ViewModel holds inside its UI state, so it
 * survives recomposition and can be tested without Compose.
 *
 * @property text what the user typed.
 * @property error why the text was refused by the last [check], or null.
 */
data class FieldState(
    val text: String = "",
    val error: FieldError? = null,
) {
    /** This field with new [text] and no error: an edit clears the error shown for the old text. */
    fun edit(text: String): FieldState = copy(text = text, error = null)

    /**
     * Validates [text] with [rule], which is one of core's own validators, for example
     * `{ Validation.requireName(it, MAX_NAME_LENGTH) }`, so the field rules are core's rules and
     * not a UI copy of them.
     *
     * @return the validated value and the state to show: on success the value and this state with
     *   no error; when [rule] throws a [LedgerError] that [toFieldError] maps, a null value and this
     *   state with that error.
     * @throws LedgerError if [rule] throws one that is not about a field (it maps to null): that is
     *   a bug in the caller's rule, so it fails loudly rather than being hidden.
     */
    fun <T> check(rule: (String) -> T): FieldCheck<T> =
        try {
            FieldCheck(rule(text), copy(error = null))
        } catch (e: LedgerError) {
            val fieldError = e.toFieldError() ?: throw e
            FieldCheck(null, copy(error = fieldError))
        }
}

/**
 * The outcome of [FieldState.check].
 *
 * @property value the validated value, or null if the text was refused.
 * @property state the field to show afterwards: the same text, with the error when refused.
 */
class FieldCheck<out T>(
    val value: T?,
    val state: FieldState,
)
