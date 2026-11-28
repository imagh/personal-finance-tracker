package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.tag.TagService

/**
 * What the transaction form reads from the account, category and tag services when it opens: the lists its
 * pickers offer and the names it shows. One class so the ViewModel stays within the constructor-size limit
 * and the reads are in one place; reads only, nothing is written or logged.
 *
 * @param accounts the accounts service.
 * @param categories the categories service.
 * @param tags the tags service.
 */
internal class EditLookups(
    private val accounts: AccountService,
    private val categories: CategoryService,
    private val tags: TagService,
) {
    /** What the pickers offer: only choices that can be made now, in core's order. */
    suspend fun choices(): EditChoices =
        EditChoices(
            accounts = accounts.active(),
            expenseCategories = categories.pickable(CategoryKind.EXPENSE),
            incomeCategories = categories.pickable(CategoryKind.INCOME),
            tags = tags.lists(),
        )

    /** The names the form shows: every account, category and tag, archived ones included. */
    suspend fun names(): NameLookups = NameLookups.read(accounts, categories, tags)
}
