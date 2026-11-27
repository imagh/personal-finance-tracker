package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

/** The drawn size of the overflow icon (the Material Symbols optical size; the button is 48dp). */
private val ROW_ICON_SIZE = 24.dp

/**
 * The one screen every entity list is (plan D11): a `CollapsingHeaderScaffold` with a Back button, the
 * **active** rows in a [GroupedSection], a collapsed **"Archived (n)"** section under them, an
 * [EmptyState] when there is nothing at all, an add button, and a snackbar host. Named consumers:
 * the accounts, categories and tags lists in slice B, and later loans and goals.
 *
 * It knows nothing about what the rows are: a consumer maps its entities to [EntityRow]s (its
 * ViewModel's state, through a mapper such as `AccountRowMapper`) and decides what each row's
 * [RowAction]s do. There is no generic list ViewModel or base class on purpose.
 *
 * - **Rows.** Each row is an M3 `ListItem` with an optional leading badge ([RowGlyph]), a title, an
 *   optional supporting line and, when it has [EntityRow.actions], a trailing overflow button that
 *   opens a menu of exactly those actions. An [EntityRow.indent] row is inset by one spacing step.
 *   A row is tappable only when it has an [EntityRow.onClick].
 * - **Archived.** Shown only when there are archived rows, collapsed until the user taps the header
 *   (which says "Expanded" or "Collapsed" to TalkBack). The expanded state survives rotation.
 * - **Empty.** Once [loaded], with no active and no archived rows the [emptyMessage] shows, with the add action as
 *   a button when there is one.
 * - **Add.** [add] is both the floating action button (a labelled `+`) and the empty state's button.
 *   Null hides both, for a list whose add form does not exist yet.
 * - **Tab bar.** Under the floating tab bar, `LocalTabBarClearance` lifts the add button (and so the
 *   snackbar, which the scaffold keeps above it) and adds the same space below the last row, so the bar
 *   covers neither. It is zero wherever the bar is not drawn.
 *
 * @param title the screen name.
 * @param active the rows to offer, in display order.
 * @param archived the archived rows, in display order.
 * @param emptyMessage what the empty state says ("No accounts yet").
 * @param onBack called when the header's Back is tapped, or null for no Back button: a tab-root list (the
 *   accounts tab) is reached from the tab bar, so it has nothing to go back to.
 * @param add the add action (its label is the spoken label of the `+` and the empty state's button
 *   text), or null for none.
 * @param snackbarHost where snackbars show; pass a `SnackbarHost` over the state the consumer's
 *   `UndoDeleteSnackbar` and messages use. Nothing by default.
 * @param loaded false until the consumer's first read has finished. While false, neither the rows nor
 *   the empty state are drawn (the header, Back and the add button stay), so an empty list never
 *   flashes "No accounts yet" before its rows arrive. True by default, for a list that is always known.
 * @param header an optional block drawn above the rows, such as the categories' Expense/Income switch;
 *   it shows before the first load and above the empty state. Null (the default) adds nothing.
 */
