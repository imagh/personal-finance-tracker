package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.ids.TagId

/**
 * Where tags are stored: [EntityRepository]'s read and write plus the one query tags need.
 * Implemented with Room in `app`; holds no business logic (the service sorts, splits and checks names).
 */
interface TagRepository : EntityRepository<Tag, TagId> {
    /**
     * Every tag that is not tombstoned, archived ones included, in no particular order (the service
     * sorts, so implementations need not).
     */
    suspend fun allLive(): List<Tag>
}
