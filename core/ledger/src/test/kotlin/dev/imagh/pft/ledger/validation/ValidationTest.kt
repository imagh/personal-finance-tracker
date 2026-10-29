package dev.imagh.pft.ledger.validation

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

private data class Widget(
    override val meta: EntityMeta<UserId>,
) : LedgerEntity<UserId, Widget> {
    override val auditType = "widget"

    override fun auditFields(): Map<String, String?> = emptyMap()

    override fun withMeta(meta: EntityMeta<UserId>) = copy(meta = meta)
}

class ValidationTest :
    FunSpec({
        context("requireName") {
            test("returns the trimmed text") {
                Validation.requireName("  Groceries \n", max = 20) shouldBe "Groceries"
            }
            test("rejects blank text") {
                shouldThrow<LedgerError.BlankName> { Validation.requireName("", max = 20) }
                shouldThrow<LedgerError.BlankName> { Validation.requireName("   \t\n", max = 20) }
            }
            test("accepts text of exactly the limit and rejects one more") {
                Validation.requireName("a".repeat(20), max = 20) shouldBe "a".repeat(20)
                val error = shouldThrow<LedgerError.NameTooLong> { Validation.requireName("a".repeat(21), max = 20) }
                error.max shouldBe 20
            }
            test("measures the limit after trimming, so padding cannot cause a rejection") {
                Validation.requireName("  " + "a".repeat(20) + "  ", max = 20) shouldBe "a".repeat(20)
            }
        }

        context("requireAmount") {
            test("accepts one paisa up to the limit inclusive") {
                Validation.requireAmount(Paise(1)) shouldBe Paise(1)
                Validation.requireAmount(LedgerLimits.MAX_AMOUNT) shouldBe LedgerLimits.MAX_AMOUNT
            }
            test("rejects zero and negative amounts") {
                shouldThrow<LedgerError.AmountNotPositive> { Validation.requireAmount(Paise.ZERO) }
                shouldThrow<LedgerError.AmountNotPositive> { Validation.requireAmount(Paise(-5)) }
            }
            test("rejects an amount above the limit, even by one paisa") {
                val over = Paise(LedgerLimits.MAX_AMOUNT.value + 1)
                val error = shouldThrow<LedgerError.AmountTooLarge> { Validation.requireAmount(over) }
                error.max shouldBe LedgerLimits.MAX_AMOUNT
            }
            test("honours a custom limit") {
                Validation.requireAmount(Paise(500), max = Paise(500)) shouldBe Paise(500)
                shouldThrow<LedgerError.AmountTooLarge> { Validation.requireAmount(Paise(501), max = Paise(500)) }
            }
        }

        context("requireLive") {
            val t0 = Instant.parse("2026-11-01T10:00:00Z")
            val meta = EntityMeta(UserId("w-1"), t0, t0, UserId("u-1"), DeviceId("d-1"))

            test("returns an active entity unchanged") {
                val widget = Widget(meta)
                Validation.requireLive(widget, "widget") shouldBe widget
            }
            test("rejects a missing reference") {
                val error = shouldThrow<LedgerError.NotFound> { Validation.requireLive<Widget>(null, "widget") }
                error.what shouldBe "widget"
            }
            test("rejects a deleted reference") {
                val deleted = Widget(meta.copy(deletedAt = t0))
                shouldThrow<LedgerError.Deleted> { Validation.requireLive(deleted, "widget") }.what shouldBe "widget"
            }
            test("rejects an archived reference") {
                val archived = Widget(meta.copy(archivedAt = t0))
                shouldThrow<LedgerError.Archived> { Validation.requireLive(archived, "widget") }.what shouldBe "widget"
            }
            test("a reference that is both deleted and archived is reported as deleted") {
                val both = Widget(meta.copy(deletedAt = t0, archivedAt = t0))
                shouldThrow<LedgerError.Deleted> { Validation.requireLive(both, "widget") }
            }
        }

        test("LedgerLimits are the documented static limits") {
            LedgerLimits.MAX_NAME_LENGTH shouldBe 100
            LedgerLimits.MAX_TITLE_LENGTH shouldBe 200
            LedgerLimits.MAX_DESCRIPTION_LENGTH shouldBe 2_000
            LedgerLimits.MAX_AMOUNT shouldBe Paise.MAX_AMOUNT
        }

        test("errors never echo user content, only the kind or the limit") {
            LedgerError.BlankName().message shouldBe "A name is required"
            LedgerError.NameTooLong(20).message shouldBe "A name may be at most 20 characters"
            LedgerError.NotFound("account").message shouldBe "No such account"
        }
    })
