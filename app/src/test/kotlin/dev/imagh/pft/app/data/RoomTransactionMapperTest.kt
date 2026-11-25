package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.common.MetaColumns
import dev.imagh.pft.app.data.transaction.LedgerTransactionEntity
import dev.imagh.pft.app.data.transaction.LedgerTransactionMapper
import dev.imagh.pft.app.data.transaction.TransactionLineEntity
import dev.imagh.pft.app.data.transaction.TransactionLineMapper
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnSource
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.Test
import java.time.Instant

/**
 * [LedgerTransactionMapper] and [TransactionLineMapper]: the stored shape of a transaction and its lines.
 * Reading **fails closed** on anything this build does not know (an unknown `type`, `direction`, `state`
 * or `source`) and on a non-positive amount or position, because only the mappers and core write these
 * tables and a stored value outside the rules means corruption or an unsupported future version. Room
 * cannot declare a `CHECK`, so these refusals are the schema's only guard (slice C plan §4). Every message
 * names the column and never the stored value (CLAUDE.md §3.7); the values here are recognisable so a
 * leak would show.
 */
class RoomTransactionMapperTest {
    private val at = Instant.parse("2026-11-01T10:00:00.123Z")
    private val metaColumns =
        MetaColumns("txn-1", at.toEpochMilli(), at.toEpochMilli(), "user-1", "device-1", null, null)

    private val transaction =
        LedgerTransaction(
            meta = EntityMeta(TransactionId("txn-1"), at, at, UserId("user-1"), DeviceId("device-1")),
            type = TransactionType.TRANSFER,
            direction = Direction.IN,
            amount = Paise(123_450),
            occurredAt = Instant.parse("2026-10-31T08:30:00.500Z"),
            accountId = AccountId("acc-1"),
            title = "Chai Point",
            description = "With friends",
            state = TxnState.UNVERIFIED,
            source = TxnSource.RECURRING,
            bankRef = "UTR123456",
            sourceMessageId = "msg-9",
            accountFragment = "4321",
            transferGroupId = "group-1",
        )

    private fun txnRow(
        type: String = "TRANSFER",
        direction: String = "IN",
        amount: Long = 123_450,
        state: String = "UNVERIFIED",
        source: String = "RECURRING",
    ) = LedgerTransactionEntity(
        meta = metaColumns,
        type = type,
        direction = direction,
        amount = amount,
        occurredAt = Instant.parse("2026-10-31T08:30:00.500Z").toEpochMilli(),
        accountId = "acc-1",
        title = "Chai Point",
        description = "With friends",
        state = state,
        source = source,
        bankRef = "UTR123456",
        sourceMessageId = "msg-9",
        accountFragment = "4321",
        transferGroupId = "group-1",
    )

    private fun failure(read: () -> Unit): String = shouldThrow<IllegalStateException> { read() }.message.orEmpty()

    // Transactions ------------------------------------------------------------------------------

    @Test
    fun `a transaction maps to a row with the documented columns and back`() {
        LedgerTransactionMapper.toRow(transaction) shouldBe txnRow()
        LedgerTransactionMapper.toModel(txnRow()) shouldBe transaction
    }

    @Test
    fun `optional columns map to and from null`() {
        val bare =
            transaction.copy(
                title = null,
                description = null,
                bankRef = null,
                sourceMessageId = null,
                accountFragment = null,
                transferGroupId = null,
            )

        val stored = LedgerTransactionMapper.toRow(bare)

        listOf(
            stored.title,
            stored.description,
            stored.bankRef,
            stored.sourceMessageId,
            stored.accountFragment,
            stored.transferGroupId,
        ).all { it == null } shouldBe true
        LedgerTransactionMapper.toModel(stored) shouldBe bare
    }

    @Test
    fun `every type, direction, state and source this build knows round-trips`() {
        for (type in TransactionType.entries) {
            LedgerTransactionMapper.toModel(txnRow(type = type.name)).type shouldBe type
        }
        for (direction in Direction.entries) {
            LedgerTransactionMapper.toModel(txnRow(direction = direction.name)).direction shouldBe direction
        }
        for (state in TxnState.entries) {
            LedgerTransactionMapper.toModel(txnRow(state = state.name)).state shouldBe state
        }
        for (source in TxnSource.entries) {
            LedgerTransactionMapper.toModel(txnRow(source = source.name)).source shouldBe source
        }
    }

