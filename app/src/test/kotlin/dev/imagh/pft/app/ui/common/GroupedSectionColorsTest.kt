package dev.imagh.pft.app.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import dev.imagh.pft.app.ui.theme.BackgroundStyle
import dev.imagh.pft.app.ui.theme.PftSurfaces
import dev.imagh.pft.app.ui.theme.ThemePalette
import dev.imagh.pft.app.ui.theme.colors
import dev.imagh.pft.app.ui.theme.pftSurfaces
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/** [groupedSectionColors]: the island's fill and edge as pure data, for every palette, mode and style. */
class GroupedSectionColorsTest {
    /** One palette, mode and style, with the surfaces and the island colours they give. */
    private class Case(
        val palette: ThemePalette,
        val dark: Boolean,
        val style: BackgroundStyle,
    ) {
        val surfaces: PftSurfaces = pftSurfaces(palette.colors(dark), style, dark)
        val colors: GroupedSectionColors = groupedSectionColors(surfaces)

        override fun toString() = "$palette dark=$dark $style"
    }

    private fun forEvery(check: (Case) -> Unit) {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                for (style in BackgroundStyle.entries) {
                    check(Case(palette, dark, style))
                }
            }
        }
    }

    @Test
    fun `the fill is the island in every palette, mode and style`() {
        forEvery { assertEquals(it.surfaces.island, it.colors.fill, "$it") }
    }

    @Test
    fun `an edge is drawn only for TONAL`() {
        forEvery {
            if (it.style == BackgroundStyle.FLAT) {
                assertNull(it.colors.borderTop, "$it top")
                assertNull(it.colors.borderBottom, "$it bottom")
            } else {
                assertNotEquals(null, it.colors.borderTop, "$it top")
                assertNotEquals(null, it.colors.borderBottom, "$it bottom")
            }
        }
    }

    @Test
    fun `light TONAL has a plain hairline edge, top and bottom the same`() {
        forEvery {
            if (!it.dark && it.style == BackgroundStyle.TONAL) {
                assertEquals(it.surfaces.islandEdge, it.colors.borderTop, "$it top")
                assertEquals(it.surfaces.islandEdge, it.colors.borderBottom, "$it bottom")
            }
        }
    }

    @Test
    fun `dark TONAL lights the top edge with the highlight and leaves the bottom as the hairline`() {
        forEvery {
            if (it.dark && it.style == BackgroundStyle.TONAL) {
                val lit = it.surfaces.islandHighlight.compositeOver(it.surfaces.islandEdge)
                assertEquals(lit, it.colors.borderTop, "$it top")
                assertEquals(it.surfaces.islandEdge, it.colors.borderBottom, "$it bottom")
                assertNotEquals(it.colors.borderTop, it.colors.borderBottom, "$it highlight is invisible")
            }
        }
    }

    @Test
    fun `the fill is never white`() {
        forEvery { assertNotEquals(Color.White, it.colors.fill, "$it") }
    }
}
