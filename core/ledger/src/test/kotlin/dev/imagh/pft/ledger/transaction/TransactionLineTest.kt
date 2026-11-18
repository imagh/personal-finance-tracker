package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.matchers.shouldHaveCompleteAuditFields
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

/** A line's classification and its audit keys, `tag_ids` above all (IT §3.1, §4). */
class TransactionLineTest :
    FunSpec({
        val at = Instant.parse("2026-11-18T10:15:30Z")

        fun line(tagIds: Set<TagId> = setOf(TagId("t-b"), TagId("t-c"), TagId("t-a"))) =
            TransactionLine(
                meta = EntityMeta(TransactionLineId("l-1"), at, at, UserId("u-1"), DeviceId("d-1")),
                transactionId = TransactionId("x-1"),
                position = 2,
                amount = Paise(4_200),
                categoryId = CategoryId("c-1"),
                note = "Milk",
                tagIds = tagIds,
            )

        test("a fully populated line lists every persisted field in auditFields()") {
            line().shouldHaveCompleteAuditFields()
        }

        test("audits each field under its column name, as text") {
            line().auditFields() shouldBe
                mapOf(
                    "transaction_id" to "x-1",
                    "position" to "2",
                    "amount" to "4200",
                    "category_id" to "c-1",
                    "note" to "Milk",
                    "tag_ids" to "t-a,t-b,t-c",
                )
        }

        test("tag_ids is the ids sorted and joined with a comma, whatever order the set holds them in") {
            line(linkedSetOf(TagId("t-c"), TagId("t-a"), TagId("t-b"))).auditFields()["tag_ids"] shouldBe "t-a,t-b,t-c"
            line(linkedSetOf(TagId("t-a"), TagId("t-c"), TagId("t-b"))).auditFields()["tag_ids"] shouldBe "t-a,t-b,t-c"
        }

        test("tag_ids sorts by the id text, so a longer id with the same prefix comes after it") {
            line(linkedSetOf(TagId("t-ab"), TagId("t-a"), TagId("t-B"))).auditFields()["tag_ids"] shouldBe
                "t-B,t-a,t-ab"
        }

        test("a line with no tags audits tag_ids as null, like any unset field") {
            line(emptySet()).auditFields()["tag_ids"] shouldBe null
            line(emptySet()).auditFields().containsKey("tag_ids") shouldBe true
        }

        test("is audited as transaction_line under its id") {
            line().auditType shouldBe "transaction_line"
            line().auditId shouldBe "l-1"
        }
    })
