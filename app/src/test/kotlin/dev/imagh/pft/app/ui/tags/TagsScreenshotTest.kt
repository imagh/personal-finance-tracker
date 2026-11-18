package dev.imagh.pft.app.ui.tags

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
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
 * Golden screenshots of the tags screens (fake data only): the list (`TagsSample`) in light and dark and with
 * Archived expanded, and the form new, with a duplicate-name error and renaming, on a 393dp-wide xxhdpi screen
 * with the committed Graphite colours (dynamic colour off). `./gradlew check` verifies against
 * `src/test/screenshots`; after a deliberate visual change run `./gradlew :app:recordRoborazziDebug` and review
 * the PNG diff. Pixels catch visual regressions; `TagsFlowTest`, `TagsScreenTest` carry correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TagsScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun capture(
        name: String,
        beforeCapture: () -> Unit = {},
        sample: @Composable () -> Unit,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { sample() } }
        }
        compose.waitForIdle()
        beforeCapture()
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    private fun form(state: TagEditUiState) = @Composable { TagEditScreen(state, TagEditActions({}, {}), onBack = {}) }

    @Test
    @Config(qualifiers = "+notnight")
    fun listLight() = capture("tags-list-light") { TagsSample() }

    @Test
    @Config(qualifiers = "+night")
    fun listDark() = capture("tags-list-dark") { TagsSample() }

    @Test
    @Config(qualifiers = "+h1100dp")
    fun listWithArchivedExpandedLight() =
        capture(
            "tags-list-archived-light",
            beforeCapture = {
                compose.onNodeWithText("Archived (1)").performClick()
                compose.waitForIdle()
            },
        ) { TagsSample() }

    @Test
    @Config(qualifiers = "+notnight")
    fun newFormLight() = capture("tag-edit-new-light", sample = form(TagEditUiState()))

    @Test
    @Config(qualifiers = "+night")
    fun newFormDark() = capture("tag-edit-new-dark", sample = form(TagEditUiState()))

    @Test
    @Config(qualifiers = "+notnight")
    fun duplicateFormLight() =
        capture(
            "tag-edit-duplicate-light",
            sample = form(TagEditUiState(editing = true, name = FieldState("travel", FieldError.Duplicate))),
        )
}
