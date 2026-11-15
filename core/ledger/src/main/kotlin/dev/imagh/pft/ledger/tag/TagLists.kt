package dev.imagh.pft.ledger.tag

/**
 * The tags a list screen shows, split in two: [active] (offered for new items) and [archived] (kept
 * for history). Deleted tags are in neither. Each list is sorted by name ignoring case, then by id, so
 * the order is total and never depends on what the repository returned.
 */
data class TagLists(
    val active: List<Tag>,
    val archived: List<Tag>,
)
