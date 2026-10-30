package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
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
import org.robolectric.annotation.GraphicsMode

/** `GroupedSection` (plan §3.4, §6.1 DS5): an inset rounded island of rows with hairline dividers between them. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class GroupedSectionTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f
    private var rowCount by mutableIntStateOf(0)

    private fun Float.dp() = this / density

    private fun SemanticsNodeInteraction.bounds(): Rect =
        fetchSemanticsNode().boundsInRoot.let { Rect(it.left.dp(), it.top.dp(), it.right.dp(), it.bottom.dp()) }

    private fun showRows(
        count: Int,
        onClick: ((Int) -> Unit)? = null,
    ) {
        rowCount = count
        compose.setContent {
            density = LocalDensity.current.density
            PftTheme(ThemePrefs(dynamicColor = false)) {
                GroupedSection(Modifier.testTag("section")) {
                    repeat(rowCount) { index ->
                        row(
                            onClick = onClick?.let { { it(index) } },
                        ) { Text("Row $index", Modifier.testTag("row-$index")) }
                    }
                }
            }
        }
    }

    private fun dividers() = compose.onAllNodesWithTag(GroupedSectionTags.DIVIDER)

    private fun rows() = compose.onAllNodesWithTag(GroupedSectionTags.ROW)

    @Test
    fun `N rows draw N minus 1 dividers`() {
        showRows(1)
        listOf(1, 2, 3, 5).forEach { count ->
            compose.runOnUiThread { rowCount = count }
            compose.waitForIdle()
            dividers().assertCountEquals(count - 1)
        }
    }

    @Test
    fun `no rows draw no divider`() {
        showRows(0)
        dividers().assertCountEquals(0)
    }

    @Test
    fun `a divider is inset from the island's start edge and is a hairline`() {
        showRows(2)
        val row = rows()[0].bounds()
        val divider = dividers()[0].fetchSemanticsNode().boundsInRoot
        assertTrue(
            "divider starts at ${divider.left.dp()} on a row at ${row.left}",
            divider.left.dp() >= row.left + 16f,
        )
        assertEquals("a divider is one physical pixel tall", 1f, divider.height, 0.01f)
        assertEquals(row.right, divider.right.dp(), 0.5f)
    }

    @Test
    fun `the island is inset 20dp from the screen edges`() {
        showRows(2)
        val row = rows()[0].bounds()
        assertEquals(20f, row.left, 0.5f)
        assertEquals(393f - 20f, row.right, 0.5f)
    }

    @Test
    fun `a clickable row is at least 56dp tall however little it holds, and a click reaches its callback`() {
        val clicked = mutableListOf<Int>()
        showRows(2) { clicked += it }
        listOf(0, 1).forEach { index ->
            val height = rows()[index].bounds().height
            assertTrue("row $index is ${height}dp tall", height >= 56f)
        }
        rows()[1].performClick()
        assertEquals(listOf(1), clicked)
    }

    @Test
    fun `a row without a click handler is not clickable`() {
        showRows(2)
        rows()[0].assertHasNoClickAction()
    }

    @Test
    fun `a clickable row is announced as a button`() {
        showRows(2) {}
        rows()[0].assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun `a plain row has no role`() {
        showRows(2)
        rows()[0].assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    @Test
    fun `a row with a click handler exposes a click action`() {
        showRows(2) {}
        rows()[0].assertHasClickAction()
    }

    @Test
    fun `a list item inside the island is given a transparent container so the island shows through`() {
        var container = Color.Unspecified
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                container =
                    GroupedSectionDefaults.listItemColors().containerColor
            }
        }
        compose.waitForIdle()
        assertEquals(Color.Transparent, container)
    }
}
