package dev.imagh.pft.ledger.account

/**
 * The accounts a list screen shows, split in two: [active] (offered for new transactions) and
 * [archived] (kept for history). Deleted accounts are in neither. Each list is sorted by name
 * ignoring case, then by creation order.
 */
data class AccountLists(
    val active: List<Account>,
    val archived: List<Account>,
)
