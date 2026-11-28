package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransactionWithLines
import dev.imagh.pft.ledger.transaction.Transfer
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * [TransactionEditViewModel] editing a stored transaction (slice C C4b): what it opens, what it refuses to open,
 * what an edit changes and audits, the time of day an edit keeps (Q6), the type rules of an edit, and an archived
 * reference that must survive an edit of something else. Real in-memory Room through [EditHarness].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionEditViewModelEditTest {
    private val h = EditHarness().start()

    @After
    fun closeDatabase() = h.close()

    private val at: Instant = ist(2026, 11, 3, 18, 45)

    private fun expense(
        amount: Long = 25_000,
        occurredAt: Instant = at,
        categoryId: CategoryId? = h.world.groceries.meta.id,
        tags: Set<TagId> = setOf(h.world.trip.meta.id),
    ): TransactionWithLines =
        h.blocking {
            h.kit.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(amount),
                    occurredAt,
                    h.world.bank.meta.id,
                    title = "Big Basket",
                    description = "weekly",
                    bankRef = "UTR1",
                    lines = listOf(LineDraft(null, Paise(amount), categoryId, tags, note = "milk")),
                ),
            )
        }

    private fun transfer(): Transfer =
        h.blocking {
            h.kit.transfers.create(
                TransferDraft(
                    h.world.bank.meta.id,
                    h.world.savings.meta.id,
                    Paise(500_000),
                    at,
                    title = "Move",
                    tagIds = setOf(h.world.gift.meta.id),
                ),
            )
        }

    private fun trailOf(id: TransactionId) =
        h.blocking { h.kit.trailOf("ledger_transaction", id.value) }.map { it.action }

    // --- Opening

    @Test
    fun `an expense opens with every field as stored, the date in the device zone, and expense and income on offer`() {
        val saved = expense()

        val state = h.open(saved.transaction.meta.id.value).state.value

        state.editing shouldBe true
        state.loaded shouldBe true
        state.notFound shouldBe false
        state.type shouldBe TransactionType.EXPENSE
        state.typeOptions shouldBe listOf(TransactionType.EXPENSE, TransactionType.INCOME)
        state.amount.text shouldBe "250.00"
        state.accountId shouldBe h.world.bank.meta.id
        state.date shouldBe LocalDate.of(2026, 11, 3)
        state.title.text shouldBe "Big Basket"
        state.description.text shouldBe "weekly"
        state.bankRef.text shouldBe "UTR1"
        state.categoryId shouldBe h.world.groceries.meta.id
        state.tagIds shouldBe setOf(h.world.trip.meta.id)
    }

    @Test
    fun `a transfer opens from either leg with both accounts, no type to switch and no category`() {
        val made = transfer()
        listOf(made.out, made.into).forEach { leg ->
            val state = h.open(leg.transaction.meta.id.value).state.value

            state.type shouldBe TransactionType.TRANSFER
            state.typeOptions shouldBe emptyList()
            state.accountId shouldBe h.world.bank.meta.id
            state.toAccountId shouldBe h.world.savings.meta.id
            state.amount.text shouldBe "5,000.00"
            state.title.text shouldBe "Move"
            state.tagIds shouldBe setOf(h.world.gift.meta.id)
            state.categoryId shouldBe null
        }
    }

    @Test
    fun `a route type is ignored when editing`() {
        val saved = expense()

        h
            .open(saved.transaction.meta.id.value, type = "TRANSFER")
            .state.value.type shouldBe TransactionType.EXPENSE
    }

    @Test
    fun `an unknown, blank, oversized, hostile or deleted id is not found and nothing is written`() {
        val saved = expense()
        h.blocking { h.kit.transactions.delete(saved.transaction.meta.id) }
        val rows = h.auditRows()
        val ids =
            listOf(
                "00000000-0000-7000-8000-000000000000",
                "",
                "   ",
                "x".repeat(10_000),
                "' OR 1=1 --",
                saved.transaction.meta.id.value,
            )

        ids.forEach { id ->
            val state = h.open(id).state.value

            state.notFound shouldBe true
        }
        h.auditRows() shouldBe rows
    }

    @Test
    fun `an itemised transaction is not opened by this form and nothing is lost`() {
        val total = Paise(30_000)
        val saved =
            h.blocking {
                h.kit.transactions.create(
                    TransactionDraft(
                        TransactionType.EXPENSE,
                        total,
                        at,
                        h.world.bank.meta.id,
                        lines =
                            listOf(
                                LineDraft(amount = Paise(10_000), categoryId = h.world.groceries.meta.id),
                                LineDraft(amount = Paise(20_000), categoryId = h.world.dining.meta.id),
                            ),
                    ),
                )
            }

        h
            .open(saved.transaction.meta.id.value)
            .state.value.notFound shouldBe true

        h.get(saved.transaction.meta.id.value)?.lines?.size shouldBe 2
    }

    // --- Saving an edit

    @Test
    fun `changing the amount updates the transaction, keeps its line and note, and audits only what changed`() {
        val saved = expense()
        val vm = h.open(saved.transaction.meta.id.value)

        vm.onTextChange(EditField.AMOUNT, "260")
        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        val after = h.get(saved.transaction.meta.id.value)!!
        after.transaction.amount shouldBe Paise(26_000)
        after.transaction.occurredAt shouldBe at
        after.lines
            .single()
            .meta.id shouldBe
            saved.lines
                .single()
                .meta.id
        after.lines.single().amount shouldBe Paise(26_000)
        after.lines.single().note shouldBe "milk"
        after.lines.single().tagIds shouldBe setOf(h.world.trip.meta.id)
        trailOf(saved.transaction.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
        h.storedIds().size shouldBe 1
    }

    @Test
    fun `saving with nothing changed writes no audit entry`() {
        val saved = expense()
        val vm = h.open(saved.transaction.meta.id.value)
        val rows = h.auditRows()

        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        h.auditRows() shouldBe rows
    }

    @Test
    fun `an edit that leaves the date alone keeps the stored instant to the millisecond`() {
        val exact = Instant.ofEpochMilli(at.toEpochMilli() + 123)
        val saved = expense(occurredAt = exact)
        val vm = h.open(saved.transaction.meta.id.value)

        vm.onTextChange(EditField.TITLE, "Renamed")
        vm.submit()
        h.awaitSaveEnd(vm)

        h.get(saved.transaction.meta.id.value)!!.transaction.occurredAt shouldBe exact
    }

    @Test
    fun `an edit that leaves the date alone keeps an instant in a clock-change overlap`() {
        // 01:30 happens twice on 2026-11-01 in New York; this is the second one (06:30 UTC, EST).
        val secondOneThirty = Instant.parse("2026-11-01T06:30:00Z")
        val saved = expense(occurredAt = secondOneThirty)
        val vm = h.open(saved.transaction.meta.id.value, clock = Clock.fixed(now(), ZoneId.of("America/New_York")))

        vm.onTextChange(EditField.TITLE, "Renamed")
        vm.submit()
        h.awaitSaveEnd(vm)

        h.get(saved.transaction.meta.id.value)!!.transaction.occurredAt shouldBe secondOneThirty
    }

    private fun now(): Instant = h.now.instant()

    @Test
    fun `changing the date keeps the stored time of day, not the time now`() {
        val saved = expense()
        val vm = h.open(saved.transaction.meta.id.value)

        vm.onDateChange(LocalDate.of(2026, 11, 9))
        vm.submit()
        h.awaitSaveEnd(vm)

        h.get(saved.transaction.meta.id.value)!!.transaction.occurredAt shouldBe ist(2026, 11, 9, 18, 45)
    }

    @Test
    fun `an archived tag already on the item survives a title edit`() {
        val saved = expense(tags = setOf(h.world.trip.meta.id, h.world.gift.meta.id))
        h.blocking { h.kit.tags.archive(h.world.trip.meta.id) }
        val vm = h.open(saved.transaction.meta.id.value)

        vm.onTextChange(EditField.TITLE, "Renamed")
        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        h
            .get(saved.transaction.meta.id.value)!!
            .lines
            .single()
            .tagIds shouldBe
            setOf(h.world.trip.meta.id, h.world.gift.meta.id)
    }

    @Test
    fun `an archived category and account already on the item survive an edit of something else`() {
        val saved = expense()
        h.blocking {
            h.kit.categories.archive(h.world.groceries.meta.id)
            h.kit.accounts.archive(h.world.bank.meta.id)
        }
        val vm = h.open(saved.transaction.meta.id.value)

        vm.onTextChange(EditField.TITLE, "Renamed")
        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        val after = h.get(saved.transaction.meta.id.value)!!
        after.lines.single().categoryId shouldBe h.world.groceries.meta.id
        after.transaction.accountId shouldBe h.world.bank.meta.id
    }

    @Test
    fun `switching an expense to income clears its category and saves as an income`() {
        val saved = expense()
        val vm = h.open(saved.transaction.meta.id.value)

        vm.onTypeChange(TransactionType.INCOME)
        vm.state.value.categoryCleared shouldBe true
        vm.submit()
        h.awaitSaveEnd(vm)

        val after = h.get(saved.transaction.meta.id.value)!!
        after.transaction.type shouldBe TransactionType.INCOME
        after.lines.single().categoryId shouldBe null
    }

    @Test
    fun `a stored transfer cannot be turned into an expense, and a stored expense cannot become a transfer`() {
        val made = transfer()
        val saved = expense()
        val transferForm = h.open(made.out.transaction.meta.id.value)
        val expenseForm = h.open(saved.transaction.meta.id.value)

        transferForm.onTypeChange(TransactionType.EXPENSE)
        expenseForm.onTypeChange(TransactionType.TRANSFER)

        transferForm.state.value.type shouldBe TransactionType.TRANSFER
        expenseForm.state.value.type shouldBe TransactionType.EXPENSE
    }

    @Test
    fun `editing a transfer changes both legs together and audits each`() {
        val made = transfer()
        val vm = h.open(made.out.transaction.meta.id.value)

        vm.onTextChange(EditField.AMOUNT, "5,500")
        vm.onToAccountPick(h.world.card.meta.id)
        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saved shouldBe true
        val legs = listOf(made.out, made.into).map { checkNotNull(h.get(it.transaction.meta.id.value)) }
        legs.forEach { it.transaction.amount shouldBe Paise(550_000) }
        legs.map { it.transaction.accountId } shouldBe listOf(h.world.bank.meta.id, h.world.card.meta.id)
        legs.forEach { trailOf(it.transaction.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE) }
        h.storedIds().size shouldBe 2
    }

    // --- A transaction that vanishes, and a failure while editing

    @Test
    fun `a transaction deleted after the form opened ends not found with nothing logged`() {
        val saved = expense()
        val vm = h.open(saved.transaction.meta.id.value)
        h.blocking { h.kit.transactions.delete(saved.transaction.meta.id) }
        vm.onTextChange(EditField.TITLE, "Renamed")
        ShadowLog.clear()

        vm.submit()
        awaitNotFound(vm)

        vm.state.value.saved shouldBe false
        vm.state.value.saveFailed shouldBe false
        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg } shouldBe emptyList()
        trailOf(saved.transaction.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `a failing edit leaves the stored transaction as it was and keeps the typed text`() {
        val saved = expense()
        val vm = h.open(saved.transaction.meta.id.value)
        vm.onTextChange(EditField.AMOUNT, "999")
        h.failure = IllegalStateException("boom")

        vm.submit()
        h.awaitSaveEnd(vm)

        vm.state.value.saveFailed shouldBe true
        vm.state.value.amount.text shouldBe "999"
        h.get(saved.transaction.meta.id.value)!!.transaction.amount shouldBe Paise(25_000)
    }

    private fun awaitNotFound(vm: TransactionEditViewModel) = awaitMain { vm.state.value.notFound }
}
