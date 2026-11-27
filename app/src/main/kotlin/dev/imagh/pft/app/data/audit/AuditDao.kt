package dev.imagh.pft.app.data.audit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * The audit trail's DAO: **insert and select only** (INT-2). There is deliberately no update or
 * delete method here, and the database's triggers would abort one anyway.
 */
@Dao
interface AuditDao {
    /** Appends [entry]; fails if the id exists (Room's default ABORT, never a replace). */
    @Insert
    suspend fun insert(entry: AuditEntryEntity)

    /** Every entry about one record, oldest first (ties broken by the time-sortable id). */
    @Query("SELECT * FROM audit_entry WHERE entity_type = :entityType AND entity_id = :entityId ORDER BY at, id")
    suspend fun forEntity(
        entityType: String,
        entityId: String,
    ): List<AuditEntryEntity>

    /**
     * Every entry about transaction [transactionId] and about each of its lines, **removed (tombstoned) lines
     * included**: the line ids come from `transaction_line` itself, which never loses a row. Oldest first,
     * ties broken by the time-sortable entry id, so the order is total. Bound parameter only.
     */
    @Query(
        "SELECT * FROM audit_entry WHERE (entity_type = 'ledger_transaction' AND entity_id = :transactionId) " +
            "OR (entity_type = 'transaction_line' AND entity_id IN " +
            "(SELECT id FROM transaction_line WHERE transaction_id = :transactionId)) ORDER BY at, id",
    )
    suspend fun forTransaction(transactionId: String): List<AuditEntryEntity>
}
