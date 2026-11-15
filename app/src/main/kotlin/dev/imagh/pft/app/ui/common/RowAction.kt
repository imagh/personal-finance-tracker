package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Immutable

/**
 * One thing a list can do: an entry of a row's overflow menu ("Archive", "Delete", "Move up") or the
 * add action of the whole list. Plain data: the consumer's ViewModel decides which actions a row
 * offers and what each does; this block only draws them.
 *
 * @property label the text of the menu entry (and the add button's spoken label); never blank.
 * @property onClick what choosing it does.
 */
@Immutable
data class RowAction(
    val label: String,
    val onClick: () -> Unit,
)
