package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryTree
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/**
 * What a month list or a search is narrowed to (slice C D5). An empty set or a null bound means "no
 * restriction" for that dimension.
 *
 * **Semantics.** Values inside one dimension are OR (any of these accounts); dimensions are AND. Account,
 * amount, state, type and date apply to the **transaction**. Category and tag apply to its **lines**: a
 * transaction matches when one live line satisfies the category filter *and* the tag filter together
 * ([matchesLine]); two lines each meeting one of them do not match.
 *
 * Validated when built (a `copy()` is checked too), so a filter that exists is a filter the queries can run.
 *
 * @param from the first day, inclusive, in the zone the query is given; null for no lower bound.
 * @param to the last day, inclusive; null for no upper bound.
 * @param accountIds the accounts a transaction is on.
 * @param categoryIds the categories a line has; a parent also stands for its children ([categoryScope]).
 * @param tagIds the tags a line carries (any of them).
 * @param amountMin the smallest transaction amount, inclusive.
 * @param amountMax the largest transaction amount, inclusive.
 * @param states the verification states a transaction may be in.
 * @param types the transaction types (a transfer leg is [TransactionType.TRANSFER]).
 * @throws LedgerError.InvalidRange if `from` is after `to`, `amountMin` is above `amountMax`, or a date is
 *   outside years 1 to 9999 (so the window's epoch milliseconds cannot overflow).
 */
data class TransactionFilter(
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val accountIds: Set<AccountId> = emptySet(),
    val categoryIds: Set<CategoryId> = emptySet(),
    val tagIds: Set<TagId> = emptySet(),
    val amountMin: Paise? = null,
    val amountMax: Paise? = null,
    val states: Set<TxnState> = emptySet(),
    val types: Set<TransactionType> = emptySet(),
) {
    init {
        if (from != null && to != null && from > to) throw LedgerError.InvalidRange()
        if (amountMin != null && amountMax != null && amountMin > amountMax) throw LedgerError.InvalidRange()
        if (listOfNotNull(from, to).any { it.year !in YEAR_RANGE }) throw LedgerError.InvalidRange()
    }

    /** True if a category or a tag is set: the totals then count only the matching lines, not the whole transaction. */
    val hasLineFilter: Boolean get() = categoryIds.isNotEmpty() || tagIds.isNotEmpty()

    /**
     * [categoryIds] with each parent's children added, from [all] (the live categories). Empty when there is
     * no category filter, which [matchesLine] reads as "any category".
     */
    fun categoryScope(all: List<Category>): Set<CategoryId> = CategoryTree.withChildren(all, categoryIds)

    /**
     * True if [line] satisfies the category filter (its category is in [categoryScope], the result of
     * [categoryScope]) and the tag filter (it carries at least one of [tagIds]). A filter with neither
     * matches every line.
     */
    fun matchesLine(
        line: TransactionLine,
        categoryScope: Set<CategoryId>,
    ): Boolean =
        (categoryIds.isEmpty() || line.categoryId in categoryScope) &&
            (tagIds.isEmpty() || line.tagIds.any { it in tagIds })

    /**
     * The [TimeWindow] this filter and [month] allow, in [zone]: the month is
     * `[start of the month, start of the next month)`, `from` is the start of its day and `to` includes its
     * whole day; where both are given, the overlap. Without a [month] only the dates bound it; with neither
     * it is unbounded.
     */
    fun window(
        zone: ZoneId,
        month: YearMonth? = null,
    ): TimeWindow {
        val starts = listOfNotNull(month?.atDay(1), from).map { it.startMs(zone) }
        val ends = listOfNotNull(month?.plusMonths(1)?.atDay(1), to?.plusDays(1)).map { it.startMs(zone) }
        return TimeWindow(starts.maxOrNull() ?: Long.MIN_VALUE, ends.minOrNull() ?: Long.MAX_VALUE)
    }

    private fun LocalDate.startMs(zone: ZoneId): Long = atStartOfDay(zone).toInstant().toEpochMilli()

    private companion object {
        val YEAR_RANGE = 1..9999
    }
}
