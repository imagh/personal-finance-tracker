package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.app.testing.TestDatabase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The audit trail is immutable where the data lives (INT-2, plan §3.2 decision 3): `audit_entry`
 * rejects UPDATE and DELETE with triggers, whatever code issues the statement. Uses a real
 * in-memory database and raw SQL on purpose, to prove the database itself refuses, not the DAO.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AuditTriggersTest {
    private val database = TestDatabase.create()
    private val sql: SupportSQLiteDatabase get() = database.openHelper.writableDatabase

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `an audit row can be appended`() {
        insertAuditRow("a1")

        singleValue("SELECT COUNT(*) FROM audit_entry") shouldBe "1"
    }

    @Test
    fun `an audit row cannot be updated`() {
        insertAuditRow("a1", changesJson = "{\"original\":true}")

        shouldThrow<SQLiteConstraintException> {
            sql.execSQL("UPDATE audit_entry SET changes_json = '{}' WHERE id = ?", arrayOf("a1"))
        }

        singleValue("SELECT changes_json FROM audit_entry WHERE id = 'a1'") shouldBe "{\"original\":true}"
    }

    @Test
    fun `an audit row cannot be deleted by id`() {
        insertAuditRow("a1")

        shouldThrow<SQLiteConstraintException> {
            sql.execSQL("DELETE FROM audit_entry WHERE id = ?", arrayOf("a1"))
        }

        singleValue("SELECT COUNT(*) FROM audit_entry") shouldBe "1"
    }

    @Test
    fun `the whole audit trail cannot be wiped in one statement`() {
        insertAuditRow("a1")
        insertAuditRow("a2")

        shouldThrow<SQLiteConstraintException> { sql.execSQL("DELETE FROM audit_entry") }

        singleValue("SELECT COUNT(*) FROM audit_entry") shouldBe "2"
    }

    @Test
    fun `the triggers exist under their documented names`() {
        triggerNames() shouldContainExactlyInAnyOrder AuditTriggers.TRIGGER_NAMES
    }

    private fun insertAuditRow(
        id: String,
        changesJson: String = "{}",
    ) = sql.execSQL(
        "INSERT INTO audit_entry (id, entity_type, entity_id, action, changes_json, user_id, device_id, source, at) " +
            "VALUES (?, 'user_profile', 'u1', 'CREATE', ?, 'u1', 'd1', 'MANUAL', 1000)",
        arrayOf(id, changesJson),
    )

    private fun singleValue(query: String): String =
        sql.query(query).use {
            it.moveToFirst()
            it.getString(0)
        }

    private fun triggerNames(): List<String> =
        sql.query("SELECT name FROM sqlite_master WHERE type = 'trigger'").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toList()
        }
}
