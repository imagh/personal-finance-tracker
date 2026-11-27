package dev.imagh.pft.app.data.transaction

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.query.SearchText
import dev.imagh.pft.ledger.query.TimeWindow
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.query.TransactionSort

/**
 * Builds the filter queries of [TransactionQueryDao] (slice C D5).
 *
 * **Every value is a bound argument.** The SQL is assembled from fixed fragments and `?` placeholders. Its
 * text varies only in how many placeholders an `IN (...)` list has (from a set's size, not its content) and
 * in which compile-time `ORDER BY` fragment [orderBy] picks for the [TransactionSort] enum. No caller value
 * (an id, an amount, a date, a sort, typed search text) is ever in the string.
 *
 * One [Match] is the filter: live transactions in the window that pass the transaction-level filters
 * (account, amount, state, type, creator), that match each text filter, and, with a category or tag filter, that
 * have **one live line** meeting both. The three queries share it, so the transactions, their lines and their
 * tags are always the same set: the lines and tags are read by joining to the same matching sub-query (same
 * predicate, same order, same `LIMIT`), not by an `IN (:ids)` list of transaction ids, so no variable-count
 * limit applies.
 *
 * Order: the [TransactionSort] key and direction ([orderBy]; the default is `occurred_at` newest first), then
 * the transfer group id (or the id) so a transfer's two legs are adjacent, then the id, which makes the order
 * total. The lines and tags sub-queries use the same order and limit as the main match.
 */
internal object TransactionQuerySql {
    /** `LIMIT` value that SQLite reads as "no limit". */
    const val NO_LIMIT = -1

