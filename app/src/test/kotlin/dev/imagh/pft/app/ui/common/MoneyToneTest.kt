package dev.imagh.pft.app.ui.common

import dev.imagh.pft.money.Paise
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** The meaning-to-colour mapping for amounts (plan §3.3, §5.2; owner decisions Q2 and Q3). Pure JVM. */
class MoneyToneTest {
    private val someAmount = Paise(25_000)

    private fun tone(
        semantic: MoneySemantic,
        amount: Paise = someAmount,
        inRow: Boolean = false,
    ) = moneyTone(semantic, amount, inRow)

    @Test
    fun `income is positive in totals and in rows`() {
        assertEquals(MoneyTone.Positive, tone(MoneySemantic.Income, inRow = false))
        assertEquals(MoneyTone.Positive, tone(MoneySemantic.Income, inRow = true))
    }

    @Test
    fun `expense is negative in totals but neutral in list rows (Q2)`() {
        assertEquals(MoneyTone.Negative, tone(MoneySemantic.Expense, inRow = false))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Expense, inRow = true))
    }

    @Test
    fun `a transfer is neutral everywhere`() {
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Transfer, inRow = false))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Transfer, inRow = true))
    }

    @Test
    fun `a positive balance is neutral, a negative balance is negative, zero is neutral (Q3)`() {
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Balance, Paise(1)))
        assertEquals(MoneyTone.Negative, tone(MoneySemantic.Balance, Paise(-1)))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Balance, Paise.ZERO))
    }

    @Test
    fun `a negative balance stays negative in a list row`() {
        assertEquals(MoneyTone.Negative, tone(MoneySemantic.Balance, Paise(-5_000), inRow = true))
    }

    @Test
    fun `a budget is positive under, warning at its threshold and negative over`() {
        assertEquals(MoneyTone.Positive, tone(MoneySemantic.Budget(BudgetState.UNDER)))
        assertEquals(MoneyTone.Warning, tone(MoneySemantic.Budget(BudgetState.AT_THRESHOLD)))
        assertEquals(MoneyTone.Negative, tone(MoneySemantic.Budget(BudgetState.OVER)))
    }

    @Test
    fun `the budget tone does not depend on the row placement or the amount`() {
        BudgetState.entries.forEach { state ->
            val semantic = MoneySemantic.Budget(state)
            assertEquals(tone(semantic, Paise(1), inRow = false), tone(semantic, Paise(9_999_999), inRow = true))
        }
    }
}
