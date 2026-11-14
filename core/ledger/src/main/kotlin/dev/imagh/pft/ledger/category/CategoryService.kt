package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError

/**
 * Categories (PRD L-2): create, edit, reorder, archive, delete and the list a picker offers. A
 * concrete service: it states the categories' rules and leaves the transaction, attribution and
 * audit trail to [AuditedWriter]. The tree, sorting and sibling checks are plain functions in
 * [CategoryTree] over the repository's `allLive()` list, so there is no tree service and no query
 * per rule.
 *
 * **Rules** (each is checked before anything is written, so a refused call changes nothing, and each
 * check runs in the same transaction as the write it guards, so two saves cannot both pass it):
 * - **Two levels.** A sub-category's parent must exist, be live (not deleted, not archived) and be
 *   top-level ([LedgerError.DepthExceeded]) and of the same kind ([LedgerError.KindMismatch]).
 * - **Kind and parent are fixed at creation.** [edit] changes only name, colour and icon.
 * - **Names** are trimmed, 1 to [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_NAME_LENGTH] characters, and
 *   unique among the live siblings (same parent and kind) ignoring case ([LedgerError.DuplicateName]);
 *   archived siblings count, deleted ones do not. [restore] checks again.
 * - **Icons** must be in the [CategoryIcon] grammar ([LedgerError.InvalidIcon]).
 * - **Order.** A new category goes last among its siblings; [reorder] rewrites the positions.
 * - **System categories** ([SystemCategory], `systemKey != null`) can be edited but never archived or
 *   deleted ([LedgerError.SystemCategoryProtected]).
 * - **Delete** is a tombstone, refused while the category has live children, archived ones included
 *   ([LedgerError.HasChildren]); there is no cascade. [restore] undoes it (INT-3).
 *
 * Each lifecycle call is idempotent and audited. There is no "in use by a transaction" check on
 * delete yet: it arrives with transaction items (slice C). Nothing here logs or puts user text in an
 * error (CLAUDE.md §3.7).
 *
 * @param runner the transaction the check-then-write sequences run in.
 * @param writer the audited write path.
 * @param repository the category store.
 * @param ids source of new category ids.
 */
