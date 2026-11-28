package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.PftRoot
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.ids.AccountId
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

/**
 * The accounts list end to end (PRD L-4, INT-3): the real [AppContainer] over a real Room database,
 * rendered through [PftRoot] and the navigation host, from Home to the Accounts tab. It asserts what
 * is on screen and what is persisted, never that anything was called.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountsFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private var activity: ComponentActivity? = null

    @After
    fun closeDatabase() = database.close()

    /** Sets up the identity (Akash on Pixel) so the app starts on Home, then shows the whole app. */
    private fun show(vararg accounts: AccountDraft): List<AccountId> {
        val ids =
            runBlocking {
                kit.initialise()
                accounts.map {
                    kit.accounts
                        .create(it)
                        .meta.id
                }
            }
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        compose.awaitHomeLoaded()
        return ids
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun awaitGone(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }

    private fun openAccounts() {
        compose.tab("Accounts").performClick()
    }

    private fun chooseFromMenu(
        account: String,
        action: String,
    ) {
        compose.onNodeWithContentDescription("Actions for $account").performClick()
        compose.onNodeWithText(action).performClick()
    }

    private fun systemBack() {
        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    private val hdfc = AccountDraft("HDFC Savings", AccountType.BANK, Paise(1_000_000), "1234", "HDFC")

    @Test
    fun `the Accounts tab shows the empty state with an Add account control`() {
        show()

        openAccounts()
        awaitText("No accounts yet")

        compose.onNodeWithText("No accounts yet").assertIsDisplayed()
        compose.onNodeWithContentDescription("Add account").assertIsDisplayed()
        compose.onNodeWithText("Add account").assertIsDisplayed()
    }

    @Test
    fun `a seeded account shows with its type, issuer and masked last four`() {
        show(hdfc)

        openAccounts()
        awaitText("HDFC Savings")

        compose.onNodeWithText("Bank · HDFC · ••1234").assertIsDisplayed()
        // The badge is silent: the supporting line above already speaks the type once.
        compose.onAllNodesWithContentDescription("Bank").fetchSemanticsNodes().size shouldBe 0
        compose.onAllNodesWithText("No accounts yet").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `archive moves the account under Archived, where it can be expanded and unarchived`() {
        val (id) = show(hdfc)
        openAccounts()
        awaitText("HDFC Savings")

        chooseFromMenu("HDFC Savings", "Archive")
        awaitText("Archived (1)")

        compose.onAllNodesWithText("HDFC Savings").fetchSemanticsNodes().size shouldBe 0
        runBlocking { kit.accounts.lists() }.archived.map { it.meta.id } shouldBe listOf(id)

        compose.onNodeWithText("Archived (1)").performClick()
        awaitText("HDFC Savings")
        chooseFromMenu("HDFC Savings", "Unarchive")
        awaitGone("Archived (1)")

        compose.onNodeWithText("HDFC Savings").assertIsDisplayed()
        runBlocking { kit.accounts.lists() }.active.map { it.meta.id } shouldBe listOf(id)
        runBlocking { kit.trailOf("account", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
    }

    @Test
    fun `delete shows an Undo snackbar, and Undo brings the account back`() {
        val (id) = show(hdfc)
        openAccounts()
        awaitText("HDFC Savings")

        chooseFromMenu("HDFC Savings", "Delete")
        awaitText("Account deleted")

        compose.onNodeWithText("Undo").assertIsDisplayed()
        compose.onAllNodesWithText("HDFC Savings").fetchSemanticsNodes().size shouldBe 0
        runBlocking { kit.accounts.all() } shouldBe emptyList()

        compose.onNodeWithText("Undo").performClick()
        awaitText("HDFC Savings")

        awaitGone("Account deleted")
        runBlocking { kit.accounts.lists() }.active.map { it.meta.id } shouldBe listOf(id)
        runBlocking { kit.trailOf("account", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
    }

    @Test
    fun `Back from Accounts goes to Home, and Back on Home finishes the activity`() {
        show()
        openAccounts()
        awaitText("No accounts yet")
        compose.onAllNodesWithContentDescription("Back").fetchSemanticsNodes().size shouldBe 0

        systemBack()
        compose.awaitHomeLoaded()
        compose.onAllNodesWithText("No accounts yet").fetchSemanticsNodes().size shouldBe 0
        checkNotNull(activity).isFinishing shouldBe false

        systemBack()
        checkNotNull(activity).isFinishing shouldBe true
    }

    // --- Create and edit (B2b)

    private fun openAddForm() {
        compose.onNodeWithContentDescription("Add account").performClick()
        awaitText("Current balance")
    }

    private fun type(
        label: String,
        text: String,
    ) = compose.onNodeWithText(label).performTextInput(text)

    private fun save() = compose.onNodeWithText("Save").performClick()

    @Test
    fun `the Add button opens the form, and saving a valid account lists it`() {
        show()
        openAccounts()
        awaitText("No accounts yet")
        openAddForm()

        type("Name", "HDFC Savings")
        compose.onNodeWithText("Bank").performClick()
        type("Last 4 digits (optional)", "1234")
        type("Issuer (optional)", "HDFC")
        type("Current balance", "10,000")
        save()
        awaitGone("Current balance")
        awaitText("Bank · HDFC · ••1234")

        compose.onNodeWithText("HDFC Savings").assertIsDisplayed()
        compose.onAllNodesWithText("No accounts yet").fetchSemanticsNodes().size shouldBe 0
        val stored = runBlocking { kit.accounts.all() }.single()
        stored.name shouldBe "HDFC Savings"
        stored.type shouldBe AccountType.BANK
        stored.openingBalance shouldBe Paise(1_000_000)
        stored.maskedIdentifier shouldBe "1234"
        stored.issuer shouldBe "HDFC"
        runBlocking { kit.trailOf("account", stored.meta.id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE)
    }

    @Test
    fun `a credit card asks for the amount owed and stores it as a negative balance`() {
        show()
        openAccounts()
        awaitText("No accounts yet")
        openAddForm()

        type("Name", "Amex")
        compose.onNodeWithText("Credit card").performClick()
        awaitText("Amount owed")
        type("Amount owed", "5,000")
        save()
        awaitGone("Amount owed")
        awaitText("Amex")

        runBlocking { kit.accounts.all() }.single().openingBalance shouldBe Paise(-500_000)
    }

    @Test
    fun `an invalid last four shows its error under the field and stays on the form`() {
        show()
        openAccounts()
        awaitText("No accounts yet")
        openAddForm()

        type("Name", "HDFC Savings")
        type("Last 4 digits (optional)", "12")
        type("Current balance", "10,000")
        save()
        awaitText("Enter exactly 4 digits")

        compose.onNodeWithText("Enter exactly 4 digits").assertIsDisplayed()
        compose.onNodeWithText("Current balance").assertIsDisplayed()
        runBlocking { kit.accounts.all() } shouldBe emptyList()
        database.rowCount("audit_entry") shouldBe 3L // the setup identity only: nothing was audited for an account
    }

    @Test
    fun `tapping a row opens it for editing, and a new name shows in the list`() {
        val (id) = show(hdfc)
        openAccounts()
        awaitText("HDFC Savings")

        compose.onNodeWithText("HDFC Savings").performClick()
        awaitText("Edit account")

        compose.onNode(hasSetTextAction() and hasText("HDFC Savings")).assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("10,000.00")).assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("1234")).assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("HDFC")).assertIsDisplayed()

        compose.onNode(hasSetTextAction() and hasText("HDFC Savings")).performTextReplacement("HDFC Salary")
        save()
        awaitGone("Current balance")
        awaitText("HDFC Salary")

        compose.onAllNodesWithText("HDFC Savings").fetchSemanticsNodes().size shouldBe 0
        runBlocking { kit.accounts.all() }.single().name shouldBe "HDFC Salary"
        runBlocking { kit.trailOf("account", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `Back from the form returns to the list without saving`() {
        show(hdfc)
        openAccounts()
        awaitText("HDFC Savings")
        compose.onNodeWithText("HDFC Savings").performClick()
        awaitText("Edit account")
        compose.onNode(hasSetTextAction() and hasText("HDFC Savings")).performTextReplacement("Changed")

        compose.onNodeWithContentDescription("Back").performClick()
        awaitGone("Current balance")

        compose.onNodeWithText("HDFC Savings").assertIsDisplayed()
        runBlocking { kit.accounts.all() }.single().name shouldBe "HDFC Savings"
    }

    @Test
    fun `an account deleted elsewhere while the list is shown returns to the list with a no longer exists message`() {
        val (id) = show(hdfc)
        openAccounts()
        awaitText("HDFC Savings")
        runBlocking { kit.accounts.delete(id) }

        compose.onNodeWithText("HDFC Savings").performClick()
        awaitText("That account no longer exists")

        compose.onAllNodesWithText("Current balance").fetchSemanticsNodes().size shouldBe 0
        awaitText("No accounts yet")
        runBlocking { kit.trailOf("account", id.value) }.map { it.action } shouldBe
            listOf(AuditAction.CREATE, AuditAction.DELETE)
    }
}
