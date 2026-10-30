package dev.imagh.pft.app.data.settings

import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.AppSettingsRepository
import java.time.Instant

/** The Room implementation of core's [AppSettingsRepository]. Time is epoch milliseconds, exactly ([EpochMillis]). */
class RoomAppSettingsRepository(
    private val dao: AppSettingDao,
) : AppSettingsRepository {
    override suspend fun get(key: String): AppSetting? =
        dao.get(key)?.let { AppSetting(it.key, it.value, Instant.ofEpochMilli(it.updatedAt)) }

    override suspend fun put(setting: AppSetting) =
        dao.put(AppSettingEntity(setting.key, setting.value, EpochMillis.of(setting.updatedAt)))
}
