package dev.imagh.pft.app.ui.startup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.string.shouldNotContain
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The fail-closed screen shown when the database key cannot be produced: a plain explanation and the
 * reason code, and nothing else. It never shows paths, key material or the underlying exception.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class KeyUnavailableScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun show(reason: Reason) =
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { KeyUnavailableScreen(reason) } }

    @Test
    fun `it says the data is safe and shows the reason code`() {
        show(Reason.KEK_MISSING)

        compose.onNodeWithText("Your data can't be opened").assertIsDisplayed()
        compose.onNodeWithText("Code: KEK_MISSING").assertIsDisplayed()
        compose.onNode(hasText("has not been changed or deleted", substring = true)).assertIsDisplayed()
    }

    @Test
    fun `it warns against clearing storage or uninstalling`() {
        show(Reason.KEY_FILE_MISSING)

        compose
            .onNode(hasText("clear the app's storage or uninstall it", substring = true, ignoreCase = true))
            .assertIsDisplayed()
    }

    @Test
    fun `it shows no path, key or exception detail`() {
        show(Reason.KEY_FILE_AUTH_FAILED)

        val shown = compose.onRoot().printToString()
        shown shouldNotContain "/data/"
        shown shouldNotContain ".bin"
        shown shouldNotContain "pft_db"
        shown shouldNotContain "Exception"
    }

    @Test
    fun `the code is the reason's own name`() {
        show(Reason.KEY_FILE_AUTH_FAILED)

        compose.onNodeWithText("Code: KEY_FILE_AUTH_FAILED").assertIsDisplayed()
    }
}
