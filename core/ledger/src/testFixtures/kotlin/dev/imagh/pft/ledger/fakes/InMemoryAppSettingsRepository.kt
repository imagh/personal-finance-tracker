package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.AppSettingsRepository

/** The in-memory [AppSettingsRepository]; part of the ledger's transactions. */
class InMemoryAppSettingsRepository(
    ledger: InMemoryLedgerStore,
) : AppSettingsRepository {
    private val rows = ledger.newStore<AppSetting, String> { it.key }

    override suspend fun get(key: String): AppSetting? = rows.get(key)

    override suspend fun put(setting: AppSetting) {
        if (rows.get(setting.key) == null) rows.insert(setting) else rows.update(setting)
    }
}
