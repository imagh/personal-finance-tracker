// The one place that may touch android.util.Log: detekt's ForbiddenImport bans it everywhere else
// (config/detekt/detekt.yml), and LoggingScanTest scans the rest of the tree for direct logging.
@file:Suppress("ForbiddenImport")

package dev.imagh.pft.app.log

import android.util.Log

/**
 * The only way the app logs (PRD SEC-5, CLAUDE.md §3.7). It takes a typed [LogEvent], which cannot
 * hold text, so a message, an amount, a payee, a name, a path or a token cannot reach logcat. There is
 * deliberately no overload taking a `String`, a `Throwable` or an `Any`.
 *
 * Release builds log too (plan decision Q3): the events are non-sensitive and logcat is readable only
 * over adb.
 */
object SafeLog {
    /** The logcat tag of every line the app writes. */
    const val TAG = "pft"

    /** Writes [e] as one info line, `e.line()`, under [TAG]. */
    fun event(e: LogEvent) {
        Log.i(TAG, e.line())
    }
}
