package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.assertShownInFull
import dev.imagh.pft.app.ui.common.layout
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The totals banner (slice C C4a): the month's expense, income and net, each with a word and an arrow
 * or sign, spoken once, neutral except income, and whole at 200% text. Fake data only.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TotalsBannerTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var neutral = Color.Unspecified
    private var positive = Color.Unspecified

    private val minus = "−"
    private val figure = PaiseFormat.format(Paise.MAX_AMOUNT)

    private fun show(
        totals: Totals,
        fontScale: Float = 1f,
        widthDp: Int = 393,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    neutral = MaterialTheme.colorScheme.onSurface
                    positive = PftTheme.money.positive
                    Box(Modifier.width(widthDp.dp)) { TotalsBanner(totals, Modifier.testTag("banner")) }
                }
            }
        }
    }

    private fun totals(
        expense: Long,
        income: Long,
    ) = Totals(Paise(expense), Paise(income))

    private fun text(value: String) = compose.onNode(hasText(value), useUnmergedTree = true)

    /** Everything a screen reader gets for the nodes that carry a description, in reading order. */
    private fun spoken(): List<String> =
        compose
            .onAllNodes(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription),
            ).fetchSemanticsNodes()
            .flatMap { it.config.getOrNull(SemanticsProperties.ContentDescription).orEmpty() }

    @Test
    fun `it shows expense with a minus, income with a plus and net, each beside its word`() {
        show(totals(expense = 2_050_00, income = 30_000_00))

        text("$minus₹2,050.00").assertExists()
        text("+₹30,000.00").assertExists()
        text("+₹27,950.00").assertExists()
        for (word in listOf("Expense", "Income", "Net")) {
            compose.onAllNodesWithText(word, useUnmergedTree = true).assertCountEquals(1)
        }
    }

    @Test
    fun `each figure is spoken once with its word, so TalkBack says expense, income and net`() {
        show(totals(expense = 2_050_00, income = 30_000_00))

        spoken() shouldContainExactly listOf("Expense, ₹2,050.00", "Income, ₹30,000.00", "Net, ₹27,950.00")
        // The visible word and figure are drawn but not offered to a screen reader a second time.
        for (visible in listOf("Expense", "Income", "Net", "$minus₹2,050.00", "+₹30,000.00", "+₹27,950.00")) {
            compose.onAllNodesWithText(visible).assertCountEquals(0)
        }
    }

    @Test
    fun `a negative net is shown with a minus, spoken with the word minus, and is not red`() {
        show(totals(expense = 500_00, income = 200_00))

        text("$minus₹300.00").assertExists()
        spoken().last() shouldBe "Net, minus ₹300.00"
        text("$minus₹300.00")
            .layout()
            .layoutInput.style.color shouldBe neutral
    }

    @Test
    fun `a month with nothing in it shows zeros and an unsigned net`() {
        show(totals(expense = 0, income = 0))

        text("$minus₹0.00").assertExists()
        text("+₹0.00").assertExists()
        text("₹0.00").assertExists()
        spoken() shouldContainExactly listOf("Expense, ₹0.00", "Income, ₹0.00", "Net, ₹0.00")
    }

    @Test
    fun `expense and net are neutral and only income is green`() {
        show(totals(expense = 2_050_00, income = 30_000_00))

        text("$minus₹2,050.00")
            .layout()
            .layoutInput.style.color shouldBe neutral
        text("+₹27,950.00")
            .layout()
            .layoutInput.style.color shouldBe neutral
        text("+₹30,000.00")
            .layout()
            .layoutInput.style.color shouldBe positive
    }

    @Test
    fun `the banner is information, not a control`() {
        show(totals(expense = 100_00, income = 0))

        compose.onNodeWithTag("banner").assertHasNoClickAction()
    }

    @Test
    fun `at 200 percent text on a 320dp screen a crore amount is shown whole`() {
        show(totals(expense = 1_000_000_000, income = 1_000_000_000), fontScale = 2f, widthDp = 320)

        text(minus + "₹1,00,00,000.00").layout().assertShownInFull("expense")
        text("+₹1,00,00,000.00").layout().assertShownInFull("income")
    }

    @Test
    fun `at 200 percent the largest amount wraps whole and is never cut`() {
        val max = Paise.MAX_AMOUNT.value
        show(totals(expense = max, income = max), fontScale = 2f, widthDp = 320)

        val layout = text(minus + figure).layout()
        assertEquals((minus + figure).length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertFalse("the amount was ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
        assertTrue(
            "a line is wider than its box",
            (0 until layout.lineCount).all { layout.getLineRight(it) <= layout.size.width + 0.5f },
        )
    }

    @Test
    fun `the largest amounts are shown whole on one line at normal text`() {
        val max = Paise.MAX_AMOUNT.value
        show(totals(expense = max, income = max))

        text(minus + figure).layout().assertShownInFull("expense")
        text("+$figure").layout().assertShownInFull("income")
    }
}
