package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden screenshots of the four palettes' role swatches side by side, light and dark, on a
 * 393x851dp xxhdpi screen. The swatches draw each palette's own colours, so the surrounding theme
 * (Graphite, dynamic colour off) does not change the picture. Fake data only.
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. `PaletteSwatchesTest`,
 * `ColorContrastTest` and `AccentHueTest` carry correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PaletteScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        dark: Boolean,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.testTag("sample")) { PaletteSwatches(dark) }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("sample").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun palettesLight() = capture("palettes-light", dark = false)

    @Test
    @Config(qualifiers = "+night")
    fun palettesDark() = capture("palettes-dark", dark = true)
}
