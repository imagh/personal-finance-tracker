package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseParser

/** The rupee sign drawn before the typed amount; the parser also accepts it as a typed prefix. */
private const val RUPEE_PREFIX = "₹"

/**
 * Checks this field's text as an amount of money, the one amount rule every form shares (the account
 * balance now; the transaction, budget and loan amounts later).
 *
 * The text is trimmed and then read by core's `PaiseParser` (Indian or Western grouping, at most two
 * decimals, up to `Paise.MAX_AMOUNT`). The result is a **magnitude only**: the parser accepts no
 * sign, so `"-5"` is invalid, and a caller that needs a signed value (an account balance) applies
 * the sign itself. Zero is a valid amount; a caller that needs a positive one checks that itself.
 *
 * @return the amount and this state with no error; or a null value and this state with
 *   [FieldError.Required] (blank text) or [FieldError.InvalidAmount] (anything the parser refuses).
 *   The text is never altered.
 */
fun FieldState.checkAmount(): FieldCheck<Paise> {
    val trimmed = text.trim()
    val amount = PaiseParser.parse(trimmed)
    return when {
        trimmed.isEmpty() -> FieldCheck(null, copy(error = FieldError.Required))
        amount == null -> FieldCheck(null, copy(error = FieldError.InvalidAmount))
        else -> FieldCheck(amount, copy(error = null))
    }
}

/**
 * A single-line text field for an amount of money in rupees, the one amount input every form uses.
 * Named consumers: the account's opening balance, and later the transaction, item, budget and loan
 * amounts.
 *
 * **It never changes what the user typed**, like [NameField]: no filtering, clipping or formatting
 * while typing. The text goes out exactly as typed through [onTextChange], and a problem is reported
 * afterwards through [FieldState.error] ([FieldError.InvalidAmount], [FieldError.Required]) by
 * [checkAmount]. The keyboard is a decimal number keyboard, and a rupee sign is drawn before the
 * text (it is not part of the text).
 *
 * The amount is a magnitude: this field has no sign. Whether it is money in or out, or owed, is the
 * screen's decision, stated in the [label] ("Current balance", "Amount owed").
 *
 * @param label the field's name, for example "Current balance".
 * @param state the typed text and the error to show.
 * @param onTextChange called with the full new text on every edit; the caller applies
 *   [FieldState.edit], which also clears the error.
 * @param imeAction `Next` while more fields follow, `Done` on the last one.
 */
@Composable
fun AmountField(
    label: String,
    state: FieldState,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
) {
    val spacing = PftTheme.spacing
    OutlinedTextField(
        value = state.text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.sm),
        label = { Text(label) },
        prefix = { Text(RUPEE_PREFIX) },
        isError = state.error != null,
        supportingText = state.error?.let { error -> { Text(error.message()) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
    )
}
