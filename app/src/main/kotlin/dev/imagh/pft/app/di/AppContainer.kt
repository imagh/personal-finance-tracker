package dev.imagh.pft.app.di

import android.content.Context
import dev.imagh.pft.app.data.DatabaseFactory
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.common.EpochMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.time.Clock

/**
 * The process-wide owner of *when* the encrypted database opens (the services are [AppServices]).
 * `PftApplication` holds the one instance; the UI ([dev.imagh.pft.app.ui.LaunchViewModel]) and, later,
 * M1b's SMS receiver ask it for the services through [open].
 *
 * Constructing it opens nothing. [open] guarantees:
 * - **off the caller's thread:** the open runs on [io] (SQLCipher's key derivation, the backup copy and
 *   any migration take a moment), never on the main thread, and the caller suspends rather than blocks;
 * - **once:** the first call starts the open and every caller, concurrent or later, shares its one
 *   result (the same [AppServices] instance);
 * - **independent of any one caller:** the open belongs to the container, not to the first caller's
 *   coroutine, so a caller that is cancelled (a screen closed mid-open) does not abort it for the others;
 * - **fail closed:** a key that cannot be produced is the value [AppStart.KeyUnavailable]; any other
 *   failure is rethrown to every caller, and nothing is cached as [AppStart.Ready]. The container never
 *   retries, so a failed start cannot create a second database over the first attempt.
 *
 * @param openDatabase opens the database. The app passes the real factory ([forApp]); it is a function,
 *   not an interface, as a test seam for a slow open and for a key that is unavailable. It is called
 *   on [io], at most once.
 * @param baseClock the clock the services are built on, truncated to whole milliseconds (the A4a
 *   criterion); tests pass a fixed clock.
 * @param io where the open runs.
 */
class AppContainer(
    private val openDatabase: () -> DatabaseStartup,
    private val baseClock: Clock = Clock.systemUTC(),
    io: CoroutineDispatcher = Dispatchers.IO,
) {
    // Process-scoped and never cancelled: the open must outlive any one caller. SupervisorJob so a
    // failed open reaches callers through await() and nothing else.
    private val scope = CoroutineScope(SupervisorJob() + io)

    // `lazy` is synchronized, so concurrent first callers start exactly one open.
    private val opening: Deferred<AppStart> by lazy {
        scope.async {
            when (val started = openDatabase()) {
                is DatabaseStartup.Ready ->
                    AppStart.Ready(AppServices(started.database, EpochMillis.clock(baseClock)))
                is DatabaseStartup.KeyUnavailable -> AppStart.KeyUnavailable(started.reason)
            }
        }
    }

    /**
     * Opens the database on the first call and returns the shared result; later and concurrent calls
     * return the same one.
     *
     * @throws Exception whatever the open threw other than a key failure (for example
     *   `PreMigrationBackupFailed`); every caller gets it and the database stays untouched.
     */
    suspend fun open(): AppStart = opening.await()

    companion object {
        /** The app's container: the real encrypted database, `pft.db`, opened on first [open]. */
        fun forApp(context: Context): AppContainer {
            val app = context.applicationContext
            return AppContainer(openDatabase = { DatabaseStartup.open(DatabaseFactory.forApp(app)) })
        }
    }
}
