package dev.imagh.pft.app.ui.nav

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
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
import dev.imagh.pft.ledger.settings.SettingKeys
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
 * The M1a acceptance test for first-run setup (PRD L-8): the real [AppContainer] over a real Room
 * database, rendered through [PftRoot] (launch state, navigation host, Setup, Home). It asserts what
 * is persisted and what the user sees, not that anything was called.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class SetupFlowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private var activity: ComponentActivity? = null

    @After
    fun closeDatabase() = database.close()

    private fun show() {
        compose.setContent {
            activity = LocalContext.current as ComponentActivity
            PftTheme(ThemePrefs(dynamicColor = false)) {
                PftRoot(AppContainer({ DatabaseStartup.Ready(database) }))
            }
        }
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText("Welcome").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun typeName(
        name: String,
        phone: String,
    ) {
        compose.onNode(hasSetTextAction() and hasText("Your name")).performTextInput(name)
        compose.onNode(hasSetTextAction() and hasText("This phone's name")).performTextReplacement(phone)
    }

    private fun tapContinue() {
        compose.onNodeWithText("Continue").performClick()
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun currentDeviceId(): String? =
        database.openHelper.readableDatabase
            .query("SELECT value FROM app_setting WHERE `key` = ?", arrayOf(SettingKeys.CURRENT_DEVICE_ID))
            .use { if (it.moveToFirst()) it.getString(0) else null }

    @Test
    fun `a fresh install shows Setup, and Continue saves the identity and lands on Home`() {
        show()
        compose.onNodeWithText("Set up this phone").assertIsDisplayed()

        typeName("Akash", "Pixel 8")
        tapContinue()
        awaitText("Akash · Pixel 8")

        compose.onNodeWithText("Akash · Pixel 8").assertIsDisplayed()
        compose.onAllNodesWithText("Welcome").fetchSemanticsNodes().size shouldBe 0
        database.rowCount("user_profile") shouldBe 1
        database.rowCount("device") shouldBe 1
        database.rowCount("audit_entry") shouldBe 3
        val deviceId = checkNotNull(currentDeviceId()) { "current_device_id was not saved" }
        val identity = runBlocking { RoomTestKit(database).identity.currentIdentity() }
        identity?.device?.id?.value shouldBe deviceId
        identity?.user?.displayName shouldBe "Akash"
        identity?.device?.label shouldBe "Pixel 8"
    }

    @Test
    fun `system back on Home finishes the activity, because Setup is not on the back stack`() {
        show()
        typeName("Akash", "Pixel 8")
        tapContinue()
        awaitText("Akash · Pixel 8")

        compose.runOnUiThread { checkNotNull(activity).onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()

        checkNotNull(activity).isFinishing shouldBe true
        compose.onAllNodesWithText("Welcome").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `a blank name stays on Setup, shows Required and saves nothing`() {
        show()

        tapContinue()
        awaitText("Required")

        compose.onNodeWithText("Welcome").assertIsDisplayed()
        database.rowCount("user_profile") shouldBe 0
        database.rowCount("audit_entry") shouldBe 0
    }

    @Test
    fun `when an identity appears while Setup is open, Home shows the existing one, not what was typed`() {
        show()
        // Another path created the identity after Setup was composed (the AlreadyInitialised race).
        runBlocking { RoomTestKit(database).identity.initialise("Existing", "Old phone") }

        typeName("Typed", "Typed phone")
        tapContinue()
        awaitText("Existing · Old phone")

        compose.onAllNodesWithText("Typed", substring = true).fetchSemanticsNodes().size shouldBe 0
        database.rowCount("user_profile") shouldBe 1
        database.rowCount("device") shouldBe 1
        database.rowCount("audit_entry") shouldBe 3
    }
}
