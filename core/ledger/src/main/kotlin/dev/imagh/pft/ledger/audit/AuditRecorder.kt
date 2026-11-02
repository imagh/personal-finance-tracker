package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.Uuid7
import java.time.Clock

/**
 * Builds and appends one [AuditEntry]: the single place an entry's id, timestamp and attribution
 * are filled in. Shared by [AuditedWriter] (ledger entities), `IdentityService` and
 * `SettingsService` (records without [dev.imagh.pft.ledger.entity.EntityMeta]).
 *
 * Callers run it inside a [dev.imagh.pft.ledger.tx.TransactionRunner] block so the entry commits
 * or rolls back together with the change it describes.
 */
class AuditRecorder(
    private val log: AuditLog,
    private val ids: Uuid7,
    private val clock: Clock,
) {
    /**
     * Appends an entry saying [by] did [action] to [subject], with the field [changes].
     * [source] defaults to a user action.
     */
    suspend fun record(
        subject: Auditable,
        action: AuditAction,
        changes: Map<String, FieldChange>,
        by: Identity,
        source: AuditSource = AuditSource.MANUAL,
    ) {
        log.append(
            AuditEntry(
                id = AuditEntryId(ids.next()),
                entityType = subject.auditType,
                entityId = subject.auditId,
                action = action,
                changes = changes,
                userId = by.user.id,
                deviceId = by.device.id,
                source = source,
                at = clock.instant(),
            ),
        )
    }
}
