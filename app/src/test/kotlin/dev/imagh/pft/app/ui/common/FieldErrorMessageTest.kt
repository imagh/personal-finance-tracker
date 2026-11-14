package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `FieldError.message()`: the one place a field error becomes the text the user reads (plan B1b),
 * shared by every field block. Each error kind has its own string.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class FieldErrorMessageTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var error by mutableStateOf<FieldError>(FieldError.Required)

    private fun show(shown: FieldError) {
        error = shown
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) { Text(error.message()) }
        }
        compose.waitForIdle()
    }

    @Test
    fun `Required reads Required`() {
        show(FieldError.Required)
        compose.onNodeWithText("Required").assertIsDisplayed()
    }

    @Test
    fun `TooLong names its limit`() {
        show(FieldError.TooLong(100))
        compose.onNodeWithText("At most 100 characters").assertIsDisplayed()
    }

    @Test
    fun `InvalidLastFour asks for exactly four digits`() {
        show(FieldError.InvalidLastFour)
        compose.onNodeWithText("Enter exactly 4 digits").assertIsDisplayed()
    }
}
