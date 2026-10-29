package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.EntityRepository
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import java.time.Clock
import java.time.Instant

/**
 * The one audited write path every service uses (plan §3.4): create, update, delete, restore and
 * archive a [LedgerEntity], each in a single transaction with its attribution, its audit diff and
 * its audit entry. No service writes a ledger entity or an audit row any other way (INT-2).
 *
 * What it guarantees, so a service only has to express its own rules:
 * - **Atomic** (INT-4): the write and its audit entry commit together or not at all.
 * - **Attributed** (L-8): the current user and device come from [IdentityService]; without an
 *   identity the call fails closed with [LedgerError.NotInitialised].
 * - **Owns the meta**: id, timestamps, attribution and the deleted/archived state are set here.
 *   A builder or update lambda cannot forge them.
 * - **Never silent** (INT-3): delete is a tombstone, restore is audited, hard delete does not exist.
 * - **No-op writes nothing**: an update that changes no audited field, or a lifecycle call that
 *   changes nothing, returns the current entity and writes no row and no audit entry.
 *
 * Because the diff comes from [LedgerEntity.auditFields], **every persisted business field must be
 * listed there**, under its snake_case name. Two guards back that rule: test-time, the fake
 * repositories reject an entity whose `auditFields()` is incomplete (`AuditFieldsCheck`, run on every
 * insert and update in tests); at runtime, [update] throws `IllegalStateException` if the entity
 * changed but no listed field did, rather than drop the edit or write it unaudited.
 *
 * Nests inside an outer [TransactionRunner] block, so a service that writes several entities (a
 * transfer's two legs) wraps them in one `runner.inTransaction`.
 */
