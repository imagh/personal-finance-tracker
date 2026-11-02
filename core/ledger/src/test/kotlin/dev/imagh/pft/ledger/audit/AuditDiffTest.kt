package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.entity.Auditable
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

private data class Thing(
    val name: String,
    val note: String? = null,
    val cents: Long = 0,
) : Auditable {
    override val auditType = "thing"
    override val auditId = "t-1"

    override fun auditFields(): Map<String, String?> =
        mapOf("name" to name, "note" to note, "cents" to cents.toString())
}

class AuditDiffTest :
    FunSpec({
        test("an update lists only the changed fields, with string old and new values") {
            val diff = AuditDiff.diff(Thing("Rent", cents = 100), Thing("Rent", cents = 250))
            diff shouldBe mapOf("cents" to FieldChange("100", "250"))
        }

        test("several changes are all listed") {
            val diff = AuditDiff.diff(Thing("Rent", "a"), Thing("Rent 2", "b"))
            diff shouldBe
                mapOf(
                    "name" to FieldChange("Rent", "Rent 2"),
                    "note" to FieldChange("a", "b"),
                )
        }

        test("setting and clearing an optional field are changes") {
            AuditDiff.diff(Thing("x", note = null), Thing("x", note = "hello")) shouldBe
                mapOf("note" to FieldChange(null, "hello"))
            AuditDiff.diff(Thing("x", note = "hello"), Thing("x", note = null)) shouldBe
                mapOf("note" to FieldChange("hello", null))
        }

        test("a create has null olds, and fields that are null stay out") {
            val diff = AuditDiff.diff(null, Thing("Rent", note = null, cents = 100))
            diff shouldBe
                mapOf(
                    "name" to FieldChange(null, "Rent"),
                    "cents" to FieldChange(null, "100"),
                )
        }

        test("a delete has null news, and fields that were null stay out") {
            val diff = AuditDiff.diff(Thing("Rent", note = null, cents = 100), null)
            diff shouldBe
                mapOf(
                    "name" to FieldChange("Rent", null),
                    "cents" to FieldChange("100", null),
                )
        }

        test("an identical object gives no diff, so a no-op edit writes no audit row") {
            AuditDiff.diff(Thing("Rent", "n", 5), Thing("Rent", "n", 5)).shouldBeEmpty()
        }

        test("nothing to compare gives no diff") {
            AuditDiff.diff(null, null).shouldBeEmpty()
        }

        test("a field present on one side only counts as null on the other") {
            val before =
                object : Auditable {
                    override val auditType = "x"
                    override val auditId = "1"

                    override fun auditFields() = mapOf("a" to "1")
                }
            val after =
                object : Auditable {
                    override val auditType = "x"
                    override val auditId = "1"

                    override fun auditFields() = mapOf("a" to "1", "b" to "2")
                }
            AuditDiff.diff(before, after) shouldBe mapOf("b" to FieldChange(null, "2"))
            AuditDiff.diff(after, before) shouldBe mapOf("b" to FieldChange("2", null))
        }
    })
