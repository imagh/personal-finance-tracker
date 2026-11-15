package dev.imagh.pft.app.ui.accounts

import dev.imagh.pft.ledger.ids.AccountId

/** What the accounts list can do to an account; the route wires these to the ViewModel. */
class AccountsActions(
    val archive: (AccountId) -> Unit,
    val unarchive: (AccountId) -> Unit,
    val delete: (AccountId) -> Unit,
)
