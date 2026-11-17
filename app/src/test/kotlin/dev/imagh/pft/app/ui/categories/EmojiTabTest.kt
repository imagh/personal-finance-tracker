package dev.imagh.pft.app.ui.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.imagh.pft.app.ui.HostedComposeRule
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
 * [EmojiTab]: which emoji the quick-pick offers, how each cell is announced (one radio-style selectable
 * whose visible emoji TalkBack reads by its Unicode name, so no second description), what a tap and
 * typed or pasted text hand to the callbacks, and the fixed error. The tab never keeps the text typed
 * into its field: it hands the text on and stays empty. Native graphics, so the quick-pick is checked
 * against the platform's real emoji font.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h2000dp-xxhdpi")
class EmojiTabTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var selected by mutableStateOf<CategoryIcon?>(null)
    private var rejected by mutableStateOf(false)
    private val picked = mutableListOf<CategoryIcon>()
    private val typed = mutableListOf<String>()
    private var shown = false

    /** Shows the tab with [initial] chosen; a second call in one test changes the state of the same tab. */
    private fun show(
        initial: CategoryIcon? = null,
        rejection: Boolean = false,
    ) {
        selected = initial
        rejected = rejection
        if (shown) return compose.waitForIdle()
        shown = true
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EmojiTab(
                    selected = selected,
                    colour = CategoryColour.AMBER,
                    rejected = rejected,
                    onSelect = {
                        picked += it
                        selected = it
                    },
                    onInput = { typed += it },
                )
            }
        }
        compose.waitForIdle()
    }

    private fun cellTexts(): List<String> =
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { node -> node.config[SemanticsProperties.Text].single().text }

    private fun selectedCells(): List<String> =
        compose
            .onAllNodes(isSelectable() and SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .fetchSemanticsNodes()
            .map { node -> node.config[SemanticsProperties.Text].single().text }

    // --- The quick-pick

    @Test
    fun `the quick-pick offers exactly the fixed list, in its order`() {
        show()

        cellTexts() shouldBe QUICK_PICK_EMOJI
    }

    @Test
    fun `the quick-pick is about 24 distinct emoji that core and the validator both accept`() {
        QUICK_PICK_EMOJI.size shouldBe 24
        QUICK_PICK_EMOJI.toSet().size shouldBe QUICK_PICK_EMOJI.size
        QUICK_PICK_EMOJI.forEach {
            CategoryIcon.Emoji(it).isValid shouldBe true
            EmojiInputValidator.validate(it) shouldBe CategoryIcon.Emoji(it)
        }
    }

    @Test
    fun `the cells are one radio group and each is spoken once, from its visible emoji`() {
        show()

        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)).assertExists()
        val cells = compose.onAllNodes(isSelectable()).fetchSemanticsNodes()
        cells.map { it.config.getOrNull(SemanticsProperties.Role) }.toSet() shouldBe setOf(Role.RadioButton)
        // The emoji is the cell's text, which TalkBack reads by its Unicode name: a description as well
        // would make it say everything twice.
        cells.map { it.config.getOrNull(SemanticsProperties.ContentDescription) }.toSet() shouldBe setOf(null)
    }

    @Test
    fun `the chosen emoji is the selected cell and the others are not`() {
        show(CategoryIcon.Emoji(QUICK_PICK_EMOJI[3]))

        selectedCells() shouldBe listOf(QUICK_PICK_EMOJI[3])
    }

    @Test
    fun `a symbol, no icon, or an emoji outside the quick-pick selects no cell`() {
        listOf(CategoryIcon.Symbol("pets"), null, CategoryIcon.Emoji("🦄")).forEach {
            show(it)

            selectedCells() shouldBe emptyList()
        }
    }

    @Test
    fun `tapping a quick-pick emoji hands that emoji to onSelect and selects it`() {
        show(CategoryIcon.Emoji(QUICK_PICK_EMOJI[0]))

        compose.onAllNodes(isSelectable())[5].performClick()

        picked shouldBe listOf(CategoryIcon.Emoji(QUICK_PICK_EMOJI[5]))
        selectedCells() shouldBe listOf(QUICK_PICK_EMOJI[5])
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

    // --- The field

    private fun fieldText(): String =
        compose
            .onNode(hasSetTextAction())
            .fetchSemanticsNode()
            .config[SemanticsProperties.EditableText]
            .text

    /** Whether TalkBack hears the field as in error (M3 sets the semantics from `isError`). */
    private fun fieldInError(): Boolean =
        compose
            .onNode(hasSetTextAction())
            .fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Error) != null

    @Test
    fun `the field has its label and starts empty`() {
        show()

        compose.onNodeWithText("Type or paste an emoji").assertIsDisplayed()
        fieldText() shouldBe ""
    }

    @Test
    fun `typed or pasted text is handed on exactly as it came, and the field keeps none of it`() {
        show()

        compose.onNode(hasSetTextAction()).performTextInput("🛒")
        compose.onNode(hasSetTextAction()).performTextInput("not an emoji")

        typed shouldBe listOf("🛒", "not an emoji")
        fieldText() shouldBe ""
        compose.onAllNodesWithText("not an emoji", substring = true).assertCountEquals(0)
    }

    @Test
    fun `a rejection shows one fixed message that does not repeat what was typed`() {
        show(rejection = true)
        compose.onNode(hasSetTextAction()).performTextInput("zzz secret")

        compose.onNodeWithText("Enter just one emoji that this phone can show").assertIsDisplayed()
        compose.onAllNodesWithText("zzz secret", substring = true).assertCountEquals(0)
        fieldInError() shouldBe true
    }

    @Test
    fun `with no rejection there is no error message`() {
        show()

        compose.onAllNodesWithText("Enter just one emoji that this phone can show").assertCountEquals(0)
        fieldInError() shouldBe false
    }

    // --- The chosen emoji

    @Test
    fun `a chosen emoji is shown once as a badge in the category colour, announced by the emoji itself`() {
        show(CategoryIcon.Emoji("🦄"))

        compose.onNodeWithContentDescription("🦄").assertIsDisplayed()
        // Once: one description, and the glyph's own text is cleared from what is spoken. (The raw text node
        // still exists in the unmerged tree, which is why the check is on the merged tree.)
        compose.onAllNodesWithContentDescription("🦄", useUnmergedTree = true).assertCountEquals(1)
        compose.onAllNodesWithText("🦄").assertCountEquals(0)
        val row = compose.onNodeWithText("Chosen emoji").fetchSemanticsNode().config
        row[SemanticsProperties.Text].map { it.text } shouldBe listOf("Chosen emoji")
        row[SemanticsProperties.ContentDescription] shouldBe listOf("🦄")
    }

    @Test
    fun `no chosen emoji row when the icon is a symbol or none`() {
        listOf(CategoryIcon.Symbol("pets"), null).forEach {
            show(it)

            compose.onAllNodesWithText("Chosen emoji").assertCountEquals(0)
        }
    }
}
