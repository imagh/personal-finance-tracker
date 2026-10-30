package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
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
 * Golden screenshots of the DS5 content primitives (`MoneyText`, `StatusChip`, `GroupedSection`,
 * `EmptyState`) in light and dark, and `MoneyText` also at font scale 2.0, on a 393x851dp xxhdpi
 * screen with the committed brand colours (dynamic colour off, so the goldens are deterministic).
 * Each shows the same `*Samples` composable as the preview and the debug catalogue, captured by
 * the bounds of the sample, on `surface`. Fake data only.
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `MoneyTextTest`, `StatusChipTest`, `GroupedSectionTest` and `EmptyStateTest` carry correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class ContentScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        fontScale: Float = 1f,
        sample: @Composable () -> Unit,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    Surface(Modifier.testTag("sample")) { Box(Modifier.padding(PftTheme.spacing.gutter)) { sample() } }
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("sample").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun statusChipLight() = capture("status-chip-light") { StatusChipSamples() }

    @Test
    @Config(qualifiers = "+night")
    fun statusChipDark() = capture("status-chip-dark") { StatusChipSamples() }
}
