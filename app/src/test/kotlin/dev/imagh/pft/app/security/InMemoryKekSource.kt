package dev.imagh.pft.app.security

import java.security.GeneralSecurityException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * A [KekSource] for JVM tests: a real AES-256 key from the JDK, held in memory instead of the
 * Android Keystore. Wrapping and unwrapping therefore run real AES-GCM, so the tests see genuine
 * authentication failures; only where the key lives is faked.
 *
 * [key] is the "Keystore entry": tests clear it (KEK lost), replace it (KEK regenerated), or set
 * [failure] to make the "Keystore" throw.
 */
internal class InMemoryKekSource : KekSource {
    /** The current entry under the alias, or null when there is none. */
    var key: SecretKey? = null

    /**
     * When set, both operations throw it, as a broken Keystore would: a [GeneralSecurityException]
     * or the Keystore's [java.security.ProviderException].
     */
    var failure: Exception? = null

    override fun existing(): SecretKey? {
        failure?.let { throw it }
        return key
    }

    override fun createNew(): SecretKey {
        failure?.let { throw it }
        return newAesKey().also { key = it }
    }

    companion object {
        /** A fresh random AES-256 key, as the Keystore would generate. */
        fun newAesKey(): SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
    }
}
