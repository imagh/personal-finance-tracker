package dev.imagh.pft.ledger.entity

import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.ids.UserId
import java.time.Instant

/**
 * The columns every user entity shares, composed into each model as `meta` (plan §3.4).
 *
 * Gives every record per-user and per-device attribution (CLAUDE.md Rule C), and the two
 * lifecycle states: *deleted* (a tombstone: hard delete never happens, INT-3) and *archived*
 * (hidden from pickers but still part of history).
 *
 * @param id the entity's own typed id.
 * @param createdAt when it was created.
 * @param updatedAt when it last changed (any audited edit or lifecycle change).
 * @param createdByUserId who created it.
 * @param createdOnDeviceId which device created it.
 * @param deletedAt when it was deleted (tombstoned), or null.
 * @param archivedAt when it was archived, or null.
 */
data class EntityMeta<Id : EntityId>(
    val id: Id,
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdByUserId: UserId,
    val createdOnDeviceId: DeviceId,
    val deletedAt: Instant? = null,
    val archivedAt: Instant? = null,
) {
    /** Tombstoned: excluded from queries, restorable. */
    val isDeleted: Boolean get() = deletedAt != null

    /** Archived: kept for history but not offered for new entries. */
    val isArchived: Boolean get() = archivedAt != null

    /** Neither deleted nor archived: usable as a reference from new data. */
    val isActive: Boolean get() = !isDeleted && !isArchived
}
