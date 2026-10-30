package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import dev.imagh.pft.app.data.audit.AuditEntryMapper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/** The audit write path: [RoomAuditLog] appends, [dev.imagh.pft.app.data.audit.AuditDao] reads. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AuditDaoTest {
    private val database = TestDatabase.create()
    private val log = RoomAuditLog(database.auditDao())
    private val start = Instant.parse("2026-11-01T10:00:00Z")

    @After
    fun closeDatabase() = database.close()

    private fun entry(
        id: String,
        entityId: String = "acc-1",
        entityType: String = "account",
        at: Instant = start,
        changes: Map<String, FieldChange> = mapOf("name" to FieldChange(null, "HDFC")),
    ) = AuditEntry(
        id = AuditEntryId(id),
        entityType = entityType,
        entityId = entityId,
        action = AuditAction.CREATE,
        changes = changes,
        userId = UserId("user-1"),
        deviceId = DeviceId("device-1"),
        source = AuditSource.MANUAL,
        at = at,
    )

    private suspend fun trailOf(
        entityType: String,
        entityId: String,
    ) = database.auditDao().forEntity(entityType, entityId).map(AuditEntryMapper::toModel)

    @Test
    fun `an appended entry is read back by entity with its changes intact`() =
        runSuspending {
            val written =
                entry("a1", changes = mapOf("name" to FieldChange("Old", "New"), "note" to FieldChange(null, "x")))

            log.append(written)

            trailOf("account", "acc-1") shouldBe listOf(written)
        }

    @Test
    fun `reading by entity returns only that entity's entries, oldest first`() =
        runSuspending {
            val second = entry("a2", at = start.plusSeconds(10))
            val first = entry("a1", at = start)
            log.append(second)
            log.append(first)
            log.append(entry("a3", entityId = "acc-2"))
            log.append(entry("a4", entityType = "category"))

            trailOf("account", "acc-1") shouldBe listOf(first, second)
            trailOf("account", "none") shouldBe emptyList()
        }

    @Test
    fun `appending an id twice fails and keeps the original`() =
        runSuspending {
            val original = entry("a1")
            log.append(original)

            shouldThrow<SQLiteConstraintException> {
                log.append(
                    entry(
                        "a1",
                        changes =
                            mapOf(
                                "name" to FieldChange(null, "Forged"),
                            ),
                    ),
                )
            }

            trailOf("account", "acc-1") shouldBe listOf(original)
        }
}
