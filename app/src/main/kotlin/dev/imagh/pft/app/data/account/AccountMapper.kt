package dev.imagh.pft.app.data.account

import dev.imagh.pft.app.data.common.EntityMapper
import dev.imagh.pft.app.data.common.MetaMapper
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.money.Paise

/**
 * Maps a core [Account] to its [AccountEntity] row and back.
 *
 * Writing always stores [CURRENCY]. Reading **fails closed** on anything else, and on a type this
 * build does not know: only this mapper writes the table in M1a, so a different value means
 * corruption or data from an unsupported future version, and showing it as rupees would be wrong
 * money. The messages name the column only, never the stored value (CLAUDE.md §3.7).
 */
object AccountMapper : EntityMapper<Account, AccountEntity> {
    /** The only currency in M1a (PRD L-4; multi-currency is out of scope). */
    const val CURRENCY = "INR"

    override fun toRow(model: Account): AccountEntity =
        AccountEntity(
            meta = MetaMapper.toColumns(model.meta),
            name = model.name,
            type = model.type.name,
            currency = CURRENCY,
            openingBalance = model.openingBalance.value,
            maskedIdentifier = model.maskedIdentifier,
            issuer = model.issuer,
        )

    /**
     * The account stored in [row].
     *
     * @throws IllegalStateException if the currency is not INR or the type is unknown.
     */
    override fun toModel(row: AccountEntity): Account {
        check(row.currency == CURRENCY) { "An account row has an unsupported value in column currency" }
        val type =
            checkNotNull(AccountType.entries.firstOrNull { it.name == row.type }) {
                "An account row has an unknown value in column type"
            }
        return Account(
            meta = MetaMapper.toMeta(row.meta, ::AccountId),
            name = row.name,
            type = type,
            openingBalance = Paise(row.openingBalance),
            maskedIdentifier = row.maskedIdentifier,
            issuer = row.issuer,
        )
    }
}
