package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.app.testing.MigrationTestSupport
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Migration tests against the exported schemas in `app/schemas/` (INT-5, plan §6 A4a). Every
 * slice that bumps the version adds a `migrateNToN+1` test here, built on [MigrationTestSupport],
 * which also checks after every migration that the audit trail and its triggers survived.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MigrationTest {
    @get:Rule
    val support = MigrationTestSupport()

    @Test
    fun `the exported v1 schema opens as the current entities with the seeded data intact`() {
        support.createV1 { it.execSQL("INSERT INTO user_profile (id, display_name) VALUES ('u1', 'Akash')") }

        val db = support.migrateTo(1)

        db.query("SELECT display_name FROM user_profile WHERE id = 'u1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Akash"
        }
    }

    @Test
    fun `the migration chain from v1 reaches the current version and keeps the audit trail`() {
        support.createV1()

        val db = support.migrateTo(PftDatabase.VERSION, *Migrations.ALL.toTypedArray())

        db.version shouldBe PftDatabase.VERSION
        support.auditRowCount(db) shouldBe 1
    }

    @Test
    fun `every version step has exactly one migration, in order, ending at the current version`() {
        val steps = Migrations.ALL.map { it.startVersion to it.endVersion }

        steps shouldBe (1 until PftDatabase.VERSION).map { it to it + 1 }
    }

    @Test
    fun `the only migration so far is 1 to 2`() {
        Migrations.ALL.map { it.startVersion to it.endVersion } shouldBe listOf(1 to 2)
    }

    @Test
    fun `migrate1To2 creates the account table, keeps the old data and the audit triggers`() {
        support.createV1 { it.execSQL("INSERT INTO user_profile (id, display_name) VALUES ('u1', 'Akash')") }

        val db = support.migrateTo(2, Migrations.MIGRATION_1_2)

        db.version shouldBe 2
        // Room lists the embedded meta columns last; the order is not part of the contract, the set is.
        columnsOf(db, "account").toSet() shouldBe
            setOf(
                "id" to "TEXT",
                "created_at" to "INTEGER",
                "updated_at" to "INTEGER",
                "created_by_user_id" to "TEXT",
                "created_on_device_id" to "TEXT",
                "deleted_at" to "INTEGER",
                "archived_at" to "INTEGER",
                "name" to "TEXT",
                "type" to "TEXT",
                "currency" to "TEXT",
                "opening_balance" to "INTEGER",
                "masked_identifier" to "TEXT",
                "issuer" to "TEXT",
            )
        db.query("SELECT display_name FROM user_profile WHERE id = 'u1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Akash"
        }
        support.auditRowCount(db) shouldBe 1
    }

    @Test
    fun `an account row can be written to the migrated table`() {
        support.createV1()
        val db = support.migrateTo(2, Migrations.MIGRATION_1_2)

        db.execSQL(
            "INSERT INTO account (id, created_at, updated_at, created_by_user_id, created_on_device_id, name, " +
                "type, currency, opening_balance) VALUES ('a1', 1, 1, 'u1', 'd1', 'Cash', 'CASH', 'INR', -500)",
        )

        db.query("SELECT opening_balance, masked_identifier FROM account WHERE id = 'a1'").use {
            it.moveToFirst() shouldBe true
            it.getLong(0) shouldBe -500L
            it.isNull(1) shouldBe true
        }
    }

    @Test
    fun `the support rejects a database whose audit trigger was lost in a migration`() {
        support.createV1(installAuditTriggers = false)

        val failure = shouldThrow<AssertionError> { support.migrateTo(1) }

        failure.message.orEmpty().contains(AuditTriggers.NO_UPDATE) shouldBe true
    }

    @Test
    fun `the seeded audit row of a migrated database is still undeletable`() {
        support.createV1()
        val db = support.migrateTo(1)

        shouldThrow<SQLiteConstraintException> {
            db.execSQL("DELETE FROM audit_entry WHERE id = ?", arrayOf(MigrationTestSupport.SEED_AUDIT_ID))
        }
        support.auditRowCount(db) shouldBe 1
    }

    /** `name to type` of every column of [table], in table order. */
    private fun columnsOf(
        db: SupportSQLiteDatabase,
        table: String,
    ): List<Pair<String, String>> =
        db.query("SELECT name, type FROM pragma_table_info(?)", arrayOf(table)).use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) to cursor.getString(1) else null }.toList()
        }
}
