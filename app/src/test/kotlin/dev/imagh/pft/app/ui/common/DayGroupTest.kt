package dev.imagh.pft.app.ui.common

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

/**
 * `DayGroup` (Cobalt plan K5, D3): a day header (Today, Yesterday or the date, and an optional net
 * total) over one island of `TransactionRow`s with hairline dividers. Fake data only.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class DayGroupTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val today = LocalDate.of(2026, 11, 25)
    private val minus = "−"

    private fun row(
        title: String,
        onClick: (() -> Unit)? = null,
    ) = TransactionRowModel(
        title = title,
        glyph = GlyphSource.Symbol(PftSymbol.RESTAURANT),
        colourKey = "coral",
        category = "Dining out",
        account = "HDFC card",
        amount = Paise(48_600),
        semantic = MoneySemantic.Expense,
        onClick = onClick,
    )

    private fun show(
        day: LocalDate,
        rows: List<TransactionRowModel> = listOf(row("Swiggy"), row("BigBasket")),
        total: Paise? = null,
        today: LocalDate = this.today,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { DayGroup(day, today, rows, total = total) }
        }
    }

    @Test
    fun `the header says Today for today`() {
        show(today)
        compose.onNodeWithText("Today").assertIsDisplayed()
    }

    @Test
    fun `the header says Yesterday for the day before`() {
        show(today.minusDays(1))
        compose.onNodeWithText("Yesterday").assertIsDisplayed()
    }

    @Test
    fun `yesterday is the day before across a year boundary`() {
        show(day = LocalDate.of(2025, 12, 31), today = LocalDate.of(2026, 1, 1))
        compose.onNodeWithText("Yesterday").assertIsDisplayed()
    }

    @Test
    fun `an older day shows its date`() {
        show(LocalDate.of(2026, 11, 20))
        compose.onNodeWithText("Nov 20, 2026").assertIsDisplayed()
        compose.onAllNodes(hasText("Today") or hasText("Yesterday")).assertCountEquals(0)
    }

    @Test
    fun `a future day shows its date, not Today`() {
        show(today.plusDays(1))
        compose.onNodeWithText("Nov 26, 2026").assertIsDisplayed()
    }

    @Test
    fun `the header is a heading`() {
        show(today)
        val config = compose.onNodeWithText("Today").fetchSemanticsNode().config
        assertTrue("not a heading", config.getOrNull(SemanticsProperties.Heading) != null)
    }

    private fun netFigures() =
        compose.onAllNodes(hasContentDescription("Net", substring = true), useUnmergedTree = true)

    @Test
    fun `no total is shown when none is given`() {
        show(today, total = null)
        compose.onNodeWithText("Today").assertIsDisplayed()
        netFigures().assertCountEquals(0)
    }

    @Test
    fun `a negative day total is a signed net figure`() {
        show(today, total = Paise(-280_050))
        netFigures().assertCountEquals(1)
        compose.onNode(hasContentDescription("Net, minus ₹2,800.50"), useUnmergedTree = true).assertExists()
        compose.onNodeWithText("$minus₹2,800.50").assertIsDisplayed()
    }

    @Test
    fun `a positive day total carries a plus`() {
        show(today.minusDays(1), total = Paise(11_968_800))
        compose.onNodeWithText("+₹1,19,688.00").assertIsDisplayed()
    }

    @Test
    fun `the rows sit in one island with a divider between each pair`() {
        show(today, rows = listOf(row("A"), row("B"), row("C")))
        compose.onAllNodesWithTag(GroupedSectionTags.ROW).assertCountEquals(3)
        compose.onAllNodesWithTag(GroupedSectionTags.DIVIDER).assertCountEquals(2)
    }

    @Test
    fun `tapping a row calls its own onClick`() {
        val taps = mutableListOf<String>()
        show(today, rows = listOf(row("A") { taps += "A" }, row("B") { taps += "B" }))
        compose.onNode(hasContentDescription("B, Dining out, HDFC card, Expense, ₹486.00")).performClick()
        assertEquals(listOf("B"), taps)
    }

    @Test
    fun `a group with no rows is rejected, since it would be a header over nothing`() {
        val failure = runCatching { show(today, rows = emptyList()) }.exceptionOrNull()
        assertTrue("$failure", failure is IllegalArgumentException)
    }
}
