package dev.imagh.pft.ledger.account

import dev.imagh.pft.money.Paise

/**
 * What the user entered for an account, **untrusted until [AccountService] has validated it**. The
 * same draft serves create and edit.
 *
 * @param name required; trimmed and length-checked by the service.
 * @param type the kind of account.
 * @param openingBalance signed paise; the UI chooses the sign from the type (plan Q3), core only
 *   bounds it.
 * @param maskedIdentifier the last four digits, or null or blank for none; see
 *   [AccountRules.requireMaskedIdentifier].
 * @param issuer the bank or provider, or null or blank for none.
 */
data class AccountDraft(
    val name: String,
    val type: AccountType,
    val openingBalance: Paise,
    val maskedIdentifier: String? = null,
    val issuer: String? = null,
)
