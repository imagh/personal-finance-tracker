package dev.imagh.pft.app.ui.accounts

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
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
 * [AccountsRoute]'s lifecycle behaviour (plan D2 and §10): the list is read again when the screen
 * resumes, which is how it picks up an edit made on another screen, and an undo offer ends when the
 * screen is paused. The screen's lifecycle is driven by hand through a [LifecycleRegistry].
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountsRouteTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val services = AppServices(database, EpochMillis.clock(), audit)

    /** The real audit log until [failing] is set; then every append throws, so a write fails and rolls back. */
    private class FailsOnDemand(
        private val real: AuditLog,
    ) : AuditLog {
        var failing = false

        override suspend fun append(entry: AuditEntry) {
            check(!failing) { "secret detail HDFC Savings" }
            real.append(entry)
        }
    }

    private val owner =
        object : LifecycleOwner {
            val registry = LifecycleRegistry(this).also { it.currentState = Lifecycle.State.RESUMED }
            override val lifecycle: Lifecycle get() = registry
        }

    @After
    fun closeDatabase() = database.close()

    private fun create(name: String) =
        runBlocking {
            services.accounts
                .create(AccountDraft(name, AccountType.BANK, Paise(0)))
                .meta.id
        }

    private var notFound by mutableStateOf(false)
    private var notFoundShown = 0
    private val added = mutableListOf<Unit>()
    private val edited = mutableListOf<AccountId>()

    private fun show() {
        runBlocking { services.identity.initialise("Akash", "Pixel") }
        create("HDFC Savings")
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    AccountsRoute(
                        services,
                        AccountsNavigation(back = {}, add = { added += Unit }, edit = { edited += it }),
                        notFound = notFound,
                        onNotFoundShown = {
                            notFoundShown++
                            notFound = false
                        },
                    )
                }
            }
        }
        awaitText("HDFC Savings")
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun moveTo(state: Lifecycle.State) {
        compose.runOnUiThread { owner.registry.currentState = state }
        compose.waitForIdle()
    }

    @Test
    fun `an account added while the screen was away shows once the screen resumes, and not before`() {
        show()
        create("Zeta")

        compose.waitForIdle()
        compose.onAllNodesWithText("Zeta").fetchSemanticsNodes().size shouldBe 0

        moveTo(Lifecycle.State.STARTED)
        moveTo(Lifecycle.State.RESUMED)
        awaitText("Zeta")

        compose.onNodeWithText("Zeta").assertIsDisplayed()
        compose.onNodeWithText("HDFC Savings").assertIsDisplayed()
    }

    @Test
    fun `pausing the screen ends the undo offer, and the delete stands`() {
        show()
        compose.onNodeWithContentDescription("Actions for HDFC Savings").performClick()
        compose.onNodeWithText("Delete").performClick()
        awaitText("Account deleted")

        moveTo(Lifecycle.State.STARTED)
        compose.onAllNodesWithText("Account deleted").fetchSemanticsNodes().size shouldBe 0
        moveTo(Lifecycle.State.RESUMED)
        compose.waitForIdle()

        compose.onAllNodesWithText("Account deleted").fetchSemanticsNodes().size shouldBe 0
        compose.onAllNodesWithText("HDFC Savings").fetchSemanticsNodes().size shouldBe 0
        runBlocking { services.accounts.all() } shouldBe emptyList()
    }

    @Test
    fun `a failed action shows a fixed message without any name or exception text, and the list is unchanged`() {
        show()
        audit.failing = true

        compose.onNodeWithContentDescription("Actions for HDFC Savings").performClick()
        compose.onNodeWithText("Archive").performClick()
        awaitText("Couldn't archive. Try again.")

        compose.onNodeWithText("HDFC Savings").assertIsDisplayed()
        compose.onAllNodesWithText("secret detail", substring = true).fetchSemanticsNodes().size shouldBe 0
        compose.onAllNodesWithText("Archived", substring = true).fetchSemanticsNodes().size shouldBe 0
        runBlocking { services.accounts.lists() }.archived shouldBe emptyList()
    }

    @Test
    fun `the Add button and a row tap reach the caller with the account's id`() {
        show()
        val id = runBlocking { services.accounts.all() }.single().meta.id

        compose.onNodeWithContentDescription("Add account").performClick()
        compose.onNodeWithText("HDFC Savings").performClick()

        added.size shouldBe 1
        edited shouldBe listOf(id)
    }

    @Test
    fun `a not found flag shows the no longer exists message once, then asks the caller to clear it`() {
        show()

        notFound = true
        awaitText("That account no longer exists")
        notFoundShown shouldBe 0

        // The message times out; only then is the flag cleared, so it is not shown twice.
        compose.mainClock.advanceTimeBy(11_000)
        awaitMain { notFoundShown == 1 }

        compose.onAllNodesWithText("That account no longer exists").fetchSemanticsNodes().size shouldBe 0
        notFound shouldBe false
    }
}
