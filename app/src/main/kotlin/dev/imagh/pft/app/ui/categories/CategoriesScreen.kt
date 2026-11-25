package dev.imagh.pft.app.ui.categories

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.EntityListScreen
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.common.UndoDeleteSnackbar
import dev.imagh.pft.app.ui.common.failureRes
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.CategoryKind

/**
 * The categories list drawn from [state] alone: an Expense/Income switch above the shared
 * `EntityListScreen` over category rows ([toRow]). Active rows offer Move up and Move down (not on
 * the first and last of their siblings), Archive and Delete; Loan EMI offers neither of the last two;
 * archived rows offer Unarchive and Delete. The add button (and the empty state's "Add category") opens the create
 * form for the kind on show; tapping a row, active or archived, opens that category's edit form.
 *
 * @param state what to show; nothing is listed, and "No categories yet" is not said, until
 *   [CategoriesUiState.loaded]. The switch is always there.
 * @param actions what each row action and the switch do.
 * @param snackbars the state behind the host this screen renders; the route shows the Undo and
 *   notice messages on it.
 * @param onBack called when Back is tapped.
 */
@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    actions: CategoriesActions,
    snackbars: SnackbarHostState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labels = CategoryAction.entries.associateWith { stringResource(actionLabelRes(it)) }
    val resources = LocalContext.current.resources
    val parentLine: (String) -> String = { name -> resources.getString(R.string.category_under_parent, name) }

    fun List<CategoryItem>.rows() =
        map {
            it.toRow(
                actionLabel = labels::getValue,
                parentLine = parentLine,
                choose = actions.choose,
                edit = actions.edit,
            )
        }

    val addLabel = stringResource(R.string.categories_add)
    EntityListScreen(
        title = stringResource(R.string.categories_title),
        active = state.active.rows(),
        archived = state.archived.rows(),
        emptyMessage = stringResource(R.string.categories_empty),
        snackbarHost = { SnackbarHost(snackbars) },
        onBack = onBack,
        modifier = modifier,
        loaded = state.loaded,
        add = RowAction(addLabel) { actions.add(state.kind) },
        header = { KindSwitch(state.kind, actions.selectKind) },
    )
}

/** The Expense/Income switch: two segmented buttons, the selected one announced as selected. */
@Composable
private fun KindSwitch(
    selected: CategoryKind,
    onSelect: (CategoryKind) -> Unit,
) {
    val spacing = PftTheme.spacing
    val kinds = CategoryKind.entries
    SingleChoiceSegmentedButtonRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.gutter, vertical = spacing.sm),
    ) {
        kinds.forEachIndexed { index, kind ->
            SegmentedButton(
                selected = kind == selected,
                onClick = { onSelect(kind) },
                shape = SegmentedButtonDefaults.itemShape(index, kinds.size),
            ) {
                Text(stringResource(kindLabelRes(kind)))
            }
        }
    }
}

/**
 * The categories list wired to its [CategoriesViewModel]: the only place that touches the ViewModel.
 *
 * - **Reload on resume (D2).** `LifecycleResumeEffect` reads the lists again whenever the screen
 *   resumes, which is how a change made on another screen (B4b's form) shows up on return.
 * - **Undo.** A delete shows "Category deleted" with Undo on the scaffold's snackbar host
 *   ([UndoDeleteSnackbar]). The offer ends, and the delete stands, when the snackbar times out or the
 *   screen is paused or left.
 * - **Notices** (a refused delete, a failed action) show a fixed message and are then cleared.
 *
 * - **Not found.** When the edit form was opened for a category that no longer exists, the caller sets
 *   [notFound] and this shows "That category no longer exists", then calls [onNotFoundShown].
 *
 * @param services the open object graph the categories service is taken from.
 * @param navigation where Back, the add button (with the kind on show) and a row tap go.
 * @param notFound true while a "no longer exists" message is waiting to be shown.
 * @param onNotFoundShown the message was shown; the caller clears [notFound].
 */
@Composable
fun CategoriesRoute(
    services: AppServices,
    navigation: CategoriesNavigation,
    notFound: Boolean,
    onNotFoundShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { CategoriesViewModel.factory(services) }
    val viewModel: CategoriesViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbars = remember { SnackbarHostState() }

    LifecycleResumeEffect(viewModel) {
        viewModel.reload()
        onPauseOrDispose { viewModel.dismissUndo() }
    }
    UndoDeleteSnackbar(
        offer = state.undo,
        message = stringResource(R.string.categories_deleted),
        host = snackbars,
        onUndo = viewModel::undo,
        onDismissed = viewModel::dismissUndo,
    )
    val notice = state.notice
    val noticeText =
        when (notice) {
            null -> null
            CategoriesNotice.DeleteBlocked -> stringResource(R.string.categories_delete_blocked)
            CategoriesNotice.InUse -> stringResource(R.string.entity_delete_in_use)
            is CategoriesNotice.Failed -> stringResource(notice.action.failureRes())
        }
    LaunchedEffect(notice) {
        if (noticeText != null) {
            snackbars.showSnackbar(noticeText)
            viewModel.noticeShown()
        }
    }

    val notFoundText = stringResource(R.string.categories_not_found)
    LaunchedEffect(notFound) {
        if (notFound) {
            snackbars.showSnackbar(notFoundText)
            onNotFoundShown()
        }
    }

    val latestNavigation by rememberUpdatedState(navigation)
    val actions =
        remember(viewModel) {
            CategoriesActions(
                choose = viewModel::choose,
                selectKind = viewModel::selectKind,
                add = { latestNavigation.add(it) },
                edit = { latestNavigation.edit(it) },
            )
        }
    CategoriesScreen(state, actions, snackbars, onBack = { latestNavigation.back() }, modifier = modifier)
}

@StringRes
private fun actionLabelRes(action: CategoryAction): Int =
    when (action) {
        CategoryAction.MOVE_UP -> R.string.category_action_move_up
        CategoryAction.MOVE_DOWN -> R.string.category_action_move_down
        CategoryAction.ARCHIVE -> R.string.entity_action_archive
        CategoryAction.UNARCHIVE -> R.string.entity_action_unarchive
        CategoryAction.DELETE -> R.string.entity_action_delete
    }

/** The label of the kind's Expense/Income switch segment, also the form's fixed Type row. */
@StringRes
internal fun kindLabelRes(kind: CategoryKind): Int =
    when (kind) {
        CategoryKind.EXPENSE -> R.string.category_kind_expense
        CategoryKind.INCOME -> R.string.category_kind_income
    }
