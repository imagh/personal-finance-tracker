package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * What the categories list can do; the route wires these to the ViewModel and to navigation.
 *
 * @property choose does a row menu entry to a category.
 * @property selectKind shows one kind's categories.
 * @property add opens the create form for a new category of the given kind (the kind on show).
 * @property edit opens a category's edit form.
 */
class CategoriesActions(
    val choose: (CategoryId, CategoryAction) -> Unit,
    val selectKind: (CategoryKind) -> Unit,
    val add: (CategoryKind) -> Unit,
    val edit: (CategoryId) -> Unit,
)

/**
 * Where the categories list can go; the route wires these to the navigation host.
 *
 * @property back leaves the list.
 * @property add opens the create form for a new category of the given kind.
 * @property edit opens a category's edit form.
 */
class CategoriesNavigation(
    val back: () -> Unit,
    val add: (CategoryKind) -> Unit,
    val edit: (CategoryId) -> Unit,
)