    /**
     * What every order ends with, whatever the key and direction (ascending): the transfer group id (the
     * row's own id for a plain row), so a transfer's two legs stay adjacent, then the id, so the order is
     * total.
     */
    private const val TIE_BREAKS = "COALESCE(t.transfer_group_id, t.id), t.id"

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
        if (filter.createdBy.isNotEmpty()) inList("t.created_by_user_id", filter.createdBy.ids())
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
        filter.text?.let { addText(sql, args, it, TextScope.EVERYTHING) }
        filter.titleText?.let { addText(sql, args, it, TextScope.TITLE) }
        filter.noteText?.let { addText(sql, args, it, TextScope.NOTES) }
        return Match(sql.toString(), args)
    }

    /**
     * Where one text filter looks. Each scope is a fixed SQL fragment: the column a `MATCH` is restricted to
     * is chosen here, never from what was typed (CLAUDE.md Rule C).
     */
    private enum class TextScope(
        /** The `MATCH` over the transaction index, or null when this scope does not read it. */
        val onTransaction: String?,
        /** True when a live line's note may also match. */
        val onLines: Boolean,
    ) {
        /** Title and description together, or a line's note. */
        EVERYTHING("transaction_fts MATCH ?", true),

        /** The title only. */
        TITLE("title MATCH ?", false),

        /** The description, or a line's note. */
        NOTES("description MATCH ?", true),
    }

    /**
     * ANDs one text filter onto [sql], **one group per word**: every word must match somewhere in the scope, not
     * necessarily next to the others or in the same column or item. A group is the word's `MATCH` on the
     * transaction index, OR (where the scope reads lines) on the note of any one live line. The typed [raw] goes
     * through `SearchText.toMatchTerms` and each term is bound as a parameter, once per index it is matched
     * against. The FTS index joins to its content row on `rowid`, and the live-row checks stay on the content
     * tables: the base predicate already requires a live transaction, and a line counts only while its own
     * `deleted_at` is null.
     *
     * **Fail closed:** text with no word in it (blank, punctuation only) adds `0`, a condition that is never
     * true, so the filter matches nothing. It must never be read as "no text filter" (null means that).
     */
    private fun addText(
        sql: StringBuilder,
        args: MutableList<Any>,
        raw: String,
        scope: TextScope,
    ) {
        val terms = SearchText.toMatchTerms(raw)
        if (terms.isEmpty()) {
            sql.append(" AND 0")
            return
        }
        for (term in terms) {
            val parts = mutableListOf<String>()
            scope.onTransaction?.let {
                parts += "t.rowid IN (SELECT rowid FROM transaction_fts WHERE $it)"
                args.add(term)
            }
            if (scope.onLines) {
                parts +=
                    "EXISTS (SELECT 1 FROM transaction_line n WHERE n.transaction_id = t.id AND n.deleted_at IS NULL " +
                    "AND n.rowid IN (SELECT rowid FROM transaction_line_fts WHERE transaction_line_fts MATCH ?))"
                args.add(term)
            }
            sql.append(" AND (").append(parts.joinToString(" OR ")).append(')')
        }
    }

    /** The matching transactions, in display order, at most [limit] rows ([NO_LIMIT] for all). */
    fun transactions(
        match: Match,
        sort: TransactionSort,
        limit: Int,
    ): SupportSQLiteQuery =
        SimpleSQLiteQuery(
            "SELECT t.* FROM ledger_transaction t WHERE ${match.sql} ORDER BY ${orderBy(sort)} LIMIT ?",
            (match.args + limit).toTypedArray(),
        )

    /** The live lines of the transactions [transactions] returns for the same [match] and [limit]. */
    fun lines(
        match: Match,
        sort: TransactionSort,
        limit: Int,
    ): SupportSQLiteQuery =
        SimpleSQLiteQuery(
            "SELECT l.* FROM transaction_line l JOIN ${matched(match, sort)} ON m.id = l.transaction_id " +
                "WHERE l.deleted_at IS NULL",
            (match.args + limit).toTypedArray(),
        )

    /**
     * The tag rows of the lines of the matching transactions. It does not filter tombstoned lines: their tag
     * rows are never looked up, because [lines] does not return them.
     */
    fun lineTags(
        match: Match,
        sort: TransactionSort,
        limit: Int,
    ): SupportSQLiteQuery =
        SimpleSQLiteQuery(
            "SELECT x.* FROM transaction_line_tag x JOIN transaction_line l ON l.id = x.line_id " +
                "JOIN ${matched(match, sort)} ON m.id = l.transaction_id",
            (match.args + limit).toTypedArray(),
        )

    private fun matched(
        match: Match,
        sort: TransactionSort,
    ) = "(SELECT t.id FROM ledger_transaction t WHERE ${match.sql} ORDER BY ${orderBy(sort)} LIMIT ?) m"

    /**
     * The `ORDER BY` of [sort]: a constant fragment chosen by an exhaustive `when` over the key and the
     * direction, then [TIE_BREAKS]. **Nothing from the caller is ever spliced in**: every fragment is a
     * compile-time string picked by an enum, so a sort cannot inject SQL (CLAUDE.md Rule C). A bound
     * `CASE :key` would not do: it cannot flip direction and would mix text and integer keys.
     *
     * - `CREATED` and `UPDATED` rank a transfer by **one value for both legs, the same in both directions**:
     *   `CREATED` by the group's `MIN` (the pair's first write), `UPDATED` by its `MAX` (the pair's last
     *   change). The legs are written by separate clock reads and an edit of one side only moves one leg's
     *   `updated_at`, so their own values can differ, even by days, and a per-row order could put other rows
     *   between them and cut the pair at the search's fetch edge. One value per pair also keeps ascending the
     *   exact reverse of descending (a `MAX` for one direction and a `MIN` for the other would not).
     *   `occurred_at`, `amount` and `title` are written from one draft, so both legs always hold the same
     *   value and need no such step.
     * - `TITLE` ignores ASCII case (SQLite's `NOCASE`) and puts rows without a title last in both directions
     *   (ascending needs `IS NULL` for that; descending has it already, as SQLite's NULL is the smallest value).
     */
    private fun orderBy(sort: TransactionSort): String {
        val key =
            when (sort.key) {
                TransactionSort.Key.OCCURRED -> if (sort.descending) "t.occurred_at DESC" else "t.occurred_at ASC"
                TransactionSort.Key.AMOUNT -> if (sort.descending) "t.amount DESC" else "t.amount ASC"
                TransactionSort.Key.CREATED ->
                    if (sort.descending) {
                        "${pairEdge("created_at", "MIN")} DESC"
                    } else {
                        "${pairEdge("created_at", "MIN")} ASC"
                    }
                TransactionSort.Key.UPDATED ->
                    if (sort.descending) {
                        "${pairEdge("updated_at", "MAX")} DESC"
                    } else {
                        "${pairEdge("updated_at", "MAX")} ASC"
                    }
                TransactionSort.Key.TITLE ->
                    if (sort.descending) {
                        // SQLite sorts NULL as the smallest value, so a descending order already ends with it.
                        "t.title COLLATE NOCASE DESC"
                    } else {
                        "t.title IS NULL, t.title COLLATE NOCASE ASC"
                    }
            }
        return "$key, $TIE_BREAKS"
    }

    /**
     * A row's [column] value, or for a transfer leg the [aggregate] (`MAX` or `MIN`) of it over the whole
     * group. Both arguments are constants chosen above. A plain row has no group, so the sub-select finds
     * nothing and its own value is used.
     */
    private fun pairEdge(
        column: String,
        aggregate: String,
    ) = "COALESCE((SELECT $aggregate(g.$column) FROM ledger_transaction g " +
        "WHERE g.transfer_group_id = t.transfer_group_id), t.$column)"

    private fun placeholders(count: Int) = List(count) { "?" }.joinToString(",")

    private fun Collection<EntityId>.ids() = map { it.value }
}
