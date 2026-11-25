package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.Random

/**
 * The wiring every transaction service test starts from: the kit's fakes, a set-up device with one
 * account, an expense and an income category and a tag, and shorthand for drafts and seeded rows.
 * Call [start] first.
 *
 * @param seed seeds the service's id source, for a test that needs predictable transaction ids;
 *   null uses the kit's own (random) one.
 */
internal class TransactionFixture(
    seed: Long? = null,
) {
    val kit = LedgerTestKit()
    private val ids = if (seed == null) kit.ids else Uuid7(kit.clock, Random(seed))
    val service =
        if (seed == null) kit.transactions else serviceOver(kit.transactionLineRepository)
    val trail get() = kit.store.auditLog.entries

    lateinit var me: Identity
    lateinit var account: Account
    lateinit var food: Category
    lateinit var salary: Category
    lateinit var travel: Tag

    /**
     * A [TransactionService] over the kit's fakes but with [lineRepository] as the line store: how a
     * test injects a failing store. It uses this fixture's id source, so its ids follow the seed.
     */
    fun serviceOver(lineRepository: TransactionLineRepository) =
        TransactionService(
            kit.store,
            kit.writer,
            kit.transactionRepository,
            TransactionLines(kit.writer, lineRepository, kit.categoryRepository, kit.tagRepository, ids),
            kit.accountRepository,
            ids,
        )

    /** Sets up the identity and the references the drafts use. */
    suspend fun start(): TransactionFixture {
        me = kit.initialise()
        account = kit.accounts.create(AccountDraft("HDFC", AccountType.BANK, Paise(0)))
        food = kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft("Food", CategoryColour.CORAL))
        salary = kit.categories.create(null, CategoryKind.INCOME, CategoryDraft("Salary", CategoryColour.SLATE))
        travel = kit.tags.create("Travel")
        return this
    }

    /** A plain (one-line) draft: the line takes the whole amount. */
    fun draft(
        type: TransactionType = TransactionType.EXPENSE,
        amount: Long = 50_000,
        accountId: AccountId = account.meta.id,
        title: String? = "Grocer",
        description: String? = null,
        bankRef: String? = null,
        categoryId: CategoryId? = food.meta.id,
        tagIds: Set<TagId> = emptySet(),
        lineId: TransactionLineId? = null,
        lineAmount: Long = amount,
    ) = TransactionDraft(
        type = type,
        amount = Paise(amount),
        occurredAt = OCCURRED,
        accountId = accountId,
        title = title,
        description = description,
        bankRef = bankRef,
        lines = listOf(LineDraft(id = lineId, amount = Paise(lineAmount), categoryId = categoryId, tagIds = tagIds)),
    )

    /** The draft that saves [saved] again exactly as it is, line id included: the base of an edit. */
    fun draftOf(saved: TransactionWithLines): TransactionDraft {
        val txn = saved.transaction
        return TransactionDraft(
            type = txn.type,
            amount = txn.amount,
            occurredAt = txn.occurredAt,
            accountId = txn.accountId,
            title = txn.title,
            description = txn.description,
            bankRef = txn.bankRef,
            lines = saved.lines.map { LineDraft(it.meta.id, it.amount, it.categoryId, it.tagIds, it.note) },
        )
    }

    /**
     * A transaction and its one line written straight through the writer, the way a later slice's
     * entry point (SMS capture, a transfer) will write one: no public create path in C1a makes these.
     */
    suspend fun seed(
        type: TransactionType = TransactionType.EXPENSE,
        direction: Direction = Direction.OUT,
        state: TxnState = TxnState.UNVERIFIED,
        source: TxnSource = TxnSource.SMS,
    ): TransactionWithLines {
        val id = TransactionId(kit.ids.next())
        val txn =
            kit.writer.create(kit.transactionRepository, id) { meta ->
                LedgerTransaction(
                    meta,
                    type,
                    direction,
                    Paise(2_500),
                    OCCURRED,
                    account.meta.id,
                    null,
                    null,
                    state,
                    source,
                    "UTR1",
                    "msg-1",
                    "1234",
                    null,
                )
            }
        val line =
            kit.writer.create(kit.transactionLineRepository, TransactionLineId(kit.ids.next())) { meta ->
                TransactionLine(meta, id, 1, Paise(2_500), null, null, emptySet())
            }
        return TransactionWithLines(txn, listOf(line))
    }

    /** The stored transactions themselves (tombstones included), not the service's view. */
    fun storedTransactions() = kit.transactionRepository.all()

    /** The stored lines themselves (tombstones included). */
    fun storedLines() = kit.transactionLineRepository.all()

    companion object {
        val OCCURRED: Instant = Instant.parse("2026-11-15T09:30:00Z")
    }
}

/**
 * Runs [block], which must be refused with an [E], and checks the refusal changed nothing: the stored
 * transactions and lines (tombstones and all) and the audit trail are equal to their snapshots from
 * before, not merely the same size. Returns the error.
 */
internal suspend inline fun <reified E : Throwable> TransactionFixture.shouldRefuse(block: () -> Any?): E {
    val transactions = storedTransactions()
    val lines = storedLines()
    val written = trail.toList()
    val error = shouldThrow<E> { block() }
    storedTransactions() shouldBe transactions
    storedLines() shouldBe lines
    trail.toList() shouldBe written
    return error
}

/**
 * Runs [block], which must be refused with [LedgerError.InUse] for [what], and checks the refusal
 * changed nothing anywhere: no account, category, tag, transaction or line row (tombstones and all)
 * and no audit entry differs from its snapshot from before. Returns the error.
 */
internal suspend inline fun TransactionFixture.shouldRefuseInUse(
    what: String,
    block: () -> Any?,
): LedgerError.InUse {
    val accounts = kit.accountRepository.all()
    val categories = kit.categoryRepository.all()
    val tags = kit.tagRepository.all()
    // Transactions, lines and the audit trail are compared by shouldRefuse.
    val error = shouldRefuse<LedgerError.InUse>(block)
    error.what shouldBe what
    kit.accountRepository.all() shouldBe accounts
    kit.categoryRepository.all() shouldBe categories
    kit.tagRepository.all() shouldBe tags
    return error
}
