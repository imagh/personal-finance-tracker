package dev.imagh.pft.app.ui.tags

/**
 * What the tag form can edit and save; the route wires these to the ViewModel. Back is separate.
 *
 * @property onNameChange the name field was edited.
 * @property save Save was tapped.
 */
class TagEditActions(
    val onNameChange: (String) -> Unit,
    val save: () -> Unit,
)
