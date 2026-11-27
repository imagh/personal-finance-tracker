package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation

/**
 * Expenses and incomes (PRD L-1, L-5, L-6, L-9, L-11): create, edit, delete, restore, mark verified
 * and read, each transaction with its lines (items). Together with [TransferService]
 * the only writer of transactions and lines, both through [TransactionLines], so the sum invariant (the live
 * lines add up exactly to the amount, CLAUDE.md §3.8) cannot be skipped. Delete and restore also act on both
 * legs of a transfer.
 * A concrete service: it states the rules and leaves the transaction, attribution and audit trail to
 * [AuditedWriter]; the line rules live in [TransactionLines].
 *
 * **Rules** (each checked before anything is committed; a refused call writes no row and no audit
 * entry, because the transaction and its lines are written in one `runner.inTransaction`):
 * - the draft passes [TransactionDraft.validated]: expense or income only ([LedgerError.IsTransfer]),
 *   a positive amount within the limit, bounded optional texts, and lines that split the amount
 *   exactly ([Breakdown]);
 * - the lines ([TransactionLines]): one to [LedgerLimits.MAX_LINES_PER_TRANSACTION], their amounts adding
 *   up exactly to the amount, matched to the stored lines by id on an edit (a draft id that is not a
 *   live line of this transaction is `NotFound("transaction_line")`), positions renumbered in draft
 *   order, a removed line tombstoned; the sum is re-read before commit and a mismatch fails closed
 *   with [IllegalStateException] (IT §3.3 rules 4 and 5);
 * - the account must be live when it is set or changed; a category must be live when it is set or
 *   changed, and of the transaction's kind always; an added tag must be live (IT §3.3 rule 3);
 * - a manual entry is [TxnSource.MANUAL] and [TxnState.VERIFIED] (L-6); the direction follows the type;
 * - an edit keeps the state, source and message facts, refuses a transfer leg ([LedgerError.IsTransfer]),
 *   and audits only the fields that changed (an edit that changes nothing writes nothing);
 * - delete and restore act on the transaction row only: its lines stay as they are, so a restore brings
 *   back exactly the lines it had (IT §3.3 rule 7). A restore is refused ([LedgerError.Deleted]) when its
 *   account, or the category or tag of a live line, was deleted since: deleting a transaction frees them
 *   to be deleted, as a sub-category is not restored under a deleted parent. Archived ones are fine.
 * - a transfer leg is never acted on alone: delete and restore act on **both legs** in one transaction (a
 *   restore is refused when either leg's account was deleted), while edit and verify are refused with
 *   [LedgerError.IsTransfer]. A transfer is created and edited by [TransferService].
 *
 * Nothing here logs or puts user text in an error (CLAUDE.md §3.7).
 *
 * @param runner the transaction each write runs in.
 * @param writer the audited write path.
 * @param transactions the transaction store.
 * @param lines the line rules and store.
 * @param accounts looked up to check the account.
 * @param ids source of new transaction ids.
 */
