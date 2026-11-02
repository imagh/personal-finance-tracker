package dev.imagh.pft.app.data.audit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table `audit_entry`: one immutable row of the audit trail (INT-2). `changes_json` holds the
 * field-level old and new values (see `AuditEntryMapper`); `at` is epoch milliseconds UTC.
 *
 * Deliberately **no foreign keys**: the trail must stay writable and intact whatever happens to
 * the rows it describes, and it records attribution as plain ids. UPDATE and DELETE are rejected
 * by triggers (`AuditTriggers`), so this class is only ever inserted and read.
 */
@Entity(
    tableName = "audit_entry",
    indices = [Index(value = ["entity_type", "entity_id"])],
)
data class AuditEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "entity_type")
    val entityType: String,
    @ColumnInfo(name = "entity_id")
    val entityId: String,
    @ColumnInfo(name = "action")
    val action: String,
    @ColumnInfo(name = "changes_json")
    val changesJson: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "at")
    val at: Long,
)
