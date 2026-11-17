package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.ids.CategoryId

/** What the category form can edit and save; the route wires these to the ViewModel. Back is separate. */
class CategoryEditActions(
    val onNameChange: (String) -> Unit,
    val onColourChange: (CategoryColour) -> Unit,
    val onIconChange: (CategoryIcon?) -> Unit,
    val onParentChange: (CategoryId?) -> Unit,
    val save: () -> Unit,
)
