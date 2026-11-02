package dev.imagh.pft.app.log

import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import java.time.Duration

/**
 * A [LogEvent] can hold no text (SEC-5, CLAUDE.md §3.7): every field of every subclass is a number,
 * a boolean, an enum or a [Duration]. This is what makes it impossible to put an amount, a payee, a
 * name or a path in a log line.
 */
class LogEventShapeTest {
    /** One sample of every subclass. Add a subclass and [covered] stops compiling until you list it. */
    private val samples: List<LogEvent> =
        listOf(
            LogEvent.DbOpened(schemaVersion = 1, took = Duration.ofMillis(12)),
            LogEvent.DbKeyUnavailable(Reason.KEK_MISSING),
            LogEvent.IdentityCorrupt,
            LogEvent.SetupCompleted,
            LogEvent.SetupFailed,
        )

    /** Exhaustive with no `else`: a new [LogEvent] subclass without a sample is a compile error. */
    @Suppress("unused")
    private fun covered(e: LogEvent): Unit =
        when (e) {
            is LogEvent.DbOpened -> Unit
            is LogEvent.DbKeyUnavailable -> Unit
            is LogEvent.IdentityCorrupt -> Unit
            is LogEvent.SetupCompleted -> Unit
            is LogEvent.SetupFailed -> Unit
        }

    private fun isAllowed(type: Class<*>): Boolean =
        type == Int::class.javaPrimitiveType ||
            type == Long::class.javaPrimitiveType ||
            type == Boolean::class.javaPrimitiveType ||
            type.isEnum ||
            type == Duration::class.java

    private fun badFields(type: Class<*>): List<String> =
        type.declaredFields
            .filterNot { Modifier.isStatic(it.modifiers) || it.isSynthetic }
            .filterNot { isAllowed(it.type) }
            .map { "${type.simpleName}.${it.name}: ${it.type.name}" }

    @Test
    fun `every field of every log event is a number, boolean, enum or duration`() {
        val bad = samples.flatMap { badFields(it.javaClass) }
        assertEquals(emptyList<String>(), bad)
    }

    @Test
    fun `the samples cover every subclass`() {
        val permitted = LogEvent::class.java.permittedSubclasses
        val declared = permitted.orEmpty().toSet()
        assertTrue(declared.isNotEmpty(), "LogEvent must be a sealed interface")
        assertEquals(declared, samples.map { it.javaClass }.toSet())
    }

    /** The rendered line, not only the field types, must be codes and numbers: no free text. */
    private val lineShape = Regex("^[a-z_]+( [a-z_]+=[A-Z0-9_]+)*$")

    @Test
    fun `every sample renders a line of lowercase name and UPPER_CASE or numeric values only`() {
        samples.forEach { event ->
            assertTrue(lineShape.matches(event.line()), "${event.javaClass.simpleName} rendered: ${event.line()}")
        }
    }

    @Test
    fun `the line check can fail, so it is not vacuous`() {
        val free =
            listOf(
                "db_opened note=Paid to Ramesh",
                "db_opened schema=1 path=/data/user/0/dev.imagh.pft",
                "Db_Opened schema=1",
                "db_opened name=akash",
                "db_opened schema=1.5",
            )
        free.forEach { assertFalse(lineShape.matches(it), "should be rejected: $it") }
    }

    private class TextBearing(
        @Suppress("unused") val message: String,
        @Suppress("unused") val count: Int,
    )

    @Test
    fun `the check can fail, so it is not vacuous`() {
        val bad = badFields(TextBearing::class.java)
        assertEquals(1, bad.size)
        assertTrue(bad.single().endsWith("java.lang.String"))
        assertFalse(isAllowed(Any::class.java))
        assertFalse(isAllowed(Throwable::class.java))
    }
}
