package dev.imagh.pft.app.testing

import android.os.Looper
import org.robolectric.Shadows.shadowOf

/**
 * Lets the main looper run until [condition] holds, for a test of code that resumes on the main
 * dispatcher (a ViewModel's `viewModelScope`). Robolectric's main thread is the test thread, so a
 * blocking wait would deadlock the looper the coroutine needs; this idles the looper in a loop
 * instead, and Room's own executors finish the database work in between.
 *
 * Plain real-time polling with a deadline (no sleeps in the happy path beyond a 1 ms yield), the
 * same style as `runSuspending`.
 *
 * @param timeoutMillis how long to wait before failing the test.
 * @throws AssertionError if [condition] is still false after [timeoutMillis].
 */
fun awaitMain(
    timeoutMillis: Long = 10_000,
    condition: () -> Boolean,
) {
    val deadline = System.nanoTime() + timeoutMillis * NANOS_PER_MILLI
    while (true) {
        shadowOf(Looper.getMainLooper()).idle()
        if (condition()) return
        if (System.nanoTime() > deadline) throw AssertionError("condition not met within $timeoutMillis ms")
        Thread.sleep(1)
    }
}

private const val NANOS_PER_MILLI = 1_000_000L
