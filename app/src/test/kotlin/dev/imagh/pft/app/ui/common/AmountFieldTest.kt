package dev.imagh.pft.app.ui.common

import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `AmountField` and `FieldState.checkAmount` (plan B2b): what the amount rule accepts and gives, what
 * the field shows for an error, that the typed text is never altered, and that the keyboard is a
 * decimal one. The amount is a magnitude only: a sign is never part of the text.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AmountFieldTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var field by mutableStateOf(FieldState())
    private var view: View? = null

    private fun showAmount(initial: FieldState = FieldState()) {
        field = initial
        compose.setContent {
            view = LocalView.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                AmountField(label = "Current balance", state = field, onTextChange = { field = field.edit(it) })
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

    /** The input type the focused field asks the keyboard for: what the platform reads when the IME starts. */
    private fun requestedInputType(): Int {
        compose.onNode(hasSetTextAction()).performClick()
        compose.waitForIdle()
        val info = EditorInfo()
        compose.runOnUiThread { checkNotNull(view).onCreateInputConnection(info) }
        return info.inputType
    }

    // --- The rule: FieldState.checkAmount

    @Test
    fun `Indian grouped text gives its paise`() {
        FieldState("1,23,456.78").checkAmount().value shouldBe Paise(12_345_678)
    }

    @Test
    fun `whole rupees and surrounding spaces are accepted, and the text is not altered`() {
        val result = FieldState("  5,000 ").checkAmount()

        result.value shouldBe Paise(500_000)
        result.state.text shouldBe "  5,000 "
        result.state.error shouldBe null
    }

    @Test
    fun `zero is a valid amount`() {
        FieldState("0").checkAmount().value shouldBe Paise.ZERO
    }

    @Test
    fun `a sign, three decimals, letters and a stray comma are InvalidAmount and give nothing`() {
        listOf("-5", "+5", "1.234", "abc", "12,34", "5 000", "1e5", "₹").forEach { typed ->
            val result = FieldState(typed).checkAmount()

            result.value shouldBe null
            result.state.error shouldBe FieldError.InvalidAmount
            result.state.text shouldBe typed
        }
    }

    @Test
    fun `an amount above the limit is InvalidAmount`() {
        // Rs 1e11 is the limit; one paisa above it is refused.
        FieldState("1,00,00,00,00,000.01").checkAmount().state.error shouldBe FieldError.InvalidAmount
        FieldState("1,00,00,00,00,000").checkAmount().value shouldBe Paise.MAX_AMOUNT
    }

    @Test
    fun `an empty or blank field is Required, not InvalidAmount`() {
        listOf("", "   ").forEach { typed ->
            val result = FieldState(typed).checkAmount()

            result.value shouldBe null
            result.state.error shouldBe FieldError.Required
        }
    }

    @Test
    fun `a previous error is cleared when the text is now valid`() {
        val result = FieldState("5", FieldError.InvalidAmount).checkAmount()

        result.value shouldBe Paise(500)
        result.state.error shouldBe null
    }

    // --- The field

    @Test
    fun `the label is shown with the rupee sign`() {
        showAmount(FieldState("5,000"))

        compose.onNodeWithText("Current balance").assertIsDisplayed()
        compose.onNodeWithText("₹").assertIsDisplayed()
    }

    @Test
    fun `an InvalidAmount error says to enter a valid amount and marks the field in error`() {
        showAmount(FieldState("-5", FieldError.InvalidAmount))

        compose.onNodeWithText("Enter a valid amount").assertIsDisplayed()
        hasErrorSemantics() shouldBe true
        editableText() shouldBe "-5"
    }

    @Test
    fun `a Required error shows Required`() {
        showAmount(FieldState("", FieldError.Required))

        compose.onNodeWithText("Required").assertIsDisplayed()
        hasErrorSemantics() shouldBe true
    }

    @Test
    fun `what the user types is kept exactly, even a sign or text the rule will refuse`() {
        showAmount()
        val typed = " -1,2345.678abc "

        compose.onNode(hasSetTextAction()).performTextInput(typed)
        compose.waitForIdle()

        editableText() shouldBe typed
        field.text shouldBe typed
        field.error shouldBe null
    }

    @Test
    fun `editing a field in error clears the error`() {
        showAmount(FieldState("x", FieldError.InvalidAmount))

        compose.onNode(hasSetTextAction()).performTextInput("1")
        compose.waitForIdle()

        compose.onNodeWithText("Enter a valid amount").assertDoesNotExist()
        hasErrorSemantics() shouldBe false
    }

    @Test
    fun `the keyboard is a decimal number keyboard`() {
        showAmount()

        val type = requestedInputType()

        (type and InputType.TYPE_MASK_CLASS) shouldBe InputType.TYPE_CLASS_NUMBER
        (type and InputType.TYPE_NUMBER_FLAG_DECIMAL) shouldBe InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    @Test
    fun `the keyboard probe can tell a text field from a number field`() {
        field = FieldState()
        compose.setContent {
            view = LocalView.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                NameField(label = "Name", state = field, onTextChange = { field = field.edit(it) })
            }
        }
        compose.waitForIdle()

        (requestedInputType() and InputType.TYPE_MASK_CLASS) shouldBe InputType.TYPE_CLASS_TEXT
    }
}
