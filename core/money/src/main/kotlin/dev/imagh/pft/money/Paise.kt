package dev.imagh.pft.money

/**
 * An amount of money in integer paise (1 rupee = 100 paise). Money is never floating point
 * (CLAUDE.md §3.8, INT-1): this is the only money representation in the app.
 *
 * The value is signed because balances can be negative (a credit card after a spend). Transaction
 * amounts, by contrast, are always positive and carry their direction separately (plan §3.2).
 *
 * Arithmetic is exact: an overflow throws [ArithmeticException] and never wraps.
 */
@JvmInline
value class Paise(
    val value: Long,
) : Comparable<Paise> {
    /** Exact sum; throws [ArithmeticException] if it does not fit in a `Long`. */
    operator fun plus(other: Paise): Paise = Paise(Math.addExact(value, other.value))

    /** Exact difference; may be negative; throws [ArithmeticException] if it does not fit in a `Long`. */
    operator fun minus(other: Paise): Paise = Paise(Math.subtractExact(value, other.value))

    override fun compareTo(other: Paise): Int = value.compareTo(other.value)

    companion object {
        /** No paise at all. */
        val ZERO: Paise = Paise(0L)

        /**
         * The largest amount the app accepts from text input: Rs 1e11 (10,000,00,00,000), i.e. 1e13
         * paise (plan §3.2 decision 8). The parser rejects anything above it; the ledger's own
         * `LedgerLimits` reuse it for transaction amounts. Far below `Long.MAX_VALUE`, so sums of
         * many limit-sized amounts cannot overflow in practice.
         */
        val MAX_AMOUNT: Paise = Paise(10_000_000_000_000L)
    }
}
