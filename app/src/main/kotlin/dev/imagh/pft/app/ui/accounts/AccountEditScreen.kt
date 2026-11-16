package dev.imagh.pft.app.ui.accounts

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.AmountField
import dev.imagh.pft.app.ui.common.EditFormScaffold
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.NameField
import dev.imagh.pft.app.ui.common.message
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.account.AccountType

/**
 * The account create and edit form drawn from [state] alone, on the shared `EditFormScaffold`: the
 * name, the type (four segmented buttons), the optional last four digits and issuer, and the opening
 * balance, with Save in the bottom bar and Back in the header.
 *
 * - **The amount's label follows the type** (plan Q3): "Amount owed" for a credit card, "Current
 *   balance" for the others. The text is the same either way; only the saved sign differs.
 * - **Each field shows its own error** from [state]; the last-four field is a number-keyboard text
 *   field that, like every form field, never alters what is typed.
 * - **Nothing is drawn before an edit has loaded** ([AccountEditUiState.loaded]) and nothing once the
 *   account was not found, and Save is disabled in both, and while saving.
 * - After an unexpected failure ([AccountEditUiState.saveFailed]) a fixed "Couldn't save. Try again."
 *   line appears under the fields as a polite live region.
 *
 * It holds no state and knows no ViewModel, so tests and goldens render it from fake data;
 * [AccountEditRoute] wires the real one.
 *
 * @param state what to show.
 * @param actions what each field and Save do.
 * @param onBack called when Back is tapped.
 */
@Composable
fun AccountEditScreen(
    state: AccountEditUiState,
    actions: AccountEditActions,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    EditFormScaffold(
        title = stringResource(if (state.editing) R.string.account_edit_title else R.string.account_new_title),
        onPrimary = actions.save,
        modifier = modifier,
        saving = state.saving || !state.loaded || state.notFound,
        onBack = onBack,
    ) {
        if (!state.loaded || state.notFound) return@EditFormScaffold
        item { NameField(stringResource(R.string.account_name_label), state.name, actions.onNameChange) }
        item { TypeSelector(state.type, actions.onTypeChange) }
        item { LastFourField(state.lastFour, actions.onLastFourChange) }
        item { NameField(stringResource(R.string.account_issuer_label), state.issuer, actions.onIssuerChange) }
        item {
            val label = stringResource(amountLabelRes(state.type))
            AmountField(label, state.amount, actions.onAmountChange, imeAction = ImeAction.Done)
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

/** "Amount owed" for a credit card (what is owed is stored negated), "Current balance" for the others. */
@StringRes
private fun amountLabelRes(type: AccountType): Int =
    if (type == AccountType.CREDIT_CARD) R.string.account_owed_label else R.string.account_balance_label

/** The four account types as M3 single-choice segmented buttons under a "Type" caption. */
@Composable
private fun TypeSelector(
    selected: AccountType,
    onSelect: (AccountType) -> Unit,
) {
    val spacing = PftTheme.spacing
    val types = AccountType.entries
    Column(Modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.sm)) {
        Text(
            stringResource(R.string.account_type_label),
            modifier = Modifier.padding(bottom = spacing.sm),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        // Equal-height segments: at a large font scale one label may wrap, and the others must follow it.
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            types.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = type == selected,
                    onClick = { onSelect(type) },
                    modifier = Modifier.fillMaxHeight(),
                    shape = SegmentedButtonDefaults.itemShape(index, types.size),
                    // No check mark: it would take the width four short labels need.
                    icon = {},
                ) {
                    // labelMedium keeps four segments narrow; a label that still wraps ("Credit card") is centred.
                    Text(
                        stringResource(typeLabelRes(type)),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

/**
 * The last-four-digits field: a text field with a number keyboard. It never changes what is typed
 * (no filtering, no length cap): a wrong value is reported afterwards by core's rule.
 */
@Composable
private fun LastFourField(
    state: FieldState,
    onTextChange: (String) -> Unit,
) {
    val spacing = PftTheme.spacing
    OutlinedTextField(
        value = state.text,
        onValueChange = onTextChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.sm),
        label = { Text(stringResource(R.string.account_last_four_label)) },
        isError = state.error != null,
        supportingText = state.error?.let { error -> { Text(error.message()) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
    )
}

/**
 * The account form wired to its [AccountEditViewModel]: the only place that touches the ViewModel.
 * When the account is saved it calls [onDone] once; when the account asked for does not exist it
 * calls [onNotFound] once. The caller navigates away in both cases.
 *
 * @param services the open object graph the accounts service is taken from.
 * @param accountId the account to edit as it arrived in the route (untrusted text), or null to create.
 * @param onDone called when the account is saved.
 * @param onNotFound called when [accountId] is unknown, deleted or not an id.
 * @param onBack called when Back is tapped.
 */
@Composable
fun AccountEditRoute(
    services: AppServices,
    accountId: String?,
    onDone: () -> Unit,
    onNotFound: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services, accountId) { AccountEditViewModel.factory(services, accountId) }
    val viewModel: AccountEditViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestOnDone by rememberUpdatedState(onDone)
    val latestOnNotFound by rememberUpdatedState(onNotFound)
    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedEffect(state.saved) { if (state.saved) latestOnDone() }
    LaunchedEffect(state.notFound) { if (state.notFound) latestOnNotFound() }
    val actions =
        remember(viewModel) {
            AccountEditActions(
                onNameChange = viewModel::onNameChange,
                onTypeChange = viewModel::onTypeChange,
                onLastFourChange = viewModel::onLastFourChange,
                onIssuerChange = viewModel::onIssuerChange,
                onAmountChange = viewModel::onAmountChange,
                save = viewModel::submit,
            )
        }
    AccountEditScreen(state, actions, onBack = { latestOnBack() }, modifier = modifier)
}
