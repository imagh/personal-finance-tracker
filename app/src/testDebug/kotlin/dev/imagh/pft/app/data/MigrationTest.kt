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
        columnsOf(db, "account").isNotEmpty() shouldBe true
        columnsOf(db, "category").isNotEmpty() shouldBe true
    }

    @Test
    fun `every version step has exactly one migration, in order, ending at the current version`() {
        val steps = Migrations.ALL.map { it.startVersion to it.endVersion }

        steps shouldBe (1 until PftDatabase.VERSION).map { it to it + 1 }
    }

    @Test
    fun `the migrations so far are 1 to 2 and 2 to 3`() {
        Migrations.ALL.map { it.startVersion to it.endVersion } shouldBe listOf(1 to 2, 2 to 3)
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
    fun `migrate2To3 creates category with its indexes and key, keeping the account and the audit triggers`() {
        support.createAt(2) {
            it.execSQL(
                "INSERT INTO account (id, created_at, updated_at, created_by_user_id, created_on_device_id, name, " +
                    "type, currency, opening_balance) VALUES ('a1', 1, 1, 'u1', 'd1', 'Cash', 'CASH', 'INR', -500)",
            )
        }

        val db = support.migrateTo(3, Migrations.MIGRATION_2_3)

        db.version shouldBe 3
        columnsOf(db, "category").toSet() shouldBe
            setOf(
                "id" to "TEXT",
                "created_at" to "INTEGER",
                "updated_at" to "INTEGER",
                "created_by_user_id" to "TEXT",
                "created_on_device_id" to "TEXT",
                "deleted_at" to "INTEGER",
                "archived_at" to "INTEGER",
                "parent_id" to "TEXT",
                "kind" to "TEXT",
                "name" to "TEXT",
                "colour" to "TEXT",
                "icon_key" to "TEXT",
                "sort_order" to "INTEGER",
                "system_key" to "TEXT",
            )
        // name to unique flag, for the two indexes the entity declares (Room validated them too).
        db.query("SELECT name, \"unique\" FROM pragma_index_list('category') WHERE origin = 'c'").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) to cursor.getInt(1) else null }.toSet()
        } shouldBe setOf("index_category_parent_id" to 0, "index_category_system_key" to 1)
        db.query("SELECT \"from\", \"table\", \"to\" FROM pragma_foreign_key_list('category')").use {
            it.moveToFirst() shouldBe true
            listOf(it.getString(0), it.getString(1), it.getString(2)) shouldBe listOf("parent_id", "category", "id")
            it.moveToNext() shouldBe false
        }
        db.query("SELECT name, opening_balance FROM account WHERE id = 'a1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Cash"
            it.getLong(1) shouldBe -500L
        }
        support.auditRowCount(db) shouldBe 1
    }

    @Test
    fun `the migrated category table refuses a dangling parent and a second system category`() {
        support.createAt(2)
        val db = support.migrateTo(3, Migrations.MIGRATION_2_3)
        db.execSQL("PRAGMA foreign_keys = ON")

        fun insert(
            id: String,
            parent: String?,
            systemKey: String?,
        ) = db.execSQL(
            "INSERT INTO category (id, created_at, updated_at, created_by_user_id, created_on_device_id, parent_id, " +
                "kind, name, colour, sort_order, system_key) VALUES (?, 1, 1, 'u1', 'd1', ?, 'EXPENSE', ?, 'slate', 0, ?)",
            arrayOf(id, parent, id, systemKey),
        )

        insert("c1", null, "LOAN_EMI")
        insert("c2", "c1", null)
        insert("c3", null, null)

        shouldThrow<SQLiteConstraintException> { insert("c4", "missing", null) }
        shouldThrow<SQLiteConstraintException> { insert("c5", null, "LOAN_EMI") }
    }

    @Test
    fun `createAt builds an older schema version with the audit row and the seeded rows`() {
        support.createAt(2) {
            it.execSQL(
                "INSERT INTO account (id, created_at, updated_at, created_by_user_id, created_on_device_id, name, " +
                    "type, currency, opening_balance) VALUES ('a1', 1, 1, 'u1', 'd1', 'Cash', 'CASH', 'INR', 700)",
            )
        }

        val db = support.migrateTo(2)

        db.version shouldBe 2
        db.query("SELECT name, opening_balance FROM account WHERE id = 'a1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Cash"
            it.getLong(1) shouldBe 700L
        }
        support.auditRowCount(db) shouldBe 1
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
