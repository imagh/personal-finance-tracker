package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
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
 * Golden screenshots of `EntityListScreen` (fake data only): a list with an indented row and a
 * collapsed Archived section in light and dark, the same list with the Archived section expanded,
 * and the empty state in light and dark, on a 393x851dp xxhdpi screen with the committed Graphite
 * colours (dynamic colour off, so the goldens are deterministic). They show `EntityListSample` and
 * `EntityListEmptySample`, the composables behind the previews.
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `EntityListScreenTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class EntityListScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        beforeCapture: () -> Unit = {},
        sample: @Composable () -> Unit,
    ) {
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { sample() } }
        compose.waitForIdle()
        beforeCapture()
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun listLight() = capture("entity-list-light") { EntityListSample() }

    @Test
    @Config(qualifiers = "+night")
    fun listDark() = capture("entity-list-dark") { EntityListSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun listWithArchivedExpandedLight() =
        capture(
            "entity-list-archived-light",
            beforeCapture = {
                compose.onNodeWithText("Archived (2)").performClick()
                compose.waitForIdle()
            },
        ) { EntityListSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun emptyLight() = capture("entity-list-empty-light") { EntityListEmptySample() }

    @Test
    @Config(qualifiers = "+night")
    fun emptyDark() = capture("entity-list-empty-dark") { EntityListEmptySample() }
}
