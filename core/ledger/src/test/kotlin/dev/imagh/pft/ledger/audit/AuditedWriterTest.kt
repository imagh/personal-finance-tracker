package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.fakes.InMemoryEntityRepository
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.matchers.shouldNotHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.Duration

@JvmInline
private value class NoteId(
    override val value: String,
) : EntityId

/** A minimal ledger entity, so the writer is tested without depending on any real entity. */
private data class Note(
    override val meta: EntityMeta<NoteId>,
    val title: String,
    val cents: Long? = null,
) : LedgerEntity<NoteId, Note> {
    override val auditType = "note"

    override fun auditFields(): Map<String, String?> = mapOf("title" to title, "cents" to cents?.toString())

    override fun withMeta(meta: EntityMeta<NoteId>) = copy(meta = meta)
}

/** A broken entity: `secret` is persisted but (wrongly) not listed in auditFields. */
private data class Leaky(
    override val meta: EntityMeta<NoteId>,
    val title: String,
    val secret: String,
) : LedgerEntity<NoteId, Leaky> {
    override val auditType = "leaky"

    override fun auditFields(): Map<String, String?> = mapOf("title" to title)

    override fun withMeta(meta: EntityMeta<NoteId>) = copy(meta = meta)
}

private class Fixture {
    val kit = LedgerTestKit()
    val notes = InMemoryEntityRepository<Note, NoteId>(kit.store, "note")
    val writer get() = kit.writer
    val trail get() = kit.store.auditLog.entries
    val id = NoteId("note-1")

    suspend fun createNote(title: String = "Rent") = writer.create(notes, id) { Note(it, title) }
}

