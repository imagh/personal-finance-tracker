package dev.imagh.pft.app.security

import javax.crypto.SecretKey

/**
 * Where the database key's key-encryption key (KEK) lives: the Android Keystore in production.
 *
 * This is the one seam in [DbKeyManager], and it exists for a single reason: the Android Keystore
 * is an external boundary that the JVM (Robolectric included) cannot provide, so the wrapping,
 * file-format and fail-closed logic is unit-tested against an in-memory AES key instead. Both sides
 * return a plain [SecretKey] and [DbKeyManager] does the AES-GCM itself, so the code under test is
 * the code that ships.
 *
 * Implementations throw a [java.security.GeneralSecurityException] (or the Keystore's
 * [java.security.ProviderException]) when the store cannot produce a key; [DbKeyManager] turns that
 * into [DbKeyUnavailable].
 */
interface KekSource {
    /** The KEK, or null when no key exists under the alias. Never creates one. */
    fun existing(): SecretKey?

    /** Creates a new AES-256 KEK, replacing any key under the alias, and returns it. */
    fun createNew(): SecretKey
}
