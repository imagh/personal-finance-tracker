package dev.imagh.pft.app.ui.theme

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

/** The spacing scale under `PftTheme` (a 4dp grid, §3.3). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftSpacingTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun themedSpacing(): PftSpacing {
        lateinit var spacing: PftSpacing
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { spacing = PftTheme.spacing } }
        compose.waitForIdle()
        return spacing
    }

    private fun PftSpacing.steps(): List<Dp> = listOf(xxs, xs, sm, md, lg, gutter, xl, xxl, huge)

    @Test
    fun `the scale has the specified values`() {
        val spacing = themedSpacing()
        assertEquals(listOf(2.dp, 4.dp, 8.dp, 12.dp, 16.dp, 20.dp, 24.dp, 32.dp, 48.dp), spacing.steps())
    }

    @Test
    fun `steps strictly grow and sit on the 4dp grid apart from the 2dp hairline step`() {
        val steps = themedSpacing().steps()
        assertTrue(steps.zipWithNext().all { (a, b) -> a < b })
        assertTrue(steps.drop(1).all { it.value % 4f == 0f })
    }
}
