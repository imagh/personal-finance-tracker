package dev.imagh.pft.app.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import dev.imagh.pft.app.data.transaction.TransactionFts
import dev.imagh.pft.app.data.transaction.TransactionQuerySql
import dev.imagh.pft.app.security.AndroidKeystoreKek
import dev.imagh.pft.app.security.DbKeyManager
import dev.imagh.pft.ledger.query.TimeWindow
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.query.TransactionSort
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.security.KeyStore

/**
 * Full-text search on the **encrypted** database, on the `pft35` emulator (slice C D6): the same
 * [TransactionQuerySql] queries the app runs, over SQLCipher's own bundled SQLite and its FTS4 and `unicode61`.
 * The JVM `RoomFtsIntegrationTest` proves the behaviour on the framework SQLite; this proves the engine the app
 * ships: the prefix phrase, the column-restricted `MATCH`, the sync triggers, tombstones, hostile text and the
 * rebuild after `VACUUM`. Rows are written by SQL (the services are covered on the JVM).
 *
 * Like [EncryptedDatabaseTest] it uses its own database name, Keystore alias and `noBackupFilesDir`
 * subdirectory, never the app's real `pft.db` or key.
 */
class EncryptedFtsTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "encrypted-fts-test.db"
    private val databaseFile = context.getDatabasePath(name)
    private val dir = File(context.noBackupFilesDir, "encrypted-fts-test")
    private val kek = AndroidKeystoreKek(alias = TEST_ALIAS)
    private var opened: PftDatabase? = null

    private fun open(): SupportSQLiteDatabase {
        val keys =
            DbKeyManager(File(dir, DbKeyManager.KEY_FILE_NAME), databaseFile, kek) { DbKeyManager.fsyncDirectory(it) }
        val backup =
            PreMigrationBackup(databaseFile, File(dir, "backups"), File(dir, "schema_version"), PftDatabase.VERSION)
        val database =
            DatabaseFactory(context, name, keys, backup) { DatabaseFactory.sqlCipherOpenHelperFactory(it) }.open()
        opened = database
        return database.openHelper.writableDatabase
    }

    private fun cleanUp() {
        opened?.let { if (it.isOpen) it.close() }
        opened = null
        context.deleteDatabase(name)
        dir.deleteRecursively()
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(TEST_ALIAS)
    }

    @Before
    fun loadNativeLibraryAndClean() {
        System.loadLibrary("sqlcipher")
        cleanUp()
    }

    @After
    fun after() = cleanUp()

    private fun SupportSQLiteDatabase.seedAccount() =
        execSQL(
            "INSERT INTO account (id, created_at, updated_at, created_by_user_id, created_on_device_id, name, " +
                "type, currency, opening_balance) VALUES ('a1', 1, 1, 'u1', 'd1', 'Cash', 'CASH', 'INR', 0)",
        )

    private fun SupportSQLiteDatabase.transaction(
        id: String,
        title: String?,
        description: String? = null,
        note: String? = null,
        at: Long = 1,
    ) {
        execSQL(
            "INSERT INTO ledger_transaction (id, created_at, updated_at, created_by_user_id, created_on_device_id, " +
                "type, direction, amount, occurred_at, account_id, title, description, state, source) " +
                "VALUES (?, 1, 1, 'u1', 'd1', 'EXPENSE', 'OUT', 500, ?, 'a1', ?, ?, 'VERIFIED', 'MANUAL')",
            arrayOf(id, at, title, description),
        )
        execSQL(
            "INSERT INTO transaction_line (id, created_at, updated_at, created_by_user_id, created_on_device_id, " +
                "transaction_id, position, amount, note) VALUES (?, 1, 1, 'u1', 'd1', ?, 1, 500, ?)",
            arrayOf("$id-line", id, note),
        )
    }

    /** The ids a search for [filter] returns, newest first, through the app's own query builder. */
    private fun SupportSQLiteDatabase.search(filter: TransactionFilter): List<String> {
        val match = TransactionQuerySql.match(TimeWindow.ALL, filter, emptySet())
        val query = TransactionQuerySql.transactions(match, TransactionSort.DEFAULT, TransactionQuerySql.NO_LIMIT)
        return query(query).use { cursor ->
            val id = cursor.getColumnIndexOrThrow("id")
            generateSequence { if (cursor.moveToNext()) cursor.getString(id) else null }.toList()
        }
    }

    /** How many rows [index] holds for [query], read from the index alone (no join to its content table). */
    private fun SupportSQLiteDatabase.indexRows(
        index: String,
        query: String,
    ): Int =
        query("SELECT COUNT(*) FROM $index WHERE $index MATCH ?", arrayOf(query)).use {
            it.moveToFirst()
            it.getInt(0)
        }

    @Test
    fun bothIndexesAndTheirEightSyncTriggersExistOnTheEncryptedDatabase() {
        val db = open()

        val triggers =
            db
                .query(
                    "SELECT name FROM sqlite_master WHERE type = 'trigger' AND name LIKE 'room_fts_content_sync_%'",
                ).use {
                    generateSequence { if (it.moveToNext()) it.getString(0) else null }.toSet()
                }

        assertEquals(FtsSyncTriggers.NAMES, triggers)
    }

    @Test
    fun aPrefixMatchesATitleADescriptionAndAnItemNote() {
        val db = open()
        db.seedAccount()
        db.transaction("t1", "Swiggy order", at = 3)
        db.transaction("t2", "Rent", description = "monthly rent", at = 2)
        db.transaction("t3", "Stationery", note = "Notebook for school", at = 1)

        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "swi")))
        assertEquals(listOf("t2"), db.search(TransactionFilter(text = "MONTH")))
        assertEquals(listOf("t3"), db.search(TransactionFilter(text = "notebook")))
        assertEquals(listOf("t2"), db.search(TransactionFilter(noteText = "rent")))
        assertEquals(listOf("t2"), db.search(TransactionFilter(titleText = "rent")))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = "monthly")))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(text = "wiggy")))
    }

    @Test
    fun eachWordMayMatchInADifferentPlaceOfItsScope() {
        val db = open()
        db.seedAccount()
        db.transaction("t1", "Swiggy", note = "biryani", at = 3)
        db.transaction("t2", "Swiggy", note = "dal", at = 2)
        db.transaction("t3", "Zomato", description = "biryani", at = 1)

        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "swiggy biryani")))
        assertEquals(listOf("t3"), db.search(TransactionFilter(text = "biryani zomato")))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = "swiggy biryani")))
        assertEquals(listOf("t1", "t3"), db.search(TransactionFilter(noteText = "biryani")))
    }

    @Test
    fun aTombstoneAndAnEditAreFollowedByTheIndex() {
        val db = open()
        db.seedAccount()
        db.transaction("t1", "Swiggy order", note = "notebook")
        db.transaction("t2", "Swiggy lunch")

        db.execSQL("UPDATE ledger_transaction SET deleted_at = 9 WHERE id = 't1'")
        assertEquals(listOf("t2"), db.search(TransactionFilter(text = "swiggy")))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(text = "notebook")))

        db.execSQL("UPDATE ledger_transaction SET deleted_at = NULL WHERE id = 't1'")
        db.execSQL("UPDATE transaction_line SET deleted_at = 9 WHERE id = 't1-line'")
        assertEquals(emptyList<String>(), db.search(TransactionFilter(text = "notebook")))
        db.execSQL("UPDATE transaction_line SET deleted_at = NULL WHERE id = 't1-line'")
        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "notebook")))

        db.execSQL("UPDATE ledger_transaction SET title = 'Zomato order' WHERE id = 't1'")
        db.execSQL("UPDATE transaction_line SET note = 'pencil' WHERE id = 't1-line'")
        assertEquals(listOf("t2"), db.search(TransactionFilter(text = "swiggy")))
        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "zomato")))
        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "pencil")))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(text = "notebook")))
    }

    @Test
    fun aDevanagariWordWithVowelSignsMatches() {
        val db = open()
        db.seedAccount()
        db.transaction("t1", KIRANA)
        db.transaction("t2", KITAB)

        assertEquals(listOf("t1"), db.search(TransactionFilter(titleText = KIRANA)))
        assertEquals(listOf("t1"), db.search(TransactionFilter(titleText = KIRANA.substring(0, 4))))
        assertEquals(listOf("t2"), db.search(TransactionFilter(titleText = KITAB)))
        // One token: other vowel signs on the same consonants, and a syllable from the middle, do not match.
        assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = KURAN)))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = RAA)))
        assertEquals(emptyList<String>(), db.search(TransactionFilter(text = KURAN)))
        // A vowel sign and a virama are part of the word: its later letters do not find it on their own.
        assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = NA)))
        db.transaction("t3", VIDYA)
        assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = YAA)))
        assertEquals(listOf("t3"), db.search(TransactionFilter(titleText = VIDYA)))
    }

    @Test
    fun hostileTextNeitherThrowsNorMatchesEverything() {
        val db = open()
        db.seedAccount()
        db.transaction("t1", "secret", description = "plain")
        db.transaction("t2", "other", description = "secret")
        val nul = String(intArrayOf(0), 0, 1)

        for (text in listOf(
            "'",
            "\"",
            "*",
            "NEAR(",
            nul,
            "title:",
            "\\",
            "\"a\" OR \"b",
            "a OR b",
            "(",
            ")",
            "^",
            "NEAR/3",
        )) {
            assertEquals(text.length.toString(), emptyList<String>(), db.search(TransactionFilter(text = text)))
            assertEquals(emptyList<String>(), db.search(TransactionFilter(titleText = text)))
            assertEquals(emptyList<String>(), db.search(TransactionFilter(noteText = text)))
        }
        // A typed column filter is two literal words, not a filter on the title.
        assertEquals(emptyList<String>(), db.search(TransactionFilter(text = "title:secret")))
        for (text in listOf("", "  ", "***")) {
            assertEquals(emptyList<String>(), db.search(TransactionFilter(text = text)))
        }
        assertEquals(listOf("t2", "t1").sorted(), db.search(TransactionFilter()).sorted())
    }

    @Test
    fun aRebuildAfterVacuumKeepsEveryMatchAndRestoresADamagedIndex() {
        val db = open()
        db.seedAccount()
        db.transaction("t1", "Swiggy order", note = "notebook")
        db.transaction("t2", "Dropped", note = "scrapped")
        db.transaction("t3", "Rent")
        assertEquals(1, db.indexRows("transaction_fts", "\"dropped*\""))
        assertEquals(1, db.indexRows("transaction_line_fts", "\"scrapped*\""))
        db.execSQL("DELETE FROM transaction_line WHERE transaction_id = 't2'")
        db.execSQL("DELETE FROM ledger_transaction WHERE id = 't2'")
        // Read from the indexes themselves: a search joins to the content tables and would hide a stale entry.
        assertEquals(0, db.indexRows("transaction_fts", "\"dropped*\""))
        assertEquals(0, db.indexRows("transaction_line_fts", "\"scrapped*\""))

        db.execSQL("VACUUM")
        TransactionFts.REBUILD.forEach(db::execSQL)

        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "swiggy")))
        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "notebook")))
        assertEquals(listOf("t3"), db.search(TransactionFilter(text = "rent")))

        db.execSQL("DELETE FROM transaction_fts")
        assertTrue(db.search(TransactionFilter(text = "swiggy")).isEmpty())
        TransactionFts.REBUILD.forEach(db::execSQL)
        assertEquals(listOf("t1"), db.search(TransactionFilter(text = "swiggy")))
    }

    private companion object {
        const val TEST_ALIAS = "pft_test_encrypted_fts_kek"

        /** Kirana (a grocery) in Devanagari: consonants with the vowel signs i and aa, which are marks. */
        val KIRANA = String(intArrayOf(0x0915, 0x093F, 0x0930, 0x093E, 0x0928, 0x093E), 0, 6)

        /** Kuran (Quran): the same consonants as [KIRANA] with other vowel signs. */
        val KURAN = String(intArrayOf(0x0915, 0x0941, 0x0930, 0x093E, 0x0928), 0, 5)

        /** Raa: a syllable from the middle of [KIRANA]. */
        val RAA = String(intArrayOf(0x0930, 0x093E), 0, 2)

        /** Na: the last consonant of [KIRANA] on its own. */
        val NA = String(intArrayOf(0x0928), 0, 1)

        /** Vidya (knowledge): a conjunct, consonants joined by a virama. */
        val VIDYA = String(intArrayOf(0x0935, 0x093F, 0x0926, 0x094D, 0x092F, 0x093E), 0, 6)

        /** Yaa: the end of [VIDYA], after its virama. */
        val YAA = String(intArrayOf(0x092F, 0x093E), 0, 2)

        /** Kitab (a book): shares its first syllable with [KIRANA]. */
        val KITAB = String(intArrayOf(0x0915, 0x093F, 0x0924, 0x093E, 0x092C), 0, 5)
    }
}
