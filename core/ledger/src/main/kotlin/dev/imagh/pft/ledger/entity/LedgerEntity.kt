package dev.imagh.pft.ledger.entity

import dev.imagh.pft.ledger.ids.EntityId

/**
 * A user entity that carries [EntityMeta] and so can go through
 * [dev.imagh.pft.ledger.audit.AuditedWriter] (create, edit, delete, restore, archive).
 *
 * Implemented as a data class that composes the meta, e.g.
 * `data class Account(override val meta: EntityMeta<AccountId>, val name: String) :
 * LedgerEntity<AccountId, Account>`. [Self] is the implementing class itself, so
 * [withMeta] can return it without a cast.
 */
interface LedgerEntity<Id : EntityId, Self : LedgerEntity<Id, Self>> : Auditable {
    /** The shared columns. */
    val meta: EntityMeta<Id>

    override val auditId: String get() = meta.id.value

    /** A copy of this entity with [meta] replaced (`copy(meta = meta)` on a data class). */
    fun withMeta(meta: EntityMeta<Id>): Self
}
