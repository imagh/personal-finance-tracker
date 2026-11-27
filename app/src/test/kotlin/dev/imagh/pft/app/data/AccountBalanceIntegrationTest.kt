package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Account balances through the real stack (slice C C3a, decision 4): opening balance + sum of IN - sum of
 * OUT over live transaction amounts (never lines), transfers included, tombstones excluded.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountBalanceIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)

    @After
    fun closeDatabase() = database.close()

    private suspend fun balance(account: dev.imagh.pft.ledger.account.Account) =
        kit.transactionQueries.accountBalance(account.meta.id)

    @Test
    fun `a balance is opening plus income minus expense, the sign following the direction`() =
        runSuspending {
            w.start()
            w.spend(ist(2026, 11, 3), w.item(rs(1_200), w.groceries))
            w.earn(rs(5_000), ist(2026, 11, 5))
            w.spend(ist(2025, 1, 1), w.item(rs(300))) // any month: balances are all-time
            w.spend(ist(2026, 11, 6), w.item(rs(999)), account = w.card)

            balance(w.bank) shouldBe Paise(rs(10_000 - 1_200 + 5_000 - 300))
            balance(w.savings) shouldBe Paise(0)
        }

    @Test
    fun `a credit card goes negative with spend, from an opening that may itself be negative`() =
        runSuspending {
            w.start()
            val owing = kit.accounts.create(AccountDraft("Old card", AccountType.CREDIT_CARD, Paise(-rs(500))))
            w.spend(ist(2026, 11, 6), w.item(rs(3_000)), account = w.card)
            w.spend(ist(2026, 11, 7), w.item(rs(250)), account = owing)

            balance(w.card) shouldBe Paise(-rs(3_000))
            balance(owing) shouldBe Paise(-rs(750))
        }

    @Test
    fun `itemising changes no balance, because it is built from the amount and not the lines`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(2_000), w.groceries))
            val before = kit.transactionQueries.accountBalances()

            kit.transactions.edit(
                shop.transaction.meta.id,
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(2_000)),
                    shop.transaction.occurredAt,
                    w.bank.meta.id,
                    lines =
                        listOf(
                            w.item(rs(1_200), w.groceries).copy(
                                id =
                                    shop.lines
                                        .single()
                                        .meta.id,
                            ),
                            w.item(rs(800), w.travel),
                        ),
                ),
            )

            kit.transactionQueries.accountBalances() shouldBe before
            balance(w.bank) shouldBe Paise(rs(8_000))
        }

    @Test
    fun `a transfer moves money from one account to the other and both legs count`() =
        runSuspending {
            w.start()
            kit.transfers.create(TransferDraft(w.bank.meta.id, w.savings.meta.id, Paise(rs(2_000)), ist(2026, 11, 4)))

            balance(w.bank) shouldBe Paise(rs(8_000))
            balance(w.savings) shouldBe Paise(rs(2_000))
        }

    @Test
    fun `a deleted transaction is not counted and restoring it counts it again`() =
        runSuspending {
            w.start()
            val shop = w.spend(ist(2026, 11, 3), w.item(rs(1_000)))
            kit.transactions.delete(shop.transaction.meta.id)

            balance(w.bank) shouldBe Paise(rs(10_000))

            kit.transactions.restore(shop.transaction.meta.id)
            balance(w.bank) shouldBe Paise(rs(9_000))
        }

    @Test
    fun `every live account has a balance, an archived one included, and a deleted or unknown one is NotFound`() =
        runSuspending {
            w.start()
            w.spend(ist(2026, 11, 3), w.item(rs(100)), account = w.card)
            kit.accounts.archive(w.card.meta.id)
            kit.accounts.delete(w.savings.meta.id)

            kit.transactionQueries.accountBalances() shouldBe
                mapOf(w.bank.meta.id to Paise(rs(10_000)), w.card.meta.id to Paise(-rs(100)))
            balance(w.card) shouldBe Paise(-rs(100))
            shouldThrow<LedgerError.NotFound> { balance(w.savings) }
            shouldThrow<LedgerError.NotFound> { kit.transactionQueries.accountBalance(AccountId("nope")) }
        }

    @Test
    fun `a stored direction this build does not know fails closed, naming the column and not the value`() =
        runSuspending {
            w.start()
            w.spend(ist(2026, 11, 3), w.item(rs(100)))
            database.openHelper.writableDatabase.execSQL("UPDATE ledger_transaction SET direction = 'SIDEWAYS'")

            val failure = shouldThrow<IllegalStateException> { kit.transactionQueries.accountBalances() }

            failure.message shouldBe "A transaction row has an unknown value in column direction"
        }
}
