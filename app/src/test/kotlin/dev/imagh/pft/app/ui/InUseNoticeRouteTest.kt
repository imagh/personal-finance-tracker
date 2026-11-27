package dev.imagh.pft.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.accounts.AccountsNavigation
import dev.imagh.pft.app.ui.accounts.AccountsRoute
import dev.imagh.pft.app.ui.categories.CategoriesNavigation
import dev.imagh.pft.app.ui.categories.CategoriesRoute
import dev.imagh.pft.app.ui.tags.TagsNavigation
import dev.imagh.pft.app.ui.tags.TagsRoute
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
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
import java.time.Instant

/**
 * The in-use refusal reaches the user through each list route (slice C C1d, D4): deleting an account, a
 * category or a tag that a transaction uses shows the one fixed message, keeps the row and leaves it
 * undeletable. One transaction uses all three.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class InUseNoticeRouteTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val services = AppServices(database, EpochMillis.clock())

    private val owner =
        object : LifecycleOwner {
            val registry = LifecycleRegistry(this).also { it.currentState = Lifecycle.State.RESUMED }
            override val lifecycle: Lifecycle get() = registry
        }

    @After
    fun closeDatabase() = database.close()

    /** One account, one expense category and one tag, all used by one transaction. */
    private fun seed() =
        runBlocking {
            services.identity.initialise("Akash", "Pixel")
            val account = services.accounts.create(AccountDraft("Cash", AccountType.CASH, Paise(0)))
            val food =
                services.categories.create(null, CategoryKind.EXPENSE, CategoryDraft("Food", CategoryColour.AMBER))
            val trip = services.tags.create("Trip")
            services.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(1_000),
                    Instant.parse("2026-11-20T10:00:00Z"),
                    account.meta.id,
                    lines = listOf(LineDraft(null, Paise(1_000), food.meta.id, setOf(trip.meta.id))),
                ),
            )
        }

    private fun show(content: @Composable () -> Unit) {
        seed()
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                PftTheme(ThemePrefs(dynamicColor = false)) { content() }
            }
        }
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun deleteRow(name: String) {
        awaitText(name)
        compose.onNodeWithContentDescription("Actions for $name").performClick()
        compose.onNodeWithText("Delete").performClick()
    }

    private fun checkRefusal(name: String) {
        awaitText("In use by transactions. Archive it instead.")
        compose.onNodeWithText(name).assertIsDisplayed()
        // No undo offer: nothing was deleted.
        compose.onAllNodesWithText("deleted", substring = true).fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `deleting an account that a transaction uses shows the in-use message and keeps the row`() {
        show {
            AccountsRoute(
                services,
                AccountsNavigation(add = {}, edit = {}),
                notFound = false,
                onNotFoundShown = {},
            )
        }

        deleteRow("Cash")

        checkRefusal("Cash")
        runBlocking { services.accounts.all() }.size shouldBe 1
    }

    @Test
    fun `deleting a category that a transaction uses shows the in-use message and keeps the row`() {
        show {
            CategoriesRoute(
                services,
                CategoriesNavigation(back = {}, add = {}, edit = {}),
                notFound = false,
                onNotFoundShown = {},
            )
        }

        deleteRow("Food")

        checkRefusal("Food")
        runBlocking { services.categories.lists(CategoryKind.EXPENSE) }.active.size shouldBe 1
    }

    @Test
    fun `deleting a tag that a transaction carries shows the in-use message and keeps the row`() {
        show {
            TagsRoute(
                services,
                TagsNavigation(back = {}, add = {}, edit = {}),
                notFound = false,
                onNotFoundShown = {},
            )
        }

        deleteRow("Trip")

        checkRefusal("Trip")
        runBlocking { services.tags.lists() }.active.size shouldBe 1
    }
}
