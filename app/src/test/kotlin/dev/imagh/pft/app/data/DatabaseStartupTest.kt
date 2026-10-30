package dev.imagh.pft.app.data

import android.app.Application
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.security.DbKeyManager
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.security.InMemoryKekSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.GeneralSecurityException

/**
 * [DatabaseStartup.open]: a database that opens is `Ready`; a database key that cannot be produced
 * is `KeyUnavailable` (the app shows `KeyUnavailableScreen`), and in that case nothing is created
 * over existing data and nothing is deleted.
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
}
