package dev.imagh.pft.app.ui.transactions

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.query.MonthSpan
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate
import java.time.YearMonth

/**
 * [TransactionsScreen] from a hand-made state (slice C C4a): what it draws while a month loads and when a
 * month is empty, and what the strip and the swipe hand back. The flow with real data is
 * `TransactionsTabFlowTest`; pixels are `TransactionsScreenshotTest`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TransactionsScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val november = YearMonth.of(2026, 11)
    private val picked = mutableListOf<YearMonth>()

    private fun show(loaded: Boolean) {
        val state =
            TransactionsUiState(
                month = november,
                span = MonthSpan(YearMonth.of(2026, 9), november),
                today = LocalDate.of(2026, 11, 15),
                loaded = loaded,
            )
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                TransactionsScreen(state, onSelectMonth = { picked += it }, onPreviousMonth = {}, onNextMonth = {})
            }
        }
    }

    @Test
    fun `while the month loads only the strip shows, no banner and no empty message`() {
        show(loaded = false)

        compose.onNodeWithContentDescription("November 2026").assertIsSelected()
        compose.onAllNodesWithContentDescription("Expense, ₹0.00").assertCountEquals(0)
        compose.onAllNodesWithText("No transactions this month").assertCountEquals(0)
    }

    @Test
    fun `an empty loaded month shows the banner at zero and says there is nothing`() {
        show(loaded = true)

        compose.onAllNodesWithContentDescription("Expense, ₹0.00").assertCountEquals(1)
        compose.onAllNodesWithText("No transactions this month").assertCountEquals(1)
    }

    @Test
    fun `tapping a month in the strip hands that month to the screen's owner`() {
        show(loaded = true)

        compose.onNodeWithContentDescription("September 2026").performClick()

        picked shouldBe listOf(YearMonth.of(2026, 9))
    }
}
