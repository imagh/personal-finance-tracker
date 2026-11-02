package dev.imagh.pft.app.ui.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The palette swatch page: every palette is shown, whatever theme surrounds it. */
@RunWith(RobolectricTestRunner::class)
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
        listOf("Graphite", "Midnight", "Ivory", "Aubergine").forEach {
            compose.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun `every palette is named and shown in light`() = assertAllShown(dark = false)

    @Test
    fun `every palette is named and shown in dark`() = assertAllShown(dark = true)
}
