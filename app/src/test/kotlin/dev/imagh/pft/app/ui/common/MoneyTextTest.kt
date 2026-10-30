package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.HeroSizeSteps
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import dev.imagh.pft.money.PaiseFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `MoneyText` with the real `PaiseFormat` from `:core:money` (plan §3.3, §5.2, UI-5, UI-10): the
 * exact text, the sign glyph, the spoken direction, the tone colour and the amount styles. The
 * hero's shrink-to-fit and the rolling digits are in the sections below. Native graphics gives
 * text its real width, which the shrink tests need.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class MoneyTextTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val minus = "−"
    private val tag = "money"

    private class Spec(
        val amount: Paise,
        val semantic: MoneySemantic,
        val size: MoneySize,
    )

    private var spec by mutableStateOf<Spec?>(null)
    private var started = false

    /** Shows one amount; calling it again updates the same composition (a rule allows one `setContent`). */
    private fun show(
        amount: Paise,
        semantic: MoneySemantic,
        size: MoneySize = MoneySize.Row,
    ) {
        spec = Spec(amount, semantic, size)
        if (started) return
        started = true
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                spec?.let { MoneyText(it.amount, it.semantic, Modifier.testTag(tag), size = it.size) }
            }
        }
    }

    private fun node() = compose.onNodeWithTag(tag)

    private fun SemanticsNodeInteraction.color(): Color = layout().layoutInput.style.color

    // --- text and sign ---------------------------------------------------------------------

    @Test
    fun `an expense of 12345678 paise shows exactly minus rupee 1,23,456_78`() {
        show(Paise(12_345_678), MoneySemantic.Expense)
        node().assertTextEquals("$minus₹1,23,456.78")
    }

    @Test
    fun `the minus is the true minus sign U+2212, not a hyphen`() {
        show(Paise(100), MoneySemantic.Expense)
        val text =
            node()
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text]
                .single()
                .text
        assertEquals('−', text.first())
    }

    @Test
    fun `income shows a plus sign`() {
        show(Paise(12_345_678), MoneySemantic.Income)
        node().assertTextEquals("+₹1,23,456.78")
    }

    @Test
    fun `a transfer has no sign`() {
        show(Paise(12_345_678), MoneySemantic.Transfer)
        node().assertTextEquals("₹1,23,456.78")
    }

    @Test
    fun `a positive balance has no sign and a negative balance has a minus`() {
        show(Paise(50_000), MoneySemantic.Balance)
        node().assertTextEquals("₹500.00")
        show(Paise(-50_000), MoneySemantic.Balance)
        node().assertTextEquals("$minus₹500.00")
    }

    @Test
    fun `a zero balance has no sign`() {
        show(Paise.ZERO, MoneySemantic.Balance)
        node().assertTextEquals("₹0.00")
    }

    @Test
    fun `a budget figure has no sign in any state`() {
        BudgetState.entries.forEach { state ->
            show(Paise(650_000), MoneySemantic.Budget(state))
            node().assertTextEquals("₹6,500.00")
        }
    }

    @Test
    fun `a negative expense is rejected, never shown with a wrong sign`() {
        val failure = runCatching { show(Paise(-1), MoneySemantic.Expense) }.exceptionOrNull()
        assertTrue("$failure", failure is IllegalArgumentException)
    }

    @Test
    fun `only a balance may be negative`() {
        listOf(
            MoneySemantic.Income,
            MoneySemantic.Expense,
            MoneySemantic.Transfer,
            MoneySemantic.Budget(BudgetState.OVER),
        ).forEach { semantic ->
            val failure = runCatching { moneySign(semantic, Paise(-1)) }.exceptionOrNull()
            assertTrue("$semantic: $failure", failure is IllegalArgumentException)
        }
        assertEquals(minus, moneySign(MoneySemantic.Balance, Paise(-1)))
    }

    // --- semantics (colour is never the only cue) -------------------------------------------

    @Test
    fun `the spoken description starts with the direction`() {
        show(Paise(25_000), MoneySemantic.Expense)
        node().assertContentDescriptionEquals("Expense, ₹250.00")
        show(Paise(25_000), MoneySemantic.Income)
        node().assertContentDescriptionEquals("Income, ₹250.00")
        show(Paise(25_000), MoneySemantic.Transfer)
        node().assertContentDescriptionEquals("Transfer, ₹250.00")
    }

    @Test
    fun `the spoken description of a balance says whether it is negative`() {
        show(Paise(25_000), MoneySemantic.Balance)
        node().assertContentDescriptionEquals("Balance, ₹250.00")
        show(Paise(-25_000), MoneySemantic.Balance)
        node().assertContentDescriptionEquals("Negative balance, ₹250.00")
    }

    @Test
    fun `the spoken description of a budget figure says its state`() {
        show(Paise(25_000), MoneySemantic.Budget(BudgetState.UNDER))
        node().assertContentDescriptionEquals("Under budget, ₹250.00")
        show(Paise(25_000), MoneySemantic.Budget(BudgetState.AT_THRESHOLD))
        node().assertContentDescriptionEquals("Budget near its limit, ₹250.00")
        show(Paise(25_000), MoneySemantic.Budget(BudgetState.OVER))
        node().assertContentDescriptionEquals("Over budget, ₹250.00")
    }

    // --- tone colour ------------------------------------------------------------------------

    @Test
    fun `an expense total is drawn in the money red and an expense row is neutral (Q2)`() {
        var money = Color.Unspecified
        var neutral = Color.Unspecified
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                money = PftTheme.money.negative
                neutral = MaterialTheme.colorScheme.onSurface
                MoneyText(Paise(100), MoneySemantic.Expense, Modifier.testTag("total"), size = MoneySize.Large)
                MoneyText(Paise(100), MoneySemantic.Expense, Modifier.testTag("row"), size = MoneySize.Row)
            }
        }
        assertEquals(money, compose.onNodeWithTag("total").color())
        assertEquals(neutral, compose.onNodeWithTag("row").color())
    }

    @Test
    fun `income is the money green and a budget at its threshold is the money amber`() {
        var positive = Color.Unspecified
        var warning = Color.Unspecified
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                positive = PftTheme.money.positive
                warning = PftTheme.money.warning
                MoneyText(Paise(100), MoneySemantic.Income, Modifier.testTag("in"))
                MoneyText(Paise(100), MoneySemantic.Budget(BudgetState.AT_THRESHOLD), Modifier.testTag("budget"))
            }
        }
        assertEquals(positive, compose.onNodeWithTag("in").color())
        assertEquals(warning, compose.onNodeWithTag("budget").color())
    }

    @Test
    fun `a negative balance is red and a positive balance is neutral (Q3)`() {
        var negative = Color.Unspecified
        var neutral = Color.Unspecified
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                negative = PftTheme.money.negative
                neutral = MaterialTheme.colorScheme.onSurface
                MoneyText(Paise(-100), MoneySemantic.Balance, Modifier.testTag("debt"))
                MoneyText(Paise(100), MoneySemantic.Balance, Modifier.testTag("credit"))
            }
        }
        assertEquals(negative, compose.onNodeWithTag("debt").color())
        assertEquals(neutral, compose.onNodeWithTag("credit").color())
    }

    // --- sizes ------------------------------------------------------------------------------

    private fun FontFamily?.isNewsreader() =
        (this as? FontListFontFamily)?.fonts.orEmpty().let { fonts ->
            fonts.isNotEmpty() && fonts.all { it is ResourceFont && it.resId == R.font.newsreader }
        }

    @Test
    fun `row and large use the sans amount styles and the hero the display font`() {
        show(Paise(100), MoneySemantic.Transfer, MoneySize.Row)
        assertEquals(
            16f,
            node()
                .layout()
                .layoutInput.style.fontSize.value,
            0f,
        )
        assertEquals(
            "tnum",
            node()
                .layout()
                .layoutInput.style.fontFeatureSettings,
        )
        show(Paise(100), MoneySemantic.Transfer, MoneySize.Large)
        assertEquals(
            32f,
            node()
                .layout()
                .layoutInput.style.fontSize.value,
            0f,
        )
        show(Paise(100), MoneySemantic.Transfer, MoneySize.Hero)
        assertEquals(
            48f,
            node()
                .layout()
                .layoutInput.style.fontSize.value,
            0f,
        )
        assertTrue(
            node()
                .layout()
                .layoutInput.style.fontFamily
                .isNewsreader(),
        )
    }

    // --- hero shrink-to-fit (plan §3.3: 48 -> 40 -> 34 -> 28sp) --------------------------------

    /** The real ledger maximum, Rs 1e11 (`Paise.MAX_AMOUNT`): 21 characters with its sign and grouping. */
    private val maxAmount = Paise.MAX_AMOUNT

    private var boxWidthDp by mutableIntStateOf(0)
    private var probeSize by mutableStateOf<TextUnit?>(null)

    /**
     * Shows a hero expense in a box [widthDp] wide at [fontScale]. When a test sets [probeSize], a
     * plain single-line `Text` of the same amount at that size sits in an identical box, so the
     * test can tell whether a larger step really would not have fitted.
     */
    private fun showHero(
        amount: Paise,
        widthDp: Int,
        fontScale: Float = 1f,
    ) {
        boxWidthDp = widthDp
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    Column {
                        Box(Modifier.width(boxWidthDp.dp)) {
                            MoneyText(amount, MoneySemantic.Expense, Modifier.testTag(tag), size = MoneySize.Hero)
                        }
                        probeSize?.let { probe ->
                            Box(Modifier.width(boxWidthDp.dp)) {
                                Text(
                                    minus + PaiseFormat.format(amount),
                                    Modifier.testTag("probe"),
                                    style = PftTheme.type.amountHero.copy(fontSize = probe),
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun heroSizeSp() =
        node()
            .layout()
            .layoutInput.style.fontSize.value

    @Test
    fun `the maximum amount at 320dp and font scale 2 fits without visual overflow, at a size from the step list`() {
        showHero(maxAmount, widthDp = 320, fontScale = 2f)
        node().assertTextEquals("$minus₹1,00,00,00,00,000.00")
        node().layout().assertShownInFull()
        assertTrue("size ${heroSizeSp()} is not a step", heroSizeSp() in HeroSizeSteps.map { it.value })
    }

    @Test
    fun `a short amount in a wide box keeps the full 48sp`() {
        showHero(Paise(8_245_000), widthDp = 393)
        assertEquals(48f, heroSizeSp(), 0f)
        node().layout().assertShownInFull()
    }

    /** The step just above the one the hero chose, rendered as plain text in the same box, must overflow. */
    private fun assertNextLargerStepWouldOverflow(width: Int) {
        val chosen = HeroSizeSteps.indexOfFirst { it.value == heroSizeSp() }
        assertTrue("size ${heroSizeSp()} at $width is not a step", chosen >= 0)
        if (chosen == 0) return
        probeSize = HeroSizeSteps[chosen - 1]
        compose.waitForIdle()
        assertTrue(
            "${HeroSizeSteps[chosen - 1]} would have fitted at $width, but the hero chose ${heroSizeSp()}",
            !compose.onNodeWithTag("probe").layout().isShownInFull(),
        )
        probeSize = null
    }

    @Test
    fun `the maximum amount shrinks where 48sp would not fit, to the largest step that does`() {
        showHero(maxAmount, widthDp = 353)
        assertTrue("size ${heroSizeSp()}", heroSizeSp() < 48f)
        node().layout().assertShownInFull()
        assertNextLargerStepWouldOverflow(353)
    }

    @Test
    fun `a narrower box never gives a larger size, every width fits, and each is the largest that fits`() {
        showHero(maxAmount, widthDp = 420)
        val sizes =
            listOf(420, 353, 300, 280).map { width ->
                boxWidthDp = width
                compose.waitForIdle()
                node().layout().assertShownInFull("at $width")
                assertNextLargerStepWouldOverflow(width)
                heroSizeSp()
            }
        assertEquals(sizes.sortedDescending(), sizes)
        assertTrue("the widest and narrowest should differ: $sizes", sizes.first() > sizes.last())
    }

    @Test
    fun `where even the smallest step does not fit, the hero wraps and every character is still shown`() {
        showHero(maxAmount, widthDp = 150, fontScale = 2f)
        val result = node().layout()
        assertEquals(HeroSizeSteps.last().value, heroSizeSp(), 0f)
        assertTrue("expected a wrapped amount, got ${result.lineCount} line", result.lineCount > 1)
        assertEquals(result.layoutInput.text.length, result.getLineEnd(result.lineCount - 1, visibleEnd = false))
    }
}
