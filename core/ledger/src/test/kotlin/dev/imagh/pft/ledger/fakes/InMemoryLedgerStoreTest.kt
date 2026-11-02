package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

private data class Row(
    val id: String,
    val value: String,
)

private fun entry(id: String) =
    AuditEntry(
        id = AuditEntryId(id),
        entityType = "row",
        entityId = "r-1",
        action = AuditAction.CREATE,
        changes = mapOf("value" to FieldChange(null, "v")),
        userId = UserId("u-1"),
        deviceId = DeviceId("d-1"),
        source = AuditSource.MANUAL,
        at = TestClock().instant(),
    )

class InMemoryLedgerStoreTest :
    FunSpec({
        test("a block that completes keeps its writes and returns its value") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            val result =
                ledger.inTransaction {
                    rows.insert(Row("a", "1"))
                    ledger.auditLog.append(entry("e-1"))
                    "done"
                }
            result shouldBe "done"
            rows.all() shouldBe listOf(Row("a", "1"))
            ledger.auditLog.entries.map { it.id.value } shouldBe listOf("e-1")
        }

        test("a block that throws restores every store, including the audit log, and rethrows") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            rows.insert(Row("kept", "0"))
            ledger.auditLog.append(entry("e-0"))

            val error =
                shouldThrow<IllegalStateException> {
                    ledger.inTransaction {
                        rows.insert(Row("a", "1"))
                        rows.update(Row("kept", "changed"))
                        ledger.auditLog.append(entry("e-1"))
                        error("boom")
                    }
                }

            error.message shouldBe "boom"
            rows.all() shouldBe listOf(Row("kept", "0"))
            ledger.auditLog.entries.map { it.id.value } shouldBe listOf("e-0")
        }

        test("a failure in a nested block rolls back the outer block's earlier writes too") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            shouldThrow<IllegalStateException> {
                ledger.inTransaction {
                    rows.insert(Row("outer", "1"))
                    ledger.inTransaction {
                        rows.insert(Row("inner", "2"))
                        error("inner failed")
                    }
                }
            }
            rows.all().shouldBeEmpty()
        }

        test("an outer block that swallows an inner failure is still rolled back, like Room's nested transaction") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            val result =
                ledger.inTransaction {
                    rows.insert(Row("outer", "1"))
                    try {
                        ledger.inTransaction {
                            rows.insert(Row("inner", "2"))
                            error("inner failed")
                        }
                    } catch (expected: IllegalStateException) {
                        // swallowed: the outer block carries on and returns normally
                    }
                    rows.insert(Row("after", "3"))
                    "returned"
                }
            result shouldBe "returned"
            rows.all().shouldBeEmpty()
        }

        test("the failed mark does not leak into the next transaction") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            ledger.inTransaction {
                runCatching { ledger.inTransaction { error("inner failed") } }
            }
            ledger.inTransaction { rows.insert(Row("b", "2")) }
            rows.all() shouldBe listOf(Row("b", "2"))
        }

        test("nested blocks that succeed are committed together") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            ledger.inTransaction {
                rows.insert(Row("outer", "1"))
                ledger.inTransaction { rows.insert(Row("inner", "2")) }
            }
            rows.all().map { it.id } shouldBe listOf("outer", "inner")
        }

        test("the store is usable again after a rollback") {
            val ledger = InMemoryLedgerStore()
            val rows = ledger.newStore<Row, String> { it.id }
            shouldThrow<IllegalStateException> {
                ledger.inTransaction {
                    rows.insert(Row("a", "1"))
                    error("boom")
                }
            }
            ledger.inTransaction { rows.insert(Row("b", "2")) }
            rows.all() shouldBe listOf(Row("b", "2"))
        }

        test("the entity store behaves like a primary key: no duplicate insert, no update of a missing row") {
            val rows = InMemoryLedgerStore().newStore<Row, String> { it.id }
            rows.insert(Row("a", "1"))
            shouldThrow<IllegalStateException> { rows.insert(Row("a", "other")) }
            shouldThrow<IllegalStateException> { rows.update(Row("missing", "x")) }
            rows.get("a") shouldBe Row("a", "1")
            rows.get("missing") shouldBe null
        }
    })
