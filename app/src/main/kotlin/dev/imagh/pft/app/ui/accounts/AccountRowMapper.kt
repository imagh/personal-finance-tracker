package dev.imagh.pft.app.ui.accounts

import dev.imagh.pft.app.ui.common.EntityRow
import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.common.RowGlyph
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.category.CategoryColour

/**
 * The badge of an account type: a registry symbol and a `CategoryPalette` colour name, fixed per
 * type (plan D4) so the type reads at a glance and the user never has to pick an icon.
 */
internal data class AccountBadge(
    val symbol: PftSymbol,
    val colourKey: String,
)

/**
 * Everything account-specific about drawing an account in the shared list: which badge a type gets
 * and what the second line says. `EntityListScreen` stays ignorant of accounts; this is the only
 * place that knows (plan §3.3, "entity logic stays in the row mapper").
 */
internal object AccountRowMapper {
    private const val SEPARATOR = " · "

    /** The mask drawn before the last four digits. */
    private const val MASK = "••"

    /** Bank is blue, Credit card violet, Cash emerald and Wallet teal. */
    fun badge(type: AccountType): AccountBadge =
        when (type) {
            AccountType.BANK -> AccountBadge(PftSymbol.ACCOUNT_BALANCE, CategoryColour.BLUE.key)
            AccountType.CREDIT_CARD -> AccountBadge(PftSymbol.CREDIT_CARD, CategoryColour.VIOLET.key)
            AccountType.CASH -> AccountBadge(PftSymbol.PAYMENTS, CategoryColour.EMERALD.key)
            AccountType.WALLET -> AccountBadge(PftSymbol.ACCOUNT_BALANCE_WALLET, CategoryColour.TEAL.key)
        }

    /** "<type> · <issuer> · ••<last four>", leaving out whatever is missing. */
    fun supporting(
        typeLabel: String,
        issuer: String?,
        lastFour: String?,
    ): String = listOfNotNull(typeLabel, issuer, lastFour?.let { MASK + it }).joinToString(SEPARATOR)
}

/**
 * This account as a list row: its name, the type badge (spoken as [typeLabel]), the supporting line,
 * and the given [actions]. It has no `onClick` until the edit screen exists (B2b).
 *
 * @param typeLabel the type's display name ("Bank"), already translated.
 * @param actions the overflow menu entries the ViewModel offers for this account.
 */
internal fun Account.toRow(
    typeLabel: String,
    actions: List<RowAction>,
): EntityRow {
    val badge = AccountRowMapper.badge(type)
    return EntityRow(
        key = meta.id.value,
        title = name,
        supporting = AccountRowMapper.supporting(typeLabel, issuer, maskedIdentifier),
        glyph = RowGlyph(GlyphSource.Symbol(badge.symbol), badge.colourKey, typeLabel),
        actions = actions,
    )
}
