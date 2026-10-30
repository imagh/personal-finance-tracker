package dev.imagh.pft.app.ui.startup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import dev.imagh.pft.R
import dev.imagh.pft.app.security.DbKeyUnavailable
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * Shown instead of the app when the database key cannot be produced (plan decision 9, fail closed).
 * It says, in plain words, that the data is safe and was not touched, warns against the one thing
 * that would destroy it (clearing storage, uninstalling), and shows the [reason] as a code the owner
 * can quote.
 *
 * It shows **only** the fixed code: never a path, a key, a file name or the underlying exception, so
 * the screen is safe to screenshot-share (and it is `FLAG_SECURE` anyway, as the whole activity is).
 * There is deliberately no "reset" or "start fresh" action: the app never creates a new database
 * over existing data.
 *
 * @param reason why the key is unavailable.
 */
@Composable
fun KeyUnavailableScreen(
    reason: DbKeyUnavailable.Reason,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.gutter, vertical = spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        Text(
            stringResource(R.string.key_unavailable_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text(stringResource(R.string.key_unavailable_explanation), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.key_unavailable_warning), style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(R.string.key_unavailable_code, reason.name),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
