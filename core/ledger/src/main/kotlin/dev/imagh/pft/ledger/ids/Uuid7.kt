package dev.imagh.pft.ledger.ids

import java.security.SecureRandom
import java.time.Clock
import java.util.Random
import java.util.UUID

/**
 * Generates UUIDv7 identifiers (RFC 9562 §5.7) as 36-character lower-case text.
 *
 * Written by hand because Kotlin 2.1's `kotlin.uuid` cannot generate v7. Layout: a 48-bit Unix
 * millisecond timestamp, the version nibble `7`, a 12-bit counter, the variant bits `10`, then 62
 * random bits. Because the timestamp leads, IDs sort by creation time, which keeps primary-key
 * inserts append-friendly and lets audit rows be ordered by id.
 *
 * Within one process IDs are strictly increasing, even inside a single millisecond (the counter,
 * RFC 9562 §6.2 method 1) and even if the clock steps backwards (the last timestamp is reused).
 *
 * Randomness comes from [SecureRandom] by default, so IDs are not guessable. Thread-safe.
 *
 * @param clock the time source; tests inject a fixed or steppable clock.
 * @param random the randomness source; tests inject a seeded one.
 */
class Uuid7(
    private val clock: Clock = Clock.systemUTC(),
    private val random: Random = SecureRandom(),
) {
    private var lastMillis = -1L
    private var counter = 0

    /**
     * The next identifier, e.g. `01934f5e-7c3a-7b21-9d4e-5a6f8c1b2d3e`.
     *
     * @throws IllegalArgumentException if the clock is outside the 48-bit range (before 1970 or
     *   after year 10889), or if 4096 IDs in the last representable millisecond would need a
     *   millisecond past it. Either would silently corrupt the timestamp.
     */
    @Synchronized
    fun next(): String {
        var millis = maxOf(clock.millis(), lastMillis)
        require(millis in 0..MAX_MILLIS) { RANGE_MESSAGE }
        var nextCounter = if (millis == lastMillis) counter + 1 else freshCounter()
        if (nextCounter > MAX_COUNTER) {
            // 4096 IDs in one millisecond: borrow the next millisecond rather than repeat it,
            // unless that would leave the 48-bit range (fail closed; state is left untouched).
            millis++
            require(millis <= MAX_MILLIS) { RANGE_MESSAGE }
            nextCounter = freshCounter()
        }
        counter = nextCounter
        lastMillis = millis

        val high = (millis shl TIMESTAMP_SHIFT) or VERSION_7 or counter.toLong()
        val low = (random.nextLong() and RANDOM_B_MASK) or VARIANT_10
        return UUID(high, low).toString()
    }

    /** Starts a new millisecond's counter in the lower half, leaving room to count up. */
    private fun freshCounter(): Int = random.nextInt(COUNTER_SEED_BOUND)

    private companion object {
        const val RANGE_MESSAGE = "Clock is outside the 48-bit UUIDv7 range"
        const val MAX_MILLIS = 0xFFFF_FFFF_FFFFL
        const val TIMESTAMP_SHIFT = 16
        const val VERSION_7 = 0x7000L
        const val MAX_COUNTER = 0x0FFF
        const val COUNTER_SEED_BOUND = 0x0800
        const val RANDOM_B_MASK = 0x3FFF_FFFF_FFFF_FFFFL
        const val VARIANT_10 = Long.MIN_VALUE
    }
}
