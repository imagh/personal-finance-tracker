package dev.imagh.pft.app.ui.accounts

import androidx.annotation.StringRes
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
import dev.imagh.pft.app.log.EntityAction
import dev.imagh.pft.app.ui.common.EntityListScreen
import dev.imagh.pft.app.ui.common.EntityRow
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.common.UndoDeleteSnackbar
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.ids.AccountId

/**
 * The accounts list drawn from [state] alone: the shared `EntityListScreen` over account rows
 * ([AccountRowMapper]). Active accounts offer Archive and Delete; archived ones offer Unarchive and
 * Delete. The add button (and the empty state's "Add account") opens the create form, and tapping a
 * row, active or archived, opens that account's edit form.
 *
 * @param state what to show; nothing is listed, and "No accounts yet" is not said, until
 *   [AccountsUiState.loaded].
 * @param actions what each row action does.
 * @param snackbars the state behind the host this screen renders; the route shows the Undo and
 *   failure messages on it.
 * @param onBack called when Back is tapped.
 */
@Composable
fun AccountsScreen(
    state: AccountsUiState,
    actions: AccountsActions,
    snackbars: SnackbarHostState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typeLabels = AccountType.entries.associateWith { stringResource(typeLabelRes(it)) }
    val archive = stringResource(R.string.entity_action_archive)
    val unarchive = stringResource(R.string.entity_action_unarchive)
    val delete = stringResource(R.string.entity_action_delete)
    val addLabel = stringResource(R.string.accounts_add)

    fun Account.row(vararg menu: Pair<String, (AccountId) -> Unit>): EntityRow =
        toRow(
            typeLabel = typeLabels.getValue(type),
            onClick = { actions.edit(meta.id) },
            actions = menu.map { (label, run) -> RowAction(label) { run(meta.id) } },
        )

    EntityListScreen(
        title = stringResource(R.string.accounts_title),
        active = state.active.map { it.row(archive to actions.archive, delete to actions.delete) },
        archived = state.archived.map { it.row(unarchive to actions.unarchive, delete to actions.delete) },
        emptyMessage = stringResource(R.string.accounts_empty),
        snackbarHost = { SnackbarHost(snackbars) },
        onBack = onBack,
        modifier = modifier,
        add = RowAction(addLabel, actions.add),
        loaded = state.loaded,
    )
}

/**
 * The accounts list wired to its [AccountsViewModel]: the only place that touches the ViewModel.
 *
 * - **Reload on resume (D2).** `LifecycleResumeEffect` reads the lists again whenever the screen
 *   resumes, which is how a change made on another screen (B2b's form) shows up on return.
 * - **Undo.** A delete shows "Account deleted" with Undo on the scaffold's snackbar host
 *   ([UndoDeleteSnackbar]). The offer ends, and the delete stands, when the snackbar times out or the
 *   screen is paused or left.
 * - **Failures** show a fixed "Couldn't ... Try again." snackbar and are then cleared.
 * - **Not found.** When the edit form was opened for an account that no longer exists, the caller sets
 *   [notFound] and this shows "That account no longer exists", then calls [onNotFoundShown].
 *
 * @param services the open object graph the accounts service is taken from.
 * @param navigation where Back, the add button and a row tap go.
 * @param notFound true while a "no longer exists" message is waiting to be shown.
 * @param onNotFoundShown the message was shown; the caller clears [notFound].
 */
@Composable
fun AccountsRoute(
    services: AppServices,
    navigation: AccountsNavigation,
    notFound: Boolean,
    onNotFoundShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { AccountsViewModel.factory(services) }
    val viewModel: AccountsViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbars = remember { SnackbarHostState() }

    LifecycleResumeEffect(viewModel) {
        viewModel.reload()
        onPauseOrDispose { viewModel.dismissUndo() }
    }
    UndoDeleteSnackbar(
        offer = state.undo,
        message = stringResource(R.string.accounts_deleted),
        host = snackbars,
        onUndo = viewModel::undo,
        onDismissed = viewModel::dismissUndo,
    )
    val failed = state.failedAction
    val failureText = failed?.let { stringResource(failureRes(it)) }
    LaunchedEffect(failed) {
        if (failureText != null) {
            snackbars.showSnackbar(failureText)
            viewModel.failureShown()
        }
    }

    val notFoundText = stringResource(R.string.accounts_not_found)
    LaunchedEffect(notFound) {
        if (notFound) {
            snackbars.showSnackbar(notFoundText)
            onNotFoundShown()
        }
    }

    val latestNavigation by rememberUpdatedState(navigation)
    val actions =
        remember(viewModel) {
            AccountsActions(
                archive = viewModel::archive,
                unarchive = viewModel::unarchive,
                delete = viewModel::delete,
                add = { latestNavigation.add() },
                edit = { latestNavigation.edit(it) },
            )
        }
    AccountsScreen(state, actions, snackbars, onBack = { latestNavigation.back() }, modifier = modifier)
}

@StringRes
internal fun typeLabelRes(type: AccountType): Int =
    when (type) {
        AccountType.BANK -> R.string.account_type_bank
        AccountType.CREDIT_CARD -> R.string.account_type_credit_card
        AccountType.CASH -> R.string.account_type_cash
        AccountType.WALLET -> R.string.account_type_wallet
    }

@StringRes
private fun failureRes(action: EntityAction): Int =
    when (action) {
        EntityAction.ARCHIVE -> R.string.entity_failed_archive
        EntityAction.UNARCHIVE -> R.string.entity_failed_unarchive
        EntityAction.DELETE -> R.string.entity_failed_delete
        EntityAction.RESTORE -> R.string.entity_failed_restore
    }
