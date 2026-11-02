package dev.imagh.pft.app.data.common

import dev.imagh.pft.app.testing.SampleId
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Instant

/** [MetaMapper]: the one mapping between core's `EntityMeta` and the shared Room columns. */
class MetaMapperTest {
    private val created = Instant.parse("2026-11-01T10:00:00.250Z")
    private val meta =
        EntityMeta(
            id = SampleId("s-1"),
            createdAt = created,
            updatedAt = created.plusSeconds(60),
            createdByUserId = UserId("user-1"),
            createdOnDeviceId = DeviceId("device-1"),
        )

    @Test
    fun `meta maps to columns holding ids as text and times as epoch milliseconds`() {
        MetaMapper.toColumns(meta) shouldBe
            MetaColumns(
                id = "s-1",
                createdAt = 1_793_527_200_250L,
                updatedAt = 1_793_527_260_250L,
                createdByUserId = "user-1",
                createdOnDeviceId = "device-1",
                deletedAt = null,
                archivedAt = null,
            )
    }

    @Test
    fun `columns map back to the same meta`() {
        MetaMapper.toMeta(MetaMapper.toColumns(meta), ::SampleId) shouldBe meta
    }

    @Test
    fun `deleted and archived times survive`() {
        val both = meta.copy(deletedAt = created.plusSeconds(5), archivedAt = created.plusSeconds(3))

        val columns = MetaMapper.toColumns(both)

        columns.deletedAt shouldBe created.plusSeconds(5).toEpochMilli()
        columns.archivedAt shouldBe created.plusSeconds(3).toEpochMilli()
        MetaMapper.toMeta(columns, ::SampleId) shouldBe both
    }

    @Test
    fun `any time with sub-millisecond precision is refused rather than silently rounded`() {
        val fine = created.plusNanos(1)

        listOf(
            meta.copy(createdAt = fine),
            meta.copy(updatedAt = fine),
            meta.copy(deletedAt = fine),
            meta.copy(archivedAt = fine),
        ).forEach { shouldThrow<IllegalArgumentException> { MetaMapper.toColumns(it) } }
    }
}
