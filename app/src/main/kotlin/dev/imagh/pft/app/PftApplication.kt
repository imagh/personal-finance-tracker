package dev.imagh.pft.app

import android.app.Application
import dev.imagh.pft.app.di.AppContainer

/**
 * The application class. It owns the process-wide [container], which opens the encrypted database
 * (off the main thread, once) and builds the services on it.
 *
 * [container] is created on first use but opens nothing until someone calls `container.open()`, so a
 * process started for another reason (a test, and later M1b's SMS receiver, which will call the same
 * `open()`) opens the database at most once, and only when it needs it.
 *
 * `open` only so a Robolectric test can substitute a container with a slow or failing opener; nothing
 * in the app subclasses it.
 */
open class PftApplication : Application() {
    /** The process-wide container; see [AppContainer.open]. */
    open val container: AppContainer by lazy { AppContainer.forApp(this) }
}
