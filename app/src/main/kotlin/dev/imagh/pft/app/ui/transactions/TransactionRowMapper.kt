package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.common.MoneySemantic
import dev.imagh.pft.app.ui.common.TransactionRowModel
import dev.imagh.pft.app.ui.common.toGlyphSource
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnState

/** What a transfer's meta puts between the source and the target account. */
private const val TRANSFER_ARROW = " → "

/**
 * The words a row needs that are not data, already translated by the screen, so the mapper stays a pure
 * function and a test needs no resources.
 *
 * @property items "3 items" for a count of lines.
 * @property typeLabel the name of a transaction type ("Expense"), a title's last fallback.
 * @property uncategorised the category text of a line with no category.
 * @property unknown the name shown for an account that is not in the lookups, so no row reads blank.
 */
internal class RowTexts(
    val items: (Int) -> String,
    val typeLabel: (TransactionType) -> String,
    val uncategorised: String,
    val unknown: String,
)

/**
 * Everything specific to drawing a ledger entry in the shared `TransactionRow` (Cobalt plan K5): which
 * glyph, which words and which money meaning. The row block stays ignorant of the ledger; this is the
 * only place that knows (the same split as `AccountRowMapper`).
 *
 * - **One line:** its category's glyph and colour, its name as the meta's first part, and the title (a
 *   blank title falls back to the category name, then to the type's label, slice C Q3).
 * - **Several lines:** the long receipt and "n items", whatever the lines' categories; a blank title falls
 *   back to the type's label.
 * - **A transfer:** the transfer glyph, "from → to" as the account, no category, an unsigned
 *   [MoneySemantic.Transfer] amount.
 * - **Money:** the amount is the transaction's own magnitude (never negative) and the type is the
 *   semantic, so an expense is neutral and an income green (plan D5).
 */
internal object TransactionRowMapper {
    /**
     * The row for [entry], with names from [names] and words from [texts]. The row is tappable only when
     * [onClick] is given (the edit form arrives in C4b, so the tab passes none yet).
     */
    fun toModel(
        entry: LedgerEntry,
        names: NameLookups,
        texts: RowTexts,
        onClick: (() -> Unit)? = null,
    ): TransactionRowModel =
        when (entry) {
            is LedgerEntry.Single -> single(entry, names, texts, onClick)
            is LedgerEntry.Transfer -> transfer(entry, names, texts, onClick)
        }

    private fun single(
        entry: LedgerEntry.Single,
        names: NameLookups,
        texts: RowTexts,
        onClick: (() -> Unit)?,
    ): TransactionRowModel {
        val transaction = entry.transaction
        val face = faceOf(entry, names, texts)
        return TransactionRowModel(
            title = transaction.titleOr(face.fallbackTitle),
            glyph = face.glyph,
            colourKey = face.colourKey,
            category = face.category,
            account = names.accounts[transaction.accountId] ?: texts.unknown,
            amount = transaction.amount,
            semantic = semanticOf(transaction.type),
            needsReview = transaction.state == TxnState.UNVERIFIED,
            tags = tagNames(entry.lines, names),
            onClick = onClick,
        )
    }

    /** What a single-transaction row draws for its lines, and the title it falls back to when it has none. */
    private class Face(
        val glyph: GlyphSource,
        val colourKey: String?,
        val category: String?,
        val fallbackTitle: String,
    )

    private fun faceOf(
        entry: LedgerEntry.Single,
        names: NameLookups,
        texts: RowTexts,
    ): Face {
        val typeLabel = texts.typeLabel(entry.transaction.type)
        return when {
            // A transfer leg shown on its own (damage, or a future account filter) is still not income or expense.
            entry.transaction.type == TransactionType.TRANSFER -> Face(TRANSFER_GLYPH, null, null, typeLabel)
            entry.lines.size > 1 ->
                Face(GlyphSource.Symbol(PftSymbol.RECEIPT_LONG), null, texts.items(entry.lines.size), typeLabel)
            else -> {
                val category =
                    entry.lines
                        .firstOrNull()
                        ?.categoryId
                        ?.let(names.categories::get)
                if (category == null) {
                    Face(GlyphSource.Symbol(PftSymbol.RECEIPT), null, texts.uncategorised, typeLabel)
                } else {
                    Face(
                        category.iconKey.toGlyphSource(category.name),
                        category.colour.key,
                        category.name,
                        category.name,
                    )
                }
            }
        }
    }

    private fun transfer(
        entry: LedgerEntry.Transfer,
        names: NameLookups,
        texts: RowTexts,
        onClick: (() -> Unit)?,
    ): TransactionRowModel {
        val from = names.accounts[entry.outgoing.accountId] ?: texts.unknown
        val to = names.accounts[entry.incoming.accountId] ?: texts.unknown
        val unverified = entry.outgoing.state == TxnState.UNVERIFIED || entry.incoming.state == TxnState.UNVERIFIED
        return TransactionRowModel(
            title = entry.outgoing.titleOr(texts.typeLabel(TransactionType.TRANSFER)),
            glyph = TRANSFER_GLYPH,
            colourKey = null,
            category = null,
            account = from + TRANSFER_ARROW + to,
            amount = entry.outgoing.amount,
            semantic = MoneySemantic.Transfer,
            needsReview = unverified,
            onClick = onClick,
        )
    }

    private fun semanticOf(type: TransactionType): MoneySemantic =
        when (type) {
            TransactionType.EXPENSE -> MoneySemantic.Expense
            TransactionType.INCOME -> MoneySemantic.Income
            TransactionType.TRANSFER -> MoneySemantic.Transfer
        }

    /** The title, or [fallback] when the transaction has none. */
    private fun LedgerTransaction.titleOr(fallback: String): String = title?.takeIf { it.isNotBlank() } ?: fallback

    /**
     * The names of the tags on [lines], each once, by name ignoring case then exactly (a total order, so
     * the chips never depend on a set's iteration order). A tag missing from the lookups is left out.
     */
    private fun tagNames(
        lines: List<TransactionLine>,
        names: NameLookups,
    ): List<String> =
        lines
            .flatMap { it.tagIds }
            .toSet()
            .mapNotNull(names.tags::get)
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER.thenBy { it })

    private val TRANSFER_GLYPH = GlyphSource.Symbol(PftSymbol.TRANSFER)
}
