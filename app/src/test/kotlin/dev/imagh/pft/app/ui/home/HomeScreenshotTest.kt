package dev.imagh.pft.app.ui.home

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
 * Golden screenshots of K6's Home components in light and dark, on a 393x851dp xxhdpi screen with
 * the committed default scheme (dynamic colour off, so the goldens are deterministic): `BalanceCard`
 * (without and with a budget meter), `QuickActionRow` (three actions, and four) and K8's
 * `GreetingHeader` (a short and a long name). Each shows the
 * same `*Samples` composable as the preview and the debug catalogue, captured by the bounds of the
 * sample, on the canvas. The components bring their own gutter. Fake data only.
 *
 * Record with `./gradlew :app:recordRoborazziDebug --tests '*HomeScreenshotTest'`; verify runs as
 * part of `check`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HomeScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        sample: @Composable () -> Unit,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.testTag("sample"), color = PftTheme.surfaces.canvas) {
                    Box(Modifier.padding(vertical = PftTheme.spacing.xl)) { sample() }
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("sample").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun balanceCardLight() = capture("balance-card-light") { BalanceCardSamples() }

    @Test
    @Config(qualifiers = "+night")
    fun balanceCardDark() = capture("balance-card-dark") { BalanceCardSamples() }

    @Test
    @Config(qualifiers = "+notnight")
    fun quickActionsLight() = capture("quick-actions-light") { QuickActionRowSamples() }

    @Test
    @Config(qualifiers = "+night")
    fun quickActionsDark() = capture("quick-actions-dark") { QuickActionRowSamples() }

    @Test
    @Config(qualifiers = "+notnight")
    fun homeHeaderLight() = capture("home-header-light") { GreetingHeaderSamples() }

    @Test
    @Config(qualifiers = "+night")
    fun homeHeaderDark() = capture("home-header-dark") { GreetingHeaderSamples() }
}
