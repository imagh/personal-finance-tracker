package dev.imagh.pft.ledger.entity

import dev.imagh.pft.ledger.ids.EntityId

/**
 * The persistence boundary every entity repository extends with its own queries (plan §3.4).
 *
 * Defined in core, implemented with Room in `app` (CLAUDE.md §3.1): this is one of the few
 * genuine swap boundaries. Implementations hold no business logic and must be safe to call
 * inside a [dev.imagh.pft.ledger.tx.TransactionRunner] block.
 */
interface EntityRepository<T : LedgerEntity<Id, T>, Id : EntityId> {
    /**
     * The kind of entity this repository holds: the same text as its entities' `auditType`
     * (`"account"`). Used to name the kind in errors when no instance exists, such as
     * [dev.imagh.pft.ledger.validation.LedgerError.NotFound]. Never user content.
     */
    val entityType: String

    /** The entity with [id], **including a tombstoned one** (needed to restore it), or null. */
    suspend fun get(id: Id): T?

    /** The entity with [id] unless it is missing or tombstoned. Archived entities are returned. */
    suspend fun getLive(id: Id): T?

    /** Stores a new entity. */
    suspend fun insert(entity: T)

    /** Replaces the stored entity that has the same id. */
    suspend fun update(entity: T)
}
