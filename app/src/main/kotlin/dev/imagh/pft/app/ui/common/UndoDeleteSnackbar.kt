package dev.imagh.pft.app.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import dev.imagh.pft.R

/**
 * Shows "[message]" with an **Undo** action for as long as a delete can be undone (plan D11, INT-3),
 * on the [host] a screen passes to `CollapsingHeaderScaffold(snackbarHost = ...)`. It draws nothing
 * itself: it is the effect that queues the snackbar, so the list's ViewModel keeps owning the
 * delete and its undo and no result has to travel across navigation.
 *
 * - **Undo** reports [onUndo]; letting the snackbar time out (10 s) or swiping it away reports
 *   [onDismissed]. Exactly one of the two is reported per offer.
 * - **A new [offer]** replaces the snackbar on screen (the old offer reports nothing: the owner
 *   already moved on to the new one), and **a null [offer]** removes it, also silently. Leaving the
 *   screen cancels the effect the same way, so the owner clears a stale offer itself (see
 *   `AccountsRoute`).
 *
 * Named consumers: the accounts, categories and tags lists (and C4's transaction delete).
 *
 * @param offer identifies the delete on offer (the deleted record's id), or null when there is
 *   none. Only its identity is used, as the effect's key; it is never shown or logged.
 * @param message the fixed text ("Account deleted"); never a name.
 * @param host the snackbar host state the scaffold's host renders.
 * @param onUndo the user tapped Undo.
 * @param onDismissed the snackbar ended without Undo.
 */
@Composable
fun UndoDeleteSnackbar(
    offer: Any?,
    message: String,
    host: SnackbarHostState,
    onUndo: () -> Unit,
    onDismissed: () -> Unit,
) {
    val undoLabel = stringResource(R.string.entity_undo)
    // The effect outlives recompositions, so it must call the latest callbacks, not the first ones.
    val undo by rememberUpdatedState(onUndo)
    val dismissed by rememberUpdatedState(onDismissed)
    LaunchedEffect(offer) {
        if (offer == null) return@LaunchedEffect
        when (host.showSnackbar(message, actionLabel = undoLabel, duration = SnackbarDuration.Long)) {
            SnackbarResult.ActionPerformed -> undo()
            SnackbarResult.Dismissed -> dismissed()
        }
    }
}
