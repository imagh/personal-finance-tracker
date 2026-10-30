package dev.imagh.pft.app.data

import dev.imagh.pft.app.security.DbKeyUnavailable

/**
 * How opening the app database ended, as the UI needs to know it.
 *
 * [KeyUnavailable] is the fail-closed outcome (plan decision 9): the database key cannot be
 * produced, so the app shows `KeyUnavailableScreen` and does nothing else: no new key, no new
 * database over existing data, no file deleted. Any other failure (a backup that could not be made,
 * a database SQLCipher cannot open) is not caught here and ends the start, which also leaves the
 * data untouched; it is not turned into a screen yet.
 */
sealed interface DatabaseStartup {
    /** The database is open and migrated. */
    data class Ready(
        val database: PftDatabase,
    ) : DatabaseStartup

    /** The key cannot be produced; [reason] is a fixed code that is safe to show. */
    data class KeyUnavailable(
        val reason: DbKeyUnavailable.Reason,
    ) : DatabaseStartup

    companion object {
        /** Opens the database with [factory], turning a [DbKeyUnavailable] into [KeyUnavailable]. */
        fun open(factory: DatabaseFactory): DatabaseStartup =
            try {
                Ready(factory.open())
            } catch (e: DbKeyUnavailable) {
                KeyUnavailable(e.reason)
            }
    }
}
