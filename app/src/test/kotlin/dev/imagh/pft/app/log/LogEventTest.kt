package dev.imagh.pft.app.log

import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

/** How each [LogEvent] renders: the exact line is the contract the logcat check (plan §11) relies on. */
class LogEventTest {
    @Test
    fun `db_opened renders the schema version and whole milliseconds`() {
        val event = LogEvent.DbOpened(schemaVersion = 1, took = Duration.ofMillis(12))
        assertEquals("db_opened schema=1 ms=12", event.line())
    }

    @Test
    fun `db_key_unavailable renders the reason code`() {
        assertEquals("db_key_unavailable reason=KEK_MISSING", LogEvent.DbKeyUnavailable(Reason.KEK_MISSING).line())
    }

    @Test
    fun `a zero duration renders as 0 ms`() {
        assertEquals("db_opened schema=1 ms=0", LogEvent.DbOpened(1, Duration.ZERO).line())
    }

    @Test
    fun `a duration over a minute renders as integer milliseconds`() {
        assertEquals("db_opened schema=2 ms=61500", LogEvent.DbOpened(2, Duration.ofMillis(61_500)).line())
    }

    @Test
    fun `sub-millisecond precision is dropped, not rounded up or shown as a fraction`() {
        assertEquals("db_opened schema=1 ms=3", LogEvent.DbOpened(1, Duration.ofNanos(3_999_999)).line())
    }
}
