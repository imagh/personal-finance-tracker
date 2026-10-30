package dev.imagh.pft.app.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The database-level guarantee that the audit trail is append-only (INT-2, plan §3.2 decision 3).
 *
 * Room does not model triggers, so they are plain SQL, applied by [callback]. [PftDatabase.create]
 * attaches that callback, and it is the only way a database is built, so no caller can forget it.
 * The callback re-installs the triggers on **every open** (dropping any trigger of the same name
 * first), so a database whose triggers were lost or replaced is repaired rather than silently
 * accepting edits to the trail. `audit_entry` exists from schema v1; later migrations never touch
 * that table, and `MigrationTestSupport` asserts in every migration test that the triggers survive
 * the migration itself (the repair on open is a safety net, not a substitute). A migration that
 * must rebuild `audit_entry` has to re-create them in the same migration.
 *
 * What this does not stop: `DROP TABLE audit_entry`, or opening the file with another tool.
 * Tamper-resistance against someone holding the key is out of scope; the encrypted file (A4b) is
 * what protects against everyone else.
 *
 * The abort messages name the table only, never row content (CLAUDE.md §3.7).
 */
object AuditTriggers {
    /** Trigger that rejects every UPDATE on `audit_entry`. */
    const val NO_UPDATE = "audit_entry_no_update"

    /** Trigger that rejects every DELETE on `audit_entry`. */
    const val NO_DELETE = "audit_entry_no_delete"

    /** Every trigger this object creates; tests compare the database against this list. */
    val TRIGGER_NAMES: List<String> = listOf(NO_UPDATE, NO_DELETE)

    /** The `CREATE TRIGGER` statements, one constant for [callback] and for the migration tests. */
    val SQL: List<String> =
        listOf(
            "CREATE TRIGGER IF NOT EXISTS $NO_UPDATE BEFORE UPDATE ON audit_entry " +
                "BEGIN SELECT RAISE(ABORT, 'audit_entry is append-only'); END",
            "CREATE TRIGGER IF NOT EXISTS $NO_DELETE BEFORE DELETE ON audit_entry " +
                "BEGIN SELECT RAISE(ABORT, 'audit_entry is append-only'); END",
        )

    /** Runs [SQL] on [db]. Idempotent; leaves an existing trigger of the same name as it is. */
    fun install(db: SupportSQLiteDatabase) = SQL.forEach { db.execSQL(it) }

    /** Drops any trigger named in [TRIGGER_NAMES] and creates it again from [SQL]: repairs a lost or replaced one. */
    private fun reinstall(db: SupportSQLiteDatabase) {
        TRIGGER_NAMES.forEach { db.execSQL("DROP TRIGGER IF EXISTS $it") }
        install(db)
    }

    /** Re-installs the triggers each time the database is opened (after any migration has run). */
    internal val callback: RoomDatabase.Callback =
        object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) = reinstall(db)
        }
}
