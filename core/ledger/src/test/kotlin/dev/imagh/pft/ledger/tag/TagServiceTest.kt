package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Duration

/** Creating and renaming tags, and the name rules that guard both (PRD L-3, plan D10). */
class TagServiceTest :
    FunSpec({
        context("create") {
            test("stores the trimmed name, audited with who and where") {
                val f = TagFixture()
                val me = f.kit.initialise()

                val tag = f.service.create("  Travel ")

                f.stored() shouldBe listOf(tag)
                tag.name shouldBe "Travel"
                tag.meta.isActive shouldBe true
                tag.meta.createdByUserId shouldBe me.user.id
                tag.meta.createdOnDeviceId shouldBe me.device.id
                f.trail.shouldHaveAudited(
                    "tag",
                    tag.meta.id.value,
                    AuditAction.CREATE,
                    changes = mapOf("name" to FieldChange(null, "Travel")),
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
            }

            test("a name of exactly the limit is accepted") {
                val f = TagFixture()
                f.kit.initialise()

                f.service
                    .create("a".repeat(LedgerLimits.MAX_NAME_LENGTH))
                    .name.length shouldBe
                    LedgerLimits.MAX_NAME_LENGTH
            }

            test("the same name under a different case or padding is a duplicate") {
                val f = TagFixture()
                f.kit.initialise()
                f.service.create("Travel")

                f.shouldRefuse<LedgerError.DuplicateName> { f.service.create("travel") }.what shouldBe "tag"
                f.shouldRefuse<LedgerError.DuplicateName> { f.service.create(" TRAVEL ") }
                f.shouldRefuse<LedgerError.DuplicateName> { f.service.create("Travel") }
                f.stored() shouldHaveSize 1
            }

            test("the duplicate rule folds non-ASCII letters too") {
                val f = TagFixture()
                f.kit.initialise()
                f.service.create("Éclair")

                f.shouldRefuse<LedgerError.DuplicateName> { f.service.create("éCLAIR") }
            }

            test("an archived tag still holds its name, a deleted one does not") {
                val f = TagFixture()
                f.kit.initialise()
                val archived = f.service.create("Travel")
                f.archive(archived)
                val deleted = f.service.create("Work")
                f.delete(deleted)

                f.shouldRefuse<LedgerError.DuplicateName> { f.service.create("travel") }
                val again = f.service.create("work")

                again.name shouldBe "work"
                f.stored() shouldHaveSize 3
            }

            test("the error names the kind of record, never the name") {
                val f = TagFixture()
                f.kit.initialise()
                f.service.create("Secret trip")

                val error = f.shouldRefuse<LedgerError.DuplicateName> { f.service.create("secret trip") }

                error.message.orEmpty().contains("trip") shouldBe false
            }

            context("rejections write nothing and audit nothing") {
                test("a blank name") {
                    val f = TagFixture()
                    f.kit.initialise()
                    f.shouldRefuse<LedgerError.BlankName> { f.service.create("   ") }
                    f.shouldRefuse<LedgerError.BlankName> { f.service.create("") }
                }
                test("a name over the limit") {
                    val f = TagFixture()
                    f.kit.initialise()
                    f
                        .shouldRefuse<LedgerError.NameTooLong> {
                            f.service.create("a".repeat(LedgerLimits.MAX_NAME_LENGTH + 1))
                        }.max shouldBe LedgerLimits.MAX_NAME_LENGTH
                }
                test("before first-run setup") {
                    val f = TagFixture()
                    f.shouldRefuse<LedgerError.NotInitialised> { f.service.create("Travel") }
                }
            }

            test("a failing audit append leaves no tag behind (INT-4)") {
                val f = TagFixture()
                f.kit.initialise()
                val trailSize = f.trail.size
                val failing =
                    object : AuditLog {
                        override suspend fun append(entry: AuditEntry) = error("audit store unavailable")
                    }
                val broken = TagService(f.kit.store, f.kit.writerWith(failing), f.kit.tagRepository, f.kit.ids)

                shouldThrow<IllegalStateException> { broken.create("Travel") }

                f.stored().shouldBeEmpty()
                f.trail shouldHaveSize trailSize
            }
        }

        context("edit") {
            test("renames, audited with only the name change") {
                val f = TagFixture()
                val me = f.kit.initialise()
                val tag = f.service.create("Travel")
                f.kit.clock.advance(Duration.ofMinutes(5))

                val edited = f.service.edit(tag.meta.id, " Trips ")

                edited.name shouldBe "Trips"
                edited.meta.createdAt shouldBe tag.meta.createdAt
                (edited.meta.updatedAt > tag.meta.updatedAt) shouldBe true
                f.stored() shouldBe listOf(edited)
                f.trail.shouldHaveAudited(
                    "tag",
                    tag.meta.id.value,
                    AuditAction.UPDATE,
                    changes = mapOf("name" to FieldChange("Travel", "Trips")),
                    userId = me.user.id,
                )
            }

            test("a case change of its own name is allowed") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("travel")

                val edited = f.service.edit(tag.meta.id, "Travel")

                edited.name shouldBe "Travel"
                f.stored() shouldBe listOf(edited)
            }

            test("an identical edit writes nothing and audits nothing") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                val trailSize = f.trail.size

                f.service.edit(tag.meta.id, " Travel ") shouldBe tag

                f.stored() shouldBe listOf(tag)
                f.trail shouldHaveSize trailSize
            }

            test("renaming to another live tag's name is refused, whatever the case, archived or not") {
                val f = TagFixture()
                f.kit.initialise()
                val travel = f.service.create("Travel")
                val work = f.service.create("Work")
                f.archive(work)

                f.shouldRefuse<LedgerError.DuplicateName> { f.service.edit(travel.meta.id, "work") }.what shouldBe "tag"
                f.shouldRefuse<LedgerError.DuplicateName> { f.service.edit(work.meta.id, " TRAVEL ") }
            }

            test("renaming to the name of a deleted tag is fine") {
                val f = TagFixture()
                f.kit.initialise()
                val travel = f.service.create("Travel")
                f.delete(f.service.create("Work"))

                f.service.edit(travel.meta.id, "work").name shouldBe "work"
            }

            test("blank and over-limit names are refused") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")

                f.shouldRefuse<LedgerError.BlankName> { f.service.edit(tag.meta.id, "  ") }
                f.shouldRefuse<LedgerError.NameTooLong> {
                    f.service.edit(tag.meta.id, "a".repeat(LedgerLimits.MAX_NAME_LENGTH + 1))
                }
            }

            test("a deleted tag cannot be edited, an unknown id is not found") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                f.delete(tag)

                f.shouldRefuse<LedgerError.Deleted> { f.service.edit(tag.meta.id, "Other") }.what shouldBe "tag"
                f.shouldRefuse<LedgerError.NotFound> { f.service.edit(TagId("missing"), "Other") }.what shouldBe "tag"
            }

            test("an archived tag can still be renamed") {
                val f = TagFixture()
                f.kit.initialise()
                val tag = f.service.create("Travel")
                f.archive(tag)

                f.service.edit(tag.meta.id, "Trips").name shouldBe "Trips"
            }
        }
    })
