package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.transaction.TransactionType
import java.time.LocalDate

/** What the form's pickers choose: the account, a transfer's destination, the category and the tags. */
internal class TransactionPickActions(
    val onAccountPick: (AccountId) -> Unit,
    val onToAccountPick: (AccountId) -> Unit,
    val onCategoryPick: (CategoryId) -> Unit,
    val onTagsDone: (Set<TagId>) -> Unit,
)

/** What the transaction form can edit and save; the route wires these to the ViewModel. Back is separate. */
internal class TransactionEditActions(
    val onTypeChange: (TransactionType) -> Unit,
    val onTextChange: (EditField, String) -> Unit,
    val onDateChange: (LocalDate) -> Unit,
    val picks: TransactionPickActions,
    val save: () -> Unit,
)

/**
 * Where the transaction form can go.
 *
 * @property onDone called once when the transaction is saved.
 * @property onNotFound called once when the transaction asked for cannot be opened.
 * @property onBack called when Back is tapped.
 */
internal class TransactionEditNavigation(
    val onDone: () -> Unit,
    val onNotFound: () -> Unit,
    val onBack: () -> Unit,
)
