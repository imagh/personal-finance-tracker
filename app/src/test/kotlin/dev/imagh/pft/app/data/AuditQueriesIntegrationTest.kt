package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Duration
import java.time.Instant

/**
 * `AuditQueries.forTransaction` through the real stack (slice C C3a): a transaction's own entries and those
 * of all its lines, removed lines included, oldest first with a total order, and nothing from elsewhere.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AuditQueriesIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)

    @After
    fun closeDatabase() = database.close()

    private suspend fun history(id: String) = kit.auditQueries.forTransaction(TransactionId(id))

    private suspend fun entry(
        id: String,
        type: String,
        entityId: String,
        at: Instant,
    ) {
        val me = kit.identity.requireIdentity()
        kit.auditLog.append(
            AuditEntry(
                AuditEntryId(id),
                type,
                entityId,
                AuditAction.UPDATE,
                emptyMap(),
                me.user.id,
                me.device.id,
                AuditSource.MANUAL,
                at,
            ),
        )
    }

    @Test
    fun `returns the transaction and all its lines' entries, removed lines included, oldest first`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(1_200), w.groceries), w.item(rs(800), w.travel))
            val removed = shop.lines.single { it.categoryId == w.travel.meta.id }
            val kept = shop.lines.single { it.categoryId == w.groceries.meta.id }
            kit.clock.advance(Duration.ofMinutes(1))
            kit.transactions.edit(
                shop.transaction.meta.id,
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(1_200)),
                    shop.transaction.occurredAt,
                    w.bank.meta.id,
                    lines = listOf(w.item(rs(1_200), w.groceries).copy(id = kept.meta.id)),
                ),
            )
            // Another transaction, with its own lines, whose entries must not appear.
            val other = w.spend(ist(2026, 11, 4), w.item(rs(10)))

            val trail = history(shop.id())

            // Five entries and no more: the creates (before the minute passed), then the edit's two.
            trail.size shouldBe 5
            trail.take(3).map { it.entityType to it.action } shouldContainExactlyInAnyOrder
                listOf(
                    "ledger_transaction" to AuditAction.CREATE,
                    "transaction_line" to AuditAction.CREATE,
                    "transaction_line" to AuditAction.CREATE,
                )
            trail.drop(3).map { Triple(it.entityType, it.action, it.entityId) } shouldContainExactlyInAnyOrder
                listOf(
                    Triple("ledger_transaction", AuditAction.UPDATE, shop.id()),
                    Triple("transaction_line", AuditAction.DELETE, removed.meta.id.value),
                )
            trail.take(3).all { it.at < trail.last().at } shouldBe true
            // Nothing of the other transaction or its line.
            trail.none {
                it.entityId == other.id() ||
                    it.entityId ==
                    other.lines
                        .single()
                        .meta.id.value
            } shouldBe true
        }

    @Test
    fun `a time tie is broken by the entry id, whatever order the rows were stored in`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(100)))
            val at = Instant.parse("2030-01-01T00:00:00Z")
            entry("zz-late-id", "ledger_transaction", shop.id(), at)
            entry("aa-early-id", "ledger_transaction", shop.id(), at)
            entry("mm-older-time", "ledger_transaction", shop.id(), at.minusSeconds(1))

            history(shop.id()).takeLast(3).map { it.id.value } shouldContainExactly
                listOf("mm-older-time", "aa-early-id", "zz-late-id")
        }

    @Test
    fun `an entry about another kind of record that happens to share the id is not returned`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(100)))
            entry("x-category", "category", shop.id(), Instant.parse("2030-01-01T00:00:00Z"))
            entry("x-line-as-txn", "transaction_line", shop.id(), Instant.parse("2030-01-01T00:00:00Z"))
            val lineId =
                shop.lines
                    .single()
                    .meta.id.value
            entry("x-category-as-line", "category", lineId, Instant.parse("2030-01-01T00:00:00Z"))
            entry("x-txn-as-line", "ledger_transaction", lineId, Instant.parse("2030-01-01T00:00:00Z"))

            history(shop.id()).map { it.id.value }.any { it.startsWith("x-") } shouldBe false
        }

    @Test
    fun `an unknown transaction has an empty history`() =
        runSuspending {
            w.start()

            history("no-such-transaction") shouldContainExactly emptyList()
        }
}
