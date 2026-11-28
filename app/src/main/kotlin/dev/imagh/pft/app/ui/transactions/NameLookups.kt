package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId

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
)
