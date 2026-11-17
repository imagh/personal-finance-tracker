package dev.imagh.pft.app.ui.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden screenshots of the category form and its two choice grids (plan B4b), on a 393dp-wide xxhdpi
 * screen with the committed Graphite colours (dynamic colour off, so the goldens are deterministic), in
 * light and dark. Fake data only ("Pets", "Vet", "Food & dining" are made up). The form screens are
 * 1500dp tall so the symbol grid is in the picture: a new category with an error under the name, and a
 * sub-category being edited with its fixed type and parent. The symbol grid and the colour swatches are
 * also drawn alone, so a change to either shows up in a small diff.
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `CategoryEditScreenTest`, `SymbolGridTest` and `ColourSwatchesTest` carry correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1500dp-xxhdpi")
class CategoryEditScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val noActions = CategoryEditActions({}, {}, {}, {}, save = {})

    private val newWithError =
        CategoryEditUiState(
            loaded = true,
            name = FieldState("Pets", FieldError.Duplicate),
            colour = CategoryColour.AMBER,
            icon = CategoryIcon.Symbol("pets"),
            parent = sampleCategory("food", "Food & dining", CategoryColour.CORAL, null),
        )

    private val editing =
        CategoryEditUiState(
            editing = true,
            loaded = true,
            name = FieldState("Vet"),
            colour = CategoryColour.TEAL,
            icon = CategoryIcon.Symbol("medication"),
            parent = sampleCategory("pets", "Pets", CategoryColour.AMBER, CategoryIcon.Symbol("pets")),
        )

    private fun capture(
        name: String,
        content: @Composable () -> Unit,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { content() } }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    private fun form(state: CategoryEditUiState): @Composable () -> Unit =
        { CategoryEditScreen(state, noActions, onBack = {}) }

    private val grid: @Composable () -> Unit = {
        Column(Modifier.padding(top = PftTheme.spacing.lg)) {
            SymbolGrid(CategoryIcon.Symbol("pets"), CategoryColour.AMBER, "Pets", onSelect = {})
        }
    }

    private val swatches: @Composable () -> Unit = {
        Column(Modifier.padding(top = PftTheme.spacing.lg)) {
            ColourSwatches(CategoryColour.TEAL, onSelect = {})
        }
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun newWithErrorLight() = capture("category-edit-new-light", form(newWithError))

    @Test
    @Config(qualifiers = "+night")
    fun newWithErrorDark() = capture("category-edit-new-dark", form(newWithError))

    @Test
    @Config(qualifiers = "+notnight")
    fun editingLight() = capture("category-edit-editing-light", form(editing))

    @Test
    @Config(qualifiers = "w393dp-h900dp-notnight-xxhdpi")
    fun symbolGridLight() = capture("category-symbol-grid-light", grid)

    @Test
    @Config(qualifiers = "w393dp-h900dp-night-xxhdpi")
    fun symbolGridDark() = capture("category-symbol-grid-dark", grid)

    @Test
    @Config(qualifiers = "w393dp-h300dp-notnight-xxhdpi")
    fun swatchesLight() = capture("category-swatches-light", swatches)

    @Test
    @Config(qualifiers = "w393dp-h300dp-night-xxhdpi")
    fun swatchesDark() = capture("category-swatches-dark", swatches)
}
