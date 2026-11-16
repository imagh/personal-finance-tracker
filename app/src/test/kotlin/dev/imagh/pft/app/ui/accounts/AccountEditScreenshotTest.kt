package dev.imagh.pft.app.ui.accounts

import androidx.compose.material3.Surface
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.AccountType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden screenshots of the account form (plan B2b): a new account (empty), a new account with a
 * missing name and an invalid amount (light only), and an existing credit card ("Amount owed"), in light
 * and dark, on a 393x851dp xxhdpi screen with the committed Graphite colours (dynamic colour off, so the goldens are
 * deterministic). Fake data only: "Axis Magnus", the issuer "Axis" and the digits "4321" are made up.
 *
 * `./gradlew check` verifies against `src/test/screenshots`; after a deliberate visual change run
 * `./gradlew :app:recordRoborazziDebug` and review the PNG diff. Pixels catch visual regressions;
 * `AccountEditScreenTest` carries correctness.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class AccountEditScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val errors =
        AccountEditUiState(
            name = FieldState("", FieldError.Required),
            lastFour = FieldState("1234"),
            issuer = FieldState("Axis"),
            amount = FieldState("abc", FieldError.InvalidAmount),
        )

    private val creditCard =
        AccountEditUiState(
            editing = true,
            name = FieldState("Axis Magnus"),
            type = AccountType.CREDIT_CARD,
            lastFour = FieldState("4321"),
            issuer = FieldState("Axis"),
            amount = FieldState("5,000.00"),
        )

    private fun capture(
        name: String,
        state: AccountEditUiState,
    ) {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Surface(color = PftTheme.surfaces.canvas) {
                    AccountEditScreen(
                        state,
                        AccountEditActions({}, {}, {}, {}, {}, save = {}),
                        onBack = {},
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun emptyLight() = capture("account-edit-empty-light", AccountEditUiState())

    @Test
    @Config(qualifiers = "+night")
    fun emptyDark() = capture("account-edit-empty-dark", AccountEditUiState())

    @Test
    @Config(qualifiers = "+notnight")
    fun errorsLight() = capture("account-edit-errors-light", errors)

    @Test
    @Config(qualifiers = "+notnight")
    fun creditCardLight() = capture("account-edit-credit-card-light", creditCard)

    @Test
    @Config(qualifiers = "+night")
    fun creditCardDark() = capture("account-edit-credit-card-dark", creditCard)
}
