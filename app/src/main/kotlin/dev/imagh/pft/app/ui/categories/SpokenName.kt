package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.category.CategoryColour

/**
 * What a screen reader says for a symbol or a colour that has no visible text: its permanent key as
 * words, with the first letter capitalised ("local_gas_station" is "Local gas station", "amber" is
 * "Amber"). The keys are English words by construction (they are permanent registry keys), so no
 * string resource per symbol is needed; nothing here is user text.
 */
private fun String.asWords(): String = replace('_', ' ').replaceFirstChar { it.uppercaseChar() }

/** The spoken name of this symbol; see [asWords]. */
internal fun PftSymbol.spokenName(): String = key.asWords()

/** The spoken name of this colour; see [asWords]. */
internal fun CategoryColour.spokenName(): String = key.asWords()
