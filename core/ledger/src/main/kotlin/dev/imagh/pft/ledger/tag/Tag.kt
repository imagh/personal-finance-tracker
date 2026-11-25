package dev.imagh.pft.ledger.tag

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.TagId

/**
 * A free-form label the user puts on the items of a transaction (PRD L-3): "Travel", "Reimbursable".
 * It has a name and nothing else (no colour or icon). A transaction's lines (items) carry tags through
 * their tag set; a tag that a live line of a live transaction carries cannot be deleted, only archived.
 *
 * Built only by [TagService], which validates and trims the name. Names are unique among live tags,
 * ignoring case; an archived tag still holds its name, a deleted one does not.
 *
 * @param name what the user calls it, trimmed, 1 to [dev.imagh.pft.ledger.validation.LedgerLimits.MAX_NAME_LENGTH]
 *   characters.
 */
data class Tag(
    override val meta: EntityMeta<TagId>,
    val name: String,
) : LedgerEntity<TagId, Tag> {
    override val auditType = "tag"

    override fun auditFields(): Map<String, String?> = mapOf("name" to name)

    override fun withMeta(meta: EntityMeta<TagId>) = copy(meta = meta)
}
