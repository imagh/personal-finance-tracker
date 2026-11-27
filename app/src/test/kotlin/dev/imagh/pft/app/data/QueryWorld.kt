package dev.imagh.pft.app.data

import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransactionWithLines
import dev.imagh.pft.ledger.transaction.TxnSource
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import java.time.Instant
import java.time.LocalDateTime

/**
 * The household every read test starts from (slice C C3a): three accounts, a Food category with two
 * children, Travel and Salary, and two tags, all created through the real services. Call [start] first.
 * Times are given in `Asia/Kolkata` (the kit's zone), whole milliseconds.
 */
internal class QueryWorld(
    val kit: RoomTestKit,
) {
    lateinit var bank: Account
    lateinit var card: Account
    lateinit var savings: Account
    lateinit var food: Category
    lateinit var groceries: Category
    lateinit var dining: Category
    lateinit var travel: Category
    lateinit var salary: Category
    lateinit var trip: Tag
    lateinit var gift: Tag

    private lateinit var me: Identity

    /** Performs first-run setup and creates the references; the credit card opens at zero. */
    suspend fun start(): QueryWorld {
        me = kit.initialise()
        bank = kit.accounts.create(AccountDraft("HDFC", AccountType.BANK, Paise(rs(10_000))))
        card = kit.accounts.create(AccountDraft("HDFC Card", AccountType.CREDIT_CARD, Paise(0)))
        savings = kit.accounts.create(AccountDraft("SBI", AccountType.BANK, Paise(0)))
        food = kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft("Food", CategoryColour.CORAL))
        groceries = child("Groceries")
        dining = child("Dining")
        travel = kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft("Travel", CategoryColour.SLATE))
        salary = kit.categories.create(null, CategoryKind.INCOME, CategoryDraft("Salary", CategoryColour.SLATE))
        trip = kit.tags.create("Trip")
        gift = kit.tags.create("Gift")
        return this
    }

    private suspend fun child(name: String) =
        kit.categories.create(food.meta.id, CategoryKind.EXPENSE, CategoryDraft(name, CategoryColour.CORAL))

    /** A line of [paise] paise. */
    fun item(
        paise: Long,
        category: Category? = null,
        vararg tags: Tag,
    ) = LineDraft(amount = Paise(paise), categoryId = category?.meta?.id, tagIds = tags.map { it.meta.id }.toSet())

    /** An expense on [account] at [at] whose amount is the sum of [lines]. */
    suspend fun spend(
        at: Instant,
        vararg lines: LineDraft,
        account: Account = bank,
    ): TransactionWithLines = draftOf(TransactionType.EXPENSE, at, account, lines.toList())

    /** An income of [paise] paise on [account] at [at], in Salary. */
    suspend fun earn(
        paise: Long,
        at: Instant,
        account: Account = bank,
    ): TransactionWithLines = draftOf(TransactionType.INCOME, at, account, listOf(item(paise, salary)))

    /**
     * Stores one row with a **chosen id** straight through the repository (no lines, no audit): for tests
     * that need many rows, colliding times or ids in an order the services would not produce. A read does
     * not need lines, so it comes back as a [LedgerEntry.Single] with none.
     */
    suspend fun row(
        id: String,
        at: Instant,
        account: Account = bank,
        type: TransactionType = TransactionType.EXPENSE,
        direction: Direction = if (type == TransactionType.INCOME) Direction.IN else Direction.OUT,
        group: String? = null,
        amount: Long = rs(1),
    ): LedgerTransaction {
        val created = Instant.parse("2026-11-26T00:00:00Z")
        val row =
            LedgerTransaction(
                meta = EntityMeta(TransactionId(id), created, created, me.user.id, me.device.id),
                type = type,
                direction = direction,
                amount = Paise(amount),
                occurredAt = at,
                accountId = account.meta.id,
                title = null,
                description = null,
                state = TxnState.VERIFIED,
                source = TxnSource.MANUAL,
                bankRef = null,
                sourceMessageId = null,
                accountFragment = null,
                transferGroupId = group,
            )
        kit.transactionRepository.insert(row)
        return row
    }

    /** [row] plus one live line `<id>-line` for the whole amount, uncategorised, written through the repository. */
    suspend fun rowWithLine(
        id: String,
        at: Instant,
    ) {
        val txn = row(id, at)
        kit.transactionLineRepository.insert(
            TransactionLine(
                meta =
                    EntityMeta(
                        TransactionLineId("$id-line"),
                        txn.meta.createdAt,
                        txn.meta.updatedAt,
                        txn.meta.createdByUserId,
                        txn.meta.createdOnDeviceId,
                    ),
                transactionId = txn.meta.id,
                position = 1,
                amount = txn.amount,
                categoryId = null,
                note = null,
                tagIds = emptySet(),
            ),
        )
    }

    /** Both legs of a transfer from [from] to [to] as raw rows with chosen ids `<group>-out` and `<group>-in`. */
    suspend fun rowPair(
        group: String,
        at: Instant,
        from: Account = bank,
        to: Account = savings,
    ) {
        row("$group-out", at, from, TransactionType.TRANSFER, Direction.OUT, group)
        row("$group-in", at, to, TransactionType.TRANSFER, Direction.IN, group)
    }

    private suspend fun draftOf(
        type: TransactionType,
        at: Instant,
        account: Account,
        lines: List<LineDraft>,
    ) = kit.transactions.create(
        TransactionDraft(type, Paise(lines.sumOf { it.amount.value }), at, account.meta.id, lines = lines),
    )

    companion object {
        /** Rupees as paise. */
        fun rs(rupees: Long) = rupees * 100

        /** An instant given as wall-clock time in `Asia/Kolkata`. */
        fun ist(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 12,
            minute: Int = 0,
            millis: Int = 0,
        ): Instant =
            LocalDateTime.of(year, month, day, hour, minute, 0, millis * 1_000_000).atZone(RoomTestKit.ZONE).toInstant()
    }
}

/** The transaction ids an entry list shows, a transfer by its group id, in order. */
internal fun List<LedgerEntry>.keys(): List<String> =
    map {
        when (it) {
            is LedgerEntry.Single -> it.transaction.meta.id.value
            is LedgerEntry.Transfer -> checkNotNull(it.outgoing.transferGroupId)
        }
    }

internal fun TransactionWithLines.id(): String = transaction.meta.id.value

internal fun id(value: String) = TransactionId(value)
