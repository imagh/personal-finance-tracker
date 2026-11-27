package dev.imagh.pft.app.data.transaction

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.query.TimeWindow
import dev.imagh.pft.ledger.query.TransactionFilter

/**
 * Builds the filter queries of [TransactionQueryDao] (slice C D5).
 *
 * **Every value is a bound argument.** The SQL is assembled from fixed fragments and `?` placeholders; the
 * only thing that varies in its text is how many placeholders an `IN (...)` list has, which comes from a
 * set's size, not its content. Nothing a user chose (an id, an amount, a date) is ever in the string.
 *
 * One [Match] is the filter: live transactions in the window that pass the transaction-level filters
 * (account, amount, state, type), and, with a category or tag filter, that have **one live line** meeting
 * both. The three queries share it, so the transactions, their lines and their tags are always the same
 * set: the lines and tags are read by joining to the same matching sub-query (same predicate, same
 * order, same `LIMIT`), not by an `IN (:ids)` list of transaction ids, so no variable-count limit applies.
 *
 * Order: `occurred_at` newest first, then the transfer group id (or the id) so a transfer's two legs, which
 * share one `occurred_at`, are adjacent, then the id, which makes the order total.
 */
internal object TransactionQuerySql {
    /** `LIMIT` value that SQLite reads as "no limit". */
    const val NO_LIMIT = -1

    private const val ORDER = "t.occurred_at DESC, COALESCE(t.transfer_group_id, t.id), t.id"

    /** The predicate of one filter and its arguments, in placeholder order. */
    class Match(
        val sql: String,
        val args: List<Any>,
    )

    /**
     * The match for [window] and [filter]. [categoryScope] is the filter's categories with each parent's
     * children (`TransactionFilter.categoryScope`); it is used only when the filter has a category.
     */
    fun match(
        window: TimeWindow,
        filter: TransactionFilter,
        categoryScope: Set<CategoryId>,
    ): Match {
        val sql = StringBuilder("t.deleted_at IS NULL AND t.occurred_at >= ? AND t.occurred_at < ?")
        val args = mutableListOf<Any>(window.startMs, window.endMs)

        fun inList(
            column: String,
            values: Collection<String>,
        ) {
            sql
                .append(" AND ")
                .append(column)
                .append(" IN (")
                .append(placeholders(values.size))
                .append(')')
            args.addAll(values)
        }
        if (filter.accountIds.isNotEmpty()) inList("t.account_id", filter.accountIds.ids())
        filter.amountMin?.let {
            sql.append(" AND t.amount >= ?")
            args.add(it.value)
        }
        filter.amountMax?.let {
            sql.append(" AND t.amount <= ?")
            args.add(it.value)
        }
        if (filter.states.isNotEmpty()) inList("t.state", filter.states.map { it.name })
        if (filter.types.isNotEmpty()) inList("t.type", filter.types.map { it.name })
        if (filter.hasLineFilter) {
            // One line must meet the category filter and the tag filter together.
            sql.append(" AND EXISTS (SELECT 1 FROM transaction_line l")
            sql.append(" WHERE l.transaction_id = t.id AND l.deleted_at IS NULL")
            if (filter.categoryIds.isNotEmpty()) inList("l.category_id", categoryScope.ids())
            if (filter.tagIds.isNotEmpty()) {
                sql.append(" AND EXISTS (SELECT 1 FROM transaction_line_tag x WHERE x.line_id = l.id")
                inList("x.tag_id", filter.tagIds.ids())
                sql.append(')')
            }
            sql.append(')')
        }
        return Match(sql.toString(), args)
    }

    /** The matching transactions, in display order, at most [limit] rows ([NO_LIMIT] for all). */
    fun transactions(
        match: Match,
        limit: Int,
    ): SupportSQLiteQuery =
        SimpleSQLiteQuery(
            "SELECT t.* FROM ledger_transaction t WHERE ${match.sql} ORDER BY $ORDER LIMIT ?",
            (match.args + limit).toTypedArray(),
        )

    /** The live lines of the transactions [transactions] returns for the same [match] and [limit]. */
    fun lines(
        match: Match,
        limit: Int,
    ): SupportSQLiteQuery =
        SimpleSQLiteQuery(
            "SELECT l.* FROM transaction_line l JOIN ${matched(match)} ON m.id = l.transaction_id " +
                "WHERE l.deleted_at IS NULL",
            (match.args + limit).toTypedArray(),
        )

    /**
     * The tag rows of the lines of the matching transactions. It does not filter tombstoned lines: their tag
     * rows are never looked up, because [lines] does not return them.
     */
    fun lineTags(
        match: Match,
        limit: Int,
    ): SupportSQLiteQuery =
        SimpleSQLiteQuery(
            "SELECT x.* FROM transaction_line_tag x JOIN transaction_line l ON l.id = x.line_id " +
                "JOIN ${matched(match)} ON m.id = l.transaction_id",
            (match.args + limit).toTypedArray(),
        )

    private fun matched(match: Match) =
        "(SELECT t.id FROM ledger_transaction t WHERE ${match.sql} ORDER BY $ORDER LIMIT ?) m"

    private fun placeholders(count: Int) = List(count) { "?" }.joinToString(",")

    private fun Collection<EntityId>.ids() = map { it.value }
}
