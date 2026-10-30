package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteException
import android.system.Os
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import dev.imagh.pft.app.security.AndroidKeystoreKek
import dev.imagh.pft.app.security.DbKeyManager
import net.zetetic.database.sqlcipher.SQLiteDatabase
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * The encrypted app database on the `pft35` emulator (PRD SEC-1): [DatabaseFactory] over the real
 * SQLCipher native library, a real Keystore KEK and [PftDatabase]. This is what proves the
 * Room 2.7.1 and sqlcipher-android 4.7.2 pairing, the audit triggers on an encrypted file, and that
 * a database reopens with the managed key through a fresh manager and a fresh [PftDatabase].
 *
 * The tests run inside the app's process, so they use their own database name, Keystore alias and a
 * subdirectory of `noBackupFilesDir`, and never touch the app's real `pft.db` or key.
 */
class EncryptedDatabaseTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "encrypted-database-test.db"
    private val databaseFile = context.getDatabasePath(name)
    private val dir = File(context.noBackupFilesDir, "encrypted-database-test")
    private val keyFile = File(dir, DbKeyManager.KEY_FILE_NAME)
    private val backupDir = File(dir, "backups")
    private val kek = AndroidKeystoreKek(alias = TEST_ALIAS)
    private val opened = mutableListOf<PftDatabase>()

    private fun keys() = DbKeyManager(keyFile, databaseFile, kek) { DbKeyManager.fsyncDirectory(it) }

    private fun backup(
        databaseFile: File = this.databaseFile,
        versionFile: File = File(dir, "schema_version"),
    ) = PreMigrationBackup(databaseFile, backupDir, versionFile, PftDatabase.VERSION)

    /** A fresh manager, backup and database every call, as after an app restart. */
    private fun open(): PftDatabase =
        DatabaseFactory(context, name, keys(), backup()) { DatabaseFactory.sqlCipherOpenHelperFactory(it) }
            .open()
            .also { opened += it }

    private fun keyCopy(): ByteArray = keys().useKey { it.copyOf() }

    private fun SupportSQLiteDatabase.putSetting(
        key: String,
        value: String,
    ) = execSQL(
        "INSERT OR REPLACE INTO app_setting (`key`, value, updated_at) VALUES (?, ?, ?)",
        arrayOf<Any>(key, value, 1L),
    )

    private fun SupportSQLiteDatabase.setting(key: String): String? =
        query("SELECT value FROM app_setting WHERE `key` = ?", arrayOf(key)).use {
            if (it.moveToFirst()) it.getString(0) else null
        }

    private fun SupportSQLiteDatabase.triggerNames(): List<String> =
        query("SELECT name FROM sqlite_master WHERE type = 'trigger'").use { c ->
            generateSequence { if (c.moveToNext()) c.getString(0) else null }.toList()
        }

    private fun settingDirectly(
        file: File,
        key: String,
    ): String? =
        openDirectly(file, keyCopy()).use { db ->
            db.rawQuery("SELECT value FROM app_setting WHERE `key` = ?", arrayOf(key)).use {
                if (it.moveToFirst()) it.getString(0) else null
            }
        }

    private fun header(file: File): ByteArray =
        ByteArray(16).also { file.inputStream().use { input -> input.read(it) } }

    private fun cleanUp() {
        opened.forEach { if (it.isOpen) it.close() }
        opened.clear()
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

    @Test
    fun theFileOnDiskIsEncrypted() {
        val started = System.nanoTime()
        val db = open()
        val millis = (System.nanoTime() - started) / NANOS_PER_MILLI
        Log.i(TAG, "first open (key creation, key derivation, schema creation) took $millis ms")
        db.openHelper.writableDatabase.putSetting("k", PLAINTEXT_MARKER)
        db.close()

        assertFalse("the file must not start with the SQLite header", header(databaseFile).contentEquals(SQLITE_HEADER))
        assertFalse(
            "the value must not be readable in the file",
            databaseFile.readBytes().containsRun(PLAINTEXT_MARKER.toByteArray()),
        )
    }

    @Test
    fun theDatabaseRunsInWriteAheadLogMode() {
        val journalMode =
            open().openHelper.writableDatabase.query("PRAGMA journal_mode").use {
                it.moveToFirst()
                it.getString(0)
            }

        assertEquals("wal", journalMode.lowercase())
    }

    @Test
    fun aWrongKeyFailsToOpenAndLeavesTheFileUntouched() {
        open().apply { openHelper.writableDatabase.putSetting("k", "v") }.close()
        val before = databaseFile.readBytes()
        val wrong = keyCopy().also { it[0] = (it[0].toInt() xor 1).toByte() }

        assertThrows(SQLiteException::class.java) { openDirectly(databaseFile, wrong) }

        assertArrayEquals(before, databaseFile.readBytes())
    }

    @Test
    fun anAllZeroKeyDoesNotOpenIt() {
        open().apply { openHelper.writableDatabase.putSetting("k", "v") }.close()
        val before = databaseFile.readBytes()

        assertThrows(SQLiteException::class.java) { openDirectly(databaseFile, ByteArray(32)) }

        assertArrayEquals(before, databaseFile.readBytes())
    }

    @Test
    fun theManagedKeyReopensItThroughAFreshManagerAndAFreshDatabase() {
        val first = open()
        first.openHelper.writableDatabase.putSetting("k", "value-1")
        first.close()

        val second = open()

        assertTrue(second !== first)
        assertEquals("value-1", second.openHelper.writableDatabase.setting("k"))
    }

    @Test
    fun theManagedKeyAlsoOpensItDirectlyWithSqlCipher() {
        open().apply { openHelper.writableDatabase.putSetting("k", "value-2") }.close()

        assertEquals("value-2", settingDirectly(databaseFile, "k"))
    }

    @Test
    fun laterConnectionsAreKeyedToo() {
        // SQLCipher keeps the key array it was given and re-keys every connection it opens later, so
        // concurrent readers (which open extra connections in WAL mode) prove the array is still intact.
        val db = open()
        db.openHelper.writableDatabase.putSetting("k", "v")
        val pool = Executors.newFixedThreadPool(8)
        try {
            val reads =
                (1..8).map {
                    pool.submit(
                        Callable {
                            repeat(25) { assertEquals("v", db.openHelper.readableDatabase.setting("k")) }
                        },
                    )
                }
            reads.forEach { it.get() }
        } finally {
            pool.shutdownNow()
        }
    }

    @Test
    fun theAuditTriggersAreInstalledAndEnforcedOnTheEncryptedDatabase() {
        val db = open().openHelper.writableDatabase
        db.execSQL(
            "INSERT INTO audit_entry (id, entity_type, entity_id, action, changes_json, user_id, device_id, " +
                "source, at) VALUES ('a1', 'user_profile', 'u1', 'CREATE', '{}', 'u1', 'd1', 'MANUAL', 1000)",
        )

        assertEquals(AuditTriggers.TRIGGER_NAMES.sorted(), db.triggerNames().sorted())
        assertThrows(SQLiteException::class.java) { db.execSQL("UPDATE audit_entry SET action = 'DELETE'") }
        assertThrows(SQLiteException::class.java) { db.execSQL("DELETE FROM audit_entry") }
    }

    @Test
    fun aLostTriggerIsRepairedWhenTheEncryptedDatabaseIsReopened() {
        val first = open()
        first.openHelper.writableDatabase.execSQL("DROP TRIGGER ${AuditTriggers.NO_UPDATE}")
        first.close()

        val names = open().openHelper.writableDatabase.triggerNames()

        assertEquals(AuditTriggers.TRIGGER_NAMES.sorted(), names.sorted())
    }

    @Test
    fun aDamagedDatabaseFailsToOpenAndIsNeverDeleted() {
        // SQLCipher's default error handler would delete a corrupt plain SQLite file; with a codec it must not.
        open().apply { openHelper.writableDatabase.putSetting("k", "v") }.close()
        val damaged = databaseFile.readBytes().also { for (i in 0 until 16) it[i] = (it[i].toInt() xor 0x5A).toByte() }
        databaseFile.writeBytes(damaged)

        assertThrows(SQLiteException::class.java) { open() }

        assertTrue(databaseFile.exists())
        assertArrayEquals(damaged, databaseFile.readBytes())
        assertTrue(keyFile.exists())
    }

    @Test
    fun aBackupOfTheEncryptedDatabaseIsEncryptedPrivateAndRestorableWithItsLog() {
        val live = open().openHelper.writableDatabase
        // Keep committed rows in the write-ahead log, as after an unclean stop with the database closed.
        live.query("PRAGMA wal_autocheckpoint = 0").close()
        live.putSetting("k", "only-in-the-log")
        val wal = File(databaseFile.path + "-wal")
        assertTrue("the row must still be in the log for this test to mean anything", wal.length() > 0)
        val image = File(databaseFile.parentFile, "encrypted-database-test-image.db")
        val restored = File(databaseFile.parentFile, "encrypted-database-test-restored.db")
        try {
            databaseFile.copyTo(image, overwrite = true)
            wal.copyTo(File(image.path + "-wal"), overwrite = true)

            // No version was ever recorded for the image, so it is backed up as v0 (unknown).
            val copy = backup(image, versionFile = File(dir, "image_schema_version")).backUpIfOlder()!!

            assertEquals("pre-migration-v0.db", copy.name)
            assertFalse(header(copy).contentEquals(SQLITE_HEADER))
            assertEquals(MODE_0600, Os.stat(copy.path).st_mode and MODE_BITS)
            assertEquals(MODE_0700, Os.stat(backupDir.path).st_mode and MODE_BITS)
            copy.copyTo(restored, overwrite = true)
            File(backupDir, copy.name + "-wal").copyTo(File(restored.path + "-wal"), overwrite = true)
            assertEquals("only-in-the-log", settingDirectly(restored, "k"))
        } finally {
            SQLiteDatabase.deleteDatabase(image)
            SQLiteDatabase.deleteDatabase(restored)
        }
    }

    private fun openDirectly(
        file: File,
        key: ByteArray,
    ): SQLiteDatabase = SQLiteDatabase.openDatabase(file.path, key, null, SQLiteDatabase.OPEN_READWRITE, null, null)

    private fun ByteArray.containsRun(needle: ByteArray): Boolean =
        (0..size - needle.size).any { start ->
            needle.indices.all {
                this[
                    start +
                        it,
                ] ==
                    needle[it]
            }
        }

    private companion object {
        const val TAG = "EncryptedDatabaseTest"
        const val NANOS_PER_MILLI = 1_000_000
        const val MODE_BITS = 0b111_111_111
        const val MODE_0600 = 0b110_000_000
        const val MODE_0700 = 0b111_000_000
        const val TEST_ALIAS = "pft_test_encrypted_db_kek"
        const val PLAINTEXT_MARKER = "PLAINTEXT-MARKER-should-not-appear-in-the-file"
        val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
    }
}
