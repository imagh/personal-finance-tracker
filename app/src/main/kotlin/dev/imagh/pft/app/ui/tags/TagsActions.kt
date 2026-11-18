package dev.imagh.pft.app.ui.tags

import dev.imagh.pft.ledger.ids.TagId

/**
 * What the tags list can do; the route wires these to the ViewModel and to navigation.
 *
 * @property archive archives a tag.
 * @property unarchive makes an archived tag active again.
 * @property delete deletes a tag (with an Undo).
 * @property add opens the create form.
 * @property edit opens a tag's edit form.
 */
class TagsActions(
    val archive: (TagId) -> Unit,
    val unarchive: (TagId) -> Unit,
    val delete: (TagId) -> Unit,
    val add: () -> Unit,
    val edit: (TagId) -> Unit,
)

/**
 * Where the tags list can go; the route wires these to the navigation host.
 *
 * @property back leaves the list.
 * @property add opens the create form.
 * @property edit opens a tag's edit form.
 */
class TagsNavigation(
    val back: () -> Unit,
    val add: () -> Unit,
    val edit: (TagId) -> Unit,
)
