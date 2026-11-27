package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.ids.TransactionId

/**
 * The read side of the audit trail for one transaction (slice C D5): the persistence boundary behind the
 * detail screen's history. Implemented with Room in `app`.
 */
interface AuditQueries {
    /**
     * Every audit entry about transaction [id] and about each of its lines, **removed lines included**
     * (their entries are history, not live data), oldest first. Ties on time are broken by the entry's
     * own id, so the order is total. Entries of any other transaction or its lines are never returned.
     * An unknown id gives an empty list.
     */
    suspend fun forTransaction(id: TransactionId): List<AuditEntry>
}
