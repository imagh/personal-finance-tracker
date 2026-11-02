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
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * Shown instead of the app when the data cannot be opened safely (plan decision 9, fail closed): the
 * database key cannot be produced, or the stored identity is inconsistent. It says, in plain words,
 * that the data is safe and was not touched, warns against the one thing that would destroy it
 * (clearing storage, uninstalling), and shows the cause's fixed code for the owner to quote. The
 * title, the warning and the `Code: ...` line are shared; only the explanation differs by [cause].
 *
 * It shows **only** fixed strings and the cause's fixed code (see [DataUnavailableCause]): never a
 * path, a key, a file name, a name or the underlying exception, so the screen is safe to
 * screenshot-share (and it is `FLAG_SECURE` anyway, as the whole activity is). There is
 * deliberately no "reset" or "start fresh" action: the app never creates a new database or
 * identity over existing data.
 *
 * @param cause why the data cannot be opened; it fixes the explanation and the code.
 */
@Composable
fun DataUnavailableScreen(
    cause: DataUnavailableCause,
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
            stringResource(R.string.data_unavailable_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text(stringResource(cause.explanation), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.data_unavailable_warning), style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(R.string.data_unavailable_code, cause.code),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
