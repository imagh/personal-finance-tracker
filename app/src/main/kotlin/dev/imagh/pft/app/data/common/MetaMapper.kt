package dev.imagh.pft.app.data.common

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.ledger.ids.UserId
import java.time.Instant

/**
 * The one mapping between core's [EntityMeta] and the shared [MetaColumns], used by every entity's
 * mapper. Times are stored as epoch milliseconds, so finer precision is dropped; the app's clock
 * is millisecond-resolution, which keeps a stored entity equal to the one that was written.
 */
object MetaMapper {
    /** The columns for [meta]. */
    fun <Id : EntityId> toColumns(meta: EntityMeta<Id>): MetaColumns =
        MetaColumns(
            id = meta.id.value,
            createdAt = meta.createdAt.toEpochMilli(),
            updatedAt = meta.updatedAt.toEpochMilli(),
            createdByUserId = meta.createdByUserId.value,
            createdOnDeviceId = meta.createdOnDeviceId.value,
            deletedAt = meta.deletedAt?.toEpochMilli(),
            archivedAt = meta.archivedAt?.toEpochMilli(),
        )

    /** The meta stored in [columns]; [toId] builds the entity's typed id from its text. */
    fun <Id : EntityId> toMeta(
        columns: MetaColumns,
        toId: (String) -> Id,
    ): EntityMeta<Id> =
        EntityMeta(
            id = toId(columns.id),
            createdAt = Instant.ofEpochMilli(columns.createdAt),
            updatedAt = Instant.ofEpochMilli(columns.updatedAt),
            createdByUserId = UserId(columns.createdByUserId),
            createdOnDeviceId = DeviceId(columns.createdOnDeviceId),
            deletedAt = columns.deletedAt?.let(Instant::ofEpochMilli),
            archivedAt = columns.archivedAt?.let(Instant::ofEpochMilli),
        )
}
