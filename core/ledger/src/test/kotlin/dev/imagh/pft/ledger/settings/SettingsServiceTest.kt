package dev.imagh.pft.ledger.settings

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.Duration

class SettingsServiceTest :
    FunSpec({
        test("get returns null for a setting that was never set") {
            LedgerTestKit().settings.get("auto_lock_seconds").shouldBeNull()
        }

        test("setting a new value stores it and audits a CREATE attributed to the current user and device") {
            val kit = LedgerTestKit()
            val me = kit.initialise()
            val before = kit.store.auditLog.entries.size

            kit.settings.set("auto_lock_seconds", "60")

            kit.settings.get("auto_lock_seconds") shouldBe "60"
            kit.store.auditLog.entries shouldHaveSize before + 1
            kit.store.auditLog.entries.shouldHaveAudited(
                entityType = "app_setting",
                entityId = "auto_lock_seconds",
                action = AuditAction.CREATE,
                changes = mapOf("value" to FieldChange(null, "60")),
                userId = me.user.id,
                deviceId = me.device.id,
                source = AuditSource.MANUAL,
            )
        }

        test("changing a value audits an UPDATE with the old and new text and moves updatedAt") {
            val kit = LedgerTestKit()
            kit.initialise()
            kit.settings.set("alert_threshold_pct", "80")
            kit.clock.advance(Duration.ofMinutes(5))

            kit.settings.set("alert_threshold_pct", "90")

            kit.settings.get("alert_threshold_pct") shouldBe "90"
            kit.settingsRepository.get("alert_threshold_pct")?.updatedAt shouldBe kit.clock.instant()
            kit.store.auditLog.entries.shouldHaveAudited(
                "app_setting",
                "alert_threshold_pct",
                AuditAction.UPDATE,
                changes = mapOf("value" to FieldChange("80", "90")),
            )
        }

        test("setting the same value again writes nothing and audits nothing") {
            val kit = LedgerTestKit()
            kit.initialise()
            kit.settings.set("alert_threshold_pct", "80")
            val firstWrite = kit.settingsRepository.get("alert_threshold_pct")
            val trailSize = kit.store.auditLog.entries.size
            kit.clock.advance(Duration.ofMinutes(5))

            kit.settings.set("alert_threshold_pct", "80")

            kit.store.auditLog.entries shouldHaveSize trailSize
            kit.settingsRepository.get("alert_threshold_pct") shouldBe firstWrite
        }

        test("the source of the change is recorded") {
            val kit = LedgerTestKit()
            kit.initialise()
            kit.settings.set("lookahead_days", "7", AuditSource.SYSTEM)
            kit.store.auditLog.entries.shouldHaveAudited(
                "app_setting",
                "lookahead_days",
                AuditAction.CREATE,
                source = AuditSource.SYSTEM,
            )
        }

        test("an empty value is allowed and a value of exactly the limit is accepted") {
            val kit = LedgerTestKit()
            kit.initialise()
            kit.settings.set("note", "")
            kit.settings.get("note") shouldBe ""
            val longest = "v".repeat(LedgerLimits.MAX_SETTING_VALUE_LENGTH)
            kit.settings.set("note", longest)
            kit.settings.get("note") shouldBe longest
        }

        test("a value over the limit is rejected and nothing is written") {
            val kit = LedgerTestKit()
            kit.initialise()
            val trailSize = kit.store.auditLog.entries.size
            val error =
                shouldThrow<LedgerError.SettingValueTooLong> {
                    kit.settings.set("note", "v".repeat(LedgerLimits.MAX_SETTING_VALUE_LENGTH + 1))
                }
            error.max shouldBe LedgerLimits.MAX_SETTING_VALUE_LENGTH
            kit.settings.get("note").shouldBeNull()
            kit.store.auditLog.entries shouldHaveSize trailSize
        }

        test("keys must be lower-case identifiers") {
            val kit = LedgerTestKit()
            kit.initialise()
            val bad =
                listOf(
                    "",
                    " ",
                    "Upper",
                    "1starts_with_digit",
                    "has space",
                    "dash-ed",
                    "semi;colon",
                    "quote'",
                    "ünïcode",
                    "k".repeat(LedgerLimits.MAX_SETTING_KEY_LENGTH + 1),
                )
            bad.forEach { key ->
                withClue("key=[$key]") { shouldThrow<LedgerError.InvalidSettingKey> { kit.settings.set(key, "x") } }
            }
            kit.settings.set("k".repeat(LedgerLimits.MAX_SETTING_KEY_LENGTH), "x")
            kit.settings.set("a1_b2", "x")
        }

        test("the current-device key is reserved: it cannot be repointed through the settings service") {
            val kit = LedgerTestKit()
            val me = kit.initialise()
            shouldThrow<LedgerError.InvalidSettingKey> {
                kit.settings.set(SettingKeys.CURRENT_DEVICE_ID, "another-device")
            }
            kit.identity.currentIdentity() shouldBe me
        }

        test("setting before first-run setup fails closed because there is nobody to attribute it to") {
            val kit = LedgerTestKit()
            shouldThrow<LedgerError.NotInitialised> { kit.settings.set("auto_lock_seconds", "60") }
            kit.settingsRepository.get("auto_lock_seconds").shouldBeNull()
            kit.store.auditLog.entries
                .shouldBeEmpty()
        }

        test("a failure while auditing leaves the setting unchanged (INT-4)") {
            val kit = LedgerTestKit()
            kit.initialise()
            kit.settings.set("alert_threshold_pct", "80")
            val failing =
                object : AuditLog {
                    override suspend fun append(entry: AuditEntry) = error("audit store unavailable")
                }
            val service =
                SettingsService(
                    kit.store,
                    kit.settingsRepository,
                    kit.identity,
                    AuditRecorder(failing, kit.ids, kit.clock),
                    kit.clock,
                )

            shouldThrow<IllegalStateException> { service.set("alert_threshold_pct", "95") }
            shouldThrow<IllegalStateException> { service.set("brand_new", "1") }

            kit.settings.get("alert_threshold_pct") shouldBe "80"
            kit.settings.get("brand_new").shouldBeNull()
        }
    })
