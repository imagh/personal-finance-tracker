package dev.imagh.pft.app.testing

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Query
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.audit.AuditEntryEntity
import dev.imagh.pft.app.data.common.BaseDao
import dev.imagh.pft.app.data.common.EntityMapper
import dev.imagh.pft.app.data.common.MetaColumns
import dev.imagh.pft.app.data.common.MetaMapper
import dev.imagh.pft.app.data.common.RoomEntityRepository
import dev.imagh.pft.app.data.identity.DeviceEntity
import dev.imagh.pft.app.data.identity.UserProfileEntity
import dev.imagh.pft.app.data.settings.AppSettingEntity
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.ledger.ids.EntityId
import dev.imagh.pft.money.Paise

/*
 * A throwaway ledger entity used to test the shared Room building blocks (plan §3.4) before the
 * first real entity (B1's account) exists: model, Room row, DAO, mapper, repository and a database
 * that adds the `sample_thing` table to everything PftDatabase has. Test code only, never shipped.
 * It uses every kind of column an entity has: text, optional text, money, and the shared meta.
 */

@JvmInline
value class SampleId(
    override val value: String,
) : EntityId

/** A fully fledged ledger entity with a money field and an optional field. */
data class SampleThing(
    override val meta: EntityMeta<SampleId>,
    val name: String,
    val price: Paise,
    val note: String?,
) : LedgerEntity<SampleId, SampleThing> {
    override val auditType: String get() = "sample_thing"

    override fun auditFields(): Map<String, String?> =
        mapOf("name" to name, "price" to price.value.toString(), "note" to note)

    override fun withMeta(meta: EntityMeta<SampleId>) = copy(meta = meta)
}

@Entity(tableName = "sample_thing", primaryKeys = ["id"])
data class SampleThingEntity(
    @Embedded val meta: MetaColumns,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Long,
    @ColumnInfo(name = "note") val note: String?,
)

@Dao
interface SampleThingDao : BaseDao<SampleThingEntity> {
    @Query("SELECT * FROM sample_thing WHERE id = :id")
    override suspend fun byId(id: String): SampleThingEntity?
}

object SampleThingMapper : EntityMapper<SampleThing, SampleThingEntity> {
    override fun toRow(model: SampleThing) =
        SampleThingEntity(MetaMapper.toColumns(model.meta), model.name, model.price.value, model.note)

    override fun toModel(row: SampleThingEntity) =
        SampleThing(MetaMapper.toMeta(row.meta, ::SampleId), row.name, Paise(row.price), row.note)
}

/** The repository a real entity would write: the shared base plus its mapper and DAO, nothing else. */
class SampleThingRepository(
    dao: SampleThingDao,
) : RoomEntityRepository<SampleThing, SampleThingEntity, SampleId>(dao, "sample_thing", SampleThingMapper)

/** [PftDatabase] plus the `sample_thing` table, so the audit trigger and transactions are the real ones. */
@Database(
    entities = [
        UserProfileEntity::class,
        DeviceEntity::class,
        AppSettingEntity::class,
        AuditEntryEntity::class,
        SampleThingEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class SampleDatabase : PftDatabase() {
    abstract fun sampleThingDao(): SampleThingDao
}
