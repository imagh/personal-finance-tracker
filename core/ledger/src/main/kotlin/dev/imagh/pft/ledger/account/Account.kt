package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.money.Paise

/**
 * A place money is kept: a bank account, card, cash or wallet (PRD L-4). Every transaction belongs
 * to exactly one (L-9).
 *
 * There is **no stored balance** (INT-6): the balance is the [openingBalance] plus the account's
 * transactions, computed when asked. The currency is always INR and is not part of the model; the
 * persistence layer writes it and refuses anything else (plan D4).
 *
 * Built only by [AccountService], which validates and trims the values. Names are not unique: two
 * cards from one bank are normal, and [maskedIdentifier] tells them apart.
 *
 * @param name what the user calls it, trimmed, 1 to [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_NAME_LENGTH]
 *   characters.
 * @param type the kind of account.
 * @param openingBalance the balance when tracking started, in paise: signed (negative for an
 *   overdrawn account or money owed on a card), at most
 *   [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_AMOUNT] in size.
 * @param maskedIdentifier the last four digits of the account or card number, or null. Sensitive:
 *   never logged (CLAUDE.md §3.7).
 * @param issuer the bank or provider, or null.
 */
data class Account(
    override val meta: EntityMeta<AccountId>,
    val name: String,
    val type: AccountType,
    val openingBalance: Paise,
    val maskedIdentifier: String?,
    val issuer: String?,
) : LedgerEntity<AccountId, Account> {
    override val auditType = "account"

    override fun auditFields(): Map<String, String?> =
        mapOf(
            "name" to name,
            "type" to type.name,
            "opening_balance" to openingBalance.value.toString(),
            "masked_identifier" to maskedIdentifier,
            "issuer" to issuer,
        )

    override fun withMeta(meta: EntityMeta<AccountId>) = copy(meta = meta)
}
