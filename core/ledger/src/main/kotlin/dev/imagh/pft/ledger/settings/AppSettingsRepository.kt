package dev.imagh.pft.ledger.settings

import dev.imagh.pft.ledger.entity.Auditable
import java.time.Instant

/** Setting keys the ledger itself owns. */
object SettingKeys {
    /**
     * The id of this installation's [dev.imagh.pft.ledger.identity.Device]. Written only by
     * `IdentityService` during first-run setup; [SettingsService] refuses to write it.
     */
    const val CURRENT_DEVICE_ID = "current_device_id"
}

/**
 * One runtime-varying value, stored in the database (CLAUDE.md §3.5) so a change can be audited:
 * for example the budget alert threshold or the auto-lock timeout.
 *
 * @param key a lower-case identifier such as `auto_lock_seconds`.
 * @param value the value as text; the owning feature parses it.
 * @param updatedAt when it last changed (not an audited field).
 */
data class AppSetting(
    val key: String,
    val value: String,
    val updatedAt: Instant,
) : Auditable {
    override val auditType: String get() = "app_setting"
    override val auditId: String get() = key

    override fun auditFields(): Map<String, String?> = mapOf("value" to value)
}

/** Persistence of [AppSetting]s. Defined in core, implemented with Room in `app`. */
interface AppSettingsRepository {
    /** The setting stored under [key], or null. */
    suspend fun get(key: String): AppSetting?

    /** Stores [setting], replacing any existing value under the same key. */
    suspend fun put(setting: AppSetting)
}
