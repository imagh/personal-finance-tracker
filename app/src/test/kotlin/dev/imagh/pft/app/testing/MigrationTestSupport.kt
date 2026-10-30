package dev.imagh.pft.app.testing

import android.database.sqlite.SQLiteConstraintException
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import dev.imagh.pft.app.data.AuditTriggers
import dev.imagh.pft.app.data.PftDatabase
import io.kotest.assertions.fail
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * The one way migration tests are written (plan §3.4, §6 A4a): a JUnit rule around Room's
 * `MigrationTestHelper` that replays the exported schemas in `app/schemas/`.
 *
 * Usage: `@get:Rule val support = MigrationTestSupport()`, then
 * `support.createV1 { db -> /* seed old-shape rows */ }` and
 * `val db = support.migrateTo(2, MIGRATION_1_2)`; assert the new shape on `db`. A chain test passes
 * `PftDatabase.VERSION` and `Migrations.ALL`.
 *
 * Why it exists: the helper builds the old database from the schema JSON, and the JSON has no
 * triggers (Room does not model them), so [createV1] installs them the way `onCreate` does and seeds
 * one audit row. [migrateTo] then runs the migrations, validates the result against the current
 * entities, and **fails unless the audit triggers are all present and still refuse to update or delete
 * the seeded row**. So no migration test can forget to prove the audit trail survived (INT-2).
 */
class MigrationTestSupport : TestRule {
    private val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), PftDatabase::class.java)

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement = helper.apply(base, description)

    /**
     * Creates the database file at schema version 1 from the exported JSON, installs the audit
     * triggers unless [installAuditTriggers] is false (only for testing this support), seeds one audit
     * row (id [SEED_AUDIT_ID]) and runs [seed] for any other old-shape rows. The database is closed
     * afterwards so [migrateTo] can open the file.
     */
    fun createV1(
        installAuditTriggers: Boolean = true,
        seed: (SupportSQLiteDatabase) -> Unit = {},
    ) {
        val db = helper.createDatabase(NAME, 1)
        if (installAuditTriggers) AuditTriggers.install(db)
        db.execSQL(
            "INSERT INTO audit_entry " +
                "(id, entity_type, entity_id, action, changes_json, user_id, device_id, source, at) " +
                "VALUES (?, 'user_profile', 'u1', 'CREATE', ?, 'u1', 'd1', 'MANUAL', 1000)",
            arrayOf(SEED_AUDIT_ID, SEED_CHANGES),
        )
        seed(db)
        db.close()
    }

    /**
     * Opens the database created by [createV1] at [version], running [migrations], and returns it
     * after Room has validated it against the current entities and [assertAuditTrailIntact] passed.
     */
    fun migrateTo(
        version: Int,
        vararg migrations: Migration,
    ): SupportSQLiteDatabase {
        val db = helper.runMigrationsAndValidate(NAME, version, true, *migrations)
        assertAuditTrailIntact(db)
        return db
    }

    /** Number of rows in `audit_entry`. */
    fun auditRowCount(db: SupportSQLiteDatabase): Int =
        db.query("SELECT COUNT(*) FROM audit_entry").use {
            it.moveToFirst()
            it.getInt(0)
        }

    /**
     * Fails unless exactly the triggers in [AuditTriggers.TRIGGER_NAMES] exist, the seeded audit row is
     * unchanged, and UPDATE and DELETE on it are refused.
     */
    private fun assertAuditTrailIntact(db: SupportSQLiteDatabase) {
        val present = triggerNames(db)
        val missing = AuditTriggers.TRIGGER_NAMES - present.toSet()
        if (missing.isNotEmpty()) fail("audit trigger(s) lost in migration: $missing")

        if (seedChanges(db) != SEED_CHANGES) fail("the seeded audit row is missing or was altered by the migration")
        refuses(db, "UPDATE audit_entry SET changes_json = '{}' WHERE id = ?") ||
            fail("audit_entry accepted an UPDATE after migration")
        refuses(db, "DELETE FROM audit_entry WHERE id = ?") || fail("audit_entry accepted a DELETE after migration")
    }

    private fun triggerNames(db: SupportSQLiteDatabase): List<String> =
        db.query("SELECT name FROM sqlite_master WHERE type = 'trigger'").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toList()
        }

    private fun seedChanges(db: SupportSQLiteDatabase): String? =
        db.query("SELECT changes_json FROM audit_entry WHERE id = ?", arrayOf(SEED_AUDIT_ID)).use {
            if (it.moveToFirst()) it.getString(0) else null
        }

    /** True when [sql] on the seeded row aborts with a constraint error (the trigger fired). */
    private fun refuses(
        db: SupportSQLiteDatabase,
        sql: String,
    ): Boolean =
        try {
            db.execSQL(sql, arrayOf(SEED_AUDIT_ID))
            false
        } catch (_: SQLiteConstraintException) {
            true
        }

    companion object {
        /** The database file name used by every migration test. */
        const val NAME = "migration-test.db"

        /** Id of the audit row [createV1] seeds. */
        const val SEED_AUDIT_ID = "seed-audit-1"

        private const val SEED_CHANGES = """{"display_name":{"old":null,"new":"Akash"}}"""
    }
}
