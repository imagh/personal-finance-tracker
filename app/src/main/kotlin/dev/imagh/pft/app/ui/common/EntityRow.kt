package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Immutable

/**
 * The badge at the start of an [EntityRow]: what a `CategoryGlyph` draws.
 *
 * @property source a registry symbol, an emoji or a letter.
 * @property colourKey the palette name (`CategoryPalette`); null or unknown gets slate.
 * @property label what TalkBack speaks for the badge, separate from the row's title so a screen reader
 *   does not say it twice. Null makes the badge decorative (nothing is spoken for it), for a row whose
 *   text already says all there is to say: a category (its title) and an account (its supporting line
 *   starts with the type).
 */
@Immutable
data class RowGlyph(
    val source: GlyphSource,
    val colourKey: String?,
    val label: String?,
)

/**
 * One row of an `EntityListScreen`, as plain data: everything the row shows and can do, already
 * resolved to text by the consumer's mapper (`AccountRowMapper`). It carries no entity and no logic,
 * so the list block never learns what an account, a category or a tag is.
 *
 * @property key a stable identity for the row (the entity id as text); keeps a row's own state (an
 *   open menu) with its entity when the list reorders.
 * @property title the main line.
 * @property supporting an optional second line.
 * @property glyph an optional leading badge.
 * @property indent true for a child row (a sub-category): inset from the start by one spacing step.
 * @property onClick what tapping the row does, or null for a row that is not tappable.
 * @property actions the entries of the row's overflow menu, in order; empty means no overflow button.
 */
@Immutable
data class EntityRow(
    val key: String,
    val title: String,
    val supporting: String? = null,
    val glyph: RowGlyph? = null,
    val indent: Boolean = false,
    val onClick: (() -> Unit)? = null,
    val actions: List<RowAction> = emptyList(),
)
