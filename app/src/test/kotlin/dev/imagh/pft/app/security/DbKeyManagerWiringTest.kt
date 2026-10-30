package dev.imagh.pft.app.security

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * [DbKeyManager.forApp] points at the app's real files: the wrapped key in `noBackupFilesDir` and
 * the `pft.db` database. Robolectric has no Android Keystore, which this also uses: with the
 * Keystore unavailable the production [AndroidKeystoreKek] fails closed and nothing is written.
 * The Keystore itself is exercised by the instrumented `DbKeyManagerTest`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DbKeyManagerWiringTest {
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val keyFile = File(context.noBackupFilesDir, DbKeyManager.KEY_FILE_NAME)

    @Test
    fun `the app's existing database without a key file fails closed`() {
        val database = context.getDatabasePath(PftDatabase.NAME)
        database.parentFile!!.mkdirs()
        database.writeBytes(byteArrayOf(1))

        shouldThrow<DbKeyUnavailable> { DbKeyManager.forApp(context).useKey { } }.reason shouldBe
            Reason.KEY_FILE_MISSING
        keyFile.exists() shouldBe false
    }

    @Test
    fun `the key file is read from noBackupFilesDir`() {
        keyFile.writeBytes(byteArrayOf(1, 2, 3))

        shouldThrow<DbKeyUnavailable> { DbKeyManager.forApp(context).useKey { } }.reason shouldBe
            Reason.KEY_FILE_MALFORMED
    }

    @Test
    fun `without a working Keystore a fresh install fails closed and writes nothing`() {
        var called = false

        shouldThrow<DbKeyUnavailable> { DbKeyManager.forApp(context).useKey { called = true } }.reason shouldBe
            Reason.KEK_UNUSABLE
        called shouldBe false
        context.noBackupFilesDir
            .listFiles()
            .orEmpty()
            .toList() shouldBe emptyList()
    }

    @Test
    fun `the KEK alias and key file name are pinned, because renaming either orphans existing data`() {
        AndroidKeystoreKek.ALIAS shouldBe "pft_db_kek"
        DbKeyManager.KEY_FILE_NAME shouldBe "pft_db_key.bin"
    }
}
