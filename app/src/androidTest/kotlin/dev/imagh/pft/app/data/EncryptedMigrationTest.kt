package dev.imagh.pft.app.data

import androidx.test.platform.app.InstrumentationRegistry
import dev.imagh.pft.app.data.account.AccountMapper
import dev.imagh.pft.app.security.AndroidKeystoreKek
import dev.imagh.pft.app.security.DbKeyManager
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import kotlinx.coroutines.runBlocking
import net.zetetic.database.sqlcipher.SQLiteDatabase
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.time.Instant

/**
 * The first real migration on an **encrypted** file, on the `pft35` emulator (INT-5): a version 1
 * database keyed with the managed key is opened through [DatabaseFactory], which must back it up
 * (still encrypted) and migrate it to [PftDatabase.VERSION] with the old rows intact.
 *
 * The audit triggers asserted here are installed by [AuditTriggers]' `onOpen` callback on every open
 * (the exported v1 JSON has none), so this proves they are present on the opened encrypted database,
 * not that a migration preserved them; that survival is the JVM `MigrationTest`'s job
 * (`MigrationTestSupport.migrateTo` fails unless they survived).
 *
 * It targets [PftDatabase.VERSION], not a fixed number, so each later migration (categories, tags)
 * re-runs it without edits. The JVM `MigrationTest` proves each step's SQL; this proves the same
 * path on SQLCipher, the Keystore-wrapped key and the backup, which Robolectric cannot.
 *
 * The version 1 file is built from the exported schema `schemas/.../1.json` (the table, index and
 * identity-hash statements Room itself recorded) with SQLCipher directly and the managed key, because
 * Room's `MigrationTestHelper` would add `room-testing` to the instrumented runtime classpath and with
 * it new lock and checksum entries (plan §6 names the helper; see the B1c learnings). The key is a
 * copy: SQLCipher keeps the array it is given and `DbKeyManager.useKey` wipes its own (learnings
 * 2026-10-30). Like [EncryptedDatabaseTest] it uses its own database name, Keystore alias and
 * `noBackupFilesDir` subdirectory, never the app's real `pft.db` or key.
 */
class EncryptedMigrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val name = "encrypted-migration-test.db"
    private val databaseFile = context.getDatabasePath(name)
    private val dir = File(context.noBackupFilesDir, "encrypted-migration-test")
    private val keyFile = File(dir, DbKeyManager.KEY_FILE_NAME)
    private val backupDir = File(dir, "backups")
    private val versionFile = File(dir, "schema_version")
    private val kek = AndroidKeystoreKek(alias = TEST_ALIAS)
    private var opened: PftDatabase? = null

    private fun keys() = DbKeyManager(keyFile, databaseFile, kek) { DbKeyManager.fsyncDirectory(it) }

    private fun cleanUp() {
        opened?.takeIf { it.isOpen }?.close()
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

    /**
     * A version 1 file keyed with the managed key and holding one user row, as an A-final install would:
     * every table and index statement and the `room_master_table` identity row from the exported 1.json,
     * then `user_version = 1`.
     */
    private fun createEncryptedV1() {
        val key = keys().useKey { it.copyOf() } // creates the key: no database file exists yet
        val schema =
            JSONObject(
                instrumentation.context.assets
                    .open(SCHEMA_V1)
                    .bufferedReader()
                    .use { it.readText() },
            ).getJSONObject("database")
        databaseFile.parentFile!!.mkdirs()
        val v1 = SQLiteDatabase.openOrCreateDatabase(databaseFile.path, key, null, null, null)
        try {
            val entities = schema.getJSONArray("entities")
            for (i in 0 until entities.length()) {
                val entity = entities.getJSONObject(i)
                val table = entity.getString("tableName")
                v1.execSQL(entity.getString("createSql").replace(TABLE_NAME, table))
                val indices = entity.optJSONArray("indices")
                for (j in 0 until (indices?.length() ?: 0)) {
                    v1.execSQL(indices!!.getJSONObject(j).getString("createSql").replace(TABLE_NAME, table))
                }
            }
            val setup = schema.getJSONArray("setupQueries")
            for (i in 0 until setup.length()) v1.execSQL(setup.getString(i))
            v1.execSQL("INSERT INTO user_profile (id, display_name) VALUES ('u1', 'Akash')")
            v1.version = 1
        } finally {
            v1.close()
        }
        versionFile.parentFile!!.mkdirs()
        versionFile.writeText("1\n")
    }

    private fun open(): PftDatabase =
        DatabaseFactory(
            context,
            name,
            keys(),
            PreMigrationBackup(databaseFile, backupDir, versionFile, PftDatabase.VERSION),
        ) { DatabaseFactory.sqlCipherOpenHelperFactory(it) }
            .open()
            .also { opened = it }

    private fun header(file: File): ByteArray =
        ByteArray(16).also { file.inputStream().use { input -> input.read(it) } }

    @Test
    fun anEncryptedV1DatabaseMigratesToTheCurrentVersionWithAnEncryptedBackup() {
        createEncryptedV1()
        assertFalse("the v1 file must be encrypted", header(databaseFile).contentEquals(SQLITE_HEADER))

        val db = open()

        val backup = File(backupDir, "pre-migration-v1.db")
        assertTrue("the pre-migration backup must exist", backup.isFile)
        assertFalse("the backup must still be encrypted", header(backup).contentEquals(SQLITE_HEADER))
        val sqlite = db.openHelper.writableDatabase
        assertEquals(PftDatabase.VERSION, sqlite.version)
        sqlite.query("SELECT display_name FROM user_profile WHERE id = 'u1'").use {
            assertTrue(it.moveToFirst())
            assertEquals("Akash", it.getString(0))
        }
        val record = PreMigrationBackup(databaseFile, backupDir, versionFile, PftDatabase.VERSION)
        assertEquals(PftDatabase.VERSION, record.lastOpenedVersion())
    }

    @Test
    fun theMigratedEncryptedDatabaseHasItsAuditTriggersAndAcceptsAnAccount() {
        createEncryptedV1()

        val db = open()

        val sqlite = db.openHelper.writableDatabase
        val triggers =
            sqlite.query("SELECT name FROM sqlite_master WHERE type = 'trigger'").use { c ->
                generateSequence { if (c.moveToNext()) c.getString(0) else null }.toList()
            }
        assertEquals(AuditTriggers.TRIGGER_NAMES.sorted(), triggers.sorted())
        val at = Instant.ofEpochMilli(1_700_000_000_000)
        val account =
            Account(
                EntityMeta(AccountId("acc-1"), at, at, UserId("u1"), DeviceId("d1")),
                "HDFC Savings",
                AccountType.BANK,
                Paise(-250_000),
                "1234",
                null,
            )
        runBlocking { db.accountDao().insert(AccountMapper.toRow(account)) }
        assertEquals(account, runBlocking { db.accountDao().byId("acc-1") }?.let(AccountMapper::toModel))
    }

    private companion object {
        const val TEST_ALIAS = "pft_test_encrypted_migration_kek"
        const val SCHEMA_V1 = "dev.imagh.pft.app.data.PftDatabase/1.json"
        const val TABLE_NAME = "\${TABLE_NAME}"
        val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
    }
}
