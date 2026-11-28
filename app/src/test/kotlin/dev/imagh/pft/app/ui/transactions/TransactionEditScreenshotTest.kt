package dev.imagh.pft.app.ui.transactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.transactions.EntryFixtures.card
import dev.imagh.pft.app.ui.transactions.EntryFixtures.groceries
import dev.imagh.pft.app.ui.transactions.EntryFixtures.hdfc
import dev.imagh.pft.app.ui.transactions.EntryFixtures.names
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.transaction.TransactionType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

/**
 * Golden screenshots of the transaction form (slice C C4b) on a tall 393x1300dp xxhdpi screen with the committed
 * default scheme (dynamic colour off): a filled expense in light and dark, a transfer between two accounts, an
 * empty new form with its errors, and an income whose category was just cleared by switching type. Hand-built
 * state, no database. Fake data only.
 *
 * Record with `./gradlew :app:recordRoborazziDebug --tests '*TransactionEditScreenshotTest'`; verify runs as part
 * of `check`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h1300dp-xxhdpi")
class TransactionEditScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val none =
        TransactionEditActions({}, { _, _ -> }, {}, TransactionPickActions({}, {}, {}, {}), {})

    private val base =
        TransactionEditUiState(
            loaded = true,
            typeOptions = listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.TRANSFER),
            date = LocalDate.of(2026, 11, 15),
            names = names,
        )

    private val expense =
        base.copy(
            amount = FieldState("486.00"),
            accountId = hdfc,
            title = FieldState("Big Basket"),
            categoryId = groceries,
            tagIds = setOf(TagId("t-b"), TagId("t-c")),
            description = FieldState("Weekly shop"),
            bankRef = FieldState("UTR4821"),
        )

    private fun capture(
        name: String,
        state: TransactionEditUiState,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(Modifier.fillMaxSize().testTag("screen"), color = PftTheme.surfaces.canvas) {
                    TransactionEditScreen(state, none, onBack = {})
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("screen").captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun expenseLight() = capture("transaction-edit-expense-light", expense)

    @Test
    @Config(qualifiers = "+night")
    fun expenseDark() = capture("transaction-edit-expense-dark", expense)

    @Test
    @Config(qualifiers = "+notnight")
    fun transferLight() =
        capture(
            "transaction-edit-transfer-light",
            base.copy(
                type = TransactionType.TRANSFER,
                amount = FieldState("5,000.00"),
                accountId = hdfc,
                toAccountId = card,
                title = FieldState("Card bill"),
            ),
        )

    @Test
    @Config(qualifiers = "+notnight")
    fun errorsLight() =
        capture(
            "transaction-edit-errors-light",
            base.copy(
                amount = FieldState("", FieldError.Required),
                accountError = FieldError.Required,
                title = FieldState("x", FieldError.TooLong(200)),
                saveFailed = true,
            ),
        )

    @Test
    @Config(qualifiers = "+notnight")
    fun categoryClearedLight() =
        capture(
            "transaction-edit-category-cleared-light",
            expense.copy(type = TransactionType.INCOME, categoryId = null, categoryCleared = true),
        )
}
