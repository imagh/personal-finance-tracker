package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.transaction.TransactionRepository
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation

/**
 * Accounts (PRD L-4): create, edit, archive, delete and list. A concrete service: it states the
 * account's rules and leaves the transaction, attribution and audit trail to [AuditedWriter].
 *
 * **Rules** (checked before anything is written, so a refused call changes nothing):
 * - a required, trimmed name of at most [LedgerLimits.MAX_NAME_LENGTH] characters (names need not be
 *   unique);
 * - an opening balance of any sign, at most [LedgerLimits.MAX_AMOUNT] in size
 *   ([Validation.requireBalance]);
 * - an optional last-four identifier ([AccountRules.requireMaskedIdentifier]) and an optional issuer;
 *   blank means none and is stored as null.
 *
 * **Lifecycle.** Archive hides an account from new entries but keeps it in the list's archived
 * section. Delete is a tombstone and [restore] undoes it (INT-3); it is refused while a live
 * transaction is on the account ([LedgerError.InUse]), so the user archives it instead (L-2; archiving
 * never touches history). Each call is idempotent and audited.
 *
 * Nothing here logs or puts user text in an error (CLAUDE.md §3.7).
 *
 * @param runner the transaction the in-use check and the delete it guards run in.
 * @param writer the audited write path.
 * @param repository the account store.
 * @param transactions asked whether a live transaction uses an account.
 * @param ids source of new account ids.
 */
class AccountService(
    private val runner: TransactionRunner,
    private val writer: AuditedWriter,
    private val repository: AccountRepository,
    private val transactions: TransactionRepository,
    private val ids: Uuid7,
) {
    /**
     * Creates an account from [draft] and audits a CREATE.
     *
     * @throws LedgerError.BlankName / [LedgerError.NameTooLong] for the name or the issuer.
     * @throws LedgerError.InvalidMaskedIdentifier for a last four that is not four ASCII digits.
     * @throws LedgerError.AmountTooLarge if the opening balance is out of bounds.
     * @throws LedgerError.NotInitialised before first-run setup.
     */
    suspend fun create(draft: AccountDraft): Account {
        val valid = checked(draft)
        return writer.create(repository, AccountId(ids.next())) { meta ->
            Account(meta, valid.name, valid.type, valid.openingBalance, valid.maskedIdentifier, valid.issuer)
        }
    }

    /**
     * Replaces the editable fields of account [id] with [draft] and audits an UPDATE listing only the
     * fields that changed; an edit that changes nothing writes nothing. Works on an archived account.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted account.
     * @throws LedgerError same validation errors as [create].
     */
    suspend fun edit(
        id: AccountId,
        draft: AccountDraft,
    ): Account {
        val valid = checked(draft)
        return writer.update(repository, id) {
            it.copy(
                name = valid.name,
                type = valid.type,
                openingBalance = valid.openingBalance,
                maskedIdentifier = valid.maskedIdentifier,
                issuer = valid.issuer,
            )
        }
    }

    /** Archives account [id] (audited ARCHIVE); already archived is a no-op. */
    suspend fun archive(id: AccountId): Account = writer.archive(repository, id)

    /** Makes archived account [id] active again (audited UNARCHIVE); not archived is a no-op. */
    suspend fun unarchive(id: AccountId): Account = writer.unarchive(repository, id)

    /**
     * Deletes account [id] as a tombstone (audited DELETE); already deleted is a no-op. The check and
     * the delete run in one transaction, so a transaction saved meanwhile cannot slip between them.
     *
     * @throws LedgerError.InUse while a live transaction is on the account (archive it instead);
     *   nothing is written.
     * @throws LedgerError.NotFound for an unknown account.
     */
    suspend fun delete(id: AccountId): Account =
        runner.inTransaction {
            if (transactions.isAccountInUse(id)) throw LedgerError.InUse(WHAT)
            writer.delete(repository, id)
        }

    /** Brings a deleted account back (audited RESTORE); not deleted is a no-op. */
    suspend fun restore(id: AccountId): Account = writer.restore(repository, id)

    /**
     * The accounts to offer for new transactions: not deleted, not archived, sorted by name ignoring
     * case, then by creation order (ids are time-ordered), so accounts with the same name never swap.
     */
    suspend fun active(): List<Account> = all().filter { it.meta.isActive }

    /** Every account that is not deleted, archived ones included, in the same order as [active]. */
    suspend fun all(): List<Account> =
        repository.allLive().sortedWith(compareBy<Account>({ it.name.lowercase() }, { it.meta.id.value }))

    /** [active] and the archived accounts, for a list screen. */
    suspend fun lists(): AccountLists {
        val (archived, active) = all().partition { it.meta.isArchived }
        return AccountLists(active, archived)
    }

    /** [draft] with every field validated and normalised (trimmed, blank optionals as null). */
    private fun checked(draft: AccountDraft) =
        AccountDraft(
            name = Validation.requireName(draft.name, LedgerLimits.MAX_NAME_LENGTH),
            type = draft.type,
            openingBalance = Validation.requireBalance(draft.openingBalance),
            maskedIdentifier = AccountRules.requireMaskedIdentifier(draft.maskedIdentifier),
            issuer = Validation.requireOptionalName(draft.issuer, LedgerLimits.MAX_NAME_LENGTH),
        )

    private companion object {
        /** The kind of record, for errors (never a name). */
        const val WHAT = "account"
    }
}
