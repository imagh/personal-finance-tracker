package dev.imagh.pft.app.ui.theme

import dev.imagh.pft.ledger.category.CategoryColour
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * Core's `CategoryColour` (what a category stores) and the app's `CategoryPalette` (what draws it)
 * are two lists of the same 12 permanent names. A name in one and not the other would draw as the
 * slate fallback, so this pins them together, in order (plan D5).
 */
class CategoryColourPaletteTest {
    @Test
    fun `core's colours and the palette's keys are the same names in the same order`() {
        CategoryColour.entries.map { it.key } shouldBe CategoryPalette.keys
    }
}
