package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * What a list shows when it has nothing to show: an optional glyph, a one-line message and an
 * optional action, centred. It does not scroll or fill the screen height; place it in the list's
 * content (an `item { EmptyState(...) }`) or in a `Box` that centres it.
 *
 * Named consumers: every entity list (`EntityListScreen`), Home's empty month and the transaction
 * search with no results.
 *
 * @param message what is missing and, if it helps, what to do ("No accounts yet").
 * @param glyph an optional picture above the message, for example a `CategoryGlyph` or a symbol.
 * @param actionLabel the action's text ("Add account"); give it together with [onAction].
 * @param onAction the action's callback.
 * @throws IllegalArgumentException if only one of [actionLabel] and [onAction] is given.
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    glyph: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    require((actionLabel == null) == (onAction == null)) { "give an action label and its callback together" }
    val spacing = PftTheme.spacing
    Column(
        modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        glyph?.invoke()
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            FilledTonalButton(onAction) { Text(actionLabel) }
        }
    }
}
