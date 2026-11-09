package dev.imagh.pft.app.log

import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.time.Duration

/** [SafeLog.event] writes exactly the event's line, under the fixed tag, and nothing else. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SafeLogTest {
    /** `android.util.Log.INFO`, spelled out so this file needs no import of the banned class. */
    private val infoPriority = android.util.Log.INFO

    @Test
    fun `an event is written as one info line under the pft tag`() {
        ShadowLog.clear()

        SafeLog.event(LogEvent.DbOpened(schemaVersion = 1, took = Duration.ofMillis(12)))

        val logs = ShadowLog.getLogs()
        logs.size shouldBe 1
        logs.single().tag shouldBe "pft"
        logs.single().type shouldBe infoPriority
        logs.single().msg shouldBe "db_opened schema=1 ms=12"
        logs.single().throwable shouldBe null
    }

    @Test
    fun `each event is its own line`() {
        ShadowLog.clear()

        SafeLog.event(LogEvent.DbKeyUnavailable(Reason.KEK_MISSING))
        SafeLog.event(LogEvent.DbOpened(1, Duration.ZERO))

        ShadowLog.getLogs().map { it.msg } shouldBe
            listOf("db_key_unavailable reason=KEK_MISSING", "db_opened schema=1 ms=0")
    }
}
