package dev.imagh.pft.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.imagh.pft.app.data.audit.AuditEntryEntity
import dev.imagh.pft.app.data.identity.DeviceEntity
import dev.imagh.pft.app.data.identity.UserProfileEntity
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
    version = 1,
    exportSchema = true,
)
abstract class PftDatabase : RoomDatabase()
