package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.CategoryId

/** What the categories list can do; the route wires these to the ViewModel. */
class CategoriesActions(
    val choose: (CategoryId, CategoryAction) -> Unit,
    val selectKind: (CategoryKind) -> Unit,
)
