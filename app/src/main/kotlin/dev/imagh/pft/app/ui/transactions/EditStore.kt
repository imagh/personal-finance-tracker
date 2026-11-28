package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionService
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.ledger.transaction.TransferService

/**
 * A stored transaction as the form edits it: one item, or a transfer's two legs shown as one.
 *
 * @property transaction the transaction (a transfer's outgoing leg).
 * @property line its one item; it keeps its id and note when the form saves.
 * @property groupId a transfer's group id, or null for an expense or income.
 * @property toAccountId a transfer's destination (the incoming leg's account), or null.
 */
internal class StoredTransaction(
    val transaction: LedgerTransaction,
    val line: TransactionLine,
    val groupId: String?,
    val toAccountId: AccountId?,
)

/** What the form saves: an expense or income, or a transfer. */
internal sealed interface EditDraft {
    /** An expense or income with one item. */
    class Plain(
        val draft: TransactionDraft,
    ) : EditDraft

    /** A transfer between two accounts. */
    class Move(
        val draft: TransferDraft,
    ) : EditDraft
}

/**
 * The form's way to the transaction and transfer services: it reads the stored transaction to edit and writes
 * what the form made. One class so the ViewModel stays within the constructor-size limit. It adds no rules of
 * its own; core validates every write, and nothing here logs.
 *
 * @param transactions the expense and income service.
 * @param transfers the transfer service.
 */
internal class EditStore(
    private val transactions: TransactionService,
    private val transfers: TransferService,
) {
    /**
     * The transaction [raw] (**untrusted text**) as the form can edit it, or null when there is nothing to edit:
     * a blank, unknown or deleted id, a transfer whose other leg cannot be found, or an itemised transaction (more
     * than one item cannot be shown here and must not be flattened; the items editor, C4c, edits those). A
     * transfer opens from either leg's id. Reads are not caught: a corrupt row fails loudly.
     */
    suspend fun read(raw: String): StoredTransaction? {
        // A blank id cannot be an id (and `TransactionId` refuses it); anything else is only looked up.
        val found = if (raw.isBlank()) null else transactions.get(TransactionId(raw))
        return when {
            found == null -> null
            found.transaction.type == TransactionType.TRANSFER -> readTransfer(found.transaction)
            else -> found.lines.singleOrNull()?.let { StoredTransaction(found.transaction, it, null, null) }
        }
    }

    private suspend fun readTransfer(leg: LedgerTransaction): StoredTransaction? =
        leg.transferGroupId?.let { group ->
            transfers.get(group)?.let {
                StoredTransaction(it.out.transaction, it.out.lines.single(), group, it.into.transaction.accountId)
            }
        }

    /** Writes [draft]: a new transaction when [stored] is null, else the edit of [stored]. */
    suspend fun save(
        draft: EditDraft,
        stored: StoredTransaction?,
    ) {
        when (draft) {
            is EditDraft.Plain ->
                if (stored == null) {
                    transactions.create(draft.draft)
                } else {
                    transactions.edit(stored.transaction.meta.id, draft.draft)
                }
            is EditDraft.Move -> {
                val group = stored?.groupId
                if (group == null) transfers.create(draft.draft) else transfers.edit(group, draft.draft)
            }
        }
    }
}
