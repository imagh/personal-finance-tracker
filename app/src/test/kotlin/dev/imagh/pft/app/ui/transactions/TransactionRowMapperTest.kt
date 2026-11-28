package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.MoneySemantic
import dev.imagh.pft.app.ui.common.MoneyTone
import dev.imagh.pft.app.ui.common.moneyTone
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.transactions.EntryFixtures.card
import dev.imagh.pft.app.ui.transactions.EntryFixtures.dining
import dev.imagh.pft.app.ui.transactions.EntryFixtures.hdfc
import dev.imagh.pft.app.ui.transactions.EntryFixtures.line
import dev.imagh.pft.app.ui.transactions.EntryFixtures.names
import dev.imagh.pft.app.ui.transactions.EntryFixtures.salary
import dev.imagh.pft.app.ui.transactions.EntryFixtures.single
import dev.imagh.pft.app.ui.transactions.EntryFixtures.transfer
import dev.imagh.pft.app.ui.transactions.EntryFixtures.txn
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 * [TransactionRowMapper]: what a ledger entry looks like as a row (slice C C4a). Pure, so every case
 * is a plain function call over hand-built entries; the row's own look and speech are
 * `TransactionRowTest`'s job.
 */
class TransactionRowMapperTest {
    private val texts =
        RowTexts(
            items = { "$it items" },
            typeLabel = { type -> type.name.lowercase().replaceFirstChar(Char::uppercase) },
            uncategorised = "Uncategorised",
            unknown = "Unknown",
        )

    private fun map(entry: LedgerEntry) = TransactionRowMapper.toModel(entry, names, texts)

    @Test
    fun `one line shows its category glyph and name, then the account, and the title`() {
        val row = map(single())

        row.title shouldBe "Big Basket"
        row.glyph shouldBe GlyphSource.Symbol(PftSymbol.RESTAURANT)
        row.colourKey shouldBe "olive"
        row.category shouldBe "Groceries"
        row.account shouldBe "HDFC"
    }

    @Test
    fun `a category with an emoji icon draws the emoji`() {
        val row = map(single(txn(), line(category = dining)))

        row.glyph shouldBe GlyphSource.Emoji("🍜")
        row.colourKey shouldBe "coral"
    }

    @Test
    fun `a category with no icon draws the first letter of its name`() {
        val row = map(single(txn(type = TransactionType.INCOME), line(category = salary)))

        row.glyph shouldBe GlyphSource.Letter("S")
    }

    @Test
    fun `an uncategorised line says so and draws the receipt`() {
        val row = map(single(txn(), line(category = null)))

        row.category shouldBe "Uncategorised"
        row.glyph shouldBe GlyphSource.Symbol(PftSymbol.RECEIPT)
    }

    @Test
    fun `three lines draw the long receipt and say 3 items, whatever their categories`() {
        val row =
            map(
                single(
                    txn(amount = 600),
                    line(position = 1, amount = 100),
                    line(position = 2, amount = 200, category = dining),
                    line(position = 3, amount = 300, category = null),
                ),
            )

        row.glyph shouldBe GlyphSource.Symbol(PftSymbol.RECEIPT_LONG)
        row.category shouldBe "3 items"
        row.colourKey shouldBe null
    }

    @Test
    fun `a transfer draws the transfer symbol and reads from account to account, with no category`() {
        val row = map(transfer())

        row.glyph shouldBe GlyphSource.Symbol(PftSymbol.TRANSFER)
        row.category shouldBe null
        row.account shouldBe "HDFC → Axis Card"
        row.semantic shouldBe MoneySemantic.Transfer
        row.amount shouldBe Paise(5_000_00)
    }

    @Test
    fun `a blank title falls back to the category name, then to the type label`() {
        map(single(txn(title = null))).title shouldBe "Groceries"
        map(single(txn(title = null), line(category = null))).title shouldBe "Expense"
        map(single(txn(title = null, type = TransactionType.INCOME), line(category = null))).title shouldBe "Income"
        map(transfer(title = null)).title shouldBe "Transfer"
    }

