package dev.imagh.pft.app.security

import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import androidx.test.platform.app.InstrumentationRegistry
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.security.KeyStore
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory

/**
 * [DbKeyManager] against the real Android Keystore, on the `pft35` emulator (PRD SEC-1): the KEK
 * is generated in the Keystore, the key survives a restart (a new manager), and a damaged key file
 * or a lost KEK fails closed instead of producing a new key.
 *
 * The tests run inside the app's process, so they use their own Keystore alias and a subdirectory
 * of `noBackupFilesDir`, and never touch the app's real key (`pft_db_kek`, `pft_db_key.bin`).
 */
class DbKeyManagerTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dir = File(context.noBackupFilesDir, "db-key-manager-test")
    private val keyFile = File(dir, DbKeyManager.KEY_FILE_NAME)
    private val kek = AndroidKeystoreKek(alias = TEST_ALIAS)

    private fun manager() = DbKeyManager(keyFile, File(dir, "test.db"), kek) { DbKeyManager.fsyncDirectory(it) }

    private fun DbKeyManager.keyCopy(): ByteArray = useKey { it.copyOf() }

    private fun keystoreEntry(): SecretKey? =
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.getKey(TEST_ALIAS, null) as SecretKey?

    @Before
    @After
    fun clean() {
        dir.deleteRecursively()
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(TEST_ALIAS)
    }

    @Test
    fun firstUseCreatesAndPersistsAWrappedKeyAndALaterManagerReadsTheSameBytes() {
        assertFalse(keyFile.exists())
        assertNull(keystoreEntry())

        val first = manager().keyCopy()

        assertEquals(32, first.size)
        assertEquals(WrappedKeyFormat.ENCODED_LENGTH.toLong(), keyFile.length())
        assertNotNull(keystoreEntry())
        assertArrayEquals(first, manager().keyCopy())
    }

    @Test
    fun aCorruptedWrappedKeyFailsClosedAndIsNotReplaced() {
        val original = manager().keyCopy()
        val good = keyFile.readBytes()
        val corrupted = good.copyOf().also { it[it.size - 1] = (it[it.size - 1].toInt() xor 1).toByte() }
        keyFile.writeBytes(corrupted)

        val failure = assertThrows(DbKeyUnavailable::class.java) { manager().keyCopy() }

        assertEquals(Reason.KEY_FILE_AUTH_FAILED, failure.reason)
        assertArrayEquals(corrupted, keyFile.readBytes())
        keyFile.writeBytes(good)
        assertArrayEquals(original, manager().keyCopy())
    }

    @Test
    fun aTruncatedWrappedKeyFailsClosed() {
        manager().keyCopy()
        keyFile.writeBytes(keyFile.readBytes().copyOf(10))

        val failure = assertThrows(DbKeyUnavailable::class.java) { manager().keyCopy() }

        assertEquals(Reason.KEY_FILE_MALFORMED, failure.reason)
    }

    @Test
    fun aKeyFileWhoseKeystoreKeyWasDeletedFailsClosedWithoutCreatingANewKey() {
        manager().keyCopy()
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(TEST_ALIAS)

        val failure = assertThrows(DbKeyUnavailable::class.java) { manager().keyCopy() }

        assertEquals(Reason.KEK_MISSING, failure.reason)
        assertNull(keystoreEntry())
    }

    @Test
    fun theKekIsAnAes256GcmKeystoreKeyThatWorksWithoutUserAuthentication() {
        manager().keyCopy()
        val key = checkNotNull(keystoreEntry())
        val factory = SecretKeyFactory.getInstance(key.algorithm, "AndroidKeyStore")
        val info = factory.getKeySpec(key, KeyInfo::class.java) as KeyInfo

        assertEquals(KeyProperties.KEY_ALGORITHM_AES, key.algorithm)
        assertEquals(256, info.keySize)
        assertTrue(info.blockModes.contentEquals(arrayOf(KeyProperties.BLOCK_MODE_GCM)))
        assertFalse(info.isUserAuthenticationRequired)
        assertEquals(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT, info.purposes)
    }

    private companion object {
        const val TEST_ALIAS = "pft_db_kek_instrumented_test"
    }
}
