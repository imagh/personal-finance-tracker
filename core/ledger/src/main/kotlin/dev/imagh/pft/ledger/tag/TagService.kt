package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation

/**
 * Tags (PRD L-3): create, rename, archive, delete and list. A concrete service: it states the tags'
 * rules and leaves the transaction, attribution and audit trail to [AuditedWriter].
 *
 * **Rules** (each is checked before anything is written, so a refused call changes nothing, and the
 * name check runs in the same transaction as the write it guards, so two saves cannot both pass it):
 * - a required, trimmed name of at most [LedgerLimits.MAX_NAME_LENGTH] characters;
 * - names are unique among the live tags, ignoring case ([Validation.nameKey]), or the call is refused
 *   with [LedgerError.DuplicateName]. Archived tags count, deleted ones do not. [restore] checks again,
 *   so bringing a tag back cannot create a clash. A rename to a case change of the tag's own name is
 *   fine.
 *
 * **Lifecycle.** Archive hides a tag from new entries but keeps it in the list's archived section and
 * on the items that already carry it. Delete is a tombstone and [restore] undoes it (INT-3). Each call
 * is idempotent and audited. There is no "in use by a transaction item" check on delete yet: it arrives
 * with transaction items (slice C).
 *
 * Nothing here logs or puts user text in an error (CLAUDE.md §3.7): [LedgerError.DuplicateName] names
 * the kind of record (`tag`), never the name.
 *
 * @param runner the transaction the check-then-write sequences run in.
 * @param writer the audited write path.
 * @param repository the tag store.
 * @param ids source of new tag ids.
 */
class TagService(
    private val runner: TransactionRunner,
    private val writer: AuditedWriter,
    private val repository: TagRepository,
    private val ids: Uuid7,
) {
    /**
     * Creates a tag named [name] (trimmed) and audits a CREATE.
     *
     * @throws LedgerError.BlankName / [LedgerError.NameTooLong] for the name.
     * @throws LedgerError.DuplicateName if a live tag (archived included) already has this name.
     * @throws LedgerError.NotInitialised before first-run setup.
     */
    suspend fun create(name: String): Tag {
        val valid = Validation.requireName(name, LedgerLimits.MAX_NAME_LENGTH)
        return runner.inTransaction {
            if (repository.allLive().isNameTaken(valid)) throw LedgerError.DuplicateName(WHAT)
            writer.create(repository, TagId(ids.next())) { meta -> Tag(meta, valid) }
        }
    }

    /**
     * Renames tag [id] to [name] (trimmed) and audits an UPDATE; a rename that changes nothing writes
     * nothing. Works on an archived tag.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted tag.
     * @throws LedgerError.BlankName / [LedgerError.NameTooLong] for the name.
     * @throws LedgerError.DuplicateName if another live tag has the name (a case-only change to its own
     *   name is fine).
     */
    suspend fun edit(
        id: TagId,
        name: String,
    ): Tag {
        val valid = Validation.requireName(name, LedgerLimits.MAX_NAME_LENGTH)
        // The check runs inside the writer's transaction, against the tag as it is stored now.
        return writer.update(repository, id) { old ->
            if (repository.allLive().isNameTaken(valid, except = old.meta.id)) throw LedgerError.DuplicateName(WHAT)
            old.copy(name = valid)
        }
    }

    /**
     * Archives tag [id] (audited ARCHIVE): it drops out of [pickable] but keeps its name and stays in
     * history. Already archived is a no-op.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted tag.
     */
    suspend fun archive(id: TagId): Tag = writer.archive(repository, id)

    /** Makes archived tag [id] available again (audited UNARCHIVE); not archived is a no-op. */
    suspend fun unarchive(id: TagId): Tag = writer.unarchive(repository, id)

    /**
     * Deletes tag [id] as a tombstone (audited DELETE); already deleted is a no-op.
     *
     * @throws LedgerError.NotFound for an unknown tag.
     */
    suspend fun delete(id: TagId): Tag = writer.delete(repository, id)

    /**
     * Brings a deleted tag back (audited RESTORE); not deleted is a no-op. It comes back as it was,
     * archived or not.
     *
     * @throws LedgerError.DuplicateName if a live tag has taken its name since.
     * @throws LedgerError.NotFound for an unknown tag.
     */
    suspend fun restore(id: TagId): Tag =
        runner.inTransaction {
            val tag = repository.get(id)
            if (tag != null && tag.meta.isDeleted && repository.allLive().isNameTaken(tag.name)) {
                throw LedgerError.DuplicateName(WHAT)
            }
            writer.restore(repository, id)
        }

    /**
     * The tags to offer for a new item: not deleted, not archived, in the same order as
     * [TagLists.active].
     */
    suspend fun pickable(): List<Tag> = lists().active

    /** The live tags split into active and archived, each sorted by name ignoring case, then by id. */
    suspend fun lists(): TagLists {
        val (archived, active) = repository.allLive().sortedWith(ORDER).partition { it.meta.isArchived }
        return TagLists(active, archived)
    }

    private companion object {
        /** The kind of record, for errors (never a name). */
        const val WHAT = "tag"

        /** Name ignoring case, then id (time-ordered), so the order is total and never the repository's. */
        val ORDER: Comparator<Tag> = compareBy<Tag>({ Validation.nameKey(it.name) }, { it.meta.id.value })
    }
}

/** True if a tag other than [except] (the one being renamed) already has [name] by [Validation.nameKey]. */
private fun List<Tag>.isNameTaken(
    name: String,
    except: TagId? = null,
): Boolean {
    val key = Validation.nameKey(name)
    return any { it.meta.id != except && Validation.nameKey(it.name) == key }
}
