package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.moveBy
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `Modifier.pressScale()` (plan §3.3, UI-10): the target shrinks to 0.97 while pressed and returns
 * to 1.0 on release. The scale is read as the width of the node's bounds in the root. The test tag
 * goes after `pressScale()` so the tagged node sits inside the graphics layer, whose scale is then
 * part of the bounds the semantics tree reports.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PressScaleTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val side = 200.dp

    private fun ComposeContentTestRule.scale(
        tag: String,
        unscaledWidth: Dp = side,
    ): Float {
        val bounds = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        return bounds.width / (unscaledWidth.value * density.density)
    }

    private lateinit var density: Density

    /** Advances the manual clock frame by frame so coroutines resumed by a frame can start the next one. */
    private fun settle() = repeat(60) { compose.mainClock.advanceTimeByFrame() }

    private fun setThemedContent(content: @Composable () -> Unit) =
        compose.setContent {
            density = LocalDensity.current
            PftTheme(content = content)
        }

    @Test
    fun `an unpressed target is at full size`() {
        setThemedContent { Box(Modifier.pressScale().testTag("target").size(side)) }
        compose.waitForIdle()
        assertEquals(1f, compose.scale("target"), 0.001f)
    }

    @Test
    fun `while pressed the target settles to 0_97 and on release returns to 1_0`() {
        setThemedContent { Box(Modifier.pressScale().testTag("target").size(side)) }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false

        compose.onNodeWithTag("target").performTouchInput { down(center) }
        settle()
        assertEquals(0.97f, compose.scale("target"), 0.001f)

        compose.onNodeWithTag("target").performTouchInput { up() }
        settle()
        assertEquals(1f, compose.scale("target"), 0.001f)
    }

    @Test
    fun `the press animates rather than jumping`() {
        setThemedContent { Box(Modifier.pressScale().testTag("target").size(side)) }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false

        compose.onNodeWithTag("target").performTouchInput { down(center) }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeByFrame()
        val mid = compose.scale("target")
        assertTrue("expected a scale between 0.97 and 1.0 mid-animation, was $mid", mid < 1f && mid > 0.97f)
    }

    @Test
    fun `the press does not swallow a click`() {
        var clicks by mutableIntStateOf(0)
        setThemedContent {
            Box(
                Modifier
                    .pressScale()
                    .testTag("target")
                    .clickable { clicks++ }
                    .size(side),
            )
        }
        compose.onNodeWithTag("target").performTouchInput {
            down(center)
            up()
        }
        compose.waitForIdle()
        assertEquals(1, clicks)
    }

    @Test
    fun `dragging away to scroll releases the press`() {
        setThemedContent {
            LazyColumn(Modifier.size(width = 300.dp, height = 400.dp)) {
                items(listOf(1, 2, 3, 4, 5, 6, 7, 8)) {
                    Box(Modifier.pressScale().testTag("row$it").size(300.dp, 100.dp))
                }
            }
        }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("row1").performTouchInput {
            down(center)
            moveBy(Offset(0f, -300f))
        }
        settle()
        assertEquals(1f, compose.scale("row1", unscaledWidth = 300.dp), 0.001f)
    }
}
