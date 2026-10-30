package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden screenshots of `CategoryGlyph` (DS3), light and dark, on a 393x851dp xxhdpi screen with
 * the committed Graphite scheme (dynamic colour off, so they are deterministic): the whole symbol
 * registry on three palette colours (teal, amber, violet), and the emoji, letter and fallback
 * sources with all 12 hues and three sizes. Fake data only. Emoji use the Robolectric
 * environment's emoji font, so they are a review aid for layout, not for emoji artwork.
 *
 * `./gradlew check` verifies against `src/test/screenshots` on both unit-test variants; after a
 * deliberate visual change run `./gradlew :app:recordRoborazziDebug` and review the PNG diff.
 * Correctness is carried by `CategoryGlyphTest`, `GlyphSourceTest` and `PftSymbolTest`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class CategoryGlyphScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        sample: @Composable () -> Unit,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.testTag("sample")) { Box(Modifier.padding(PftTheme.spacing.gutter)) { sample() } }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("sample").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun symbolsTealLight() = capture("category-glyph-symbols-teal-light") { CategoryGlyphSymbolSamples("teal") }

    @Test
    @Config(qualifiers = "+night")
    fun symbolsTealDark() = capture("category-glyph-symbols-teal-dark") { CategoryGlyphSymbolSamples("teal") }

    @Test
    @Config(qualifiers = "+notnight")
    fun symbolsAmberLight() = capture("category-glyph-symbols-amber-light") { CategoryGlyphSymbolSamples("amber") }

    @Test
    @Config(qualifiers = "+night")
    fun symbolsAmberDark() = capture("category-glyph-symbols-amber-dark") { CategoryGlyphSymbolSamples("amber") }

    @Test
    @Config(qualifiers = "+notnight")
    fun symbolsVioletLight() = capture("category-glyph-symbols-violet-light") { CategoryGlyphSymbolSamples("violet") }

    @Test
    @Config(qualifiers = "+night")
    fun symbolsVioletDark() = capture("category-glyph-symbols-violet-dark") { CategoryGlyphSymbolSamples("violet") }

    @Test
    @Config(qualifiers = "+notnight")
    fun sourcesLight() = capture("category-glyph-sources-light") { CategoryGlyphSamples() }

    @Test
    @Config(qualifiers = "+night")
    fun sourcesDark() = capture("category-glyph-sources-dark") { CategoryGlyphSamples() }
}
