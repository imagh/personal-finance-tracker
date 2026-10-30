package dev.imagh.pft.app.testing

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.data.PftDatabase

/**
 * The one way app tests get a database: a real [PftDatabase] over in-memory SQLite (Robolectric's
 * native SQLite), built through [PftDatabase.create], the production entry point, so the migrations
 * and the audit triggers are wired exactly as in the app. Until A4b the only difference is the
 * missing file and encryption.
 *
 * Close the database in an `@After`. Suspend DAO calls are made from `runSuspending`.
 */
object TestDatabase {
    /** A fresh, empty in-memory database at the latest schema version. */
    fun create(context: Context = ApplicationProvider.getApplicationContext()): PftDatabase =
        PftDatabase.create(context, name = null)

    /** [PftDatabase] plus the throwaway `sample_thing` table (see `SampleThing`), for the shared-block tests. */
    fun createWithSample(context: Context = ApplicationProvider.getApplicationContext()): SampleDatabase =
        PftDatabase.build(context, SampleDatabase::class.java, name = null, openHelperFactory = null)
}