class TransactionService(
    private val runner: TransactionRunner,
    private val writer: AuditedWriter,
    private val transactions: TransactionRepository,
    private val lines: TransactionLines,
    private val accounts: AccountRepository,
    private val ids: Uuid7,
) {
    /**
     * Creates a manual expense or income from [draft], with its lines, and audits a CREATE of each.
     *
     * @return the transaction and its lines.
     * @throws LedgerError.IsTransfer for a transfer.
     * @throws LedgerError the amount, text and line errors of [TransactionDraft.validated].
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for the account,
     *   the category or a tag; [LedgerError.NotFound] (`transaction_line`) if a line names an id.
     * @throws LedgerError.KindMismatch if a line's category is not of the transaction's kind.
     * @throws LedgerError.NotInitialised before first-run setup.
     * @throws IllegalStateException if the stored lines do not add up to the amount (fails closed).
     */
    suspend fun create(draft: TransactionDraft): TransactionWithLines {
        val valid = draft.validated()
        return runner.inTransaction {
            Validation.requireLive(accounts.get(valid.accountId), ACCOUNT)
            val txn =
                writer.create(transactions, TransactionId(ids.next())) { meta ->
                    LedgerTransaction(
                        meta = meta,
                        type = valid.type,
                        direction = directionOf(valid.type),
                        amount = valid.amount,
                        occurredAt = valid.occurredAt,
                        accountId = valid.accountId,
                        title = valid.title,
                        description = valid.description,
                        state = TxnState.VERIFIED,
                        source = TxnSource.MANUAL,
                        bankRef = valid.bankRef,
                        sourceMessageId = null,
                        accountFragment = null,
                        transferGroupId = null,
                    )
                }
            TransactionWithLines(txn, lines.create(txn, valid.lines))
        }
    }

    /**
     * Replaces the payment facts and the lines of transaction [id] with [draft] and audits each row
     * written: an UPDATE of the fields that changed, a CREATE of an added line, a DELETE of a removed
     * one. Expense and income may swap when every category matches the new kind. The state, source and
     * message facts are kept, so itemising an auto-captured transaction changes lines only.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted transaction.
     * @throws LedgerError.IsTransfer for a transfer leg, or a draft that would make one.
     * @throws LedgerError.NotFound (`transaction_line`) for a draft line id that is not one of its live lines.
     * @throws LedgerError the validation and reference errors of [create], for what changed.
     * @throws IllegalStateException if the stored lines do not add up to the amount (fails closed).
     */
    suspend fun edit(
        id: TransactionId,
        draft: TransactionDraft,
    ): TransactionWithLines {
        val valid = draft.validated()
        return runner.inTransaction {
            val txn =
                writer.update(transactions, id) { old ->
                    if (old.type == TransactionType.TRANSFER) throw LedgerError.IsTransfer()
                    if (valid.accountId != old.accountId) Validation.requireLive(accounts.get(valid.accountId), ACCOUNT)
                    old.copy(
                        type = valid.type,
                        direction = directionOf(valid.type),
                        amount = valid.amount,
                        occurredAt = valid.occurredAt,
                        accountId = valid.accountId,
                        title = valid.title,
                        description = valid.description,
                        bankRef = valid.bankRef,
                    )
                }
            TransactionWithLines(txn, lines.reconcile(txn, valid.lines))
        }
    }

    /**
     * Deletes transaction [id] as a tombstone (audited DELETE); its lines are left as they are and
     * hidden with it. Already deleted is a no-op. A transfer leg deletes **both legs** in one transaction
     * (audited DELETE of each): half a transfer must never be left behind.
     *
     * @return the transaction [id] as now stored (for a transfer, the leg asked for).
     * @throws LedgerError.IsTransfer for a transfer leg with no group id (its other leg cannot be found).
     * @throws LedgerError.NotFound for an unknown transaction.
     * @throws IllegalStateException for a transfer whose group is not exactly one OUT and one IN leg.
     */
    suspend fun delete(id: TransactionId): LedgerTransaction =
        runner.inTransaction {
            legsWith(id).map { writer.delete(transactions, it.meta.id) }.first { it.meta.id == id }
        }

    /**
     * Brings deleted transaction [id] back (audited RESTORE), with the lines it had. Not deleted is a
     * no-op. Deleting a transaction frees its account, categories and tags to be deleted, so the
     * restore first checks that none of them was deleted since; archived ones are fine (D4, Q4). A
     * transfer leg restores **both legs** in one transaction, and the checks cover both: a deleted
     * account on either leg refuses the whole restore.
     *
     * @return the transaction [id] as now stored (for a transfer, the leg asked for).
     * @throws LedgerError.IsTransfer for a transfer leg with no group id.
     * @throws LedgerError.NotFound for an unknown transaction.
     * @throws LedgerError.Deleted (`account`, `category` or `tag`) if its account, or a category or tag of
     *   one of its live lines, was deleted since; nothing is written.
     */
    suspend fun restore(id: TransactionId): LedgerTransaction =
        runner.inTransaction {
            val legs = legsWith(id)
            legs.filter { it.meta.isDeleted }.forEach {
                Validation.requireNotDeleted(accounts.get(it.accountId), ACCOUNT)
                lines.requireReferencesNotDeleted(it.meta.id)
            }
            legs.map { writer.restore(transactions, it.meta.id) }.first { it.meta.id == id }
        }

    /**
     * Marks transaction [id] as confirmed by the user (L-6): an audited UPDATE of `state`. Already
     * verified is a no-op. A transfer leg is refused: a manual transfer is verified when it is made, and a
     * captured one is revisited with the M1b captures.
     *
     * @throws LedgerError.IsTransfer for a transfer leg.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted transaction.
     */
    suspend fun markVerified(id: TransactionId): LedgerTransaction =
        runner.inTransaction {
            if (transactions.get(id)?.type == TransactionType.TRANSFER) throw LedgerError.IsTransfer()
            writer.update(transactions, id) { it.copy(state = TxnState.VERIFIED) }
        }

    /**
     * Transaction [id] with its live lines (by position, then id), or null if it is unknown or
     * deleted. Both are read in one transaction, so the amount and the lines come from the same state.
     */
    suspend fun get(id: TransactionId): TransactionWithLines? =
        runner.inTransaction {
            transactions.getLive(id)?.let { TransactionWithLines(it, lines.liveLinesOf(id)) }
        }

    /**
     * Transaction [id] and, if it is a transfer leg, the other leg of its group (both in the one list, in
     * no meaningful order): what delete and restore act on together. Tombstones are included.
     *
     * @throws IllegalStateException if the group is not exactly one OUT and one IN transfer leg (fails closed:
     *   nothing is deleted or restored on a malformed group).
     */
    private suspend fun legsWith(id: TransactionId): List<LedgerTransaction> {
        val txn = transactions.get(id) ?: throw LedgerError.NotFound(transactions.entityType)
        if (txn.type != TransactionType.TRANSFER) return listOf(txn)
        val group = txn.transferGroupId ?: throw LedgerError.IsTransfer()
        val (out, into) = transactions.legsOfGroup(group).asTransferLegs()
        return listOf(out, into)
    }

    private companion object {
        /** The kind of record, for errors. */
        const val ACCOUNT = "account"

        /** An expense takes money out of the account, an income brings it in. */
        fun directionOf(type: TransactionType): Direction =
            if (type == TransactionType.INCOME) Direction.IN else Direction.OUT
    }
}
