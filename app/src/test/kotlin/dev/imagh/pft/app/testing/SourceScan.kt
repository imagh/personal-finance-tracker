package dev.imagh.pft.app.testing

import java.io.File

/**
 * The text scanner behind the source-policy tests (`ThemeLiteralScanTest`, `LoggingScanTest`): it
 * strips comments, applies a list of forbidden-pattern regexes line by line, and lists the Kotlin
 * files under some roots.
 *
 * It is syntactic, so it has known limits that review still covers:
 * - an alias import slips past;
 * - the scan is per line, so a line break inside a call or before `.name` is missed;
 * - comment stripping is naive, so a line-comment or block-comment opener inside a string literal
 *   blanks the rest of that line (or a block) and can hide a hit.
 *
 * ktlint formatting keeps a call and its receiver on one line, which makes those cases unlikely.
 */
object SourceScan {
    /** One forbidden hit: its 1-based [line] and the offending [text]. */
    data class Violation(
        val line: Int,
        val text: String,
    )

    /** Blanks comments (keeping line numbers) so a KDoc that mentions a forbidden call is not a hit. */
    fun stripComments(source: String): String {
        val withoutBlocks =
            Regex("""/\*[\s\S]*?\*/""").replace(source) { match -> match.value.filter { it == '\n' } }
        return withoutBlocks.lines().joinToString("\n") { it.replace(Regex("""//.*$"""), "") }
    }

    /** Applies each of [rules] to every non-comment line of [source]; one [Violation] per rule per line. */
    fun scan(
        source: String,
        rules: List<Regex>,
    ): List<Violation> =
        stripComments(source).lines().flatMapIndexed { index, line ->
            rules.mapNotNull { rule -> rule.find(line)?.let { Violation(index + 1, it.value) } }
        }

    /** Every `.kt` file under [roots]. Fails if a root is not a directory, so a wrong working directory is loud. */
    fun kotlinFiles(roots: List<File>): List<File> {
        val missing = roots.filterNot { it.isDirectory }
        check(missing.isEmpty()) {
            "run from the app module directory (${File(".").absolutePath}); " +
                "missing: ${missing.joinToString { it.path }}"
        }
        return roots.flatMap { root -> root.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList() }
    }
}
