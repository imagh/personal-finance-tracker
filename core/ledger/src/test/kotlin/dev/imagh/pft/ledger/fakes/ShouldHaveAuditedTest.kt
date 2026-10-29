package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.matchers.shouldNotHaveAudited
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class ShouldHaveAuditedTest :
    FunSpec({
        val entry =
            AuditEntry(
                id = AuditEntryId("e-1"),
                entityType = "account",
                entityId = "a-1",
                action = AuditAction.UPDATE,
                changes = mapOf("name" to FieldChange("Old", "New")),
                userId = UserId("u-1"),
                deviceId = DeviceId("d-1"),
                source = AuditSource.MANUAL,
                at = TestClock().instant(),
            )
        val trail = listOf(entry)

        test("passes on the type, id and action alone") {
            trail.shouldHaveAudited("account", "a-1", AuditAction.UPDATE)
        }

        test("passes when every optional expectation matches") {
            trail.shouldHaveAudited(
                entityType = "account",
                entityId = "a-1",
                action = AuditAction.UPDATE,
                changes = mapOf("name" to FieldChange("Old", "New")),
                userId = UserId("u-1"),
                deviceId = DeviceId("d-1"),
                source = AuditSource.MANUAL,
            )
        }

        test("fails when nothing matches the action, the id, the type, the changes, or the attribution") {
            shouldThrow<AssertionError> { trail.shouldHaveAudited("account", "a-1", AuditAction.CREATE) }
            shouldThrow<AssertionError> { trail.shouldHaveAudited("account", "a-2", AuditAction.UPDATE) }
            shouldThrow<AssertionError> { trail.shouldHaveAudited("tag", "a-1", AuditAction.UPDATE) }
            shouldThrow<AssertionError> {
                trail.shouldHaveAudited("account", "a-1", AuditAction.UPDATE, changes = emptyMap())
            }
            shouldThrow<AssertionError> {
                trail.shouldHaveAudited("account", "a-1", AuditAction.UPDATE, userId = UserId("other"))
            }
            shouldThrow<AssertionError> {
                trail.shouldHaveAudited("account", "a-1", AuditAction.UPDATE, deviceId = DeviceId("other"))
            }
            shouldThrow<AssertionError> {
                trail.shouldHaveAudited("account", "a-1", AuditAction.UPDATE, source = AuditSource.SYSTEM)
            }
        }

        test("fails on an empty trail, and shouldNotHaveAudited is its negation") {
            val empty = emptyList<AuditEntry>()
            shouldThrow<AssertionError> { empty.shouldHaveAudited("account", "a-1", AuditAction.UPDATE) }
            empty.shouldNotHaveAudited("account", "a-1", AuditAction.UPDATE)
            shouldThrow<AssertionError> { trail.shouldNotHaveAudited("account", "a-1", AuditAction.UPDATE) }
        }
    })
