package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat

/** From this font scale up the amount sits under the tile and text, not beside them: it needs the width. */
private const val STACK_FONT_SCALE = 1.5f

/** What a screen reader pauses on between the parts of a row. */
private const val SPOKEN_SEPARATOR = ", "

/** What the meta line puts between the category and the account. */
private const val META_SEPARATOR = " · "

/**
 * One transaction in a list, as plain data: everything [TransactionRow] shows and can do, already
 * resolved to text by the consumer's mapper. It carries no entity and no logic, so the block never
 * learns what a ledger transaction is.
 *
 * @property title the main line: the payee or the description.
 * @property glyph what the category tile draws.
 * @property colourKey the tile's palette name (`CategoryPalette`); null or unknown gets slate.
 * @property category the category's name, or null for a transfer (it has none).
 * @property account the account's name.
 * @property amount the amount as a magnitude: the direction is [semantic], never the sign.
 * @property semantic [MoneySemantic.Expense], [MoneySemantic.Income] or [MoneySemantic.Transfer].
 * @property needsReview true for an auto-logged entry the user has not confirmed: the row shows a
 *   "Needs review" chip.
 * @property tags the names of the tags on the transaction, in the order to show them.
 * @property onClick what tapping the row does, or null for a row that is not tappable.
 * @throws IllegalArgumentException if [semantic] is not an expense, an income or a transfer, or
 *   [amount] is negative.
 */
@Immutable
data class TransactionRowModel(
    val title: String,
    val glyph: GlyphSource,
    val colourKey: String?,
    val category: String?,
    val account: String,
    val amount: Paise,
    val semantic: MoneySemantic,
    val needsReview: Boolean = false,
    val tags: List<String> = emptyList(),
    val onClick: (() -> Unit)? = null,
) {
    init {
        require(
            semantic == MoneySemantic.Expense || semantic == MoneySemantic.Income || semantic == MoneySemantic.Transfer,
        ) { "a transaction is an expense, an income or a transfer, not $semantic" }
        require(amount.value >= 0) { "a transaction amount is a magnitude; the direction is the semantic" }
    }
}

/**
 * One transaction row (Cobalt plan D3): a decorative category tile, the title, a "category · account"
 * meta line, optional tag chips, the amount (a [MoneyText]: an expense is neutral with a minus, an
 * income green with a plus, a transfer neutral and unsigned) and, for an unconfirmed auto-logged
 * entry, a "Needs review" [StatusChip]. It draws no background of its own: put it in a
 * [GroupedSection] row, which is what [DayGroup] does.
 *
 * Named consumers: Home's recent transactions, the Transactions tab, search results, budget detail
 * and loan detail.
 *
 * **Accessibility.** The row is one node. With an [TransactionRowModel.onClick] it is a button, at
 * least 56dp tall, and either way it speaks one description: the title, the category, the account, the
 * tags, the amount ("Expense, ₹486.00") and the chip once each, in that order. The tile is decorative
 * (`CategoryGlyph(name = null)`): the meta already names the category. Nothing is colour-only: the
 * amount carries its sign and direction, and the review state and every tag are words.
 *
 * **Large text.** The title and meta wrap and never truncate. From a font scale of
 * [STACK_FONT_SCALE] the amount moves under the tile and the text and takes the row's whole width.
 * Tag chips wrap onto further lines.
 *
 * @param model what to show.
 */
@Composable
fun TransactionRow(
    model: TransactionRowModel,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    val stacked = LocalDensity.current.fontScale >= STACK_FONT_SCALE
    val tapped = model.onClick?.let { Modifier.clickable(role = Role.Button, onClick = it) } ?: Modifier
    val spoken = spokenRow(model)
    val frame =
        modifier
            .fillMaxWidth()
            .heightIn(min = spacing.huge + spacing.sm)
            .then(tapped)
            // One node that speaks the whole row. Merging is not enough: MoneyText and StatusChip are merged
            // nodes themselves, and a merged child is not folded into its parent, so TalkBack would stop on them.
            .clearAndSetSemantics { contentDescription = spoken }
            .padding(horizontal = spacing.lg, vertical = spacing.md)
    if (stacked) {
        Column(frame, verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                CategoryGlyph(model.glyph, name = null, colourKey = model.colourKey)
                Column(Modifier.weight(1f)) { RowText(model) }
            }
            RowEnd(model, Alignment.Start)
        }
    } else {
        Row(
            frame,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            CategoryGlyph(model.glyph, name = null, colourKey = model.colourKey)
            Column(Modifier.weight(1f)) { RowText(model) }
            RowEnd(model, Alignment.End)
        }
    }
}

/**
 * What a screen reader says for the row: the title, the category, the account, each tag, the amount
 * with its direction ("Expense, ₹486.00") and the review chip's words, once each, in that order.
 */
@Composable
private fun spokenRow(model: TransactionRowModel): String {
    val review = stringResource(R.string.transaction_needs_review)
    val amount = spokenAmount(model.semantic, model.amount, PaiseFormat.format(model.amount))
    val parts = listOfNotNull(model.title, model.category, model.account) + model.tags + amount
    return (parts + listOfNotNull(review.takeIf { model.needsReview })).joinToString(SPOKEN_SEPARATOR)
}

/** The title, the meta line and the tag chips. */
@Composable
private fun RowText(model: TransactionRowModel) {
    // Explicit colour: a Surface with a non-theme fill (the island) does not provide a content colour.
    Text(
        model.title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
    )
    Text(
        listOfNotNull(model.category, model.account).joinToString(META_SEPARATOR),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
    if (model.tags.isNotEmpty()) TagChips(model.tags, Modifier.padding(top = PftTheme.spacing.xs))
}

/** The amount and, under it, the review chip. */
@Composable
private fun RowEnd(
    model: TransactionRowModel,
    alignment: Alignment.Horizontal,
) {
    Column(horizontalAlignment = alignment) {
        MoneyText(model.amount, model.semantic)
        if (model.needsReview) {
            StatusChip(
                stringResource(R.string.transaction_needs_review),
                MoneyTone.Warning,
                Modifier.padding(top = PftTheme.spacing.xs),
            )
        }
    }
}

/**
 * [tags] as neutral pills in reading order, as many to a line as fit, the rest on further lines.
 * A plain [Layout] because `FlowRow` is still experimental in the pinned Compose BOM.
 */
@Composable
private fun TagChips(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    val gap = PftTheme.spacing.xs
    Layout(content = { tags.forEach { StatusChip(it, MoneyTone.Neutral) } }, modifier) { measurables, constraints ->
        val gapPx = gap.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        var x = 0
        var y = 0
        var lineHeight = 0
        var widest = 0
        val offsets =
            placeables.map { placeable ->
                if (x > 0 && x + placeable.width > constraints.maxWidth) {
                    x = 0
                    y += lineHeight + gapPx
                    lineHeight = 0
                }
                val offset = IntOffset(x, y)
                x += placeable.width + gapPx
                widest = maxOf(widest, offset.x + placeable.width)
                lineHeight = maxOf(lineHeight, placeable.height)
                offset
            }
        layout(constraints.constrainWidth(widest), constraints.constrainHeight(y + lineHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.place(offsets[index]) }
        }
    }
}
