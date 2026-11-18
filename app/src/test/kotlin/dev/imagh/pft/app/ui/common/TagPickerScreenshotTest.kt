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
 * Golden screenshots of `TagPicker` (fake data only): the picker with one active tag selected and one
 * archived-but-selected tag (light and dark), and the empty sheet (light), on a 393x851dp xxhdpi screen with
 * the committed Graphite colours (dynamic colour off). They show `TagPickerSample` and `TagPickerEmptySample`.
 *
 * The sheet lives in its own dialog window, which `captureToImage` cannot read under Robolectric, so the
 * window is drawn by `drawSheetWindow` over the screen's canvas colour. `./gradlew check` verifies against
 * `src/test/screenshots`; after a deliberate visual change run `./gradlew :app:recordRoborazziDebug` and
 * review the PNG diff. Pixels catch visual regressions; `TagPickerTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TagPickerScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
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
            .drawSheetWindow(containing = "Choose tags", background = canvas)
            .captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun pickerLight() = capture("tag-picker-light") { TagPickerSample() }

    @Test
    @Config(qualifiers = "+night")
    fun pickerDark() = capture("tag-picker-dark") { TagPickerSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun emptyLight() = capture("tag-picker-empty-light") { TagPickerEmptySample() }
}