    @Test
    fun `an itemised transaction with no title falls back to the type label, not to one of its categories`() {
        val row =
            map(
                single(
                    txn(title = null, amount = 300),
                    line(position = 1, amount = 100),
                    line(position = 2, amount = 200, category = dining),
                ),
            )

        row.title shouldBe "Expense"
    }

    @Test
    fun `amounts are non-negative magnitudes with the type's semantic, an expense neutral and an income green`() {
        val expense = map(single(txn(amount = 48_600)))
        val income =
            map(
                single(
                    txn(type = TransactionType.INCOME, amount = 12_000_00),
                    line(amount = 12_000_00, category = salary),
                ),
            )

        expense.amount shouldBe Paise(48_600)
        expense.semantic shouldBe MoneySemantic.Expense
        moneyTone(expense.semantic, expense.amount) shouldBe MoneyTone.Neutral
        income.amount shouldBe Paise(12_000_00)
        income.semantic shouldBe MoneySemantic.Income
        moneyTone(income.semantic, income.amount) shouldBe MoneyTone.Positive
    }

    @Test
    fun `an unverified transaction needs review and a verified one does not`() {
        map(single(txn(state = TxnState.UNVERIFIED))).needsReview shouldBe true
        map(single(txn(state = TxnState.VERIFIED))).needsReview shouldBe false
    }

    @Test
    fun `a transfer needs review when either of its legs is unverified`() {
        val verified = transfer()
        val outUnverified =
            LedgerEntry.Transfer(
                txn("t-out", TransactionType.TRANSFER, 100, hdfc, "Bill", TxnState.UNVERIFIED, Direction.OUT, "g"),
                verified.incoming,
            )
        val inUnverified =
            LedgerEntry.Transfer(
                verified.outgoing,
                txn("t-in", TransactionType.TRANSFER, 100, card, "Bill", TxnState.UNVERIFIED, Direction.IN, "g"),
            )

        map(verified).needsReview shouldBe false
        map(outUnverified).needsReview shouldBe true
        map(inUnverified).needsReview shouldBe true
    }

    @Test
    fun `tags are the names on the lines, each once, in name order ignoring case`() {
        val row =
            map(
                single(
                    txn(amount = 300),
                    line(position = 1, amount = 100, tags = setOf("t-b", "t-c")),
                    line(position = 2, amount = 200, tags = setOf("t-a", "t-c")),
                ),
            )

        row.tags shouldBe listOf("bulk", "Cash", "Weekly")
    }

    @Test
    fun `a name that is not in the lookups never reads blank`() {
        val row =
            map(
                single(
                    txn(account = AccountId("gone")),
                    line(category = CategoryId("gone"), tags = setOf("t-gone", "t-c")),
                ),
            )

        row.account shouldBe "Unknown"
        row.category shouldBe "Uncategorised"
        row.tags shouldBe listOf("Cash")
    }

    @Test
    fun `a transfer leg shown on its own is a transfer with its own account and no category`() {
        val leg = txn("t-out", TransactionType.TRANSFER, 5_000_00, hdfc, "Bill", direction = Direction.OUT, group = "g")

        val row = map(LedgerEntry.Single(leg, listOf(line("t-out", amount = 5_000_00, category = null))))

        row.semantic shouldBe MoneySemantic.Transfer
        row.glyph shouldBe GlyphSource.Symbol(PftSymbol.TRANSFER)
        row.category shouldBe null
        row.account shouldBe "HDFC"
    }

    @Test
    fun `the row is tappable only when the caller gives a click`() {
        val entry = single()
        var tapped = 0

        TransactionRowMapper.toModel(entry, names, texts).onClick shouldBe null
        TransactionRowMapper.toModel(entry, names, texts) { tapped++ }.onClick?.invoke()

        tapped shouldBe 1
    }
}
