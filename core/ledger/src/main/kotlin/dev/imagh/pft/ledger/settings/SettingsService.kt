package dev.imagh.pft.ledger.settings

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditDiff
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import java.time.Clock

/**
 * Reads and writes runtime-varying settings (budget alert threshold, auto-lock timeout, ...).
 *
 * These values live in the database rather than in config (CLAUDE.md §3.5) because they vary per
 * user at runtime, so every change is audited with who made it. Values are text; the feature that
 * owns a key parses and range-checks it. **Values are recorded in the audit trail as plain text, so
 * this service is for non-sensitive values only**; never store a secret through it. A write that
 * does not change the value is a no-op and writes no audit row.
 */
class SettingsService(
    private val runner: TransactionRunner,
    private val settings: AppSettingsRepository,
    private val identity: IdentityService,
    private val recorder: AuditRecorder,
    private val clock: Clock,
) {
    /** The value stored under [key], or null if it was never set. */
    suspend fun get(key: String): String? = settings.get(key)?.value

    /**
     * Sets [key] to [value] and audits the change (CREATE the first time, UPDATE after), attributed
     * to the current user and device, in one transaction.
     *
     * @throws LedgerError.InvalidSettingKey if [key] is not a lower-case identifier
     *   (`[a-z][a-z0-9_]*`, at most [LedgerLimits.MAX_SETTING_KEY_LENGTH] characters) or is
     *   reserved ([SettingKeys.CURRENT_DEVICE_ID] is written only by first-run setup).
     * @throws LedgerError.SettingValueTooLong if [value] is longer than
     *   [LedgerLimits.MAX_SETTING_VALUE_LENGTH].
     * @throws LedgerError.NotInitialised before first-run setup.
     */
    suspend fun set(
        key: String,
        value: String,
        source: AuditSource = AuditSource.MANUAL,
    ) {
        if (!isValidKey(key)) throw LedgerError.InvalidSettingKey()
        if (value.length > LedgerLimits.MAX_SETTING_VALUE_LENGTH) {
            throw LedgerError.SettingValueTooLong(LedgerLimits.MAX_SETTING_VALUE_LENGTH)
        }
        runner.inTransaction {
            val by = identity.requireIdentity()
            val old = settings.get(key)
            if (old?.value != value) {
                val new = AppSetting(key, value, clock.instant())
                settings.put(new)
                val action = if (old == null) AuditAction.CREATE else AuditAction.UPDATE
                recorder.record(new, action, AuditDiff.diff(old, new), by, source)
            }
        }
    }

    private fun isValidKey(key: String): Boolean =
        key != SettingKeys.CURRENT_DEVICE_ID &&
            key.length <= LedgerLimits.MAX_SETTING_KEY_LENGTH &&
            KEY_PATTERN.matches(key)

    private companion object {
        val KEY_PATTERN = Regex("[a-z][a-z0-9_]*")
    }
}
