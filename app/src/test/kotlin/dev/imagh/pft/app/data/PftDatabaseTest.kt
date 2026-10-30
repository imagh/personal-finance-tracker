package dev.imagh.pft.app.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * [PftDatabase.create], the one place a database is built (INT-2): it wires the audit triggers
 * itself, so no caller can forget them, and re-installs them on every open, so a database whose
 * triggers were lost or replaced (an old file, a bad migration, tampering) is repaired instead of
 * silently accepting edits to the audit trail.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftDatabaseTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val fileName = "heal-test.db"
    private val opened = mutableListOf<PftDatabase>()

    @After
    fun cleanUp() {
        opened.forEach { if (it.isOpen) it.close() }
        context.deleteDatabase(fileName)
    }

    private fun open(name: String?): SupportSQLiteDatabase =
        PftDatabase
            .create(context, name)
            .also { opened += it }
            .openHelper.writableDatabase

    private fun triggerNames(db: SupportSQLiteDatabase): List<String> =
        db.query("SELECT name FROM sqlite_master WHERE type = 'trigger'").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toList()
        }

    private fun seedAuditRow(db: SupportSQLiteDatabase) =
        db.execSQL(
            "INSERT INTO audit_entry " +
                "(id, entity_type, entity_id, action, changes_json, user_id, device_id, source, at) " +
                "VALUES ('a1', 'user_profile', 'u1', 'CREATE', '{}', 'u1', 'd1', 'MANUAL', 1000)",
        )

    private fun assertAuditRowIsImmutable(db: SupportSQLiteDatabase) {
        shouldThrow<SQLiteConstraintException> { db.execSQL("UPDATE audit_entry SET changes_json = 'x'") }
        shouldThrow<SQLiteConstraintException> { db.execSQL("DELETE FROM audit_entry") }
    }

    @Test
    fun `a database built through create has the triggers and refuses to change the audit trail`() {
        val db = open(name = null)
        seedAuditRow(db)

        triggerNames(db) shouldContainExactlyInAnyOrder AuditTriggers.TRIGGER_NAMES
        assertAuditRowIsImmutable(db)
    }

    @Test
    fun `a file database that lost its triggers gets them back on the next open`() {
        val first = open(fileName)
        seedAuditRow(first)
        AuditTriggers.TRIGGER_NAMES.forEach { first.execSQL("DROP TRIGGER $it") }
        triggerNames(first) shouldBe emptyList()
        opened.single().close()

        val reopened = open(fileName)

        triggerNames(reopened) shouldContainExactlyInAnyOrder AuditTriggers.TRIGGER_NAMES
        assertAuditRowIsImmutable(reopened)
    }

    @Test
    fun `a trigger replaced by a harmless one of the same name is restored too`() {
        val first = open(fileName)
        seedAuditRow(first)
        first.execSQL("DROP TRIGGER ${AuditTriggers.NO_DELETE}")
        first.execSQL(
            "CREATE TRIGGER ${AuditTriggers.NO_DELETE} BEFORE DELETE ON audit_entry BEGIN SELECT 1; END",
        )
        first.execSQL("DELETE FROM audit_entry") // the forged trigger lets this through
        seedAuditRow(first)
        opened.single().close()

        val reopened = open(fileName)

        assertAuditRowIsImmutable(reopened)
    }

    @Test
    fun `reopening an intact database keeps its audit rows`() {
        val first = open(fileName)
        seedAuditRow(first)
        opened.single().close()

        val reopened = open(fileName)

        reopened.query("SELECT COUNT(*) FROM audit_entry").use {
            it.moveToFirst()
            it.getInt(0) shouldBe 1
        }
    }
}
