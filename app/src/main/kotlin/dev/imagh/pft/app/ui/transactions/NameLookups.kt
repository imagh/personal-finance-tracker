package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.TagService

/**
 * The names a transaction row needs, looked up by id: the live accounts, categories and tags **archived
 * ones included** (a transaction may still point at one). Built by the ViewModel from the core services'
 * lists; the mapper never reads a repository.
 *
 * @property accounts account names.
 * @property categories the categories, whole, because a row draws a category's icon and colour as well as its name.
 * @property tags tag names.
 */
internal data class NameLookups(
    val accounts: Map<AccountId, String>,
    val categories: Map<CategoryId, Category>,
    val tags: Map<TagId, String>,
) {
    companion object {
        /**
         * The names of every account, category and tag a transaction can point at, read through the core
         * services' lists: archived ones too, deleted ones never. Shared by the Transactions tab and the
         * transaction form, so both name things the same way.
         */
        suspend fun read(
            accounts: AccountService,
            categories: CategoryService,
            tags: TagService,
        ): NameLookups {
            val accountLists = accounts.lists()
            val tagLists = tags.lists()
            val categoryLists = CategoryKind.entries.map { categories.lists(it) }
            return NameLookups(
                accounts = (accountLists.active + accountLists.archived).associate { it.meta.id to it.name },
                categories = categoryLists.flatMap { it.active + it.archived }.associateBy { it.meta.id },
                tags = (tagLists.active + tagLists.archived).associate { it.meta.id to it.name },
            )
        }
    }
}
