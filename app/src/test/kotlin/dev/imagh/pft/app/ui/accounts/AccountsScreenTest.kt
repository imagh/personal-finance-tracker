package dev.imagh.pft.app.ui.accounts

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [AccountsScreen] drawn from fake state: it must not say "No accounts yet" before the first read has
 * finished (`loaded` false), only once the list is known to be empty.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountsScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private fun show(state: AccountsUiState) =
        compose.setContent {
            val snackbars = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                AccountsScreen(
                    state = state,
                    actions = AccountsActions(archive = {}, unarchive = {}, delete = {}),
                    snackbars = snackbars,
                    onBack = {},
                )
            }
        }

    @Test
    fun `before the first load the empty message does not show`() {
        show(AccountsUiState(loaded = false))

        compose.onAllNodesWithText("No accounts yet").assertCountEquals(0)
    }

    @Test
    fun `once loaded with no accounts the empty message shows`() {
        show(AccountsUiState(loaded = true))

        compose.onNodeWithText("No accounts yet").assertIsDisplayed()
    }
}
