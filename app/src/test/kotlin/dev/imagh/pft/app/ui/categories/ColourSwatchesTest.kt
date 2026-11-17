package dev.imagh.pft.app.ui.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.CategoryColour
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [ColourSwatches]: the 12 palette colours in the palette's order, each announced by its name (a colour
 * has no other meaning to a screen reader), one selected, and what a tap hands to `onSelect`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class ColourSwatchesTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var selected by mutableStateOf(CategoryColour.AMBER)
    private val chosen = mutableListOf<CategoryColour>()

    private fun show() =
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                ColourSwatches(
                    selected = selected,
                    onSelect = {
                        chosen += it
                        selected = it
                    },
                )
            }
        }

    private fun names(): List<String> =
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config[SemanticsProperties.ContentDescription].single() }

    @Test
    fun `all twelve colours are offered in the palette's order, each announced by its name`() {
        show()

        names() shouldBe
            listOf(
                "Rose",
                "Coral",
                "Amber",
                "Olive",
                "Emerald",
                "Teal",
                "Sky",
                "Blue",
                "Indigo",
                "Violet",
                "Magenta",
                "Slate",
            )
    }

    @Test
    fun `the swatches are one radio group`() {
        show()

        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)).assertExists()
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config.getOrNull(SemanticsProperties.Role) }
            .toSet() shouldBe setOf(Role.RadioButton)
    }

    @Test
    fun `the chosen colour is announced as selected and the others are not`() {
        show()

        compose.onNodeWithContentDescription("Amber").assertIsSelected()
        compose.onNodeWithContentDescription("Rose").assertIsNotSelected()
        compose.onNodeWithContentDescription("Slate").assertIsNotSelected()
    }

    @Test
    fun `tapping a colour hands it to onSelect and moves the selection`() {
        show()

        compose.onNodeWithContentDescription("Teal").performClick()
        compose.waitForIdle()

        chosen shouldBe listOf(CategoryColour.TEAL)
        compose.onNodeWithContentDescription("Teal").assertIsSelected()
        compose.onNodeWithContentDescription("Amber").assertIsNotSelected()
    }

    @Test
    fun `every swatch is a touch target of at least 48dp`() {
        show()

        val minimum =
            48f *
                RuntimeEnvironment
                    .getApplication()
                    .resources.displayMetrics.density
        compose.onAllNodes(isSelectable()).fetchSemanticsNodes().forEach {
            (it.boundsInRoot.width >= minimum) shouldBe true
            (it.boundsInRoot.height >= minimum) shouldBe true
        }
    }
}
