package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.RowAction
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.transactions.EntryFixtures.at
import dev.imagh.pft.app.ui.transactions.EntryFixtures.groceries
import dev.imagh.pft.app.ui.transactions.EntryFixtures.line
import dev.imagh.pft.app.ui.transactions.EntryFixtures.names
import dev.imagh.pft.app.ui.transactions.EntryFixtures.single
import dev.imagh.pft.app.ui.transactions.EntryFixtures.txn
import dev.imagh.pft.ledger.query.MonthSpan
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.money.Paise
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate
import java.time.YearMonth

/**
 * Golden screenshot of the Transactions tab with its add button (slice C C4b), light, on a 393x851dp xxhdpi
 * screen: one day with one entry, and the "+" floating button at the bottom end. The tab without the button
 * is the C4a golden, which this change leaves alone. Hand-built entries, no database. Fake data only.
 *
 * Record with `./gradlew :app:recordRoborazziDebug --tests '*TransactionsAddScreenshotTest'`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-notnight-xxhdpi")
class TransactionsAddScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val state =
        TransactionsUiState(
            month = YearMonth.of(2026, 11),
            span = MonthSpan(YearMonth.of(2026, 9), YearMonth.of(2026, 11)),
            today = LocalDate.of(2026, 11, 15),
            days =
                listOf(
                    DayEntries(
                        LocalDate.of(2026, 11, 15),
                        listOf(
                            single(
                                txn("g-1", amount = 48_600, title = "Big Basket", occurredAt = at),
                                line("g-1", amount = 48_600, category = groceries),
                            ),
                        ),
                        Paise(-48_600),
                    ),
                ),
            totals = Totals(expense = Paise(48_600), income = Paise.ZERO),
            names = names,
            loaded = true,
        )

    @Test
    fun transactionsTabWithAddLight() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.fillMaxSize().testTag("screen"), color = PftTheme.surfaces.canvas) {
                    TransactionsScreen(
                        state,
                        onSelectMonth = {},
                        onPreviousMonth = {},
                        onNextMonth = {},
                        add = RowAction("Add transaction") {},
                        onOpen = {},
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("screen").captureRoboImage("src/test/screenshots/transactions-tab-add-light.png")
    }
}
