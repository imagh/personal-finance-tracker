package dev.imagh.pft.app.log

import dev.imagh.pft.app.testing.SourceScan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/**
 * All logging goes through [SafeLog] (PRD SEC-5, CLAUDE.md §3.7). detekt's `ForbiddenImport` bans
 * `import android.util.Log` everywhere but `SafeLog.kt`; this scan covers what an import ban cannot
 * see: `println`/`print`, `System.out`/`System.err`, `printStackTrace`, any fully qualified
 * `android.util.Log` reference (a call or a callable reference like `android.util.Log::d`),
 * `java.util.logging`, `System.getLogger` and `Timber`. It scans every production source set of `app` and of every
 * `core` module.
 *
 * It is a syntactic text scan with the limits listed on [SourceScan]; review covers the rest.
 */
class LoggingScanTest {
    private val forbidden =
        listOf(
            Regex("""\bprintln\s*\("""),
            Regex("""(?<![\w.])print\s*\("""),
            Regex("""\bSystem\s*\.\s*(?:out|err)\b"""),
            Regex("""\bprintStackTrace\s*\("""),
            Regex("""\bandroid\.util\.Log\b"""),
            Regex("""\bjava\.util\.logging\b"""),
            Regex("""\bSystem\.getLogger\b"""),
            Regex("""\bTimber\b"""),
        )

    private fun scan(source: String) = SourceScan.scan(source, forbidden)

    @Test
    fun `the scanner flags every forbidden logging call`() {
        val fixture =
            """
            fun f(e: Exception) {
                println("x")
                System.out.print("y")
                e.printStackTrace()
                android.util.Log.d("t", "m")
                print("z")
                System.err.println("w")
            }
            """.trimIndent()
        assertEquals(listOf(2, 3, 4, 5, 6, 7, 7), scan(fixture).map { it.line })
    }

    @Test
    fun `the scanner passes the same tokens inside comments and unrelated identifiers`() {
        val clean =
            """
            /** Never println("x") or System.out; call e.printStackTrace() nowhere. */
            // android.util.Log.d("t", "m") is banned
            val a = SafeLog.event(event)
            val b = footprint(1)
            val c = pretty.print(x)
            val d = blueprint(2)
            """.trimIndent()
        assertEquals(emptyList<SourceScan.Violation>(), scan(clean))
    }

    @Test
    fun `the scanner flags other logging APIs, imports and callable references`() {
        val fixture =
            """
            import android.util.Log
            import java.util.logging.Logger
            val a = java.util.logging.Logger.getLogger("x")
            val b = System.getLogger("x")
            val c = Timber.d("x")
            val d = android.util.Log::d
            """.trimIndent()
        assertEquals(listOf(1, 2, 3, 4, 5, 6), scan(fixture).map { it.line })
    }

    @Test
    fun `the scanner passes lookalikes and mentions in comments`() {
        val clean =
            """
            /** Not Timber, not System.getLogger, not java.util.logging, not android.util.Log. */
            // Timber.d("x") is banned too
            val a = TimberLike()
            val b = android.util.Logger
            val c = com.example.java.util.loggingFoo
            val d = SafeLog.event(event)
            """.trimIndent()
        assertEquals(emptyList<SourceScan.Violation>(), scan(clean))
    }

    @Test
    fun `no production source outside SafeLog logs directly`() {
        val roots =
            listOf("src/main/kotlin", "src/debug/kotlin", "src/release/kotlin").map { File(it) } +
                File("../core").listFiles { f -> f.isDirectory }.orEmpty().map { File(it, "src/main/kotlin") }
        val files = SourceScan.kotlinFiles(roots)
        assertTrue(files.size > 10, "the scan found only ${files.size} files, so it is not looking at the tree")
        assertTrue(files.any { it.path.contains("core/") }, "the scan did not reach any core module")

        val hits =
            files
                .filterNot { it.invariantSeparatorsPath.endsWith("/dev/imagh/pft/app/log/SafeLog.kt") }
                .flatMap { file -> scan(file.readText()).map { "${file.path}:${it.line}: ${it.text}" } }
        assertTrue(hits.isEmpty(), "Log through SafeLog.event(LogEvent) instead:\n${hits.joinToString("\n")}")
    }
}
