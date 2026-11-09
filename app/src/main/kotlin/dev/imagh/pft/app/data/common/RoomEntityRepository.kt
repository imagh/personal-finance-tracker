package dev.imagh.pft.app.data.common

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.EntityId

/**
 * The shared Room implementation of core's [EntityRepository] (plan §3.4): get, getLive, insert
 * and update for any ledger entity, given its [dao], its kind and its [mapper]. A concrete
 * repository extends it with the entity's own queries, or is used as is when it has none.
 *
 * Holds no business logic. It enforces two things that would otherwise corrupt data silently:
 * an entity of another kind is refused, and [update] fails when no row matched.
 *
 * @param entityType the entities' `auditType` (`"account"`), reported by [entityType].
 */
open class RoomEntityRepository<T : LedgerEntity<Id, T>, E, Id : EntityId>(
    private val dao: BaseDao<E>,
    override val entityType: String,
    private val mapper: EntityMapper<T, E>,
) : EntityRepository<T, Id> {
    override suspend fun get(id: Id): T? = dao.byId(id.value)?.let(mapper::toModel)

    override suspend fun getLive(id: Id): T? = get(id)?.takeUnless { it.meta.isDeleted }

    override suspend fun insert(entity: T) = dao.insert(mapper.toRow(requireKind(entity)))

    override suspend fun update(entity: T) {
        // The message names the kind only, never the id or any value.
        check(dao.update(mapper.toRow(requireKind(entity))) == 1) { "No stored $entityType to update" }
    }

    private fun requireKind(entity: T): T {
        require(entity.auditType == entityType) { "Repository for $entityType got an entity of another kind" }
        return entity
    }
}
