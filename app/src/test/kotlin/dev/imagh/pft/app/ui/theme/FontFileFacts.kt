package dev.imagh.pft.app.ui.theme

import java.nio.ByteBuffer

/**
 * What a test needs to know about an OpenType file, read from its bytes alone: which code points
 * its `cmap` maps to a real glyph and which feature tags its `GSUB` table offers. It exists
 * because `Paint.hasGlyph` cannot prove a bundled font's own coverage: under Robolectric native
 * graphics it also consults the system fallback fonts, so it answers true for a CJK ideograph that
 * neither Latin font has (the negative control in `TabularFiguresTest` showed it).
 *
 * Reads only the BMP `cmap` subtable (format 4), which is all both Cobalt fonts have; a font
 * without one makes [hasGlyph] throw rather than answer a wrong "no".
 */
internal class FontFileFacts(
    bytes: ByteArray,
) {
    private val buf: ByteBuffer = ByteBuffer.wrap(bytes)
    private val tables: Map<String, Int> = readTables()

    private fun u16(offset: Int): Int = buf.getShort(offset).toInt() and MASK16

    private fun u32(offset: Int): Int = buf.getInt(offset)

    private fun tag(offset: Int): String = String(buf.array(), offset, TAG_LENGTH, Charsets.US_ASCII)

    private fun readTables(): Map<String, Int> =
        (0 until u16(NUM_TABLES_AT)).associate { i ->
            val entry = DIRECTORY_AT + DIRECTORY_ENTRY_SIZE * i
            tag(entry) to u32(entry + TABLE_OFFSET_IN_ENTRY)
        }

    /** Every feature tag in the `GSUB` FeatureList (for example `tnum`, `lnum`). */
    fun gsubFeatures(): Set<String> {
        val gsub = tables.getValue("GSUB")
        val featureList = gsub + u16(gsub + FEATURE_LIST_OFFSET_IN_GSUB)
        return (0 until u16(featureList)).map { tag(featureList + 2 + FEATURE_RECORD_SIZE * it) }.toSet()
    }

    /** Whether the font's own `cmap` maps [codePoint] (a BMP code point) to a glyph other than `.notdef`. */
    fun hasGlyph(codePoint: Int): Boolean {
        if (codePoint > MASK16) return false
        val format4 = format4Subtable()
        val segCount = u16(format4 + 6) / 2
        val ends = format4 + 14
        val starts = ends + 2 * segCount + 2
        val deltas = starts + 2 * segCount
        val rangeOffsets = deltas + 2 * segCount
        val segment = (0 until segCount).firstOrNull { codePoint <= u16(ends + 2 * it) }
        val start = segment?.let { u16(starts + 2 * it) }
        return when {
            segment == null || start == null || codePoint < start -> false
            u16(rangeOffsets + 2 * segment) == 0 -> ((codePoint + u16(deltas + 2 * segment)) and MASK16) != 0
            else ->
                u16(rangeOffsets + 2 * segment + u16(rangeOffsets + 2 * segment) + 2 * (codePoint - start)) != 0
        }
    }

    private fun format4Subtable(): Int {
        val cmap = tables.getValue("cmap")
        val subtables = (0 until u16(cmap + 2)).map { cmap + u32(cmap + 8 + 8 * it) }
        return checkNotNull(subtables.firstOrNull { u16(it) == FORMAT_4 }) { "no cmap format 4 subtable" }
    }

    private companion object {
        const val MASK16 = 0xFFFF
        const val TAG_LENGTH = 4
        const val NUM_TABLES_AT = 4
        const val DIRECTORY_AT = 12
        const val DIRECTORY_ENTRY_SIZE = 16
        const val TABLE_OFFSET_IN_ENTRY = 8
        const val FEATURE_LIST_OFFSET_IN_GSUB = 6
        const val FEATURE_RECORD_SIZE = 6
        const val FORMAT_4 = 4
    }
}
