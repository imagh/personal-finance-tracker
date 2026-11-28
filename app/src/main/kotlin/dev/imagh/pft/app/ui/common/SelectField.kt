package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

/** The least height of a select field: the 48dp touch target, which M3's 56dp outlined field exceeds. */
private val MIN_HEIGHT = 48.dp

/**
 * A form field that shows a chosen value and opens something to choose another when tapped (a date
 * dialog, a picker sheet): the look of the other form fields, with the read-only text drawn inside an
 * outlined field and a [trailing] symbol saying what it opens. Named consumers: [DateField], and the
 * account, category and tag fields of the transaction form.
 *
 * - **TalkBack.** The text field underneath is hidden from semantics and a transparent overlay is the one
 *   node: a button that speaks [spoken] (the label and the value together, e.g. "Date, Nov 15, 2026"), so
 *   the label and the value are not read twice. A [error] is exposed as the node's error and drawn under
 *   the field.
 * - **Touch.** The whole field, error text included, is the target; it is never under 48dp.
 *
 * @param label the field's name, drawn as the outlined field's label.
 * @param value the chosen value as shown; empty when nothing is chosen (the label then sits in the field).
 * @param spoken what a screen reader says for the field: label and value, built by the caller.
 * @param trailing the symbol at the end of the field.
 * @param onClick opens the choice.
 * @param error why the current choice is refused, or null.
 */
@Composable
internal fun SelectField(
    label: String,
    value: String,
    spoken: String,
    trailing: PftSymbol,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: FieldError? = null,
) {
    val spacing = PftTheme.spacing
    val errorText = error?.message()
    Box(modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.sm)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().heightIn(min = MIN_HEIGHT).clearAndSetSemantics {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(painterResource(trailing.res), contentDescription = null) },
            isError = errorText != null,
            supportingText = errorText?.let { text -> { Text(text) } },
            singleLine = true,
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(role = Role.Button, onClick = onClick)
                .semantics {
                    contentDescription = spoken
                    errorText?.let { error(it) }
                },
        )
    }
}
