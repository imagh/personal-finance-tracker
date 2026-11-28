package dev.imagh.pft.app.ui.transactions

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.AmountField
import dev.imagh.pft.app.ui.common.DateField
import dev.imagh.pft.app.ui.common.EditFormScaffold
import dev.imagh.pft.app.ui.common.NameField
import dev.imagh.pft.ledger.transaction.TransactionType
import java.time.Clock

/**
 * The transaction add and edit form drawn from [state] alone, on the shared `EditFormScaffold` (slice C D8):
 * the type, the amount, the account (a transfer's source) and its destination, the date, the title, the item's
 * category and tags, a note and a bank reference, with Save in the bottom bar and Back in the header.
 *
 * - **Type.** Expense, income and transfer as segmented buttons when [TransactionEditUiState.typeOptions] has a
 *   choice to make; none when it has not (editing a transfer). A transfer shows no category and no bank
 *   reference; it shows both accounts.
 * - **Pickers.** The account, destination, category and tags each open their shared sheet; the sheet's own
 *   rules apply (only active accounts, only categories of the kind, an archived tag already chosen stays
 *   visible). Each field is one button for a screen reader that says its label and its value together.
 * - **Each field shows its own error** from [state]. When switching expense and income took the category away,
 *   a polite live-region line says so; after an unexpected failure a fixed "Couldn't save. Try again." line shows.
 * - **Nothing is drawn before the form has loaded** and nothing once the transaction was not found; Save is
 *   disabled in both and while saving.
 *
 * It holds no state of its own beyond which sheet is open, and knows no ViewModel, so tests and goldens render
 * it from fake data; [TransactionEditRoute] wires the real one.
 *
 * @param state what to show.
 * @param actions what each field and Save do.
 * @param onBack called when Back is tapped.
 */
@Composable
internal fun TransactionEditScreen(
    state: TransactionEditUiState,
    actions: TransactionEditActions,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var sheet by rememberSaveable { mutableStateOf(Sheet.NONE) }
    EditFormScaffold(
        title = stringResource(titleRes(state)),
        onPrimary = actions.save,
        modifier = modifier,
        saving = state.saving || !state.loaded || state.notFound,
        onBack = onBack,
    ) {
        if (!state.loaded || state.notFound) return@EditFormScaffold
        if (state.typeOptions.size > 1) item { TypeSelector(state.type, state.typeOptions, actions.onTypeChange) }
        item {
            AmountField(
                stringResource(R.string.transaction_amount_label),
                state.amount,
                onTextChange = { actions.onTextChange(EditField.AMOUNT, it) },
            )
        }
        accountItems(state) { sheet = it }
        item { DateField(stringResource(R.string.transaction_date_label), state.date, actions.onDateChange) }
        item {
            NameField(
                stringResource(R.string.transaction_title_label),
                state.title,
                onTextChange = { actions.onTextChange(EditField.TITLE, it) },
            )
        }
        itemFields(state, actions) { sheet = it }
        if (state.saveFailed) item { Notice(stringResource(R.string.form_save_failed), error = true) }
    }
    Sheets(state, actions, sheet) { sheet = Sheet.NONE }
}

@StringRes
private fun titleRes(state: TransactionEditUiState): Int =
    when {
        !state.editing -> R.string.transaction_new_title
        state.type == TransactionType.TRANSFER -> R.string.transaction_edit_transfer_title
        else -> R.string.transaction_edit_title
    }

/** The account field, and a transfer's destination. */
private fun LazyListScope.accountItems(
    state: TransactionEditUiState,
    open: (Sheet) -> Unit,
) {
    val transfer = state.type == TransactionType.TRANSFER
    item {
        val label = if (transfer) R.string.transaction_from_label else R.string.transaction_account_label
        AccountField(stringResource(label), state.accountId, state, state.accountError) { open(Sheet.ACCOUNT) }
    }
    if (transfer) {
        item {
            val label = stringResource(R.string.transaction_to_label)
            AccountField(label, state.toAccountId, state, state.toAccountError) { open(Sheet.TO_ACCOUNT) }
        }
    }
}

/** The category (not for a transfer), tags, note and bank reference (not for a transfer) fields. */
private fun LazyListScope.itemFields(
    state: TransactionEditUiState,
    actions: TransactionEditActions,
    open: (Sheet) -> Unit,
) {
    val transfer = state.type == TransactionType.TRANSFER
    if (!transfer) {
        item {
            val name = state.categoryId?.let { state.names.categories[it]?.name }.orEmpty()
            ValueField(stringResource(R.string.transaction_category_label), name) { open(Sheet.CATEGORY) }
        }
        if (state.categoryCleared) item { Notice(stringResource(R.string.transaction_category_cleared)) }
    }
    item {
        val names = state.tagIds.mapNotNull(state.names.tags::get).sortedWith(TAG_ORDER)
        ValueField(stringResource(R.string.transaction_tags_label), names.joinToString(", ")) { open(Sheet.TAGS) }
    }
    item {
        PlainField(stringResource(R.string.transaction_note_label), state.description, singleLine = false) {
            actions.onTextChange(EditField.DESCRIPTION, it)
        }
    }
    if (!transfer) {
        item {
            PlainField(
                stringResource(R.string.transaction_bank_ref_label),
                state.bankRef,
                singleLine = true,
                imeAction = ImeAction.Done,
            ) { actions.onTextChange(EditField.BANK_REF, it) }
        }
    }
}

/** Tag names in the order the chips and the field show them: ignoring case, then exactly (a total order). */
private val TAG_ORDER: Comparator<String> = String.CASE_INSENSITIVE_ORDER.thenBy { it }

/**
 * The transaction form wired to its [TransactionEditViewModel]: the only place that touches the ViewModel. When
 * the transaction is saved it calls [TransactionEditNavigation.onDone] once; when the one asked for does not exist
 * (or cannot be edited here) it calls [TransactionEditNavigation.onNotFound] once. The caller navigates away in
 * both cases.
 *
 * @param services the open object graph the ViewModel is built over.
 * @param clock gives the clock for "now" and the zone, read when the form opens.
 * @param request what the route opened the form with (untrusted text).
 * @param navigation where the form goes when it is done, not found or left.
 */
@Composable
internal fun TransactionEditRoute(
    services: AppServices,
    clock: () -> Clock,
    request: TransactionEditRequest,
    navigation: TransactionEditNavigation,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services, request) { TransactionEditViewModel.factory(services, clock, request) }
    val viewModel: TransactionEditViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latest by rememberUpdatedState(navigation)
    LaunchedEffect(state.saved) { if (state.saved) latest.onDone() }
    LaunchedEffect(state.notFound) { if (state.notFound) latest.onNotFound() }
    val actions =
        remember(viewModel) {
            TransactionEditActions(
                onTypeChange = viewModel::onTypeChange,
                onTextChange = viewModel::onTextChange,
                onDateChange = viewModel::onDateChange,
                picks =
                    TransactionPickActions(
                        viewModel::onAccountPick,
                        viewModel::onToAccountPick,
                        viewModel::onCategoryPick,
                        viewModel::onTagsDone,
                    ),
                save = viewModel::submit,
            )
        }
    TransactionEditScreen(state, actions, onBack = { latest.onBack() }, modifier = modifier)
}