@Composable
fun EntityListScreen(
    title: String,
    active: List<EntityRow>,
    archived: List<EntityRow>,
    emptyMessage: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    add: RowAction? = null,
    snackbarHost: @Composable () -> Unit = {},
    loaded: Boolean = true,
    header: (@Composable () -> Unit)? = null,
) {
    val spacing = PftTheme.spacing
    val backLabel = stringResource(R.string.form_back)
    val barClearance = LocalTabBarClearance.current
    var archivedExpanded by rememberSaveable { mutableStateOf(false) }
    CollapsingHeaderScaffold(
        title = title,
        modifier = modifier,
        actions = { if (onBack != null) HeaderIconButton(PftSymbol.BACK, backLabel, onBack) },
        floatingActionButton = {
            if (add != null) {
                FloatingActionButton(onClick = add.onClick, modifier = Modifier.padding(bottom = barClearance)) {
                    Icon(painterResource(PftSymbol.ADD.res), contentDescription = add.label)
                }
            }
        },
        snackbarHost = snackbarHost,
    ) {
        if (header != null) item(key = HEADER_KEY) { header() }
        if (!loaded) return@CollapsingHeaderScaffold
        if (active.isEmpty() && archived.isEmpty()) {
            item { EmptyState(emptyMessage, actionLabel = add?.label, onAction = add?.onClick) }
        }
        if (active.isNotEmpty()) {
            item(key = ACTIVE_KEY) { EntitySection(active, Modifier.padding(top = spacing.sm)) }
        }
        if (archived.isNotEmpty()) {
            item(key = ARCHIVED_HEADER_KEY) {
                ArchivedHeader(archived.size, archivedExpanded) { archivedExpanded = !archivedExpanded }
            }
            if (archivedExpanded) {
                item(key = ARCHIVED_KEY) { EntitySection(archived) }
            }
        }
        // Room below the last row, so neither the floating button nor the tab bar covers it.
        val roomBelow = (if (add != null) spacing.huge + spacing.huge else 0.dp) + barClearance
        if (roomBelow > 0.dp) item(key = FAB_CLEARANCE_KEY) { Spacer(Modifier.height(roomBelow)) }
    }
}

private const val HEADER_KEY = "entity-list-header"
private const val ACTIVE_KEY = "entity-list-active"
private const val ARCHIVED_HEADER_KEY = "entity-list-archived-header"
private const val ARCHIVED_KEY = "entity-list-archived"
private const val FAB_CLEARANCE_KEY = "entity-list-fab-clearance"

/** One [GroupedSection] island of [rows]. */
@Composable
private fun EntitySection(
    rows: List<EntityRow>,
    modifier: Modifier = Modifier,
) {
    GroupedSection(modifier) {
        rows.forEach { entity ->
            row(onClick = entity.onClick) { key(entity.key) { EntityRowContent(entity) } }
        }
    }
}

@Composable
private fun EntityRowContent(entity: EntityRow) {
    val inset = if (entity.indent) PftTheme.spacing.xl else 0.dp
    ListItem(
        headlineContent = { Text(entity.title) },
        modifier = Modifier.padding(start = inset),
        supportingContent = entity.supporting?.let { text -> { Text(text) } },
        leadingContent =
            entity.glyph?.let { glyph -> { CategoryGlyph(glyph.source, glyph.label, glyph.colourKey) } },
        trailingContent = if (entity.actions.isEmpty()) null else ({ RowMenu(entity) }),
        colors = GroupedSectionDefaults.listItemColors(),
    )
}

/** The overflow button of [entity] and the menu of exactly its actions. */
@Composable
private fun RowMenu(entity: EntityRow) {
    var open by rememberSaveable { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(
                painterResource(PftSymbol.OVERFLOW.res),
                contentDescription = stringResource(R.string.entity_row_actions, entity.title),
                modifier = Modifier.size(ROW_ICON_SIZE),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            entity.actions.forEach { action ->
                DropdownMenuItem(
                    text = { Text(action.label) },
                    onClick = {
                        open = false
                        action.onClick()
                    },
                )
            }
        }
    }
}

/** The "Archived (n)" toggle row: at least 48dp tall, a button that announces whether it is open. */
@Composable
private fun ArchivedHeader(
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val spacing = PftTheme.spacing
    val state =
        stringResource(if (expanded) R.string.entity_list_expanded else R.string.entity_list_collapsed)
    Row(
        Modifier
            .padding(top = spacing.lg)
            .fillMaxWidth()
            .heightIn(min = spacing.huge)
            .clickable(role = Role.Button, onClick = onToggle)
            .semantics { stateDescription = state }
            .padding(horizontal = spacing.gutter),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(R.string.entity_list_archived, count),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
        )
        Icon(
            painterResource((if (expanded) PftSymbol.EXPAND_LESS else PftSymbol.EXPAND_MORE).res),
            contentDescription = null,
            modifier = Modifier.size(ROW_ICON_SIZE),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
