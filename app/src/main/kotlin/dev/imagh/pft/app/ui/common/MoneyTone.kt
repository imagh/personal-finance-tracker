package dev.imagh.pft.app.ui.common

import dev.imagh.pft.money.Paise

/**
 * What an amount means, so the UI can pick its sign, tone and spoken direction (plan §5.2).
 * Transaction amounts are positive magnitudes and the direction is the semantic; a [Balance] is the
 * only signed one.
 */
sealed interface MoneySemantic {
    /** Money in: a `+` sign, positive tone. */
    data object Income : MoneySemantic

    /** Money out: a true minus sign, negative tone in totals, neutral in list rows (Q2). */
    data object Expense : MoneySemantic

    /** Between the household's own accounts: no sign, neutral. */
    data object Transfer : MoneySemantic

    /** An account balance. The sign comes from the (signed) amount: only a negative one is marked and toned (Q3). */
    data object Balance : MoneySemantic

    /** A budget figure. No sign; the tone follows [state]. */
    data class Budget(
        val state: BudgetState,
    ) : MoneySemantic
}

/**
 * How far a budget has been used. Decided in core and only mapped here (plan §5.2). Until the
 * budget module exists (D1) this is the UI's own enum with the three values the design needs;
 * D4 maps core's `BudgetProgress` onto it.
 */
enum class BudgetState {
    /** Below the alert threshold. */
    UNDER,

    /** At or above the alert threshold but not over the limit. */
    AT_THRESHOLD,

    /** Over the limit. */
    OVER,
}

/**
 * The colour family of an amount or status. It is an index into `PftTheme.money`, never a raw
 * colour, and it is never the only cue: every toned amount also carries a sign or a word.
 * `StatusChip` reuses it for its pill colours.
 */
enum class MoneyTone {
    /** Ordinary text colour. */
    Neutral,

    /** Income and under-budget (the money green). */
    Positive,

    /** Expense totals, over-budget and negative balances (the money red). */
    Negative,

    /** A budget at or above its alert threshold (the money amber). */
    Warning,
}

/**
 * The tone for [amount] with the given [semantic]. [inRow] is true for a list-row amount, where an
 * expense stays neutral and red is kept for totals, over-budget and negative balances (owner
 * decision Q2). A positive or zero balance is neutral (Q3).
 */
fun moneyTone(
    semantic: MoneySemantic,
    amount: Paise,
    inRow: Boolean,
): MoneyTone =
    when (semantic) {
        MoneySemantic.Income -> MoneyTone.Positive
        MoneySemantic.Expense -> if (inRow) MoneyTone.Neutral else MoneyTone.Negative
        MoneySemantic.Transfer -> MoneyTone.Neutral
        MoneySemantic.Balance -> if (amount.value < 0) MoneyTone.Negative else MoneyTone.Neutral
        is MoneySemantic.Budget ->
            when (semantic.state) {
                BudgetState.UNDER -> MoneyTone.Positive
                BudgetState.AT_THRESHOLD -> MoneyTone.Warning
                BudgetState.OVER -> MoneyTone.Negative
            }
    }
