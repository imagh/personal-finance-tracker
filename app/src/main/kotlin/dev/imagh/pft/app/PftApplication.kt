package dev.imagh.pft.app

import android.app.Application
import dev.imagh.pft.app.data.DatabaseFactory
import dev.imagh.pft.app.data.DatabaseStartup

/**
 * The application class. It owns the one opening of the encrypted database ([startup]); A5's
 * `AppContainer` takes this over and builds the services on the open database.
 *
 * [startup] is opened on first use (by [MainActivity]) rather than in `onCreate`, so a process
 * started for another reason (a test, and later M1b's SMS receiver, which will ask for the same
 * `startup`) opens it at most once, and only when it needs it. It runs on the calling thread: SQLCipher's
 * key derivation and any migration take a moment, so A5 moves it off the main thread.
 */
class PftApplication : Application() {
    /** The result of opening the database; opened once, on first access. */
    val startup: DatabaseStartup by lazy { DatabaseStartup.open(DatabaseFactory.forApp(this)) }
}
