package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.validation.LedgerError

/**
 * The rules about an account's own fields that no shared validator covers. Public so a form can
 * check a field with core's rule as the user types (plan A5 D5), and [AccountService] applies the
 * same function again on save.
 */
object AccountRules {
    /** How many digits the identifier has. */
    const val MASKED_IDENTIFIER_LENGTH = 4

    /**
     * The validated last-digits identifier: null for null, empty or whitespace-only text (none was
     * given), otherwise the trimmed text if it is exactly [MASKED_IDENTIFIER_LENGTH] ASCII digits.
     *
     * Only `0-9` count: `Char.isDigit()` would also accept Arabic-Indic, Devanagari and full-width
     * digits, which are not what a bank prints on a card and would not match across sources.
     *
     * @throws LedgerError.InvalidMaskedIdentifier for any other text.
     */
    fun requireMaskedIdentifier(text: String?): String? {
        val trimmed = text?.trim().orEmpty()
        if (trimmed.isEmpty()) return null
        if (trimmed.length != MASKED_IDENTIFIER_LENGTH || trimmed.any { it !in '0'..'9' }) {
            throw LedgerError.InvalidMaskedIdentifier()
        }
        return trimmed
    }
}
