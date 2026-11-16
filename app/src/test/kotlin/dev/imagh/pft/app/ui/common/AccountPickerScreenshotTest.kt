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
 * Golden screenshots of `AccountPicker` (fake data only): the picker with a selected account and the
 * empty picker, in light and dark, on a 393x851dp xxhdpi screen with the committed Graphite colours
 * (dynamic colour off, so the goldens are deterministic). They show `AccountPickerSample` and
 * `AccountPickerEmptySample`, the composables behind the previews.
 *
 * The sheet lives in its own dialog window, which `captureToImage` cannot read under Robolectric, so
 * the window is drawn by `drawSheetWindow` over the screen's canvas colour. `./gradlew check`
 * verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `AccountPickerTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountPickerScreenshotTest {
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
            .drawSheetWindow(containing = "Choose account", background = canvas)
            .captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun pickerLight() = capture("account-picker-light") { AccountPickerSample() }

    @Test
    @Config(qualifiers = "+night")
    fun pickerDark() = capture("account-picker-dark") { AccountPickerSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun emptyLight() = capture("account-picker-empty-light") { AccountPickerEmptySample() }

    @Test
    @Config(qualifiers = "+night")
    fun emptyDark() = capture("account-picker-empty-dark") { AccountPickerEmptySample() }
}
