package dev.imagh.pft.app.ui.nav

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
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
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
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

/**
 * Adding and editing a transaction end to end (slice C C4b): real in-memory Room through [PftNavHost] with a fixed
 * clock ("now" is 2026-11-15 10:00 in `Asia/Kolkata`). The "+" on the Transactions tab opens the form, a saved entry
 * shows on the tab with the totals, a row tap opens it for editing, a transfer is one neutral row, and an archived
 * tag survives an edit of the title.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1400dp-xxhdpi")
class TransactionsFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val world = QueryWorld(kit)
    private val now = Clock.fixed(ist(2026, 11, 15, 10), RoomTestKit.ZONE)
    private var controller: NavHostController? = null

    @After
    fun closeDatabase() = database.close()

    private fun showHost() {
        compose.setContent {
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
            compose.onAllNodesWithContentDescription(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun described(text: String) = compose.onAllNodesWithContentDescription(text)

    private fun openAddForm() {
        compose.onNodeWithContentDescription("Add transaction").performClick()
        awaitText("Amount")
    }

    private fun type(
        label: String,
        text: String,
    ) = compose.onNodeWithText(label).performTextInput(text)

    private fun save() = compose.onNodeWithText("Save").performClick()

    /** Opens a picker field and taps [choice] in its sheet. */
    private fun choose(
        field: String,
        choice: String,
    ) {
        compose.onNodeWithContentDescription(field, substring = true).performClick()
        awaitText(choice)
        compose.onNodeWithText(choice).performClick()
        compose.waitForIdle()
    }

    private fun rowCount() = database.rowCount("ledger_transaction")

    /** Writes a one-rupee probe row while a form is open, so the tab's read on return has something to show. */
    private fun writeProbe() =
        runBlocking {
            kit.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(1)),
                    ist(2026, 11, 15, 9),
                    world.bank.meta.id,
                    title = "Probe",
                    lines = listOf(LineDraft(amount = Paise(rs(1)), categoryId = world.travel.meta.id)),
                ),
            )
        }

    /**
     * Waits for the probe on the list. The read that shows it started after the probe was written, so no read of
     * the tab is still in flight when the test ends and the database is closed under it.
     */
    private fun awaitProbe() = awaitDescription("Probe, Travel, HDFC, Expense, ₹1.00")

    private fun seedExpenseWithTags() =
        runBlocking {
            world.start()
            kit.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(250)),
                    ist(2026, 11, 15, 9),
                    world.bank.meta.id,
                    title = "Lunch",
                    lines =
                        listOf(
                            LineDraft(
                                amount = Paise(rs(250)),
                                categoryId = world.groceries.meta.id,
                                tagIds = setOf(world.trip.meta.id, world.gift.meta.id),
                            ),
                        ),
                ),
            )
        }

    @Test
    fun `the add button opens the form, which hides the tab bar, and Back returns without saving`() {
        runBlocking { world.start() }
        showHost()
        awaitText("No transactions this month")

        openAddForm()

        compose.tabCount() shouldBe 0
        compose.onNodeWithText("New entry").assertExists()
        rowCount() shouldBe 0
        writeProbe()
        compose.onNodeWithContentDescription("Back").performClick()
        awaitProbe()

        rowCount() shouldBe 1
    }

    @Test
    fun `add an expense from the tab, and its row and the totals show it`() {
        runBlocking { world.start() }
        showHost()
        awaitText("No transactions this month")
        openAddForm()

        type("Amount", "250")
        type("Title (optional)", "Lunch")
        choose("Account", "HDFC")
        choose("Category", "Groceries")
        compose.onNodeWithContentDescription("Tags", substring = true).performClick()
        awaitText("Trip")
        compose.onNodeWithText("Trip").performClick()
        compose.onNodeWithText("Gift").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()
        save()
        awaitDescription("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹250.00")

        described("Expense, ₹250.00").fetchSemanticsNodes().size shouldBe 1
        // The net shows twice: in the banner and as the day's total.
        described("Net, minus ₹250.00").fetchSemanticsNodes().size shouldBe 2
        rowCount() shouldBe 1
        compose.tabCount() shouldBe 4
    }

    @Test
    fun `an empty form shows its errors and saves nothing`() {
        runBlocking { world.start() }
        showHost()
        awaitText("No transactions this month")
        openAddForm()

        save()
        awaitText("Required")

        compose.onAllNodesWithText("Required").fetchSemanticsNodes().isNotEmpty() shouldBe true
        rowCount() shouldBe 0
    }

    @Test
    fun `tapping a row opens it for editing, and a new amount updates the row`() {
        seedExpenseWithTags()
        showHost()
        awaitDescription("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹250.00")

        described("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹250.00").onFirst().performClick()
        awaitText("Edit entry")
        compose.onNode(hasSetTextAction() and hasText("250.00")).performTextReplacement("300")
        save()
        awaitDescription("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹300.00")

        described("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹250.00").fetchSemanticsNodes().size shouldBe 0
        described("Expense, ₹300.00").fetchSemanticsNodes().size shouldBe 1
        rowCount() shouldBe 1
    }

    @Test
    fun `a transfer saves as one neutral row and leaves the totals unchanged`() {
        runBlocking { world.start() }
        showHost()
        awaitText("No transactions this month")
        openAddForm()

        compose.onNodeWithText("Transfer").performClick()
        awaitDescription("From account")
        type("Amount", "1000")
        choose("From account", "HDFC")
        choose("To account", "SBI")
        save()
        awaitDescription("Transfer, HDFC → SBI, Transfer, ₹1,000.00")

        described("Transfer, HDFC → SBI, Transfer, ₹1,000.00").fetchSemanticsNodes().size shouldBe 1
        described("Expense, ₹0.00").fetchSemanticsNodes().size shouldBe 1
        described("Income, ₹0.00").fetchSemanticsNodes().size shouldBe 1
        rowCount() shouldBe 2
    }

    @Test
    fun `an archived tag already on the item survives a title edit`() {
        val saved = seedExpenseWithTags()
        runBlocking { kit.tags.archive(world.trip.meta.id) }
        showHost()
        awaitDescription("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹250.00")

        described("Lunch, Groceries, HDFC, Gift, Trip, Expense, ₹250.00").onFirst().performClick()
        awaitText("Edit entry")
        compose.onNode(hasSetTextAction() and hasText("Lunch")).performTextReplacement("Dinner")
        save()
        awaitDescription("Dinner, Groceries, HDFC, Gift, Trip, Expense, ₹250.00")

        val stored = runBlocking { kit.transactions.get(saved.transaction.meta.id) }!!.lines.single()
        stored.tagIds shouldBe setOf(world.trip.meta.id, world.gift.meta.id)
    }

    @Test
    fun `opening the form for a transaction that no longer exists returns to the tab with a message`() {
        runBlocking { world.start() }
        showHost()
        awaitText("No transactions this month")

        writeProbe()
        compose.runOnUiThread { checkNotNull(controller).navigate(Routes.transactionEdit(id = "no-such-id")) }
        awaitText("That transaction no longer exists")
        awaitProbe()

        rowCount() shouldBe 1
        compose.tabCount() shouldBe 4
    }

    @Test
    fun `a deleted transaction's id does not open either`() {
        val saved = seedExpenseWithTags()
        runBlocking { kit.transactions.delete(saved.transaction.meta.id) }
        showHost()
        awaitText("No transactions this month")

        writeProbe()
        compose.runOnUiThread {
            checkNotNull(controller).navigate(Routes.transactionEdit(id = saved.transaction.meta.id.value))
        }
        awaitText("That transaction no longer exists")
        awaitProbe()

        compose.onAllNodesWithText("Edit entry").fetchSemanticsNodes().size shouldBe 0
        runBlocking { kit.transactions.get(TransactionId(saved.transaction.meta.id.value)) } shouldBe null
    }
}
