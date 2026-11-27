package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.money.Paise
import java.time.YearMonth
import java.time.ZoneId

/**
 * The read side of transactions (slice C D5): the persistence boundary for the month list, search and
 * account balances. Implemented with Room in `app`; every method reads **live rows only** (a tombstoned
 * transaction or line is never read) and returns a snapshot, not a stream.
 */
interface TransactionQueries {
    /**
     * The entries of [month] in [zone] that match [filter], newest first (ties in a fixed order, a
     * transfer's two legs together), with the totals over them. A month is never capped. The filter's
     * `from` and `to` narrow the month further.
     *
     * @throws ArithmeticException if [month] is so extreme that its start does not fit epoch milliseconds
     *   (`java.time.DateTimeException` at the very last month). A filter's own dates are bounded to years 1 to
     *   9999 and cannot.
     * @throws IllegalStateException if a stored row is not one this build understands (fail closed).
     */
    suspend fun month(
        month: YearMonth,
        zone: ZoneId,
        filter: TransactionFilter = TransactionFilter(),
    ): MonthPage

    /**
     * The entries that match [filter] in any month, newest first, at most
     * [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_SEARCH_RESULTS] of them: the cap counts entries after
     * a transfer's legs are folded and never cuts a pair. [SearchPage.truncated] is true only when more matched.
     * [zone] reads the filter's `from` and `to` dates.
     */
    suspend fun search(
        filter: TransactionFilter,
        zone: ZoneId,
    ): SearchPage

    /**
     * The balance of every live account (archived included): its opening balance plus the sum of its live
     * transactions' amounts that came IN minus those that went OUT, transfer legs included. Built from
     * transaction amounts, never lines. An account with no transactions is at its opening balance.
     *
     * @throws ArithmeticException if a balance does not fit in a `Long` (exact arithmetic, never wrapping).
     * @throws IllegalStateException if a stored direction is not one this build knows (fail closed).
     */
    suspend fun accountBalances(): Map<AccountId, Paise>

    /**
     * The balance of account [id], as [accountBalances] defines it.
     *
     * @throws dev.imagh.pft.ledger.validation.LedgerError.NotFound if there is no live account [id].
     * @throws ArithmeticException if the balance does not fit in a `Long`.
     * @throws IllegalStateException if a stored direction is not one this build knows (fail closed).
     */
    suspend fun accountBalance(id: AccountId): Paise
}
