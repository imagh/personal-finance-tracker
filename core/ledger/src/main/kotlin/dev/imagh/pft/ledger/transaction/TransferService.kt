package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.account.AccountRepository
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.Validation

/**
 * Money moved between two of the user's own accounts, such as a card bill payment (a transfer from a bank
 * account to a credit card). A transfer is two linked transactions, an OUT leg on the source and an IN leg on
 * the target, that share a `transferGroupId`; it is not spending, so it has no category and one line per leg.
 *
 * A concrete service beside [TransactionService], sharing [TransactionLines] for the line rules (slice C D3).
 *
 * **Rules** (checked before anything is committed; a refused call writes no row and no audit entry):
 * - the draft passes [TransferDraft.validated]: two different accounts ([LedgerError.SameAccount]), a
 *   positive amount within the limit, bounded optional texts;
 * - each account must be live when it is set or changed, and each tag must be live when it is added;
 * - both legs have the same amount, time, title and description; each has exactly one uncategorised line of
 *   that amount, with the same tags (IT §3.3 rule 2, [LedgerError.TransferCannotBeItemised]);
 * - a manual transfer is [TxnSource.MANUAL] and [TxnState.VERIFIED];
 * - **all or nothing**: both legs, both lines and every audit entry of one call are written in one
 *   `runner.inTransaction`, so a failure on the second leg leaves nothing of the first;
 * - an edit keeps the group, the directions and the line ids, audits only what changed, and is refused for a
 *   deleted transfer as an edit of a deleted transaction is. A group that is not exactly one OUT and one IN
 *   leg fails closed ([IllegalStateException]).
 *
 * Deleting and restoring a transfer go through [TransactionService], which acts on both legs. Nothing here
 * logs or puts user text in an error (CLAUDE.md §3.7).
 *
 * @param runner the transaction each write runs in.
 * @param writer the audited write path.
 * @param transactions the transaction store.
 * @param lines the line rules and store.
 * @param accounts looked up to check the accounts.
 * @param ids source of new transaction and group ids.
 */
class TransferService(
    private val runner: TransactionRunner,
    private val writer: AuditedWriter,
    private val transactions: TransactionRepository,
    private val lines: TransactionLines,
    private val accounts: AccountRepository,
    private val ids: Uuid7,
) {
    /**
     * Creates a manual transfer from [draft]: an OUT leg on the source account and an IN leg on the target,
     * one group, one uncategorised line each, with four audited CREATEs.
     *
     * @return the two legs with their lines.
     * @throws LedgerError.SameAccount if the accounts are the same.
     * @throws LedgerError the amount and text errors of [TransferDraft.validated].
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] for an account or a tag.
     * @throws LedgerError.NotInitialised before first-run setup.
     */
    suspend fun create(draft: TransferDraft): Transfer {
        val valid = draft.validated()
        return runner.inTransaction {
            Validation.requireLive(accounts.get(valid.from), ACCOUNT)
            Validation.requireLive(accounts.get(valid.to), ACCOUNT)
            val groupId = ids.next()
            val out = createLeg(groupId, Direction.OUT, valid.from, valid)
            val into = createLeg(groupId, Direction.IN, valid.to, valid)
            Transfer(groupId, out, into)
        }
    }

    /**
     * Replaces the amount, time, texts, accounts and tags of the transfer [groupId] with [draft], on both
     * legs and both lines, and audits each row that changed. The group, the directions and the line ids stay.
     *
     * @return the two legs with their lines.
     * @throws LedgerError.NotFound for an unknown group.
     * @throws LedgerError.Deleted for a deleted transfer.
     * @throws LedgerError.SameAccount / [LedgerError] the validation and reference errors of [create], for
     *   what changed.
     * @throws IllegalStateException if the group is not exactly one OUT and one IN transfer leg, or a leg does
     *   not have exactly one live line (fails closed).
     */
    suspend fun edit(
        groupId: String,
        draft: TransferDraft,
    ): Transfer {
        val valid = draft.validated()
        return runner.inTransaction {
            val legs = transactions.legsOfGroup(groupId)
            if (legs.isEmpty()) throw LedgerError.NotFound(transactions.entityType)
            val (out, into) = legs.asTransferLegs()
            Transfer(groupId, editLeg(out.meta.id, valid.from, valid), editLeg(into.meta.id, valid.to, valid))
        }
    }

    /**
     * The live transfer [groupId] with both legs and their lines, or null if it is unknown or deleted: what an
     * edit form opens a stored transfer from. Both legs and their lines are read in one transaction, so they
     * come from the same state. Reads only: nothing is written or logged.
     *
     * @throws IllegalStateException if the group's live legs are not exactly one OUT and one IN transfer leg,
     *   or a leg does not have exactly one live line (fails closed: half a transfer is never shown for editing).
     */
    suspend fun get(groupId: String): Transfer? =
        runner.inTransaction {
            val live = transactions.legsOfGroup(groupId).filterNot { it.meta.isDeleted }
            if (live.isEmpty()) return@inTransaction null
            val (out, into) = live.asTransferLegs()
            Transfer(groupId, withLine(out), withLine(into))
        }

    /** [leg] with its one live line. */
    private suspend fun withLine(leg: LedgerTransaction): TransactionWithLines {
        val live = lines.liveLinesOf(leg.meta.id)
        check(live.size == 1) { "A transfer leg must have exactly one item" }
        return TransactionWithLines(leg, live)
    }

    /** Writes one leg on [accountId] and its single uncategorised line, which carries the whole amount. */
    private suspend fun createLeg(
        groupId: String,
        direction: Direction,
        accountId: AccountId,
        draft: TransferDraft,
    ): TransactionWithLines {
        val txn =
            writer.create(transactions, TransactionId(ids.next())) { meta ->
                LedgerTransaction(
                    meta = meta,
                    type = TransactionType.TRANSFER,
                    direction = direction,
                    amount = draft.amount,
                    occurredAt = draft.occurredAt,
                    accountId = accountId,
                    title = draft.title,
                    description = draft.description,
                    state = TxnState.VERIFIED,
                    source = TxnSource.MANUAL,
                    bankRef = null,
                    sourceMessageId = null,
                    accountFragment = null,
                    transferGroupId = groupId,
                )
            }
        return TransactionWithLines(txn, lines.create(txn, listOf(legLine(draft))))
    }

    /** Updates leg [id] to [draft] on [accountId] and its one line to the same amount and tags. */
    private suspend fun editLeg(
        id: TransactionId,
        accountId: AccountId,
        draft: TransferDraft,
    ): TransactionWithLines {
        val txn =
            writer.update(transactions, id) { old ->
                if (accountId != old.accountId) Validation.requireLive(accounts.get(accountId), ACCOUNT)
                old.copy(
                    amount = draft.amount,
                    occurredAt = draft.occurredAt,
                    accountId = accountId,
                    title = draft.title,
                    description = draft.description,
                )
            }
        val live = lines.liveLinesOf(id)
        check(live.size == 1) { "A transfer leg must have exactly one item" }
        return TransactionWithLines(txn, lines.reconcile(txn, listOf(legLine(draft).copy(id = live.first().meta.id))))
    }

    private companion object {
        /** The kind of record, for errors. */
        const val ACCOUNT = "account"

        /** The one line of a leg: the whole amount, no category, the draft's tags. */
        fun legLine(draft: TransferDraft) = LineDraft(amount = draft.amount, categoryId = null, tagIds = draft.tagIds)
    }
}
