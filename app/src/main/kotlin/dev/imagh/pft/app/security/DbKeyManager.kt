package dev.imagh.pft.app.security

import android.content.Context
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.GeneralSecurityException
import java.security.ProviderException
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Owns the SQLCipher database key (plan decision 9, PRD SEC-1).
 *
 * The key is 32 random bytes from [SecureRandom]. It is stored only wrapped: AES-256-GCM under a
 * key-encryption key (KEK) that lives in the Android Keystore ([KekSource]), in [keyFile]
 * (`noBackupFilesDir/`[KEY_FILE_NAME], never backed up), laid out as [WrappedKeyFormat].
 *
 * **Using the key.** [useKey] hands the raw key to a block and wipes it (fills it with zeros) when
 * the block returns or throws, so there is no way to get the key without the wipe. Open the database
 * inside the block; do not keep the array or a copy of it.
 *
 * **Fail closed.** The decision is made from what is on disk, never from a caller's flag:
 * - the key file exists: unwrap it. Any problem (malformed or altered file, KEK missing, invalidated
 *   or failing, wrong KEK) throws [DbKeyUnavailable]. A new key is **never** generated over an
 *   existing key file, because it could not read the data the old key encrypted.
 * - no key file, but [databaseFile] exists: throw [DbKeyUnavailable] ([Reason.KEY_FILE_MISSING]).
 *   Creating a key now would make the existing database unreadable for good.
 * - neither exists (first run): create the KEK and the key, write the key file, then hand out the
 *   key **read back from that file**, so a key that was not persisted can never reach the database.
 *
 * So callers do not need to know whether a database exists: the key must be obtained before the
 * database file is created, and once it is, a lost key file can only ever fail closed.
 *
 * **Crash safety.** The key file is written to a temp file, synced, then renamed over the final name
 * (atomic on the same file system), and the directory is synced ([syncDirectory]). A crash leaves
 * either no key file (and no database yet, so the next start simply creates one) or a complete one;
 * a leftover temp file is ignored and overwritten. Concurrent calls in this process are serialised.
 *
 * **Not user-authentication-bound.** The KEK can be used while the device or the app is locked,
 * because M1b's SMS receiver writes transactions to the database in the background. App lock (SEC-2)
 * is a UI gate; at-rest protection is SQLCipher plus the Keystore-held KEK.
 *
 * **No leaks.** Key material is never logged and never put in an exception; [DbKeyUnavailable]
 * carries a fixed reason and a class name only, never a path or a cause.
 *
 * Build it with [forApp] in production; the constructor is for tests.
 *
 * @param keyFile where the wrapped key is stored.
 * @param databaseFile the encrypted database the key belongs to; only its existence is checked.
 * @param kek where the KEK lives.
 * @param syncDirectory flushes a directory's entries to storage after the rename; best effort, must
 *   not throw.
 */
