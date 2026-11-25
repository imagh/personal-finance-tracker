package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.assertShownInFull
import dev.imagh.pft.app.ui.common.layout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The palette swatch page: every palette is shown, whatever theme surrounds it. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PaletteSwatchesTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun assertAllShown(dark: Boolean) {
        // The surrounding theme is deliberately the opposite mode: swatches use each palette's own colours.
        compose.setContent {
            PftTheme(ThemePrefs(mode = if (dark) ThemeMode.LIGHT else ThemeMode.DARK)) { PaletteSwatches(dark) }
        }
        ThemePalette.entries.forEach { compose.onNodeWithTag("swatches-${it.name}").assertIsDisplayed() }
        listOf("Graphite", "Midnight", "Ivory", "Aubergine", "Cobalt").forEach {
            compose.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun `five columns still fit every palette name and swatch label on one line`() {
        // The golden of this page is the only other check of the narrow columns; wrapped names read as bugs.
        compose.setContent { PftTheme(ThemePrefs.Default) { PaletteSwatches(dark = false) } }
        ThemePalette.entries.forEach {
            compose.onNodeWithText(it.displayName).layout().assertShownInFull("(${it.displayName} name)")
        }
        val labels = listOf("Text", "Variant", "Island", "Primary", "Tinted", "Bright", "Dim")
        for (label in labels) {
            val nodes = compose.onAllNodesWithText(label)
            for (index in 0 until ThemePalette.entries.size) {
                nodes[index].layout().assertShownInFull("(label $label, column $index)")
            }
        }
    }

    @Test
    fun `every palette is named and shown in light`() = assertAllShown(dark = false)

    @Test
    fun `every palette is named and shown in dark`() = assertAllShown(dark = true)
}
