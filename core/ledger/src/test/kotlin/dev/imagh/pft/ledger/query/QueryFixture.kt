package dev.imagh.pft.ledger.query

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
import java.time.Instant

private val created = Instant.parse("2026-11-01T00:00:00Z")

/** A transaction with just the fields a read cares about; the id is the only thing a test must give. */
internal fun tx(
    id: String,
    type: TransactionType = TransactionType.EXPENSE,
    direction: Direction = if (type == TransactionType.INCOME) Direction.IN else Direction.OUT,
    amount: Long = 100_00,
    occurredAt: Instant = Instant.parse("2026-11-10T06:00:00Z"),
    account: String = "a-1",
    group: String? = null,
) = LedgerTransaction(
    meta = EntityMeta(TransactionId(id), created, created, UserId("u"), DeviceId("d")),
    type = type,
    direction = direction,
    amount = Paise(amount),
    occurredAt = occurredAt,
    accountId = AccountId(account),
    title = null,
    description = null,
    state = TxnState.VERIFIED,
    source = TxnSource.MANUAL,
    bankRef = null,
    sourceMessageId = null,
    accountFragment = null,
    transferGroupId = group,
)

/** A line of [transaction]; [id] defaults to a name that sorts the way [position] does. */
internal fun line(
    transaction: String,
    position: Int = 1,
    amount: Long = 100_00,
    category: String? = null,
    tags: Set<String> = emptySet(),
    id: String = "$transaction-l$position",
) = TransactionLine(
    meta = EntityMeta(TransactionLineId(id), created, created, UserId("u"), DeviceId("d")),
    transactionId = TransactionId(transaction),
    position = position,
    amount = Paise(amount),
    categoryId = category?.let(::CategoryId),
    note = null,
    tagIds = tags.map(::TagId).toSet(),
)
