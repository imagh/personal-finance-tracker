package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.SampleId
import dev.imagh.pft.app.testing.SampleThing
import dev.imagh.pft.app.testing.SampleThingRepository
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Duration
import java.time.Instant

/**
 * `AuditedWriter` over Room (INT-2, INT-3, INT-4): the entity row and its audit entry are written
 * together or not at all, through the real runner, repository, audit log and SQLite. The entity is
 * the throwaway `SampleThing`; B1 repeats the same flow for accounts.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomAuditedWriterIntegrationTest {
    private val database = TestDatabase.createWithSample()
    private val kit = RoomTestKit(database)
    private val things = SampleThingRepository(database.sampleThingDao())
    private val id = SampleId("thing-1")

    @After
    fun closeDatabase() = database.close()

    private suspend fun create(
        name: String = "Chai",
        price: Long = 2_500,
        note: String? = null,
        writer: AuditedWriter = kit.writer,
    ): SampleThing = writer.create(things, id) { meta -> SampleThing(meta, name, Paise(price), note) }

    private suspend fun trail() = kit.trailOf("sample_thing", id.value)

    private fun failingAuditKit(): RoomTestKit =
        RoomTestKit(
            database,
            auditLog =
                object : AuditLog {
                    override suspend fun append(entry: AuditEntry) = error("audit append failed")
                },
        )

    @Test
    fun `create stores the row and its audit entry together, attributed to the current identity`() =
        runSuspending {
            val who: Identity = kit.initialise()

            val created = create(note = "hot")

            things.get(id) shouldBe created
            created.meta.createdByUserId shouldBe who.user.id
            created.meta.createdOnDeviceId shouldBe who.device.id
            val entry = trail().single()
            entry.action shouldBe AuditAction.CREATE
            entry.userId shouldBe who.user.id
            entry.deviceId shouldBe who.device.id
            entry.changes shouldBe
                mapOf(
                    "name" to FieldChange(null, "Chai"),
                    "price" to FieldChange(null, "2500"),
                    "note" to FieldChange(null, "hot"),
                )
        }

    @Test
    fun `a failing audit write leaves neither the row nor an audit entry`() =
        runSuspending {
            val failing = failingAuditKit()
            kit.initialise()

            shouldThrow<IllegalStateException> { create(writer = failing.writer) }

            things.get(id) shouldBe null
            trail() shouldBe emptyList()
        }

    @Test
    fun `before first-run setup nothing is written`() =
        runSuspending {
            shouldThrow<LedgerError.NotInitialised> { create() }

            things.get(id) shouldBe null
        }

    @Test
    fun `update stores the change and audits only the changed fields`() =
        runSuspending {
            kit.initialise()
            val created = create()
            kit.clock.advance(Duration.ofMinutes(5))

            val updated = kit.writer.update(things, id) { it.copy(name = "Coffee", note = "iced") }

            things.get(id) shouldBe updated
            updated.meta.updatedAt shouldBe created.meta.updatedAt.plus(Duration.ofMinutes(5))
            updated.meta.createdAt shouldBe created.meta.createdAt
            val update = trail().last()
            update.action shouldBe AuditAction.UPDATE
            update.changes shouldBe
                mapOf("name" to FieldChange("Chai", "Coffee"), "note" to FieldChange(null, "iced"))
        }

    @Test
    fun `an update that changes nothing writes no row and no audit entry`() =
        runSuspending {
            kit.initialise()
            val created = create()
            kit.clock.advance(Duration.ofMinutes(5))

            kit.writer.update(things, id) { it }

            things.get(id) shouldBe created
            trail().size shouldBe 1
        }

    @Test
    fun `an update whose change block throws leaves the stored entity and the trail untouched`() =
        runSuspending {
            kit.initialise()
            val created = create()

            shouldThrow<IllegalStateException> { kit.writer.update(things, id) { error("rejected") } }

            things.get(id) shouldBe created
            trail().size shouldBe 1
        }

    @Test
    fun `an update whose audit write fails is rolled back`() =
        runSuspending {
            kit.initialise()
            val created = create()

            shouldThrow<IllegalStateException> {
                failingAuditKit().writer.update(things, id) { it.copy(name = "Coffee") }
            }

            things.get(id) shouldBe created
            trail().size shouldBe 1
        }

    @Test
    fun `delete tombstones the row and restore brings it back, both audited`() =
        runSuspending {
            kit.initialise()
            create()
            kit.clock.advance(Duration.ofMinutes(1))

            kit.writer.delete(things, id)

            things.getLive(id) shouldBe null
            things.get(id)?.meta?.deletedAt shouldBe kit.clock.instant()

            kit.writer.restore(things, id)

            things.getLive(id)?.meta?.deletedAt shouldBe null
            trail().map { it.action } shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
        }

    @Test
    fun `archive and unarchive keep the row and audit the archived time`() =
        runSuspending {
            kit.initialise()
            create()

            kit.writer.archive(things, id)
            things.getLive(id)?.meta?.isArchived shouldBe true
            kit.writer.unarchive(things, id)
            things.getLive(id)?.meta?.isArchived shouldBe false

            trail().map { it.action } shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
        }

    @Test
    fun `repeating a lifecycle call is a no-op that audits nothing`() =
        runSuspending {
            kit.initialise()
            create()
            kit.writer.delete(things, id)

            kit.writer.delete(things, id)

            trail().map { it.action } shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
        }

    @Test
    fun `the audit trail of a stored entity stays immutable`() =
        runSuspending {
            kit.initialise()
            create()

            shouldThrow<SQLiteConstraintException> {
                database.openHelper.writableDatabase.execSQL(
                    "DELETE FROM audit_entry WHERE entity_id = ?",
                    arrayOf(id.value),
                )
            }

            trail().size shouldBe 1
        }

    @Test
    fun `with a millisecond clock over a nanosecond source, what is written equals what is read back`() =
        runSuspending {
            val msKit = RoomTestKit(database, wrapClock = EpochMillis::clock)
            msKit.clock.set(Instant.parse("2026-11-01T10:00:00.123456789Z"))
            msKit.initialise()

            val created = create(writer = msKit.writer)
            msKit.clock.set(Instant.parse("2026-11-01T10:05:00.987654321Z"))
            val updated = msKit.writer.update(things, id) { it.copy(name = "Coffee") }

            things.get(id) shouldBe updated
            created.meta.createdAt shouldBe Instant.parse("2026-11-01T10:00:00.123Z")
            updated.meta.updatedAt shouldBe Instant.parse("2026-11-01T10:05:00.987Z")
            msKit.trailOf("sample_thing", id.value).map { it.at } shouldBe
                listOf(created.meta.createdAt, updated.meta.updatedAt)
        }

    @Test
    fun `a clock finer than a millisecond is refused and nothing is written`() =
        runSuspending {
            kit.initialise()
            kit.clock.set(Instant.parse("2026-11-01T10:00:00.123456789Z"))

            shouldThrow<IllegalArgumentException> { create() }

            things.get(id) shouldBe null
            trail() shouldBe emptyList()
        }
}
