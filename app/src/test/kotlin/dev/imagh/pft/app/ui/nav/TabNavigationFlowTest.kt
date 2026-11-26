package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.PftRoot
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
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
 * The tab bar and the nested graphs end to end (Cobalt plan D8, acceptance 5): real in-memory Room,
 * through [PftRoot] and [PftNavHost]. The bar shows on the three tab roots and nowhere else; Back on a
 * tab root goes to Home and Back on Home leaves the app; each tab keeps its own stack across tab
 * switches. The bar is hidden on every screen where a tab switch could strand a stack, so the cases that
 * need a switch from inside a stack (form restore, pop to root) drive [selectTab] on the host's own
 * controller, exactly what a tap on the bar would call.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TabNavigationFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private var activity: ComponentActivity? = null
    private var controller: NavHostController? = null

    @After
    fun closeDatabase() = database.close()

    /** The whole app over the database, through [PftRoot], waiting for [ready] to be on screen. */
    private fun showApp(ready: String) {
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        awaitText(ready)
    }

    /** The navigation host alone, with a controller the test can see, on an already initialised database. */
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
                )
            }
        }
        awaitText(HOME_PLACEHOLDER)
    }

    private fun initialised() = runBlocking { kit.initialise() }

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

    private fun systemBack() {
        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
    }

    private fun selectTab(tab: Tab) {
        compose.runOnUiThread { checkNotNull(controller).selectTab(tab) }
        compose.waitForIdle()
    }

    private fun currentRoute(): String? {
        var route: String? = null
        compose.runOnUiThread { route = checkNotNull(controller).currentDestination?.route }
        return route
    }

    // --- The bar: where it shows

    @Test
    fun `the bar shows on Home, Accounts and More, each time with that tab selected`() {
        initialised()
        showApp(HOME_PLACEHOLDER)

        compose.tabCount() shouldBe 3
        compose.selectedTabs() shouldBe listOf("Home")

        compose.tab("Accounts").performClick()
        awaitText("No accounts yet")
        compose.tabCount() shouldBe 3
        compose.selectedTabs() shouldBe listOf("Accounts")

        compose.tab("More").performClick()
        awaitText("Categories")
        compose.tabCount() shouldBe 3
        compose.selectedTabs() shouldBe listOf("More")

        compose.tab("Home").performClick()
        awaitText(HOME_PLACEHOLDER)
        compose.selectedTabs() shouldBe listOf("Home")
    }

    @Test
    fun `the bar is hidden on the account form and comes back when the form closes`() {
        initialised()
        showApp(HOME_PLACEHOLDER)
        compose.tab("Accounts").performClick()
        awaitText("No accounts yet")

        compose.onNodeWithContentDescription("Add account").performClick()
        awaitText("New account")
        compose.tabCount() shouldBe 0

        systemBack()
        awaitText("No accounts yet")
        compose.tabCount() shouldBe 3
    }

    @Test
    fun `the bar is hidden on the lists under More, which keep their Back button`() {
        initialised()
        showApp(HOME_PLACEHOLDER)
        compose.tab("More").performClick()
        awaitText("Tags")

        compose.onNodeWithText("Tags").performClick()
        awaitText("No tags yet")

        compose.tabCount() shouldBe 0
        compose.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun `the bar is hidden on Setup and shows once Setup is done`() {
        showApp("Welcome")
        compose.tabCount() shouldBe 0

        compose.onNode(hasSetTextAction() and hasText("Your name")).performTextInput("Akash")
        compose.onNode(hasSetTextAction() and hasText("This phone's name")).performTextReplacement("Pixel 8")
        compose.onNodeWithText("Continue").performClick()
        awaitText(HOME_PLACEHOLDER)

        compose.tabCount() shouldBe 3
        compose.selectedTabs() shouldBe listOf("Home")
    }

    // --- Back

    @Test
    fun `Back on More goes to Home, and Back on Home finishes the activity`() {
        initialised()
        showApp(HOME_PLACEHOLDER)
        compose.tab("More").performClick()
        awaitText("Categories")

        systemBack()
        awaitText(HOME_PLACEHOLDER)
        compose.selectedTabs() shouldBe listOf("Home")
        checkNotNull(activity).isFinishing shouldBe false

        systemBack()
        checkNotNull(activity).isFinishing shouldBe true
    }

    @Test
    fun `going Home by its tab leaves nothing under Home, so Back then finishes the activity`() {
        initialised()
        showApp(HOME_PLACEHOLDER)
        compose.tab("Accounts").performClick()
        awaitText("No accounts yet")
        compose.tab("More").performClick()
        awaitText("Categories")
        compose.tab("Home").performClick()
        awaitText(HOME_PLACEHOLDER)

        systemBack()

        checkNotNull(activity).isFinishing shouldBe true
    }

    @Test
    fun `from Accounts the More tab and then Back goes to Home, not back to Accounts`() {
        initialised()
        showApp(HOME_PLACEHOLDER)
        compose.tab("Accounts").performClick()
        awaitText("No accounts yet")
        compose.tab("More").performClick()
        awaitText("Categories")

        systemBack()
        awaitText(HOME_PLACEHOLDER)

        compose.onAllNodesWithText("No accounts yet").fetchSemanticsNodes().size shouldBe 0
        checkNotNull(activity).isFinishing shouldBe false
    }

    // --- Each tab keeps its own stack

    @Test
    fun `an open account form is restored when the user leaves Accounts and comes back`() {
        initialised()
        showHost()
        selectTab(Tab.ACCOUNTS)
        awaitText("No accounts yet")
        compose.onNodeWithContentDescription("Add account").performClick()
        awaitText("New account")
        compose.onNode(hasSetTextAction() and hasText("Name")).performTextInput("Half typed")

        selectTab(Tab.MORE)
        awaitText("Categories")
        compose.tabCount() shouldBe 3
        compose.onAllNodesWithText("New account").fetchSemanticsNodes().size shouldBe 0

        selectTab(Tab.ACCOUNTS)
        awaitText("New account")

        compose.onNodeWithText("Half typed").assertIsDisplayed()
        compose.tabCount() shouldBe 0
        systemBack()
        awaitText("No accounts yet")
    }

    @Test
    fun `an open list under More is restored when the user leaves More and comes back`() {
        initialised()
        showHost()
        selectTab(Tab.MORE)
        awaitText("Tags")
        compose.onNodeWithText("Tags").performClick()
        awaitText("No tags yet")

        selectTab(Tab.ACCOUNTS)
        awaitText("No accounts yet")
        selectTab(Tab.MORE)

        awaitText("No tags yet")
        currentRoute() shouldBe Routes.TAGS
    }

    @Test
    fun `selecting the active tab pops it to its root`() {
        initialised()
        showHost()
        selectTab(Tab.ACCOUNTS)
        awaitText("No accounts yet")
        compose.onNodeWithContentDescription("Add account").performClick()
        awaitText("New account")

        selectTab(Tab.ACCOUNTS)

        awaitGone("New account")
        currentRoute() shouldBe Routes.ACCOUNTS
        compose.tabCount() shouldBe 3
        compose.selectedTabs() shouldBe listOf("Accounts")
    }

    @Test
    fun `selecting the active tab at its root changes nothing`() {
        initialised()
        showHost()
        selectTab(Tab.ACCOUNTS)
        awaitText("No accounts yet")

        selectTab(Tab.ACCOUNTS)

        currentRoute() shouldBe Routes.ACCOUNTS
        compose.onAllNodesWithText("No accounts yet").fetchSemanticsNodes().size shouldBe 1
    }
}
