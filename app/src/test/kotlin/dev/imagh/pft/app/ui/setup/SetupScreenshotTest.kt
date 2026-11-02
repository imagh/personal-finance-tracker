package dev.imagh.pft.app.ui.setup

import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden screenshots of the first-run setup screen with the header expanded (its state on first
 * launch; every file name says `expanded`): light, dark, light at font scale 2.0, light with the
 * name in error, and light while saving (Continue disabled), on a 393x851dp xxhdpi screen
 * with the committed Graphite colours (dynamic colour off, so the goldens are deterministic). Fake
 * data only: the sample phone name is "Pixel 8" and the name is empty (or "Akash" in the saving shot).
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `SetupScreenTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class SetupScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val fresh = SetupUiState(deviceLabel = FieldState("Pixel 8"))

    private fun capture(
        name: String,
        state: SetupUiState = fresh,
        fontScale: Float = 1f,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    Surface(color = PftTheme.surfaces.canvas) {
                        SetupScreen(state, onNameChange = {}, onLabelChange = {}, onContinue = {})
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun setupLight() = capture("setup-expanded-light")

    @Test
    @Config(qualifiers = "+night")
    fun setupDark() = capture("setup-expanded-dark")

    @Test
    @Config(qualifiers = "+notnight")
    fun setupLightFont200() = capture("setup-expanded-light-2x", fontScale = 2f)

    @Test
    @Config(qualifiers = "+notnight")
    fun setupLightNameRequired() =
        capture("setup-expanded-error-light", fresh.copy(name = FieldState("", FieldError.Required)))

    @Test
    @Config(qualifiers = "+notnight")
    fun setupLightSaving() =
        capture("setup-expanded-saving-light", fresh.copy(name = FieldState("Akash"), saving = true))
}
