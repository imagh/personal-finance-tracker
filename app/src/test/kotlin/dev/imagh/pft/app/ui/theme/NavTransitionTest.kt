package dev.imagh.pft.app.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The navigation transitions (plan §3.3): a push slides the incoming screen in from the end by 25%
 * of the width and the outgoing one out toward the start; a pop is the reverse. Driven through
 * `AnimatedVisibility` with a manual clock and read from the screen's bounds.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w400dp-h800dp-mdpi")
class NavTransitionTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var visible by mutableStateOf(false)

    private fun show(
        enter: EnterTransition,
        exit: ExitTransition,
        initiallyVisible: Boolean,
    ) {
        visible = initiallyVisible
        compose.setContent {
            AnimatedVisibility(visible, enter = enter, exit = exit) { Box(Modifier.fillMaxSize().testTag("screen")) }
        }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
    }

    private fun left() =
        compose
            .onNodeWithTag("screen")
            .fetchSemanticsNode()
            .boundsInRoot.left

    /** One frame at a time, letting recomposition run in between, as a real display would. */
    private fun frames(count: Int) =
        repeat(count) {
            compose.mainClock.advanceTimeByFrame()
            compose.waitForIdle()
        }

    private fun settle() = frames(80)

    /** Flips visibility and runs about 190 ms of frames: the transition is under way, not finished. */
    private fun toggle(to: Boolean) {
        visible = to
        frames(12)
    }

    @Test
    fun `a pushed screen slides in from the end by a quarter of the width`() {
        show(PftMotion.navEnter, PftMotion.navExit, initiallyVisible = false)
        toggle(true)
        assertTrue("mid-slide from the end, was ${left()}", left() > 0f && left() < 100f)
        settle()
        assertEquals(0f, left(), 0.5f)
    }

    @Test
    fun `the screen being pushed over slides out toward the start by a quarter of the width`() {
        show(PftMotion.navEnter, PftMotion.navExit, initiallyVisible = true)
        toggle(false)
        assertTrue("mid-slide toward the start, was ${left()}", left() < 0f && left() > -100f)
        settle()
        compose.onNodeWithTag("screen").assertDoesNotExist()
    }

    @Test
    fun `a popped-back screen slides in from the start`() {
        show(PftMotion.navPopEnter, PftMotion.navPopExit, initiallyVisible = false)
        toggle(true)
        assertTrue("mid-slide from the start, was ${left()}", left() < 0f && left() > -100f)
        settle()
        assertEquals(0f, left(), 0.5f)
    }

    @Test
    fun `the popped screen slides out toward the end`() {
        show(PftMotion.navPopEnter, PftMotion.navPopExit, initiallyVisible = true)
        toggle(false)
        assertTrue("mid-slide toward the end, was ${left()}", left() > 0f && left() < 100f)
        settle()
        compose.onNodeWithTag("screen").assertDoesNotExist()
    }
}
