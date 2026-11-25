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
        columnsOf(db, "tag").isNotEmpty() shouldBe true
        columnsOf(db, "ledger_transaction").isNotEmpty() shouldBe true
        columnsOf(db, "transaction_line").isNotEmpty() shouldBe true
        columnsOf(db, "transaction_line_tag").isNotEmpty() shouldBe true
    }

    @Test
    fun `every version step has exactly one migration, in order, ending at the current version`() {
        val steps = Migrations.ALL.map { it.startVersion to it.endVersion }

        steps shouldBe (1 until PftDatabase.VERSION).map { it to it + 1 }
    }

    @Test
    fun `the migrations so far are 1 to 2, 2 to 3, 3 to 4 and 4 to 5`() {
        Migrations.ALL.map { it.startVersion to it.endVersion } shouldBe listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5)
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
    fun `migrate3To4 creates tag, keeping the account, the category and the audit triggers`() {
        support.createAt(3) {
            it.execSQL(
                "INSERT INTO account (id, created_at, updated_at, created_by_user_id, created_on_device_id, name, " +
                    "type, currency, opening_balance) VALUES ('a1', 1, 1, 'u1', 'd1', 'Cash', 'CASH', 'INR', -500)",
            )
            // A top-level category: the self-referencing key on parent_id is enforced, so a child would need its
            // parent inserted first.
            it.execSQL(
                "INSERT INTO category (id, created_at, updated_at, created_by_user_id, created_on_device_id, " +
                    "parent_id, kind, name, colour, sort_order, system_key) " +
                    "VALUES ('c1', 1, 1, 'u1', 'd1', NULL, 'EXPENSE', 'Food', 'coral', 0, NULL)",
            )
        }

        val db = support.migrateTo(4, Migrations.MIGRATION_3_4)

        db.version shouldBe 4
        columnsOf(db, "tag").toSet() shouldBe
            setOf(
                "id" to "TEXT",
                "created_at" to "INTEGER",
                "updated_at" to "INTEGER",
                "created_by_user_id" to "TEXT",
                "created_on_device_id" to "TEXT",
                "deleted_at" to "INTEGER",
                "archived_at" to "INTEGER",
                "name" to "TEXT",
            )
        // No index and no key beyond the primary key (plan §2: Room cannot declare a partial or NOCASE index).
        db.query("SELECT name FROM pragma_index_list('tag') WHERE origin = 'c'").use { it.count } shouldBe 0
        db.query("SELECT COUNT(*) FROM pragma_foreign_key_list('tag')").use {
            it.moveToFirst() shouldBe true
            it.getInt(0) shouldBe 0
        }
        db.query("SELECT name, opening_balance FROM account WHERE id = 'a1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Cash"
            it.getLong(1) shouldBe -500L
        }
        db.query("SELECT name, colour FROM category WHERE id = 'c1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Food"
            it.getString(1) shouldBe "coral"
        }
        support.auditRowCount(db) shouldBe 1
    }

    @Test
    fun `a tag row can be written to the migrated table, and two tags may share a name in the table`() {
        support.createAt(3)
        val db = support.migrateTo(4, Migrations.MIGRATION_3_4)

        fun insert(id: String) =
            db.execSQL(
                "INSERT INTO tag (id, created_at, updated_at, created_by_user_id, created_on_device_id, name) " +
                    "VALUES (?, 1, 1, 'u1', 'd1', 'Travel')",
                arrayOf(id),
            )
        insert("t1")
        // Uniqueness is the service's rule, not the table's (a fresh install has the same table).
        insert("t2")

        db.query("SELECT COUNT(*) FROM tag WHERE name = 'Travel'").use {
            it.moveToFirst() shouldBe true
            it.getInt(0) shouldBe 2
        }
    }

    @Test
    fun `migrate4To5 creates the transaction tables empty, keeping the old rows and the audit triggers`() {
        support.createAt(4, seed = ::seedV4)

        val db = support.migrateTo(5, Migrations.MIGRATION_4_5)

        db.version shouldBe 5
        // Room lists the embedded meta columns last; the order is not part of the contract, the set is.
        columnsOf(db, "ledger_transaction").toSet() shouldBe metaColumns + transactionColumns
        columnsOf(db, "transaction_line").toSet() shouldBe metaColumns + lineColumns
        // The join table is not a ledger entity: no meta columns.
        columnsOf(db, "transaction_line_tag").toSet() shouldBe setOf("line_id" to "TEXT", "tag_id" to "TEXT")
        for (table in listOf("ledger_transaction", "transaction_line", "transaction_line_tag")) {
            db.query("SELECT COUNT(*) FROM $table").use {
                it.moveToFirst() shouldBe true
                it.getInt(0) shouldBe 0
            }
        }
        // The rows that existed at v4 are intact.
        db.query("SELECT name, opening_balance FROM account WHERE id = 'a1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Cash"
            it.getLong(1) shouldBe -500L
        }
        db.query("SELECT name FROM category WHERE id = 'c1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Food"
        }
        db.query("SELECT name FROM tag WHERE id = 't1'").use {
            it.moveToFirst() shouldBe true
            it.getString(0) shouldBe "Travel"
        }
        support.auditRowCount(db) shouldBe 1
    }

    @Test
    fun `the migrated transaction tables have the indexes and keys the entities declare`() {
        support.createAt(4, seed = ::seedV4)
        val db = support.migrateTo(5, Migrations.MIGRATION_4_5)

        indexesOf(db, "ledger_transaction") shouldBe
            setOf(
                "index_ledger_transaction_occurred_at",
                "index_ledger_transaction_account_id_occurred_at",
                "index_ledger_transaction_transfer_group_id",
            )
        indexesOf(db, "transaction_line") shouldBe
            setOf("index_transaction_line_transaction_id", "index_transaction_line_category_id")
        indexesOf(db, "transaction_line_tag") shouldBe setOf("index_transaction_line_tag_tag_id")
        foreignKeysOf(db, "ledger_transaction") shouldBe setOf(Triple("account_id", "account", "id"))
        foreignKeysOf(db, "transaction_line") shouldBe
            setOf(Triple("transaction_id", "ledger_transaction", "id"), Triple("category_id", "category", "id"))
        foreignKeysOf(db, "transaction_line_tag") shouldBe
            setOf(Triple("line_id", "transaction_line", "id"), Triple("tag_id", "tag", "id"))
    }

    @Test
    fun `the migrated tables refuse dangling references and accept a full transaction with a tagged line`() {
        support.createAt(4, seed = ::seedV4)
        val db = support.migrateTo(5, Migrations.MIGRATION_4_5)
        db.execSQL("PRAGMA foreign_keys = ON")

        fun insertTransaction(accountId: String) =
            db.execSQL(
                "INSERT INTO ledger_transaction (id, created_at, updated_at, created_by_user_id, " +
                    "created_on_device_id, type, direction, amount, occurred_at, account_id, state, source) " +
                    "VALUES ('x1', 1, 1, 'u1', 'd1', 'EXPENSE', 'OUT', 500, 1, ?, 'VERIFIED', 'MANUAL')",
                arrayOf(accountId),
            )

        fun insertLine(
            transactionId: String,
            categoryId: String?,
        ) = db.execSQL(
            "INSERT INTO transaction_line (id, created_at, updated_at, created_by_user_id, created_on_device_id, " +
                "transaction_id, position, amount, category_id) VALUES ('l1', 1, 1, 'u1', 'd1', ?, 1, 500, ?)",
            arrayOf(transactionId, categoryId),
        )

        fun insertTag(
            lineId: String,
            tagId: String,
        ) = db.execSQL("INSERT INTO transaction_line_tag (line_id, tag_id) VALUES (?, ?)", arrayOf(lineId, tagId))

        shouldThrow<SQLiteConstraintException> { insertTransaction("missing") }
        insertTransaction("a1")
        shouldThrow<SQLiteConstraintException> { insertLine("missing", "c1") }
        shouldThrow<SQLiteConstraintException> { insertLine("x1", "missing") }
        insertLine("x1", "c1")
        shouldThrow<SQLiteConstraintException> { insertTag("missing", "t1") }
        shouldThrow<SQLiteConstraintException> { insertTag("l1", "missing") }
        insertTag("l1", "t1")
        // The primary key is the pair: the same tag twice on one line is refused.
        shouldThrow<SQLiteConstraintException> { insertTag("l1", "t1") }
    }

    private fun seedV4(db: SupportSQLiteDatabase) {
        // Parents before children: the foreign keys are enforced on both engines.
        db.execSQL(
            "INSERT INTO account (id, created_at, updated_at, created_by_user_id, created_on_device_id, name, " +
                "type, currency, opening_balance) VALUES ('a1', 1, 1, 'u1', 'd1', 'Cash', 'CASH', 'INR', -500)",
        )
        db.execSQL(
            "INSERT INTO category (id, created_at, updated_at, created_by_user_id, created_on_device_id, " +
                "parent_id, kind, name, colour, sort_order, system_key) " +
                "VALUES ('c1', 1, 1, 'u1', 'd1', NULL, 'EXPENSE', 'Food', 'coral', 0, NULL)",
        )
        db.execSQL(
            "INSERT INTO tag (id, created_at, updated_at, created_by_user_id, created_on_device_id, name) " +
                "VALUES ('t1', 1, 1, 'u1', 'd1', 'Travel')",
        )
    }

    private val metaColumns =
        setOf(
            "id" to "TEXT",
            "created_at" to "INTEGER",
            "updated_at" to "INTEGER",
            "created_by_user_id" to "TEXT",
            "created_on_device_id" to "TEXT",
            "deleted_at" to "INTEGER",
            "archived_at" to "INTEGER",
        )

    private val transactionColumns =
        setOf(
            "type" to "TEXT",
            "direction" to "TEXT",
            "amount" to "INTEGER",
            "occurred_at" to "INTEGER",
            "account_id" to "TEXT",
            "title" to "TEXT",
            "description" to "TEXT",
            "state" to "TEXT",
            "source" to "TEXT",
            "bank_ref" to "TEXT",
            "source_message_id" to "TEXT",
            "account_fragment" to "TEXT",
            "transfer_group_id" to "TEXT",
        )

    private val lineColumns =
        setOf(
            "transaction_id" to "TEXT",
            "position" to "INTEGER",
            "amount" to "INTEGER",
            "category_id" to "TEXT",
            "note" to "TEXT",
        )

    /** The names of the indexes declared on [table] (origin `c`: `CREATE INDEX`, not keys). */
    private fun indexesOf(
        db: SupportSQLiteDatabase,
        table: String,
    ): Set<String> =
        db.query("SELECT name FROM pragma_index_list(?) WHERE origin = 'c'", arrayOf(table)).use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toSet()
        }

    /** `(from column, referenced table, referenced column)` of every foreign key of [table]. */
    private fun foreignKeysOf(
        db: SupportSQLiteDatabase,
        table: String,
    ): Set<Triple<String, String, String>> =
        db.query("SELECT \"from\", \"table\", \"to\" FROM pragma_foreign_key_list(?)", arrayOf(table)).use { cursor ->
            generateSequence {
                if (cursor.moveToNext()) Triple(cursor.getString(0), cursor.getString(1), cursor.getString(2)) else null
            }.toSet()
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
