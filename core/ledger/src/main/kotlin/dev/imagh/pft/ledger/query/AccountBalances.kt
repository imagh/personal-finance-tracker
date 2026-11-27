package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.money.Paise

/**
 * The total amount of one account's live transactions that moved one way: what the store hands core to
 * build a balance from. Amounts of transactions, never of lines.
 *
 * @property accountId the account.
 * @property direction the stored direction name (a [Direction] name); anything else is refused by
 *   [AccountBalances.of], so a damaged row cannot silently count as money in or out.
 * @property total the sum of the amounts, positive.
 */
data class AccountMovement(
    val accountId: AccountId,
    val direction: String,
    val total: Paise,
)

/** The balance rule (slice C D5, decision 4), pure so it is tested without a database. */
object AccountBalances {
    /**
     * Each of [accounts] at its opening balance plus the sum of its IN movements minus the sum of its OUT
     * movements. An account with no movements is at its opening balance; a movement for an account that is
     * not in [accounts] is ignored. Transfer legs are ordinary movements (they move money between accounts).
     *
     * @throws IllegalStateException if a movement's direction is neither IN nor OUT (the message names the
     *   column, never the value).
     * @throws ArithmeticException if a sum does not fit in a `Long` (exact arithmetic, never wrapping).
     */
    fun of(
        accounts: List<Account>,
        movements: List<AccountMovement>,
    ): Map<AccountId, Paise> {
        val byAccount = movements.groupBy { it.accountId }
        return accounts.associate { account ->
            val moved = byAccount[account.meta.id].orEmpty().fold(Paise.ZERO) { sum, m -> sum + signed(m) }
            account.meta.id to account.openingBalance + moved
        }
    }

    private fun signed(movement: AccountMovement): Paise {
        val direction =
            checkNotNull(Direction.entries.firstOrNull { it.name == movement.direction }) {
                "A transaction row has an unknown value in column direction"
            }
        return when (direction) {
            Direction.IN -> movement.total
            Direction.OUT -> Paise.ZERO - movement.total
        }
    }
}
