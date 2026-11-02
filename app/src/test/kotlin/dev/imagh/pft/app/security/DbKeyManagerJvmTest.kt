package dev.imagh.pft.app.security

import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Base64

/**
 * [DbKeyManager]'s file format and fail-closed rules on the JVM. The KEK is a real AES key held in
 * memory ([InMemoryKekSource]), so every wrap and unwrap is real AES-GCM; the Keystore-backed
 * source is covered by the instrumented `DbKeyManagerTest`.
 */
class DbKeyManagerJvmTest {
    @get:Rule
    val temp = TemporaryFolder()

    private val kek = InMemoryKekSource()
    private val keyFile by lazy { File(temp.root, "no_backup/${DbKeyManager.KEY_FILE_NAME}") }
    private val databaseFile by lazy { File(temp.root, "databases/pft.db") }

    private fun manager() = DbKeyManager(keyFile, databaseFile, kek, syncDirectory = {})

    /** The key as the caller saw it, copied out before the manager wipes it. */
    private fun DbKeyManager.keyCopy(): ByteArray = useKey { it.copyOf() }

    @Test
    fun `a fresh install creates a 32-byte key and a later start reads back the same bytes`() {
        val first = manager().keyCopy()

        first.size shouldBe 32
        keyFile.exists() shouldBe true
        manager().keyCopy() shouldBe first
    }

    @Test
    fun `two installs get different keys`() {
        val one = manager().keyCopy()
        val otherKek = InMemoryKekSource()
        val otherDir = temp.newFolder("other")
        val two = DbKeyManager(File(otherDir, "key"), File(otherDir, "db"), otherKek, syncDirectory = {}).keyCopy()

        one shouldNotBe two
    }

    @Test
    fun `the key file holds the key wrapped, never in the clear`() {
        val key = manager().keyCopy()
        val stored = keyFile.readBytes()

        stored.size shouldBe WrappedKeyFormat.ENCODED_LENGTH
        stored.copyOfRange(0, 5) shouldBe "PFTK".toByteArray() + 1.toByte()
        stored.containsRun(key) shouldBe false
    }

    @Test
    fun `the key bytes are wiped once the block returns`() {
        var seen: ByteArray? = null
        val result =
            manager().useKey {
                seen = it
                "opened"
            }

        result shouldBe "opened"
        seen!!.all { it == 0.toByte() } shouldBe true
    }

    @Test
    fun `the key bytes are wiped when the block throws, and the block's exception propagates`() {
        var seen: ByteArray? = null
        val failure =
            shouldThrow<IllegalStateException> {
                manager().useKey {
                    seen = it
                    error("open failed")
                }
            }

        failure.message shouldBe "open failed"
        seen!!.all { it == 0.toByte() } shouldBe true
    }

    @Test
    fun `an altered ciphertext fails closed and is never replaced by a new key`() {
        val original = manager().keyCopy()
        val good = keyFile.readBytes()
        keyFile.writeBytes(good.copyOf().also { it[it.size - 20] = (it[it.size - 20] + 1).toByte() })
        val altered = keyFile.readBytes()

        failure { manager().keyCopy() }.reason shouldBe Reason.KEY_FILE_AUTH_FAILED
        keyFile.readBytes() shouldBe altered
        keyFile.writeBytes(good)
        manager().keyCopy() shouldBe original
    }

    @Test
    fun `an altered tag fails authentication`() {
        manager().keyCopy()
        keyFile.writeBytes(keyFile.readBytes().also { it[it.size - 1] = (it[it.size - 1].toInt() xor 1).toByte() })

        failure { manager().keyCopy() }.reason shouldBe Reason.KEY_FILE_AUTH_FAILED
    }

    @Test
    fun `an altered IV fails authentication`() {
        manager().keyCopy()
        keyFile.writeBytes(
            keyFile.readBytes().also {
                it[WrappedKeyFormat.HEADER_LENGTH] =
                    (it[WrappedKeyFormat.HEADER_LENGTH].toInt() xor 1).toByte()
            },
        )

        failure { manager().keyCopy() }.reason shouldBe Reason.KEY_FILE_AUTH_FAILED
    }

    @Test
    fun `a key file wrapped by another KEK fails authentication, as after a Keystore reset`() {
        manager().keyCopy()
        kek.key = InMemoryKekSource.newAesKey()

        failure { manager().keyCopy() }.reason shouldBe Reason.KEY_FILE_AUTH_FAILED
    }

    @Test
    fun `an unknown magic, version or IV length is malformed, not reinterpreted`() {
        manager().keyCopy()
        val good = keyFile.readBytes()
        for (offset in 0 until WrappedKeyFormat.HEADER_LENGTH) {
            keyFile.writeBytes(good.copyOf().also { it[offset] = (it[offset].toInt() xor 0x40).toByte() })

            failure { manager().keyCopy() }.reason shouldBe Reason.KEY_FILE_MALFORMED
        }
    }

    @Test
    fun `a truncated, empty or oversized key file is malformed`() {
        manager().keyCopy()
        val good = keyFile.readBytes()
        val variants =
            listOf(
                ByteArray(0),
                good.copyOf(WrappedKeyFormat.HEADER_LENGTH),
                good.copyOf(good.size - 1),
                good + 0.toByte(),
                ByteArray(1 shl 20),
            )
        for (bytes in variants) {
            keyFile.writeBytes(bytes)

            failure { manager().keyCopy() }.reason shouldBe Reason.KEY_FILE_MALFORMED
            keyFile.readBytes() shouldBe bytes
        }
    }

