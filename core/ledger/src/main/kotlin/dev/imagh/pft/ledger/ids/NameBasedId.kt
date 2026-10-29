package dev.imagh.pft.ledger.ids

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID

/**
 * Deterministic, name-based identifiers (UUIDv5, RFC 9562 §5.5).
 *
 * Used where two devices must derive the **same** ID for the same logical thing, so they can
 * never create divergent duplicates once sync lands. The motivating case (plan §3.2): a
 * recurring-generated transaction gets `NameBasedId.of("recurring-txn", ruleId, dueDate)`.
 *
 * UUIDv5 uses SHA-1 by specification. That is acceptable here: it derives an identifier from
 * values the app itself produced, it is not a signature or an integrity check, and an attacker
 * controls none of the inputs. An ID is never a secret.
 */
object NameBasedId {
    /** The app's fixed namespace UUID. **Never change it**: it would change every derived ID. */
    private const val APP_NAMESPACE = "8f6d3c1e-5b2a-4c97-9e04-7a1d2b6f0c35"

    /**
     * An ID for [kind] (what is being identified, e.g. `"recurring-txn"`) and [parts] (what makes
     * it unique, e.g. the rule id and the due date as ISO text).
     *
     * Every input is length-prefixed (`<utf8 byte length>:<text>`) before hashing, so the way the
     * parts are split matters: `("ab", "c")` and `("a", "bc")` give different IDs. Pinned by tests:
     * changing the encoding would change IDs that devices have already stored.
     */
    fun of(
        kind: String,
        vararg parts: String,
    ): String {
        val name =
            (listOf(kind) + parts)
                .joinToString(separator = "") { "${it.toByteArray(Charsets.UTF_8).size}:$it" }
        return uuid5(APP_NAMESPACE, name)
    }

    /**
     * The RFC 9562 UUIDv5 of [name] within [namespace] (a UUID in text form).
     *
     * @throws IllegalArgumentException if [namespace] is not a UUID.
     */
    fun uuid5(
        namespace: String,
        name: String,
    ): String {
        require(namespace.length == UUID_TEXT_LENGTH) { "Namespace is not a UUID" }
        val parsed =
            try {
                UUID.fromString(namespace)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Namespace is not a UUID", e)
            }
        val namespaceBytes =
            ByteBuffer
                .allocate(UUID_BYTES)
                .putLong(parsed.mostSignificantBits)
                .putLong(parsed.leastSignificantBits)
                .array()
        val digest = MessageDigest.getInstance("SHA-1").digest(namespaceBytes + name.toByteArray(Charsets.UTF_8))
        val bytes = ByteBuffer.wrap(digest.copyOf(UUID_BYTES))
        val high = (bytes.getLong() and VERSION_CLEAR) or VERSION_5
        val low = (bytes.getLong() and VARIANT_CLEAR) or VARIANT_10
        return UUID(high, low).toString()
    }

    private const val UUID_TEXT_LENGTH = 36
    private const val UUID_BYTES = 16
    private const val VERSION_CLEAR = -0xF001L // keeps every bit except the version nibble
    private const val VERSION_5 = 0x5000L
    private const val VARIANT_CLEAR = 0x3FFF_FFFF_FFFF_FFFFL
    private const val VARIANT_10 = Long.MIN_VALUE
}
