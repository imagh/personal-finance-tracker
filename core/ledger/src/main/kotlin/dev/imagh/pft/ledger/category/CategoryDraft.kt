package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation

/**
 * What the user entered for a category, **untrusted until [CategoryService] has validated it**. The
 * same draft serves create and edit. It carries only what can change after creation: the kind and
 * the parent are fixed when the category is created (plan D7), so they are not part of it
 * (`create` takes them as arguments).
 *
 * @param name required; trimmed and length-checked by the service, and unique among its siblings.
 * @param colour one of the 12 palette colours.
 * @param icon a symbol or an emoji, or null for none (the UI draws the name's first letter). The
 *   service refuses one that is not in the grammar (`InvalidIcon`).
 */
data class CategoryDraft(
    val name: String,
    val colour: CategoryColour,
    val icon: CategoryIcon? = null,
)

/**
 * This draft with the name trimmed and checked, and the icon checked: what [CategoryService] writes.
 *
 * @throws LedgerError.BlankName / [LedgerError.NameTooLong] for the name.
 * @throws LedgerError.InvalidIcon for an icon outside the [CategoryIcon] grammar.
 */
internal fun CategoryDraft.validated(): CategoryDraft {
    val checkedName = Validation.requireName(name, LedgerLimits.MAX_NAME_LENGTH)
    if (icon != null && !icon.isValid) throw LedgerError.InvalidIcon()
    return copy(name = checkedName)
}
