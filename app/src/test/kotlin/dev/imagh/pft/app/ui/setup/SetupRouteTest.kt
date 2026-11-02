package dev.imagh.pft.app.ui.setup

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
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

/**
 * [SetupRoute] end to end over a real Room database: the screen, its ViewModel and the identity
 * service. Typing a name and tapping Continue saves the identity and calls `onDone`; a blank name
 * saves nothing and shows Required.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class SetupRouteTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val services = AppServices(database, EpochMillis.clock())
    private var done by mutableIntStateOf(0)

    @After
    fun tearDown() = database.close()

    private fun show() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { SetupRoute(services, onDone = { done++ }) }
        }
        compose.waitForIdle()
    }

    @Test
    fun `a name and Continue save the identity with the device model, then call onDone`() {
        show()

        compose.onNode(hasSetTextAction() and hasText("Your name")).performTextInput("Akash")
        compose.onNodeWithText("Continue").performClick()
        awaitMain {
            compose.waitForIdle()
            done == 1
        }
        compose.waitForIdle()

        val identity = runBlocking { services.identity.currentIdentity() }
        identity?.user?.displayName shouldBe "Akash"
        identity?.device?.label shouldBe Build.MODEL.trim()
        done shouldBe 1
    }

    @Test
    fun `a blank name shows Required, saves nothing and does not call onDone`() {
        show()

        compose.onNodeWithText("Continue").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("Required").assertIsDisplayed()
        runBlocking { services.identity.currentIdentity() } shouldBe null
        done shouldBe 0
    }
}
