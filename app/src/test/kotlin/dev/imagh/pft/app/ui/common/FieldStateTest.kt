package dev.imagh.pft.app.ui.common

import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * [FieldState.check] through core's real [Validation], [FieldState.edit], and the exhaustive
 * [LedgerError.toFieldError] mapping. Pure JVM: no Compose, no Android.
 */
class FieldStateTest {
    private val rule: (String) -> String = { Validation.requireName(it, 100) }

    @Test
    fun `a blank text gives no value and the Required error`() {
        val result = FieldState("  ").check(rule)

        assertNull(result.value)
        assertEquals(FieldError.Required, result.state.error)
        assertEquals("  ", result.state.text)
    }

    @Test
    fun `an over-long text gives no value and TooLong with the limit, keeping the text as typed`() {
        val typed = "a".repeat(101)

        val result = FieldState(typed).check(rule)

        assertNull(result.value)
        assertEquals(FieldError.TooLong(100), result.state.error)
        assertEquals(typed, result.state.text)
    }

    @Test
    fun `a valid text gives the trimmed value and no error, leaving the typed text untouched`() {
        val result = FieldState(" Akash ").check(rule)

        assertEquals("Akash", result.value)
        assertNull(result.state.error)
        assertEquals(" Akash ", result.state.text)
    }

    @Test
    fun `exactly the limit is valid`() {
        assertEquals("a".repeat(100), FieldState("a".repeat(100)).check(rule).value)
    }

    @Test
    fun `editing replaces the text and clears the error`() {
        val errored = FieldState("").check(rule).state
        assertEquals(FieldError.Required, errored.error)

        val edited = errored.edit("A")

        assertEquals("A", edited.text)
        assertNull(edited.error)
    }

    @Test
    fun `a non-field LedgerError from the rule is not swallowed`() {
        val failing: (String) -> String = { throw LedgerError.NotInitialised() }

        assertThrows(LedgerError.NotInitialised::class.java) { FieldState("x").check(failing) }
    }

    @Test
    fun `other exceptions from the rule are not swallowed`() {
        val failing: (String) -> String = { error("boom") }

        assertThrows(IllegalStateException::class.java) { FieldState("x").check(failing) }
    }

    @Test
    fun `BlankName maps to Required`() {
        assertSame(FieldError.Required, LedgerError.BlankName().toFieldError())
    }

    @Test
    fun `NameTooLong maps to TooLong with its limit`() {
        assertEquals(FieldError.TooLong(100), LedgerError.NameTooLong(100).toFieldError())
    }

    @Test
    fun `InvalidMaskedIdentifier maps to InvalidLastFour`() {
        assertSame(FieldError.InvalidLastFour, LedgerError.InvalidMaskedIdentifier().toFieldError())
    }

    @Test
    fun `form-level errors map to null`() {
        val formLevel: List<LedgerError> =
            listOf(
                LedgerError.NotInitialised(),
                LedgerError.IdentityCorrupt(),
                LedgerError.AlreadyInitialised(),
                LedgerError.AmountNotPositive(),
                LedgerError.AmountTooLarge(Paise(1)),
                LedgerError.NotFound("account"),
                LedgerError.Deleted("account"),
                LedgerError.Archived("account"),
                LedgerError.InvalidSettingKey(),
                LedgerError.SettingValueTooLong(10),
            )

        formLevel.forEach { assertNull(it.toFieldError(), it::class.simpleName) }
    }
}
