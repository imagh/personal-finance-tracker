package dev.imagh.pft.app.ui.accounts

import dev.imagh.pft.ledger.ids.AccountId

/**
 * What the accounts list can do; the route wires these to the ViewModel and to navigation.
 *
 * @property archive archives an account.
 * @property unarchive makes an archived account active again.
 * @property delete deletes an account (with an Undo).
 * @property add opens the create form.
 * @property edit opens an account's edit form.
 */
class AccountsActions(
    val archive: (AccountId) -> Unit,
    val unarchive: (AccountId) -> Unit,
    val delete: (AccountId) -> Unit,
    val add: () -> Unit,
    val edit: (AccountId) -> Unit,
)

/**
 * Where the accounts list can go; the route wires these to the navigation host. There is no "back": the
 * list is the Accounts tab's root, reached from the tab bar.
 *
 * @property add opens the create form.
 * @property edit opens an account's edit form.
 */
class AccountsNavigation(
    val add: () -> Unit,
    val edit: (AccountId) -> Unit,
)
