package dev.imagh.pft.app.ui.categories

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
 * Golden screenshots of the categories list (fake data only, `CategoriesSample`): the Expense list in
 * light and dark, with a parent and its indented sub-categories, an emoji icon, a category with no icon,
 * the Loan EMI row and a collapsed Archived section; and the same list with Archived expanded. The
 * screen is 393dp wide at xxhdpi and tall enough (1100dp, 1500dp with Archived expanded) for the
 * Archived section to be in the picture, with the committed Graphite colours (dynamic colour off, so
 * the goldens are deterministic).
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `CategoriesScreenTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1100dp-xxhdpi")
class CategoriesScreenshotTest {
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
    fun listLight() = capture("categories-list-light") { CategoriesSample() }

    @Test
    @Config(qualifiers = "+night")
    fun listDark() = capture("categories-list-dark") { CategoriesSample() }

    @Test
    @Config(qualifiers = "+h1500dp")
    fun listWithArchivedExpandedLight() =
        capture(
            "categories-list-archived-light",
            beforeCapture = {
                compose.onNodeWithText("Archived (2)").performClick()
                compose.waitForIdle()
            },
        ) { CategoriesSample() }
}
