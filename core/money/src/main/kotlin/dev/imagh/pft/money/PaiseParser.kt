package dev.imagh.pft.money

/**
 * Parses user-typed amount text into [Paise]. The text is untrusted input, so the grammar is
 * strict, parsing is deterministic and linear (a hand-written scan, no regex, so no backtracking
 * blow-up), and anything that does not match is rejected.
 *
 * Grammar (the whole string must match; no trimming is done, callers trim first):
 * ```
 * amount   = [ "₹" ] integer [ "." digit [ digit ] ]
 * integer  = digit+                      ; plain digits, any length
 *          | indian | western            ; with grouping commas
 * indian   = d{1,2} ( "," d{2} )* "," d{3}      ; 1,23,456
 * western  = d{1,3} ( "," d{3} )+               ; 123,456
 * ```
 * `d` is an ASCII digit `0-9`; other Unicode digits are rejected. There is no sign, exponent,
 * whitespace or thousands separator other than the comma. The optional leading rupee sign lets
 * [PaiseFormat]'s output parse back to the same value.
 *
 * **Rejection is signalled by returning `null`** (never by throwing): bad text is an expected
 * outcome at the UI boundary, not an exceptional one, and the caller shows its own message.
 * The result is always within `0..`[Paise.MAX_AMOUNT]. Inputs longer than [MAX_INPUT_LENGTH]
 * characters are rejected before they are scanned.
 */
object PaiseParser {
    /**
     * The longest input accepted, in characters. The longest legitimate input is
     * `₹1,00,00,00,00,000.00` (21 characters); this leaves room for leading zeros while bounding
     * the work done on hostile input.
     */
    const val MAX_INPUT_LENGTH = 32

    private const val RUPEE_SIGN = '₹'
    private const val RADIX = 10
    private const val PAISE_PER_RUPEE = 100L
    private const val MAX_FRACTION_DIGITS = 2
    private const val MAX_INDIAN_HEAD = 2
    private const val MAX_WESTERN_HEAD = 3
    private const val INDIAN_MIDDLE = 2
    private const val LAST_GROUP = 3

    /** Parses [text] to its amount in paise, or returns `null` if it is not a valid amount. */
    fun parse(text: String): Paise? =
        if (text.length > MAX_INPUT_LENGTH) null else parseBody(text.removePrefix(RUPEE_SIGN.toString()))

    private fun parseBody(body: String): Paise? {
        val hasFraction = '.' in body
        val fractionPart = body.substringAfter('.', "")
        val rupees = rupees(body.substringBefore('.'))
        val fraction = digitsValue(fractionPart)
        val fractionShapeOk = !hasFraction || fractionPart.length in 1..MAX_FRACTION_DIGITS
        if (!fractionShapeOk || rupees == null || fraction == null) return null
        // A single decimal digit is tenths of a rupee: "0.5" is 50 paise, not 5.
        val paise = if (fractionPart.length == 1) fraction * RADIX else fraction
        return Paise(rupees * PAISE_PER_RUPEE + paise).takeIf { it <= Paise.MAX_AMOUNT }
    }

    /** The whole-rupee value of the integer part, or `null` if it is malformed or over the limit. */
    private fun rupees(integerPart: String): Long? {
        val digits = if (',' in integerPart) groupedDigits(integerPart) else integerPart
        // A rupee count above the limit cannot be valid whatever the paise are; checking it here
        // also keeps `rupees * 100` far from overflow.
        return digits
            ?.takeIf { it.isNotEmpty() }
            ?.let(::digitsValue)
            ?.takeIf { it <= Paise.MAX_AMOUNT.value / PAISE_PER_RUPEE }
    }

    /** Strips the commas if (and only if) they form valid Indian or Western grouping. */
    private fun groupedDigits(integerPart: String): String? {
        val groups = integerPart.split(',')
        return if (isIndian(groups) || isWestern(groups)) groups.joinToString("") else null
    }

    private fun isIndian(groups: List<String>): Boolean =
        groups.first().length in 1..MAX_INDIAN_HEAD &&
            groups.drop(1).dropLast(1).all { it.length == INDIAN_MIDDLE } &&
            groups.last().length == LAST_GROUP

    private fun isWestern(groups: List<String>): Boolean =
        groups.first().length in 1..MAX_WESTERN_HEAD &&
            groups.drop(1).all { it.length == LAST_GROUP }

    /**
     * The value of an all-ASCII-digit string, or `null` if it has any other character. Stops
     * accumulating (returning `Long.MAX_VALUE`) once the value is past any possible limit, so it
     * never overflows. The empty string is 0 (an absent fraction).
     */
    private fun digitsValue(digits: String): Long? {
        var value = 0L
        for (c in digits) {
            if (c !in '0'..'9') return null
            value = if (value > Long.MAX_VALUE / RADIX - 1) Long.MAX_VALUE else value * RADIX + (c - '0')
        }
        return value
    }
}
