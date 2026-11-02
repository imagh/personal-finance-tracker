package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import java.time.Instant

/** What happened to a record. */
enum class AuditAction {
    /** A new record. */
    CREATE,

    /** A change to a record's fields. */
    UPDATE,

    /** A record was tombstoned (soft delete). */
    DELETE,

    /** A tombstoned record was brought back. */
    RESTORE,

    /** A record was archived. */
    ARCHIVE,

    /** An archived record was brought back. */
    UNARCHIVE,
}

/** What caused the change. */
enum class AuditSource {
    /** The user, in the UI. */
    MANUAL,

    /** The app itself (for example, seeding the default categories on first run). */
    SYSTEM,

    /** The recurring engine. */
    RECURRING,
}

/**
 * One immutable row of the audit trail (INT-2): who changed what, when, how.
 *
 * Immutability is enforced where the data lives: the Room table rejects updates and deletes with
 * triggers and its DAO only inserts and selects. [changes] holds field-level old and new values
 * as text; `app` serialises it. The trail may hold names and amounts, so it is encrypted at rest
 * like every other table and never written to logs.
 *
 * @param id the row's own id (a UUIDv7, so rows sort in creation order).
 * @param entityType the kind of record, see [dev.imagh.pft.ledger.entity.Auditable.auditType].
 * @param entityId the record's id as text.
 * @param action what happened.
 * @param changes the changed fields, values as text (instants as ISO-8601). ARCHIVE and UNARCHIVE
 *   carry `archived_at`; every other lifecycle action carries a field snapshot (see [AuditedWriter]).
 * @param userId who did it.
 * @param deviceId on which device.
 * @param source what caused it.
 * @param at when.
 */
data class AuditEntry(
    val id: AuditEntryId,
    val entityType: String,
    val entityId: String,
    val action: AuditAction,
    val changes: Map<String, FieldChange>,
    val userId: UserId,
    val deviceId: DeviceId,
    val source: AuditSource,
    val at: Instant,
)
