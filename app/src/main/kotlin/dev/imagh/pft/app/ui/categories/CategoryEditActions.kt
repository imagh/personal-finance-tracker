package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.ids.CategoryId

/**
 * What the category form can edit and save; the route wires these to the ViewModel. Back is separate.
 *
 * @property onIconChange a symbol, no symbol (null) or a quick-pick emoji was chosen.
 * @property onEmojiInput text was typed or pasted into the emoji field. It is untrusted and reaches here
 *   exactly as typed; the ViewModel judges it.
 */
class CategoryEditActions(
    val onNameChange: (String) -> Unit,
    val onColourChange: (CategoryColour) -> Unit,
    val onIconChange: (CategoryIcon?) -> Unit,
    val onEmojiInput: (String) -> Unit,
    val onParentChange: (CategoryId?) -> Unit,
    val save: () -> Unit,
)
