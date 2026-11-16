package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.category.CategoryIcon

/**
 * The glyph a category's [CategoryIcon] draws, for the category called [name]. This is the app's half
 * of the icon rules (core's [CategoryIcon] owns the grammar): a symbol must exist in the registry
 * ([PftSymbol.fromKey]) and an emoji must be in core's grammar; anything else, and no icon at all,
 * is the first letter of the name ([GlyphSource.letterFor]). It never throws, because an icon read
 * from storage is untrusted (a corrupt row, or one written by a newer synced version).
 *
 * Named consumers: the categories list (B4a), `CategoryPicker` (B4b), transaction rows (C4), budget
 * rows (D4) and recurring rows (E4). [GlyphSource.fromIconKey] is this over `CategoryIcon.parse`.
 */
internal fun CategoryIcon?.toGlyphSource(name: String): GlyphSource =
    when (val icon = this?.takeIf { it.isValid }) {
        is CategoryIcon.Symbol -> PftSymbol.fromKey(icon.key)?.let(GlyphSource::Symbol)
        is CategoryIcon.Emoji -> GlyphSource.Emoji(icon.text)
        null -> null
    } ?: GlyphSource.letterFor(name)
