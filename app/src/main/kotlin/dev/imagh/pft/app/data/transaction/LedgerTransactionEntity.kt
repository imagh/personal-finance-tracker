package dev.imagh.pft.app.data.transaction

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.imagh.pft.app.data.account.AccountEntity
import dev.imagh.pft.app.data.common.MetaColumns

/**
 * Table `ledger_transaction` (schema v5, slice C plan §4): one row per transaction, the Room twin of
 * core's `LedgerTransaction`. It holds the **payment facts** only. There is no `category_id` and no tag
 * column: classification lives on the transaction's lines (`transaction_line`), so a transaction can be
 * itemised without a second place to keep a category (PRD L-1, L-11).
 *
 * - Money is positive integer paise; the direction says which way it moved. `occurred_at` is epoch
 *   milliseconds UTC like every time column.
 * - `type`, `direction`, `state` and `source` are enum names, validated by [LedgerTransactionMapper] on read.
 * - `account_id` is a foreign key to `account(id)` with no `onDelete` action (rows are tombstoned,
 *   never removed). Its index is the composite `(account_id, occurred_at)`, which also serves the month
 *   list of one account.
 * - There is deliberately no `CHECK (amount > 0)`: Room cannot declare one on an entity, so it would exist
 *   only in migrated databases (plan §2). Core and the mapper hold the rule.
 * - `bank_ref` and `account_fragment` are sensitive: never logged.
 *
 * The meta columns are embedded, and [Entity.primaryKeys] names `id` because Room does not count a
 * key inside an embedded class.
 */
@Entity(
    tableName = "ledger_transaction",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
        ),
    ],
    indices = [
        Index(value = ["occurred_at"]),
        Index(value = ["account_id", "occurred_at"]),
        Index(value = ["transfer_group_id"]),
    ],
)
data class LedgerTransactionEntity(
    @Embedded val meta: MetaColumns,
    /** The `TransactionType` name. */
    @ColumnInfo(name = "type") val type: String,
    /** The `Direction` name. */
    @ColumnInfo(name = "direction") val direction: String,
    /** Positive paise. */
    @ColumnInfo(name = "amount") val amount: Long,
    /** Epoch milliseconds UTC. */
    @ColumnInfo(name = "occurred_at") val occurredAt: Long,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    /** The `TxnState` name. */
    @ColumnInfo(name = "state") val state: String,
    /** The `TxnSource` name. */
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "bank_ref") val bankRef: String?,
    @ColumnInfo(name = "source_message_id") val sourceMessageId: String?,
    @ColumnInfo(name = "account_fragment") val accountFragment: String?,
    @ColumnInfo(name = "transfer_group_id") val transferGroupId: String?,
)
