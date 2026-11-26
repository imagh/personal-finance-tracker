package dev.imagh.pft.app.ui.common

import dev.imagh.pft.money.Paise
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * The meaning-to-colour mapping for amounts (plan §3.3, §5.2; Cobalt plan D5: an expense is neutral
 * everywhere, a net figure is always neutral, red is for a negative balance and an over-budget figure).
 * Pure JVM.
 */
class MoneyToneTest {
    private val someAmount = Paise(25_000)

    private fun tone(
        semantic: MoneySemantic,
        amount: Paise = someAmount,
    ) = moneyTone(semantic, amount)

    @Test
    fun `income is positive whatever the amount`() {
        assertEquals(MoneyTone.Positive, tone(MoneySemantic.Income))
        assertEquals(MoneyTone.Positive, tone(MoneySemantic.Income, Paise.MAX_AMOUNT))
    }

    @Test
    fun `an expense is neutral whatever the amount, so a total or hero is never red`() {
        listOf(Paise.ZERO, Paise(1), someAmount, Paise.MAX_AMOUNT).forEach { amount ->
            assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Expense, amount), "$amount")
        }
    }

    @Test
    fun `a net figure is neutral when negative, zero and positive`() {
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Net, Paise(-1)))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Net, Paise(-5_000_000)))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Net, Paise.ZERO))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Net, Paise(1)))
    }

    @Test
    fun `a transfer is neutral`() {
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Transfer))
    }

    @Test
    fun `a positive balance is neutral, a negative balance is negative, zero is neutral (Q3)`() {
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Balance, Paise(1)))
        assertEquals(MoneyTone.Negative, tone(MoneySemantic.Balance, Paise(-1)))
        assertEquals(MoneyTone.Neutral, tone(MoneySemantic.Balance, Paise.ZERO))
    }

    @Test
    fun `a budget is positive under, warning at its threshold and negative over`() {
        assertEquals(MoneyTone.Positive, tone(MoneySemantic.Budget(BudgetState.UNDER)))
        assertEquals(MoneyTone.Warning, tone(MoneySemantic.Budget(BudgetState.AT_THRESHOLD)))
        assertEquals(MoneyTone.Negative, tone(MoneySemantic.Budget(BudgetState.OVER)))
    }

    @Test
    fun `the budget tone does not depend on the amount`() {
        BudgetState.entries.forEach { state ->
            val semantic = MoneySemantic.Budget(state)
            assertEquals(tone(semantic, Paise(1)), tone(semantic, Paise(9_999_999)))
        }
    }
}
