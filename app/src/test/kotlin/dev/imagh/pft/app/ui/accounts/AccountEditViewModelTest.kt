package dev.imagh.pft.app.ui.accounts

import android.os.Looper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * [AccountEditViewModel] over real in-memory Room through the production wiring ([RoomTestKit]): what
 * it refuses, what it saves and audits, how the balance sign follows the account type (plan Q3), how
 * edit mode prefills, and what a failure does. The ViewModel runs on the main dispatcher, so tests
 * wait with [awaitMain].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountEditViewModelTest {
    private val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val kit = RoomTestKit(database, auditLog = audit)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    /**
     * The real audit log until a test sets [failure]; then every append throws it, so any write that
     * reaches the audit log fails and Room rolls it back (the audit log is the write path's real swap
     * boundary, so this needs no seam of its own).
     */
    private class FailsOnDemand(
        private val real: AuditLog,
    ) : AuditLog {
        var failure: Throwable? = null
        var failedAppends = 0
            private set

        override suspend fun append(entry: AuditEntry) {
            failure?.let {
                failedAppends++
                throw it
            }
            real.append(entry)
        }
    }

    private fun failWith(failure: Throwable = IllegalStateException("secret detail HDFC Savings 1234")) {
        audit.failure = failure
    }

    private fun auditRowCount(): Long =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM audit_entry").use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    private fun accountRows(): List<Account> = blocking { kit.accounts.all() }

    private fun trail(id: AccountId) = blocking { kit.trailOf("account", id.value) }.map { it.action }

    /** Sets up the identity once, so a save can attribute its audit entry. */
    private fun ensureIdentity() {
        blocking { if (kit.identity.currentIdentity() == null) kit.initialise() }
    }

    /** A create form. */
    private fun createForm(): AccountEditViewModel {
        ensureIdentity()
        return AccountEditViewModel(kit.accounts, null)
    }

    /**
     * Sets up the identity and one account from [draft], then opens the edit form for it (or for
     * [idOverride] when given) and, unless [wait] is false, waits for the load.
     */
    private fun editForm(
        draft: AccountDraft = hdfc,
        idOverride: String? = null,
        wait: Boolean = true,
    ): Pair<AccountEditViewModel, AccountId> {
        ensureIdentity()
        val id = blocking { kit.accounts.create(draft) }.meta.id
        val vm = AccountEditViewModel(kit.accounts, idOverride ?: id.value)
        if (wait) awaitMain { vm.state.value.loaded }
        return vm to id
    }

    private val hdfc = AccountDraft("HDFC Savings", AccountType.BANK, Paise(1_000_000), "1234", "HDFC")

    private fun AccountEditViewModel.fill(
        name: String = "HDFC Savings",
        type: AccountType = AccountType.BANK,
        lastFour: String = "1234",
        issuer: String = "HDFC",
        amount: String = "10,000",
    ) {
        onNameChange(name)
        onTypeChange(type)
        onLastFourChange(lastFour)
        onIssuerChange(issuer)
        onAmountChange(amount)
    }

    private fun AccountEditViewModel.saveAndWait() {
        submit()
        awaitMain { state.value.saved }
    }

    // --- Create

    @Test
    fun `a new form is empty, for a Bank account, already loaded and not editing`() {
        val state = createForm().state.value

        state.editing shouldBe false
        state.loaded shouldBe true
        state.notFound shouldBe false
        state.name.text shouldBe ""
        state.type shouldBe AccountType.BANK
        state.lastFour.text shouldBe ""
        state.issuer.text shouldBe ""
        state.amount.text shouldBe ""
        state.saving shouldBe false
        state.saved shouldBe false
        state.saveFailed shouldBe false
    }

    @Test
    fun `a valid create saves one account with trimmed fields and audits a CREATE`() {
        val vm = createForm()
        vm.fill(name = "  HDFC Savings ", lastFour = " 1234 ", issuer = " HDFC ", amount = " 10,000 ")
        ShadowLog.clear()

        vm.saveAndWait()

        val stored = accountRows().single()
        stored.name shouldBe "HDFC Savings"
        stored.type shouldBe AccountType.BANK
        stored.openingBalance shouldBe Paise(1_000_000)
        stored.maskedIdentifier shouldBe "1234"
        stored.issuer shouldBe "HDFC"
        trail(stored.meta.id) shouldBe listOf(AuditAction.CREATE)
        vm.state.value.saving shouldBe false
        vm.state.value.saveFailed shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }

    @Test
    fun `blank last four and issuer are saved as none`() {
        val vm = createForm()
        vm.fill(lastFour = "  ", issuer = "")

        vm.saveAndWait()

        val stored = accountRows().single()
        stored.maskedIdentifier shouldBe null
        stored.issuer shouldBe null
    }

    @Test
    fun `a second submit while saving is ignored, so one account and one audit entry are written`() {
        val vm = createForm()
        vm.fill()

        vm.submit()
        vm.state.value.saving shouldBe true
        // The name is now invalid: without the guard this second submit would validate it and show Required.
        vm.onNameChange("")
        vm.submit()
        awaitMain { vm.state.value.saved }

        vm.state.value.name.error shouldBe null
        val stored = accountRows().single()
        stored.name shouldBe "HDFC Savings"
        auditRowCount() shouldBe 4 // the identity's three and the one CREATE
    }

    @Test
    fun `submitting again once saved changes nothing`() {
        val vm = createForm()
        vm.fill()
        vm.saveAndWait()

        vm.submit()
        repeat(5) { shadowOf(Looper.getMainLooper()).idle() }

        accountRows().size shouldBe 1
        auditRowCount() shouldBe 4 // the identity's three and the one CREATE
        vm.state.value.saved shouldBe true
    }

    // --- Refusals

    @Test
    fun `every invalid field shows its own error at once and nothing is saved`() {
        val vm = createForm()
        vm.fill(name = "  ", lastFour = "12a4", issuer = "x".repeat(101), amount = "abc")

        vm.submit()

        val state = vm.state.value
        state.name.error shouldBe FieldError.Required
        state.lastFour.error shouldBe FieldError.InvalidLastFour
        state.issuer.error shouldBe FieldError.TooLong(100)
        state.amount.error shouldBe FieldError.InvalidAmount
        state.saving shouldBe false
        state.saved shouldBe false
        // What was typed is shown as typed.
        state.name.text shouldBe "  "
        state.lastFour.text shouldBe "12a4"
        state.issuer.text shouldBe "x".repeat(101)
        state.amount.text shouldBe "abc"
        accountRows() shouldBe emptyList()
        auditRowCount() shouldBe 3 // the setup identity only
    }

    @Test
    fun `each field alone is refused with its own error and the others are clean`() {
        class Case(
            val typed: AccountEditViewModel.() -> Unit,
            val error: (AccountEditUiState) -> FieldError?,
            val expected: FieldError,
        )
        val devanagari = "\u0967\u0968\u0969\u096A"
        listOf(
            Case({ onNameChange("a".repeat(101)) }, { it.name.error }, FieldError.TooLong(100)),
            Case({ onLastFourChange("123") }, { it.lastFour.error }, FieldError.InvalidLastFour),
            Case({ onLastFourChange(devanagari) }, { it.lastFour.error }, FieldError.InvalidLastFour),
            Case({ onIssuerChange("y".repeat(101)) }, { it.issuer.error }, FieldError.TooLong(100)),
            Case({ onAmountChange("-5") }, { it.amount.error }, FieldError.InvalidAmount),
            Case({ onAmountChange("1.234") }, { it.amount.error }, FieldError.InvalidAmount),
            Case({ onAmountChange("") }, { it.amount.error }, FieldError.Required),
        ).forEach { case ->
            val vm = createForm()
            vm.fill()
            vm.apply(case.typed)

            vm.submit()

            case.error(vm.state.value) shouldBe case.expected
            listOf(
                vm.state.value.name.error,
                vm.state.value.lastFour.error,
                vm.state.value.issuer.error,
                vm.state.value.amount.error,
            ).count { it != null } shouldBe 1
            vm.state.value.saved shouldBe false
            accountRows() shouldBe emptyList()
        }
    }

    @Test
    fun `typing in a field clears only that field's error`() {
        val vm = createForm()
        vm.submit()
        vm.state.value.name.error shouldBe FieldError.Required
        vm.state.value.amount.error shouldBe FieldError.Required

        vm.onNameChange("H")

        vm.state.value.name.error shouldBe null
        vm.state.value.amount.error shouldBe FieldError.Required
    }

    // --- The balance sign follows the type (plan Q3)

    @Test
    fun `a credit card stores what is owed as a negative balance`() {
        val vm = createForm()
        vm.fill(type = AccountType.CREDIT_CARD, amount = "5,000")

        vm.saveAndWait()

        accountRows().single().openingBalance shouldBe Paise(-500_000)
    }

    @Test
    fun `bank, cash and wallet store the balance as entered`() {
        listOf(AccountType.BANK, AccountType.CASH, AccountType.WALLET).forEach { type ->
            val vm = createForm()
            vm.fill(name = "A $type", type = type, amount = "5,000")

            vm.saveAndWait()

            accountRows().single { it.name == "A $type" }.openingBalance shouldBe Paise(500_000)
        }
    }

    @Test
    fun `switching the type after typing flips the stored sign and leaves the text alone`() {
        val vm = createForm()
        vm.fill(type = AccountType.BANK, amount = "5,000")

        vm.onTypeChange(AccountType.CREDIT_CARD)

        vm.state.value.amount.text shouldBe "5,000"
        vm.saveAndWait()
        accountRows().single().openingBalance shouldBe Paise(-500_000)
        vm.state.value.amount.text shouldBe "5,000"
    }

    @Test
    fun `a zero balance is saved as zero for a credit card too`() {
        val vm = createForm()
        vm.fill(type = AccountType.CREDIT_CARD, amount = "0")

        vm.saveAndWait()

        accountRows().single().openingBalance shouldBe Paise.ZERO
    }

    // --- Edit mode

    @Test
    fun `edit mode prefills every field from the stored account`() {
        val (vm, _) = editForm(AccountDraft("HDFC Savings", AccountType.BANK, Paise(12_345_678), "1234", "HDFC"))

        val state = vm.state.value

        state.editing shouldBe true
        state.notFound shouldBe false
        state.name.text shouldBe "HDFC Savings"
        state.type shouldBe AccountType.BANK
        state.lastFour.text shouldBe "1234"
        state.issuer.text shouldBe "HDFC"
        state.amount.text shouldBe "1,23,456.78"
    }

    @Test
    fun `a negative card balance is shown as a positive amount owed`() {
        val (vm, _) = editForm(AccountDraft("Axis", AccountType.CREDIT_CARD, Paise(-500_000)))

        vm.state.value.type shouldBe AccountType.CREDIT_CARD
        vm.state.value.amount.text shouldBe "5,000.00"
        vm.state.value.lastFour.text shouldBe ""
        vm.state.value.issuer.text shouldBe ""
    }

    @Test
    fun `before the load finishes an edit form is neither loaded nor not found`() {
        val (vm, _) = editForm(wait = false)

        vm.state.value.loaded shouldBe false
        vm.state.value.notFound shouldBe false
        vm.state.value.editing shouldBe true
        // A save before the account is loaded would validate (and could write) a blank form: it is ignored.
        vm.submit()
        vm.state.value.name.error shouldBe null
        awaitMain { vm.state.value.loaded }
        vm.state.value.saving shouldBe false
        vm.state.value.saved shouldBe false
        auditRowCount() shouldBe 4
    }

    @Test
    fun `an edit saves the changes, keeps the card sign and audits an UPDATE`() {
        val (vm, id) = editForm(AccountDraft("Axis", AccountType.CREDIT_CARD, Paise(-500_000), "4321", "Axis"))
        vm.onNameChange("Axis Magnus")
        vm.onAmountChange("7,500")

        vm.saveAndWait()

        val stored = accountRows().single()
        stored.name shouldBe "Axis Magnus"
        stored.openingBalance shouldBe Paise(-750_000)
        stored.maskedIdentifier shouldBe "4321"
        trail(id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `an edit that changes nothing writes no audit entry`() {
        val (vm, id) = editForm(AccountDraft("Axis", AccountType.CREDIT_CARD, Paise(-500_000), "4321", "Axis"))
        val before = auditRowCount()

        vm.saveAndWait()

        accountRows().single().openingBalance shouldBe Paise(-500_000)
        trail(id) shouldBe listOf(AuditAction.CREATE)
        auditRowCount() shouldBe before
    }

    @Test
    fun `an untouched balance keeps its stored sign even when the sign does not match the type`() {
        // An overdrawn bank account cannot be entered in the form (plan Q3) but core allows it, and
        // an import later may create one: renaming it must not turn the overdraft into a credit.
        val (vm, id) = editForm(AccountDraft("Old current", AccountType.BANK, Paise(-50_000)))
        vm.state.value.amount.text shouldBe "500.00"
        vm.onNameChange("Old current account")

        vm.saveAndWait()

        accountRows().single().openingBalance shouldBe Paise(-50_000)
        trail(id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `once the amount or the type is changed the sign follows the type again`() {
        val (typed, _) = editForm(AccountDraft("Old current", AccountType.BANK, Paise(-50_000)))
        typed.onAmountChange("10")
        typed.saveAndWait()
        accountRows().single { it.name == "Old current" }.openingBalance shouldBe Paise(1_000)

        val (switched, _) = editForm(AccountDraft("Old wallet", AccountType.BANK, Paise(-50_000)))
        switched.onTypeChange(AccountType.WALLET)
        switched.saveAndWait()
        accountRows().single { it.name == "Old wallet" }.openingBalance shouldBe Paise(50_000)
    }

    // --- Unknown, deleted and malformed ids (untrusted navigation arguments)

    @Test
    fun `an id that does not exist ends in not found`() {
        val (vm, _) = editForm(idOverride = "00000000-0000-7000-8000-000000000000")

        awaitMain { vm.state.value.notFound }

        vm.state.value.loaded shouldBe true
        vm.state.value.editing shouldBe true
    }

    @Test
    fun `a deleted account ends in not found`() {
        val (_, id) = editForm()
        blocking { kit.accounts.delete(id) }
        val reopened = AccountEditViewModel(kit.accounts, id.value)

        awaitMain { reopened.state.value.notFound }

        reopened.state.value.loaded shouldBe true
        reopened.state.value.name.text shouldBe ""
    }

    @Test
    fun `blank, odd or oversized ids end in not found and never crash`() {
        ensureIdentity()
        listOf("", "   ", "../../etc/passwd", "x'); DROP TABLE account; --", "a".repeat(10_000)).forEach { raw ->
            val vm = AccountEditViewModel(kit.accounts, raw)

            awaitMain { vm.state.value.notFound }

            vm.state.value.loaded shouldBe true
            vm.state.value.saved shouldBe false
        }
        accountRows() shouldBe emptyList()
    }

    @Test
    fun `an account deleted after the form loaded ends in not found on save, with nothing logged`() {
        val (vm, id) = editForm()
        blocking { kit.accounts.delete(id) }
        vm.onNameChange("Renamed")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.notFound }

        vm.state.value.saved shouldBe false
        vm.state.value.saving shouldBe false
        vm.state.value.saveFailed shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
        trail(id) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    // --- An unexpected failure (the A5 save shape)

    @Test
    fun `a failing audit keeps the text, resets saving, sets saveFailed and logs only the enum`() {
        val vm = createForm()
        vm.fill(name = "  HDFC Savings ", amount = " 10,000 ")
        failWith()
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        val state = vm.state.value
        audit.failedAppends shouldBe 1
        state.saving shouldBe false
        state.saved shouldBe false
        state.name.text shouldBe "  HDFC Savings "
        state.amount.text shouldBe " 10,000 "
        state.name.error shouldBe null
        accountRows() shouldBe emptyList()
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("entity_save_failed kind=ACCOUNT")
    }

    @Test
    fun `after a failure the user can fix nothing and just retry, which saves once`() {
        val vm = createForm()
        vm.fill()
        failWith()
        vm.submit()
        awaitMain { vm.state.value.saveFailed }
        audit.failure = null

        vm.submit()
        awaitMain { vm.state.value.saved }

        vm.state.value.saveFailed shouldBe false
        accountRows().size shouldBe 1
        auditRowCount() shouldBe 4 // the identity's three and the one CREATE
    }

    @Test
    fun `editing a field clears the failure message`() {
        val vm = createForm()
        vm.fill()
        failWith()
        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        vm.onIssuerChange("HDFC Bank")

        vm.state.value.saveFailed shouldBe false
    }

    @Test
    fun `a failing audit on an edit keeps the stored account as it was`() {
        val (vm, id) = editForm()
        vm.onNameChange("Renamed")
        failWith()
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        vm.state.value.name.text shouldBe "Renamed"
        accountRows().single().name shouldBe "HDFC Savings"
        trail(id) shouldBe listOf(AuditAction.CREATE)
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("entity_save_failed kind=ACCOUNT")
    }

    @Test
    fun `a cancellation is not reported as a save failure`() {
        val vm = createForm()
        vm.fill()
        failWith(CancellationException("cancelled"))
        ShadowLog.clear()

        vm.submit()
        awaitMain { audit.failedAppends >= 1 }
        repeat(10) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(10)
        }

        vm.state.value.saveFailed shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }
}
