package dev.imagh.pft.app.data.audit

import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog

/**
 * The Room implementation of core's [AuditLog]. Services reach it only through `AuditedWriter`
 * inside a transaction, so the entry commits with the change it describes.
 */
class RoomAuditLog(
    private val dao: AuditDao,
) : AuditLog {
    override suspend fun append(entry: AuditEntry) = dao.insert(AuditEntryMapper.toEntity(entry))
}
