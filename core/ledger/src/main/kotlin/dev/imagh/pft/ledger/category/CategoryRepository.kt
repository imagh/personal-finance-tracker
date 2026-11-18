package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * Where categories are stored: [EntityRepository]'s read and write plus the two queries categories
 * need. Implemented with Room in `app`; holds no business logic (sorting, pickability and sibling
 * checks are plain functions over [allLive]'s list, so they are tested without Room).
 */
interface CategoryRepository : EntityRepository<Category, CategoryId> {
    /**
     * Every category that is not tombstoned, archived ones included, in no particular order (the
     * service sorts, so implementations need not). The whole tree is small: tens of rows.
     */
    suspend fun allLive(): List<Category>

    /** The category that is [key], unless tombstoned (system categories never are), or null. */
    suspend fun bySystemKey(key: SystemCategory): Category?
}
