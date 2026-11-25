package dev.imagh.pft.app.testing

import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.entity.LedgerEntity

/**
 * Compares a table's real columns with what the entity audits, so a persisted field cannot escape
 * the audit trail (INT-2). Core's `AuditFieldsCheck` does the same for the model's fields; only
 * this check sees the Room columns.
 *
 * For a table: the columns from `pragma_table_info`, minus the shared meta columns ([META_COLUMNS])
 * and the table's reviewed allowlist, must equal the keys of the sample's `auditFields()`. Room
 * columns therefore need explicit snake_case `@ColumnInfo` names. A table that holds a
 * [LedgerEntity] must also have every meta column. The allowlist must not contain a column that does
 * not exist or one that is also audited, so it cannot rot.
 *
 * A key audited but held in another table (`keysStoredIn`, see [AuditedTable]) needs no column of its
 * own, but its holder table must exist and be a listed non-entity table, and the sample must audit
 * the key, so the exemption cannot hide a typo or go stale.
 *
 * Messages name tables and columns only, never values.
 */
object AuditColumnsCheck {
    /** The columns `EntityMeta` is stored in: never audited as fields (the lifecycle is the audit action). */
    val META_COLUMNS: Set<String> =
        setOf(
            "id",
            "created_at",
            "updated_at",
            "created_by_user_id",
            "created_on_device_id",
            "deleted_at",
            "archived_at",
        )

    /** The columns of [table], via the parameterised table-valued `pragma_table_info`. */
    fun columnsOf(
        db: SupportSQLiteDatabase,
        table: String,
    ): Set<String> =
        db.query("SELECT name FROM pragma_table_info(?)", arrayOf(table)).use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toSet()
        }

    /**
     * Everything wrong with one [table], given its fully populated [sample] and its [allowlist].
     *
     * @param keysStoredIn audited keys with no column in [table], each mapped to the table that holds them.
     * @param nonEntityTables the tables a [keysStoredIn] holder must be listed in.
     */
    fun problems(
        db: SupportSQLiteDatabase,
        table: String,
        sample: Auditable,
        allowlist: Set<String>,
        keysStoredIn: Map<String, String> = emptyMap(),
        nonEntityTables: Set<String> = AuditedTables.notEntityTables,
    ): List<String> {
        val columns = columnsOf(db, table)
        val audited = sample.auditFields().keys
        val persisted = columns - META_COLUMNS - allowlist
        val problems = mutableListOf<String>()
        for (column in (persisted - audited).sorted()) {
            problems += "column '$column' of table '$table' is not audited: add it to auditFields() or the allowlist"
        }
        for (key in (audited - persisted - keysStoredIn.keys).sorted()) {
            problems += "auditFields() key '$key' has no column in table '$table'"
        }
        problems += storedElsewhereProblems(db, table, audited, keysStoredIn, nonEntityTables)
        for (column in (allowlist - columns).sorted()) {
            problems += "allowlisted column '$column' does not exist in table '$table': remove it from the allowlist"
        }
        if (sample is LedgerEntity<*, *>) {
            for (column in (META_COLUMNS - columns).sorted()) {
                problems += "ledger entity table '$table' lacks meta column '$column'"
            }
        }
        return problems
    }

    /** The problems with the keys [table] declares as stored in another table (see [AuditedTable.keysStoredIn]). */
    private fun storedElsewhereProblems(
        db: SupportSQLiteDatabase,
        table: String,
        audited: Set<String>,
        keysStoredIn: Map<String, String>,
        nonEntityTables: Set<String>,
    ): List<String> {
        val existing = tablesIn(db)
        return keysStoredIn.toSortedMap().flatMap { (key, holder) ->
            buildList {
                if (key !in audited) {
                    add("keysStoredIn names key '$key' of table '$table', which auditFields() does not contain")
                }
                if (holder !in existing) {
                    add("auditFields() key '$key' of table '$table' is stored in table '$holder', which does not exist")
                }
                if (holder !in nonEntityTables) {
                    add(
                        "auditFields() key '$key' of table '$table' is stored in table '$holder', " +
                            "which is not a listed non-entity table",
                    )
                }
            }
        }
    }

    /** [problems] for every registered table that exists in [db]. */
    fun problemsFor(
        db: SupportSQLiteDatabase,
        tables: List<AuditedTable>,
    ): List<String> {
        val present = tablesIn(db)
        return tables
            .filter { it.table in present }
            .flatMap { problems(db, it.table, it.sample, it.nonAudited, it.keysStoredIn) }
    }

    /** Tables in [db] that are neither registered in [tables] nor a known non-entity table. */
    fun unaccountedTables(
        db: SupportSQLiteDatabase,
        tables: List<AuditedTable>,
    ): List<String> = (tablesIn(db) - tables.map { it.table }.toSet() - AuditedTables.notEntityTables).sorted()

    private fun tablesIn(db: SupportSQLiteDatabase): Set<String> =
        db.query("SELECT name FROM sqlite_master WHERE type = 'table'").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toSet()
        }
}
