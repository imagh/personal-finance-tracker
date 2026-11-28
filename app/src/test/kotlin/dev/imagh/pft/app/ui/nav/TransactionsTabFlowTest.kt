package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.QueryWorld
import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.HeaderTags
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Clock
import java.time.Instant

/**
 * The Transactions tab end to end (slice C C4a): real in-memory Room through [PftNavHost] with a fixed
 * clock, "now" being 2026-11-15 10:00 in `Asia/Kolkata`. Seeded rows show under their day headers with
 * signed amounts and day totals, the banner speaks its words, the strip and a swipe change month, and a
 * tab switch keeps the month and reads again on return.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1400dp-xxhdpi")
class TransactionsTabFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val world = QueryWorld(kit)
    private val now = Clock.fixed(ist(2026, 11, 15, 10), RoomTestKit.ZONE)
    private var controller: NavHostController? = null
    private var activity: ComponentActivity? = null

    @After
    fun closeDatabase() = database.close()

    private fun showHost() {
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            val navController = rememberNavController()
            controller = navController
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftNavHost(
                    AppServices(database, EpochMillis.clock()),
                    hasIdentity = true,
                    navController = navController,
                    clock = { now },
                )
            }
        }
        compose.awaitHomeLoaded()
    }

    private fun openTransactions() {
        compose.tab("Transactions").performClick()
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun awaitDescription(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithContentDescription(text, substring = true).fetchSemanticsNodes().isNotEmpty()
        }

    /** The nodes whose whole description is [text]: a banner figure must not match the row that ends with it. */
    private fun described(text: String) = compose.onAllNodesWithContentDescription(text)

    private suspend fun spend(
        at: Instant,
        rupees: Long,
        title: String,
        category: Category,
        account: Account = world.bank,
    ) = kit.transactions.create(
        TransactionDraft(
            TransactionType.EXPENSE,
            Paise(rs(rupees)),
            at,
            account.meta.id,
            title = title,
            lines = listOf(LineDraft(amount = Paise(rs(rupees)), categoryId = category.meta.id)),
        ),
    )

    private suspend fun earn(
        at: Instant,
        rupees: Long,
        title: String,
    ) = kit.transactions.create(
        TransactionDraft(
            TransactionType.INCOME,
            Paise(rs(rupees)),
            at,
            world.bank.meta.id,
            title = title,
            lines = listOf(LineDraft(amount = Paise(rs(rupees)), categoryId = world.salary.meta.id)),
        ),
    )

    /** A household with entries today, yesterday, on the 12th (a transfer) and in October. */
    private fun seed() =
        runBlocking {
            world.start()
            kit.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(486)),
                    ist(2026, 11, 15, 9),
                    world.bank.meta.id,
                    title = "Big Basket",
                    lines =
                        listOf(
                            LineDraft(
                                amount = Paise(rs(486)),
                                categoryId = world.groceries.meta.id,
                                tagIds = setOf(world.trip.meta.id),
                            ),
                        ),
                ),
            )
            earn(ist(2026, 11, 14, 18), 1_000, "Refund")
            spend(ist(2026, 11, 14, 8), 200, "Metro", world.travel)
            kit.transfers.create(
                TransferDraft(world.bank.meta.id, world.savings.meta.id, Paise(rs(5_000)), ist(2026, 11, 12, 11)),
            )
            spend(ist(2026, 10, 20, 20), 300, "Old Cinema", world.dining)
        }

    // --- The bar

    @Test
    fun `the bar shows Home, Transactions, Accounts and More, in that order`() {
        seed()
        showHost()

        compose.tabLabels() shouldBe listOf("Home", "Transactions", "Accounts", "More")
        compose.selectedTabs() shouldBe listOf("Home")
    }

    // --- The list

    @Test
    fun `seeded rows show under their day headers with their amounts, and each day has its total`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")

        compose.onAllNodesWithText("Today").fetchSemanticsNodes().size shouldBe 1
        compose.onAllNodesWithText("Yesterday").fetchSemanticsNodes().size shouldBe 1
        compose.onAllNodesWithText("Nov 12, 2026").fetchSemanticsNodes().size shouldBe 1
        described("Big Basket, Groceries, HDFC, Trip, Expense, ₹486.00").fetchSemanticsNodes().size shouldBe 1
        described("Refund, Salary, HDFC, Income, ₹1,000.00").fetchSemanticsNodes().size shouldBe 1
        described("Metro, Travel, HDFC, Expense, ₹200.00").fetchSemanticsNodes().size shouldBe 1
        // Today: one expense. Yesterday: 1,000 in and 200 out. The 12th: a transfer, which is no income or expense.
        described("Net, minus ₹486.00").fetchSemanticsNodes().size shouldBe 1
        described("Net, ₹800.00").fetchSemanticsNodes().size shouldBe 1
        described("Net, ₹0.00").fetchSemanticsNodes().size shouldBe 1
    }

    @Test
    fun `a transfer shows from account to account and is not an expense or income`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("HDFC → SBI")

        described("Transfer, HDFC → SBI, Transfer, ₹5,000.00").fetchSemanticsNodes().size shouldBe 1
    }

    @Test
    fun `the banner says expense, income and net with words for a screen reader`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")

        // This month: 486 + 200 out, 1,000 in, so a net of 314 (the transfer is in neither).
        described("Expense, ₹686.00").fetchSemanticsNodes().size shouldBe 1
        described("Income, ₹1,000.00").fetchSemanticsNodes().size shouldBe 1
        described("Net, ₹314.00").fetchSemanticsNodes().size shouldBe 1
    }

    @Test
    fun `a month with no transactions shows the empty state and zero totals`() {
        runBlocking { world.start() }
        showHost()
        openTransactions()

        awaitText("No transactions this month")

        described("Expense, ₹0.00").fetchSemanticsNodes().size shouldBe 1
        described("Net, ₹0.00").fetchSemanticsNodes().size shouldBe 1
    }

    @Test
    fun `the empty state is not shown while the month has rows`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")

        compose.onAllNodesWithText("No transactions this month").fetchSemanticsNodes().size shouldBe 0
    }

    // --- The months

    @Test
    fun `this month is selected, and tapping October shows October's rows and totals`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")
        compose.onNodeWithContentDescription("November 2026").assertIsSelected()

        compose.onNodeWithContentDescription("October 2026").performClick()
        awaitDescription("Old Cinema")

        compose.onNodeWithContentDescription("October 2026").assertIsSelected()
        described("Big Basket").fetchSemanticsNodes().size shouldBe 0
        described("Expense, ₹300.00").fetchSemanticsNodes().size shouldBe 1
        compose.onAllNodesWithText("Today").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `swiping the list left shows the next month, empty past today, and right steps back`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")

        compose.onNodeWithTag(HeaderTags.LIST).performTouchInput { swipeLeft() }
        awaitText("No transactions this month")
        compose.onNodeWithContentDescription("December 2026").assertIsSelected()

        compose.onNodeWithTag(HeaderTags.LIST).performTouchInput { swipeRight() }
        awaitDescription("Big Basket")
        compose.onNodeWithContentDescription("November 2026").assertIsSelected()

        compose.onNodeWithTag(HeaderTags.LIST).performTouchInput { swipeRight() }
        awaitDescription("Old Cinema")
        compose.onNodeWithContentDescription("October 2026").assertIsSelected()
    }

    // --- Tabs

    @Test
    fun `switching to Accounts and back keeps the selected month and its rows`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")
        compose.onNodeWithContentDescription("October 2026").performClick()
        awaitDescription("Old Cinema")

        compose.tab("Accounts").performClick()
        awaitText("HDFC")
        openTransactions()
        awaitDescription("Old Cinema")

        compose.onNodeWithContentDescription("October 2026").assertIsSelected()
        described("Big Basket").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `a transaction written while another tab was open shows when the user comes back`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")
        compose.tab("Accounts").performClick()
        awaitText("HDFC")

        runBlocking { spend(ist(2026, 11, 15, 9, 30), 75, "Chai", world.dining) }
        openTransactions()

        awaitDescription("Chai, Dining, HDFC, Expense, ₹75.00")
        described("Expense, ₹761.00").fetchSemanticsNodes().size shouldBe 1
    }

    @Test
    fun `tapping the Transactions tab again keeps the month on its root`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")
        compose.onNodeWithContentDescription("October 2026").performClick()
        awaitDescription("Old Cinema")

        openTransactions()
        compose.waitForIdle()

        currentRoute() shouldBe Routes.TRANSACTIONS
        compose.onNodeWithContentDescription("October 2026").assertIsSelected()
    }

    @Test
    fun `Back from Transactions goes to Home`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")

        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()

        compose.awaitHomeLoaded()
        compose.selectedTabs() shouldBe listOf("Home")
    }

    private fun currentRoute(): String? {
        var route: String? = null
        compose.runOnUiThread { route = checkNotNull(controller).currentDestination?.route }
        return route
    }

    @Test
    fun `no row is tappable yet and the tab has no add button, the form arrives in the next change`() {
        seed()
        showHost()
        openTransactions()
        awaitDescription("Big Basket")

        compose
            .onAllNodes(hasContentDescription("Big Basket", substring = true) and hasClickAction())
            .fetchSemanticsNodes()
            .size shouldBe 0
        compose.onAllNodesWithContentDescription("Add transaction").fetchSemanticsNodes().size shouldBe 0
    }
}
