package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * A single-line text field for a name (a person, a phone, an account, a category), the one name
 * input every form uses. Named consumers: first-run setup, and the account, category and loan
 * forms.
 *
 * **It never changes what the user typed.** There is no `maxLength`, no clipping and no trimming
 * here: the text is reported exactly as typed through [onTextChange], and a length problem is
 * reported afterwards as [FieldError.TooLong] by core's validation (see [FieldState.check]). Silent
 * truncation would save a different name from the one the user saw.
 *
 * It is an M3 `OutlinedTextField` with a [label], word capitalisation, and the error under it from
 * [FieldState.error] as supporting text ("Required", "At most N characters"). M3 marks the field
 * as in error for TalkBack. The field fills the width between the screen gutters and adds the form's
 * vertical rhythm, so a form lists its fields with no extra spacing.
 *
 * @param label the field's name, for example "Your name" (shown inside, then floating above the text).
 * @param state the typed text and the error to show.
 * @param onTextChange called with the full new text on every edit; the caller applies
 *   [FieldState.edit], which also clears the error.
 * @param imeAction `Next` while more fields follow, `Done` on the last one.
 */
@Composable
fun NameField(
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
        isError = state.error != null,
        supportingText = state.error?.let { error -> { Text(errorMessage(error)) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = imeAction),
    )
}

@Composable
private fun errorMessage(error: FieldError): String =
    when (error) {
        FieldError.Required -> stringResource(R.string.field_required)
        is FieldError.TooLong -> stringResource(R.string.field_too_long, error.max)
    }
