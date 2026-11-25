package dev.imagh.pft.app.data.transaction

import dev.imagh.pft.app.data.common.EntityMapper
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.data.common.MetaMapper
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnSource
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * Maps a core [LedgerTransaction] to its [LedgerTransactionEntity] row and back.
 *
 * Reading **fails closed**: an unknown `type`, `direction`, `state` or `source`, or an amount that is not
 * positive, is corruption or data from an unsupported future version, and the schema has no `CHECK` to
 * refuse it (Room cannot declare one, slice C plan §4). Guessing a value would show wrong money or hide a
 * transaction from review. The messages name the column only, never the stored value (CLAUDE.md §3.7).
 */
object LedgerTransactionMapper : EntityMapper<LedgerTransaction, LedgerTransactionEntity> {
    override fun toRow(model: LedgerTransaction): LedgerTransactionEntity =
        LedgerTransactionEntity(
            meta = MetaMapper.toColumns(model.meta),
            type = model.type.name,
            direction = model.direction.name,
            amount = model.amount.value,
            occurredAt = EpochMillis.of(model.occurredAt),
            accountId = model.accountId.value,
            title = model.title,
            description = model.description,
            state = model.state.name,
            source = model.source.name,
            bankRef = model.bankRef,
            sourceMessageId = model.sourceMessageId,
            accountFragment = model.accountFragment,
            transferGroupId = model.transferGroupId,
        )

    /**
     * The transaction stored in [row].
     *
     * @throws IllegalStateException if the type, direction, state or source is not one this build knows,
     *   or the amount is not positive.
     */
    override fun toModel(row: LedgerTransactionEntity): LedgerTransaction {
        val type = TransactionType.entries.firstOrNull { it.name == row.type }
        val direction = Direction.entries.firstOrNull { it.name == row.direction }
        val state = TxnState.entries.firstOrNull { it.name == row.state }
        val source = TxnSource.entries.firstOrNull { it.name == row.source }
        check(row.amount > 0) { "A transaction row has a non-positive value in column amount" }
        return LedgerTransaction(
            meta = MetaMapper.toMeta(row.meta, ::TransactionId),
            type = checkNotNull(type) { unknown("type") },
            direction = checkNotNull(direction) { unknown("direction") },
            amount = Paise(row.amount),
            occurredAt = Instant.ofEpochMilli(row.occurredAt),
            accountId = AccountId(row.accountId),
            title = row.title,
            description = row.description,
            state = checkNotNull(state) { unknown("state") },
            source = checkNotNull(source) { unknown("source") },
            bankRef = row.bankRef,
            sourceMessageId = row.sourceMessageId,
            accountFragment = row.accountFragment,
            transferGroupId = row.transferGroupId,
        )
    }

    private fun unknown(column: String) = "A transaction row has an unknown value in column $column"
}
