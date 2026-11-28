package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnSource
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.time.LocalDate

/**
 * [TransactionEditViewModel] creating a transaction (slice C C4b): the type preset from the route, what each field
 * is checked with, what a valid expense, income and transfer save, the category rule when the type switches, the
 * single-save guard and what a failure does. Real in-memory Room through [EditHarness]; the editing side is
 * [TransactionEditViewModelEditTest].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionEditViewModelTest {
    private val h = EditHarness().start()

    @After
    fun closeDatabase() = h.close()

    private val bank get() = h.world.bank.meta.id
    private val savings get() = h.world.savings.meta.id

    /** Fills a valid expense of Rs 250 on HDFC. */
    private fun TransactionEditViewModel.fillExpense(amount: String = "250") {
        onTextChange(EditField.AMOUNT, amount)
        onAccountPick(bank)
    }

    // --- The route's type preset

    @Test
    fun `a new form opens as an expense on today with the three types on offer`() {
        val state = h.open().state.value

        state.type shouldBe TransactionType.EXPENSE
        state.editing shouldBe false
        state.date shouldBe LocalDate.of(2026, 11, 15)
        state.typeOptions shouldBe listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.TRANSFER)
    }

    @Test
    fun `type INCOME opens in income mode`() {
        h
            .open(type = "INCOME")
            .state.value.type shouldBe TransactionType.INCOME
    }

    @Test
    fun `type TRANSFER opens in transfer mode`() {
        h
            .open(type = "TRANSFER")
            .state.value.type shouldBe TransactionType.TRANSFER
    }

    @Test
    fun `an unknown, hostile or lower-case type opens in expense mode`() {
        listOf("EXPENSE", "bogus", "income", "", "INCOME; DROP TABLE", "x".repeat(10_000)).forEach {
            h
                .open(type = it)
                .state.value.type shouldBe TransactionType.EXPENSE
        }
    }

    // --- Each field is checked with core's rule, and nothing is saved

    private fun TransactionEditViewModel.assertRefusedWithNothingWritten(check: (TransactionEditUiState) -> Unit) {
        val rows = h.auditRows()
        submit()
        h.awaitSaveEnd(this)
        check(state.value)
        state.value.saving shouldBe false
        state.value.saved shouldBe false
        h.storedIds() shouldBe emptyList()
        h.auditRows() shouldBe rows
    }

    @Test
    fun `a title over the limit is TooLong and nothing is saved`() {
        val vm = h.open()
        vm.fillExpense()
        vm.onTextChange(EditField.TITLE, "t".repeat(201))

        vm.assertRefusedWithNothingWritten { it.title.error shouldBe FieldError.TooLong(200) }
    }

    @Test
    fun `a note over the limit is TooLong and nothing is saved`() {
        val vm = h.open()
        vm.fillExpense()
        vm.onTextChange(EditField.DESCRIPTION, "n".repeat(2_001))

        vm.assertRefusedWithNothingWritten { it.description.error shouldBe FieldError.TooLong(2_000) }
    }

    @Test
    fun `a bank reference over the limit is TooLong and nothing is saved`() {
        val vm = h.open()
        vm.fillExpense()
        vm.onTextChange(EditField.BANK_REF, "r".repeat(65))

        vm.assertRefusedWithNothingWritten { it.bankRef.error shouldBe FieldError.TooLong(64) }
    }

    @Test
    fun `a bad, signed, over-precise, zero or blank amount is refused with its own error`() {
        listOf("abc" to FieldError.InvalidAmount, "-5" to FieldError.InvalidAmount, "1.234" to FieldError.InvalidAmount)
            .plus("0" to FieldError.InvalidAmount)
            .plus("0.00" to FieldError.InvalidAmount)
            .plus("1,00,00,00,00,001" to FieldError.InvalidAmount)
            .plus("" to FieldError.Required)
            .forEach { (text, error) ->
                val vm = h.open()
                vm.fillExpense(text)

                vm.assertRefusedWithNothingWritten { it.amount.error shouldBe error }
                vm.state.value.amount.text shouldBe text
            }
    }

    @Test
    fun `no account is Required and nothing is saved`() {
        val vm = h.open()
        vm.onTextChange(EditField.AMOUNT, "250")

        vm.assertRefusedWithNothingWritten { it.accountError shouldBe FieldError.Required }
    }

    @Test
    fun `every invalid field shows its error at once`() {
        val vm = h.open()
        vm.onTextChange(EditField.TITLE, "t".repeat(201))

        vm.assertRefusedWithNothingWritten {
            it.amount.error shouldBe FieldError.Required
            it.accountError shouldBe FieldError.Required
            it.title.error shouldBe FieldError.TooLong(200)
        }
    }

    @Test
    fun `a transfer with no destination is Required and one to the same account is refused`() {
        val vm = h.open(type = "TRANSFER")
        vm.fillExpense()
        vm.assertRefusedWithNothingWritten { it.toAccountError shouldBe FieldError.Required }

        vm.onToAccountPick(bank)

        vm.assertRefusedWithNothingWritten { it.toAccountError shouldBe FieldError.SameAccount }
    }

    @Test
    fun `an error clears as soon as its field is edited`() {
        val vm = h.open()
        vm.submit()
        vm.state.value.amount.error shouldBe FieldError.Required

        vm.onTextChange(EditField.AMOUNT, "1")
        vm.onAccountPick(bank)

        vm.state.value.amount.error shouldBe null
        vm.state.value.accountError shouldBe null
    }

    // --- A valid save

    @Test
    fun `a valid expense is saved with one line, today's time of day, attribution and an audit trail`() {
        val vm = h.open()
        vm.fillExpense(" 250 ")
        vm.onTextChange(EditField.TITLE, "  Big Basket ")
        vm.onTextChange(EditField.DESCRIPTION, "weekly")
        vm.onTextChange(EditField.BANK_REF, " UTR1 ")
        vm.onCategoryPick(h.world.groceries.meta.id)
        vm.onTagsDone(setOf(h.world.trip.meta.id, h.world.gift.meta.id))

        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        val stored = h.only()
        val txn = stored.transaction
        txn.type shouldBe TransactionType.EXPENSE
        txn.direction shouldBe Direction.OUT
        txn.amount shouldBe Paise(25_000)
        txn.occurredAt shouldBe ist(2026, 11, 15, 10)
        txn.accountId shouldBe bank
        txn.title shouldBe "Big Basket"
        txn.description shouldBe "weekly"
        txn.bankRef shouldBe "UTR1"
        txn.source shouldBe TxnSource.MANUAL
        txn.state shouldBe TxnState.VERIFIED
        val line = stored.lines.single()
        line.amount shouldBe Paise(25_000)
        line.categoryId shouldBe h.world.groceries.meta.id
        line.tagIds shouldBe setOf(h.world.trip.meta.id, h.world.gift.meta.id)
        h.blocking { h.kit.trailOf("ledger_transaction", txn.meta.id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE)
    }

    @Test
    fun `a blank title, note and bank reference are saved as none, and a category is optional`() {
        val vm = h.open()
        vm.fillExpense()
        vm.onTextChange(EditField.TITLE, "   ")

        vm.submit()
        h.awaitSaveEnd(vm)

        val stored = h.only()
        stored.transaction.title shouldBe null
        stored.transaction.description shouldBe null
        stored.transaction.bankRef shouldBe null
        stored.lines.single().categoryId shouldBe null
    }

    @Test
    fun `a chosen date keeps the time of day the form was opened at`() {
        val vm = h.open()
        vm.fillExpense()
        vm.onDateChange(LocalDate.of(2026, 11, 3))

        vm.submit()
        h.awaitSaveEnd(vm)

        h.only().transaction.occurredAt shouldBe ist(2026, 11, 3, 10)
    }

    @Test
    fun `an income is saved as money in on an income category`() {
        val vm = h.open(type = "INCOME")
        vm.fillExpense("1000")
        vm.onCategoryPick(h.world.salary.meta.id)

        vm.submit()
        h.awaitSaveEnd(vm)

        val txn = h.only().transaction
        txn.type shouldBe TransactionType.INCOME
        txn.direction shouldBe Direction.IN
        txn.amount shouldBe Paise(100_000)
    }

    @Test
    fun `a transfer is saved as two legs in one group with the tags, and no bank reference or category`() {
        val vm = h.open(type = "TRANSFER")
        vm.fillExpense("5000")
        vm.onToAccountPick(savings)
        vm.onTextChange(EditField.TITLE, "Move")
        vm.onTextChange(EditField.BANK_REF, "ignored")
        vm.onTagsDone(setOf(h.world.trip.meta.id))

        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        val legs = h.storedIds().map { checkNotNull(h.get(it)) }
        legs.size shouldBe 2
        legs.map { it.transaction.type }.toSet() shouldBe setOf(TransactionType.TRANSFER)
        legs.map { it.transaction.accountId to it.transaction.direction } shouldContainExactlyInAnyOrder
            listOf(bank to Direction.OUT, savings to Direction.IN)
        legs.map { it.transaction.transferGroupId }.toSet().size shouldBe 1
        legs.forEach {
            it.transaction.amount shouldBe Paise(500_000)
            it.transaction.title shouldBe "Move"
            it.transaction.bankRef shouldBe null
            it.lines.single().categoryId shouldBe null
            it.lines.single().tagIds shouldBe setOf(h.world.trip.meta.id)
        }
    }

    @Test
    fun `a transfer ignores a bank reference left over from expense mode, even one too long to save`() {
        val vm = h.open()
        vm.fillExpense("100")
        vm.onTextChange(EditField.BANK_REF, "r".repeat(65))
        vm.onTypeChange(TransactionType.TRANSFER)
        vm.onToAccountPick(savings)

        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        vm.state.value.bankRef.error shouldBe null
        h.storedIds().size shouldBe 2
    }

    // --- Switching the type

    @Test
    fun `switching expense to income clears an expense category and says so`() {
        val vm = h.open()
        vm.onCategoryPick(h.world.groceries.meta.id)

        vm.onTypeChange(TransactionType.INCOME)

        vm.state.value.type shouldBe TransactionType.INCOME
        vm.state.value.categoryId shouldBe null
        vm.state.value.categoryCleared shouldBe true
    }

    @Test
    fun `switching income to expense clears an income category and says so`() {
        val vm = h.open(type = "INCOME")
        vm.onCategoryPick(h.world.salary.meta.id)

        vm.onTypeChange(TransactionType.EXPENSE)

        vm.state.value.categoryId shouldBe null
        vm.state.value.categoryCleared shouldBe true
    }

    @Test
    fun `switching with no category, or to transfer and back, clears nothing and shows no notice`() {
        val vm = h.open()
        vm.onTypeChange(TransactionType.INCOME)
        vm.state.value.categoryCleared shouldBe false

        vm.onTypeChange(TransactionType.EXPENSE)
        vm.onCategoryPick(h.world.travel.meta.id)
        vm.onTypeChange(TransactionType.TRANSFER)
        vm.onTypeChange(TransactionType.EXPENSE)

        vm.state.value.categoryId shouldBe h.world.travel.meta.id
        vm.state.value.categoryCleared shouldBe false
    }

    @Test
    fun `the notice goes when a category is picked or the type changes again`() {
        val vm = h.open()
        vm.onCategoryPick(h.world.groceries.meta.id)
        vm.onTypeChange(TransactionType.INCOME)
        vm.onCategoryPick(h.world.salary.meta.id)
        vm.state.value.categoryCleared shouldBe false

        vm.onTypeChange(TransactionType.EXPENSE)
        vm.state.value.categoryCleared shouldBe true

        vm.onTypeChange(TransactionType.INCOME)
        vm.state.value.categoryCleared shouldBe false
    }

    // --- One save at a time

    @Test
    fun `a second Save while saving is ignored`() {
        val vm = h.open()
        vm.fillExpense()

        vm.submit()
        vm.submit()
        h.awaitSaveEnd(vm)

        h.storedIds().size shouldBe 1
    }

    @Test
    fun `a Save after the transaction is saved is ignored`() {
        val vm = h.open()
        vm.fillExpense()
        vm.submit()
        h.awaitSaveEnd(vm)
        vm.onTextChange(EditField.AMOUNT, "999")

        vm.submit()
        h.awaitSaveEnd(vm)

        h.storedIds().size shouldBe 1
        h.only().transaction.amount shouldBe Paise(25_000)
    }

    // --- A failure

    @Test
    fun `a failing save keeps the text, resets saving, logs only the kind, and a retry saves once`() {
        val vm = h.open()
        vm.fillExpense("250")
        vm.onTextChange(EditField.TITLE, "Secret Payee 9876")
        h.failure = IllegalStateException("secret detail Secret Payee 9876 250")
        ShadowLog.clear()

        vm.submit()
        h.awaitSaveEnd(vm)

        val state = vm.state.value
        state.saveFailed shouldBe true
        state.saving shouldBe false
        state.saved shouldBe false
        state.title.text shouldBe "Secret Payee 9876"
        state.amount.text shouldBe "250"
        h.storedIds() shouldBe emptyList()
        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg } shouldBe
            listOf(LogEvent.EntitySaveFailed(EntityKind.TRANSACTION).line())

        h.failure = null
        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        vm.state.value.saveFailed shouldBe false
        h.storedIds().size shouldBe 1
    }

    @Test
    fun `editing a field clears the failure message`() {
        val vm = h.open()
        vm.fillExpense()
        h.failure = IllegalStateException("boom")
        vm.submit()
        h.awaitSaveEnd(vm)

        vm.onTextChange(EditField.AMOUNT, "251")

        vm.state.value.saveFailed shouldBe false
    }

    @Test
    fun `an archived account is not offered, and the pickers offer only what can be chosen now`() {
        h.blocking { h.kit.accounts.archive(h.world.savings.meta.id) }

        val choices =
            h
                .open()
                .state.value.choices

        choices.accounts.map { it.name } shouldBe listOf("HDFC", "HDFC Card")
        choices.expenseCategories.map { it.name }.contains("Salary") shouldBe false
        choices.incomeCategories.map { it.name } shouldBe listOf("Salary")
    }
}
