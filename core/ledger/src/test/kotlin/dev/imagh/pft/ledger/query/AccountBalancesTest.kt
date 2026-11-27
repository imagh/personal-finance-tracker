package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

private val at = Instant.parse("2026-11-01T00:00:00Z")

private fun account(
    id: String,
    opening: Long,
) = Account(
    meta = EntityMeta(AccountId(id), at, at, UserId("u"), DeviceId("d")),
    name = id,
    type = AccountType.BANK,
    openingBalance = Paise(opening),
    maskedIdentifier = null,
    issuer = null,
)

private fun move(
    account: String,
    direction: String,
    total: Long,
) = AccountMovement(AccountId(account), direction, Paise(total))

/** Slice C D5 (decision 4): balance = opening + sum of IN - sum of OUT, from movements, never lines. */
class AccountBalancesTest :
    FunSpec({
        test("a balance is the opening balance plus IN minus OUT, whichever order the movements come in") {
            val balances =
                AccountBalances.of(
                    listOf(account("a", 10_000)),
                    listOf(move("a", "OUT", 1_200), move("a", "IN", 5_000)),
                )

            balances shouldBe mapOf(AccountId("a") to Paise(13_800))
        }

        test("several movements of the same direction add up") {
            val balances =
                AccountBalances.of(
                    listOf(account("a", 0)),
                    listOf(move("a", "OUT", 100), move("a", "OUT", 250), move("a", "IN", 30), move("a", "IN", 20)),
                )

            balances[AccountId("a")] shouldBe Paise(-300)
        }

        test("an account with no movements is at its opening balance, which may be negative") {
            val balances = AccountBalances.of(listOf(account("a", -500), account("b", 0)), emptyList())

            balances shouldBe mapOf(AccountId("a") to Paise(-500), AccountId("b") to Paise(0))
        }

        test("movements of one account never touch another, and a movement for no listed account is ignored") {
            val balances =
                AccountBalances.of(
                    listOf(account("a", 0), account("b", 0)),
                    listOf(move("a", "IN", 70), move("b", "OUT", 5), move("gone", "IN", 999)),
                )

            balances shouldBe mapOf(AccountId("a") to Paise(70), AccountId("b") to Paise(-5))
        }

        test("a direction that is neither IN nor OUT fails closed, naming the column and not the value") {
            val failure =
                shouldThrow<IllegalStateException> {
                    AccountBalances.of(listOf(account("a", 0)), listOf(move("a", "SIDEWAYS", 1)))
                }

            failure.message shouldBe "A transaction row has an unknown value in column direction"
        }

        test("a sum that does not fit in a Long throws ArithmeticException rather than wrapping") {
            shouldThrow<ArithmeticException> {
                AccountBalances.of(listOf(account("a", 1)), listOf(move("a", "IN", Long.MAX_VALUE)))
            }
        }
    })
