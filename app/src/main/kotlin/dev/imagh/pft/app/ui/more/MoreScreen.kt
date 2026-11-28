package dev.imagh.pft.app.ui.more

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.CollapsingHeaderScaffold
import dev.imagh.pft.app.ui.common.GroupedSection
import dev.imagh.pft.app.ui.common.GroupedSectionDefaults
import dev.imagh.pft.app.ui.common.LocalTabBarClearance
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

private val CHEVRON_SIZE = 24.dp

/**
 * The More tab's root (plan D8): the entry point to the lists that are not tabs of their own, today
 * **Categories** and **Tags**, in one `GroupedSection`. It has no Back button (it is a tab root, reached
 * from the tab bar) and no Accounts row (Accounts is a tab). The header subtitle is the attribution line,
 * "<name> · <phone>", once [MoreUiState.loaded] (the line Home used to show).
 *
 * Under the floating tab bar, `LocalTabBarClearance` adds the same space below the last row so the bar
 * never covers it (at large text the list can reach the bar).
 *
 * Stateless, drawn from [state] alone; [MoreRoute] wires the ViewModel.
 *
 * @param state the attribution to show.
 * @param onOpenCategories opens the categories list.
 * @param onOpenTags opens the tags list.
 */
@Composable
fun MoreScreen(
    state: MoreUiState,
    onOpenCategories: () -> Unit,
    onOpenTags: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val barClearance = LocalTabBarClearance.current
    CollapsingHeaderScaffold(
        title = stringResource(R.string.more_title),
        modifier = modifier,
        subtitle =
            if (state.loaded) stringResource(R.string.more_subtitle, state.userName, state.deviceLabel) else null,
    ) {
        item {
            GroupedSection(Modifier.padding(top = PftTheme.spacing.sm)) {
                row(onClick = onOpenCategories) { MoreRow(stringResource(R.string.more_categories)) }
                row(onClick = onOpenTags) { MoreRow(stringResource(R.string.more_tags)) }
            }
        }
        // Room below the last row for the floating tab bar (zero, and so no item, wherever it is not drawn).
        if (barClearance > 0.dp) item { Spacer(Modifier.height(barClearance)) }
    }
}

/**
 * The More tab wired to its [MoreViewModel]: the only place that touches the ViewModel.
 *
 * @param services the open object graph the identity is read through.
 * @param onOpenCategories opens the categories list.
 * @param onOpenTags opens the tags list.
 */
@Composable
fun MoreRoute(
    services: AppServices,
    onOpenCategories: () -> Unit,
    onOpenTags: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = remember(services) { MoreViewModel.factory(services) }
    val viewModel: MoreViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    MoreScreen(state, onOpenCategories, onOpenTags, modifier)
}

/** One row of the More list: its [title] and a chevron. */
@Composable
private fun MoreRow(title: String) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = {
            Icon(
                painterResource(PftSymbol.CHEVRON_RIGHT.res),
                contentDescription = null,
                modifier = Modifier.size(CHEVRON_SIZE),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        colors = GroupedSectionDefaults.listItemColors(),
    )
}
