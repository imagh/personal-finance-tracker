package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
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
 * `EditFormScaffold` (plan D6, UI-7): a Standard header screen whose primary action lives in the
 * bottom bar, is disabled while saving, and whose optional Back is a 48dp-target header button.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class EditFormScaffoldTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = Density(1f)
    private val windowHeight = 851.dp

    /**
     * The bar's height: a 56dp primary button plus 12dp of padding above and below. The primary's
     * bottom edge must sit within one bar-height of the window bottom (no system inset in this window).
     */
    private val barHeight = 80.dp

    private fun setForm(
        primaryLabel: String = "Save",
        saving: Boolean = false,
        onBack: (() -> Unit)? = null,
        onPrimary: () -> Unit = {},
        rows: Int = 3,
    ) {
        compose.setContent {
            density = LocalDensity.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EditFormScaffold(
                    "Edit account",
                    onPrimary = onPrimary,
                    primaryLabel = primaryLabel,
                    saving = saving,
                    onBack = onBack,
                ) { listRows(rows) }
            }
        }
        compose.waitForIdle()
    }

    private fun androidx.compose.foundation.lazy.LazyListScope.listRows(count: Int) {
        items((1..count).toList()) { Text("Row $it") }
    }

    private fun Float.toDp() = (this / density.density).dp

    @Test
    fun `the primary action defaults to Save and Back to Back, from string resources`() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                EditFormScaffold("Edit account", onPrimary = {}, onBack = {}) {}
            }
        }
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Back").assertIsDisplayed()
        compose.onNodeWithText("Save").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun `the caller can name the primary action`() {
        setForm(primaryLabel = "Continue")
        compose.onNodeWithText("Continue").assertIsDisplayed()
        compose.onNodeWithText("Save").assertDoesNotExist()
    }

    @Test
    fun `the primary action is a button in the bottom bar, outside the scrolling list`() {
        setForm(rows = 60)
        val save = compose.onNodeWithText("Save")
        save.assertIsDisplayed()
        assertEquals(Role.Button, save.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role))
        compose.onNode(hasText("Save") and hasAnyAncestor(hasTestTag(HeaderTags.LIST))).assertDoesNotExist()

        // It stays put while the list scrolls to its end, and sits in the thumb zone.
        compose.onNodeWithTag(HeaderTags.LIST).performScrollToNode(hasText("Row 60"))
        compose.onNodeWithText("Row 60").assertIsDisplayed()
        save.assertIsDisplayed()
        val bottom =
            save
                .fetchSemanticsNode()
                .boundsInRoot.bottom
                .toDp()
        assertTrue("bottom was $bottom", windowHeight - bottom <= barHeight)
    }

    @Test
    fun `tapping the primary action invokes it once`() {
        var taps by mutableIntStateOf(0)
        setForm(onPrimary = { taps++ })

        compose.onNodeWithText("Save").performClick()

        assertEquals(1, taps)
    }

    @Test
    fun `while saving the primary action is disabled and ignores taps`() {
        var taps by mutableIntStateOf(0)
        setForm(saving = true, onPrimary = { taps++ })

        compose.onNodeWithText("Save").assertIsNotEnabled()
        compose.onNodeWithText("Save").performClick()

        assertEquals(0, taps)
    }

    @Test
    fun `without onBack there is no Back button`() {
        setForm(onBack = null)
        compose.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun `with onBack a Back button exists, has a 48dp touch target and calls back`() {
        var backs by mutableIntStateOf(0)
        setForm(onBack = { backs++ })

        val back = compose.onNodeWithContentDescription("Back")
        back.assertIsDisplayed()
        val touch = back.fetchSemanticsNode().touchBoundsInRoot
        assertTrue("width was ${touch.width.toDp()}", touch.width.toDp() >= 48.dp)
        assertTrue("height was ${touch.height.toDp()}", touch.height.toDp() >= 48.dp)

        back.performClick()
        assertEquals(1, backs)
    }

    @Test
    fun `the content items render in the list under the title`() {
        setForm(rows = 3)
        compose.onNodeWithTag(HeaderTags.TITLE_LARGE).assertTextEquals("Edit account")
        listOf("Row 1", "Row 2", "Row 3").forEach { compose.onNodeWithText(it).assertIsDisplayed() }
    }
}
