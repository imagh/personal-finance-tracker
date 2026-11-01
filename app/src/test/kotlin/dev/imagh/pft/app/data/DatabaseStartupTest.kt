package dev.imagh.pft.app.data

import android.app.Application
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.security.DbKeyManager
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.security.InMemoryKekSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.io.File
import java.security.GeneralSecurityException

/**
 * [DatabaseStartup.open]: a database that opens is `Ready`; a database key that cannot be produced
 * is `KeyUnavailable` (the app shows `DataUnavailableScreen`), and in that case nothing is created
 * over existing data and nothing is deleted. Each outcome is also logged as one
 * [dev.imagh.pft.app.log.LogEvent] line that carries no path, file name or key material.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DatabaseStartupTest {
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val name = "startup-test.db"
    private val databaseFile = context.getDatabasePath(name)
    private val dir = File(context.noBackupFilesDir, "startup-test")
    private val keyFile = File(dir, DbKeyManager.KEY_FILE_NAME)
    private val kek = InMemoryKekSource()
    private var ready: PftDatabase? = null

    private fun factory() =
        DatabaseFactory(
            context,
            name,
            DbKeyManager(keyFile, databaseFile, kek, syncDirectory = {}),
            PreMigrationBackup(databaseFile, File(dir, "backups"), File(dir, "schema_version"), PftDatabase.VERSION),
        ) { FrameworkSQLiteOpenHelperFactory() }

    @After
    fun cleanUp() {
        ready?.close()
        context.deleteDatabase(name)
        dir.deleteRecursively()
    }

    @Test
    fun `a database that opens is ready`() {
        val startup = DatabaseStartup.open(factory())

        ready = startup.shouldBeInstanceOf<DatabaseStartup.Ready>().database
        ready!!.openHelper.writableDatabase.version shouldBe PftDatabase.VERSION
    }

    @Test
    fun `a missing Keystore key on a fresh install is key-unavailable and creates nothing`() {
        kek.failure = GeneralSecurityException("keystore down")

        val startup = DatabaseStartup.open(factory())

        startup shouldBe DatabaseStartup.KeyUnavailable(Reason.KEK_UNUSABLE)
        keyFile.exists() shouldBe false
        databaseFile.exists() shouldBe false
    }

    @Test
    fun `a lost key file next to an existing database is key-unavailable and nothing is touched`() {
        DatabaseStartup
            .open(factory())
            .shouldBeInstanceOf<DatabaseStartup.Ready>()
            .database
            .close()
        val before = databaseFile.readBytes()
        keyFile.delete()

        val startup = DatabaseStartup.open(factory())

        startup shouldBe DatabaseStartup.KeyUnavailable(Reason.KEY_FILE_MISSING)
        keyFile.exists() shouldBe false
        databaseFile.readBytes() shouldBe before
    }

    private fun pftLines() = ShadowLog.getLogs().filter { it.tag == "pft" }.map { it.msg }

    @Test
    fun `a database that opens logs db_opened with the schema version and a duration`() {
        ShadowLog.clear()

        ready = DatabaseStartup.open(factory()).shouldBeInstanceOf<DatabaseStartup.Ready>().database

        pftLines().single() shouldMatch Regex("""db_opened schema=${PftDatabase.VERSION} ms=\d+""")
    }

    @Test
    fun `a key that cannot be produced logs db_key_unavailable with only the reason code`() {
        kek.failure = GeneralSecurityException("keystore down")
        ShadowLog.clear()

        DatabaseStartup.open(factory())

        pftLines() shouldBe listOf("db_key_unavailable reason=KEK_UNUSABLE")
    }

    @Test
    fun `no log line carries a path, a file name, an exception message or anything but codes and numbers`() {
        ShadowLog.clear()
        val first = DatabaseStartup.open(factory()).shouldBeInstanceOf<DatabaseStartup.Ready>()
        first.database.close()
        keyFile.delete()
        kek.failure = GeneralSecurityException("keystore down at /secret/path")
        DatabaseStartup.open(factory())

        val logs = ShadowLog.getLogs()
        logs.size shouldBe 2
        logs.forEach { item ->
            item.tag shouldBe "pft"
            item.throwable shouldBe null
            item.msg shouldMatch Regex("^[a-z_]+( [a-z_]+=[A-Z0-9_]+)*$")
            listOf(name, databaseFile.path, dir.path, "keystore", "secret").forEach { part ->
                item.msg.contains(part) shouldBe false
            }
        }
    }
}
