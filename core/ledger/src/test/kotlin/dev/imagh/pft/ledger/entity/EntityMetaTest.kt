package dev.imagh.pft.ledger.entity

import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class EntityMetaTest :
    FunSpec({
        val t0 = Instant.parse("2026-11-01T10:00:00Z")
        val fresh =
            EntityMeta(
                id = UserId("e-1"),
                createdAt = t0,
                updatedAt = t0,
                createdByUserId = UserId("u-1"),
                createdOnDeviceId = DeviceId("d-1"),
            )

        test("a fresh entity is active: neither deleted nor archived") {
            fresh.isDeleted shouldBe false
            fresh.isArchived shouldBe false
            fresh.isActive shouldBe true
        }

        test("a tombstoned entity is deleted and not active") {
            val deleted = fresh.copy(deletedAt = t0.plusSeconds(1))
            deleted.isDeleted shouldBe true
            deleted.isArchived shouldBe false
            deleted.isActive shouldBe false
        }

        test("an archived entity is not active but is not deleted either") {
            val archived = fresh.copy(archivedAt = t0.plusSeconds(1))
            archived.isArchived shouldBe true
            archived.isDeleted shouldBe false
            archived.isActive shouldBe false
        }

        test("the audit id of a ledger entity is its meta id") {
            data class Note(
                override val meta: EntityMeta<UserId>,
            ) : LedgerEntity<UserId, Note> {
                override val auditType = "note"

                override fun auditFields(): Map<String, String?> = emptyMap()

                override fun withMeta(meta: EntityMeta<UserId>) = copy(meta = meta)
            }
            Note(fresh).auditId shouldBe "e-1"
            Note(fresh).withMeta(fresh.copy(updatedAt = t0.plusSeconds(9))).meta.updatedAt shouldBe t0.plusSeconds(9)
        }
    })
