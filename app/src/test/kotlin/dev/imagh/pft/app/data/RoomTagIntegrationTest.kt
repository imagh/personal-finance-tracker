package dev.imagh.pft.app.data

import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tags through the real stack (L-3, INT-2, INT-3, INT-4): `TagService` over the Room repository,
 * `AuditedWriter`, the real audit log and in-memory SQLite. The name is stored as given (trimmed), every
 * rule that reads the stored tags runs here, and each change has its audit entry read back through the
 * DAO and mapper. No test relies on the order Room returns rows in: the service sorts.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomTagIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val tags get() = kit.tags

    @After
    fun closeDatabase() = database.close()

    private suspend fun trail(id: TagId) = kit.trailOf("tag", id.value)

    private fun storedCount(): Int =
        database.openHelper.writableDatabase.query("SELECT COUNT(*) FROM tag").use {
            it.moveToFirst()
            it.getInt(0)
        }

    private fun storedNames(): List<String> =
        database.openHelper.writableDatabase.query("SELECT name FROM tag ORDER BY name").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toList()
        }

    // Create and round-trips ----------------------------------------------------------------------

    @Test
    fun `create stores the trimmed name and audits a CREATE attributed to the current identity`() =
        runSuspending {
            val who = kit.initialise()

            val created = tags.create("  Travel ")

            kit.tagRepository.get(created.meta.id) shouldBe created
            created.name shouldBe "Travel"
            storedNames() shouldBe listOf("Travel")
            created.meta.createdByUserId shouldBe who.user.id
            created.meta.createdOnDeviceId shouldBe who.device.id
            val entry = trail(created.meta.id).single()
            entry.action shouldBe AuditAction.CREATE
            entry.source shouldBe AuditSource.MANUAL
            entry.userId shouldBe who.user.id
            entry.deviceId shouldBe who.device.id
            entry.changes shouldBe mapOf("name" to FieldChange(null, "Travel"))
        }

    @Test
    fun `non-ASCII names and quote characters round-trip as plain text`() =
        runSuspending {
            kit.initialise()
            val names = listOf("Éclair ☕", "यात्रा", "O'Brien \"quoted\"", "x'); DROP TABLE tag; --")

            val created = names.map { tags.create(it) }

            created.forEach { kit.tagRepository.get(it.meta.id) shouldBe it }
            storedNames().toSet() shouldBe names.toSet()
        }

    @Test
    fun `rename stores the new name and audits only the name change`() =
        runSuspending {
            kit.initialise()
            val created = tags.create("Travel")

            val edited = tags.edit(created.meta.id, "Trips")

            kit.tagRepository.get(created.meta.id) shouldBe edited
            storedNames() shouldBe listOf("Trips")
            val update = trail(created.meta.id).last()
            update.action shouldBe AuditAction.UPDATE
            update.changes shouldBe mapOf("name" to FieldChange("Travel", "Trips"))
        }

    @Test
    fun `renaming to a case change of its own name is stored`() =
        runSuspending {
            kit.initialise()
            val created = tags.create("travel")

            tags.edit(created.meta.id, "Travel")

            storedNames() shouldBe listOf("Travel")
        }

    // Name uniqueness reads the stored tags -----------------------------------------------------

    @Test
    fun `a name that differs only by case or padding is refused and nothing is written`() =
        runSuspending {
            kit.initialise()
            val travel = tags.create("Travel")
            val other = tags.create("Work")

            shouldThrow<LedgerError.DuplicateName> { tags.create("travel") }.what shouldBe "tag"
            shouldThrow<LedgerError.DuplicateName> { tags.create(" TRAVEL ") }
            shouldThrow<LedgerError.DuplicateName> { tags.edit(other.meta.id, "tRaVeL") }

            storedNames() shouldBe listOf("Travel", "Work")
            trail(travel.meta.id).size shouldBe 1
            trail(other.meta.id).size shouldBe 1
        }

    @Test
    fun `an archived tag still blocks its name and a deleted one does not`() =
        runSuspending {
            kit.initialise()
            val travel = tags.create("Travel")
            val work = tags.create("Work")
            tags.archive(travel.meta.id)
            tags.delete(work.meta.id)

            shouldThrow<LedgerError.DuplicateName> { tags.create("travel") }
            val again = tags.create("work")

            again.name shouldBe "work"
            storedCount() shouldBe 3
        }

    @Test
    fun `restore into a name clash is refused and the tag stays a tombstone`() =
        runSuspending {
            kit.initialise()
            val old = tags.create("Travel")
            tags.delete(old.meta.id)
            tags.create("travel")

            shouldThrow<LedgerError.DuplicateName> { tags.restore(old.meta.id) }

            kit.tagRepository
                .get(old.meta.id)!!
                .meta.isDeleted shouldBe true
            trail(old.meta.id).map { it.action } shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
        }

    // Lifecycle -----------------------------------------------------------------------------------

    @Test
    fun `archive and unarchive are stored and audited, and move the tag between the lists`() =
        runSuspending {
            kit.initialise()
            val tag = tags.create("Travel")

            tags.archive(tag.meta.id)

            kit.tagRepository
                .get(tag.meta.id)!!
                .meta.isArchived shouldBe true
            tags.lists().active.shouldBeEmpty()
            tags.lists().archived.map { it.meta.id } shouldBe listOf(tag.meta.id)
            tags.pickable().shouldBeEmpty()

            tags.unarchive(tag.meta.id)

            tags.lists().active.map { it.meta.id } shouldBe listOf(tag.meta.id)
            tags.lists().archived.shouldBeEmpty()
            tags.pickable().map { it.meta.id } shouldBe listOf(tag.meta.id)
            trail(tag.meta.id).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
        }

    @Test
    fun `delete is a tombstone excluded from every list, restore brings it back, both audited`() =
        runSuspending {
            kit.initialise()
            val tag = tags.create("Travel")
            val archived = tags.create("Work")
            tags.archive(archived.meta.id)

            tags.delete(tag.meta.id)
            tags.delete(archived.meta.id)

            storedCount() shouldBe 2
            kit.tagRepository.allLive().shouldBeEmpty()
            tags.lists().active.shouldBeEmpty()
            tags.lists().archived.shouldBeEmpty()
            tags.pickable().shouldBeEmpty()
            kit.tagRepository
                .get(tag.meta.id)!!
                .meta.isDeleted shouldBe true

            val restored = tags.restore(tag.meta.id)

            restored.meta.isDeleted shouldBe false
            tags.lists().active.map { it.meta.id } shouldBe listOf(tag.meta.id)
            trail(tag.meta.id).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
        }

    @Test
    fun `lists are sorted by name ignoring case whatever order Room returns the rows in`() =
        runSuspending {
            kit.initialise()
            val cherry = tags.create("cherry")
            val banana = tags.create("Banana")
            val apple = tags.create("apple")
            val zebra = tags.create("Zebra")
            tags.archive(zebra.meta.id)

            tags.lists().active.map { it.meta.id } shouldBe listOf(apple.meta.id, banana.meta.id, cherry.meta.id)
            tags.lists().archived.map { it.meta.id } shouldBe listOf(zebra.meta.id)
            tags.pickable().map { it.name } shouldBe listOf("apple", "Banana", "cherry")
        }

    // Atomicity and refusals ----------------------------------------------------------------------

    /** A kit whose audit log fails on its [failOn]th append and records the others in the real trail. */
    private fun failingKit(failOn: Int): RoomTestKit {
        val real = kit.auditLog
        var appends = 0
        return RoomTestKit(
            database,
            auditLog =
                object : AuditLog {
                    override suspend fun append(entry: AuditEntry) {
                        appends++
                        if (appends == failOn) error("audit append failed")
                        real.append(entry)
                    }
                },
        )
    }

    @Test
    fun `a failing audit write leaves no tag row (INT-4)`() =
        runSuspending {
            kit.initialise()
            val failing = failingKit(failOn = 1)

            shouldThrow<IllegalStateException> { failing.tags.create("Travel") }

            storedCount() shouldBe 0
            kit.tagRepository.allLive().shouldBeEmpty()
        }

    @Test
    fun `a failing audit write on a rename keeps the old name`() =
        runSuspending {
            kit.initialise()
            val created = tags.create("Travel")
            val failing = failingKit(failOn = 1)

            shouldThrow<IllegalStateException> { failing.tags.edit(created.meta.id, "Trips") }

            storedNames() shouldBe listOf("Travel")
            trail(created.meta.id).map { it.action } shouldBe listOf(AuditAction.CREATE)
        }

    @Test
    fun `an edit of an unknown or deleted tag is refused`() =
        runSuspending {
            kit.initialise()
            val tag = tags.create("Travel")
            tags.delete(tag.meta.id)

            shouldThrow<LedgerError.Deleted> { tags.edit(tag.meta.id, "Other") }
            shouldThrow<LedgerError.NotFound> { tags.edit(TagId("missing"), "Other") }
        }

    @Test
    fun `blank and over-long names are refused and nothing is written`() =
        runSuspending {
            kit.initialise()

            shouldThrow<LedgerError.BlankName> { tags.create("   ") }
            shouldThrow<LedgerError.NameTooLong> { tags.create("a".repeat(101)) }

            storedCount() shouldBe 0
        }

    @Test
    fun `before first-run setup nothing is written`() =
        runSuspending {
            shouldThrow<LedgerError.NotInitialised> { tags.create("Travel") }

            storedCount() shouldBe 0
        }
}
