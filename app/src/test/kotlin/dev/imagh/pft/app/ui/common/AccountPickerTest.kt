package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.money.Paise
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
 * [AccountPicker] fed by the real [dev.imagh.pft.ledger.account.AccountService.active] over in-memory
 * Room (the consumer's ViewModel does the same): which accounts are offered and in what order, the
 * type badge of each, what a tap hands to `onPick`, the selected row and the empty state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountPickerTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)

    private var open by mutableStateOf(true)
    private val picked = mutableListOf<AccountId>()
    private var dismissals = 0

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun create(
        name: String,
        type: AccountType,
        issuer: String? = null,
        lastFour: String? = null,
    ): Account = blocking { kit.accounts.create(AccountDraft(name, type, Paise(0), lastFour, issuer)) }

    /**
     * Seeds six accounts whose names are not in alphabetical order and mix cases, archives one and
     * deletes one, and returns what the consumer's ViewModel would hand the picker: `active()`.
     */
    private fun seedAndLoadActive(): List<Account> {
        blocking { kit.initialise() }
        create("Wallet", AccountType.WALLET)
        create("HDFC Savings", AccountType.BANK, issuer = "HDFC", lastFour = "1234")
        create("axis Card", AccountType.CREDIT_CARD)
        create("Cash", AccountType.CASH)
        val archived = create("Old Bank", AccountType.BANK)
        val deleted = create("Gone Card", AccountType.CREDIT_CARD)
        blocking {
            kit.accounts.archive(archived.meta.id)
            kit.accounts.delete(deleted.meta.id)
        }
        return blocking { kit.accounts.active() }
    }

    private fun show(
        accounts: List<Account>,
        selected: AccountId? = null,
    ) = compose.setContent {
        PftTheme(ThemePrefs(dynamicColor = false)) {
            if (open) {
                AccountPicker(
                    accounts = accounts,
                    selected = selected,
                    onPick = { picked += it },
                    onDismiss = {
                        dismissals++
                        open = false
                    },
                )
            }
        }
    }

    /** The name of each row, top to bottom. */
    private fun rowNames(): List<String> =
        compose
            .onAllNodes(isSelectable())
            .fetchSemanticsNodes()
            .map { it.config[SemanticsProperties.Text].first().text }

    /** The spoken label of each type badge, top to bottom (the badge is its own node, so read the unmerged tree). */
    private fun badgeLabels(): List<String> {
        val labels = setOf("Bank", "Credit card", "Cash", "Wallet")
        val isTypeBadge =
            SemanticsMatcher("is a type badge") { node ->
                node.config.getOrNull(SemanticsProperties.ContentDescription)?.firstOrNull() in labels
            }
        return compose
            .onAllNodes(isTypeBadge, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .map { it.config[SemanticsProperties.ContentDescription].first() }
    }

    @Test
    fun `lists only the active accounts, sorted by name ignoring case, each with its type badge`() {
        show(seedAndLoadActive())

        rowNames() shouldBe listOf("axis Card", "Cash", "HDFC Savings", "Wallet")
        badgeLabels() shouldBe listOf("Credit card", "Cash", "Bank", "Wallet")
    }

    @Test
    fun `the rows are one radio group, so TalkBack presents them as a set of choices`() {
        show(seedAndLoadActive())

        compose
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .onChildren()
            .filter(isSelectable())
            .assertCountEquals(4)
    }

    @Test
    fun `a row also shows the issuer and last four digits that tell similar accounts apart`() {
        show(seedAndLoadActive())

        compose.onNodeWithText("Bank · HDFC · ••1234").assertIsDisplayed()
    }

    @Test
    fun `tapping an account hands its id to onPick and dismisses the sheet`() {
        val active = seedAndLoadActive()
        val cash = active.single { it.name == "Cash" }
        show(active)

        compose.onNodeWithText("Cash").performClick()
        compose.waitForIdle()

        picked shouldBe listOf(cash.meta.id)
        compose.onNodeWithText("Cash").assertDoesNotExist()
        dismissals shouldBe 1
    }

    @Test
    fun `a second tap while the sheet is closing picks nothing more`() {
        val active = seedAndLoadActive()
        show(active)

        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Cash").performClick()
        compose.onNodeWithText("Wallet").performClick()
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()

        picked shouldBe listOf(active.single { it.name == "Cash" }.meta.id)
        dismissals shouldBe 1
    }

    @Test
    fun `Back closes the picker without picking`() {
        show(seedAndLoadActive())

        Espresso.pressBack()
        compose.waitForIdle()

        compose.onNodeWithText("Cash").assertDoesNotExist()
        picked shouldBe emptyList()
        dismissals shouldBe 1
    }

    @Test
    fun `the current selection is marked selected and the others are not`() {
        val active = seedAndLoadActive()
        show(active, selected = active.single { it.name == "HDFC Savings" }.meta.id)

        compose.onNodeWithText("HDFC Savings").assertIsSelected()
        compose.onNodeWithText("axis Card").assertIsNotSelected()
        compose.onNodeWithText("Cash").assertIsNotSelected()
        compose.onNodeWithText("Wallet").assertIsNotSelected()
    }

    @Test
    fun `a selection that is not among the offered accounts marks nothing`() {
        val active = seedAndLoadActive()
        val archived = blocking { kit.accounts.all() }.single { it.name == "Old Bank" }
        show(active, selected = archived.meta.id)

        compose.onNodeWithText("Cash").assertIsNotSelected()
        compose.onNodeWithText("Wallet").assertIsNotSelected()
    }

    @Test
    fun `with no accounts the sheet says so and offers nothing to pick`() {
        show(emptyList())

        compose.onNodeWithText("No accounts yet. Add one from More.").assertIsDisplayed()
        rowNames() shouldBe emptyList()
    }
}
