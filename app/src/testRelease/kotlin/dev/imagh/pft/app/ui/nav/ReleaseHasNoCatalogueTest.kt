package dev.imagh.pft.app.ui.nav

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
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
import io.kotest.assertions.throwables.shouldThrow
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
 * The release build ships no developer tooling: Home has no "Design catalogue" action, and the
 * catalogue's class is not on the release classpath at all. (Runs on the release unit-test task only;
 * `DevRouteTest` is its debug counterpart.)
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class ReleaseHasNoCatalogueTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `Home has no Design catalogue action`() {
        runBlocking { RoomTestKit(database).initialise() }
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText("Akash · Pixel").fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNodeWithText("Akash · Pixel").assertIsDisplayed()
        compose.onAllNodesWithContentDescription("Design catalogue").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `the catalogue code is not part of the release build`() {
        // Twin: DevRouteTest (debug) asserts this same name resolves. Keep the two literals in step.
        shouldThrow<ClassNotFoundException> { Class.forName("dev.imagh.pft.app.DesignCatalogKt") }
    }
}
