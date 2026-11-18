package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.Validation

/**
 * Expenses and incomes (PRD L-1, L-5, L-6, L-9, L-11): create, edit, delete, restore, mark verified
 * and read, each transaction with its lines (items). The only writer of transactions and lines, so
 * the sum invariant (the live lines add up exactly to the amount, CLAUDE.md §3.8) cannot be skipped.
 * A concrete service: it states the rules and leaves the transaction, attribution and audit trail to
 * [AuditedWriter]; the line rules live in [TransactionLines].
 *
 * **Rules** (each checked before anything is committed; a refused call writes no row and no audit
 * entry, because the transaction and its lines are written in one `runner.inTransaction`):
 * - the draft passes [TransactionDraft.validated]: expense or income only ([LedgerError.IsTransfer]),
 *   a positive amount within the limit, bounded optional texts, and lines that split the amount
 *   exactly ([Breakdown]);
 * - the account must be live when it is set or changed; a category must be live when it is set or
 *   changed, and of the transaction's kind always; an added tag must be live (IT §3.3 rule 3);
 * - a manual entry is [TxnSource.MANUAL] and [TxnState.VERIFIED] (L-6); the direction follows the type;
 * - an edit keeps the state, source and message facts, refuses a transfer leg ([LedgerError.IsTransfer]),
 *   and audits only the fields that changed (an edit that changes nothing writes nothing);
 * - delete and restore act on the transaction only: its lines stay as they are, so a restore brings
 *   back exactly the lines it had (IT §3.3 rule 7).
 * - a transfer leg is refused by every call here ([LedgerError.IsTransfer]): one leg must never be
 *   edited, deleted, restored or verified without the other. The transfer service (C2) acts on both.
 *
 * **One line in C1a.** Create and edit take exactly one line until itemising (C1b) adds several.
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
     * Creates a manual expense or income from [draft], with its line, and audits a CREATE of each.
     *
     * @return the transaction and its line.
     * @throws LedgerError.IsTransfer for a transfer.
     * @throws LedgerError the amount, text and line errors of [TransactionDraft.validated].
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for the account,
     *   the category or a tag; [LedgerError.NotFound] (`transaction_line`) if the line names an id.
     * @throws LedgerError.KindMismatch if the category is not of the transaction's kind.
     * @throws LedgerError.NotInitialised before first-run setup.
     * @throws IllegalArgumentException for more than one line (until C1b).
     */
    suspend fun create(draft: TransactionDraft): TransactionWithLines {
        val valid = draft.validated()
        val only = valid.onlyLine()
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
            TransactionWithLines(txn, listOf(lines.createFirst(txn, only)))
        }
    }

    /**
     * Replaces the payment facts and the line of transaction [id] with [draft] and audits an UPDATE
     * of each row whose fields changed. Expense and income may swap when the category matches the new
     * kind. The state, source and message facts are kept.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted transaction.
     * @throws LedgerError.IsTransfer for a transfer leg, or a draft that would make one.
     * @throws LedgerError.NotFound (`transaction_line`) unless the draft line names the transaction's line.
     * @throws LedgerError the validation and reference errors of [create], for what changed.
     * @throws IllegalArgumentException for more than one line (until C1b).
     */
    suspend fun edit(
        id: TransactionId,
        draft: TransactionDraft,
    ): TransactionWithLines {
        val valid = draft.validated()
        val only = valid.onlyLine()
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
            TransactionWithLines(txn, listOf(lines.updateOnly(txn, only)))
        }
    }

    /**
     * Deletes transaction [id] as a tombstone (audited DELETE); its lines are left as they are and
     * hidden with it. Already deleted is a no-op.
     *
     * @throws LedgerError.IsTransfer for a transfer leg: deleting one leg would leave half a transfer.
     * @throws LedgerError.NotFound for an unknown transaction.
     */
    suspend fun delete(id: TransactionId): LedgerTransaction = notTransfer(id) { writer.delete(transactions, id) }

    /**
     * Brings deleted transaction [id] back (audited RESTORE), with the lines it had. Not deleted is a
     * no-op.
     *
     * @throws LedgerError.IsTransfer for a transfer leg.
     * @throws LedgerError.NotFound for an unknown transaction.
     */
    suspend fun restore(id: TransactionId): LedgerTransaction = notTransfer(id) { writer.restore(transactions, id) }

    /**
     * Marks transaction [id] as confirmed by the user (L-6): an audited UPDATE of `state`. Already
     * verified is a no-op.
     *
     * @throws LedgerError.IsTransfer for a transfer leg.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted transaction.
     */
    suspend fun markVerified(id: TransactionId): LedgerTransaction =
        notTransfer(id) { writer.update(transactions, id) { it.copy(state = TxnState.VERIFIED) } }

    /**
     * Transaction [id] with its live lines (by position, then id), or null if it is unknown or
     * deleted. Both are read in one transaction, so the amount and the lines come from the same state.
     */
    suspend fun get(id: TransactionId): TransactionWithLines? =
        runner.inTransaction {
            transactions.getLive(id)?.let { TransactionWithLines(it, lines.liveLinesOf(id)) }
        }

    /**
     * Runs [write] on transaction [id] unless it is a transfer leg, in one transaction with the check.
     * Transfer legs are refused with [LedgerError.IsTransfer], so these calls never act on one leg alone.
     */
    private suspend fun notTransfer(
        id: TransactionId,
        write: suspend () -> LedgerTransaction,
    ): LedgerTransaction =
        runner.inTransaction {
            // C2: TransferService deletes, restores and verifies both legs of a transfer together.
            if (transactions.get(id)?.type == TransactionType.TRANSFER) throw LedgerError.IsTransfer()
            write()
        }

    private companion object {
        /** The kind of record, for errors. */
        const val ACCOUNT = "account"

        /** An expense takes money out of the account, an income brings it in. */
        fun directionOf(type: TransactionType): Direction =
            if (type == TransactionType.INCOME) Direction.IN else Direction.OUT
    }
}

/**
 * The draft's one line. C1b: itemising replaces this with the reconciliation of several lines, and
 * the refusal goes. Until then more than one line fails closed, before anything is written.
 */
private fun TransactionDraft.onlyLine(): LineDraft {
    // C1b: remove with the reconciliation (IT §3.3 rule 4).
    require(lines.size == 1) { "Only one item per transaction is supported yet" }
    return lines.first()
}
