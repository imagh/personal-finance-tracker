package dev.imagh.pft.app.ui.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.HeaderTags
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.AccountType
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [AccountEditScreen] drawn from fake state (plan B2b): its fields and copy, how the type changes the
 * amount's label, the error under each field, Save and its disabled states, and that nothing is drawn
 * before an edit has loaded. The state is held here and updated by the screen's actions, so the screen
 * is exercised the way a ViewModel drives it.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountEditScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var state by mutableStateOf(AccountEditUiState())
    private var saves by mutableIntStateOf(0)
    private var backs by mutableIntStateOf(0)

    private fun show(initial: AccountEditUiState = AccountEditUiState()) {
        state = initial
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                AccountEditScreen(
                    state = state,
                    actions =
                        AccountEditActions(
                            onNameChange = { state = state.copy(name = state.name.edit(it)) },
                            onTypeChange = { state = state.copy(type = it) },
                            onLastFourChange = { state = state.copy(lastFour = state.lastFour.edit(it)) },
                            onIssuerChange = { state = state.copy(issuer = state.issuer.edit(it)) },
                            onAmountChange = { state = state.copy(amount = state.amount.edit(it)) },
                            save = { saves++ },
                        ),
                    onBack = { backs++ },
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun `a new account form has its title, every field and a Save button`() {
        show()

        compose.onNodeWithTag(HeaderTags.TITLE_LARGE).assertTextEquals("New account")
        compose.onNodeWithText("Name").assertIsDisplayed()
        compose.onNodeWithText("Last 4 digits (optional)").assertIsDisplayed()
        compose.onNodeWithText("Issuer (optional)").assertIsDisplayed()
        compose.onNodeWithText("Current balance").assertIsDisplayed()
        compose.onNodeWithText("Save").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun `an edit form is titled Edit account`() {
        show(AccountEditUiState(editing = true, name = FieldState("HDFC Savings")))

        compose.onNodeWithTag(HeaderTags.TITLE_LARGE).assertTextEquals("Edit account")
        compose.onNodeWithText("HDFC Savings").assertIsDisplayed()
    }

    @Test
    fun `all four types are offered, the current one is selected, and Bank is the default`() {
        show()

        listOf("Bank", "Credit card", "Cash", "Wallet").forEach { compose.onNodeWithText(it).assertIsDisplayed() }
        compose.onNodeWithText("Bank").assertIsSelected()
        compose.onNodeWithText("Credit card").assertIsNotSelected()
        compose.onNodeWithText("Cash").assertIsNotSelected()
        compose.onNodeWithText("Wallet").assertIsNotSelected()
    }

    @Test
    fun `choosing a type selects it and the amount label follows it`() {
        show()
        compose.onAllNodesWithText("Amount owed").assertCountEquals(0)

        compose.onNodeWithText("Credit card").performClick()
        compose.waitForIdle()

        state.type shouldBe AccountType.CREDIT_CARD
        compose.onNodeWithText("Credit card").assertIsSelected()
        compose.onNodeWithText("Bank").assertIsNotSelected()
        compose.onNodeWithText("Amount owed").assertIsDisplayed()
        compose.onAllNodesWithText("Current balance").assertCountEquals(0)

        compose.onNodeWithText("Wallet").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("Current balance").assertIsDisplayed()
        compose.onAllNodesWithText("Amount owed").assertCountEquals(0)
    }

    @Test
    fun `typing in each field reports the text as typed`() {
        show()

        compose.onNodeWithText("Name").performTextInput("  HDFC ")
        compose.onNodeWithText("Last 4 digits (optional)").performTextInput("12a4")
        compose.onNodeWithText("Issuer (optional)").performTextInput("HDFC")
        compose.onNodeWithText("Current balance").performTextInput("-5,000")
        compose.waitForIdle()

        state.name.text shouldBe "  HDFC "
        state.lastFour.text shouldBe "12a4"
        state.issuer.text shouldBe "HDFC"
        state.amount.text shouldBe "-5,000"
    }

    @Test
    fun `each field shows its own error under it`() {
        show(
            AccountEditUiState(
                name = FieldState("", FieldError.Required),
                lastFour = FieldState("12", FieldError.InvalidLastFour),
                issuer = FieldState("x", FieldError.TooLong(100)),
                amount = FieldState("abc", FieldError.InvalidAmount),
            ),
        )

        compose.onNodeWithText("Required").assertIsDisplayed()
        compose.onNodeWithText("Enter exactly 4 digits").assertIsDisplayed()
        compose.onNodeWithText("At most 100 characters").assertIsDisplayed()
        compose.onNodeWithText("Enter a valid amount").assertIsDisplayed()
    }

    @Test
    fun `Save calls save once`() {
        show()

        compose.onNodeWithText("Save").performClick()

        saves shouldBe 1
    }

    @Test
    fun `Save is disabled while saving`() {
        show(AccountEditUiState(saving = true))

        compose.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `Back is offered and calls back`() {
        show()

        compose.onNodeWithContentDescription("Back").performClick()

        backs shouldBe 1
    }

    @Test
    fun `an edit that has not loaded draws no fields and cannot be saved`() {
        show(AccountEditUiState(editing = true, loaded = false))

        compose.onAllNodesWithText("Name").assertCountEquals(0)
        compose.onAllNodesWithText("Current balance").assertCountEquals(0)
        compose.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `an account that was not found draws no fields and cannot be saved`() {
        show(AccountEditUiState(editing = true, notFound = true))

        compose.onAllNodesWithText("Name").assertCountEquals(0)
        compose.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun `after a failed save a polite try-again message shows, and not before`() {
        show()
        compose.onAllNodesWithText("Couldn't save. Try again.").assertCountEquals(0)

        state = state.copy(saveFailed = true)
        compose.waitForIdle()

        val node = compose.onNodeWithText("Couldn't save. Try again.")
        node.assertIsDisplayed()
        node.fetchSemanticsNode().config.getOrNull(SemanticsProperties.LiveRegion) shouldBe LiveRegionMode.Polite
    }
}
