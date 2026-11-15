package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagRepository

/**
 * The in-memory [TagRepository]: the generic fake plus the tag query. [allLive] returns the rows
 * **newest first**, deliberately not in insertion order: the contract promises no order (Room returns
 * none without an `ORDER BY`), so a service test that passes only because the fake happens to return
 * insertion order is exposed here.
 */
class InMemoryTagRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<Tag, TagId>(ledger, "tag"),
    TagRepository {
    override suspend fun allLive(): List<Tag> = rows.all().filterNot { it.meta.isDeleted }.reversed()
}
