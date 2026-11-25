package dev.imagh.pft.app.ui.tags

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.EntityListScreen
import dev.imagh.pft.app.ui.common.EntityRow
import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.common.RowGlyph
import dev.imagh.pft.app.ui.common.UndoDeleteSnackbar
import dev.imagh.pft.app.ui.common.failureRes
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag

/**
 * The tags list drawn from [state] alone: the shared `EntityListScreen` over tag rows. A row is the
 * tag's name with a neutral tag badge; the badge is decorative (nothing is spoken for it), because the
 * title already says the name and a tag has no type, colour or icon to say anything more. Active tags
 * offer Archive and Delete; archived ones offer Unarchive and Delete. Tags have no order, so there is no
 * Move up or Move down. The add button (and the empty state's "Add tag") opens the create form, and
 * tapping a row, active or archived, opens that tag's form.
 *
 * @param state what to show; nothing is listed, and "No tags yet" is not said, until [TagsUiState.loaded].
 * @param actions what each row action does.
 * @param snackbars the state behind the host this screen renders; the route shows the Undo, failure and
 *   not-found messages on it.
 * @param onBack called when Back is tapped.
 */
@Composable
fun TagsScreen(
    state: TagsUiState,
    actions: TagsActions,
    snackbars: SnackbarHostState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val archive = stringResource(R.string.entity_action_archive)
    val unarchive = stringResource(R.string.entity_action_unarchive)
    val delete = stringResource(R.string.entity_action_delete)
    val addLabel = stringResource(R.string.tags_add)

    fun Tag.row(vararg menu: Pair<String, (TagId) -> Unit>): EntityRow =
        toRow(
            onClick = { actions.edit(meta.id) },
            actions = menu.map { (label, run) -> RowAction(label) { run(meta.id) } },
        )

    EntityListScreen(
        title = stringResource(R.string.tags_title),
        active = state.active.map { it.row(archive to actions.archive, delete to actions.delete) },
        archived = state.archived.map { it.row(unarchive to actions.unarchive, delete to actions.delete) },
        emptyMessage = stringResource(R.string.tags_empty),
        snackbarHost = { SnackbarHost(snackbars) },
        onBack = onBack,
        modifier = modifier,
        add = RowAction(addLabel, actions.add),
        loaded = state.loaded,
    )
}

/** This tag as a list row: its name, a silent tag badge, what tapping it does and the given [actions]. */
private fun Tag.toRow(
    onClick: () -> Unit,
    actions: List<RowAction>,
): EntityRow =
    EntityRow(
        key = meta.id.value,
        title = name,
        glyph = RowGlyph(GlyphSource.Symbol(PftSymbol.TAG), CategoryColour.SLATE.key, label = null),
        onClick = onClick,
        actions = actions,
    )

/**
 * The tags list wired to its [TagsViewModel]: the only place that touches the ViewModel.
 *
 * - **Reload on resume (D2).** `LifecycleResumeEffect` reads the lists again whenever the screen
 *   resumes, which is how a change made in the form shows up on return.
 * - **Undo.** A delete shows "Tag deleted" with Undo on the scaffold's snackbar host
 *   ([UndoDeleteSnackbar]). The offer ends, and the delete stands, when the snackbar times out or the
 *   screen is paused or left.
 * - **Failures** show a fixed "Couldn't ... Try again." snackbar and are then cleared.
 * - **Not found.** When the form was opened for a tag that no longer exists, the caller sets [notFound]
 *   and this shows "That tag no longer exists", then calls [onNotFoundShown].
 *
 * @param services the open object graph the tags service is taken from.
 * @param navigation where Back, the add button and a row tap go.
 * @param notFound true while a "no longer exists" message is waiting to be shown.
 * @param onNotFoundShown the message was shown; the caller clears [notFound].
 */
@Composable
fun TagsRoute(
    services: AppServices,
    navigation: TagsNavigation,
    notFound: Boolean,
    onNotFoundShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { TagsViewModel.factory(services) }
    val viewModel: TagsViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbars = remember { SnackbarHostState() }

    LifecycleResumeEffect(viewModel) {
        viewModel.reload()
        onPauseOrDispose { viewModel.dismissUndo() }
    }
    UndoDeleteSnackbar(
        offer = state.undo,
        message = stringResource(R.string.tags_deleted),
        host = snackbars,
        onUndo = viewModel::undo,
        onDismissed = viewModel::dismissUndo,
    )
    val failed = state.failedAction
    val failureText = failed?.let { stringResource(it.failureRes()) }
    LaunchedEffect(failed) {
        if (failureText != null) {
            snackbars.showSnackbar(failureText)
            viewModel.failureShown()
        }
    }
    val inUse = state.inUse
    val inUseText = stringResource(R.string.entity_delete_in_use)
    LaunchedEffect(inUse) {
        if (inUse) {
            snackbars.showSnackbar(inUseText)
            viewModel.inUseShown()
        }
    }

    val notFoundText = stringResource(R.string.tags_not_found)
    LaunchedEffect(notFound) {
        if (notFound) {
            snackbars.showSnackbar(notFoundText)
            onNotFoundShown()
        }
    }

    val latestNavigation by rememberUpdatedState(navigation)
    val actions =
        remember(viewModel) {
            TagsActions(
                archive = viewModel::archive,
                unarchive = viewModel::unarchive,
                delete = viewModel::delete,
                add = { latestNavigation.add() },
                edit = { latestNavigation.edit(it) },
            )
        }
    TagsScreen(state, actions, snackbars, onBack = { latestNavigation.back() }, modifier = modifier)
}
