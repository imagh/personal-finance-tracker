package dev.imagh.pft.app.ui.home

import java.text.BreakIterator
import java.util.Locale

/**
 * The avatar's initials for [name]: the first grapheme cluster of the first word and of the last word,
 * upper-cased, so "Akash Gupta" gives "AG", "Akash Kumar Gupta" gives "AG" and a one-word name gives one
 * letter. Words are split on whitespace. Clusters come from `BreakIterator`, never `first()` or a
 * surrogate index, so a Devanagari letter with its vowel sign or an emoji with a variation selector
 * stays whole instead of being cut in half. A blank name gives "" (the avatar then draws nothing).
 *
 * Pure; the result is for the screen only and is never logged.
 */
fun initialsOf(name: String): String {
    val words = name.trim().split(WHITESPACE).filter { it.isNotEmpty() }
    val first = words.firstOrNull() ?: return ""
    val last = words.last()
    return if (words.size == 1) firstCluster(first) else firstCluster(first) + firstCluster(last)
}

private val WHITESPACE = Regex("\\s+")

private fun firstCluster(word: String): String {
    val breaks = BreakIterator.getCharacterInstance(Locale.ROOT)
    breaks.setText(word)
    return word.substring(0, breaks.next()).uppercase(Locale.ROOT)
}
