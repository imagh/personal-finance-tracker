package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnSource
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * Hand-built ledger entries and the names around them, for tests that need a row's input without a
 * database (the mapper and the golden). Fake data only. Ids are plain readable text.
 */
internal object EntryFixtures {
    val at: Instant = Instant.parse("2026-11-10T06:00:00Z")

    val hdfc = AccountId("a-hdfc")
    val card = AccountId("a-card")
    val groceries = CategoryId("c-groceries")
    val dining = CategoryId("c-dining")
    val salary = CategoryId("c-salary")

    fun <T : EntityId> meta(id: T) = EntityMeta(id, at, at, UserId("u"), DeviceId("d"))

    fun category(
        id: CategoryId,
        name: String,
        kind: CategoryKind = CategoryKind.EXPENSE,
        icon: CategoryIcon? = null,
        colour: CategoryColour = CategoryColour.OLIVE,
    ) = Category(meta(id), null, kind, name, colour, icon, 1, null)

    /** The names the fixtures' ids stand for: two accounts, three categories and three tags. */
    val names =
        NameLookups(
            accounts = mapOf(hdfc to "HDFC", card to "Axis Card"),
            categories =
                listOf(
                    category(groceries, "Groceries", icon = CategoryIcon.Symbol("restaurant")),
                    category(dining, "Dining", icon = CategoryIcon.Emoji("🍜"), colour = CategoryColour.CORAL),
                    category(salary, "Salary", CategoryKind.INCOME, colour = CategoryColour.EMERALD),
                ).associateBy { it.meta.id },
            tags = mapOf(TagId("t-b") to "Weekly", TagId("t-a") to "bulk", TagId("t-c") to "Cash"),
        )

    fun txn(
        id: String = "t-1",
        type: TransactionType = TransactionType.EXPENSE,
        amount: Long = 48_600,
        account: AccountId = hdfc,
        title: String? = "Big Basket",
        state: TxnState = TxnState.VERIFIED,
        direction: Direction = if (type == TransactionType.INCOME) Direction.IN else Direction.OUT,
        group: String? = null,
        occurredAt: Instant = at,
    ) = LedgerTransaction(
        meta = meta(TransactionId(id)),
        type = type,
        direction = direction,
        amount = Paise(amount),
        occurredAt = occurredAt,
        accountId = account,
        title = title,
        description = null,
        state = state,
        source = TxnSource.MANUAL,
        bankRef = null,
        sourceMessageId = null,
        accountFragment = null,
        transferGroupId = group,
    )

    fun line(
        txn: String = "t-1",
        position: Int = 1,
        amount: Long = 48_600,
        category: CategoryId? = groceries,
        tags: Set<String> = emptySet(),
    ) = TransactionLine(
        meta = meta(TransactionLineId("$txn-l$position")),
        transactionId = TransactionId(txn),
        position = position,
        amount = Paise(amount),
        categoryId = category,
        note = null,
        tagIds = tags.map(::TagId).toSet(),
    )

    fun single(
        transaction: LedgerTransaction = txn(),
        vararg lines: TransactionLine = arrayOf(line()),
    ) = LedgerEntry.Single(transaction, lines.toList())

    fun transfer(
        title: String? = "Bill",
        amount: Long = 5_000_00,
        occurredAt: Instant = at,
    ) = LedgerEntry.Transfer(
        outgoing =
            txn(
                "t-out",
                TransactionType.TRANSFER,
                amount,
                hdfc,
                title,
                direction = Direction.OUT,
                group = "g",
                occurredAt = occurredAt,
            ),
        incoming =
            txn(
                "t-in",
                TransactionType.TRANSFER,
                amount,
                card,
                title,
                direction = Direction.IN,
                group = "g",
                occurredAt = occurredAt,
            ),
    )
}
