package dev.imagh.pft.app.ui.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.CategoryPicker
import dev.imagh.pft.app.ui.common.EditFormScaffold
import dev.imagh.pft.app.ui.common.NameField
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

/** The size of the chevron that shows a row can be tapped. */
private val CHEVRON = 24.dp

/**
 * The category create and edit form drawn from [state] alone, on the shared `EditFormScaffold`: the
 * name, the type and the parent, the colour swatches and the icon choice, with Save in the bottom bar
 * and Back in the header.
 *
 * - **Type and parent.** The type (Expense or Income) is always shown and never edited here: the list's
 *   switch chose it. The parent row shows the parent's name or "None (top level)". When creating, the
 *   row opens [CategoryPicker] over [CategoryEditUiState.parentOptions] (top-level categories only) and
 *   a "Make top level" button clears a chosen parent. When editing, both rows are plain text that says
 *   they cannot be changed (plan D7, owner Q7): no click action, so no picker can open.
 * - **Colour and icon.** [ColourSwatches], then [IconChoice]: a Symbol | Emoji switch over the [SymbolGrid]
 *   (previewing the chosen colour on every symbol; the name's first letter shows on the "No symbol" cell as
 *   the name is typed) or the [EmojiTab] (a quick-pick, and a field to type or paste an emoji).
 * - **The name shows its error** from [state] (required, too long, or already in use).
 * - **Nothing is drawn before the form has loaded** ([CategoryEditUiState.loaded]) and nothing once the
 *   category was not found, and Save is disabled in both, and while saving.
 * - After an unexpected failure ([CategoryEditUiState.saveFailed]) a fixed "Couldn't save. Try again."
 *   line appears under the fields as a polite live region.
 *
 * It holds no state of the form and knows no ViewModel, so tests and goldens render it from fake data
 * (the only state it keeps is whether the parent picker is open); [CategoryEditRoute] wires the real one.
 *
 * @param state what to show.
 * @param actions what each field and Save do.
 * @param onBack called when Back is tapped.
 */
@Composable
fun CategoryEditScreen(
    state: CategoryEditUiState,
    actions: CategoryEditActions,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    var pickerOpen by rememberSaveable { mutableStateOf(false) }
    EditFormScaffold(
        title = stringResource(if (state.editing) R.string.category_edit_title else R.string.category_new_title),
        onPrimary = actions.save,
        modifier = modifier,
        saving = state.saving || !state.loaded || state.notFound,
        onBack = onBack,
    ) {
        if (!state.loaded || state.notFound) return@EditFormScaffold
        item { NameField(stringResource(R.string.category_name_label), state.name, actions.onNameChange) }
        item { FixedRow(stringResource(R.string.category_kind_label), stringResource(kindLabelRes(state.kind)), null) }
        item {
            val none = stringResource(R.string.category_parent_none)
            val onClick = if (state.editing) null else ({ pickerOpen = true })
            FixedRow(stringResource(R.string.category_parent_label), state.parent?.name ?: none, onClick)
        }
        if (state.parent != null && !state.editing) {
            item {
                TextButton(
                    onClick = { actions.onParentChange(null) },
                    modifier = Modifier.padding(horizontal = spacing.md),
                ) { Text(stringResource(R.string.category_parent_clear)) }
            }
        }
        item { SectionLabel(stringResource(R.string.category_colour_label)) }
        item { ColourSwatches(state.colour, actions.onColourChange) }
        item { SectionLabel(stringResource(R.string.category_icon_label)) }
        item { IconChoice(state, actions) }
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
    if (pickerOpen) {
        CategoryPicker(
            categories = state.parentOptions,
            selected = state.parent?.meta?.id,
            onPick = actions.onParentChange,
            onDismiss = { pickerOpen = false },
            topLevelOnly = true,
            title = stringResource(R.string.category_parent_picker_title),
        )
    }
}

/** A small caption above a group of choices, announced as a heading. */
@Composable
private fun SectionLabel(text: String) {
    val spacing = PftTheme.spacing
    Text(
        text,
        modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.sm).semantics { heading() },
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelLarge,
    )
}

/**
 * A labelled value row: [label] with [value] under it. With [onClick] it is a button that opens a
 * choice (with a chevron); without it the value is fixed and a note says so, and the row has no click
 * action at all.
 */
@Composable
private fun FixedRow(
    label: String,
    value: String,
    onClick: (() -> Unit)?,
) {
    ListItem(
        headlineContent = { Text(label) },
        supportingContent = {
            Column {
                Text(value)
                if (onClick == null) Text(stringResource(R.string.category_fixed_note))
            }
        },
        modifier = if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier,
        trailingContent =
            if (onClick != null) {
                {
                    Icon(
                        painterResource(PftSymbol.CHEVRON_RIGHT.res),
                        contentDescription = null,
                        modifier = Modifier.size(CHEVRON),
                    )
                }
            } else {
                null
            },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

/**
 * The category form wired to its [CategoryEditViewModel]: the only place that touches the ViewModel.
 * When the category is saved it calls [onDone] once; when the category asked for does not exist, or a
 * route argument is not valid, it calls [onNotFound] once. The caller navigates away in both cases.
 *
 * @param services the open object graph the categories service is taken from.
 * @param request the route's arguments as they arrived (untrusted text).
 * @param onDone called when the category is saved.
 * @param onNotFound called when an argument is unknown, deleted or not valid.
 * @param onBack called when Back is tapped.
 */
@Composable
fun CategoryEditRoute(
    services: AppServices,
    request: CategoryEditRequest,
    onDone: () -> Unit,
    onNotFound: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services, request) { CategoryEditViewModel.factory(services, request) }
    val viewModel: CategoryEditViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestOnDone by rememberUpdatedState(onDone)
    val latestOnNotFound by rememberUpdatedState(onNotFound)
    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedEffect(state.saved) { if (state.saved) latestOnDone() }
    LaunchedEffect(state.notFound) { if (state.notFound) latestOnNotFound() }
    val actions =
        remember(viewModel) {
            CategoryEditActions(
                onNameChange = viewModel::onNameChange,
                onColourChange = viewModel::onColourChange,
                onIconChange = viewModel::onIconChange,
                onEmojiInput = viewModel::onEmojiInput,
                onParentChange = viewModel::onParentChange,
                save = viewModel::submit,
            )
        }
    CategoryEditScreen(state, actions, onBack = { latestOnBack() }, modifier = modifier)
}
