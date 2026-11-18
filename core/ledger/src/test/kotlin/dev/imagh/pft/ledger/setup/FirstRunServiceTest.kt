package dev.imagh.pft.ledger.setup

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.category.DefaultCategories
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.settings.SettingKeys
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class FirstRunServiceTest :
    FunSpec({
        test("initialise creates the identity and the default categories, attributed to the new identity") {
            val kit = LedgerTestKit()

            val me = kit.firstRun.initialise("Akash", "Pixel")

            me.user.displayName shouldBe "Akash"
            me.device.label shouldBe "Pixel"
            kit.identity.currentIdentity() shouldBe me
            kit.categoryRepository.all() shouldHaveSize DefaultCategories.LIST.size
            kit.categoryRepository.all().all {
                it.meta.createdByUserId == me.user.id && it.meta.createdOnDeviceId == me.device.id
            } shouldBe true
            val trail = kit.store.auditLog.entries
            trail.filter { it.entityType != "category" } shouldHaveSize 3
            trail.filter { it.entityType == "category" } shouldHaveSize 31
            trail
                .filter { it.entityType == "category" }
                .all {
                    it.action == AuditAction.CREATE && it.source == AuditSource.SYSTEM && it.userId == me.user.id
                } shouldBe
                true
        }

        test("an audit append that fails on the 10th write leaves no user, device, setting or category (INT-4)") {
            val kit = LedgerTestKit()
            val attempted = mutableListOf<AuditEntry>()
            // Three identity appends succeed, then six category appends, and the seventh category's append is the 10th.
            val failsOnTenth =
                object : AuditLog {
                    override suspend fun append(entry: AuditEntry) {
                        check(attempted.size < 9) { "audit store unavailable" }
                        attempted += entry
                        kit.store.auditLog.append(entry)
                    }
                }
            val broken =
                FirstRunService(
                    kit.store,
                    kit.identityServiceWith(failsOnTenth),
                    CategoryService(kit.store, kit.writerWith(failsOnTenth), kit.categoryRepository, kit.ids),
                )

            shouldThrow<IllegalStateException> { broken.initialise("Akash", "Pixel") }

            // The nine appends that did succeed were three identity rows and six categories...
            attempted.count { it.entityType == "category" } shouldBe 6
            // ...and none of what they described survived.
            kit.identityRepository.users().shouldBeEmpty()
            attempted
                .filter { it.entityType == "device" }
                .forEach { kit.identityRepository.findDevice(DeviceId(it.entityId)).shouldBeNull() }
            kit.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID).shouldBeNull()
            kit.identity.currentIdentity().shouldBeNull()
            kit.categoryRepository.all().shouldBeEmpty()
            kit.store.auditLog.entries
                .shouldBeEmpty()

            // Nothing was half-done: the device can still be set up.
            kit.firstRun.initialise("Akash", "Pixel")
            kit.identityRepository.users() shouldHaveSize 1
            kit.categoryRepository.all() shouldHaveSize 31
        }

        test("a second call fails with AlreadyInitialised and seeds nothing more") {
            val kit = LedgerTestKit()
            val first = kit.firstRun.initialise("Akash", "Pixel")
            val rows = kit.categoryRepository.all()
            val trailSize = kit.store.auditLog.entries.size

            shouldThrow<LedgerError.AlreadyInitialised> { kit.firstRun.initialise("Someone", "Else") }

            kit.identity.currentIdentity() shouldBe first
            kit.identityRepository.users() shouldHaveSize 1
            kit.categoryRepository.all() shouldBe rows
            kit.store.auditLog.entries shouldHaveSize trailSize
        }

        test("an invalid name is refused before anything is written") {
            val kit = LedgerTestKit()

            shouldThrow<LedgerError.BlankName> { kit.firstRun.initialise(" ", "Pixel") }
            shouldThrow<LedgerError.BlankName> { kit.firstRun.initialise("Akash", "") }

            kit.identityRepository.users().shouldBeEmpty()
            kit.categoryRepository.all().shouldBeEmpty()
            kit.store.auditLog.entries
                .shouldBeEmpty()
        }
    })