class DbKeyManager internal constructor(
    private val keyFile: File,
    private val databaseFile: File,
    private val kek: KekSource,
    private val syncDirectory: (File) -> Unit,
) {
    private val random = SecureRandom()
    private val keyDirectory: File =
        requireNotNull(keyFile.absoluteFile.parentFile) { "the key file must be inside a directory" }

    /**
     * Runs [block] with the raw 32-byte database key and returns its result. The array is wiped as
     * soon as [block] returns or throws; an exception from [block] propagates unchanged.
     *
     * Creates the key on first run (see the class doc for when that is allowed).
     *
     * @throws DbKeyUnavailable if the key cannot be produced; [block] is not called. Never recovered
     *   from by creating a new key.
     */
    fun <T> useKey(block: (ByteArray) -> T): T {
        val key = synchronized(LOCK) { loadOrCreate() }
        try {
            return block(key)
        } finally {
            key.fill(0)
        }
    }

    private fun loadOrCreate(): ByteArray =
        when {
            keyFile.exists() -> load()
            databaseFile.exists() -> unavailable(Reason.KEY_FILE_MISSING)
            else -> create()
        }

    /** Unwraps the key file. The checks run cheapest first: format, then KEK, then authentication. */
    private fun load(): ByteArray {
        val decoded = WrappedKeyFormat.decode(readKeyFile()) ?: unavailable(Reason.KEY_FILE_MALFORMED)
        val wrappingKey = keystore { kek.existing() } ?: unavailable(Reason.KEK_MISSING)
        val cipher =
            keystore {
                Cipher.getInstance(TRANSFORMATION).apply {
                    init(Cipher.DECRYPT_MODE, wrappingKey, GCMParameterSpec(WrappedKeyFormat.TAG_BITS, decoded.iv))
                    updateAAD(WrappedKeyFormat.aad())
                }
            }
        val key =
            try {
                cipher.doFinal(decoded.sealed)
            } catch (e: AEADBadTagException) {
                unavailable(Reason.KEY_FILE_AUTH_FAILED, e)
            } catch (e: GeneralSecurityException) {
                unavailable(Reason.KEK_UNUSABLE, e)
            } catch (e: ProviderException) {
                unavailable(Reason.KEK_UNUSABLE, e)
            }
        // Unreachable for an authenticated version-1 file (its ciphertext length is fixed); kept so
        // a key of the wrong length can never reach SQLCipher.
        if (key.size != WrappedKeyFormat.KEY_LENGTH) {
            key.fill(0)
            unavailable(Reason.KEY_FILE_MALFORMED)
        }
        return key
    }

    private fun readKeyFile(): ByteArray =
        try {
            // A file this large is not ours; do not read it into memory.
            if (keyFile.length() > MAX_KEY_FILE_BYTES) ByteArray(0) else keyFile.readBytes()
        } catch (e: IOException) {
            unavailable(Reason.KEY_FILE_IO, e)
        }

    /** First run: a fresh KEK (replacing any left by an earlier install), a fresh key, persisted. */
    private fun create(): ByteArray {
        val wrappingKey = keystore { kek.createNew() }
        val raw = ByteArray(WrappedKeyFormat.KEY_LENGTH)
        try {
            random.nextBytes(raw)
            writeAtomically(keystore { wrap(wrappingKey, raw) })
        } finally {
            raw.fill(0)
        }
        return load()
    }

    private fun wrap(
        wrappingKey: SecretKey,
        raw: ByteArray,
    ): ByteArray {
        // No IV is passed: the Keystore requires randomised encryption and picks its own.
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, wrappingKey)
        cipher.updateAAD(WrappedKeyFormat.aad())
        val sealed = cipher.doFinal(raw)
        return WrappedKeyFormat.encode(cipher.iv, sealed)
    }

    private fun writeAtomically(bytes: ByteArray) {
        val temp = File(keyDirectory, keyFile.name + TEMP_SUFFIX)
        try {
            keyDirectory.mkdirs()
            FileOutputStream(temp).use { out ->
                out.write(bytes)
                out.fd.sync()
            }
            Files.move(temp.toPath(), keyFile.toPath(), StandardCopyOption.ATOMIC_MOVE)
        } catch (e: IOException) {
            temp.delete()
            unavailable(Reason.KEY_FILE_IO, e)
        }
        syncDirectory(keyDirectory)
    }

    /** Runs a Keystore or JCA operation, turning its failure into [Reason.KEK_UNUSABLE]. */
    private inline fun <R> keystore(operation: () -> R): R =
        try {
            operation()
        } catch (e: GeneralSecurityException) {
            unavailable(Reason.KEK_UNUSABLE, e)
        } catch (e: ProviderException) {
            // The Android Keystore reports internal failures as this unchecked exception.
            unavailable(Reason.KEK_UNUSABLE, e)
        }

    /** Throws [DbKeyUnavailable], naming only the failure's class (never its message or the cause). */
    private fun unavailable(
        reason: Reason,
        failure: Exception? = null,
    ): Nothing = throw DbKeyUnavailable(reason, failure?.javaClass?.simpleName)

    companion object {
        /** The wrapped key's file name in `noBackupFilesDir`. */
        const val KEY_FILE_NAME = "pft_db_key.bin"

        private const val TEMP_SUFFIX = ".tmp"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val MAX_KEY_FILE_BYTES = 4096L

        /** Serialises key creation across instances, so two callers cannot both create a key. */
        private val LOCK = Any()

        /**
         * The app's key manager: the wrapped key in `noBackupFilesDir/`[KEY_FILE_NAME], guarding the
         * database [PftDatabase.NAME], with the KEK in the Android Keystore ([AndroidKeystoreKek.ALIAS]).
         * Cheap to build; nothing is read until [useKey].
         */
        fun forApp(context: Context): DbKeyManager =
            DbKeyManager(
                keyFile = File(context.noBackupFilesDir, KEY_FILE_NAME),
                databaseFile = context.getDatabasePath(PftDatabase.NAME),
                kek = AndroidKeystoreKek(),
                syncDirectory = ::fsyncDirectory,
            )

        /**
         * Flushes [dir]'s entries, so the renamed key file survives a power cut. Best effort: if the
         * directory cannot be synced the file is still complete (the rename is atomic), and a lost
         * entry can only ever fail closed later ([Reason.KEY_FILE_MISSING]), never create a key.
         */
        internal fun fsyncDirectory(dir: File) {
            try {
                val fd = Os.open(dir.path, OsConstants.O_RDONLY, 0)
                try {
                    Os.fsync(fd)
                } finally {
                    Os.close(fd)
                }
            } catch (ignored: ErrnoException) {
                // Best effort, see above; the error names a path, so it is not reported.
            }
        }
    }
}
