package dev.imagh.pft.app.ui.nav

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
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
 * Later launches go straight to Home (PRD L-8): an already initialised database opens on Home with
 * the stored identity, Setup is not shown, and nothing new is written. "Not shown" is sampled at
 * every poll while Home appears and again at the end; it cannot prove a frame was never composed.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class LaunchRoutingTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `an initialised database opens on Home with the stored identity and Setup is not shown`() {
        runBlocking { RoomTestKit(database).identity.initialise("Existing", "Old phone") }
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }

        var setupShown = false
        awaitMain {
            compose.waitForIdle()
            setupShown = setupShown || compose.onAllNodesWithText("Welcome").fetchSemanticsNodes().isNotEmpty()
            compose.onAllNodesWithText("Existing · Old phone").fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNodeWithText("Existing · Old phone").assertIsDisplayed()
        setupShown shouldBe false
        compose.onAllNodesWithText("Welcome").fetchSemanticsNodes().size shouldBe 0
        database.rowCount("user_profile") shouldBe 1
        database.rowCount("audit_entry") shouldBe 3
    }
}
