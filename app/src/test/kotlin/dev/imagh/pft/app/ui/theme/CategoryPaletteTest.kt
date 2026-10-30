package dev.imagh.pft.app.ui.theme

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

/** The 12 named category hues (plan §3.3): their names are persisted in `category.colour`. */
class CategoryPaletteTest {
    private val names =
        listOf(
            "rose",
            "coral",
            "amber",
            "olive",
            "emerald",
            "teal",
            "sky",
            "blue",
            "indigo",
            "violet",
            "magenta",
            "slate",
        )

    @Test
    fun `the palette has exactly the twelve persisted names`() {
        assertEquals(names, CategoryPalette.keys)
    }

    @Test
    fun `every key resolves to its own colours in both themes`() {
        for (dark in listOf(false, true)) {
            val palette = categoryPalette(dark)
            val containers = names.map { palette[it].container }
            assertEquals(names.size, containers.toSet().size, "containers must be distinct (dark=$dark)")
        }
    }

    @Test
    fun `an unknown, empty, null or mis-cased key falls back to slate`() {
        val palette = categoryPalette(dark = false)
        listOf("nope", "", "Teal", " teal", "teal ", "sym:teal", null).forEach {
            assertSame(palette["slate"], palette[it], "'$it'")
        }
        assertEquals("slate", CategoryPalette.FALLBACK_KEY)
    }

    @Test
    fun `light and dark use different colours for the same key`() {
        for (name in names) {
            assertNotEquals(categoryPalette(false)[name].container, categoryPalette(true)[name].container, name)
            assertNotEquals(categoryPalette(false)[name].content, categoryPalette(true)[name].content, name)
        }
    }
}
