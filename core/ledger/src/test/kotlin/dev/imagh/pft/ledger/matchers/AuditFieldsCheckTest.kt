package dev.imagh.pft.ledger.matchers

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.fakes.InMemoryEntityRepository
import dev.imagh.pft.ledger.fakes.InMemoryLedgerStore
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.time.Instant
import java.time.LocalDate

@JvmInline
private value class WidgetId(
    override val value: String,
) : EntityId

private enum class Kind { SMALL, LARGE }

private val t0 = Instant.parse("2026-11-01T10:00:00Z")
private val meta = EntityMeta(WidgetId("w-1"), t0, t0, UserId("u-1"), DeviceId("d-1"))

/** Covers every field shape an entity will use: text, optional text, ids, money, enums, dates. */
private data class Complete(
    override val meta: EntityMeta<WidgetId>,
    val name: String,
    val note: String?,
    val openingBalance: Paise,
    val limitAmount: Paise?,
    val parentId: WidgetId?,
    val ownerId: WidgetId,
    val kind: Kind,
    val startDate: LocalDate,
    val reminderAt: Instant?,
    val count: Int,
    val bankRefID: Long,
    val isActive: Boolean,
) : LedgerEntity<WidgetId, Complete> {
    override val auditType = "widget"

    override fun auditFields(): Map<String, String?> =
        mapOf(
            "name" to name,
            "note" to note,
            "opening_balance" to openingBalance.value.toString(),
            "limit_amount" to limitAmount?.value?.toString(),
            "parent_id" to parentId?.value,
            "owner_id" to ownerId.value,
            "kind" to kind.name,
            "start_date" to startDate.toString(),
            "reminder_at" to reminderAt?.toString(),
            "count" to count.toString(),
            "bank_ref_id" to bankRefID.toString(),
            "is_active" to isActive.toString(),
        )

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

private val complete =
    Complete(
        meta,
        "Rent",
        null,
        Paise(-500),
        Paise(100),
        null,
        WidgetId("o-1"),
        Kind.LARGE,
        LocalDate.parse("2026-11-05"),
        null,
        3,
        99L,
        true,
    )

/** `forgotten` is persisted but missing from auditFields. */
private data class Omits(
    override val meta: EntityMeta<WidgetId>,
    val name: String,
    val forgotten: String,
) : LedgerEntity<WidgetId, Omits> {
    override val auditType = "omits"

    override fun auditFields(): Map<String, String?> = mapOf("name" to name)

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

/** `openingBalance` is audited under a misspelt key, so the key matches no field. */
private data class Misspelt(
    override val meta: EntityMeta<WidgetId>,
    val openingBalance: Long,
) : LedgerEntity<WidgetId, Misspelt> {
    override val auditType = "misspelt"

    override fun auditFields(): Map<String, String?> = mapOf("opening_balence" to openingBalance.toString())

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

/** Audits a key that is camelCase instead of snake_case. */
private data class WrongCase(
    override val meta: EntityMeta<WidgetId>,
    val openingBalance: Long,
) : LedgerEntity<WidgetId, WrongCase> {
    override val auditType = "wrong"

    override fun auditFields(): Map<String, String?> = mapOf("openingBalance" to openingBalance.toString())

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

/** Audits the right key with the wrong value. */
private data class WrongValue(
    override val meta: EntityMeta<WidgetId>,
    val amount: Paise,
    val label: String,
) : LedgerEntity<WidgetId, WrongValue> {
    override val auditType = "wrong_value"

    override fun auditFields(): Map<String, String?> =
        mapOf("amount" to (amount.value + 1).toString(), "label" to label)

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

/** A field that is "not persisted" is still not allowed: derive it in a getter instead. */
private data class HasScratch(
    override val meta: EntityMeta<WidgetId>,
    val name: String,
) : LedgerEntity<WidgetId, HasScratch> {
    override val auditType = "scratch"

    @Suppress("unused") // the unused backing field is exactly what the check must reject
    private val cache: String = name.uppercase()

    override fun auditFields(): Map<String, String?> = mapOf("name" to name)

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

/** The allowed way to have a derived value: a getter with no backing field. */
private data class HasDerived(
    override val meta: EntityMeta<WidgetId>,
    val name: String,
) : LedgerEntity<WidgetId, HasDerived> {
    override val auditType get() = "derived"
    val shout: String get() = name.uppercase()

    override fun auditFields(): Map<String, String?> = mapOf("name" to name)

    override fun withMeta(meta: EntityMeta<WidgetId>) = copy(meta = meta)
}

/**
 * A repository whose writes can bypass the check (by writing `rows` directly), so a test can seed a row
 * that the real fake would refuse. Private to the test: the published fixture has no opt-out.
 */
private class UncheckedOmitsRepository : InMemoryEntityRepository<Omits, WidgetId>(InMemoryLedgerStore(), "omits") {
    var enforce = false

    override suspend fun insert(entity: Omits) = if (enforce) super.insert(entity) else rows.insert(entity)

    override suspend fun update(entity: Omits) = if (enforce) super.update(entity) else rows.update(entity)
}

class AuditFieldsCheckTest :
    FunSpec({
        context("column names") {
            test("camelCase properties map to snake_case columns") {
                AuditFieldsCheck.columnName("name") shouldBe "name"
                AuditFieldsCheck.columnName("openingBalance") shouldBe "opening_balance"
                AuditFieldsCheck.columnName("accountId") shouldBe "account_id"
                AuditFieldsCheck.columnName("interestRateBps") shouldBe "interest_rate_bps"
                AuditFieldsCheck.columnName("line2Text") shouldBe "line2_text"
            }
            test("acronyms stay together") {
                AuditFieldsCheck.columnName("bankRefID") shouldBe "bank_ref_id"
                AuditFieldsCheck.columnName("smsURLText") shouldBe "sms_url_text"
            }
        }

        context("a complete entity") {
            test("passes, with every field shape: text, optional, ids, money, enum, dates, numbers, booleans") {
                complete.shouldHaveCompleteAuditFields()
                AuditFieldsCheck.problems(complete) shouldBe emptyList()
            }
            test("passes with the optional fields set") {
                complete
                    .copy(note = "n", limitAmount = Paise(5), parentId = WidgetId("p"), reminderAt = t0)
                    .shouldHaveCompleteAuditFields()
            }
            test("a derived value computed in a getter is fine") {
                HasDerived(meta, "x").shouldHaveCompleteAuditFields()
            }
        }

        context("an incomplete entity fails") {
            test("a persisted field missing from auditFields is named") {
                val error = shouldThrow<AssertionError> { Omits(meta, "a", "b").shouldHaveCompleteAuditFields() }
                error.message shouldContain "forgotten"
                error.message shouldContain "add \"forgotten\" to auditFields()"
            }
            test("an auditFields key that matches no field is named") {
                val error = shouldThrow<AssertionError> { Misspelt(meta, 1).shouldHaveCompleteAuditFields() }
                error.message shouldContain "opening_balence"
                error.message shouldContain "opening_balance"
            }
            test("a camelCase key does not satisfy the snake_case mapping") {
                val problems = AuditFieldsCheck.problems(WrongCase(meta, 1))
                problems.any { it.contains("openingBalance") } shouldBe true
            }
            test("a key with the wrong value is named, without printing the values") {
                val error =
                    shouldThrow<AssertionError> { WrongValue(meta, Paise(100), "x").shouldHaveCompleteAuditFields() }
                error.message shouldContain "amount"
                error.message shouldNotContain "101"
                AuditFieldsCheck.problems(WrongValue(meta, Paise(100), "x")).size shouldBe 1
            }
            test("a field that is not persisted is not allowed on an entity") {
                val error = shouldThrow<AssertionError> { HasScratch(meta, "a").shouldHaveCompleteAuditFields() }
                error.message shouldContain "cache"
            }
        }

        context("the fake repository applies the check on every write") {
            test("insert of an incomplete entity fails and stores nothing") {
                val repo = InMemoryEntityRepository<Omits, WidgetId>(InMemoryLedgerStore(), "omits")
                shouldThrow<AssertionError> { repo.insert(Omits(meta, "a", "b")) }
                repo.all() shouldBe emptyList()
            }
            test("update of an incomplete entity fails and keeps the stored one") {
                val repo = UncheckedOmitsRepository()
                val stored = Omits(meta, "a", "b")
                repo.insert(stored) // seeded past the check, as a pre-existing row
                repo.enforce = true

                shouldThrow<AssertionError> { repo.update(stored.copy(forgotten = "changed")) }

                repo.get(WidgetId("w-1")) shouldBe stored
            }
            test("complete entities insert and update normally") {
                val repo = InMemoryEntityRepository<Complete, WidgetId>(InMemoryLedgerStore(), "widget")
                repo.insert(complete)
                repo.update(complete.copy(name = "Changed"))
                repo.get(WidgetId("w-1"))?.name shouldBe "Changed"
            }
        }
    })
