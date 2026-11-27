package dev.imagh.pft.app.data.transaction

import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.category.CategoryRepository
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.query.AccountBalances
import dev.imagh.pft.ledger.query.AccountMovement
import dev.imagh.pft.ledger.query.LedgerEntries
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.MonthPage
import dev.imagh.pft.ledger.query.MonthSpan
import dev.imagh.pft.ledger.query.RecentEntries
import dev.imagh.pft.ledger.query.SearchPage
import dev.imagh.pft.ledger.query.TimeWindow
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.query.TransactionQueries
import dev.imagh.pft.ledger.query.TransactionSort
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import java.time.YearMonth
import java.time.ZoneId

/**
 * The Room implementation of core's [TransactionQueries] (slice C D5): it **fetches** rows with
 * [TransactionQuerySql]'s queries and hands them to core, which holds every rule: the window, the category
 * scope, the fold, the totals, the search cap ([SearchPage.capped]) and the balances ([AccountBalances]).
 * It only chooses how many rows to fetch ([SearchPage.FETCH_ROWS]).
 *
 * Each read runs in one [runner] block, so the transactions, their lines and their tags come from one
 * snapshot. Tombstoned transactions and lines are never read.
 */
class RoomTransactionQueries(
    private val dao: TransactionQueryDao,
    private val accounts: AccountRepository,
    private val categories: CategoryRepository,
    private val runner: TransactionRunner,
) : TransactionQueries {
    override suspend fun month(
        month: YearMonth,
        zone: ZoneId,
        filter: TransactionFilter,
        sort: TransactionSort,
    ): MonthPage =
        runner.inTransaction {
            val scope = filter.categoryScope(categories.allLive())
            val entries = read(filter.window(zone, month), filter, scope, sort, TransactionQuerySql.NO_LIMIT)
            MonthPage(entries, Totals.of(entries, filter, scope))
        }

    override suspend fun search(
        filter: TransactionFilter,
        zone: ZoneId,
        sort: TransactionSort,
    ): SearchPage =
        runner.inTransaction {
            val scope = filter.categoryScope(categories.allLive())
            val entries = read(filter.window(zone), filter, scope, sort, SearchPage.FETCH_ROWS)
            SearchPage.capped(entries)
        }

    override suspend fun recent(limit: Int): List<LedgerEntry> {
        val rows = RecentEntries.fetchRows(limit)
        return runner.inTransaction {
            val entries = read(TimeWindow.ALL, TransactionFilter(), emptySet(), TransactionSort.DEFAULT, rows)
            RecentEntries.cut(entries, limit)
        }
    }

    override suspend fun entryMonthSpan(zone: ZoneId): MonthSpan? =
        runner.inTransaction {
            val range = dao.occurredRange()
            val first = range.earliest
            val last = range.latest
            if (first == null || last == null) null else MonthSpan.ofInstants(first, last, zone)
        }

    /**
     * The sums of live transaction amounts per account and direction, with every live account (archived
     * included), handed to [AccountBalances]: no balance arithmetic happens here.
     */
    override suspend fun accountBalances(): Map<AccountId, Paise> =
        runner.inTransaction {
            val movements =
                dao.directionTotals().map {
                    AccountMovement(
                        AccountId(it.accountId),
                        it.direction,
                        Paise(it.total),
                    )
                }
            AccountBalances.of(accounts.allLive(), movements)
        }

    override suspend fun accountBalance(id: AccountId): Paise =
        accountBalances()[id] ?: throw LedgerError.NotFound("account")

    private suspend fun read(
        window: TimeWindow,
        filter: TransactionFilter,
        scope: Set<CategoryId>,
        sort: TransactionSort,
        limit: Int,
    ): List<LedgerEntry> {
        val match = TransactionQuerySql.match(window, filter, scope)
        val transactions =
            dao.transactions(TransactionQuerySql.transactions(match, sort, limit)).map(LedgerTransactionMapper::toModel)
        val tagsOfLine =
            dao.lineTags(TransactionQuerySql.lineTags(match, sort, limit)).groupBy({ it.lineId }, { TagId(it.tagId) })
        val linesOf =
            dao
                .lines(TransactionQuerySql.lines(match, sort, limit))
                .map { TransactionLineMapper.toModel(it, tagsOfLine[it.meta.id].orEmpty().toSet()) }
                .groupBy { it.transactionId }
        return LedgerEntries.fold(transactions, linesOf, foldTransfers = filter.accountIds.isEmpty())
    }
}
