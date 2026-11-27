package dev.imagh.pft.app.data.transaction

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * The reads behind the month list, search and balances (slice C D5). No logic: [TransactionQuerySql] builds
 * the filter queries, and [RoomTransactionQueries] runs them and hands the rows to core.
 *
 * The three `@RawQuery` methods take a query that [TransactionQuerySql] built **with bound arguments only**:
 * the SQL text holds fixed fragments and `?` placeholders, never a value (CLAUDE.md Rule C). They are raw
 * only because the filter's optional parts and list sizes vary, and a fixed `@Query` would need a dozen
 * parameters.
 */
@Dao
interface TransactionQueryDao {
    /** The transactions a [TransactionQuerySql.transactions] query matches, in its order. */
    @RawQuery
    suspend fun transactions(query: SupportSQLiteQuery): List<LedgerTransactionEntity>

    /** The live lines of the transactions a [TransactionQuerySql.lines] query matches; order unspecified. */
    @RawQuery
    suspend fun lines(query: SupportSQLiteQuery): List<TransactionLineEntity>

    /** The tag rows of those lines ([TransactionQuerySql.lineTags]); order unspecified. */
    @RawQuery
    suspend fun lineTags(query: SupportSQLiteQuery): List<TransactionLineTagEntity>

    /**
     * Per account and direction, the paise of its live transactions (the balance input). Amounts, never
     * lines, so itemising changes nothing; transfer legs are included, since they move money between accounts.
     */
    @Query(
        "SELECT account_id AS accountId, direction, SUM(amount) AS total FROM ledger_transaction " +
            "WHERE deleted_at IS NULL GROUP BY account_id, direction",
    )
    suspend fun directionTotals(): List<DirectionTotal>

    /**
     * The earliest and latest `occurred_at` of the live transactions, both null when there are none (the
     * `entryMonthSpan` input). One pass over the live rows, cheap at household scale; no index is added.
     */
    @Query(
        "SELECT MIN(occurred_at) AS earliest, MAX(occurred_at) AS latest FROM ledger_transaction " +
            "WHERE deleted_at IS NULL",
    )
    suspend fun occurredRange(): OccurredRange
}

/** The result of [TransactionQueryDao.occurredRange]: epoch milliseconds, both null when no live row exists. */
data class OccurredRange(
    @ColumnInfo(name = "earliest") val earliest: Long?,
    @ColumnInfo(name = "latest") val latest: Long?,
)

/** One row of [TransactionQueryDao.directionTotals]: [total] paise moving [direction] (an enum name) on [accountId]. */
data class DirectionTotal(
    @ColumnInfo(name = "accountId") val accountId: String,
    @ColumnInfo(name = "direction") val direction: String,
    @ColumnInfo(name = "total") val total: Long,
)
