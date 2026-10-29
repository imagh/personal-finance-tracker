package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.EntityId

/**
 * The in-memory [EntityRepository]: the base every fake entity repository extends with its own
 * queries (plan §3.4), backed by an [InMemoryEntityStore] that takes part in [ledger]'s
 * transactions. [entityType] is the entity's `auditType`. Use it directly when an entity has no queries of its own.
 */
open class InMemoryEntityRepository<T : LedgerEntity<Id, T>, Id : EntityId>(
    ledger: InMemoryLedgerStore,
    override val entityType: String,
) : EntityRepository<T, Id> {
    /** The table; subclasses read it to implement their own queries. */
    protected val rows = ledger.newStore<T, Id> { it.meta.id }

    /** Every stored entity (tombstoned and archived ones included), in insertion order. */
    fun all(): List<T> = rows.all()

    override suspend fun get(id: Id): T? = rows.get(id)

    override suspend fun getLive(id: Id): T? = rows.get(id)?.takeUnless { it.meta.isDeleted }

    override suspend fun insert(entity: T) = rows.insert(entity)

    override suspend fun update(entity: T) = rows.update(entity)
}
