package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.ids.CategoryId

/**
 * What a categories list screen shows for one kind: the live categories split into those in use
 * and the archived ones. A category is *archived* here when it is archived itself or sits under an
 * archived parent (it is hidden from pickers either way, so the list shows it with its parent in the
 * archived section). Both lists are depth-first, each top-level category followed by its own children.
 *
 * @property active categories offered for new entries.
 * @property archived archived categories, and the children of archived parents.
 */
data class CategoryLists(
    val active: List<Category>,
    val archived: List<Category>,
) {
    /**
     * The live categories under [parentId] (null for top level) from both lists, in the order they
     * are shown: `sort_order`, ties broken by id. This is exactly the set
     * [CategoryService.reorder] needs, archived ones included.
     */
    fun siblingsOf(parentId: CategoryId?): List<Category> =
        (active + archived).filter { it.parentId == parentId }.sortedWith(CategoryTree.ORDER)
}
