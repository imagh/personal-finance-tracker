package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.accounts.AccountRowMapper
import dev.imagh.pft.app.ui.accounts.typeLabelRes
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.ids.AccountId

/** The drawn size of the selected row's check mark. */
private val CHECK_SIZE = 24.dp

/**
 * The sheet that asks "which account?": the account chip of the transaction form (C4), and later the
 * budget and loan forms, open it. It is built on [PftBottomSheet] and is stateless: it draws the
 * [accounts] it is given and reports what happened. There is no ViewModel here; the consumer's
 * ViewModel loads `AccountService.active()` (live, not archived, sorted by name ignoring case) and
 * passes that list, so **only active accounts are ever offered and their order is the service's**.
 *
 * - **Rows.** The list is one selectable group (TalkBack presents the rows as a set of choices) and each
 *   account is a radio-style row: its type badge (the same symbol and colour as the accounts list, from
 *   `AccountRowMapper`, silent because the supporting line speaks the type), its name, and the supporting line of the
 *   list ("Bank · HDFC · ••1234") so two similar names can be told apart.
 * - **Selection.** The row of [selected] has `selected` semantics (TalkBack says "selected") and a
 *   check mark. A [selected] id that is not among [accounts] (the transaction's account was archived
 *   since) marks nothing.
 * - **Picking.** A tap calls [onPick] with the account's id, once, and then slides the sheet out and
 *   calls [onDismiss]. Taps while the sheet is sliding out are ignored, so a double tap cannot pick
 *   two accounts.
 * - **Empty.** With no accounts the sheet says so and points to More, where accounts are added.
 * - **Dismissing without picking** (Back, the scrim, a swipe down) calls [onDismiss] only.
 *
 * The consumer shows it with `if (open) AccountPicker(...)` and sets `open = false` in [onDismiss].
 * It logs nothing: an account's name and last four digits are never logged.
 *
 * @param accounts what to offer, in display order.
 * @param selected the id of the account that is chosen now, or null for none.
 * @param onPick called with the tapped account's id.
 * @param onDismiss called once when the sheet has closed, whether or not something was picked.
 */
@Composable
fun AccountPicker(
    accounts: List<Account>,
    selected: AccountId?,
    onPick: (AccountId) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PftBottomSheet(
        title = stringResource(R.string.account_picker_title),
        onDismiss = onDismiss,
        modifier = modifier,
    ) { close ->
        if (accounts.isEmpty()) {
            EmptyState(stringResource(R.string.account_picker_empty))
        } else {
            var picked by remember { mutableStateOf(false) }
            LazyColumn(Modifier.selectableGroup()) {
                items(accounts, key = { it.meta.id.value }) { account ->
                    AccountPickerRow(account, selected = account.meta.id == selected) {
                        if (!picked) {
                            picked = true
                            onPick(account.meta.id)
                            close()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountPickerRow(
    account: Account,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val typeLabel = stringResource(typeLabelRes(account.type))
    val badge = AccountRowMapper.badge(account.type)
    ListItem(
        headlineContent = { Text(account.name) },
        modifier = Modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        supportingContent = {
            Text(AccountRowMapper.supporting(typeLabel, account.issuer, account.maskedIdentifier))
        },
        leadingContent = { CategoryGlyph(GlyphSource.Symbol(badge.symbol), name = null, colourKey = badge.colourKey) },
        trailingContent =
            if (selected) {
                {
                    Icon(
                        painterResource(PftSymbol.CHECK.res),
                        contentDescription = null,
                        modifier = Modifier.size(CHECK_SIZE),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                null
            },
        colors = GroupedSectionDefaults.listItemColors(),
    )
}
