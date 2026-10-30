package dev.imagh.pft.app.data

import android.app.Application
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.security.DbKeyManager
import dev.imagh.pft.app.security.DbKeyUnavailable
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.security.InMemoryKekSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.GeneralSecurityException

/**
 * [DatabaseFactory]'s order of events and its fail-closed rules on the JVM. Robolectric has neither
 * the Keystore nor SQLCipher's native library, so the key's KEK is an in-memory AES key
 * ([InMemoryKekSource]) and the open helper is the framework's, standing in for SQLCipher's; the
 * factory function receives the same key array SQLCipher would. That SQLCipher really encrypts and
 * reopens with this key is the instrumented `EncryptedDatabaseTest`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DatabaseFactoryTest {
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val name = "factory-test.db"
    private val databaseFile = context.getDatabasePath(name)
    private val dir = File(context.noBackupFilesDir, "factory-test")
    private val keyFile = File(dir, DbKeyManager.KEY_FILE_NAME)
    private val kek = InMemoryKekSource()
    private val opened = mutableListOf<PftDatabase>()

    /** What the factory function saw at the moment the open helper was requested. */
    private class Handoff(
        val key: ByteArray,
        val keyFileExisted: Boolean,
        val databaseExisted: Boolean,
        val backupExisted: Boolean,
    )

    private val handoffs = mutableListOf<Handoff>()
    private var failOpenHelper: Exception? = null

    private val backup =
        PreMigrationBackup(
            databaseFile = databaseFile,
            backupDir = File(dir, "backups"),
            versionFile = File(dir, "schema_version"),
            currentVersion = PftDatabase.VERSION,
        )

    private fun factory(
        keys: DbKeyManager = DbKeyManager(keyFile, databaseFile, kek, syncDirectory = {}),
        pre: PreMigrationBackup = backup,
    ) = DatabaseFactory(context, name, keys, pre) { key ->
        handoffs +=
            Handoff(
                key,
                keyFileExisted = keyFile.exists(),
                databaseExisted = databaseFile.exists(),
                backupExisted = File(dir, "backups/${PreMigrationBackup.backupName(0)}").exists(),
            )
        failOpenHelper?.let { throw it }
        FrameworkSQLiteOpenHelperFactory() as SupportSQLiteOpenHelper.Factory
    }

    private fun open(factory: DatabaseFactory = factory()): PftDatabase = factory.open().also { opened += it }

    @Before
    fun clean() = cleanUp()

    @After
    fun cleanUp() {
        opened.forEach { if (it.isOpen) it.close() }
        context.deleteDatabase(name)
        dir.deleteRecursively()
    }

    @Test
    fun `on a fresh install the key exists before the database file is created, and the database is usable`() {
        val db = open()

        val handoff = handoffs.single()
        handoff.keyFileExisted shouldBe true
        handoff.databaseExisted shouldBe false
        databaseFile.exists() shouldBe true
        db.openHelper.writableDatabase.version shouldBe PftDatabase.VERSION
    }

    @Test
    fun `the open helper gets the real key, and it is still intact once open returns`() {
        open()

        val handed = handoffs.single().key
        val real = DbKeyManager(keyFile, databaseFile, kek, syncDirectory = {}).useKey { it.copyOf() }
        // SQLCipher keeps this very array and uses it again for each new connection, so wiping it after the
        // first open would key later connections (and a first-run database) with zeros.
        handed.size shouldBe 32
        handed.all { it == 0.toByte() } shouldBe false
        handed shouldBe real
    }

    @Test
    fun `a failed open wipes the key it was handed, and records no version`() {
        failOpenHelper = IllegalStateException("boom")

        shouldThrow<IllegalStateException> { factory().open() }

        handoffs.single().key.all { it == 0.toByte() } shouldBe true
        backup.lastOpenedVersion() shouldBe null
    }

    @Test
    fun `the schema version is recorded after a successful open`() {
        open()

        backup.lastOpenedVersion() shouldBe PftDatabase.VERSION
    }

    @Test
    fun `a database of unknown version is backed up before it is opened`() {
        open().close()
        File(dir, "schema_version").delete()
        handoffs.clear()

        open()

        handoffs.single().backupExisted shouldBe true
    }

    @Test
    fun `a backup that fails stops the open before the database is touched`() {
        open().close()
        File(dir, "schema_version").delete()
        handoffs.clear()
        val before = databaseFile.readBytes()
        File(dir, "backups").writeBytes(byteArrayOf(0)) // a file where the directory must go

        shouldThrow<PreMigrationBackupFailed> { factory().open() }

        handoffs shouldBe emptyList()
        databaseFile.readBytes() shouldBe before
    }

    @Test
    fun `an existing database whose key file is gone fails closed and nothing is created or changed`() {
        open().close()
        val before = databaseFile.readBytes()
        keyFile.delete()
        handoffs.clear()

        shouldThrow<DbKeyUnavailable> { factory().open() }.reason shouldBe Reason.KEY_FILE_MISSING

        handoffs shouldBe emptyList()
        keyFile.exists() shouldBe false
        databaseFile.readBytes() shouldBe before
    }

    @Test
    fun `a damaged key file fails closed and is left as it is`() {
        open().close()
        val damaged = keyFile.readBytes().also { it[it.size - 1] = (it[it.size - 1].toInt() xor 1).toByte() }
        keyFile.writeBytes(damaged)
        val before = databaseFile.readBytes()
        handoffs.clear()

        shouldThrow<DbKeyUnavailable> { factory().open() }.reason shouldBe Reason.KEY_FILE_AUTH_FAILED

        handoffs shouldBe emptyList()
        keyFile.readBytes() shouldBe damaged
        databaseFile.readBytes() shouldBe before
    }

    @Test
    fun `a broken Keystore on a fresh install creates neither a key nor a database`() {
        kek.failure = GeneralSecurityException("keystore down")

        shouldThrow<DbKeyUnavailable> { factory().open() }.reason shouldBe Reason.KEK_UNUSABLE

        handoffs shouldBe emptyList()
        keyFile.exists() shouldBe false
        databaseFile.exists() shouldBe false
    }
}
