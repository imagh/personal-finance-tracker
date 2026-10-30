package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
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
 * Golden screenshots of the scaffold's two header styles, expanded and collapsed, and of the
 * bottom action bar, in light and dark, on a 393x851dp xxhdpi screen with the committed Graphite
 * colours (dynamic colour off, so the goldens are deterministic). The samples are the same
 * composables the previews and the debug catalogue show; fake data only.
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `CollapsingHeaderScaffoldTest` and `BottomActionBarTest` carry correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HeaderScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        collapse: Boolean = false,
        sample: @Composable () -> Unit,
    ) {
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { sample() } }
        compose.waitForIdle()
        if (collapse) {
            compose.onNodeWithTag(HeaderTags.LIST).performTouchInput { swipeUp() }
            compose.waitForIdle()
        }
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    private fun captureBar(name: String) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { Surface(Modifier.testTag("bar")) { BottomActionBarSamples() } }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("bar").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun standardExpandedLight() = capture("header-standard-expanded-light") { StandardHeaderSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun standardCollapsedLight() =
        capture("header-standard-collapsed-light", collapse = true) { StandardHeaderSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun dashboardExpandedLight() = capture("header-dashboard-expanded-light") { DashboardHeaderSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun dashboardCollapsedLight() =
        capture("header-dashboard-collapsed-light", collapse = true) { DashboardHeaderSample() }

    @Test
    @Config(qualifiers = "+night")
    fun standardExpandedDark() = capture("header-standard-expanded-dark") { StandardHeaderSample() }

    @Test
    @Config(qualifiers = "+night")
    fun standardCollapsedDark() = capture("header-standard-collapsed-dark", collapse = true) { StandardHeaderSample() }

    @Test
    @Config(qualifiers = "+night")
    fun dashboardExpandedDark() = capture("header-dashboard-expanded-dark") { DashboardHeaderSample() }

    @Test
    @Config(qualifiers = "+night")
    fun dashboardCollapsedDark() =
        capture("header-dashboard-collapsed-dark", collapse = true) { DashboardHeaderSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun bottomActionBarLight() = captureBar("bottom-action-bar-light")

    @Test
    @Config(qualifiers = "+night")
    fun bottomActionBarDark() = captureBar("bottom-action-bar-dark")
}
