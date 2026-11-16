package dev.imagh.pft.app.ui.more

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.CollapsingHeaderScaffold
import dev.imagh.pft.app.ui.common.GroupedSection
import dev.imagh.pft.app.ui.common.GroupedSectionDefaults
import dev.imagh.pft.app.ui.common.HeaderIconButton
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

private val CHEVRON_SIZE = 24.dp

/**
 * The More screen (plan D9): the entry point to the lists that are not tabs of their own. Today it
 * has two rows, **Accounts** and **Categories**; the Tags row is added with its screen. It is a
 * `CollapsingHeaderScaffold` with a Back button and one `GroupedSection`, reached from Home's header
 * until C4's bottom navigation bar gets a "More" tab, which will reuse this screen unchanged.
 *
 * Stateless: there is nothing to load, so there is no ViewModel and no route wrapper.
 *
 * @param onBack called when Back is tapped.
 * @param onOpenAccounts opens the accounts list.
 * @param onOpenCategories opens the categories list.
 */
@Composable
fun MoreScreen(
    onBack: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenCategories: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backLabel = stringResource(R.string.form_back)
    CollapsingHeaderScaffold(
        title = stringResource(R.string.more_title),
        modifier = modifier,
        actions = { HeaderIconButton(PftSymbol.BACK, backLabel, onBack) },
    ) {
        item {
            GroupedSection(Modifier.padding(top = PftTheme.spacing.sm)) {
                row(onClick = onOpenAccounts) { MoreRow(stringResource(R.string.more_accounts)) }
                row(onClick = onOpenCategories) { MoreRow(stringResource(R.string.more_categories)) }
            }
        }
    }
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
