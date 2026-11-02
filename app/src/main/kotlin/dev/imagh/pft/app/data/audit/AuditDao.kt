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
}
