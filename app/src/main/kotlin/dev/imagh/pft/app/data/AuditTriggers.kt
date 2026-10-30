package dev.imagh.pft.app.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The database-level guarantee that the audit trail is append-only (INT-2, plan §3.2 decision 3).
 *
 * Room does not model triggers, so they are plain SQL that [installAuditTriggers] runs when the
 * database file is first created. `audit_entry` exists from schema v1, so every database already
 * has them; later migrations never touch that table, and `MigrationTestSupport` asserts in every
 * migration test that the triggers survive. A migration that must rebuild `audit_entry` has to
 * re-create them in the same migration.
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

    /** The `CREATE TRIGGER` statements, one constant for `onCreate` and for the migration tests. */
    val SQL: List<String> =
        listOf(
            "CREATE TRIGGER IF NOT EXISTS $NO_UPDATE BEFORE UPDATE ON audit_entry " +
                "BEGIN SELECT RAISE(ABORT, 'audit_entry is append-only'); END",
            "CREATE TRIGGER IF NOT EXISTS $NO_DELETE BEFORE DELETE ON audit_entry " +
                "BEGIN SELECT RAISE(ABORT, 'audit_entry is append-only'); END",
        )

    /** Runs [SQL] on [db]. Idempotent. */
    fun install(db: SupportSQLiteDatabase) = SQL.forEach { db.execSQL(it) }
}

/**
 * Makes the database create the audit triggers when its file is first created. Every builder of a
 * [PftDatabase] (the production factory and the tests' `TestDatabase`) must call this.
 */
fun <T : RoomDatabase> RoomDatabase.Builder<T>.installAuditTriggers(): RoomDatabase.Builder<T> =
    addCallback(
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) = AuditTriggers.install(db)
        },
    )
