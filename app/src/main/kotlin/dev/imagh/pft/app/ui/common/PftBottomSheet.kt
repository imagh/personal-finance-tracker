package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import dev.imagh.pft.app.ui.theme.PftTheme
import kotlinx.coroutines.launch

/**
 * The one bottom sheet every picker opens in (plan D11 and the design plan: pickers are sheets,
 * never top dropdowns). Named consumers: `AccountPicker` (B2c), `CategoryPicker` (B4b), `TagPicker`
 * (B5b) and the transaction date picker (C4).
 *
 * It is the only file that opts into M3's `ExperimentalMaterial3Api` for `ModalBottomSheet`, so an
 * upgrade that changes that API touches this one place. It adds nothing of its own to the look: the
 * sheet takes the theme's container colour and its extra-large corner radius (32dp, top corners only)
 * from `MaterialTheme`, a scrim dims the screen, and the sheet slides in and out.
 *
 * **Showing it.** Compose it only while it should be open (`if (open) PftBottomSheet(...) { ... }`);
 * removing it from the composition removes the sheet. [onDismiss] is how the sheet asks to be removed,
 * and the consumer must then stop composing it.
 *
 * **Dismissing.** Back, a tap on the scrim, a swipe down and [content]'s `close` all slide the sheet
 * out and then call [onDismiss], once even if `close` is called twice. The sheet opens at its full
 * content height (it has no half-open state), so a short picker is short and a long one scrolls.
 *
 * **Accessibility.** [title] is announced as a heading; the sheet itself is announced as a bottom
 * sheet by M3, and the scrim is a "Close sheet" button for TalkBack.
 *
 * @param title the heading shown above the content ("Choose account").
 * @param onDismiss called once, after the sheet has left the screen.
 * @param content the sheet body. It receives `close`, which slides the sheet out and then calls
 *   [onDismiss]; call it after the user has chosen something so the sheet closes with an animation
 *   rather than vanishing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PftBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(close: () -> Unit) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val latestOnDismiss by rememberUpdatedState(onDismiss)
    // A second hide() cancels the first, and a cancelled one leaves the sheet visible, so only the hide
    // that really finished reports the dismissal.
    val close: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) latestOnDismiss() }
    }

    ModalBottomSheet(onDismissRequest = { latestOnDismiss() }, modifier = modifier, sheetState = sheetState) {
        Column {
            Text(
                title,
                modifier =
                    Modifier
                        .padding(horizontal = PftTheme.spacing.gutter, vertical = PftTheme.spacing.md)
                        .semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
            )
            content(close)
        }
    }
}
