package dev.imagh.pft.app.data

import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.app.testing.AuditColumnsCheck
import dev.imagh.pft.app.testing.AuditedTables
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.ledger.entity.Auditable
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The Room-side twin of core's `AuditFieldsCheck` (INT-2, plan §3.4 "As built (A3)"): the fake
 * repositories cannot see Room columns, so this compares, for every table, the real columns with
 * the keys of a fully populated sample's `auditFields()`. A column added to a table without being
 * audited (or named in the reviewed allowlist) fails here, which is what stops a persisted field
 * from silently escaping the audit trail.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomAuditColumnsTest {
    private val pft = TestDatabase.create()
    private val sample = TestDatabase.createWithSample()

    @After
    fun closeDatabases() {
        pft.close()
        sample.close()
    }

    private val scratch: SupportSQLiteDatabase get() = pft.openHelper.writableDatabase

    private class Probe(
        private val fields: Map<String, String?>,
    ) : Auditable {
        override val auditType = "probe"
        override val auditId = "probe-1"

        override fun auditFields() = fields
    }

    // The real schema ------------------------------------------------------------------------

    @Test
    fun `every audited table in the production schema audits exactly its business columns`() {
        AuditColumnsCheck.problemsFor(pft.openHelper.writableDatabase, AuditedTables.all).shouldBeEmpty()
    }

    @Test
    fun `the throwaway ledger entity table audits exactly its business columns and has every meta column`() {
        AuditColumnsCheck.problemsFor(sample.openHelper.writableDatabase, AuditedTables.all).shouldBeEmpty()
    }

    @Test
    fun `no table in the database is missing from the registry or the reviewed non-entity list`() {
        AuditColumnsCheck.unaccountedTables(sample.openHelper.writableDatabase, AuditedTables.all).shouldBeEmpty()
    }

    @Test
    fun `the allowlist is small and reviewed`() {
        // Every non-audited column must be named here with its reason. Adding one is a review decision.
        val allowlisted = AuditedTables.all.associate { it.table to it.nonAudited }.filterValues { it.isNotEmpty() }

        allowlisted shouldBe mapOf("app_setting" to setOf("key"))
    }

    // The check itself: it must catch what it exists to catch ----------------------------------

    private fun probe(vararg keys: String) = Probe(keys.associateWith { "x" })

    private fun problems(
        probe: Probe,
        allowlist: Set<String> = emptySet(),
    ) = AuditColumnsCheck.problems(scratch, "probe", probe, allowlist)

    @Test
    fun `a column that is neither audited nor allowlisted is reported by name`() {
        scratch.execSQL("CREATE TABLE probe (id TEXT PRIMARY KEY, name TEXT, secret TEXT)")

        problems(probe("name")) shouldContainExactly
            listOf("column 'secret' of table 'probe' is not audited: add it to auditFields() or the allowlist")
    }

    @Test
    fun `an audited key with no column is reported`() {
        scratch.execSQL("CREATE TABLE probe (id TEXT PRIMARY KEY, name TEXT)")

        problems(probe("name", "ghost")) shouldContainExactly
            listOf("auditFields() key 'ghost' has no column in table 'probe'")
    }

    @Test
    fun `an allowlisted column is accepted`() {
        scratch.execSQL("CREATE TABLE probe (id TEXT PRIMARY KEY, name TEXT, sort_key TEXT)")

        problems(probe("name"), setOf("sort_key")).shouldBeEmpty()
    }

    @Test
    fun `an allowlist entry for a column that does not exist is reported`() {
        scratch.execSQL("CREATE TABLE probe (id TEXT PRIMARY KEY, name TEXT)")

        problems(probe("name"), setOf("gone")) shouldContainExactly
            listOf("allowlisted column 'gone' does not exist in table 'probe': remove it from the allowlist")
    }

    @Test
    fun `a column that is both allowlisted and audited is reported`() {
        scratch.execSQL("CREATE TABLE probe (id TEXT PRIMARY KEY, name TEXT, sort_key TEXT)")

        problems(probe("name", "sort_key"), setOf("sort_key")) shouldContainExactly
            listOf("auditFields() key 'sort_key' has no column in table 'probe'")
    }

    @Test
    fun `meta columns are never expected in auditFields()`() {
        scratch.execSQL(
            "CREATE TABLE probe (id TEXT PRIMARY KEY, created_at INTEGER, updated_at INTEGER, " +
                "created_by_user_id TEXT, created_on_device_id TEXT, deleted_at INTEGER, " +
                "archived_at INTEGER, name TEXT)",
        )

        problems(probe("name")).shouldBeEmpty()
    }

    @Test
    fun `a table the registry does not know is reported`() {
        scratch.execSQL("CREATE TABLE brand_new (id TEXT PRIMARY KEY)")

        AuditColumnsCheck.unaccountedTables(scratch, AuditedTables.all) shouldContainExactly listOf("brand_new")
    }
}
