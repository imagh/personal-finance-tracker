package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
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
}
