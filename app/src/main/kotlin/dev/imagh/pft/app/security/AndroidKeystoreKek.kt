package dev.imagh.pft.app.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.KeyStoreException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * The production [KekSource]: an AES-256 key for GCM in the Android Keystore under [alias]. The
 * key material never leaves the Keystore (hardware-backed where the device has it); this class only
 * hands out the Keystore's opaque handle.
 *
 * The key is deliberately **not** bound to user authentication (`setUserAuthenticationRequired(false)`)
 * nor to an unlocked device (`setUnlockedDeviceRequired(false)`): M1b's SMS receiver must open the
 * database and write while the device or the app is locked. App lock is a UI gate (SEC-2); binding
 * the KEK to authentication would make background capture impossible, not the data safer at rest.
 * Randomised encryption stays required, so every wrap gets a fresh Keystore-chosen IV.
 *
 * @param alias the Keystore alias; production uses [ALIAS]. Instrumented tests pass their own so
 *   they never touch the app's real key.
 */
internal class AndroidKeystoreKek(
    private val alias: String = ALIAS,
) : KekSource {
    override fun existing(): SecretKey? {
        val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
        val key = keyStore.getKey(alias, null) ?: return null
        return key as? SecretKey ?: throw KeyStoreException("the KEK alias holds a non-secret key")
    }

    override fun createNew(): SecretKey {
        val spec =
            KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(KEY_SIZE_BITS)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .setUserAuthenticationRequired(false)
                .setUnlockedDeviceRequired(false)
                .build()
        // Generating under an existing alias replaces that key; DbKeyManager only calls this when
        // there is no wrapped key file, so nothing the old key protected is lost.
        return KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
            .apply { init(spec) }
            .generateKey()
    }

    companion object {
        /** The KEK's alias (plan decision 9). Permanent: a new alias cannot unwrap existing key files. */
        const val ALIAS = "pft_db_kek"

        private const val PROVIDER = "AndroidKeyStore"
        private const val KEY_SIZE_BITS = 256
    }
}
