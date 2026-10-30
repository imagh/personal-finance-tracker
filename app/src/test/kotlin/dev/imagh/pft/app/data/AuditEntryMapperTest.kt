package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.audit.AuditEntryEntity
import dev.imagh.pft.app.data.audit.AuditEntryMapper
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/**
 * [AuditEntryMapper]: the stored shape of an audit row. `changes_json` is a contract (devices will
 * read each other's rows once sync lands), so the exact text is pinned. Runs under Robolectric for
 * the real `org.json`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AuditEntryMapperTest {
    private val entry =
        AuditEntry(
            id = AuditEntryId("audit-1"),
            entityType = "account",
            entityId = "acc-1",
            action = AuditAction.UPDATE,
            changes = mapOf("name" to FieldChange("Old", "New")),
            userId = UserId("user-1"),
            deviceId = DeviceId("device-1"),
            source = AuditSource.RECURRING,
            at = Instant.parse("2026-11-01T10:00:00.123Z"),
        )

    @Test
    fun `an entry maps to a row with the documented columns and JSON shape`() {
        AuditEntryMapper.toEntity(entry) shouldBe
            AuditEntryEntity(
                id = "audit-1",
                entityType = "account",
                entityId = "acc-1",
                action = "UPDATE",
                changesJson = """{"name":{"old":"Old","new":"New"}}""",
                userId = "user-1",
                deviceId = "device-1",
                source = "RECURRING",
                at = 1_793_527_200_123L,
            )
    }

    @Test
    fun `a row maps back to the same entry`() {
        AuditEntryMapper.toModel(AuditEntryMapper.toEntity(entry)) shouldBe entry
    }

    @Test
    fun `unset sides survive as nulls and not as the text null`() {
        val create = entry.copy(action = AuditAction.CREATE, changes = mapOf("note" to FieldChange(null, "x")))
        val delete = entry.copy(action = AuditAction.DELETE, changes = mapOf("note" to FieldChange("x", null)))

        AuditEntryMapper.toModel(AuditEntryMapper.toEntity(create)) shouldBe create
        AuditEntryMapper.toModel(AuditEntryMapper.toEntity(delete)) shouldBe delete
    }

    @Test
    fun `an empty change set round trips`() {
        val empty = entry.copy(changes = emptyMap())

        AuditEntryMapper.toEntity(empty).changesJson shouldBe "{}"
        AuditEntryMapper.toModel(AuditEntryMapper.toEntity(empty)) shouldBe empty
    }

    @Test
    fun `awkward text round trips exactly`() {
        val awkward =
            mapOf(
                "quotes" to FieldChange("say \"hi\"", "back\\slash"),
                "lines" to FieldChange("a\nb\tc", " "),
                "unicode" to FieldChange("₹1,00,000.00 café", "💸"),
                "empty" to FieldChange("", " "),
                "json_like" to FieldChange("{\"a\":null}", "[1,2]"),
            )
        val tricky = entry.copy(changes = awkward)

        AuditEntryMapper.toModel(AuditEntryMapper.toEntity(tricky)).changes shouldBe awkward
    }

    @Test
    fun `field order is preserved`() {
        val ordered = entry.copy(changes = linkedMapOf("z" to FieldChange("1", "2"), "a" to FieldChange("3", "4")))

        AuditEntryMapper
            .toModel(AuditEntryMapper.toEntity(ordered))
            .changes.keys
            .toList() shouldBe listOf("z", "a")
    }

    @Test
    fun `time with sub-millisecond precision is refused rather than silently rounded`() {
        val fine = entry.copy(at = Instant.parse("2026-11-01T10:00:00.123456789Z"))

        shouldThrow<IllegalArgumentException> { AuditEntryMapper.toEntity(fine) }
    }

    @Test
    fun `an unreadable row fails closed with a message that holds no row content`() {
        val good = AuditEntryMapper.toEntity(entry)
        val unreadable =
            listOf(
                good.copy(changesJson = "{not json SECRET-VALUE"),
                good.copy(changesJson = """{"f":"not an object SECRET-VALUE"}"""),
                good.copy(action = "SECRET-VALUE"),
                good.copy(source = "SECRET-VALUE"),
            )

        for (row in unreadable) {
            val failure = shouldThrow<IllegalStateException> { AuditEntryMapper.toModel(row) }
            failure.message.orEmpty() shouldNotContain "SECRET-VALUE"
        }
    }
}
