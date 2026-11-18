package dev.imagh.pft.app.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.HeaderTags
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [CategoryEditScreen] drawn from fake state (plan B4b): its fields and copy, the fixed kind and parent,
 * the parent field opening [dev.imagh.pft.app.ui.common.CategoryPicker], the colour and symbol choices, the
 * error under the name, Save and its disabled states, and that nothing is drawn before the form has loaded.
 * The state is held here and updated by the screen's actions, so the screen is exercised the way a
 * ViewModel drives it.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h2400dp-xxhdpi")
class CategoryEditScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val food = sampleCategory("food", "Food & dining", CategoryColour.CORAL, CategoryIcon.Symbol("restaurant"))
    private val transport = sampleCategory("transport", "Transport", CategoryColour.SKY, null)
    private val groceries = sampleCategory("groceries", "Groceries", CategoryColour.OLIVE, null, parent = "food")

    private var state by mutableStateOf(CategoryEditUiState())
    private var saves by mutableIntStateOf(0)
    private var backs by mutableIntStateOf(0)
    private val pasted = mutableListOf<String>()

    private fun create(parent: Boolean = false) =
        CategoryEditUiState(
            loaded = true,
            parentOptions = listOf(food, transport),
            parent = if (parent) transport else null,
        )

    /** Set by the test that checks the screen survives recreation; null means the plain rule hosts the screen. */
    private var restoration: StateRestorationTester? = null

    private fun show(initial: CategoryEditUiState = create()) {
        state = initial
        val content: @Composable () -> Unit = {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                CategoryEditScreen(
                    state = state,
                    actions =
                        CategoryEditActions(
                            onNameChange = { state = state.copy(name = state.name.edit(it)) },
                            onColourChange = { state = state.copy(colour = it) },
                            onIconChange = { state = state.copy(icon = it) },
                            onEmojiInput = { pasted += it },
                            onParentChange = { id ->
                                state =
                                    state.copy(parent = state.parentOptions.firstOrNull { it.meta.id == id })
                            },
                            save = { saves++ },
                        ),
                    onBack = { backs++ },
                )
            }
        }
        restoration?.setContent(content) ?: compose.setContent(content)
        compose.waitForIdle()
    }

    @Test
    fun `a new category form has its title, the name, the fixed type, a top-level parent, colours, symbols and Save`() {
        show()

        compose.onNodeWithTag(HeaderTags.TITLE_LARGE).assertTextEquals("New category")
        compose.onNodeWithText("Name").assertIsDisplayed()
        compose.onNodeWithText("Type").assertIsDisplayed()
        compose.onNodeWithText("Expense").assertIsDisplayed()
        compose.onNodeWithText("Parent category").assertIsDisplayed()
        compose.onNodeWithText("None (top level)").assertIsDisplayed()
        compose.onNodeWithText("Colour").assertIsDisplayed()
        compose.onNodeWithText("Symbol").assertIsDisplayed()
        compose.onNodeWithContentDescription("Amber").assertIsDisplayed()
        compose.onNodeWithContentDescription("Restaurant").assertIsDisplayed()
        compose.onNodeWithText("Save").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun `an income form says Income`() {
        show(create().copy(kind = CategoryKind.INCOME))

        compose.onNodeWithText("Income").assertIsDisplayed()
        compose.onAllNodesWithText("Expense").assertCountEquals(0)
    }

    @Test
    fun `the colour and symbol of the state are the selected ones`() {
        show(create().copy(colour = CategoryColour.AMBER, icon = CategoryIcon.Symbol("pets")))

        compose.onNodeWithContentDescription("Amber").assertIsSelected()
        compose.onNodeWithContentDescription("Pets").assertIsSelected()
    }

    @Test
    fun `choosing a colour and a symbol reports them`() {
        show()

        compose.onNodeWithContentDescription("Teal").performClick()
        compose.onNodeWithContentDescription("Pets").performClick()
        compose.waitForIdle()

        state.colour shouldBe CategoryColour.TEAL
        state.icon shouldBe CategoryIcon.Symbol("pets")
        compose.onNodeWithContentDescription("Teal").assertIsSelected()
    }

    @Test
    fun `typing in the name field reports the text`() {
        show()

        compose.onNodeWithText("Name").performTextInput("Pets")
        compose.waitForIdle()

        state.name.text shouldBe "Pets"
    }

    @Test
    fun `the name's error is shown under the name field`() {
        show(create().copy(name = FieldState("food", FieldError.Duplicate)))

        compose.onNodeWithText("Already in use").assertIsDisplayed()
    }

    // --- The icon: a Symbol side and an Emoji side (B4c)

    private fun isSymbolGridShown(): Boolean =
        compose.onAllNodesWithContentDescription("Restaurant").fetchSemanticsNodes().isNotEmpty()

    private fun isEmojiTabShown(): Boolean =
        compose.onAllNodesWithText("Type or paste an emoji").fetchSemanticsNodes().isNotEmpty()

    @Test
    fun `the icon section has Symbol and Emoji radio buttons, the side on show selected`() {
        show()

        compose.onNodeWithText("Icon").assertIsDisplayed()
        compose.onNodeWithText("Symbol").assertIsSelected()
        compose.onNodeWithText("Emoji").assertIsNotSelected()
        listOf("Symbol", "Emoji").forEach {
            compose.onNodeWithText(it).fetchSemanticsNode().config[SemanticsProperties.Role] shouldBe Role.RadioButton
        }
    }

    @Test
    fun `the Symbol and Emoji choices are in one selectable group`() {
        show()

        fun groupOf(label: String): Int {
            var node = compose.onNodeWithText(label).fetchSemanticsNode()
            while (!node.config.contains(SemanticsProperties.SelectableGroup)) {
                node = checkNotNull(node.parent) { "$label is not inside a selectable group" }
            }
            return node.id
        }

        groupOf("Symbol") shouldBe groupOf("Emoji")
    }

    @Test
    fun `a new category opens on the Symbol side`() {
        show()

        isSymbolGridShown() shouldBe true
        isEmojiTabShown() shouldBe false
    }

    @Test
    fun `choosing Emoji shows the quick-pick and the field, hides the symbol grid and changes no icon`() {
        show(create().copy(icon = CategoryIcon.Symbol("pets")))

        compose.onNodeWithText("Emoji").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("Emoji").assertIsSelected()
        compose.onNodeWithText("Symbol").assertIsNotSelected()
        isEmojiTabShown() shouldBe true
        isSymbolGridShown() shouldBe false
        compose.onNodeWithText(QUICK_PICK_EMOJI[0]).assertIsDisplayed()
        state.icon shouldBe CategoryIcon.Symbol("pets")
    }

    @Test
    fun `choosing Symbol again brings the grid back, still with no icon changed`() {
        show(create().copy(icon = CategoryIcon.Symbol("pets")))
        compose.onNodeWithText("Emoji").performClick()

        compose.onNodeWithText("Symbol").performClick()
        compose.waitForIdle()

        isSymbolGridShown() shouldBe true
        isEmojiTabShown() shouldBe false
        compose.onNodeWithContentDescription("Pets").assertIsSelected()
        state.icon shouldBe CategoryIcon.Symbol("pets")
    }

    @Test
    fun `a category whose icon is an emoji opens on the Emoji side, with its emoji shown as chosen`() {
        show(create().copy(icon = CategoryIcon.Emoji(QUICK_PICK_EMOJI[2])))

        compose.onNodeWithText("Emoji").assertIsSelected()
        isEmojiTabShown() shouldBe true
        isSymbolGridShown() shouldBe false
        compose.onNodeWithText("Chosen emoji").assertIsDisplayed()
    }

    @Test
    fun `an emoji category being edited opens on the Emoji side too`() {
        show(editing().copy(icon = CategoryIcon.Emoji(QUICK_PICK_EMOJI[2])))

        compose.onNodeWithText("Emoji").assertIsSelected()
        isEmojiTabShown() shouldBe true
    }

    @Test
    fun `a category with a symbol or no icon opens on the Symbol side, editing or not`() {
        show(editing().copy(icon = CategoryIcon.Symbol("pets")))

        compose.onNodeWithText("Symbol").assertIsSelected()
        isSymbolGridShown() shouldBe true
        isEmojiTabShown() shouldBe false
    }

    @Test
    fun `an edit form with no icon opens on the Symbol side`() {
        show(editing().copy(icon = null))

        compose.onNodeWithText("Symbol").assertIsSelected()
        isEmojiTabShown() shouldBe false
    }

    @Test
    fun `with an emoji icon the Symbol side selects no symbol and keeps the emoji`() {
        show(create().copy(icon = CategoryIcon.Emoji(QUICK_PICK_EMOJI[2])))

        compose.onNodeWithText("Symbol").performClick()
        compose.waitForIdle()

        isSymbolGridShown() shouldBe true
        compose.onNodeWithContentDescription("No symbol").assertIsNotSelected()
        compose.onNodeWithContentDescription("Pets").assertIsNotSelected()
        state.icon shouldBe CategoryIcon.Emoji(QUICK_PICK_EMOJI[2])
    }

    @Test
    fun `the side the user chose survives the screen being recreated`() {
        val tester = StateRestorationTester(compose)
        restoration = tester
        show(create().copy(icon = CategoryIcon.Symbol("pets")))
        compose.onNodeWithText("Emoji").performClick()
        compose.waitForIdle()

        tester.emulateSavedInstanceStateRestore()
        compose.waitForIdle()

        compose.onNodeWithText("Emoji").assertIsSelected()
        isEmojiTabShown() shouldBe true
        isSymbolGridShown() shouldBe false
    }

    @Test
    fun `tapping a quick-pick emoji reports it as the icon and stays on the Emoji side`() {
        show()
        compose.onNodeWithText("Emoji").performClick()

        compose.onNodeWithText(QUICK_PICK_EMOJI[4]).performClick()
        compose.waitForIdle()

        state.icon shouldBe CategoryIcon.Emoji(QUICK_PICK_EMOJI[4])
        compose.onNodeWithText("Emoji").assertIsSelected()
        compose.onNodeWithText("Chosen emoji").assertIsDisplayed()
    }

    @Test
    fun `text typed into the emoji field is reported as typed, and a rejection shows the fixed message`() {
        show(create().copy(icon = CategoryIcon.Symbol("pets")))
        compose.onNodeWithText("Emoji").performClick()
        compose.onAllNodesWithText("Enter just one emoji that this phone can show").assertCountEquals(0)

        compose.onNode(hasSetTextAction() and hasText("Type or paste an emoji")).performTextInput("not an emoji")
        state = state.copy(emojiRejected = true)
        compose.waitForIdle()

        pasted shouldBe listOf("not an emoji")
        compose.onNodeWithText("Enter just one emoji that this phone can show").assertIsDisplayed()
        compose.onAllNodesWithText("not an emoji", substring = true).assertCountEquals(0)
        state.icon shouldBe CategoryIcon.Symbol("pets")
    }

    // --- The parent field

    @Test
    fun `tapping the parent field offers the top-level choices, and picking one sets the parent`() {
        show()

        compose.onNodeWithText("Parent category").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Choose parent category").assertIsDisplayed()
        compose.onNodeWithText("Transport").performClick()
        compose.waitForIdle()

        state.parent?.name shouldBe "Transport"
        compose.onNodeWithText("Choose parent category").assertDoesNotExist()
        compose.onNodeWithText("Transport").assertIsDisplayed()
    }

    @Test
    fun `the parent picker offers only the parent choices the state holds, never a sub-category`() {
        show(create().copy(parentOptions = listOf(food, groceries, transport)))

        compose.onNodeWithText("Parent category").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onAllNodesWithText("Groceries").assertCountEquals(0)
    }

    @Test
    fun `with a parent chosen the field shows it, and Make top level clears it`() {
        show(create(parent = true))

        compose.onNodeWithText("Transport").assertIsDisplayed()
        compose.onAllNodesWithText("None (top level)").assertCountEquals(0)

        compose.onNodeWithText("Make top level").performClick()
        compose.waitForIdle()

        state.parent shouldBe null
        compose.onNodeWithText("None (top level)").assertIsDisplayed()
        compose.onAllNodesWithText("Make top level").assertCountEquals(0)
    }

    @Test
    fun `with no parent choices the picker says there is nothing to choose`() {
        show(create().copy(parentOptions = emptyList()))

        compose.onNodeWithText("Parent category").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("No categories to choose from yet").assertIsDisplayed()
    }

    // --- Edit mode: kind and parent are shown but fixed

    private fun editing(parent: Boolean = true) =
        CategoryEditUiState(
            editing = true,
            loaded = true,
            name = FieldState("Groceries"),
            parent = if (parent) food else null,
        )

    @Test
    fun `an edit form is titled Edit category and prefilled`() {
        show(editing())

        compose.onNodeWithTag(HeaderTags.TITLE_LARGE).assertTextEquals("Edit category")
        compose.onNodeWithText("Groceries").assertIsDisplayed()
    }

    @Test
    fun `when editing, the type and the parent are shown, say they are fixed, and cannot be tapped`() {
        show(editing())

        compose.onNodeWithText("Expense").assertIsDisplayed()
        compose.onNodeWithText("Food & dining").assertIsDisplayed()
        compose.onAllNodesWithText("Can't be changed after the category is created").assertCountEquals(2)
        compose.onAllNodesWithText("Make top level").assertCountEquals(0)
        // Neither fixed row has a click action, so no picker can be opened from them.
        listOf("Parent category", "Type").forEach { label ->
            val node = compose.onNodeWithText(label).fetchSemanticsNode()
            node.config.getOrNull(SemanticsActions.OnClick) shouldBe null
        }
        compose.onNodeWithText("Parent category").performClick()
        compose.waitForIdle()
        compose.onAllNodesWithText("Choose parent category").assertCountEquals(0)
    }

    @Test
    fun `a top-level category being edited shows no parent`() {
        show(editing(parent = false))

        compose.onNodeWithText("None (top level)").assertIsDisplayed()
    }

    // --- Save, Back and the states around them

    @Test
    fun `Save reports a save, and Back reports a back`() {
        show()

        compose.onNodeWithText("Save").performClick()
        compose.onNodeWithContentDescription("Back").performClick()

        saves shouldBe 1
        backs shouldBe 1
    }

    @Test
    fun `Save is disabled while saving`() {
        show(create().copy(saving = true))

        compose.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `nothing is drawn before the form has loaded, and Save is disabled`() {
        show(CategoryEditUiState(editing = true, loaded = false))

        compose.onAllNodesWithText("Name").assertCountEquals(0)
        compose.onAllNodesWithText("Colour").assertCountEquals(0)
        compose.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `nothing is drawn for a category that was not found, and Save is disabled`() {
        show(CategoryEditUiState(editing = true, loaded = true, notFound = true))

        compose.onAllNodesWithText("Name").assertCountEquals(0)
        compose.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `after a failed save a fixed try-again line appears as a polite live region`() {
        show(create().copy(saveFailed = true))

        compose.onNodeWithText("Couldn't save. Try again.").assertIsDisplayed()
        compose
            .onNodeWithText("Couldn't save. Try again.")
            .fetchSemanticsNode()
            .config[SemanticsProperties.LiveRegion] shouldBe LiveRegionMode.Polite
    }

    @Test
    fun `without a failure there is no try-again line`() {
        show()

        compose.onAllNodesWithText("Couldn't save. Try again.").assertCountEquals(0)
    }
}
