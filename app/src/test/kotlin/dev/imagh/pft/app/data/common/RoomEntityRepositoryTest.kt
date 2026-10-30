package dev.imagh.pft.app.data.common

import android.database.sqlite.SQLiteConstraintException
import dev.imagh.pft.app.testing.SampleId
import dev.imagh.pft.app.testing.SampleThing
import dev.imagh.pft.app.testing.SampleThingEntity
import dev.imagh.pft.app.testing.SampleThingMapper
import dev.imagh.pft.app.testing.SampleThingRepository
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/** The shared [RoomEntityRepository] base, exercised through a throwaway entity on a real database. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomEntityRepositoryTest {
    private val database = TestDatabase.createWithSample()
    private val repository = SampleThingRepository(database.sampleThingDao())
    private val now = Instant.parse("2026-11-01T10:00:00Z")

    @After
    fun closeDatabase() = database.close()

    private fun thing(
        id: String = "s-1",
        name: String = "Chai",
        price: Paise = Paise(2_500),
        note: String? = "hot",
        deletedAt: Instant? = null,
        archivedAt: Instant? = null,
    ) = SampleThing(
        EntityMeta(SampleId(id), now, now, UserId("user-1"), DeviceId("device-1"), deletedAt, archivedAt),
        name,
        price,
        note,
    )

    @Test
    fun `the repository names its kind`() {
        repository.entityType shouldBe "sample_thing"
    }

    @Test
    fun `an inserted entity is read back whole, money and optional fields included`() =
        runSuspending {
            val withNote = thing(price = Paise(9_999_999_999_00L))
            val withoutNote = thing(id = "s-2", note = null)

            repository.insert(withNote)
            repository.insert(withoutNote)

            repository.get(withNote.meta.id) shouldBe withNote
            repository.get(withoutNote.meta.id) shouldBe withoutNote
            repository.get(SampleId("missing")) shouldBe null
        }

    @Test
    fun `inserting an existing id fails and keeps the stored row`() =
        runSuspending {
            repository.insert(thing())

            shouldThrow<SQLiteConstraintException> { repository.insert(thing(name = "Other")) }

            repository.get(SampleId("s-1"))?.name shouldBe "Chai"
        }

    @Test
    fun `update replaces the stored entity`() =
        runSuspending {
            repository.insert(thing())
            val edited = thing(name = "Coffee", note = null)

            repository.update(edited)

            repository.get(SampleId("s-1")) shouldBe edited
        }

    @Test
    fun `updating an entity that is not stored fails instead of doing nothing`() =
        runSuspending {
            val failure = shouldThrow<IllegalStateException> { repository.update(thing(id = "ghost")) }

            failure.message.orEmpty() shouldNotContain "ghost"
            repository.get(SampleId("ghost")) shouldBe null
        }

    @Test
    fun `getLive hides a tombstone that get still returns, and keeps an archived entity`() =
        runSuspending {
            val deleted = thing(id = "gone", deletedAt = now)
            val archived = thing(id = "old", archivedAt = now)
            repository.insert(deleted)
            repository.insert(archived)

            repository.get(deleted.meta.id) shouldBe deleted
            repository.getLive(deleted.meta.id) shouldBe null
            repository.getLive(archived.meta.id) shouldBe archived
        }

    @Test
    fun `an entity whose audit type differs from the repository's kind is rejected`() =
        runSuspending {
            // A repository that claims another kind than the entities it is given.
            val wrongKind =
                RoomEntityRepository<SampleThing, SampleThingEntity, SampleId>(
                    database.sampleThingDao(),
                    "account",
                    SampleThingMapper,
                )

            shouldThrow<IllegalArgumentException> { wrongKind.insert(thing()) }
            shouldThrow<IllegalArgumentException> { wrongKind.update(thing()) }

            repository.get(SampleId("s-1")) shouldBe null
        }
}
