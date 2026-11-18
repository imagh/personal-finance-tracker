package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.money.Paise

/**
 * What the user entered for one line (item) of a transaction, **untrusted until [Breakdown] and
 * [TransactionService] have checked it**. A plain transaction is one draft for the whole amount; the
 * ViewModel builds it, and core does not special-case it.
 *
 * @param id the line it edits, or null for a new line.
 * @param amount its share of the transaction's amount.
 * @param categoryId its category, or null for uncategorised.
 * @param tagIds its tags.
 * @param note an optional note; trimmed, and blank means none.
 */
data class LineDraft(
    val id: TransactionLineId? = null,
    val amount: Paise,
    val categoryId: CategoryId?,
    val tagIds: Set<TagId> = emptySet(),
    val note: String? = null,
)
