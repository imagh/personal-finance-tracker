package dev.imagh.pft.app.testing

import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.ledger.entity.Auditable
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The `keysStoredIn` rule of [AuditColumnsCheck] (slice C, C1c): an audited key that is held in
 * another table (a line's `tag_ids` in `transaction_line_tag`) is accepted only when that table
 * exists **and** is a listed non-entity table, so a misspelt or unreviewed join table cannot hide a
 * missing column. Each rule has its own test, so removing one fails exactly one of them.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AuditColumnsCheckTest {
    private val pft = TestDatabase.create()

    @After
    fun closeDatabase() = pft.close()

    private val scratch: SupportSQLiteDatabase get() = pft.openHelper.writableDatabase

    private class Probe(
        private val fields: Map<String, String?>,
    ) : Auditable {
        override val auditType = "probe"
        override val auditId = "probe-1"

        override fun auditFields() = fields
    }

    private val sample = Probe(mapOf("name" to "x", "tag_ids" to "a,b"))

    private fun createProbeTables() {
        scratch.execSQL("CREATE TABLE probe (id TEXT PRIMARY KEY, name TEXT)")
        scratch.execSQL("CREATE TABLE probe_tag (probe_id TEXT, tag_id TEXT, PRIMARY KEY (probe_id, tag_id))")
    }

    private fun problems(
        keysStoredIn: Map<String, String>,
        nonEntityTables: Set<String>,
    ) = AuditColumnsCheck.problems(scratch, "probe", sample, emptySet(), keysStoredIn, nonEntityTables)

    @Test
    fun `a key stored in an existing listed non-entity table is accepted`() {
        createProbeTables()

        problems(mapOf("tag_ids" to "probe_tag"), setOf("probe_tag")).shouldBeEmpty()
    }

    @Test
    fun `the same key with no keysStoredIn entry still has no column`() {
        createProbeTables()

        problems(emptyMap(), setOf("probe_tag")) shouldContainExactly
            listOf("auditFields() key 'tag_ids' has no column in table 'probe'")
    }

    @Test
    fun `a misspelt join table is reported as missing`() {
        createProbeTables()

        // The misspelling is listed as a non-entity table too, so only the existence rule can catch it.
        problems(mapOf("tag_ids" to "probe_tags"), setOf("probe_tags")) shouldContainExactly
            listOf("auditFields() key 'tag_ids' of table 'probe' is stored in table 'probe_tags', which does not exist")
    }

    @Test
    fun `an existing table that is not a listed non-entity table is reported`() {
        createProbeTables()

        problems(mapOf("tag_ids" to "probe_tag"), emptySet()) shouldContainExactly
            listOf(
                "auditFields() key 'tag_ids' of table 'probe' is stored in table 'probe_tag', " +
                    "which is not a listed non-entity table",
            )
    }

    @Test
    fun `a keysStoredIn entry for a key the sample does not audit is reported`() {
        createProbeTables()

        problems(mapOf("tag_ids" to "probe_tag", "ghost" to "probe_tag"), setOf("probe_tag")) shouldContainExactly
            listOf("keysStoredIn names key 'ghost' of table 'probe', which auditFields() does not contain")
    }
}
