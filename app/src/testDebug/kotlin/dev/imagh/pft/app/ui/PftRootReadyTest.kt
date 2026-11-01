package dev.imagh.pft.app.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
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
 * A ready, initialised database reaches the app content. Until the navigation host arrives (A5f)
 * that content is the debug design catalogue, so this lives in the debug tests: the release twin of
 * `DevContent` is an empty surface.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PftRootReadyTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `an initialised database shows the app content and no fail-closed screen`() {
        runBlocking { RoomTestKit(database).initialise() }

        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { PftRoot(AppContainer({ DatabaseStartup.Ready(database) })) }
        }
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText("Type scale: display, headline").fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNode(hasText("Type scale: display, headline")).assertExists()
        compose.onAllNodesWithText("Your data can't be opened").fetchSemanticsNodes().size shouldBe 0
    }
}
