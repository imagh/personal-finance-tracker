package dev.imagh.pft.app.data.common

import dev.imagh.pft.ledger.fakes.TestClock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.Test
import java.time.Instant

/** [EpochMillis]: timestamps are stored as epoch milliseconds and anything finer is refused, not dropped. */
class EpochMillisTest {
    @Test
    fun `a whole-millisecond instant maps to its epoch milliseconds`() {
        EpochMillis.of(Instant.parse("2026-11-01T10:00:00.123Z")) shouldBe 1_793_527_200_123L
        EpochMillis.of(Instant.parse("1969-12-31T23:59:59.999Z")) shouldBe -1L
    }

    @Test
    fun `an instant with sub-millisecond precision is refused without echoing it`() {
        val failure =
            shouldThrow<IllegalArgumentException> {
                EpochMillis.of(Instant.parse("2026-11-01T10:00:00.123456789Z"))
            }

        failure.message.orEmpty() shouldNotContain "123456789"
    }

    @Test
    fun `a millisecond clock truncates its source so every instant it gives is storable`() {
        val source = TestClock(Instant.parse("2026-11-01T10:00:00.123456789Z"))
        val clock = EpochMillis.clock(source)

        clock.instant() shouldBe Instant.parse("2026-11-01T10:00:00.123Z")
        EpochMillis.of(clock.instant()) shouldBe 1_793_527_200_123L
        source.set(Instant.parse("2026-11-01T10:00:00.999999999Z"))
        clock.instant() shouldBe Instant.parse("2026-11-01T10:00:00.999Z")
    }

    @Test
    fun `the default millisecond clock is storable`() {
        EpochMillis.of(EpochMillis.clock().instant()) // does not throw
    }
}
