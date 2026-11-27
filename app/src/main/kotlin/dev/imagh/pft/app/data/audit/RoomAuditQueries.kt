package dev.imagh.pft.app.data.audit

import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditQueries
import dev.imagh.pft.ledger.ids.TransactionId

/** The Room implementation of core's [AuditQueries]: one [AuditDao] query and the entry mapper, no logic. */
class RoomAuditQueries(
    private val dao: AuditDao,
) : AuditQueries {
    override suspend fun forTransaction(id: TransactionId): List<AuditEntry> =
        dao.forTransaction(id.value).map(AuditEntryMapper::toModel)
}