    @Test
    fun `a key file without its KEK fails closed and creates no new KEK`() {
        manager().keyCopy()
        val stored = keyFile.readBytes()
        kek.key = null

        failure { manager().keyCopy() }.reason shouldBe Reason.KEK_MISSING
        kek.key.shouldBeNull()
        keyFile.readBytes() shouldBe stored
    }

    @Test
    fun `a Keystore that cannot produce the KEK fails closed`() {
        manager().keyCopy()
        kek.failure = java.security.UnrecoverableKeyException("keystore said no")

        failure { manager().keyCopy() }.reason shouldBe Reason.KEK_UNUSABLE
    }

    @Test
    fun `a KEK the cipher refuses fails closed, as an invalidated Keystore key does`() {
        manager().keyCopy()
        // KeyPermanentlyInvalidatedException is an InvalidKeyException thrown by Cipher.init; a key
        // the JDK's AES refuses at init is the same failure on the JVM.
        kek.key = javax.crypto.spec.SecretKeySpec(ByteArray(7), "AES")

        failure { manager().keyCopy() }.reason shouldBe Reason.KEK_UNUSABLE
    }

    @Test
    fun `an existing database without a key file fails closed and writes no key`() {
        databaseFile.parentFile!!.mkdirs()
        databaseFile.writeBytes(byteArrayOf(1, 2, 3))
        var called = false

        failure { manager().useKey { called = true } }.reason shouldBe Reason.KEY_FILE_MISSING
        called shouldBe false
        keyFile.exists() shouldBe false
        kek.key.shouldBeNull()
        databaseFile.readBytes() shouldBe byteArrayOf(1, 2, 3)
    }

    @Test
    fun `an existing key file is used even when the database does not exist yet`() {
        val key = manager().keyCopy()

        databaseFile.exists() shouldBe false
        manager().keyCopy() shouldBe key
    }

    @Test
    fun `a KEK left over from an earlier install does not block a fresh install`() {
        kek.key = InMemoryKekSource.newAesKey()

        val key = manager().keyCopy()

        manager().keyCopy() shouldBe key
    }

    @Test
    fun `a half-written temp file from a crash is ignored and replaced`() {
        val temp = File(keyFile.parentFile, "${keyFile.name}.tmp")
        temp.parentFile!!.mkdirs()
        temp.writeBytes(byteArrayOf(9, 9, 9))

        val key = manager().keyCopy()

        temp.exists() shouldBe false
        manager().keyCopy() shouldBe key
    }

    @Test
    fun `a key that could not be persisted is never handed out`() {
        // The key directory's place is taken by a plain file, so the write cannot happen.
        keyFile.parentFile!!.parentFile!!.mkdirs()
        keyFile.parentFile!!.writeBytes(byteArrayOf(0))
        var called = false

        failure { manager().useKey { called = true } }.reason shouldBe Reason.KEY_FILE_IO
        called shouldBe false
        keyFile.exists() shouldBe false
    }

    @Test
    fun `a Keystore that cannot create a KEK on first run writes nothing`() {
        kek.failure = java.security.ProviderException("keystore daemon down")
        var called = false

        failure { manager().useKey { called = true } }.reason shouldBe Reason.KEK_UNUSABLE
        called shouldBe false
        keyFile.exists() shouldBe false
        keyFile.parentFile
            ?.listFiles()
            .orEmpty()
            .toList()
            .shouldBeEmpty()
    }

    @Test
    fun `failures name neither the key nor any path, and chain no cause`() {
        val key = manager().keyCopy()
        val good = keyFile.readBytes()
        val failures =
            buildList {
                keyFile.writeBytes(good.copyOf().also { it[it.size - 1] = (it[it.size - 1].toInt() xor 1).toByte() })
                add(failure { manager().keyCopy() })
                keyFile.writeBytes(ByteArray(3))
                add(failure { manager().keyCopy() })
                keyFile.writeBytes(good)
                kek.failure = java.security.UnrecoverableKeyException("${temp.root.absolutePath} ${key.toHex()}")
                add(failure { manager().keyCopy() })
            }

        for (f in failures) {
            val text = f.toString() + f.stackTraceToString()
            text shouldNotContain temp.root.absolutePath
            text shouldNotContain key.toHex()
            text shouldNotContain Base64.getEncoder().encodeToString(key)
            f.cause.shouldBeNull()
        }
        failures.map { it.message } shouldBe
            listOf(
                "Database key unavailable: KEY_FILE_AUTH_FAILED (AEADBadTagException)",
                "Database key unavailable: KEY_FILE_MALFORMED",
                "Database key unavailable: KEK_UNUSABLE (UnrecoverableKeyException)",
            )
    }

    private fun failure(block: () -> Unit): DbKeyUnavailable = shouldThrow<DbKeyUnavailable>(block)

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun ByteArray.containsRun(run: ByteArray): Boolean =
        (0..size - run.size).any { start -> run.indices.all { this[start + it] == run[it] } }
}