class CategoryService(
    private val runner: TransactionRunner,
    private val writer: AuditedWriter,
    private val repository: CategoryRepository,
    private val ids: Uuid7,
) {
    /**
     * Creates a category from [draft] and audits a CREATE. It goes last among its siblings.
     *
     * @param parentId the top-level category to put it under, or null for a top-level category.
     * @param kind expense or income; a sub-category must have its parent's kind.
     * @throws LedgerError.BlankName / [LedgerError.NameTooLong] for the name.
     * @throws LedgerError.InvalidIcon for an icon outside the grammar.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] if the parent is not live.
     * @throws LedgerError.DepthExceeded if the parent is itself a sub-category.
     * @throws LedgerError.KindMismatch if [kind] differs from the parent's.
     * @throws LedgerError.DuplicateName if a live sibling already has this name.
     * @throws LedgerError.NotInitialised before first-run setup.
     */
    suspend fun create(
        parentId: CategoryId?,
        kind: CategoryKind,
        draft: CategoryDraft,
    ): Category {
        val valid = draft.validated()
        return runner.inTransaction {
            if (parentId != null) CategoryTree.requireParent(repository.get(parentId), kind)
            val siblings = CategoryTree.siblings(repository.allLive(), parentId, kind)
            if (CategoryTree.isNameTaken(siblings, valid.name)) throw LedgerError.DuplicateName(WHAT)
            writer.create(repository, CategoryId(ids.next())) { meta ->
                val sortOrder = CategoryTree.nextSortOrder(siblings)
                Category(meta, parentId, kind, valid.name, valid.colour, valid.icon, sortOrder, null)
            }
        }
    }

    /**
     * Replaces the name, colour and icon of category [id] with [draft] and audits an UPDATE listing
     * only the fields that changed; an edit that changes nothing writes nothing. Works on an archived
     * category and on a system category. Kind, parent and position are untouched.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted category.
     * @throws LedgerError same name and icon errors as [create], and [LedgerError.DuplicateName] if
     *   another live sibling has the name (a case-only change to its own name is fine).
     */
    suspend fun edit(
        id: CategoryId,
        draft: CategoryDraft,
    ): Category {
        val valid = draft.validated()
        // The check runs inside the writer's transaction, against the category as it is stored now.
        return writer.update(repository, id) { old ->
            val siblings = CategoryTree.siblings(repository.allLive(), old.parentId, old.kind)
            if (CategoryTree.isNameTaken(siblings, valid.name, except = old.meta.id)) {
                throw LedgerError.DuplicateName(WHAT)
            }
            old.copy(name = valid.name, colour = valid.colour, iconKey = valid.icon)
        }
    }

    /**
     * Archives category [id] (audited ARCHIVE): it, and its children, drop out of [pickable], and it
     * stays in history. Already archived is a no-op.
     *
     * @throws LedgerError.SystemCategoryProtected for a system category.
     * @throws LedgerError.NotFound / [LedgerError.Deleted] for an unknown or deleted category.
     */
    suspend fun archive(id: CategoryId): Category =
        runner.inTransaction {
            requireNotSystem(id)
            writer.archive(repository, id)
        }

    /** Makes archived category [id] available again (audited UNARCHIVE); not archived is a no-op. */
    suspend fun unarchive(id: CategoryId): Category = writer.unarchive(repository, id)

    /**
     * Deletes category [id] as a tombstone (audited DELETE); already deleted is a no-op.
     *
     * @throws LedgerError.SystemCategoryProtected for a system category.
     * @throws LedgerError.HasChildren while it has live (including archived) sub-categories.
     * @throws LedgerError.NotFound for an unknown category.
     */
    suspend fun delete(id: CategoryId): Category =
        runner.inTransaction {
            requireNotSystem(id)
            if (repository.allLive().any { it.parentId == id }) throw LedgerError.HasChildren()
            writer.delete(repository, id)
        }

    /**
     * Brings a deleted category back (audited RESTORE); not deleted is a no-op. It comes back as it
     * was, archived or not.
     *
     * @throws LedgerError.Deleted if it is a sub-category whose parent is deleted (restore the parent first).
     * @throws LedgerError.DuplicateName if a live sibling has taken its name since.
     * @throws LedgerError.NotFound for an unknown category.
     */
    suspend fun restore(id: CategoryId): Category =
        runner.inTransaction {
            val category = repository.get(id)
            if (category != null && category.meta.isDeleted) {
                if (category.parentId != null && repository.getLive(category.parentId) == null) {
                    throw LedgerError.Deleted(WHAT)
                }
                val siblings = CategoryTree.siblings(repository.allLive(), category.parentId, category.kind)
                if (CategoryTree.isNameTaken(siblings, category.name)) throw LedgerError.DuplicateName(WHAT)
            }
            writer.restore(repository, id)
        }

    /**
     * Puts the live children of [parentId] (or the top-level categories, for null) of [kind] in the
     * order [orderedIds] gives, first to last, in one transaction.
     *
     * The sort values the siblings already have are handed out again in the new order, so a category
     * that keeps its place keeps its value and writes nothing, even when a deleted sibling left a gap;
     * each one that moves gets an audited UPDATE. **Ties are broken here:** if two siblings hold the same
     * value (a deleted one was restored after a new one took its place, or synced devices disagreed), the
     * values handed out are made strictly increasing by raising each tied value to one past the one
     * before it, so a swap of tied siblings takes effect and later reads no longer depend on the id tie-break.
     *
     * @param orderedIds must be exactly the live siblings (archived ones included), each once.
     * @throws LedgerError.InvalidOrder if it names a missing, extra, deleted or repeated category;
     *   nothing is written.
     */
    suspend fun reorder(
        parentId: CategoryId?,
        kind: CategoryKind,
        orderedIds: List<CategoryId>,
    ) {
        runner.inTransaction {
            val siblings = CategoryTree.siblings(repository.allLive(), parentId, kind)
            if (!CategoryTree.isExactly(siblings, orderedIds)) throw LedgerError.InvalidOrder()
            // The siblings' own values, ascending, made strictly increasing: a value that is not above the one
            // before it (a tie) moves up to one past it, so every other value stays where it was.
            val positions = mutableListOf<Int>()
            siblings.forEach { positions += maxOf(it.sortOrder, (positions.lastOrNull() ?: Int.MIN_VALUE) + 1) }
            val current = siblings.associateBy { it.meta.id }
            orderedIds.forEachIndexed { index, id ->
                if (current.getValue(id).sortOrder != positions[index]) {
                    writer.update(repository, id) { it.copy(sortOrder = positions[index]) }
                }
            }
        }
    }

    /**
     * The categories to offer for a new transaction item of [kind]: not deleted, not archived, and
     * not under an archived parent. **Depth-first order:** each top-level category in its position,
     * immediately followed by its own pickable children in theirs; positions that tie are ordered by
     * id, so the order never depends on what the repository returned. Unarchiving a parent brings
     * its non-archived children back.
     */
    suspend fun pickable(kind: CategoryKind): List<Category> = CategoryTree.pickable(repository.allLive(), kind)

    /**
     * Creates the default categories ([DefaultCategories.LIST]) that do not exist yet, in one
     * transaction, audited with source [AuditSource.SYSTEM]; run by first-run setup.
     *
     * Each gets the id `DefaultCategories.idOf(seedKey)` and its position from the table order.
     * One that already exists **as a tombstone too** is skipped, so a default the user deleted is not
     * created again, and a second call writes and audits nothing.
     *
     * @throws LedgerError.NotInitialised before first-run setup.
     */
    suspend fun seedDefaults() {
        runner.inTransaction {
            DefaultCategories.LIST.forEach { entry ->
                val id = DefaultCategories.idOf(entry.seedKey)
                // `get`, not `getLive`: a default the user deleted is a tombstone, and stays deleted.
                if (repository.get(id) == null) {
                    val siblings =
                        DefaultCategories.LIST.filter {
                            it.parentSeedKey == entry.parentSeedKey && it.kind == entry.kind
                        }
                    writer.create(repository, id, AuditSource.SYSTEM) { meta ->
                        Category(
                            meta = meta,
                            parentId = entry.parentSeedKey?.let(DefaultCategories::idOf),
                            kind = entry.kind,
                            name = entry.name,
                            colour = entry.colour,
                            iconKey = CategoryIcon.Symbol(entry.symbolKey),
                            sortOrder = siblings.indexOf(entry),
                            systemKey = entry.systemKey,
                        )
                    }
                }
            }
        }
    }

    private suspend fun requireNotSystem(id: CategoryId) {
        if (repository.get(id)?.systemKey != null) throw LedgerError.SystemCategoryProtected()
    }

    private companion object {
        /** The kind of record, for errors (never a name). */
        const val WHAT = "category"
    }
}
