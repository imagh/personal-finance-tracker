package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * rendered through [PftRoot] and the navigation host, from Home to More to Accounts. It asserts what
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
        awaitText("Akash · Pixel")
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
        compose.onNodeWithContentDescription("More").performClick()
        awaitText("Accounts")
        compose.onNodeWithText("Accounts").performClick()
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
    fun `Home to More to Accounts shows the empty state, with no add control until the form exists`() {
        show()

        openAccounts()
        awaitText("No accounts yet")

        compose.onNodeWithText("No accounts yet").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Add account").fetchSemanticsNodes().size shouldBe 0
        compose.onAllNodesWithText("Add account").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `a seeded account shows with its type, issuer and masked last four`() {
        show(hdfc)

        openAccounts()
        awaitText("HDFC Savings")

        compose.onNodeWithText("Bank · HDFC · ••1234").assertIsDisplayed()
        compose.onNodeWithContentDescription("Bank").assertIsDisplayed()
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
    fun `Back from Accounts returns to More, and from More to Home`() {
        show()
        openAccounts()
        awaitText("No accounts yet")

        compose.onNodeWithContentDescription("Back").performClick()
        awaitGone("No accounts yet")
        compose.onNodeWithText("Accounts").assertIsDisplayed()

        systemBack()
        awaitText("Akash · Pixel")
        compose.onAllNodesWithText("Accounts").fetchSemanticsNodes().size shouldBe 0
        checkNotNull(activity).isFinishing shouldBe false

        systemBack()
        checkNotNull(activity).isFinishing shouldBe true
    }
}
