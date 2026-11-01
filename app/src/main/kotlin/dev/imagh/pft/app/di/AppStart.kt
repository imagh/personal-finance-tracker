package dev.imagh.pft.app.di

import dev.imagh.pft.app.security.DbKeyUnavailable

/**
 * How [AppContainer.open] ended, as the rest of the app needs to know it: the services to use, or the
 * fail-closed reason. Only [DbKeyUnavailable] becomes a value; any other failure to open (a backup
 * that could not be made, a database SQLCipher cannot open) is thrown to the caller and ends the
 * start without touching data, as `DatabaseStartup` documents.
 */
sealed interface AppStart {
    /** The database is open; [services] is the object graph over it. */
    data class Ready(
        val services: AppServices,
    ) : AppStart

    /** The database key cannot be produced; [reason] is a fixed code that is safe to show. */
    data class KeyUnavailable(
        val reason: DbKeyUnavailable.Reason,
    ) : AppStart
}
