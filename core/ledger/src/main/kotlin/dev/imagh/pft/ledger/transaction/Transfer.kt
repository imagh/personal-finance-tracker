package dev.imagh.pft.ledger.transaction

/**
 * The two legs of a transfer, each with its one line (slice C C2).
 *
 * @property groupId the id both legs carry as `transferGroupId`; what [TransferService.edit] takes.
 * @property out the leg on the source account ([Direction.OUT]).
 * @property into the leg on the target account ([Direction.IN]).
 */
data class Transfer(
    val groupId: String,
    val out: TransactionWithLines,
    val into: TransactionWithLines,
)

/**
 * The OUT and IN legs of one transfer group, read from [this] (every transaction carrying the group id).
 * Fails closed: the group must be exactly one OUT and one IN leg, both of type [TransactionType.TRANSFER],
 * or a delete, restore or edit could act on the wrong rows. The message is fixed text.
 *
 * @return the OUT leg and the IN leg.
 * @throws IllegalStateException if the group is not exactly those two legs.
 */
internal fun List<LedgerTransaction>.asTransferLegs(): Pair<LedgerTransaction, LedgerTransaction> {
    val out = filter { it.direction == Direction.OUT }
    val into = filter { it.direction == Direction.IN }
    check(out.size == 1 && into.size == 1 && all { it.type == TransactionType.TRANSFER }) {
        "A transfer must have one OUT and one IN leg"
    }
    return out.first() to into.first()
}
