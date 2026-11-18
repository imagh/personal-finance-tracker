package dev.imagh.pft.app.data.category

import dev.imagh.pft.app.data.common.RoomEntityRepository
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryRepository
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.ids.CategoryId

/** The Room implementation of core's [CategoryRepository]: the shared base plus the two category queries. */
class RoomCategoryRepository(
    private val dao: CategoryDao,
) : RoomEntityRepository<Category, CategoryEntity, CategoryId>(dao, "category", CategoryMapper),
    CategoryRepository {
    override suspend fun allLive(): List<Category> = dao.allLive().map(CategoryMapper::toModel)

    override suspend fun bySystemKey(key: SystemCategory): Category? =
        dao.bySystemKey(key.name)?.let(CategoryMapper::toModel)
}
