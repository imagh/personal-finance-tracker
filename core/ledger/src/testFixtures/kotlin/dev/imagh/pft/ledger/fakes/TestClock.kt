package dev.imagh.pft.ledger.fakes

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A [Clock] that tests move by hand, so timestamps and time-sortable ids are deterministic.
 * Starts at 2026-11-01T10:00:00Z unless told otherwise.
 */
class TestClock(
    start: Instant = Instant.parse("2026-11-01T10:00:00Z"),
) : Clock() {
    private var now: Instant = start

    override fun getZone(): ZoneId = ZoneOffset.UTC

    override fun withZone(zone: ZoneId?): Clock = this

    override fun instant(): Instant = now

    /** Moves the clock forward (or backward, for a negative [by]). */
    fun advance(by: Duration) {
        now = now.plus(by)
    }

    /** Jumps to [instant]. */
    fun set(instant: Instant) {
        now = instant
    }
}
