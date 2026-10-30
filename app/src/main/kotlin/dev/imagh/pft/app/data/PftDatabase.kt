package dev.imagh.pft.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
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
 * Schemas are exported to `app/schemas/` and committed. Build it only through a builder that also
 * calls [installAuditTriggers]. Until A4b the file is plaintext (tests use it in memory); A4b
 * opens it through SQLCipher and adds the key handling.
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
    }

    /** Users and devices. */
    abstract fun identityDao(): IdentityDao

    /** The audit trail (insert and select only). */
    abstract fun auditDao(): AuditDao

    /** Runtime settings. */
    abstract fun appSettingDao(): AppSettingDao
}
