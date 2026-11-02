package dev.imagh.pft.ledger.identity

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.SettingKeys
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class IdentityServiceTest :
    FunSpec({
        test("currentIdentity is null before initialisation") {
            LedgerTestKit().identity.currentIdentity().shouldBeNull()
        }

        test("requireIdentity before initialisation fails closed") {
            shouldThrow<LedgerError.NotInitialised> { LedgerTestKit().identity.requireIdentity() }
        }

        context("initialise") {
            test("persists a user, a device and the current-device setting") {
                val kit = LedgerTestKit()
                val identity = kit.identity.initialise("Akash", "Pixel")

                identity.user.displayName shouldBe "Akash"
                identity.device.label shouldBe "Pixel"
                identity.device.userId shouldBe identity.user.id
                kit.identityRepository.users() shouldBe listOf(identity.user)
                kit.identityRepository.findDevice(identity.device.id) shouldBe identity.device
                kit.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID)?.value shouldBe identity.device.id.value
                kit.identity.currentIdentity() shouldBe identity
                kit.identity.requireIdentity() shouldBe identity
            }

            test("appends three audit entries, attributed to the new user and device") {
                val kit = LedgerTestKit()
                val identity = kit.identity.initialise("Akash", "Pixel")
                val trail = kit.store.auditLog.entries

                trail shouldHaveSize 3
                trail.shouldHaveAudited(
                    entityType = "user_profile",
                    entityId = identity.user.id.value,
                    action = AuditAction.CREATE,
                    changes = mapOf("display_name" to FieldChange(null, "Akash")),
                    userId = identity.user.id,
                    deviceId = identity.device.id,
                    source = AuditSource.MANUAL,
                )
                trail.shouldHaveAudited(
                    entityType = "device",
                    entityId = identity.device.id.value,
                    action = AuditAction.CREATE,
                    changes =
                        mapOf(
                            "user_id" to FieldChange(null, identity.user.id.value),
                            "label" to FieldChange(null, "Pixel"),
                        ),
                    userId = identity.user.id,
                    deviceId = identity.device.id,
                )
                trail.shouldHaveAudited(
                    entityType = "app_setting",
                    entityId = SettingKeys.CURRENT_DEVICE_ID,
                    action = AuditAction.CREATE,
                    changes = mapOf("value" to FieldChange(null, identity.device.id.value)),
                    userId = identity.user.id,
                    deviceId = identity.device.id,
                )
            }

            test("audit entries are stamped with the clock and have time-ordered ids") {
                val kit = LedgerTestKit()
                kit.initialise()
                val trail = kit.store.auditLog.entries
                trail.map { it.at }.toSet() shouldBe setOf(kit.clock.instant())
                trail.map { it.id.value } shouldBe trail.map { it.id.value }.sorted()
            }

            test("trims the names") {
                val identity = LedgerTestKit().identity.initialise("  Akash  ", " Pixel 8 ")
                identity.user.displayName shouldBe "Akash"
                identity.device.label shouldBe "Pixel 8"
            }

            test("a second call fails with AlreadyInitialised and writes nothing") {
                val kit = LedgerTestKit()
                val first = kit.identity.initialise("Akash", "Pixel")

                shouldThrow<LedgerError.AlreadyInitialised> { kit.identity.initialise("Someone", "Else") }

                kit.identity.currentIdentity() shouldBe first
                kit.identityRepository.users() shouldHaveSize 1
                kit.store.auditLog.entries shouldHaveSize 3
            }

            test("rejects a blank or over-long user name or device label, and persists nothing") {
                val kit = LedgerTestKit()
                shouldThrow<LedgerError.BlankName> { kit.identity.initialise("", "Pixel") }
                shouldThrow<LedgerError.BlankName> { kit.identity.initialise("   ", "Pixel") }
                shouldThrow<LedgerError.BlankName> { kit.identity.initialise("Akash", " ") }
                shouldThrow<LedgerError.NameTooLong> {
                    kit.identity.initialise("a".repeat(LedgerLimits.MAX_NAME_LENGTH + 1), "Pixel")
                }
                shouldThrow<LedgerError.NameTooLong> {
                    kit.identity.initialise("Akash", "d".repeat(LedgerLimits.MAX_NAME_LENGTH + 1))
                }

                kit.identity.currentIdentity().shouldBeNull()
                kit.identityRepository.users().shouldBeEmpty()
                kit.store.auditLog.entries
                    .shouldBeEmpty()
            }

            test("a failure while auditing leaves no user, device, setting or audit entry (INT-4)") {
                val kit = LedgerTestKit()
                var appended = 0
                val failsOnThird =
                    object : AuditLog {
                        override suspend fun append(entry: AuditEntry) {
                            check(++appended < 3) { "audit store unavailable" }
                            kit.store.auditLog.append(entry)
                        }
                    }
                val service = kit.identityServiceWith(failsOnThird)

                shouldThrow<IllegalStateException> { service.initialise("Akash", "Pixel") }

                kit.identityRepository.users().shouldBeEmpty()
                kit.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID).shouldBeNull()
                kit.store.auditLog.entries
                    .shouldBeEmpty()
                kit.identity.currentIdentity().shouldBeNull()
            }
        }

        context("initialiseForExistingUser") {
            test("adds a device for a known user without creating the user again") {
                val kit = LedgerTestKit()
                val existing = UserProfile(UserId("u-existing"), "Priya")
                kit.identityRepository.insertUser(existing)

                val identity = kit.identity.initialiseForExistingUser(existing.id, "Galaxy")

                identity.user shouldBe existing
                identity.device.userId shouldBe existing.id
                kit.identity.currentIdentity() shouldBe identity
                kit.identityRepository.users() shouldBe listOf(existing)
                val trail = kit.store.auditLog.entries
                trail shouldHaveSize 2
                trail.shouldHaveAudited("device", identity.device.id.value, AuditAction.CREATE, userId = existing.id)
                trail.shouldHaveAudited("app_setting", SettingKeys.CURRENT_DEVICE_ID, AuditAction.CREATE)
            }

            test("an unknown user is NotFound and nothing is written") {
                val kit = LedgerTestKit()
                shouldThrow<LedgerError.NotFound> {
                    kit.identity.initialiseForExistingUser(UserId("nobody"), "Galaxy")
                }
                kit.identity.currentIdentity().shouldBeNull()
                kit.store.auditLog.entries
                    .shouldBeEmpty()
            }

            test("fails with AlreadyInitialised when this device already has an identity") {
                val kit = LedgerTestKit()
                val first = kit.identity.initialise("Akash", "Pixel")
                shouldThrow<LedgerError.AlreadyInitialised> {
                    kit.identity.initialiseForExistingUser(first.user.id, "Another")
                }
            }

            test("rejects a blank device label") {
                val kit = LedgerTestKit()
                val existing = UserProfile(UserId("u-existing"), "Priya")
                kit.identityRepository.insertUser(existing)
                shouldThrow<LedgerError.BlankName> { kit.identity.initialiseForExistingUser(existing.id, " ") }
            }
        }

        context("users") {
            test("lists the known household members") {
                val kit = LedgerTestKit()
                kit.identityRepository.insertUser(UserProfile(UserId("u-1"), "Priya"))
                kit.identity.users().map { it.displayName } shouldBe listOf("Priya")
            }
        }

        context("a damaged identity fails closed instead of looking uninitialised") {
            test("the current-device setting points at a device that does not exist") {
                val kit = LedgerTestKit()
                kit.settingsRepository.put(AppSetting(SettingKeys.CURRENT_DEVICE_ID, "ghost", kit.clock.instant()))
                shouldThrow<LedgerError.IdentityCorrupt> { kit.identity.currentIdentity() }
                shouldThrow<LedgerError.AlreadyInitialised> { kit.identity.initialise("Akash", "Pixel") }
            }

            test("the device's user does not exist") {
                val kit = LedgerTestKit()
                val device =
                    Device(
                        dev.imagh.pft.ledger.ids
                            .DeviceId("d-1"),
                        UserId("missing"),
                        "Pixel",
                    )
                kit.identityRepository.insertDevice(device)
                kit.settingsRepository.put(AppSetting(SettingKeys.CURRENT_DEVICE_ID, "d-1", kit.clock.instant()))
                shouldThrow<LedgerError.IdentityCorrupt> { kit.identity.currentIdentity() }
            }

            test("the setting is blank") {
                val kit = LedgerTestKit()
                kit.settingsRepository.put(AppSetting(SettingKeys.CURRENT_DEVICE_ID, " ", kit.clock.instant()))
                shouldThrow<LedgerError.IdentityCorrupt> { kit.identity.currentIdentity() }
            }
        }
    })
