package dev.imagh.pft.app.data.common

import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * The one rule for timestamps in the database: **epoch milliseconds UTC, exactly** (`INTEGER`).
 *
 * Every time written to a Room column goes through [of], which refuses an instant finer than a
 * millisecond instead of rounding it. Rounding would make a written entity differ from the one read
 * back, and a record's `updated_at` differ from its audit entry's `at`; refusing makes a wrong clock
 * fail loudly on the first write (nothing is stored: the write runs in a transaction). The app builds
 * the `Clock` it gives core with [clock], so every instant core produces is already storable.
 */
object EpochMillis {
    private const val NANOS_PER_MILLI = 1_000_000

    /**
     * [instant] as epoch milliseconds.
     *
     * @throws IllegalArgumentException if [instant] has sub-millisecond precision. The message does
     *   not include the value.
     */
    fun of(instant: Instant): Long {
        require(instant.nano % NANOS_PER_MILLI == 0) {
            "A timestamp has sub-millisecond precision: give core a millisecond clock (EpochMillis.clock)"
        }
        return instant.toEpochMilli()
    }

    /** [base] truncated to whole milliseconds: the clock the app hands to `AuditedWriter` and the services. */
    fun clock(base: Clock = Clock.systemUTC()): Clock = Clock.tick(base, Duration.ofMillis(1))
}
