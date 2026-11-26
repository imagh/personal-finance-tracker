package dev.imagh.pft.app.ui.accounts

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Instant

/**
 * [AccountsScreen] drawn from fake state: it must not say "No accounts yet" before the first read has
 * finished (`loaded` false), only once the list is known to be empty; the add button opens the create
 * form and tapping a row opens that account's edit form (B2b).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountsScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var adds = 0
    private val edited = mutableListOf<AccountId>()

    private fun show(state: AccountsUiState) =
        compose.setContent {
            val snackbars = remember { SnackbarHostState() }
            PftTheme(ThemePrefs(dynamicColor = false)) {
                AccountsScreen(
                    state = state,
                    actions =
                        AccountsActions(
                            archive = {},
                            unarchive = {},
                            delete = {},
                            add = { adds++ },
                            edit = { edited += it },
                        ),
                    snackbars = snackbars,
                )
            }
        }

    @Test
    fun `the accounts tab root has no Back button`() {
        show(AccountsUiState(loaded = true))

        compose.onAllNodesWithContentDescription("Back").assertCountEquals(0)
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

    private fun account(
        id: String,
        name: String,
    ) = Account(
        meta = EntityMeta(AccountId(id), Instant.EPOCH, Instant.EPOCH, UserId("u1"), DeviceId("d1")),
        name = name,
        type = AccountType.BANK,
        openingBalance = Paise(0),
        maskedIdentifier = null,
        issuer = null,
    )

    @Test
    fun `the add button is there before the first load and opens the create form`() {
        show(AccountsUiState(loaded = false))

        compose.onNodeWithContentDescription("Add account").performClick()

        adds shouldBe 1
    }

    @Test
    fun `the empty state offers an Add account button`() {
        show(AccountsUiState(loaded = true))

        compose.onNodeWithText("Add account").performClick()

        adds shouldBe 1
    }

    @Test
    fun `tapping a row opens that account for editing, active or archived`() {
        show(
            AccountsUiState(
                active = listOf(account("a1", "HDFC Savings")),
                archived = listOf(account("a2", "Old")),
                loaded = true,
            ),
        )

        compose.onNodeWithText("HDFC Savings").performClick()
        compose.onNodeWithText("Archived (1)").performClick()
        compose.onNodeWithText("Old").performClick()

        edited shouldBe listOf(AccountId("a1"), AccountId("a2"))
    }
}
