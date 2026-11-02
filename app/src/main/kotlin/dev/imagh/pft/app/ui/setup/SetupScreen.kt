package dev.imagh.pft.app.ui.setup

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.EditFormScaffold
import dev.imagh.pft.app.ui.common.NameField
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * First-run setup, drawn from [state] alone: a welcome header, one line saying what the two fields
 * are for, the user's name and this phone's name, and Continue in the bottom bar (disabled while
 * [SetupUiState.saving]). There is no Back, because nothing is behind this screen.
 *
 * It holds no state and knows no ViewModel, so tests and goldens render it from fake data;
 * [SetupRoute] wires the real one.
 *
 * @param state what to show.
 * @param onNameChange the name field was edited to this text.
 * @param onLabelChange the phone-name field was edited to this text.
 * @param onContinue Continue was tapped.
 */
@Composable
fun SetupScreen(
    state: SetupUiState,
    onNameChange: (String) -> Unit,
    onLabelChange: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    EditFormScaffold(
        title = stringResource(R.string.setup_title),
        onPrimary = onContinue,
        modifier = modifier,
        subtitle = stringResource(R.string.setup_subtitle),
        primaryLabel = stringResource(R.string.setup_continue),
        saving = state.saving,
    ) {
        item {
            Text(
                stringResource(R.string.setup_intro),
                modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.sm),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item { NameField(stringResource(R.string.setup_name_label), state.name, onNameChange) }
        item {
            NameField(
                stringResource(R.string.setup_device_label),
                state.deviceLabel,
                onLabelChange,
                imeAction = ImeAction.Done,
            )
        }
    }
}

/**
 * The setup screen wired to its [SetupViewModel]: the only place that touches the ViewModel. The
 * phone name is prefilled with the device model. When setup is done (saved now or already there) it
 * calls [onDone] once, and the caller navigates away.
 *
 * @param services the open object graph the identity is saved through.
 * @param onDone called when an identity exists.
 */
@Composable
fun SetupRoute(
    services: AppServices,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { SetupViewModel.factory(services, Build.MODEL) }
    val viewModel: SetupViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestOnDone by rememberUpdatedState(onDone)
    LaunchedEffect(state.done) { if (state.done) latestOnDone() }
    SetupScreen(
        state = state,
        onNameChange = viewModel::onNameChange,
        onLabelChange = viewModel::onDeviceLabelChange,
        onContinue = viewModel::submit,
        modifier = modifier,
    )
}
