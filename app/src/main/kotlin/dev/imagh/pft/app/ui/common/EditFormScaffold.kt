package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftSymbol

/**
 * The frame of every create and edit screen: a [HeaderStyle.Standard] [CollapsingHeaderScaffold]
 * whose list holds the form's fields, with the primary action (Save, Continue) in a
 * [BottomActionBar] in the thumb zone. Named consumers: first-run setup, and the account, category,
 * loan, goal and budget forms.
 *
 * - **Back** is optional. With [onBack] the header's action row gets a back [HeaderIconButton]
 *   (a 48dp touch target, labelled for TalkBack); without it there is none. Setup passes none,
 *   because there is nothing behind it.
 * - **[saving]** disables the primary action while a save is in flight, so a double tap cannot
 *   save twice. It does not replace the ViewModel's own guard.
 * - **Keyboard.** This adds no `imePadding()`: the underlying scaffold already lifts its bottom
 *   bar above the keyboard, and a second one would pad twice.
 *
 * @param title the screen name, shown in the expanded and collapsed header.
 * @param onPrimary called when the primary action is tapped (it is not called while [saving]).
 * @param modifier applied to the scaffold.
 * @param subtitle an optional one-line caption under the title.
 * @param primaryLabel the primary action's text, a verb; "Save" by default.
 * @param saving true while the form is being saved.
 * @param onBack called when Back is tapped, or null for no Back button.
 * @param content the form's fields as list items (for example one [NameField] per item).
 */
@Composable
fun EditFormScaffold(
    title: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    primaryLabel: String = stringResource(R.string.form_save),
    saving: Boolean = false,
    onBack: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    val backLabel = stringResource(R.string.form_back)
    CollapsingHeaderScaffold(
        title = title,
        modifier = modifier,
        style = HeaderStyle.Standard,
        subtitle = subtitle,
        actions = { if (onBack != null) HeaderIconButton(PftSymbol.BACK, backLabel, onBack) },
        bottomBar = {
            BottomActionBar(primary = BottomBarAction(primaryLabel, enabled = !saving, onClick = onPrimary))
        },
        content = content,
    )
}
