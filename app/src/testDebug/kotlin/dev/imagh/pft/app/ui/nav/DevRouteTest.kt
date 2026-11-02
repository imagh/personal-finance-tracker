package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
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
 * In debug builds Home has a "Design catalogue" header action that opens the catalogue as a
 * navigation route; system back returns to Home. (Release has neither: `ReleaseHasNoCatalogueTest`.)
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class DevRouteTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private var activity: ComponentActivity? = null

    @After
    fun closeDatabase() = database.close()

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    @Test
    fun `the catalogue class exists in debug, the positive control for the release absence test`() {
        // Twin of ReleaseHasNoCatalogueTest, which asserts this same name is absent from release. If
        // DesignCatalog.kt is renamed, update both literals, or the release check would pass vacuously.
        Class.forName("dev.imagh.pft.app.DesignCatalogKt").simpleName shouldBe "DesignCatalogKt"
    }

    @Test
    fun `the Design catalogue action on Home opens the catalogue, and back returns to Home`() {
        runBlocking { RoomTestKit(database).initialise() }
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        awaitText("Akash · Pixel")

        compose.onNodeWithContentDescription("Design catalogue").performClick()
        awaitText("Type scale: display, headline")
        // The catalogue's toggle chips are at the top of the page (the sheet below them scrolls).
        compose.onNodeWithText("Bundled serif").assertIsDisplayed()

        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        awaitText("Akash · Pixel")

        compose.onNodeWithText("Akash · Pixel").assertIsDisplayed()
        compose.onAllNodesWithText("Type scale: display, headline").fetchSemanticsNodes().size shouldBe 0
        checkNotNull(activity).isFinishing shouldBe false
    }
}
