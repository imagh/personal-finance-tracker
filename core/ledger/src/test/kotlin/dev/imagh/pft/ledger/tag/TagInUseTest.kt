package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionFixture
import dev.imagh.pft.ledger.transaction.shouldRefuseInUse
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * A tag that a live line of a live transaction carries cannot be deleted: archive it instead
 * (slice C C1d, D4, Q4; PRD L-3). The same join as categories, over the line's tag set.
 */
class TagInUseTest :
    FunSpec({
        context("delete") {
            test("is refused while a live line of a live transaction carries the tag: no tombstone, no audit entry") {
                val f = TransactionFixture().start()
                f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))

                val error = f.shouldRefuseInUse("tag") { f.kit.tags.delete(f.travel.meta.id) }

                error.message shouldBe "This tag is in use by transactions"
            }

            test("is refused for an archived tag, and archiving is still allowed while in use") {
                val f = TransactionFixture().start()
                f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))

                f.kit.tags
                    .archive(f.travel.meta.id)
                    .meta.isArchived shouldBe true

                f.shouldRefuseInUse("tag") { f.kit.tags.delete(f.travel.meta.id) }
            }

            test("succeeds when only a deleted transaction's line, still live, carries the tag") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))
                f.service.delete(saved.transaction.meta.id)
                f
                    .storedLines()
                    .single()
                    .meta.isDeleted shouldBe false

                f.kit.tags
                    .delete(f.travel.meta.id)
                    .meta.isDeleted shouldBe true

                f.trail.shouldHaveAudited("tag", f.travel.meta.id.value, AuditAction.DELETE)
            }

            test("succeeds once an edit dropped the tag from the only line that carried it") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))
                f.service.edit(
                    saved.transaction.meta.id,
                    f.draft(
                        lineId =
                            saved.lines
                                .single()
                                .meta.id,
                    ),
                )

                f.kit.tags
                    .delete(f.travel.meta.id)
                    .meta.isDeleted shouldBe true
            }

            test("succeeds once an edit removed the only line that carried it (a tombstoned line)") {
                val f = TransactionFixture().start()
                val saved =
                    f.service.create(
                        f.draft().copy(
                            lines =
                                listOf(
                                    LineDraft(null, Paise(30_000), f.food.meta.id),
                                    LineDraft(null, Paise(20_000), f.food.meta.id, setOf(f.travel.meta.id)),
                                ),
                        ),
                    )
                f.shouldRefuseInUse("tag") { f.kit.tags.delete(f.travel.meta.id) }
                val kept = saved.lines.first { it.tagIds.isEmpty() }
                f.service.edit(saved.transaction.meta.id, f.draft(lineId = kept.meta.id))
                f.storedLines().single { it.meta.isDeleted }.tagIds shouldBe setOf(f.travel.meta.id)

                f.kit.tags
                    .delete(f.travel.meta.id)
                    .meta.isDeleted shouldBe true
            }

            test("counts only lines that carry this tag: an unused tag is deleted while another is in use") {
                val f = TransactionFixture().start()
                f.service.create(f.draft(tagIds = setOf(f.travel.meta.id)))
                val spare = f.kit.tags.create("Spare")

                f.kit.tags
                    .delete(spare.meta.id)
                    .meta.isDeleted shouldBe true
            }
        }
    })
