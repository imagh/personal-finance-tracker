package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.Validation
import java.util.Locale

/**
 * The tree's plain functions: sorting, sibling sets, the picker's order and the name rule, over the
 * list `CategoryRepository.allLive()` returns (tombstoned categories are not in it). Pure and
 * without I/O, so they are unit-tested directly and their results never depend on the order the
 * repository returned the rows in. [CategoryService] builds on them.
 */
internal object CategoryTree {
    /** The order siblings are shown in: `sort_order`, ties broken by id so it is total and stable. */
    val ORDER: Comparator<Category> = compareBy<Category>({ it.sortOrder }, { it.meta.id.value })

    /** The categories of [kind] under [parentId] (null for top level), in [ORDER]. */
    fun siblings(
        all: List<Category>,
        parentId: CategoryId?,
        kind: CategoryKind,
    ): List<Category> = all.filter { it.parentId == parentId && it.kind == kind }.sortedWith(ORDER)

    /**
     * [parent] if a new category of [kind] may go under it: it exists, is neither deleted nor archived,
     * is top-level and has the same kind.
     *
     * @throws LedgerError.NotFound / [LedgerError.Deleted] / [LedgerError.Archived] if it is not live.
     * @throws LedgerError.DepthExceeded if it is itself a sub-category.
     * @throws LedgerError.KindMismatch if its kind is not [kind].
     */
    fun requireParent(
        parent: Category?,
        kind: CategoryKind,
    ): Category {
        val live = Validation.requireLive(parent, "category")
        if (live.parentId != null) throw LedgerError.DepthExceeded()
        if (live.kind != kind) throw LedgerError.KindMismatch()
        return live
    }

    /** The `sort_order` for a new category that goes after [siblings]: one past the largest, or 0. */
    fun nextSortOrder(siblings: List<Category>): Int = (siblings.maxOfOrNull { it.sortOrder } ?: -1) + 1

    /**
     * What the picker offers for [kind], depth-first: each top-level category that is not archived,
     * immediately followed by its own children that are not archived, both in [ORDER]. A child of an
     * archived or missing parent, or of a different kind than its parent, is left out.
     */
    fun pickable(
        all: List<Category>,
        kind: CategoryKind,
    ): List<Category> =
        siblings(all, null, kind)
            .filterNot { it.meta.isArchived }
            .flatMap { top -> listOf(top) + siblings(all, top.meta.id, kind).filterNot { it.meta.isArchived } }

    /** True if [ids] names every one of [siblings] exactly once and nothing else. */
    fun isExactly(
        siblings: List<Category>,
        ids: List<CategoryId>,
    ): Boolean = ids.size == siblings.size && ids.toSet() == siblings.map { it.meta.id }.toSet()

    /**
     * The text names are compared by: trimmed and lower-cased with Unicode rules and no locale (SQLite's
     * `NOCASE` folds ASCII only).
     */
    fun nameKey(name: String): String = name.trim().lowercase(Locale.ROOT)

    /**
     * True if one of [siblings], other than the category [except] (the one being renamed), already
     * has [name] by [nameKey].
     */
    fun isNameTaken(
        siblings: List<Category>,
        name: String,
        except: CategoryId? = null,
    ): Boolean {
        val key = nameKey(name)
        return siblings.any { it.meta.id != except && nameKey(it.name) == key }
    }
}
