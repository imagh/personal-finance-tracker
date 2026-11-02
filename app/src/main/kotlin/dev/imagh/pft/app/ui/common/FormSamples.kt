package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.validation.Validation

/** The limit the sample form checks against; the real forms use core's own constant. */
private const val SAMPLE_MAX_NAME = 100

/** How many times "Sample " is repeated to make a fake nickname just over the limit (105 characters). */
private const val SAMPLE_LONG_REPEATS = 15

/**
 * A form screen with fake data: an `EditFormScaffold` with a Back button and two `NameField`s, the
 * second one starting in error (too long). It is live: editing a field clears its error, and Save
 * re-checks both fields with core's real `Validation.requireName`. The single source for the
 * preview, the debug catalogue's Forms page and the form goldens. Release builds drop it (R8; see
 * `verifyReleaseShrink`).
 */
@Composable
internal fun EditFormSample() {
    var name by remember { mutableStateOf(FieldState("Everyday spending")) }
    var nickname by remember {
        mutableStateOf(FieldState("Sample ".repeat(SAMPLE_LONG_REPEATS), FieldError.TooLong(SAMPLE_MAX_NAME)))
    }
    EditFormScaffold(
        title = "Edit account",
        subtitle = "Fake data",
        onPrimary = {
            name = name.check { Validation.requireName(it, SAMPLE_MAX_NAME) }.state
            nickname = nickname.check { Validation.requireName(it, SAMPLE_MAX_NAME) }.state
        },
        onBack = {},
    ) {
        item { NameField("Account name", name, onTextChange = { name = name.edit(it) }) }
        item {
            NameField("Nickname", nickname, onTextChange = { nickname = nickname.edit(it) }, imeAction = ImeAction.Done)
        }
    }
}

@PreviewLightDark
@Composable
internal fun EditFormPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { EditFormSample() } }
}
