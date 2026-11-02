package dev.imagh.pft.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.SettingKeys
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [PftRoot] maps the launch state to what is drawn, through the real [AppContainer] over a real
 * Room database: nothing while opening, the fail-closed screen for each fail-closed cause, and for a
 * ready database the navigation host's first screen (Setup on a fresh install, Home otherwise). The
 * whole first-run flow is `SetupFlowTest`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PftRootTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()

    @After
    fun closeDatabase() = database.close()

    private fun show(container: AppContainer) =
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { PftRoot(container) }
        }

    // awaitMain, not compose.waitUntil: Home's ViewModel runs on the main dispatcher (A5e learning).
    private fun waitForText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    @Test
    fun `an inconsistent identity shows the fail-closed screen with its code`() {
        val kit = RoomTestKit(database)
        runBlocking {
            kit.settingsRepository.put(AppSetting(SettingKeys.CURRENT_DEVICE_ID, "no-such-device", kit.clock.instant()))
        }

        show(AppContainer({ DatabaseStartup.Ready(database) }))
        waitForText("Code: IDENTITY_CORRUPT")

        compose.onNodeWithText("Your data can't be opened").assertIsDisplayed()
        compose.onNode(hasText("who uses this phone", substring = true)).assertIsDisplayed()
    }

    @Test
    fun `a ready, initialised database shows Home and no fail-closed screen`() {
        runBlocking { RoomTestKit(database).initialise() }

        show(AppContainer({ DatabaseStartup.Ready(database) }))
        waitForText("Akash · Pixel")

        compose.onNodeWithText("Home").assertIsDisplayed()
        compose.onAllNodesWithText("Welcome").fetchSemanticsNodes().size shouldBe 0
        compose.onAllNodesWithText("Your data can't be opened").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `a ready, fresh database shows Setup and no fail-closed screen`() {
        show(AppContainer({ DatabaseStartup.Ready(database) }))
        waitForText("Welcome")

        compose.onNodeWithText("Set up this phone").assertIsDisplayed()
        compose.onAllNodesWithText("Your data can't be opened").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `an unavailable key shows the fail-closed screen with the reason code`() {
        show(AppContainer({ DatabaseStartup.KeyUnavailable(Reason.KEK_MISSING) }))
        waitForText("Code: KEK_MISSING")

        compose.onNodeWithText("Your data can't be opened").assertIsDisplayed()
        compose.onNode(hasText("could not get the key", substring = true)).assertIsDisplayed()
    }

    @Test
    fun `while the database is opening nothing is drawn but the canvas`() {
        val release = java.util.concurrent.CountDownLatch(1)
        try {
            show(
                AppContainer({
                    release.await(10, java.util.concurrent.TimeUnit.SECONDS)
                    DatabaseStartup.KeyUnavailable(Reason.KEK_MISSING)
                }),
            )
            compose.waitForIdle()

            compose.onAllNodesWithText("Your data can't be opened").fetchSemanticsNodes().size shouldBe 0
            compose.onRoot().printToString() shouldNotContain "Text"
        } finally {
            release.countDown()
        }
    }
}
