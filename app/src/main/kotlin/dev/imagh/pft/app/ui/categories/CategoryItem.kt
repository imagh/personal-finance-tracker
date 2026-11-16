package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryLists

/** What a category's row menu can offer. The ViewModel decides which apply to a row ([CategoryItem.actions]). */
enum class CategoryAction {
    /** Swap with the previous sibling. */
    MOVE_UP,

    /** Swap with the next sibling. */
    MOVE_DOWN,

    /** Hide it, and its sub-categories, from pickers. */
    ARCHIVE,

    /** Make an archived category available again. */
    UNARCHIVE,

    /** Delete it (a tombstone), with an Undo. */
    DELETE,
}

/**
 * One row of the categories list, with the rules already applied: which menu entries it offers
 * ([actions], in menu order), whether it is inset under its parent, and the parent's name when the
 * row is shown away from it. [CategoryRowMapper] turns it into drawable text.
 *
 * @property category the category itself.
 * @property indent true for a sub-category shown directly under its parent.
 * @property parentName the parent's name for a sub-category listed without its parent (an archived
 *   sub-category of an active parent), so it can still be told apart; null otherwise.
 * @property actions the menu entries, never containing [CategoryAction.ARCHIVE] or
 *   [CategoryAction.DELETE] for a system category.
 */
data class CategoryItem(
    val category: Category,
    val indent: Boolean,
    val parentName: String?,
    val actions: List<CategoryAction>,
)

/**
 * The two sections of the list, with each row's menu decided (plan D7, D8, D11):
 *
 * - **Active rows** offer Move up unless first, and Move down unless last, among the *active*
 *   siblings under the same parent (so each parent's children are their own group), then Archive and
 *   Delete. A system category (Loan EMI) never gets Archive or Delete.
 * - **Archived rows** offer no move. A category archived itself offers Unarchive and Delete; a
 *   sub-category that is only hidden because its parent is archived offers Delete alone (it is not
 *   archived itself, and the parent's Delete is refused while it exists).
 */
internal fun CategoryLists.toItems(): Pair<List<CategoryItem>, List<CategoryItem>> {
    val names = (active + archived).associate { it.meta.id to it.name }
    val archivedIds = archived.map { it.meta.id }.toSet()
    val activeItems =
        active.map { category ->
            val peers = active.filter { it.parentId == category.parentId }
            val index = peers.indexOf(category)
            val menu =
                buildList {
                    if (index > 0) add(CategoryAction.MOVE_UP)
                    if (index < peers.lastIndex) add(CategoryAction.MOVE_DOWN)
                    if (category.systemKey == null) addAll(listOf(CategoryAction.ARCHIVE, CategoryAction.DELETE))
                }
            CategoryItem(category, indent = category.parentId != null, parentName = null, actions = menu)
        }
    val archivedItems =
        archived.map { category ->
            val shownUnderParent = category.parentId?.let { it in archivedIds } == true
            val menu =
                buildList {
                    if (category.meta.isArchived) add(CategoryAction.UNARCHIVE)
                    if (category.systemKey == null) add(CategoryAction.DELETE)
                }
            CategoryItem(
                category,
                indent = shownUnderParent,
                parentName = category.parentId?.takeUnless { shownUnderParent }?.let(names::get),
                actions = menu,
            )
        }
    return activeItems to archivedItems
}