    @Test
    fun `an unknown type fails closed naming the column, not the value`() {
        val message = failure { LedgerTransactionMapper.toModel(txnRow(type = "LOAN_SECRET")) }

        message shouldContain "column type"
        message shouldNotContain "LOAN_SECRET"
    }

    @Test
    fun `an unknown direction fails closed naming the column, not the value`() {
        val message = failure { LedgerTransactionMapper.toModel(txnRow(direction = "SIDEWAYS_SECRET")) }

        message shouldContain "column direction"
        message shouldNotContain "SIDEWAYS_SECRET"
    }

    @Test
    fun `an unknown state fails closed naming the column, not the value`() {
        val message = failure { LedgerTransactionMapper.toModel(txnRow(state = "PENDING_SECRET")) }

        message shouldContain "column state"
        message shouldNotContain "PENDING_SECRET"
    }

    @Test
    fun `an unknown source fails closed naming the column, not the value`() {
        val message = failure { LedgerTransactionMapper.toModel(txnRow(source = "CARRIER_PIGEON_SECRET")) }

        message shouldContain "column source"
        message shouldNotContain "CARRIER_PIGEON_SECRET"
    }

    @Test
    fun `a stored amount of zero or less fails closed naming the column, not the value`() {
        for (stored in listOf(0L, -1L, -987_654L)) {
            val message = failure { LedgerTransactionMapper.toModel(txnRow(amount = stored)) }

            message shouldContain "column amount"
            message shouldNotContain stored.toString()
        }
    }

    @Test
    fun `the smallest positive amount is accepted`() {
        LedgerTransactionMapper.toModel(txnRow(amount = 1)).amount shouldBe Paise(1)
    }

    // Lines -------------------------------------------------------------------------------------

    private val line =
        TransactionLine(
            meta = EntityMeta(TransactionLineId("line-1"), at, at, UserId("user-1"), DeviceId("device-1")),
            transactionId = TransactionId("txn-1"),
            position = 2,
            amount = Paise(50_000),
            categoryId = CategoryId("cat-1"),
            note = "Snacks",
            tagIds = setOf(TagId("tag-b"), TagId("tag-a")),
        )

    private fun lineRow(
        position: Int = 2,
        amount: Long = 50_000,
    ) = TransactionLineEntity(
        meta = metaColumns.copy(id = "line-1"),
        transactionId = "txn-1",
        position = position,
        amount = amount,
        categoryId = "cat-1",
        note = "Snacks",
    )

    @Test
    fun `a line maps to a row without its tags, and back with the tags it is given`() {
        TransactionLineMapper.toRow(line) shouldBe lineRow()
        TransactionLineMapper.toModel(lineRow(), line.tagIds) shouldBe line
    }

    @Test
    fun `a line without a category or note maps to and from null`() {
        val bare = line.copy(categoryId = null, note = null, tagIds = emptySet())

        val stored = TransactionLineMapper.toRow(bare)

        stored.categoryId shouldBe null
        stored.note shouldBe null
        TransactionLineMapper.toModel(stored, emptySet()) shouldBe bare
    }

    @Test
    fun `a stored line amount of zero or less fails closed naming the column, not the value`() {
        for (stored in listOf(0L, -1L, -987_654L)) {
            val message = failure { TransactionLineMapper.toModel(lineRow(amount = stored), emptySet()) }

            message shouldContain "column amount"
            message shouldNotContain stored.toString()
        }
    }

    @Test
    fun `a stored line position of zero or less fails closed naming the column, not the value`() {
        for (stored in listOf(0, -1, -4242)) {
            val message = failure { TransactionLineMapper.toModel(lineRow(position = stored), emptySet()) }

            message shouldContain "column position"
            message shouldNotContain stored.toString()
        }
    }

    @Test
    fun `position 1 and amount 1 are accepted`() {
        TransactionLineMapper.toModel(lineRow(position = 1, amount = 1), emptySet()).position shouldBe 1
    }
}
