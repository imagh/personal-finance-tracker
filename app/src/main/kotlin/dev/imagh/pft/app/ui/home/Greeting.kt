package dev.imagh.pft.app.ui.home

import java.time.Clock
import java.time.LocalTime

/** The part of the day Home greets for (plan D9). Which words to show is the screen's job. */
enum class Greeting {
    /** 05:00 to 11:59. */
    MORNING,

    /** 12:00 to 16:59. */
    AFTERNOON,

    /** 17:00 to 04:59. */
    EVENING,
}

private const val MORNING_STARTS_HOUR = 5
private const val AFTERNOON_STARTS_HOUR = 12
private const val EVENING_STARTS_HOUR = 17

/**
 * The [Greeting] for the local time on [clock] (its own zone): 05:00 to 11:59 morning, 12:00 to 16:59
 * afternoon, everything else evening. Pure: the caller injects the clock, so tests fix the hour and the
 * zone.
 */
fun greetingAt(clock: Clock): Greeting {
    val hour = LocalTime.now(clock).hour
    return when {
        hour in MORNING_STARTS_HOUR until AFTERNOON_STARTS_HOUR -> Greeting.MORNING
        hour in AFTERNOON_STARTS_HOUR until EVENING_STARTS_HOUR -> Greeting.AFTERNOON
        else -> Greeting.EVENING
    }
}
