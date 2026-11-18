package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/** The wiring every tag service test starts from: the kit's fakes and a few shorthand writers. */
internal class TagFixture {
    val kit = LedgerTestKit()
    val service = kit.tags
    val trail get() = kit.store.auditLog.entries

    /** The stored rows themselves (tombstones included), not the service's view. */
    fun stored() = kit.tagRepository.all()

    suspend fun archive(tag: Tag) = kit.writer.archive(kit.tagRepository, tag.meta.id)

    suspend fun delete(tag: Tag) = kit.writer.delete(kit.tagRepository, tag.meta.id)

    /**
     * A tag written straight through the writer with a chosen id, bypassing the service's name rule: the
     * way a second device's tag, or a restore that raced a create, could end up in the table.
     */
    suspend fun raw(
        id: String,
        name: String,
    ): Tag = kit.writer.create(kit.tagRepository, TagId(id)) { meta -> Tag(meta, name) }
}

/**
 * Runs [block], which must be refused with an [E], and checks the refusal changed nothing: no row
 * written (tombstones and all) and no audit entry appended. Returns the error for further checks.
 */
internal suspend inline fun <reified E : LedgerError> TagFixture.shouldRefuse(block: () -> Any?): E {
    val rows = stored()
    val trailSize = trail.size
    val error = shouldThrow<E> { block() }
    stored() shouldBe rows
    trail shouldHaveSize trailSize
    return error
}
