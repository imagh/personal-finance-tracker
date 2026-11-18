package dev.imagh.pft.app.ui.tags

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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.EditFormScaffold
import dev.imagh.pft.app.ui.common.NameField
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * The tag create and rename form drawn from [state] alone, on the shared `EditFormScaffold`: one name
 * field with Save in the bottom bar and Back in the header.
 *
 * - **The name field shows its own error** from [state], a duplicate ("Already in use") included.
 * - **Nothing is drawn before an edit has loaded** ([TagEditUiState.loaded]) and nothing once the tag was
 *   not found, and Save is disabled in both, and while saving.
 * - After an unexpected failure ([TagEditUiState.saveFailed]) a fixed "Couldn't save. Try again." line
 *   appears under the field as a polite live region.
 *
 * It holds no state and knows no ViewModel, so tests and goldens render it from fake data;
 * [TagEditRoute] wires the real one.
 *
 * @param state what to show.
 * @param actions what the field and Save do.
 * @param onBack called when Back is tapped.
 */
@Composable
fun TagEditScreen(
    state: TagEditUiState,
    actions: TagEditActions,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    EditFormScaffold(
        title = stringResource(if (state.editing) R.string.tag_edit_title else R.string.tag_new_title),
        onPrimary = actions.save,
        modifier = modifier,
        saving = state.saving || !state.loaded || state.notFound,
        onBack = onBack,
    ) {
        if (!state.loaded || state.notFound) return@EditFormScaffold
        item {
            NameField(
                stringResource(R.string.tag_name_label),
                state.name,
                actions.onNameChange,
                imeAction = ImeAction.Done,
            )
        }
        if (state.saveFailed) {
            item {
                Text(
                    stringResource(R.string.form_save_failed),
                    modifier =
                        Modifier
                            .padding(horizontal = spacing.gutter, vertical = spacing.sm)
                            // Announced when it appears, without moving focus.
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

/**
 * The tag form wired to its [TagEditViewModel]: the only place that touches the ViewModel. When the tag
 * is saved it calls [onDone] once; when the tag asked for does not exist it calls [onNotFound] once. The
 * caller navigates away in both cases.
 *
 * @param services the open object graph the tags service is taken from.
 * @param tagId the tag to edit as it arrived in the route (untrusted text), or null to create.
 * @param onDone called when the tag is saved.
 * @param onNotFound called when [tagId] is unknown, deleted or not an id.
 * @param onBack called when Back is tapped.
 */
@Composable
fun TagEditRoute(
    services: AppServices,
    tagId: String?,
    onDone: () -> Unit,
    onNotFound: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services, tagId) { TagEditViewModel.factory(services, tagId) }
    val viewModel: TagEditViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestOnDone by rememberUpdatedState(onDone)
    val latestOnNotFound by rememberUpdatedState(onNotFound)
    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedEffect(state.saved) { if (state.saved) latestOnDone() }
    LaunchedEffect(state.notFound) { if (state.notFound) latestOnNotFound() }
    val actions =
        remember(viewModel) { TagEditActions(onNameChange = viewModel::onNameChange, save = viewModel::submit) }
    TagEditScreen(state, actions, onBack = { latestOnBack() }, modifier = modifier)
}
