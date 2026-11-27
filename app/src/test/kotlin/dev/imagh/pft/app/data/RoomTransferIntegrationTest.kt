package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionLineRepository
import dev.imagh.pft.ledger.transaction.TransactionLines
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.Transfer
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.ledger.transaction.TransferService
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/**
 * Transfers through the real stack (slice C C2): `TransferService` and `TransactionService` over the Room
 * repositories, `AuditedWriter`, the real audit log and in-memory SQLite. A transfer is two legs, two lines
 * and four audit entries in **one** Room transaction: the failure tests inject the failure at a real boundary
 * (a line insert that throws, an account that only the foreign key refuses) **after the first leg was written**
 * and compare every row of the four tables and the audit log, as text, before and after.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomTransferIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val raw: SupportSQLiteDatabase get() = database.openHelper.writableDatabase
    private val at = Instant.parse("2026-10-31T08:30:00.500Z")

    @After
    fun closeDatabase() = database.close()

    private class World(
        val bank: Account,
        val savings: Account,
        val card: Account,
        val trip: Tag,
    )

    private suspend fun world(): World {
        kit.initialise()

        fun account(
            name: String,
            type: AccountType,
        ) = AccountDraft(name, type, Paise(0))
        return World(
            bank = kit.accounts.create(account("HDFC", AccountType.BANK)),
            savings = kit.accounts.create(account("SBI", AccountType.BANK)),
            card = kit.accounts.create(account("HDFC Card", AccountType.CREDIT_CARD)),
            trip = kit.tags.create("Trip"),
        )
    }

    private fun World.draft(
        from: Account = bank,
        to: Account = savings,
        amount: Long = 500_00,
        tags: Set<Tag> = setOf(trip),
    ) = TransferDraft(from.meta.id, to.meta.id, Paise(amount), at, "Move", "Monthly", tags.map { it.meta.id }.toSet())

    private suspend fun mismatches() = database.transactionLineDao().breakdownMismatchCount()

    private fun dump(table: String): List<String> =
        raw.query("SELECT * FROM $table ORDER BY rowid").use { cursor ->
            generateSequence { if (cursor.moveToNext()) cursor else null }
                .map { row ->
                    (0 until row.columnCount).joinToString("|") { if (row.isNull(it)) "NULL" else row.getString(it) }
                }.toList()
        }

    /** Every row of the tables a transfer touches, plus the audit log, as text. */
    private fun everything(): Map<String, List<String>> =
        listOf("ledger_transaction", "transaction_line", "transaction_line_tag", "audit_entry")
            .associateWith(::dump)

    private fun count(sql: String): Int =
        raw.query(sql).use {
            it.moveToFirst()
            it.getInt(0)
        }

    private companion object {
        /** The audit entries of transactions and lines, the ones a failed transfer must not leave. */
        const val TRANSFER_AUDIT =
            "SELECT COUNT(*) FROM audit_entry WHERE entity_type IN ('ledger_transaction','transaction_line')"
    }

    private suspend fun Transfer.legsInStore() = kit.transactionRepository.legsOfGroup(groupId)

    // Create ------------------------------------------------------------------------------------------

    @Test
    fun `a transfer is two legs and two lines with four CREATE entries, and the legs are found by group`() =
        runSuspending {
            val w = world()
            val before = count("SELECT COUNT(*) FROM audit_entry")

            val made = kit.transfers.create(w.draft())

            made.legsInStore() shouldContainExactlyInAnyOrder listOf(made.out.transaction, made.into.transaction)
            made.out.transaction.direction shouldBe Direction.OUT
            made.out.transaction.accountId shouldBe w.bank.meta.id
            made.into.transaction.direction shouldBe Direction.IN
            made.into.transaction.accountId shouldBe w.savings.meta.id
            listOf(made.out, made.into).forEach { leg ->
                leg.transaction.type shouldBe TransactionType.TRANSFER
                leg.transaction.amount shouldBe Paise(500_00)
                leg.lines.single().categoryId shouldBe null
                leg.lines.single().tagIds shouldBe setOf(w.trip.meta.id)
                kit.transactions.get(leg.transaction.meta.id) shouldBe leg
            }
            count("SELECT COUNT(*) FROM audit_entry") - before shouldBe 4
            val created =
                listOf(
                    "ledger_transaction" to made.out.transaction.meta.id.value,
                    "ledger_transaction" to made.into.transaction.meta.id.value,
                    "transaction_line" to
                        made.out.lines
                            .single()
                            .meta.id.value,
                    "transaction_line" to
                        made.into.lines
                            .single()
                            .meta.id.value,
                )
            created.forEach { (type, id) ->
                kit.trailOf(type, id).map { it.action } shouldBe listOf(AuditAction.CREATE)
            }
            mismatches() shouldBe 0
        }

    @Test
    fun `a card bill payment is stored as a bank to credit card transfer`() =
        runSuspending {
            val w = world()

            val made = kit.transfers.create(w.draft(from = w.bank, to = w.card, amount = 12_000_00, tags = emptySet()))

            made.out.transaction.accountId shouldBe w.bank.meta.id
            made.into.transaction.accountId shouldBe w.card.meta.id
            made.legsInStore() shouldHaveSize 2
            mismatches() shouldBe 0
        }

    @Test
    fun `two transfers keep their legs apart`() =
        runSuspending {
            val w = world()

            val first = kit.transfers.create(w.draft(amount = 100_00))
            val second = kit.transfers.create(w.draft(amount = 200_00))

            first.legsInStore() shouldContainExactlyInAnyOrder listOf(first.out.transaction, first.into.transaction)
            second.legsInStore() shouldContainExactlyInAnyOrder listOf(second.out.transaction, second.into.transaction)
            kit.transactionRepository.legsOfGroup("no-such-group") shouldBe emptyList()
        }

    // All or nothing ----------------------------------------------------------------------------------

    /** Throws the way SQLite does on a constraint failure at the second line insert; the first one is written. */
    private class SecondLineFails(
        private val real: TransactionLineRepository,
        private val failOnInsert: Int = Int.MAX_VALUE,
        private val failOnUpdate: Int = Int.MAX_VALUE,
    ) : TransactionLineRepository by real {
        private var inserts = 0
        private var updates = 0

        override suspend fun insert(entity: TransactionLine) {
            if (++inserts == failOnInsert) throw SQLiteConstraintException("simulated")
            real.insert(entity)
        }

        override suspend fun update(entity: TransactionLine) {
            if (++updates == failOnUpdate) throw SQLiteConstraintException("simulated")
            real.update(entity)
        }
    }

    private fun transfersOver(lines: TransactionLineRepository) =
        TransferService(
            kit.runner,
            kit.writer,
            kit.transactionRepository,
            TransactionLines(kit.writer, lines, kit.categoryRepository, kit.tagRepository, kit.ids),
            kit.accountRepository,
            kit.ids,
        )

    @Test
    fun `a failure on the second leg's line leaves zero rows and zero audit entries`() =
        runSuspending {
            val w = world()
            kit.transfers.create(w.draft(amount = 100_00)) // other data must survive
            val before = everything()
            val auditBefore = count(TRANSFER_AUDIT)

            shouldThrow<SQLiteConstraintException> {
                transfersOver(SecondLineFails(kit.transactionLineRepository, failOnInsert = 2))
                    .create(w.draft(amount = 700_00))
            }

            everything() shouldBe before
            count(TRANSFER_AUDIT) shouldBe auditBefore
            count("SELECT COUNT(*) FROM ledger_transaction") shouldBe 2
            mismatches() shouldBe 0
            // One retry on the real stores succeeds with no duplicate.
            kit.transfers.create(w.draft(amount = 700_00))
            count("SELECT COUNT(*) FROM ledger_transaction") shouldBe 4
            count("SELECT COUNT(*) FROM transaction_line") shouldBe 4
            mismatches() shouldBe 0
        }

    /** Reports a live account for [ghost] that has no row, so core accepts it and only the foreign key refuses. */
    private class GhostAccounts(
        private val real: AccountRepository,
        private val ghost: AccountId,
        private val template: Account,
    ) : AccountRepository by real {
        override suspend fun get(id: AccountId): Account? =
            if (id == ghost) template.copy(meta = template.meta.copy(id = ghost)) else real.get(id)
    }

    @Test
    fun `a foreign key failure on the second leg itself leaves every row and audit entry as before`() =
        runSuspending {
            val w = world()
            val ghost = AccountId("no-such-account")
            val service =
                TransferService(
                    kit.runner,
                    kit.writer,
                    kit.transactionRepository,
                    TransactionLines(
                        kit.writer,
                        kit.transactionLineRepository,
                        kit.categoryRepository,
                        kit.tagRepository,
                        kit.ids,
                    ),
                    GhostAccounts(kit.accountRepository, ghost, w.bank),
                    kit.ids,
                )
            val before = everything()

            shouldThrow<SQLiteConstraintException> {
                service.create(w.draft().copy(to = ghost))
            }

            everything() shouldBe before
            count("SELECT COUNT(*) FROM ledger_transaction") shouldBe 0
        }

    // Edit --------------------------------------------------------------------------------------------

    @Test
    fun `editing the amount updates both legs and both lines, and the sum holds on each`() =
        runSuspending {
            val w = world()
            val made = kit.transfers.create(w.draft())

            val edited = kit.transfers.edit(made.groupId, w.draft(amount = 750_00))

            edited.groupId shouldBe made.groupId
            made.legsInStore().forEach { it.amount shouldBe Paise(750_00) }
            listOf(edited.out, edited.into).forEach { leg ->
                val stored = kit.transactionLineRepository.liveLinesOf(leg.transaction.meta.id)
                stored.single().amount shouldBe Paise(750_00)
            }
            kit.trailOf("ledger_transaction", made.out.transaction.meta.id.value).map { it.action } shouldBe
                listOf(AuditAction.CREATE, AuditAction.UPDATE)
            mismatches() shouldBe 0
        }

    @Test
    fun `a failure on the second leg's line update leaves both legs and both lines as before`() =
        runSuspending {
            val w = world()
            val made = kit.transfers.create(w.draft())
            val before = everything()

            shouldThrow<SQLiteConstraintException> {
                transfersOver(SecondLineFails(kit.transactionLineRepository, failOnUpdate = 2))
                    .edit(made.groupId, w.draft(amount = 750_00))
            }

            everything() shouldBe before
            mismatches() shouldBe 0
        }

    // Delete and restore of the pair --------------------------------------------------------------------

    @Test
    fun `deleting either leg tombstones both, and a restore from either leg brings both back with their lines`() =
        runSuspending {
            val w = world()
            val made = kit.transfers.create(w.draft())
            val legs = listOf(made.out, made.into)

            // Round 1 deletes from the IN leg and restores from the OUT leg; round 2 the other way round.
            listOf(made.into to made.out, made.out to made.into).forEach { (deleteFrom, restoreFrom) ->
                kit.transactions.delete(deleteFrom.transaction.meta.id)

                made.legsInStore().forEach { it.meta.isDeleted shouldBe true }
                legs.forEach { leg ->
                    kit.transactions.get(leg.transaction.meta.id) shouldBe null
                    kit.transactionLineRepository.liveLinesOf(leg.transaction.meta.id) shouldBe leg.lines
                }
                mismatches() shouldBe 0

                kit.transactions.restore(restoreFrom.transaction.meta.id)

                made.legsInStore().forEach { it.meta.isDeleted shouldBe false }
                legs.forEach { kit.transactions.get(it.transaction.meta.id) shouldBe it }
            }
            legs.forEach { leg ->
                kit.trailOf("ledger_transaction", leg.transaction.meta.id.value).map { it.action } shouldBe
                    listOf(
                        AuditAction.CREATE,
                        AuditAction.DELETE,
                        AuditAction.RESTORE,
                        AuditAction.DELETE,
                        AuditAction.RESTORE,
                    )
            }
        }

    @Test
    fun `a deleted account on either leg refuses the restore from either leg and leaves every row as before`() =
        runSuspending {
            val w = world()
            val made = kit.transfers.create(w.draft())

            // The target account first (restored afterwards so the transfer can come back), then the source.
            listOf(w.savings, w.bank).forEach { account ->
                kit.transactions.delete(made.out.transaction.meta.id)
                kit.accounts.delete(account.meta.id)
                val before = everything()

                listOf(made.out, made.into).forEach { leg ->
                    shouldThrow<LedgerError.Deleted> {
                        kit.transactions.restore(leg.transaction.meta.id)
                    }.what shouldBe "account"
                }

                everything() shouldBe before
                made.legsInStore().forEach { it.meta.isDeleted shouldBe true }
                kit.accounts.restore(account.meta.id)
                kit.transactions.restore(made.into.transaction.meta.id)
                made.legsInStore().forEach { it.meta.isDeleted shouldBe false }
            }
        }

    @Test
    fun `a transfer leg is still refused by edit and mark verified, on both legs`() =
        runSuspending {
            val w = world()
            val made = kit.transfers.create(w.draft())
            val before = everything()

            listOf(made.out, made.into).forEach { leg ->
                val expense =
                    TransactionDraft(
                        TransactionType.EXPENSE,
                        Paise(1_00),
                        at,
                        leg.transaction.accountId,
                        lines = listOf(LineDraft(null, Paise(1_00), null)),
                    )

                shouldThrow<LedgerError.IsTransfer> { kit.transactions.edit(leg.transaction.meta.id, expense) }
                shouldThrow<LedgerError.IsTransfer> { kit.transactions.markVerified(leg.transaction.meta.id) }
            }

            everything() shouldBe before
        }
}
