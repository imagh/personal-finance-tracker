package dev.imagh.pft.app.ui.more

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** The More screen (plan D9): a titled list with one row today, Accounts, and a labelled Back. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class MoreScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val taps = mutableListOf<String>()

    private fun show() =
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                MoreScreen(onBack = { taps += "back" }, onOpenAccounts = { taps += "accounts" })
            }
        }

    @Test
    fun `it shows the More title and an Accounts row that opens accounts`() {
        show()

        compose.onAllNodesWithText("More").fetchSemanticsNodes().isNotEmpty() shouldBe true
        compose.onNodeWithText("Accounts").assertIsDisplayed().performClick()

        taps shouldBe listOf("accounts")
    }

    @Test
    fun `Back is a labelled header button`() {
        show()

        compose.onNodeWithContentDescription("Back").assertIsDisplayed().performClick()

        taps shouldBe listOf("back")
    }
}
