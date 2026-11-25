package dev.imagh.pft.app.data.transaction

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.imagh.pft.app.data.category.CategoryEntity
import dev.imagh.pft.app.data.common.MetaColumns

/**
 * Table `transaction_line` (schema v5, slice C plan §4): one row per line (item) of a transaction, the
 * Room twin of core's `TransactionLine` **minus its tags**, which live in [TransactionLineTagEntity].
 *
 * - `transaction_id` is a foreign key to `ledger_transaction(id)` and `category_id` one to `category(id)`;
 *   neither has an `onDelete` action (rows are tombstoned, never removed).
 * - `position` is the display order from 1 and `amount` is positive paise. There is deliberately no
 *   `CHECK` and no unique index on `(transaction_id, position)`: Room cannot declare a `CHECK` or a partial
 *   index on an entity, so they would exist only in migrated databases (plan §2). Core and
 *   [TransactionLineMapper] hold the rules.
 * - `archived_at` is never set for a line; the meta column exists for uniformity.
 *
 * The meta columns are embedded, and [Entity.primaryKeys] names `id` because Room does not count a
 * key inside an embedded class.
 */
@Entity(
    tableName = "transaction_line",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = LedgerTransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
        ),
    ],
    indices = [
        Index(value = ["transaction_id"]),
        Index(value = ["category_id"]),
    ],
)
data class TransactionLineEntity(
    @Embedded val meta: MetaColumns,
    @ColumnInfo(name = "transaction_id") val transactionId: String,
    /** From 1. */
    @ColumnInfo(name = "position") val position: Int,
    /** Positive paise. */
    @ColumnInfo(name = "amount") val amount: Long,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    @ColumnInfo(name = "note") val note: String?,
)
