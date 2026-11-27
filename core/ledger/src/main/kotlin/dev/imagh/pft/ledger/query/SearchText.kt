package dev.imagh.pft.ledger.query

/**
 * Turns what a person typed into the terms of an FTS4 `MATCH`, one per word (slice C D6). **This is the only
 * way search text reaches the database**, and each term is always bound as a parameter, never spliced into
 * SQL (CLAUDE.md Rule C).
 *
 * FTS4's query language has operators (`OR`, `NOT`, `NEAR/n`, `-`, `*`, `col:`, quotes, brackets) and a
 * malformed expression throws, so typed text is never passed through. Instead it is cut into words, and
 * each word becomes a **quoted prefix phrase** `"word*"`. Inside quotes every operator is an ordinary word.
 * The query then ANDs the terms itself, one `MATCH` per term, so each word may be found anywhere in its
 * scope (title, description or any item's note) and not necessarily next to the others. Nothing but quotes,
 * stars, letters, digits and combining marks can come out, so no input can build an operator, a column filter
 * (`:` is a separator, which also keeps the column a `MATCH` is restricted to, because FTS4 lets an in-query
 * `col:` override it) or an unbalanced quote.
 *
 * A word is a run of letters, digits and **combining marks** (`\p{M}`), so a vowel sign stays in the word
 * it belongs to in the *query*. Whether the *index* keeps such a word whole is the tokenizer's business:
 * `unicode61` treats marks as separators, so the Devanagari marks are declared token characters on both full-text
 * tables (`TransactionFtsEntity`). **Other Indic scripts (Bengali, Tamil, ...) have the same problem and would need
 * the same treatment**, and changing a table's tokenizer is a drop, recreate and `rebuild` migration. Everything
 * that is not a letter, digit or mark (spaces, punctuation, NUL, a lone surrogate) separates words. A run with no
 * letter or digit in it (a stray mark) is not a word. Matching is case-insensitive and ignores Latin accents
 * because `unicode61` folds both sides.
 *
 * The caps bound the work one search can cause: at most [MAX_TOKENS] words, each at most [MAX_TOKEN_LENGTH]
 * characters (code points, so a surrogate pair is never cut).
 */
object SearchText {
    /** Most words kept; the rest of the text is ignored. */
    const val MAX_TOKENS = 8

    /** Longest word kept, in code points; a longer word is cut. */
    const val MAX_TOKEN_LENGTH = 32

    private val MARK_TYPES =
        setOf(
            Character.NON_SPACING_MARK.toInt(),
            Character.COMBINING_SPACING_MARK.toInt(),
            Character.ENCLOSING_MARK.toInt(),
        )

    /**
     * The `MATCH` terms for [raw], one `"word*"` per word, in the order typed; an **empty list** when no word is
     * left (empty, blank or punctuation-only text).
     *
     * **Empty means "searches for nothing", not "everything"**: a caller that was given text must return no
     * matches for an empty result (fail closed), and pass no text at all to mean "no text filter".
     */
    fun toMatchTerms(raw: String): List<String> {
        val words = mutableListOf<String>()
        val word = StringBuilder()
        var length = 0
        var hasBase = false

        fun finish() {
            if (hasBase && words.size < MAX_TOKENS) words += word.toString()
            word.setLength(0)
            length = 0
            hasBase = false
        }
        raw.codePoints().forEach { cp ->
            val isBase = Character.isLetterOrDigit(cp)
            if (isBase || Character.getType(cp) in MARK_TYPES) {
                if (length < MAX_TOKEN_LENGTH) {
                    word.appendCodePoint(cp)
                    length++
                    hasBase = hasBase || isBase
                }
            } else {
                finish()
            }
        }
        finish()
        return words.map { "\"$it*\"" }
    }
}
