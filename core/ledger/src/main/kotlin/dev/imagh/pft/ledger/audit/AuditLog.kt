package dev.imagh.pft.ledger.audit

/**
 * The write side of the audit trail. Insert-only by design: there is no update and no delete.
 *
 * A swap boundary like the repositories (core defines it, `app` implements it with Room, plan
 * §3.2 decision 3). Services never call it directly; they go through
 * [dev.imagh.pft.ledger.audit.AuditedWriter] so the entry lands in the same transaction as the
 * change it describes.
 */
interface AuditLog {
    /** Appends [entry] to the trail. */
    suspend fun append(entry: AuditEntry)
}
