package dev.imagh.pft.app.ui.accounts

import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.theme.CategoryPalette
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

/** The account to row mapping (plan D4): the type badge and the supporting line. Pure, so no Compose. */
class AccountRowMapperTest {
    private fun account(
        type: AccountType = AccountType.BANK,
        lastFour: String? = null,
        issuer: String? = null,
    ) = Account(
        meta =
            EntityMeta(
                id = AccountId("a1"),
                createdAt = Instant.EPOCH,
                updatedAt = Instant.EPOCH,
                createdByUserId = UserId("u1"),
                createdOnDeviceId = DeviceId("d1"),
                deletedAt = null,
                archivedAt = null,
            ),
        name = "HDFC Savings",
        type = type,
        openingBalance = Paise(0),
        maskedIdentifier = lastFour,
        issuer = issuer,
    )

    @Test
    fun `each type has its badge symbol and colour`() {
        AccountRowMapper.badge(AccountType.BANK) shouldBe AccountBadge(PftSymbol.ACCOUNT_BALANCE, "blue")
        AccountRowMapper.badge(AccountType.CREDIT_CARD) shouldBe AccountBadge(PftSymbol.CREDIT_CARD, "violet")
        AccountRowMapper.badge(AccountType.CASH) shouldBe AccountBadge(PftSymbol.PAYMENTS, "emerald")
        AccountRowMapper.badge(AccountType.WALLET) shouldBe AccountBadge(PftSymbol.ACCOUNT_BALANCE_WALLET, "teal")
    }

    @Test
    fun `every badge colour is a name in the category palette, so none falls back to slate`() {
        AccountType.entries.forEach { CategoryPalette.keys shouldContain AccountRowMapper.badge(it).colourKey }
    }

    @Test
    fun `the supporting line joins the type, the issuer and the masked last four`() {
        AccountRowMapper.supporting("Bank", issuer = "HDFC", lastFour = "1234") shouldBe "Bank · HDFC · ••1234"
    }

    @Test
    fun `missing parts are left out, not shown as blanks`() {
        AccountRowMapper.supporting("Cash", issuer = null, lastFour = null) shouldBe "Cash"
        AccountRowMapper.supporting("Bank", issuer = "HDFC", lastFour = null) shouldBe "Bank · HDFC"
        AccountRowMapper.supporting("Credit card", issuer = null, lastFour = "9876") shouldBe "Credit card · ••9876"
    }

    @Test
    fun `a row carries the name as its title, the type badge labelled by the type and the given actions`() {
        val actions = listOf(RowAction("Archive") {})
        var opened = 0

        val row =
            account(AccountType.CREDIT_CARD, lastFour = "4321", issuer = "Axis")
                .toRow("Credit card", onClick = { opened++ }, actions = actions)

        row.key shouldBe "a1"
        row.title shouldBe "HDFC Savings"
        row.supporting shouldBe "Credit card · Axis · ••4321"
        row.glyph?.source shouldBe GlyphSource.Symbol(PftSymbol.CREDIT_CARD)
        row.glyph?.colourKey shouldBe "violet"
        row.glyph?.label shouldBe "Credit card"
        row.actions shouldBe actions
        row.indent shouldBe false
        // Tapping the row opens the account: the row's click is the one given.
        row.onClick?.invoke()
        opened shouldBe 1
    }
}
