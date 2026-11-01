package dev.imagh.pft.app.ui.theme

import dev.imagh.pft.app.testing.SourceScan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Screens never hardcode colours or fonts (plan §3.9): no `Color(...)`, no named `Color.*`
 * constant, no `FontFamily(...)`, no `FontFamily.Serif`/`SansSerif`/`Monospace`/`Default` and no
 * `R.font.*` outside `ui/theme`. Only `Color.Transparent` and `Color.Unspecified` are allowed,
 * because they carry no design value. This is what lets a palette or font change reach every
 * screen, present and future, through the theme alone.
 *
 * It is a text scan, not a detekt rule: detekt runs here without type resolution, and banning a
 * constructor call would need a custom rule module. The scanner is the shared [SourceScan], whose
 * syntactic limits (alias imports, line breaks inside a call, comment openers inside strings) review
 * still covers. XML colour resources and `colorResource(...)` are not scanned.
 */
class ThemeLiteralScanTest {
    private val forbidden =
        listOf(
            Regex("""\bColor\s*\("""),
            Regex("""\bColor\.(?!Transparent\b|Unspecified\b)\w+"""),
            Regex("""\bFontFamily\s*\("""),
            Regex("""\bFontFamily\.(?:Serif|SansSerif|Monospace|Cursive|Default)\b"""),
            Regex("""\bR\.font\.\w+"""),
        )

    private fun scan(source: String) = SourceScan.scan(source, forbidden)

    @Test
    fun `the scanner flags every forbidden colour and font literal`() {
        val fixture =
            """
            val a = Color(0xFF112233)
            val b = Color.Red
            val c = Color.White
            val d = FontFamily(Font(R.font.figtree))
            val e = FontFamily.Serif
            val f = FontFamily.Default
            """.trimIndent()
        assertEquals(
            listOf(1, 2, 3, 4, 4, 5, 6),
            scan(fixture).map { it.line },
        )
    }

    @Test
    fun `the scanner passes clean code, including Transparent, Unspecified and comments`() {
        val clean =
            """
            /** Mentions Color(0xFF112233) and FontFamily.Serif only in a KDoc. */
            val a = Color.Transparent
            val b = Color.Unspecified // not Color.Red
            val c = MaterialTheme.colorScheme.primary
            val d = lightColorScheme(primary = tokens.accent)
            val e = ColorScheme.default
            """.trimIndent()
        assertEquals(emptyList<SourceScan.Violation>(), scan(clean))
    }

    @Test
    fun `no source file outside ui theme hardcodes a colour or a font`() {
        val files =
            SourceScan
                .kotlinFiles(listOf(File("src/main/kotlin"), File("src/debug/kotlin")))
                .filterNot { it.invariantSeparatorsPath.contains("/dev/imagh/pft/app/ui/theme/") }
        assertTrue(files.size > 10, "the scan found only ${files.size} files, so it is not looking at the source tree")

        val hits = files.flatMap { file -> scan(file.readText()).map { "${file.path}:${it.line}: ${it.text}" } }
        assertTrue(hits.isEmpty(), "Move these to ui/theme tokens (never allowlist them):\n${hits.joinToString("\n")}")
    }
}
