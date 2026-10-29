package dev.imagh.pft.money

/**
 * Renders [Paise] for display in Indian digit grouping: the last three integer digits form a
 * group, and the digits before them are grouped in pairs (`₹1,23,456.78`). Always two decimals.
 *
 * This is the single formatter for money text: UI components such as `MoneyText` build on it and
 * must not reformat money themselves (design plan §6.1 DS5).
 *
 * **The output is sign-free.** `format` never emits a sign. The caller owns the sign glyph (for
 * example U+2212 MINUS SIGN for an expense) and prepends it to the result. A negative [Paise]
 * (a signed balance) formats as its magnitude, so a caller may pass a signed value straight in
 * and choose the glyph from [Paise.value].
 */
object PaiseFormat {
    private const val RUPEE_SIGN = '₹'
    private const val DECIMAL_DIGITS = 2
    private const val FIRST_GROUP = 3
    private const val GROUP = 2

    /** Formats the magnitude of [amount], for example `Paise(12345678)` as `"₹1,23,456.78"`. */
    fun format(amount: Paise): String {
        // Take the digits from the decimal string rather than from `abs()`: |Long.MIN_VALUE| does
        // not fit in a Long, and string handling needs no arithmetic on it.
        val digits =
            amount.value
                .toString()
                .removePrefix("-")
                .padStart(DECIMAL_DIGITS + 1, '0')
        val rupees = digits.dropLast(DECIMAL_DIGITS)
        val paise = digits.takeLast(DECIMAL_DIGITS)
        return "$RUPEE_SIGN${groupIndian(rupees)}.$paise"
    }

    private fun groupIndian(integerDigits: String): String {
        if (integerDigits.length <= FIRST_GROUP) return integerDigits
        val head = integerDigits.dropLast(FIRST_GROUP)
        val tail = integerDigits.takeLast(FIRST_GROUP)
        val pairs =
            head
                .reversed()
                .chunked(GROUP)
                .joinToString(",")
                .reversed()
        return "$pairs,$tail"
    }
}
