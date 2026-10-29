package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.matchers.shouldHaveCompleteAuditFields

/**
 * The in-memory [EntityRepository]: the base every fake entity repository extends with its own
 * queries (plan §3.4), backed by an [InMemoryEntityStore] that takes part in [ledger]'s
 * transactions. [entityType] is the entity's `auditType`. Use it directly when an entity has no
 * queries of its own.
 *
 * Every [insert] and [update] first checks that the entity's `auditFields()` is complete (see
 * [dev.imagh.pft.ledger.matchers.AuditFieldsCheck]), so a service test that writes an entity with
 * a field missing from `auditFields()` fails with an `AssertionError`, whichever service wrote it.
 */
open class InMemoryEntityRepository<T : LedgerEntity<Id, T>, Id : EntityId>(
    ledger: InMemoryLedgerStore,
    override val entityType: String,
) : EntityRepository<T, Id> {
    /**
     * Whether writes check `auditFields()` completeness. Leave it `true`; switch it off only to
     * test `AuditedWriter`'s own runtime guard against an incomplete entity.
     */
    var checkAuditFields: Boolean = true

    /** The table; subclasses read it to implement their own queries. */
    protected val rows = ledger.newStore<T, Id> { it.meta.id }

    /** Every stored entity (tombstoned and archived ones included), in insertion order. */
    fun all(): List<T> = rows.all()

    override suspend fun get(id: Id): T? = rows.get(id)

    override suspend fun getLive(id: Id): T? = rows.get(id)?.takeUnless { it.meta.isDeleted }

    override suspend fun insert(entity: T) {
        verify(entity)
        rows.insert(entity)
    }

    override suspend fun update(entity: T) {
        verify(entity)
        rows.update(entity)
    }

    private fun verify(entity: T) {
        if (checkAuditFields) entity.shouldHaveCompleteAuditFields()
    }
}
