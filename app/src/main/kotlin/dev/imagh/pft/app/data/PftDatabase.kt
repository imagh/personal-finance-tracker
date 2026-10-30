package dev.imagh.pft.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import dev.imagh.pft.app.data.audit.AuditDao
import dev.imagh.pft.app.data.audit.AuditEntryEntity
import dev.imagh.pft.app.data.identity.DeviceEntity
import dev.imagh.pft.app.data.identity.IdentityDao
import dev.imagh.pft.app.data.identity.UserProfileEntity
import dev.imagh.pft.app.data.settings.AppSettingDao
import dev.imagh.pft.app.data.settings.AppSettingEntity

/**
 * The app's Room database, `pft.db` (plan §4). Version 1 holds the identity, settings and audit
 * tables; each later slice adds its tables through its own migration and a migration test.
 *
 * Schemas are exported to `app/schemas/` and committed. **Build it only with [create]** (production
 * and tests alike): it adds the migrations and the callback that installs and repairs the audit
 * triggers, so the audit trail cannot end up unprotected. In the app the file is encrypted:
 * [DatabaseFactory] passes SQLCipher's open-helper factory to [create]. Tests use the framework's
 * SQLite in memory (Robolectric), and the instrumented `EncryptedDatabaseTest` covers SQLCipher.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        DeviceEntity::class,
        AppSettingEntity::class,
        AuditEntryEntity::class,
    ],
    version = PftDatabase.VERSION,
    exportSchema = true,
)
abstract class PftDatabase : RoomDatabase() {
    companion object {
        /** The current schema version; bump it with a migration in [Migrations] and a migration test. */
        const val VERSION = 1

        /** The database file name. */
        const val NAME = "pft.db"

        /**
         * The one way to build a [PftDatabase]. Registers [Migrations.ALL] and the audit-trigger
         * callback ([AuditTriggers.callback]).
         *
         * @param name the database file, or null for an in-memory database (tests).
         * @param openHelperFactory a custom open helper, SQLCipher's in the app ([DatabaseFactory]);
         *   null uses the framework's.
         */
        fun create(
            context: Context,
            name: String? = NAME,
            openHelperFactory: SupportSQLiteOpenHelper.Factory? = null,
        ): PftDatabase = build(context, PftDatabase::class.java, name, openHelperFactory)

        /** [create] for a subclass (the tests' `SampleDatabase`), with the same migrations and triggers. */
        internal fun <T : PftDatabase> build(
            context: Context,
            databaseClass: Class<T>,
            name: String?,
            openHelperFactory: SupportSQLiteOpenHelper.Factory?,
        ): T {
            val appContext = context.applicationContext
            val builder =
                if (name == null) {
                    Room.inMemoryDatabaseBuilder(appContext, databaseClass)
                } else {
                    Room.databaseBuilder(appContext, databaseClass, name)
                }
            if (openHelperFactory != null) builder.openHelperFactory(openHelperFactory)
            Migrations.ALL.forEach { builder.addMigrations(it) }
            return builder.addCallback(AuditTriggers.callback).build()
        }
    }

    /** Users and devices. */
    abstract fun identityDao(): IdentityDao

    /** The audit trail (insert and select only). */
    abstract fun auditDao(): AuditDao

    /** Runtime settings. */
    abstract fun appSettingDao(): AppSettingDao
}
