package dev.imagh.pft.app.data.transaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.imagh.pft.app.data.common.BaseDao

/**
 * Reads and writes for `transaction_line` and its join table `transaction_line_tag`; inserts and updates
 * of lines come from [BaseDao]. No logic: [RoomTransactionLineRepository] makes the line and its tag rows
 * one unit, and core decides what a line may be.
 */
@Dao
interface TransactionLineDao : BaseDao<TransactionLineEntity> {
    @Query("SELECT * FROM transaction_line WHERE id = :id")
    override suspend fun byId(id: String): TransactionLineEntity?

    /** The lines of [transactionId] that are not tombstoned, in no particular order. */
    @Query("SELECT * FROM transaction_line WHERE transaction_id = :transactionId AND deleted_at IS NULL")
    suspend fun liveOf(transactionId: String): List<TransactionLineEntity>

    /** The tag rows of the given lines, in no particular order. */
    @Query("SELECT * FROM transaction_line_tag WHERE line_id IN (:lineIds)")
    suspend fun tagsOf(lineIds: List<String>): List<TransactionLineTagEntity>

    /** Adds tag rows; fails (and so rolls the surrounding transaction back) on an unknown line or tag. */
    @Insert
    suspend fun insertTags(rows: List<TransactionLineTagEntity>)

    /** Removes every tag row of [lineId]: a line's tags are replaced as a set, never tombstoned. */
    @Query("DELETE FROM transaction_line_tag WHERE line_id = :lineId")
    suspend fun deleteTagsOf(lineId: String)

    /**
     * How many live transactions break the sum invariant (hardcoded, CLAUDE.md §3.8): one that has no live
     * line, or whose live lines do not add up to its amount. Always 0 when only `TransactionService` has
     * written. A tombstoned transaction is not counted (its lines stay as they were), and a tombstoned line
     * does not count towards the sum. The invariant is not a database trigger, so this is how a violation
     * becomes visible (data-model.md).
     */
    @Query(
        "SELECT COUNT(*) FROM ledger_transaction t WHERE t.deleted_at IS NULL AND (" +
            "NOT EXISTS (SELECT 1 FROM transaction_line l WHERE l.transaction_id = t.id AND l.deleted_at IS NULL) " +
            "OR t.amount != (SELECT SUM(l.amount) FROM transaction_line l " +
            "WHERE l.transaction_id = t.id AND l.deleted_at IS NULL))",
    )
    suspend fun breakdownMismatchCount(): Int
}
