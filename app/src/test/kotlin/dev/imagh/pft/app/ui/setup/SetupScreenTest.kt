package dev.imagh.pft.app.ui.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.HeaderTags
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The stateless setup screen, rendered from fake state: its copy, the two fields, the error text,
 * and Continue as the bottom bar's primary action (a button outside the list, with a 48dp target,
 * disabled while saving).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class SetupScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = Density(1f)
    private var state by mutableStateOf(SetupUiState(deviceLabel = FieldState("Pixel 8")))
    private val names = mutableListOf<String>()
    private val labels = mutableListOf<String>()
    private var continues by mutableIntStateOf(0)

    private fun show(initial: SetupUiState = state) {
        state = initial
        compose.setContent {
            density = LocalDensity.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                SetupScreen(
                    state = state,
                    onNameChange = { names += it },
                    onLabelChange = { labels += it },
                    onContinue = { continues++ },
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun `it shows the title, the subtitle, the explanation and both fields`() {
        show()

        compose.onNodeWithTag(HeaderTags.TITLE_LARGE).assertTextEquals("Welcome")
        compose.onNodeWithText("Set up this phone").assertIsDisplayed()
        compose.onNode(hasText("encrypted", substring = true)).assertIsDisplayed()
        compose.onNodeWithText("Your name").assertIsDisplayed()
        compose.onNodeWithText("This phone's name").assertIsDisplayed()
        compose.onNodeWithText("Pixel 8").assertIsDisplayed()
    }

    @Test
    fun `there is no Back button, because nothing is behind setup`() {
        show()
        compose.onAllNodesWithText("Back").assertCountEquals(0)
    }

    @Test
    fun `Continue is the bottom bar's button, outside the list, with a 48dp touch target`() {
        show()

        val continueNode = compose.onNodeWithText("Continue")
        continueNode.assertIsDisplayed().assertIsEnabled()
        assertEquals(Role.Button, continueNode.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role))
        compose.onNode(hasText("Continue") and hasAnyAncestor(hasTestTag(HeaderTags.LIST))).assertDoesNotExist()

        val touch = continueNode.fetchSemanticsNode().touchBoundsInRoot
        assertTrue("height was ${touch.height.toDp()}", touch.height.toDp() >= 48.dp)
        assertTrue("width was ${touch.width.toDp()}", touch.width.toDp() >= 48.dp)
        val bottom =
            continueNode
                .fetchSemanticsNode()
                .boundsInRoot.bottom
                .toDp()
        assertTrue("bottom was $bottom", 851.dp - bottom <= 80.dp)
    }

    @Test
    fun `tapping Continue calls it once`() {
        show()
        compose.onNodeWithText("Continue").performClick()
        assertEquals(1, continues)
    }

    @Test
    fun `while saving Continue is disabled and ignores taps`() {
        show(SetupUiState(saving = true))

        compose.onNodeWithText("Continue").assertIsNotEnabled().performClick()

        assertEquals(0, continues)
    }

    @Test
    fun `a name error shows Required under the name field only`() {
        show(SetupUiState(name = FieldState("", FieldError.Required), deviceLabel = FieldState("Pixel 8")))

        compose.onAllNodesWithText("Required").assertCountEquals(1)
    }

    @Test
    fun `a failed save shows the try-again message, announced politely, and Continue is enabled`() {
        show(SetupUiState(name = FieldState("Akash"), deviceLabel = FieldState("Pixel 8"), saveFailed = true))

        val message = compose.onNodeWithText("Couldn't save. Try again.")
        message.assertIsDisplayed()
        assertEquals(
            LiveRegionMode.Polite,
            message.fetchSemanticsNode().config.getOrNull(SemanticsProperties.LiveRegion),
        )
        compose.onNodeWithText("Continue").assertIsEnabled()
        compose.onNodeWithText("Akash").assertIsDisplayed()
    }

    @Test
    fun `without a failed save there is no try-again message`() {
        show()
        compose.onAllNodesWithText("Couldn't save. Try again.").assertCountEquals(0)
    }

    @Test
    fun `a phone-name error shows its own text`() {
        show(SetupUiState(name = FieldState("Akash"), deviceLabel = FieldState("x", FieldError.TooLong(100))))

        compose.onNodeWithText("At most 100 characters").assertIsDisplayed()
        compose.onAllNodesWithText("Required").assertCountEquals(0)
    }

    @Test
    fun `typing reports the new text of the field it was typed in`() {
        show()

        compose.onNode(hasSetTextAction() and hasText("Your name")).performTextInput("Akash")

        assertEquals(listOf("Akash"), names)
        assertEquals(emptyList<String>(), labels)
    }

    @Test
    fun `typing in the phone-name field reports to the label callback`() {
        show()

        compose.onNode(hasSetTextAction() and hasText("Pixel 8")).performTextReplacement("Pixel 9")

        assertEquals(listOf("Pixel 9"), labels)
        assertEquals(emptyList<String>(), names)
    }

    private fun Float.toDp() = (this / density.density).dp
}
