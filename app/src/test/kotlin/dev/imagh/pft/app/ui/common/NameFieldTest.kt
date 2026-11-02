package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `NameField` (plan D7): the label, the error text and error semantics from a [FieldError], and the
 * rule that what the user types is never truncated or altered, however long.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class NameFieldTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var field by mutableStateOf(FieldState())

    private fun showField(initial: FieldState = FieldState()) {
        field = initial
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                NameField(label = "Your name", state = field, onTextChange = { field = field.edit(it) })
            }
        }
        compose.waitForIdle()
    }

    private fun editableText(): String =
        compose
            .onNode(hasSetTextAction())
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.EditableText)
            ?.text
            .orEmpty()

    private fun hasErrorSemantics(): Boolean =
        compose
            .onNode(hasSetTextAction())
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Error) != null

    @Test
    fun `the label is shown`() {
        showField()
        compose.onNodeWithText("Your name").assertIsDisplayed()
    }

    @Test
    fun `typing 120 characters keeps all 120`() {
        showField()
        val typed = "a".repeat(120)

        compose.onNode(hasSetTextAction()).performTextInput(typed)
        compose.waitForIdle()

        assertEquals(typed, editableText())
        assertEquals(typed, field.text)
    }

    @Test
    fun `a very long text is never clipped and surrounding spaces are kept as typed`() {
        showField()
        val typed = "  " + "b".repeat(5_000) + "  "

        compose.onNode(hasSetTextAction()).performTextInput(typed)
        compose.waitForIdle()

        assertEquals(typed, editableText())
        assertEquals(typed, field.text)
    }

    @Test
    fun `with no error there is no supporting text and no error semantics`() {
        showField(FieldState("Akash"))

        compose.onNodeWithText("Required").assertDoesNotExist()
        assertFalse(hasErrorSemantics())
    }

    @Test
    fun `a TooLong error shows the limit and marks the field as in error`() {
        showField(FieldState("a".repeat(101), FieldError.TooLong(100)))

        compose.onNodeWithText("At most 100 characters").assertIsDisplayed()
        assertTrue(hasErrorSemantics())
        assertEquals("a".repeat(101), editableText())
    }

    @Test
    fun `a Required error shows Required and marks the field as in error`() {
        showField(FieldState("", FieldError.Required))

        compose.onNodeWithText("Required").assertIsDisplayed()
        assertTrue(hasErrorSemantics())
    }

    @Test
    fun `editing a field in error clears the error`() {
        showField(FieldState("", FieldError.Required))

        compose.onNode(hasSetTextAction()).performTextInput("A")
        compose.waitForIdle()

        compose.onNodeWithText("Required").assertDoesNotExist()
        assertFalse(hasErrorSemantics())
    }
}
