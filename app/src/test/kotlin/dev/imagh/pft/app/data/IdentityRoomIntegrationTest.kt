package dev.imagh.pft.app.data

import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.SettingKeys
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `IdentityService` and `SettingsService` over the real Room repositories, runner and audit log:
 * the service -> repository -> SQLite path, with first-run setup (L-8) as the representative flow.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class IdentityRoomIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `initialise persists the user, the device and the current-device setting`() =
        runSuspending {
            val created = kit.initialise()

            kit.identity.currentIdentity() shouldBe created
            kit.identityRepository.users() shouldBe listOf(created.user)
            kit.identityRepository.findDevice(created.device.id) shouldBe created.device
            kit.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID)?.value shouldBe created.device.id.value
        }

    @Test
    fun `initialise audits all three records, attributed to the identity it creates`() =
        runSuspending {
            val created = kit.initialise()

            val user = kit.trailOf("user_profile", created.user.id.value).single()
            user.action shouldBe AuditAction.CREATE
            user.changes shouldBe mapOf("display_name" to FieldChange(null, "Akash"))
            user.userId shouldBe created.user.id
            user.deviceId shouldBe created.device.id
            user.source shouldBe AuditSource.MANUAL

            kit.trailOf("device", created.device.id.value).single().changes shouldBe
                mapOf("user_id" to FieldChange(null, created.user.id.value), "label" to FieldChange(null, "Pixel"))
            kit.trailOf("app_setting", SettingKeys.CURRENT_DEVICE_ID).single().changes shouldBe
                mapOf("value" to FieldChange(null, created.device.id.value))
        }

    @Test
    fun `a second initialise is refused and writes nothing`() =
        runSuspending {
            kit.initialise()

            shouldThrow<LedgerError.AlreadyInitialised> { kit.identity.initialise("Someone", "Else") }

            kit.identityRepository.users().size shouldBe 1
            auditRowCount() shouldBe 3
        }

    @Test
    fun `a rejected name persists nothing`() =
        runSuspending {
            shouldThrow<LedgerError.BlankName> { kit.identity.initialise("  ", "Pixel") }

            kit.identityRepository.users() shouldBe emptyList()
            kit.identity.currentIdentity() shouldBe null
            auditRowCount() shouldBe 0
        }

    @Test
    fun `a failure while auditing rolls the whole first run back`() =
        runSuspending {
            val failing = RoomTestKit(database, auditLog = FailingOnNthAppend(kit.auditLog, failOn = 3))

            shouldThrow<IllegalStateException> { failing.initialise() }

            kit.identityRepository.users() shouldBe emptyList()
            kit.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID) shouldBe null
            kit.identity.currentIdentity() shouldBe null
            auditRowCount() shouldBe 0
        }

    @Test
    fun `a current-device setting that points nowhere fails closed`() =
        runSuspending {
            kit.settingsRepository.put(AppSetting(SettingKeys.CURRENT_DEVICE_ID, "no-such-device", kit.clock.instant()))

            shouldThrow<LedgerError.IdentityCorrupt> { kit.identity.currentIdentity() }
        }

    @Test
    fun `a changed setting is stored and audited, and an unchanged one writes no audit row`() =
        runSuspending {
            kit.initialise()

            kit.settings.set("auto_lock_seconds", "60")
            kit.settings.set("auto_lock_seconds", "60")
            kit.settings.set("auto_lock_seconds", "120")

            kit.settings.get("auto_lock_seconds") shouldBe "120"
            kit.trailOf("app_setting", "auto_lock_seconds").map { it.action to it.changes } shouldBe
                listOf(
                    AuditAction.CREATE to mapOf("value" to FieldChange(null, "60")),
                    AuditAction.UPDATE to mapOf("value" to FieldChange("60", "120")),
                )
        }

    private fun auditRowCount(): Int =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM audit_entry").use {
            it.moveToFirst()
            it.getInt(0)
        }

    /** The real audit log, except that append number [failOn] throws: a fault injected into the write path. */
    private class FailingOnNthAppend(
        private val real: AuditLog,
        private val failOn: Int,
    ) : AuditLog {
        private var appends = 0

        override suspend fun append(entry: AuditEntry) {
            appends++
            check(appends != failOn) { "audit append failed" }
            real.append(entry)
        }
    }
}
