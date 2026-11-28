package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.query.MonthSpan
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.YearMonth

/**
 * The month strip (slice C C4a): the months it lists (pure), and how it draws, selects and scrolls
 * (Compose). Fake data only.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class MonthStripTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f
    private var selected by mutableStateOf(YearMonth.of(2026, 11))
    private val picked = mutableListOf<YearMonth>()

    private fun ym(
        year: Int,
        month: Int,
    ) = YearMonth.of(year, month)

    // --- The months (pure)

    @Test
    fun `a span lists every month from first to last, inclusive and in order`() {
        monthsIn(MonthSpan(ym(2025, 11), ym(2026, 2))) shouldBe
            listOf(ym(2025, 11), ym(2025, 12), ym(2026, 1), ym(2026, 2))
    }

    @Test
    fun `a one month span lists that month alone`() {
        monthsIn(MonthSpan(ym(2026, 11), ym(2026, 11))) shouldBe listOf(ym(2026, 11))
    }

    @Test
    fun `the year is shown under the first month and under every January, nowhere else`() {
        val months = monthsIn(MonthSpan(ym(2025, 10), ym(2027, 2)))

        val withYear = months.filterIndexed { index, month -> showsYear(index, month) }

        withYear shouldBe listOf(ym(2025, 10), ym(2026, 1), ym(2027, 1))
    }

    @Test
    fun `a strip that starts in January shows that year once`() {
        val months = monthsIn(MonthSpan(ym(2026, 1), ym(2026, 3)))

        months.filterIndexed { index, month -> showsYear(index, month) } shouldBe listOf(ym(2026, 1))
    }

    // --- The strip (Compose)

    private fun show(
        months: List<YearMonth>,
        widthDp: Int = 393,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                density = androidx.compose.ui.platform.LocalDensity.current.density
                Box(Modifier.width(widthDp.dp)) {
                    MonthStrip(months, selected, onSelect = {
                        picked += it
                        selected = it
                    })
                }
            }
        }
    }

    private fun month(name: String) = compose.onNodeWithContentDescription(name)

    private val isRadio = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)

    private val recent = monthsIn(MonthSpan(ym(2026, 9), ym(2026, 12)))

    @Test
    fun `each month is spoken with its full name and year, and exactly the selected one is selected`() {
        show(recent)

        month("November 2026").assertIsSelected()
        for (other in listOf("September 2026", "October 2026", "December 2026")) {
            val selectedFlag = month(other).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected)
            assertEquals("$other selected", false, selectedFlag)
        }
    }

    @Test
    fun `the months are one selectable group of radio buttons`() {
        show(recent)

        val group = SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)
        compose.onAllNodes(isRadio and hasAnyAncestor(group)).fetchSemanticsNodes().size shouldBe recent.size
    }

    @Test
    fun `only the selected month is underlined`() {
        show(recent)

        compose.onAllNodesWithTag(MonthStripTags.UNDERLINE, useUnmergedTree = true).assertCountEquals(1)
        compose
            .onAllNodes(
                hasTestTag(MonthStripTags.UNDERLINE) and hasAnyAncestor(hasContentDescription("November 2026")),
                useUnmergedTree = true,
            ).assertCountEquals(1)
    }

    @Test
    fun `the underline moves with the selection`() {
        show(recent)

        compose.runOnUiThread { selected = ym(2026, 10) }
        compose.waitForIdle()

        compose
            .onAllNodes(
                hasTestTag(MonthStripTags.UNDERLINE) and hasAnyAncestor(hasContentDescription("October 2026")),
                useUnmergedTree = true,
            ).assertCountEquals(1)
        compose.onAllNodesWithTag(MonthStripTags.UNDERLINE, useUnmergedTree = true).assertCountEquals(1)
    }

    @Test
    fun `a month shows its short name and the year only under the first month and each January`() {
        show(monthsIn(MonthSpan(ym(2025, 11), ym(2026, 2))))

        for (short in listOf("Nov", "Dec", "Jan", "Feb")) {
            compose.onAllNodesWithText(short, useUnmergedTree = true).assertCountEquals(1)
        }
        compose.onAllNodesWithText("2025", useUnmergedTree = true).assertCountEquals(1)
        compose.onAllNodesWithText("2026", useUnmergedTree = true).assertCountEquals(1)
    }

    @Test
    fun `tapping a month selects it`() {
        show(recent)

        month("October 2026").performClick()
        compose.waitForIdle()

        picked shouldBe listOf(ym(2026, 10))
        month("October 2026").assertIsSelected()
    }

    @Test
    fun `tapping the selected month again reports it, so the screen can decide`() {
        show(recent)

        month("November 2026").performClick()

        picked shouldBe listOf(ym(2026, 11))
    }

    @Test
    fun `every month is drawn at least 48dp wide and tall`() {
        show(recent)

        for (name in listOf("September 2026", "October 2026", "November 2026", "December 2026")) {
            val bounds = month(name).fetchSemanticsNode().boundsInRoot
            assertTrue("$name is ${bounds.width / density}x${bounds.height / density}dp", bounds.width / density >= 48f)
            assertTrue(
                "$name is ${bounds.width / density}x${bounds.height / density}dp",
                bounds.height / density >= 48f,
            )
        }
    }

    // --- Scrolling

    private val threeYears = monthsIn(MonthSpan(ym(2024, 1), ym(2026, 12)))

    @Test
    fun `a selected month far along the strip is scrolled into view when it first shows`() {
        selected = ym(2026, 11)

        show(threeYears, widthDp = 320)

        month("November 2026").assertIsDisplayed()
    }

    @Test
    fun `selecting a month that is off screen scrolls it into view`() {
        selected = ym(2024, 2)
        show(threeYears, widthDp = 320)
        month("February 2024").assertIsDisplayed()

        compose.runOnUiThread { selected = ym(2026, 6) }
        compose.waitForIdle()

        month("June 2026").assertIsDisplayed()
    }

    @Test
    fun `tapping the leftmost month that is fully in view does not move the strip`() {
        selected = ym(2026, 11)
        show(threeYears, widthDp = 320)
        compose.waitForIdle()
        val inView =
            compose
                .onAllNodes(isRadio)
                .fetchSemanticsNodes()
                // boundsInRoot is clipped to the strip, so a month cut off at an edge is narrower than 48dp.
                .filter { it.boundsInRoot.width >= 48f * density }
                .sortedBy { it.boundsInRoot.left }
        val target = inView.first()
        val name = target.config.getOrNull(SemanticsProperties.ContentDescription)!!.single()
        val before = target.boundsInRoot.left

        month(name).performClick()
        compose.waitForIdle()

        month(name).assertIsDisplayed()
        month(name).fetchSemanticsNode().boundsInRoot.left shouldBe before
        month(name).assertIsSelected()
    }
}
