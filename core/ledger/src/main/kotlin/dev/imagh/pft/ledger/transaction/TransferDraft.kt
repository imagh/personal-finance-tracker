package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * What the user entered for a transfer, **untrusted until [TransferService] has validated it**: money
 * moved from one of their accounts to another (a card bill payment is a transfer from a bank account
 * to a credit card). It has no category and no items: a transfer is not spending (IT §3.3 rule 2).
 *
 * @param from the account the money leaves.
 * @param to the account the money arrives in; must differ from [from].
 * @param amount the amount moved, positive and within the limit.
 * @param occurredAt when the money moved.
 * @param title an optional label; trimmed, blank means none.
 * @param description an optional longer note; trimmed, blank means none.
 * @param tagIds the tags, written on both legs' lines.
 */
data class TransferDraft(
    val from: AccountId,
    val to: AccountId,
    val amount: Paise,
    val occurredAt: Instant,
    val title: String? = null,
    val description: String? = null,
    val tagIds: Set<TagId> = emptySet(),
)

/**
 * This draft with every typed value checked and the texts trimmed (blank becomes null): what
 * [TransferService] writes. Pure: it looks nothing up, so the accounts and tags are checked by the service.
 *
 * @throws LedgerError.SameAccount if [TransferDraft.from] equals [TransferDraft.to].
 * @throws LedgerError.AmountNotPositive / [LedgerError.AmountTooLarge] for the amount.
 * @throws LedgerError.NameTooLong for a title or description over its limit.
 */
internal fun TransferDraft.validated(): TransferDraft {
    if (from == to) throw LedgerError.SameAccount()
    Validation.requireAmount(amount)
    return copy(
        title = Validation.requireOptionalName(title, LedgerLimits.MAX_TITLE_LENGTH),
        description = Validation.requireOptionalName(description, LedgerLimits.MAX_DESCRIPTION_LENGTH),
    )
}
