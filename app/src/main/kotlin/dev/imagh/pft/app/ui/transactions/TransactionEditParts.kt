package dev.imagh.pft.app.ui.transactions

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.AccountPicker
import dev.imagh.pft.app.ui.common.CategoryPicker
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.SelectField
import dev.imagh.pft.app.ui.common.TagPicker
import dev.imagh.pft.app.ui.common.message
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.transaction.TransactionType

/** The open picker sheet, if any. */
@Composable
internal fun Sheets(
    state: TransactionEditUiState,
    actions: TransactionEditActions,
    sheet: Sheet,
    close: () -> Unit,
) {
    val picks = actions.picks
    when (sheet) {
        Sheet.NONE -> Unit
        Sheet.ACCOUNT -> AccountPicker(state.choices.accounts, state.accountId, picks.onAccountPick, close)
        Sheet.TO_ACCOUNT -> AccountPicker(state.choices.accounts, state.toAccountId, picks.onToAccountPick, close)
        Sheet.CATEGORY ->
            CategoryPicker(categoriesFor(state), state.categoryId, picks.onCategoryPick, close)
        Sheet.TAGS -> TagPicker(state.choices.tags, state.tagIds, picks.onTagsDone, close)
    }
}

/** The categories the picker offers: those of the type's kind. */
internal fun categoriesFor(state: TransactionEditUiState): List<Category> =
    if (state.type == TransactionType.INCOME) state.choices.incomeCategories else state.choices.expenseCategories

/** An account field: the account's name (archived ones too, for a transaction already on one), or empty. */
@Composable
internal fun AccountField(
    label: String,
    id: AccountId?,
    state: TransactionEditUiState,
    error: FieldError?,
    onClick: () -> Unit,
) {
    val name = id?.let { state.names.accounts[it] ?: stringResource(R.string.transaction_unknown_account) }.orEmpty()
    ValueField(label, name, error, onClick)
}

/** A field that shows [value] (nothing when empty) and opens a picker. */
@Composable
internal fun ValueField(
    label: String,
    value: String,
    error: FieldError? = null,
    onClick: () -> Unit,
) {
    SelectField(
        label = label,
        value = value,
        spoken = if (value.isEmpty()) label else "$label, $value",
        trailing = PftSymbol.EXPAND_MORE,
        onClick = onClick,
        error = error,
    )
}

/** The types as M3 single-choice segmented buttons under a "Type" caption (the account form's idiom). */
@Composable
internal fun TypeSelector(
    selected: TransactionType,
    options: List<TransactionType>,
    onSelect: (TransactionType) -> Unit,
) {
    val spacing = PftTheme.spacing
    Column(Modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.sm)) {
        Text(
            stringResource(R.string.transaction_type_label),
            modifier = Modifier.padding(bottom = spacing.sm),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            options.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = type == selected,
                    onClick = { onSelect(type) },
                    modifier = Modifier.fillMaxHeight(),
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                    icon = {},
                ) {
                    Text(
                        stringResource(typeLabel(type)),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

internal fun typeLabel(type: TransactionType): Int =
    when (type) {
        TransactionType.EXPENSE -> R.string.money_expense
        TransactionType.INCOME -> R.string.money_income
        TransactionType.TRANSFER -> R.string.money_transfer
    }

/** A text field that never alters what is typed (no capitalisation): the note and the bank reference. */
@Composable
internal fun PlainField(
    label: String,
    state: FieldState,
    singleLine: Boolean,
    imeAction: ImeAction = ImeAction.Next,
    onTextChange: (String) -> Unit,
) {
    val spacing = PftTheme.spacing
    OutlinedTextField(
        value = state.text,
        onValueChange = onTextChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.gutter, vertical = spacing.sm),
        label = { Text(label) },
        isError = state.error != null,
        supportingText = state.error?.let { error -> { Text(error.message()) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else NOTE_MIN_LINES,
        maxLines = if (singleLine) 1 else NOTE_MAX_LINES,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = imeAction),
    )
}

private const val NOTE_MIN_LINES = 2
private const val NOTE_MAX_LINES = 5

/** A polite live-region line announced when it appears, without moving focus: a notice, or (with [error]) a failure. */
@Composable
internal fun Notice(
    text: String,
    error: Boolean = false,
) {
    Text(
        text,
        modifier =
            Modifier
                .padding(horizontal = PftTheme.spacing.gutter, vertical = PftTheme.spacing.sm)
                .semantics { liveRegion = LiveRegionMode.Polite },
        color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
    )
}
