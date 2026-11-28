package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.DisplayFont
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `MoneyText` drawn on the balance card (Cobalt plan D4, step K3): `onColor` draws a neutral figure
 * in a given colour and refuses every other tone, and `fadeDecimals` fades the hero's decimals to
 * 0.68 alpha only while the hero is at a large-text step (24sp or more). The fade is read from the
 * `TextLayoutResult` span colours; the text and the spoken description do not change. The rest of
 * `MoneyText` is in `MoneyTextTest`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class MoneyTextOnCardTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val minus = "−"
    private val tag = "money"
    private val maxAmount = Paise.MAX_AMOUNT
    private val grotesque = ThemePrefs(dynamicColor = false, displayFont = DisplayFont.BUNDLED_GROTESQUE)

    private var boxWidthDp by mutableIntStateOf(0)

    /** The theme's `onSurface`, which a neutral amount is drawn in; captured by [showHero]. */
    private var onSurface = Color.Unspecified

    private fun node() = compose.onNodeWithTag(tag)

    private fun SemanticsNodeInteraction.color(): Color = layout().layoutInput.style.color

    private fun heroSizeSp() =
        node()
            .layout()
            .layoutInput.style.fontSize.value

    /** Every text layout under the money node, in the unmerged tree: one per rolling digit, or one for plain text. */
    private fun textNodes() =
        compose
            .onAllNodes(
                SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult),
                useUnmergedTree = true,
            ).fetchSemanticsNodes()

    /** Shows a hero in a box [widthDp] wide; changing [boxWidthDp] later re-fits it. */
    private fun showHero(
        amount: Paise,
        widthDp: Int,
        prefs: ThemePrefs = ThemePrefs(dynamicColor = false),
        fade: Boolean = false,
        onColor: Color = Color.Unspecified,
        animate: Boolean = false,
    ) {
        boxWidthDp = widthDp
        compose.setContent {
            PftTheme(prefs) {
                onSurface = MaterialTheme.colorScheme.onSurface
                Box(Modifier.width(boxWidthDp.dp)) {
                    MoneyText(
                        amount,
                        MoneySemantic.Expense,
                        Modifier.testTag(tag),
                        MoneySize.Hero,
                        animate,
                        onColor,
                        fade,
                    )
                }
            }
        }
    }

    // --- onColor: money drawn on a fill the theme sweeps separately (Cobalt plan D4) --------------

    private val onCard = Color(0xFFFFFFFF)

    @Test
    fun `onColor draws a neutral amount in that colour, for an expense, a net figure and a transfer`() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                MoneyText(Paise(100), MoneySemantic.Expense, Modifier.testTag("expense"), onColor = onCard)
                MoneyText(Paise(-100), MoneySemantic.Net, Modifier.testTag("net"), onColor = onCard)
                MoneyText(Paise(100), MoneySemantic.Transfer, Modifier.testTag("transfer"), onColor = onCard)
                MoneyText(Paise(100), MoneySemantic.Balance, Modifier.testTag("balance"), onColor = onCard)
            }
        }
        listOf("expense", "net", "transfer", "balance").forEach {
            assertEquals(it, onCard, compose.onNodeWithTag(it).color())
        }
    }

    /** Composes [semantic] with an `onColor` and returns what composition threw, or null. */
    private fun onColorFailure(
        semantic: MoneySemantic,
        amount: Paise = Paise(100),
    ): Throwable? =
        runCatching {
            compose.setContent {
                PftTheme(ThemePrefs(dynamicColor = false)) { MoneyText(amount, semantic, onColor = onCard) }
            }
        }.exceptionOrNull()

    private fun assertOnColorRejected(
        semantic: MoneySemantic,
        amount: Paise = Paise(100),
    ) {
        val failure = onColorFailure(semantic, amount)
        // The failure may arrive wrapped by the test harness; the guard's own message is what we look for.
        val causes = generateSequence(failure) { it.cause }.toList()
        assertTrue("$semantic with onColor must throw, got $failure", causes.any { it is IllegalArgumentException })
    }

    @Test
    fun `onColor with income, a positive tone, throws so no green is drawn on the blue`() {
        assertOnColorRejected(MoneySemantic.Income)
    }

    @Test
    fun `onColor with an over-budget figure, a negative tone, throws so no red is drawn on the blue`() {
        assertOnColorRejected(MoneySemantic.Budget(BudgetState.OVER))
    }

    @Test
    fun `onColor with a budget at its threshold, a warning tone, throws`() {
        assertOnColorRejected(MoneySemantic.Budget(BudgetState.AT_THRESHOLD))
    }

    @Test
    fun `onColor with a negative balance, a negative tone, throws`() {
        assertOnColorRejected(MoneySemantic.Balance, Paise(-100))
    }

    // --- fadeDecimals: the hero's decimals at 0.68 alpha while it is at a large-text step -----

    private val decimals = ".78"
    private val heroText = "$minus₹1,23,456.78"

    /** The colour spans of the node's single text layout. */
    private fun SemanticsNodeInteraction.spans() = layout().layoutInput.text.spanStyles

    private fun SemanticsNodeInteraction.assertDecimalsFaded(base: Color) {
        val span = spans().single()
        assertEquals(heroText.length - decimals.length, span.start)
        assertEquals(heroText.length, span.end)
        assertEquals(base.copy(alpha = 0.68f), span.item.color)
        assertEquals("the whole figure keeps its solid colour", base, layout().layoutInput.style.color)
    }

    private fun SemanticsNodeInteraction.assertSolid() {
        assertTrue("expected no colour span, got ${spans()}", spans().none { it.item.color != Color.Unspecified })
    }

    @Test
    fun `a hero draws its decimals at 0_68 alpha at 34sp and the rest solid`() {
        showHero(Paise(12_345_678), widthDp = 393, prefs = grotesque, fade = true)
        assertEquals(34f, heroSizeSp(), 0f)
        node().assertDecimalsFaded(onSurface)
        node().assertTextEquals(heroText)
    }

    @Test
    fun `a hero draws its decimals solid once it has shrunk to 20sp`() {
        showHero(maxAmount, widthDp = 250, prefs = grotesque, fade = true)
        assertEquals(20f, heroSizeSp(), 0f)
        node().assertSolid()
    }

    @Test
    fun `the decimals fade at 24sp and above and are solid at 20sp and below, each the size the hero chose`() {
        showHero(maxAmount, widthDp = 420, prefs = grotesque, fade = true)
        val seen = mutableMapOf<Float, Boolean>()
        listOf(420, 353, 330, 300, 280, 250, 230, 210, 190, 170, 150).forEach { width ->
            boxWidthDp = width
            compose.waitForIdle()
            val faded = node().spans().any { it.item.color != Color.Unspecified }
            seen[heroSizeSp()] = faded
        }
        seen.forEach { (size, faded) -> assertEquals("faded at ${size}sp", size >= 24f, faded) }
        assertTrue("24sp and 20sp must both be covered: ${seen.keys}", 24f in seen && 20f in seen)
    }

    @Test
    fun `fading the decimals changes only the colour, the spoken text and the shown text are unchanged`() {
        showHero(Paise(12_345_678), widthDp = 393, prefs = grotesque, fade = true)
        node().assertTextEquals(heroText)
        node().assertContentDescriptionEquals("Expense, ₹1,23,456.78")
    }

    @Test
    fun `fadeDecimals is for the hero only, a large amount ignores it`() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                MoneyText(
                    Paise(12_345_678),
                    MoneySemantic.Expense,
                    Modifier.testTag(tag),
                    MoneySize.Large,
                    fadeDecimals = true,
                )
            }
        }
        node().assertSolid()
    }

    @Test
    fun `fadeDecimals fades the decimals of an onColor hero from that colour`() {
        showHero(Paise(12_345_678), widthDp = 393, prefs = grotesque, fade = true, onColor = onCard)
        node().assertDecimalsFaded(onCard)
    }

    @Test
    fun `animated digits fade the same decimals and no others`() {
        showHero(Paise(12_345_678), widthDp = 393, prefs = grotesque, fade = true, animate = true)
        val alphas =
            textNodes()
                .sortedBy { it.boundsInRoot.left }
                .map {
                    it.shownText() to
                        it
                            .layoutResult()
                            .layoutInput.style.color.alpha
                }
        assertEquals(heroText, alphas.joinToString("") { it.first })
        val fadedText = alphas.filter { it.second < 1f }
        assertEquals(decimals, fadedText.joinToString("") { it.first })
        assertTrue(fadedText.all { it.second == onSurface.copy(alpha = 0.68f).alpha })
    }
}
