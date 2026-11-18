package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryRepository
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * The in-memory [CategoryRepository]: the generic fake plus the category queries. [allLive] returns
 * the rows **newest first**, deliberately not in insertion order: the contract promises no order
 * (Room returns none without an `ORDER BY`), so a service test that passes only because the fake
 * happens to return insertion order is exposed here.
 */
class InMemoryCategoryRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<Category, CategoryId>(ledger, "category"),
    CategoryRepository {
    override suspend fun allLive(): List<Category> = rows.all().filterNot { it.meta.isDeleted }.reversed()

    override suspend fun bySystemKey(key: SystemCategory): Category? =
        rows.all().firstOrNull { it.systemKey == key && !it.meta.isDeleted }
}
