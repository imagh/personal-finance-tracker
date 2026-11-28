package dev.imagh.pft.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.assertShownInFull
import dev.imagh.pft.app.ui.common.layout
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `PftTabBar` (Cobalt plan K7, D8): one tab per entry of [TAB_ORDER], each a `Role.Tab` inside one
 * selectable group, exactly one `selected`; the active tab shows its label and the others are
 * icon-only but still spoken by name (once); every target is at least 48dp; the bar holds at 200%
 * text on a 320dp screen. Stateless, so everything is driven from a `mutableStateOf`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PftTabBarTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f
    private var selected by mutableStateOf(Tab.HOME)
    private val picked = mutableListOf<Tab>()

    private val labels =
        mapOf(
            Tab.HOME to "Home",
            Tab.TRANSACTIONS to "Transactions",
            Tab.ACCOUNTS to "Accounts",
            Tab.MORE to "More",
        )

    private fun show(
        fontScale: Float = 1f,
        widthDp: Int = 393,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    density = LocalDensity.current.density
                    Box(Modifier.width(widthDp.dp)) { PftTabBar(TAB_ORDER, selected, onSelect = { picked += it }) }
                }
            }
        }
    }

    private fun select(tab: Tab) {
        compose.runOnUiThread { selected = tab }
        compose.waitForIdle()
    }

    private val isTab = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)

    private fun tabs(): List<SemanticsNode> = compose.onAllNodes(isTab).fetchSemanticsNodes()

    private fun SemanticsNode.isSelected() = config.getOrNull(SemanticsProperties.Selected) == true

    /** Everything TalkBack would read for the node: its merged text and its content description. */
    private fun SemanticsNode.spoken(): List<String> =
        config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } +
            config.getOrNull(SemanticsProperties.ContentDescription).orEmpty()

    private fun Rect.inDp() = Rect(left / density, top / density, right / density, bottom / density)

    @Test
    fun `there is one Role Tab per tab, in the order of TAB_ORDER, each spoken by its label`() {
        show()

        val spoken = tabs().sortedBy { it.boundsInRoot.left }.map { it.spoken() }
        spoken shouldBe TAB_ORDER.map { listOf(labels.getValue(it)) }
    }

    @Test
    fun `exactly the selected tab is selected, whichever tab it is`() {
        show()

        for (tab in TAB_ORDER) {
            select(tab)
            val selectedLabels =
                tabs().filter { it.isSelected() }.map { it.spoken() }
            selectedLabels shouldBe listOf(listOf(labels.getValue(tab)))
            tabs().size shouldBe TAB_ORDER.size
        }
    }

    @Test
    fun `only the active tab shows its label as text, the others are icon-only`() {
        show()

        for (tab in TAB_ORDER) {
            select(tab)
            for (other in TAB_ORDER) {
                compose.onAllNodesWithText(labels.getValue(other)).assertCountEquals(if (other == tab) 1 else 0)
            }
        }
    }

    @Test
    fun `an inactive tab is named by its content description and the active one is not named twice`() {
        show()

        select(Tab.ACCOUNTS)

        compose.onAllNodesWithContentDescription("Home").assertCountEquals(1)
        compose.onAllNodesWithContentDescription("More").assertCountEquals(1)
        compose.onAllNodesWithContentDescription("Accounts").assertCountEquals(0)
        tabs().forEach { it.spoken().size shouldBe 1 }
    }

    @Test
    fun `the tabs are one selectable group, so TalkBack presents them as a set`() {
        show()

        val group = SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)
        compose.onAllNodes(isTab and hasAnyAncestor(group)).assertCountEquals(TAB_ORDER.size)
    }

    @Test
    fun `tapping a tab hands that tab to onSelect, the active one included`() {
        show()

        compose.onNodeWithContentDescription("More").performClick()
        compose.onNode(hasText("Home")).performClick()
        compose.onNodeWithContentDescription("Accounts").performClick()

        picked shouldBe listOf(Tab.MORE, Tab.HOME, Tab.ACCOUNTS)
    }

    @Test
    fun `every tab is drawn at least 48dp tall and wide, active or not`() {
        show()

        // Drawn bounds, not touchBoundsInRoot: Compose pads touch bounds up to 48dp whatever is drawn.
        for (tab in TAB_ORDER) {
            select(tab)
            for (node in tabs()) {
                val drawn = node.boundsInRoot.inDp()
                assertTrue("a tab is ${drawn.width}x${drawn.height}dp: ${node.spoken()}", drawn.width >= 48f)
                assertTrue("a tab is ${drawn.width}x${drawn.height}dp: ${node.spoken()}", drawn.height >= 48f)
            }
        }
    }

    @Test
    fun `the active label is semibold or heavier`() {
        show()

        for (tab in TAB_ORDER) {
            select(tab)
            val weight =
                compose
                    .onAllNodesWithText(
                        labels.getValue(tab),
                    ).onFirst()
                    .layout()
                    .layoutInput.style.fontWeight
            assertTrue(
                "the $tab label is weight ${weight?.weight}",
                (weight?.weight ?: 0) >= FontWeight.SemiBold.weight,
            )
        }
    }

    @Test
    fun `the active pill has roomy padding, at least 12dp each side and 8dp between icon and label`() {
        show()

        for (tab in TAB_ORDER) {
            select(tab)
            val pill =
                tabs()
                    .single { it.isSelected() }
                    .boundsInRoot
                    .inDp()
                    .width
            val label =
                compose
                    .onAllNodesWithText(labels.getValue(tab))
                    .onFirst()
                    .layout()
                    .size.width / density
            val spare = pill - 24f - label
            assertTrue(
                "the $tab pill is ${pill}dp for a 24dp icon and a ${label}dp label: $spare spare",
                spare >= 31.9f,
            )
        }
    }

    /** The tabs' bounds in dp, left to right. */
    private fun tabBounds() = tabs().sortedBy { it.boundsInRoot.left }.map { it.boundsInRoot.inDp() }

    @Test
    fun `at 200 percent text on a 320dp screen every tab stays in the bar, 48dp wide, and a shown label is whole`() {
        show(fontScale = 2f, widthDp = 320)

        for (tab in TAB_ORDER) {
            select(tab)
            val shown = compose.onAllNodesWithText(labels.getValue(tab))
            if (shown.fetchSemanticsNodes().isNotEmpty()) shown.onFirst().layout().assertShownInFull("active $tab")
            val bounds = tabBounds()
            assertTrue("a tab leaves the 320dp screen: $bounds", bounds.all { it.left >= 0f && it.right <= 320f })
            val overlaps = bounds.zipWithNext().filter { (a, b) -> a.right > b.left + 0.5f }
            assertTrue("tabs overlap at 200%: $overlaps", overlaps.isEmpty())
            bounds.forEach { assertTrue("a tab is under 48dp wide at 200%: $bounds", it.width >= 48f) }
        }
    }

    @Test
    fun `a label that cannot fit beside three 48dp targets is left out and the tab is named by its description`() {
        show(fontScale = 2f, widthDp = 320)

        select(Tab.TRANSACTIONS)

        compose.onAllNodesWithText("Transactions").assertCountEquals(0)
        compose.onAllNodesWithContentDescription("Transactions").assertCountEquals(1)
        tabs().single { it.isSelected() }.spoken() shouldBe listOf("Transactions")
        tabs().forEach { it.spoken().size shouldBe 1 }
    }

    @Test
    fun `a label that fits is still shown at 200 percent, so the bar only gives way when it must`() {
        show(fontScale = 2f, widthDp = 320)

        select(Tab.HOME)

        compose.onAllNodesWithText("Home").assertCountEquals(1)
        compose.onAllNodesWithContentDescription("Home").assertCountEquals(0)
    }

    @Test
    fun `every label is shown on a 360dp screen at normal text`() {
        show(widthDp = 360)

        for (tab in TAB_ORDER) {
            select(tab)
            compose.onAllNodesWithText(labels.getValue(tab)).assertCountEquals(1)
            tabBounds().forEach { assertTrue("a tab is under 48dp wide: ${tabBounds()}", it.width >= 48f) }
        }
    }

    @Test
    fun `the label fits exactly when the pill and the other tabs at 48dp add up to the room or less`() {
        // 24 icon + 8 gap + 100 label + 2 x 12 padding = 156 for the pill, and 3 x 48 = 144 for the others.
        assertTrue(activeLabelFits(100.dp, tabCount = 4, room = 300.dp))
        assertFalse(activeLabelFits(100.dp, tabCount = 4, room = 299.9.dp))
        // One more tab needs 48dp more.
        assertFalse(activeLabelFits(100.dp, tabCount = 5, room = 347.9.dp))
        assertTrue(activeLabelFits(100.dp, tabCount = 5, room = 348.dp))
    }
}
