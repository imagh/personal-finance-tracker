package dev.imagh.pft.app.ui.accounts

import dev.imagh.pft.ledger.account.AccountType

/** What the account form can edit and save; the route wires these to the ViewModel. Back is separate. */
class AccountEditActions(
    val onNameChange: (String) -> Unit,
    val onTypeChange: (AccountType) -> Unit,
    val onLastFourChange: (String) -> Unit,
    val onIssuerChange: (String) -> Unit,
    val onAmountChange: (String) -> Unit,
    val save: () -> Unit,
)
