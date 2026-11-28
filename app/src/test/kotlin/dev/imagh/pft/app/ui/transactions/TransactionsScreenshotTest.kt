package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.transactions.EntryFixtures.dining
import dev.imagh.pft.app.ui.transactions.EntryFixtures.groceries
import dev.imagh.pft.app.ui.transactions.EntryFixtures.line
import dev.imagh.pft.app.ui.transactions.EntryFixtures.names
import dev.imagh.pft.app.ui.transactions.EntryFixtures.salary
import dev.imagh.pft.app.ui.transactions.EntryFixtures.single
import dev.imagh.pft.app.ui.transactions.EntryFixtures.transfer
import dev.imagh.pft.app.ui.transactions.EntryFixtures.txn
import dev.imagh.pft.ledger.query.MonthSpan
import dev.imagh.pft.ledger.query.Totals
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

/**
 * Golden screenshots of the Transactions tab (slice C C4a) in light and dark, on a tall 393x1300dp xxhdpi screen
 * (so all three days show)
 * with the committed default scheme (dynamic colour off, so the goldens are deterministic): the header, the
 * month strip (August to November, November selected, the year under August), the totals banner
 * and three days of fake entries (a salary and a groceries run, an itemised purchase and a card bill
 * transfer, an unverified dining entry). Hand-built entries, no database. Fake data only.
 *
 * Record with `./gradlew :app:recordRoborazziDebug --tests '*TransactionsScreenshotTest'`; verify runs as
 * part of `check`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1300dp-xxhdpi")
class TransactionsScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun instant(
        day: Int,
        hour: Int,
    ): Instant = Instant.parse("2026-11-%02dT%02d:00:00Z".format(day, hour))

    private fun entries(): List<DayEntries> {
        val salaryEntry =
            single(
                txn("s-1", TransactionType.INCOME, 1_20_000_00, title = "Acme Payroll", occurredAt = instant(15, 3)),
                line("s-1", amount = 1_20_000_00, category = salary),
            )
        val basket =
            single(
                txn("g-1", amount = 48_600, title = "Big Basket", occurredAt = instant(15, 6)),
                line("g-1", amount = 48_600, category = groceries, tags = setOf("t-b")),
            )
        val market =
            single(
                txn("m-1", amount = 90_000, title = "Weekend market", occurredAt = instant(14, 7)),
                line("m-1", 1, 50_000, groceries),
                line("m-1", 2, 25_000, dining),
                line("m-1", 3, 15_000, null),
            )
        val bill = transfer("Card bill", 5_000_00, instant(14, 4))
        val swiggy =
            single(
                txn(
                    "d-1",
                    amount = 32_000,
                    title = "Swiggy",
                    state = TxnState.UNVERIFIED,
                    occurredAt = instant(12, 14),
                ),
                line("d-1", amount = 32_000, category = dining, tags = setOf("t-c", "t-b")),
            )
        return listOf(
            DayEntries(LocalDate.of(2026, 11, 15), listOf(basket, salaryEntry), Paise(11_951_400)),
            DayEntries(LocalDate.of(2026, 11, 14), listOf(market, bill), Paise(-90_000)),
            DayEntries(LocalDate.of(2026, 11, 12), listOf(swiggy), Paise(-32_000)),
        )
    }

    private val state =
        TransactionsUiState(
            month = YearMonth.of(2026, 11),
            span = MonthSpan(YearMonth.of(2026, 8), YearMonth.of(2026, 11)),
            today = LocalDate.of(2026, 11, 15),
            days = entries(),
            totals = Totals(expense = Paise(1_70_600), income = Paise(1_20_000_00)),
            names = names,
            loaded = true,
        )

    private fun capture(name: String) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.fillMaxSize().testTag("screen"), color = PftTheme.surfaces.canvas) {
                    TransactionsScreen(state, onSelectMonth = {}, onPreviousMonth = {}, onNextMonth = {})
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("screen").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun transactionsTabLight() = capture("transactions-tab-light")

    @Test
    @Config(qualifiers = "+night")
    fun transactionsTabDark() = capture("transactions-tab-dark")
}
