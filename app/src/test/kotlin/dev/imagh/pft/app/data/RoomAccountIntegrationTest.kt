package dev.imagh.pft.app.data

import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Accounts through the real stack (INT-2, INT-3, INT-4, L-4): `AccountService` over the Room
 * repository, `AuditedWriter`, the real audit log and in-memory SQLite. Every field round-trips,
 * and each change has its audit entry read back through the DAO and mapper.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomAccountIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val accounts get() = kit.accounts

    @After
    fun closeDatabase() = database.close()

    private fun draft(
        name: String = "HDFC Savings",
        type: AccountType = AccountType.BANK,
        opening: Paise = Paise(1_000_000),
        lastFour: String? = "1234",
        issuer: String? = "HDFC",
    ) = AccountDraft(name, type, opening, lastFour, issuer)

    private suspend fun trail(id: AccountId) = kit.trailOf("account", id.value)

    private fun storedCurrencies(): List<String> =
        database.openHelper.writableDatabase.query("SELECT currency FROM account").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor.getString(0) else null }.toList()
        }

    @Test
    fun `create stores every field and audits a CREATE attributed to the current identity`() =
        runSuspending {
            val who = kit.initialise()

            val created = accounts.create(draft(opening = Paise(-250_000)))

            kit.accountRepository.get(created.meta.id) shouldBe created
            created.openingBalance shouldBe Paise(-250_000)
            created.meta.createdByUserId shouldBe who.user.id
            created.meta.createdOnDeviceId shouldBe who.device.id
            val entry = trail(created.meta.id).single()
            entry.action shouldBe AuditAction.CREATE
            entry.userId shouldBe who.user.id
            entry.deviceId shouldBe who.device.id
            entry.changes shouldBe
                mapOf(
                    "name" to FieldChange(null, "HDFC Savings"),
                    "type" to FieldChange(null, "BANK"),
                    "opening_balance" to FieldChange(null, "-250000"),
                    "masked_identifier" to FieldChange(null, "1234"),
                    "issuer" to FieldChange(null, "HDFC"),
                )
        }

    @Test
    fun `optional fields round-trip as null and every type round-trips`() =
        runSuspending {
            kit.initialise()

            val created =
                AccountType.entries.map { type ->
                    accounts.create(draft(name = type.name, type = type, lastFour = null, issuer = null))
                }

            created.forEach { account ->
                val stored = kit.accountRepository.get(account.meta.id)!!
                stored shouldBe account
                stored.maskedIdentifier shouldBe null
                stored.issuer shouldBe null
            }
            created.map { it.type } shouldBe AccountType.entries
        }

    @Test
    fun `the largest balances in both directions and zero are stored exactly`() =
        runSuspending {
            kit.initialise()

            val values = listOf(Paise.MAX_AMOUNT, Paise.ZERO, Paise(-Paise.MAX_AMOUNT.value))
            val created = values.map { accounts.create(draft(opening = it)) }

            created.map { kit.accountRepository.get(it.meta.id)!!.openingBalance } shouldBe values
        }

    @Test
    fun `the stored currency is always INR`() =
        runSuspending {
            kit.initialise()
            accounts.create(draft(name = "One"))
            accounts.create(draft(name = "Two", type = AccountType.CASH))

            storedCurrencies() shouldBe listOf("INR", "INR")
        }

    @Test
    fun `edit stores the change and audits only the changed fields`() =
        runSuspending {
            kit.initialise()
            val created = accounts.create(draft())

            val edited = accounts.edit(created.meta.id, draft(name = "HDFC Salary", issuer = null))

            kit.accountRepository.get(created.meta.id) shouldBe edited
            val update = trail(created.meta.id).last()
            update.action shouldBe AuditAction.UPDATE
            update.changes shouldBe
                mapOf(
                    "name" to FieldChange("HDFC Savings", "HDFC Salary"),
                    "issuer" to FieldChange("HDFC", null),
                )
        }

    @Test
    fun `archive and unarchive are stored and audited, and move the account between the lists`() =
        runSuspending {
            kit.initialise()
            val account = accounts.create(draft())

            accounts.archive(account.meta.id)
            accounts.lists().archived.map { it.meta.id } shouldBe listOf(account.meta.id)
            accounts.lists().active.shouldBeEmpty()
            accounts.unarchive(account.meta.id)
            accounts.lists().active.map { it.meta.id } shouldBe listOf(account.meta.id)

            trail(account.meta.id).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
        }

    @Test
    fun `delete is a tombstone excluded from every list, restore brings it back, both audited`() =
        runSuspending {
            kit.initialise()
            val kept = accounts.create(draft(name = "Kept"))
            val gone = accounts.create(draft(name = "Gone"))

            accounts.delete(gone.meta.id)

            accounts.lists().active.map { it.meta.id } shouldBe listOf(kept.meta.id)
            accounts.all().map { it.meta.id } shouldBe listOf(kept.meta.id)
            kit.accountRepository
                .get(gone.meta.id)!!
                .meta.isDeleted shouldBe true
            kit.accountRepository.getLive(gone.meta.id) shouldBe null

            accounts.restore(gone.meta.id)

            accounts.all().map { it.name } shouldBe listOf("Gone", "Kept")
            trail(gone.meta.id).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
        }

    @Test
    fun `a failing audit write leaves no account row (INT-4)`() =
        runSuspending {
            kit.initialise()
            val failing =
                RoomTestKit(
                    database,
                    auditLog =
                        object : AuditLog {
                            override suspend fun append(entry: AuditEntry) = error("audit append failed")
                        },
                )

            shouldThrow<IllegalStateException> { failing.accounts.create(draft()) }

            storedCurrencies().shouldBeEmpty()
            accounts.all().shouldBeEmpty()
        }

    @Test
    fun `an edit of an unknown or deleted account is refused`() =
        runSuspending {
            kit.initialise()
            val account = accounts.create(draft())
            accounts.delete(account.meta.id)

            shouldThrow<LedgerError.Deleted> { accounts.edit(account.meta.id, draft(name = "Other")) }
            shouldThrow<LedgerError.NotFound> { accounts.edit(AccountId("missing"), draft()) }
        }

    @Test
    fun `before first-run setup nothing is written`() =
        runSuspending {
            shouldThrow<LedgerError.NotInitialised> { accounts.create(draft()) }

            storedCurrencies().shouldBeEmpty()
        }

    @Test
    fun `the production wiring exposes the same service`() {
        (kit.accounts is AccountService) shouldBe true
    }
}
