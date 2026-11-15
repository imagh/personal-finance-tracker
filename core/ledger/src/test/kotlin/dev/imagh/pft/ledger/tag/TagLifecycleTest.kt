package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.matchers.shouldNotHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/** Archive, unarchive, delete and restore of tags, and the lists a screen and a picker read from them. */
class TagLifecycleTest :
    FunSpec({
        context("archive and unarchive") {
            test("an archived tag is kept but leaves the picker, audited, and unarchive reverses it") {
                val f = TagFixture()
                val me = f.kit.initialise()
                val keep = f.service.create("Work")
                val travel = f.service.create("Travel")

                val archived = f.service.archive(travel.meta.id)

                archived.meta.isArchived shouldBe true
                archived.name shouldBe "Travel"
                f.service.pickable().map { it.meta.id } shouldBe listOf(keep.meta.id)
                f.service
                    .lists()
                    .archived
                    .map { it.meta.id } shouldBe listOf(travel.meta.id)
                f.trail.shouldHaveAudited(
                    "tag",
                    travel.meta.id.value,
                    AuditAction.ARCHIVE,
                    userId = me.user.id,
                    deviceId = me.device.id,
                )

                f.service
                    .unarchive(travel.meta.id)
                    .meta.isArchived shouldBe false

                f.service.pickable().map { it.meta.id } shouldBe listOf(travel.meta.id, keep.meta.id)
                f.service
                    .lists()
                    .archived
                    .shouldBeEmpty()
                f.trail.shouldHaveAudited("tag", travel.meta.id.value, AuditAction.UNARCHIVE, userId = me.user.id)
            }

            test("repeating either is a no-op that audits nothing more") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                f.service.unarchive(tag.meta.id) // not archived: nothing to do
                f.trail.shouldNotHaveAudited("tag", tag.meta.id.value, AuditAction.UNARCHIVE)
                f.service.archive(tag.meta.id)
                val trailSize = f.trail.size

                f.service.archive(tag.meta.id)

                f.trail shouldHaveSize trailSize
            }

            test("an unknown or deleted tag cannot be archived or unarchived") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                f.delete(tag)

                f.shouldRefuse<LedgerError.NotFound> { f.service.archive(TagId("missing")) }
                f.shouldRefuse<LedgerError.Deleted> { f.service.archive(tag.meta.id) }
                f.shouldRefuse<LedgerError.Deleted> { f.service.unarchive(tag.meta.id) }
            }
        }

        context("delete and restore") {
            test("a deleted tag is a tombstone, absent from every list, audited, and repeating it is a no-op") {
                val f = TagFixture()
                val me = f.kit.initialise()
                val tag = f.service.create("Travel")
                val archived = f.service.create("Work")
                f.service.archive(archived.meta.id)

                val gone = f.service.delete(tag.meta.id)
                f.service.delete(archived.meta.id)

                gone.meta.isDeleted shouldBe true
                f.stored() shouldBe listOf(gone, f.stored().last())
                f.stored().all { it.meta.isDeleted } shouldBe true
                f.service.pickable().shouldBeEmpty()
                f.service
                    .lists()
                    .active
                    .shouldBeEmpty()
                f.service
                    .lists()
                    .archived
                    .shouldBeEmpty()
                f.trail.shouldHaveAudited(
                    "tag",
                    tag.meta.id.value,
                    AuditAction.DELETE,
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
                val trailSize = f.trail.size

                f.service.delete(tag.meta.id)

                f.trail shouldHaveSize trailSize
            }

            test("restore brings it back with its name, audited, and not-deleted is a no-op") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                f.service.restore(tag.meta.id) // not deleted: nothing to do
                f.trail.shouldNotHaveAudited("tag", tag.meta.id.value, AuditAction.RESTORE)
                f.service.delete(tag.meta.id)

                val restored = f.service.restore(tag.meta.id)

                restored.meta.isDeleted shouldBe false
                restored.name shouldBe "Travel"
                f.service.pickable().map { it.meta.id } shouldBe listOf(tag.meta.id)
                f.trail.shouldHaveAudited("tag", tag.meta.id.value, AuditAction.RESTORE)
                val trailSize = f.trail.size

                f.service.restore(tag.meta.id)

                f.trail shouldHaveSize trailSize
            }

            test("restore keeps an archived tag archived") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                f.service.archive(tag.meta.id)
                f.service.delete(tag.meta.id)

                f.service
                    .restore(tag.meta.id)
                    .meta.isArchived shouldBe true
                f.service
                    .lists()
                    .archived
                    .map { it.meta.id } shouldBe listOf(tag.meta.id)
            }

            test("restore into a name clash is refused and the tag stays deleted") {
                val f = TagFixture()
                f.kit.initialise()
                val old = f.service.create("Travel")
                f.service.delete(old.meta.id)
                f.service.create("travel")

                f.shouldRefuse<LedgerError.DuplicateName> { f.service.restore(old.meta.id) }.what shouldBe "tag"

                f
                    .stored()
                    .single { it.meta.id == old.meta.id }
                    .meta.isDeleted shouldBe true
            }

            test("restore of an unknown id is not found") {
                val f = TagFixture()
                f.kit.initialise()

                f.shouldRefuse<LedgerError.NotFound> { f.service.restore(TagId("missing")) }
            }

            test("delete of an unknown id is not found") {
                val f = TagFixture()
                f.kit.initialise()

                f.shouldRefuse<LedgerError.NotFound> { f.service.delete(TagId("missing")) }
            }
        }

        context("the lists") {
            test("lists() sorts ignoring case, so an upper-case name does not jump ahead of a lower-case one") {
                val f = TagFixture()
                f.kit.initialise()
                val cherry = f.service.create("cherry")
                val banana = f.service.create("Banana")
                val apple = f.service.create("apple")
                val archivedZ = f.service.create("Zebra")
                val archivedA = f.service.create("avocado")
                f.service.archive(archivedZ.meta.id)
                f.service.archive(archivedA.meta.id)

                val lists = f.service.lists()

                lists.active.map { it.meta.id } shouldBe listOf(apple.meta.id, banana.meta.id, cherry.meta.id)
                lists.archived.map { it.meta.id } shouldBe listOf(archivedA.meta.id, archivedZ.meta.id)
            }

            test("tags with the same name are ordered by id, never by the repository's order") {
                val f = TagFixture()
                f.kit.initialise()
                // Written straight through the writer, so two live tags share a name (a sync or a restore race).
                // The fake hands rows back newest first, so only a full tie-break puts "a" before "b".
                f.raw("tag-a", "Travel")
                f.raw("tag-b", "travel")
                f.raw("tag-c", "TRAVEL")

                f.service
                    .lists()
                    .active
                    .map { it.meta.id.value } shouldBe listOf("tag-a", "tag-b", "tag-c")
                f.service.pickable().map { it.meta.id.value } shouldBe listOf("tag-a", "tag-b", "tag-c")
            }

            test("pickable() excludes archived and deleted tags and keeps lists().active's order") {
                val f = TagFixture()
                f.kit.initialise()
                val work = f.service.create("Work")
                val travel = f.service.create("Travel")
                val old = f.service.create("Old")
                val gone = f.service.create("Gone")
                f.service.archive(old.meta.id)
                f.service.delete(gone.meta.id)

                f.service.pickable().map { it.meta.id } shouldBe listOf(travel.meta.id, work.meta.id)
                f.service.pickable() shouldBe f.service.lists().active
            }

            test("no tags gives empty lists") {
                val f = TagFixture()
                f.kit.initialise()

                f.service
                    .lists()
                    .active
                    .shouldBeEmpty()
                f.service
                    .lists()
                    .archived
                    .shouldBeEmpty()
                f.service.pickable().shouldBeEmpty()
            }
        }
    })
