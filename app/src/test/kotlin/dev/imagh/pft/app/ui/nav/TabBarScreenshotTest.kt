package dev.imagh.pft.app.ui.nav

import androidx.compose.material3.Surface
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
 * Golden screenshots of K7's floating tab bar in light and dark, on a 393x851dp xxhdpi screen with the
 * committed default scheme (dynamic colour off, so the goldens are deterministic): the bar four times,
 * with Home, Transactions, Accounts and More selected in turn ([TabBarSamples]), on the canvas. Fake data only.
 *
 * Record with `./gradlew :app:recordRoborazziDebug --tests '*TabBarScreenshotTest'`; verify runs as part
 * of `check`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TabBarScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(name: String) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.testTag("sample"), color = PftTheme.surfaces.canvas) { TabBarSamples() }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("sample").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun tabBarLight() = capture("tab-bar-light")

    @Test
    @Config(qualifiers = "+night")
    fun tabBarDark() = capture("tab-bar-dark")
}
