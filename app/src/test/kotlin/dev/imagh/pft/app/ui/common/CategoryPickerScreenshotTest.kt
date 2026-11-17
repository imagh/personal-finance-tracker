package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
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
 * Golden screenshots of `CategoryPicker` (fake data only): the full picker (families of a category and
 * its sub-categories, a repeated "Misc" told apart by "Under ...", an emoji, a letter fallback, a long
 * wrapping name, one cell selected) in light and dark, and the parent-field variant (top-level only) in
 * light and dark, on a 393x851dp xxhdpi screen with the committed Graphite colours (dynamic colour off).
 * They show `CategoryPickerSample` and `CategoryParentPickerSample`, the composables behind the previews.
 *
 * The sheet lives in its own dialog window, which `captureToImage` cannot read under Robolectric, so
 * the window is drawn by `drawSheetWindow` over the screen's canvas colour. `./gradlew check` verifies
 * against `src/test/screenshots`; after a deliberate visual change run `./gradlew :app:recordRoborazziDebug`
 * and review the PNG diff. Pixels catch visual regressions; `CategoryPickerTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class CategoryPickerScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        title: String,
        sample: @Composable () -> Unit,
    ) {
        var canvas = 0
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                canvas = PftTheme.surfaces.canvas.toArgb()
                sample()
            }
        }
        compose.waitForIdle()
        compose
            .drawSheetWindow(containing = title, background = canvas)
            .captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun pickerLight() = capture("category-picker-light", "Choose category") { CategoryPickerSample() }

    @Test
    @Config(qualifiers = "+night")
    fun pickerDark() = capture("category-picker-dark", "Choose category") { CategoryPickerSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun parentLight() =
        capture("category-picker-parent-light", "Choose parent category") { CategoryParentPickerSample() }

    @Test
    @Config(qualifiers = "+night")
    fun parentDark() = capture("category-picker-parent-dark", "Choose parent category") { CategoryParentPickerSample() }
}
