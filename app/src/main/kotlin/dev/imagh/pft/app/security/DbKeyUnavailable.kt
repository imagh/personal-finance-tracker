package dev.imagh.pft.app.security

/**
 * The database key cannot be produced, so the database must not be opened or created (fail closed,
 * plan decision 9). The app shows an error screen; it never generates a replacement key, because a
 * new key cannot read the existing data and would orphan it.
 *
 * The message carries only [reason] and, when there was one, the simple class name of the
 * underlying failure ([causeType], for example `AEADBadTagException`). The underlying exception is
 * deliberately **not** chained as [cause]: I/O and Keystore exceptions put file paths and provider
 * details in their messages, and this exception may reach logs and error screens.
 *
 * @property reason what went wrong, safe to show or log.
 * @property causeType the simple class name of the underlying failure, or null.
 */
class DbKeyUnavailable(
    val reason: Reason,
    val causeType: String? = null,
) : Exception(message(reason, causeType)) {
    /** Why the key is unavailable. None of these is recovered from automatically. */
    enum class Reason {
        /** The wrapped key file exists but the Keystore has no KEK under the alias. */
        KEK_MISSING,

        /** The Keystore refused or failed: KEK invalidated or unrecoverable, or a Keystore error. */
        KEK_UNUSABLE,

        /** The wrapped key file is truncated, oversized, or not in a known format version. */
        KEY_FILE_MALFORMED,

        /** The wrapped key failed GCM authentication: the file was altered, or a different KEK wrapped it. */
        KEY_FILE_AUTH_FAILED,

        /** The database file exists but the wrapped key file does not; a new key would orphan the data. */
        KEY_FILE_MISSING,

        /** The wrapped key file could not be read or written. */
        KEY_FILE_IO,
    }

    private companion object {
        fun message(
            reason: Reason,
            causeType: String?,
        ): String = "Database key unavailable: $reason" + if (causeType == null) "" else " ($causeType)"
    }
}