class AuditedWriterTest :
    FunSpec({
        context("create") {
            test("stores the entity with fresh meta and audits a CREATE with the current user and device") {
                val f = Fixture()
                val me = f.kit.initialise()
                val auditedBefore = f.trail.size

                val note = f.writer.create(f.notes, f.id) { Note(it, "Rent", cents = 1_500) }

                note.meta.id shouldBe f.id
                note.meta.createdAt shouldBe f.kit.clock.instant()
                note.meta.updatedAt shouldBe f.kit.clock.instant()
                note.meta.createdByUserId shouldBe me.user.id
                note.meta.createdOnDeviceId shouldBe me.device.id
                note.meta.isActive shouldBe true
                f.notes.get(f.id) shouldBe note
                f.trail shouldHaveSize auditedBefore + 1
                f.trail.shouldHaveAudited(
                    entityType = "note",
                    entityId = "note-1",
                    action = AuditAction.CREATE,
                    changes = mapOf("title" to FieldChange(null, "Rent"), "cents" to FieldChange(null, "1500")),
                    userId = me.user.id,
                    deviceId = me.device.id,
                    source = AuditSource.MANUAL,
                )
            }

            test("records the source of the change") {
                val f = Fixture()
                f.kit.initialise()
                f.writer.create(f.notes, f.id, AuditSource.RECURRING) { Note(it, "EMI") }
                f.trail.shouldHaveAudited("note", "note-1", AuditAction.CREATE, source = AuditSource.RECURRING)
            }

            test("the writer owns the meta: a builder cannot forge attribution or timestamps") {
                val f = Fixture()
                val me = f.kit.initialise()
                val forged =
                    f.writer.create(f.notes, f.id) {
                        Note(it.copy(createdByUserId = UserId("someone-else"), deletedAt = it.createdAt), "Rent")
                    }
                forged.meta.createdByUserId shouldBe me.user.id
                forged.meta.deletedAt.shouldBeNull()
            }

            test("fails closed without an identity and stores nothing") {
                val f = Fixture()
                shouldThrow<LedgerError.NotInitialised> { f.createNote() }
                f.notes.all().shouldBeEmpty()
                f.trail.shouldBeEmpty()
            }
        }

        context("update") {
            test("stores the change, bumps updatedAt, keeps createdAt, and audits only the changed fields") {
                val f = Fixture()
                val me = f.kit.initialise()
                val created = f.createNote("Rent")
                f.kit.clock.advance(Duration.ofHours(2))

                val updated = f.writer.update(f.notes, f.id) { it.copy(title = "Rent (Nov)", cents = 99) }

                updated.title shouldBe "Rent (Nov)"
                updated.meta.createdAt shouldBe created.meta.createdAt
                updated.meta.updatedAt shouldBe f.kit.clock.instant()
                f.notes.get(f.id) shouldBe updated
                f.trail.shouldHaveAudited(
                    "note",
                    "note-1",
                    AuditAction.UPDATE,
                    changes =
                        mapOf(
                            "title" to FieldChange("Rent", "Rent (Nov)"),
                            "cents" to FieldChange(null, "99"),
                        ),
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
            }

            test("a no-op update writes no audit row and does not touch updatedAt") {
                val f = Fixture()
                f.kit.initialise()
                val created = f.createNote("Rent")
                val trailSize = f.trail.size
                f.kit.clock.advance(Duration.ofHours(2))

                val result = f.writer.update(f.notes, f.id) { it.copy(title = "Rent") }

                result shouldBe created
                f.notes.get(f.id) shouldBe created
                f.trail shouldHaveSize trailSize
            }

            test("the writer owns the meta: an update cannot change attribution or lifecycle") {
                val f = Fixture()
                f.kit.initialise()
                val created = f.createNote()
                val updated =
                    f.writer.update(f.notes, f.id) {
                        it.copy(
                            title = "Changed",
                            meta = it.meta.copy(createdByUserId = UserId("x"), deletedAt = f.kit.clock.instant()),
                        )
                    }
                updated.meta.createdByUserId shouldBe created.meta.createdByUserId
                updated.meta.isDeleted shouldBe false
            }

            test("a change to a field missing from auditFields fails closed instead of being dropped silently") {
                val f = Fixture()
                f.kit.initialise()
                val leaky = InMemoryEntityRepository<Leaky, NoteId>(f.kit.store, "leaky")
                f.writer.create(leaky, f.id) { Leaky(it, "Rent", secret = "a") }
                val trailSize = f.trail.size

                shouldThrow<IllegalStateException> {
                    f.writer.update(leaky, f.id) { it.copy(secret = "b") }
                }

                leaky.get(f.id)?.secret shouldBe "a"
                f.trail shouldHaveSize trailSize
            }

            test("an update that changes an audited field is still fine for an entity with an unaudited one") {
                val f = Fixture()
                f.kit.initialise()
                val leaky = InMemoryEntityRepository<Leaky, NoteId>(f.kit.store, "leaky")
                f.writer.create(leaky, f.id) { Leaky(it, "Rent", secret = "a") }
                f.writer.update(leaky, f.id) { it.copy(title = "Rent 2", secret = "b") }
                leaky.get(f.id)?.title shouldBe "Rent 2"
            }

            test("updating a missing or deleted entity is refused") {
                val f = Fixture()
                f.kit.initialise()
                shouldThrow<LedgerError.NotFound> { f.writer.update(f.notes, f.id) { it.copy(title = "x") } }
                    .what shouldBe "note"
                f.createNote()
                f.writer.delete(f.notes, f.id)
                shouldThrow<LedgerError.Deleted> { f.writer.update(f.notes, f.id) { it.copy(title = "x") } }
            }
        }

        context("delete and restore") {
            test("delete tombstones the entity, hides it from getLive, and audits a DELETE snapshot") {
                val f = Fixture()
                val me = f.kit.initialise()
                f.createNote("Rent")
                f.kit.clock.advance(Duration.ofHours(1))

                val deleted = f.writer.delete(f.notes, f.id)

                deleted.meta.deletedAt shouldBe f.kit.clock.instant()
                deleted.meta.updatedAt shouldBe f.kit.clock.instant()
                f.notes.get(f.id) shouldBe deleted
                f.notes.getLive(f.id).shouldBeNull()
                f.trail.shouldHaveAudited(
                    "note",
                    "note-1",
                    AuditAction.DELETE,
                    changes = mapOf("title" to FieldChange("Rent", null)),
                    userId = me.user.id,
                )
            }

            test("deleting twice is idempotent: the second call audits nothing") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote()
                f.writer.delete(f.notes, f.id)
                val trailSize = f.trail.size
                f.kit.clock.advance(Duration.ofHours(1))

                val again = f.writer.delete(f.notes, f.id)

                f.trail shouldHaveSize trailSize
                again.meta.deletedAt shouldBe
                    f.notes
                        .get(f.id)
                        ?.meta
                        ?.deletedAt
            }

            test("restore brings a tombstoned entity back and audits a RESTORE") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote("Rent")
                f.writer.delete(f.notes, f.id)

                val restored = f.writer.restore(f.notes, f.id)

                restored.meta.isDeleted shouldBe false
                f.notes.getLive(f.id) shouldBe restored
                f.trail.shouldHaveAudited(
                    "note",
                    "note-1",
                    AuditAction.RESTORE,
                    changes = mapOf("title" to FieldChange(null, "Rent")),
                )
            }

            test("restoring an entity that is not deleted is a no-op") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote()
                val trailSize = f.trail.size
                f.writer.restore(f.notes, f.id)
                f.trail shouldHaveSize trailSize
            }

            test("deleting or restoring a missing entity is refused") {
                val f = Fixture()
                f.kit.initialise()
                shouldThrow<LedgerError.NotFound> { f.writer.delete(f.notes, f.id) }.what shouldBe "note"
                shouldThrow<LedgerError.NotFound> { f.writer.restore(f.notes, f.id) }.what shouldBe "note"
                shouldThrow<LedgerError.NotFound> { f.writer.archive(f.notes, f.id) }.what shouldBe "note"
            }
        }

        context("archive and unarchive") {
            test("archive keeps the entity live but not active, and audits the archived_at change") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote()
                f.kit.clock.advance(Duration.ofHours(1))

                val archived = f.writer.archive(f.notes, f.id)

                archived.meta.archivedAt shouldBe f.kit.clock.instant()
                archived.meta.isActive shouldBe false
                f.notes.getLive(f.id) shouldBe archived
                f.trail.shouldHaveAudited(
                    "note",
                    "note-1",
                    AuditAction.ARCHIVE,
                    changes =
                        mapOf(
                            "archived_at" to
                                FieldChange(
                                    null,
                                    f.kit.clock
                                        .instant()
                                        .toString(),
                                ),
                        ),
                )
            }

            test("unarchive makes it active again and audits the archived_at change back to null") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote()
                val archivedAt = f.kit.clock.instant()
                f.writer.archive(f.notes, f.id)
                f.kit.clock.advance(Duration.ofHours(3))

                val unarchived = f.writer.unarchive(f.notes, f.id)

                unarchived.meta.isActive shouldBe true
                f.trail.shouldHaveAudited(
                    "note",
                    "note-1",
                    AuditAction.UNARCHIVE,
                    changes = mapOf("archived_at" to FieldChange(archivedAt.toString(), null)),
                )
            }

            test("archiving an archived entity, or unarchiving an active one, is a no-op") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote()
                f.writer.unarchive(f.notes, f.id)
                f.trail.shouldNotHaveAudited("note", "note-1", AuditAction.UNARCHIVE)
                f.writer.archive(f.notes, f.id)
                val trailSize = f.trail.size
                f.writer.archive(f.notes, f.id)
                f.trail shouldHaveSize trailSize
            }

            test("a deleted entity cannot be archived or unarchived") {
                val f = Fixture()
                f.kit.initialise()
                f.createNote()
                f.writer.delete(f.notes, f.id)
                shouldThrow<LedgerError.Deleted> { f.writer.archive(f.notes, f.id) }
                shouldThrow<LedgerError.Deleted> { f.writer.unarchive(f.notes, f.id) }
            }
        }

        context("atomicity (INT-4)") {
            val failing =
                object : AuditLog {
                    override suspend fun append(entry: AuditEntry) = error("audit store unavailable")
                }

            test("a failing audit append rolls the create back") {
                val f = Fixture()
                f.kit.initialise()
                val trailSize = f.trail.size
                val broken = f.kit.writerWith(failing)

                shouldThrow<IllegalStateException> { broken.create(f.notes, f.id) { Note(it, "Rent") } }

                f.notes.all().shouldBeEmpty()
                f.trail shouldHaveSize trailSize
            }

            test("a failing audit append rolls back an update, a delete and an archive") {
                val f = Fixture()
                f.kit.initialise()
                val created = f.createNote("Rent")
                val trailSize = f.trail.size
                val broken = f.kit.writerWith(failing)

                shouldThrow<IllegalStateException> { broken.update(f.notes, f.id) { it.copy(title = "Changed") } }
                shouldThrow<IllegalStateException> { broken.delete(f.notes, f.id) }
                shouldThrow<IllegalStateException> { broken.archive(f.notes, f.id) }

                f.notes.get(f.id) shouldBe created
                f.trail shouldHaveSize trailSize
            }

            test("an exception later in the caller's transaction rolls back the write and its audit entry") {
                val f = Fixture()
                f.kit.initialise()
                val trailSize = f.trail.size

                shouldThrow<IllegalStateException> {
                    f.kit.store.inTransaction {
                        f.createNote("Rent")
                        error("a later step failed")
                    }
                }

                f.notes.all().shouldBeEmpty()
                f.trail shouldHaveSize trailSize
            }

            test("several audited writes in one caller transaction commit together") {
                val f = Fixture()
                f.kit.initialise()
                f.kit.store.inTransaction {
                    f.createNote("Rent")
                    f.writer.update(f.notes, f.id) { it.copy(title = "Rent 2") }
                }
                f.notes.get(f.id)?.title shouldBe "Rent 2"
                f.trail.map { it.action }.takeLast(2) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
            }
        }
    })