class AuditedWriter(
    private val runner: TransactionRunner,
    private val identity: IdentityService,
    private val recorder: AuditRecorder,
    private val clock: Clock,
) {
    /**
     * Creates the entity [build] returns for a fresh meta with id [id], stores it with
     * [repository] and audits a CREATE.
     *
     * @return the stored entity.
     */
    suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> create(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource = AuditSource.MANUAL,
        build: (EntityMeta<Id>) -> T,
    ): T =
        runner.inTransaction {
            val by = identity.requireIdentity()
            val now = clock.instant()
            val meta = EntityMeta(id, now, now, by.user.id, by.device.id)
            val entity = build(meta).withMeta(meta)
            repository.insert(entity)
            recorder.record(entity, AuditAction.CREATE, AuditDiff.diff(null, entity), by, source)
            entity
        }

    /**
     * Applies [change] to the live entity with [id] and audits an UPDATE with the changed fields.
     * If [change] leaves the entity exactly as it was, nothing is written.
     *
     * @return the updated entity, or the unchanged one for a no-op.
     * @throws IllegalStateException if [change] altered the entity but no field listed in
     *   [LedgerEntity.auditFields] changed. That is a programming error (a persisted field missing
     *   from `auditFields()`): writing would skip the audit entry and not writing would drop the
     *   edit silently, so the call fails closed and nothing is written (CLAUDE.md §3.8).
     * @throws LedgerError.NotFound if there is no such entity.
     * @throws LedgerError.Deleted if it is deleted.
     */
    suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> update(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource = AuditSource.MANUAL,
        change: suspend (T) -> T,
    ): T =
        runner.inTransaction {
            val by = identity.requireIdentity()
            val old = notDeleted(repository, id)
            val new = change(old).withMeta(old.meta.copy(updatedAt = clock.instant()))
            val changes = AuditDiff.diff(old, new)
            if (changes.isEmpty()) {
                // Same audited fields. Anything else that differs is a field auditFields() forgot.
                check(new.withMeta(old.meta) == old) {
                    "${old.auditType} changed in a field that auditFields() does not list"
                }
                old
            } else {
                repository.update(new)
                recorder.record(new, AuditAction.UPDATE, changes, by, source)
                new
            }
        }

    /**
     * Tombstones the entity (INT-3) and audits a DELETE carrying a snapshot of its fields.
     * Already deleted: a no-op.
     *
     * @throws LedgerError.NotFound if there is no such entity.
     */
    suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> delete(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource = AuditSource.MANUAL,
    ): T =
        lifecycle(repository, id, source, AuditAction.DELETE) { old, now ->
            if (old.meta.isDeleted) null else old.meta.copy(deletedAt = now) to AuditDiff.diff(old, null)
        }

    /**
     * Brings a tombstoned entity back and audits a RESTORE carrying its fields. Not deleted: a no-op.
     *
     * @throws LedgerError.NotFound if there is no such entity.
     */
    suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> restore(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource = AuditSource.MANUAL,
    ): T =
        lifecycle(repository, id, source, AuditAction.RESTORE) { old, _ ->
            if (!old.meta.isDeleted) null else old.meta.copy(deletedAt = null) to AuditDiff.diff(null, old)
        }

    /**
     * Archives the entity (hidden from pickers, kept in history) and audits an ARCHIVE whose changes
     * are `archived_at: null -> <instant as ISO text>`.
     * Already archived: a no-op.
     *
     * @throws LedgerError.NotFound if there is no such entity.
     * @throws LedgerError.Deleted if it is deleted.
     */
    suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> archive(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource = AuditSource.MANUAL,
    ): T =
        lifecycle(repository, id, source, AuditAction.ARCHIVE) { old, now ->
            if (old.requireNotDeleted().meta.isArchived) {
                null
            } else {
                old.meta.copy(archivedAt = now) to mapOf(ARCHIVED_AT to FieldChange(old = null, new = now.toString()))
            }
        }

    /**
     * Makes an archived entity active again and audits an UNARCHIVE whose changes are
     * `archived_at: <instant> -> null`. Not archived: a no-op.
     *
     * @throws LedgerError.NotFound if there is no such entity.
     * @throws LedgerError.Deleted if it is deleted.
     */
    suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> unarchive(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource = AuditSource.MANUAL,
    ): T =
        lifecycle(repository, id, source, AuditAction.UNARCHIVE) { old, _ ->
            val archivedAt = old.requireNotDeleted().meta.archivedAt
            if (archivedAt == null) {
                null
            } else {
                old.meta.copy(archivedAt = null) to
                    mapOf(ARCHIVED_AT to FieldChange(old = archivedAt.toString(), new = null))
            }
        }

    /**
     * Shared body of the four lifecycle calls. [transition] returns the new meta and the audit
     * changes, null when there is nothing to do, or throws to refuse.
     */
    private suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> lifecycle(
        repository: EntityRepository<T, Id>,
        id: Id,
        source: AuditSource,
        action: AuditAction,
        transition: (T, Instant) -> Pair<EntityMeta<Id>, Map<String, FieldChange>>?,
    ): T =
        runner.inTransaction {
            val by = identity.requireIdentity()
            val old = found(repository, id)
            val now = clock.instant()
            val step = transition(old, now)
            if (step == null) {
                old
            } else {
                val new = old.withMeta(step.first.copy(updatedAt = now))
                repository.update(new)
                recorder.record(new, action, step.second, by, source)
                new
            }
        }

    private suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> found(
        repository: EntityRepository<T, Id>,
        id: Id,
    ): T = repository.get(id) ?: throw LedgerError.NotFound(repository.entityType)

    private suspend fun <Id : EntityId, T : LedgerEntity<Id, T>> notDeleted(
        repository: EntityRepository<T, Id>,
        id: Id,
    ): T = found(repository, id).requireNotDeleted()

    private fun <T : LedgerEntity<*, *>> T.requireNotDeleted(): T {
        if (meta.isDeleted) throw LedgerError.Deleted(auditType)
        return this
    }

    private companion object {
        /** The audit key for the archive state: the only meta column that is audited as a field. */
        const val ARCHIVED_AT = "archived_at"
    }
}
