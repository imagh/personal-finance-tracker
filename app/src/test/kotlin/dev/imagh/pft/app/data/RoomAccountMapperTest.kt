package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.account.AccountEntity
import dev.imagh.pft.app.data.account.AccountMapper
import dev.imagh.pft.app.data.common.MetaColumns
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.Test
import java.time.Instant

/**
 * [AccountMapper]: the stored shape of an account. The currency column always holds `INR`; a row
 * with anything else is corrupt (or from an unsupported future sync) and must fail closed on read,
 * naming the column and never the value (plan D4).
 */
class RoomAccountMapperTest {
    private val at = Instant.parse("2026-11-01T10:00:00.123Z")
    private val account =
        Account(
            meta = EntityMeta(AccountId("acc-1"), at, at, UserId("user-1"), DeviceId("device-1")),
            name = "HDFC Savings",
            type = AccountType.CREDIT_CARD,
            openingBalance = Paise(-250_000),
            maskedIdentifier = "1234",
            issuer = "HDFC",
        )

    private fun row(currency: String = "INR") =
        AccountEntity(
            meta = MetaColumns("acc-1", at.toEpochMilli(), at.toEpochMilli(), "user-1", "device-1", null, null),
            name = "HDFC Savings",
            type = "CREDIT_CARD",
            currency = currency,
            openingBalance = -250_000,
            maskedIdentifier = "1234",
            issuer = "HDFC",
        )

    @Test
    fun `an account maps to a row with the documented columns, currency INR`() {
        AccountMapper.toRow(account) shouldBe row()
    }

    @Test
    fun `a row maps back to the same account`() {
        AccountMapper.toModel(row()) shouldBe account
    }

    @Test
    fun `optional columns map to and from null`() {
        val bare = account.copy(maskedIdentifier = null, issuer = null)

        val stored = AccountMapper.toRow(bare)

        stored.maskedIdentifier shouldBe null
        stored.issuer shouldBe null
        AccountMapper.toModel(stored) shouldBe bare
    }

    @Test
    fun `a row with any other currency fails on read, naming the column and not the value`() {
        val failure = shouldThrow<IllegalStateException> { AccountMapper.toModel(row(currency = "USD")) }

        failure.message.orEmpty() shouldContain "currency"
        failure.message.orEmpty() shouldNotContain "USD"
    }

    @Test
    fun `a row with an unknown type fails on read without echoing the stored text`() {
        val failure = shouldThrow<IllegalStateException> { AccountMapper.toModel(row().copy(type = "PIGGY_BANK")) }

        failure.message.orEmpty() shouldContain "type"
        failure.message.orEmpty() shouldNotContain "PIGGY_BANK"
    }
}
