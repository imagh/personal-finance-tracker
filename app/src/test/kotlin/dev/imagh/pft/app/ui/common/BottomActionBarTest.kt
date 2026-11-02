package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * `BottomActionBar` (plan §3.4, UI-7): the primary action is a full-width button in the thumb zone,
 * at least 48dp tall; with two actions the primary is on the end side.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class BottomActionBarTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = Density(1f)
    private val windowHeight = 851.dp
    private val windowWidth = 393.dp

    private fun setBar(
        primary: BottomBarAction,
        secondary: BottomBarAction? = null,
    ) {
        compose.setContent {
            density = LocalDensity.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Scaffold(bottomBar = { BottomActionBar(primary = primary, secondary = secondary) }) { padding ->
                    Box(Modifier.padding(padding))
                }
            }
        }
        compose.waitForIdle()
    }

    private fun bounds(text: String) = compose.onNodeWithText(text).fetchSemanticsNode().boundsInRoot

    private fun Float.toDp() = (this / density.density).dp

    @Test
    fun `the primary button is in the thumb zone and at least 48dp tall`() {
        setBar(BottomBarAction("Save") {})
        val save = bounds("Save")
        assertTrue("bottom was ${save.bottom.toDp()}", windowHeight - save.bottom.toDp() <= 120.dp)
        assertTrue("height was ${save.height.toDp()}", save.height.toDp() >= 48.dp)
    }

    @Test
    fun `a lone primary spans the width between the gutters`() {
        setBar(BottomBarAction("Continue") {})
        val button = bounds("Continue")
        assertTrue("width was ${button.width.toDp()}", button.width.toDp() >= windowWidth - 48.dp)
    }

    @Test
    fun `with two actions the primary is on the end side and both are tall enough`() {
        setBar(BottomBarAction("Save") {}, secondary = BottomBarAction("Cancel") {})
        val save = bounds("Save")
        val cancel = bounds("Cancel")
        assertTrue("primary starts at ${save.left}, secondary ends at ${cancel.right}", save.left >= cancel.right)
        assertTrue(save.height.toDp() >= 48.dp)
        assertTrue(cancel.height.toDp() >= 48.dp)
        assertTrue("both sit in the thumb zone", windowHeight - cancel.bottom.toDp() <= 120.dp)
    }

    @Test
    fun `each action invokes only its own callback`() {
        var primaryClicks by mutableIntStateOf(0)
        var secondaryClicks by mutableIntStateOf(0)
        setBar(BottomBarAction("Save") { primaryClicks++ }, secondary = BottomBarAction("Cancel") { secondaryClicks++ })

        compose.onNodeWithText("Save").performClick()
        assertEquals(1, primaryClicks)
        assertEquals(0, secondaryClicks)

        compose.onNodeWithText("Cancel").performClick()
        assertEquals(1, primaryClicks)
        assertEquals(1, secondaryClicks)
    }

    @Test
    fun `a disabled primary is not clickable`() {
        var clicks by mutableIntStateOf(0)
        setBar(BottomBarAction("Save", enabled = false) { clicks++ })

        compose.onNodeWithText("Save").assertIsNotEnabled()
        compose.onNodeWithText("Save").performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun `an enabled primary is clickable`() {
        setBar(BottomBarAction("Save") {})
        compose.onNodeWithText("Save").assertIsEnabled().assertHasClickAction()
    }
}
