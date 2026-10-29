package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.ids.AuditEntryId

/** The in-memory [AuditLog]: append-only, and restored by the fake runner on rollback. */
class InMemoryAuditLog internal constructor(
    ledger: InMemoryLedgerStore,
) : AuditLog {
    private val store = ledger.newStore<AuditEntry, AuditEntryId> { it.id }

    /** Every entry appended so far, oldest first. */
    val entries: List<AuditEntry> get() = store.all()

    override suspend fun append(entry: AuditEntry) = store.insert(entry)
}
