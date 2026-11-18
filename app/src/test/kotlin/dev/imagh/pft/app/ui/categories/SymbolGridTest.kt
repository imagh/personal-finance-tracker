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
import dev.imagh.pft.app.ui.theme.CATEGORY_SYMBOLS
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [SymbolGrid]: which symbols are offered, how each is announced, what the selected cell says, and what a
 * tap hands to `onSelect`. The cells carry no visible text, so TalkBack hears a spoken name from each
 * cell's description (the badge inside is decorative), which is asserted from the semantics.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h2000dp-xxhdpi")
class SymbolGridTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var selected by mutableStateOf<CategoryIcon?>(null)
    private val chosen = mutableListOf<CategoryIcon?>()

    private fun show(initial: CategoryIcon? = null) {
        selected = initial
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                SymbolGrid(
                    selected = selected,
                    colour = CategoryColour.AMBER,
                    name = "Pets",
                    onSelect = {
                        chosen += it
                        selected = it
                    },
                )
            }
        }
        compose.waitForIdle()
    }

    private fun descriptions(): List<String> =
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config[SemanticsProperties.ContentDescription].single() }

    @Test
    fun `the grid offers No symbol and then exactly the category symbols, in the list's order`() {
        show()

        descriptions() shouldBe listOf("No symbol") + CATEGORY_SYMBOLS.map { it.spokenName() }
    }

    @Test
    fun `the cells are one radio group, each announced by a spoken name`() {
        show()

        compose
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .assertExists()
        val cells = compose.onAllNodes(isSelectable()).fetchSemanticsNodes()
        cells.map { it.config.getOrNull(SemanticsProperties.Role) }.toSet() shouldBe setOf(Role.RadioButton)
        val names = descriptions()
        names.all { it.isNotBlank() } shouldBe true
        // Every spoken name is different, so no two cells sound alike.
        names.toSet().size shouldBe names.size
        names.first() shouldBe "No symbol"
        names[1] shouldBe PftSymbol.RESTAURANT.spokenName()
        // The badge inside a cell is decorative: the only description in the cell is the cell's own.
        val badgeDescribed =
            SemanticsMatcher("a badge that speaks") { it.config.getOrNull(SemanticsProperties.Role) == Role.Image }
        compose.onAllNodes(badgeDescribed, useUnmergedTree = true).fetchSemanticsNodes() shouldBe emptyList()
    }

    @Test
    fun `a spoken name is the key as words`() {
        PftSymbol.LOCAL_GAS_STATION.spokenName() shouldBe "Local gas station"
        PftSymbol.PETS.spokenName() shouldBe "Pets"
        CategoryColour.AMBER.spokenName() shouldBe "Amber"
    }

    @Test
    fun `the chosen symbol is announced as selected and the others are not`() {
        show(CategoryIcon.Symbol("pets"))

        compose.onNodeWithContentDescription("Pets").assertIsSelected()
        compose.onNodeWithContentDescription("Restaurant").assertIsNotSelected()
        compose.onNodeWithContentDescription("No symbol").assertIsNotSelected()
    }

    @Test
    fun `with no icon the No symbol cell is the selected one`() {
        show(null)

        compose.onNodeWithContentDescription("No symbol").assertIsSelected()
        compose.onNodeWithContentDescription("Pets").assertIsNotSelected()
    }

    @Test
    fun `an emoji icon, which this grid does not offer, selects nothing`() {
        show(CategoryIcon.Emoji("🐶"))

        compose
            .onAllNodes(isSelectable() and SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .fetchSemanticsNodes() shouldBe emptyList()
    }

    @Test
    fun `tapping a symbol hands its symbol to onSelect and tapping No symbol hands null`() {
        show(CategoryIcon.Symbol("pets"))

        compose.onNodeWithContentDescription("Restaurant").performClick()
        compose.onNodeWithContentDescription("No symbol").performClick()

        chosen shouldBe listOf(CategoryIcon.Symbol("restaurant"), null)
        compose.onNodeWithContentDescription("No symbol").assertIsSelected()
    }

    @Test
    fun `every cell is a touch target of at least 48dp`() {
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
