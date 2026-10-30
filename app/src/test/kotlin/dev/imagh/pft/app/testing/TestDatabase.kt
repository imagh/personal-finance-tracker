package dev.imagh.pft.app.testing

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.installAuditTriggers

/**
 * The one way app tests get a database: a real [PftDatabase] over in-memory SQLite (Robolectric's
 * native SQLite), built exactly like production except for the file and, until A4b, the
 * encryption. The audit triggers are installed the same way production does it
 * ([installAuditTriggers]), so every integration test runs against the immutable audit trail.
 *
 * Close the database in an `@After`. Suspend DAO calls are made from `runBlocking`.
 */
object TestDatabase {
    /** A fresh, empty in-memory database at the latest schema version. */
    fun create(context: Context = ApplicationProvider.getApplicationContext()): PftDatabase =
        Room
            .inMemoryDatabaseBuilder(context, PftDatabase::class.java)
            .installAuditTriggers()
            .build()

    /** [PftDatabase] plus the throwaway `sample_thing` table (see `SampleThing`), for the shared-block tests. */
    fun createWithSample(context: Context = ApplicationProvider.getApplicationContext()): SampleDatabase =
        Room
            .inMemoryDatabaseBuilder(context, SampleDatabase::class.java)
            .installAuditTriggers()
            .build()
}
