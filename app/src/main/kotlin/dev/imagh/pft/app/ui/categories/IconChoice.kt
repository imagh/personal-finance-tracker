package dev.imagh.pft.app.ui.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.ledger.category.CategoryIcon

/**
 * The icon choice of the category form: a **Symbol | Emoji** switch over the [SymbolGrid] or the
 * [EmojiTab].
 *
 * **Why segmented buttons and not tabs.** The app already uses one idiom for "choose which of a few
 * options is on show" (the Expense/Income switch of the list and the account type): a
 * `SingleChoiceSegmentedButtonRow`, whose buttons TalkBack announces as radio buttons with a selected
 * state ("Symbol, radio button, selected"). Tabs (`Role.Tab`) would add a second idiom and a tab row
 * with its own indicator for two options, and their "tab 1 of 2" announcement promises a panel
 * container the form does not have. The switch only chooses which input is shown; the category's icon
 * is the form's state, not the switch's.
 *
 * **Which side opens first.** The Emoji side when the category's icon is an emoji (so editing "Pets" with
 * a dog shows the dog selected rather than a symbol grid with nothing selected), otherwise the Symbol
 * side, including a new category. This is decided once, when the section first appears; after that
 * the user's choice stands (and survives rotation). **Switching sides never changes the icon**: a category
 * whose icon is an emoji keeps it until a symbol or another emoji is chosen, and the Symbol side then
 * selects no cell, as [SymbolGrid] specifies.
 *
 * @param state the form's state: the icon, the colour, the name and whether the last paste was refused.
 * @param actions what choosing an icon, or typing an emoji, does.
 */
@Composable
internal fun IconChoice(
    state: CategoryEditUiState,
    actions: CategoryEditActions,
    modifier: Modifier = Modifier,
) {
    var emojiSide by rememberSaveable { mutableStateOf(state.icon is CategoryIcon.Emoji) }
    Column(modifier) {
        SingleChoiceSegmentedButtonRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = PftTheme.spacing.gutter, vertical = PftTheme.spacing.sm),
        ) {
            // Each segment says which side it is, so nothing depends on its position.
            val sides = listOf(false to R.string.category_symbol_label, true to R.string.category_emoji_label)
            sides.forEachIndexed { index, (isEmojiSide, label) ->
                SegmentedButton(
                    selected = emojiSide == isEmojiSide,
                    onClick = { emojiSide = isEmojiSide },
                    shape = SegmentedButtonDefaults.itemShape(index, 2),
                ) { Text(stringResource(label)) }
            }
        }
        if (emojiSide) {
            EmojiTab(state.icon, state.colour, state.emojiRejected, actions.onIconChange, actions.onEmojiInput)
        } else {
            SymbolGrid(state.icon, state.colour, state.name.text, actions.onIconChange)
        }
    }
}
