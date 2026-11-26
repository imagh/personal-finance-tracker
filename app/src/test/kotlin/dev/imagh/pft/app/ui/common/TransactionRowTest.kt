package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `TransactionRow` (Cobalt plan K5, D3): the tile, the title, the "category · account" meta, the
 * amount, an optional "Needs review" chip and optional tag chips, as one clickable node. Fake data
 * only. Pixels are the goldens' job (`TransactionRowScreenshotTest`).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TransactionRowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f
    private var neutral = Color.Unspecified
    private var positive = Color.Unspecified
    private var muted = Color.Unspecified

    private val minus = "−"

    private fun model(
        title: String = "Swiggy",
        category: String? = "Dining out",
        account: String = "HDFC card",
        amount: Paise = Paise(48_600),
        semantic: MoneySemantic = MoneySemantic.Expense,
        needsReview: Boolean = false,
        tags: List<String> = emptyList(),
        onClick: (() -> Unit)? = null,
    ) = TransactionRowModel(
        title = title,
        glyph = GlyphSource.Symbol(PftSymbol.RESTAURANT),
        colourKey = "coral",
        category = category,
        account = account,
        amount = amount,
        semantic = semantic,
        needsReview = needsReview,
        tags = tags,
        onClick = onClick,
    )

    private fun show(
        model: TransactionRowModel,
        fontScale: Float = 1f,
        widthDp: Int = 353,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    density = LocalDensity.current.density
                    neutral = MaterialTheme.colorScheme.onSurface
                    positive = PftTheme.money.positive
                    muted = MaterialTheme.colorScheme.onSurfaceVariant
                    Box(Modifier.width(widthDp.dp)) { TransactionRow(model, Modifier.testTag("row")) }
                }
            }
        }
    }

    private fun row() = compose.onNodeWithTag("row")

    private fun text(value: String) = compose.onNode(hasText(value), useUnmergedTree = true)

    private fun amountColor(figure: String) =
        text(figure)
            .layout()
            .layoutInput.style.color

    /** What the row node offers to a screen reader: its text list, then its content descriptions (just the one). */
    private fun spoken(): List<String> {
        val config = row().fetchSemanticsNode().config
        return config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } +
            config.getOrNull(SemanticsProperties.ContentDescription).orEmpty()
    }

    private fun Rect.inDp() = Rect(left / density, top / density, right / density, bottom / density)

    @Test
    fun `the title and the category dot account meta are shown`() {
        show(model())
        text("Swiggy").assertIsDisplayed()
        text("Dining out · HDFC card").assertIsDisplayed()
    }

    @Test
    fun `the title is onSurface and the meta onSurfaceVariant, whatever content colour surrounds the row`() {
        show(model())
        assertEquals(
            "title",
            neutral,
            text("Swiggy")
                .layout()
                .layoutInput.style.color,
        )
        assertEquals(
            "meta",
            muted,
            text("Dining out · HDFC card")
                .layout()
                .layoutInput.style.color,
        )
    }

    @Test
    fun `a row with no category shows the account alone`() {
        show(model(category = null, account = "Everyday"))
        text("Everyday").assertIsDisplayed()
        compose.onAllNodes(hasText("·", substring = true), useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `an expense is a signed neutral amount`() {
        show(model(amount = Paise(48_600), semantic = MoneySemantic.Expense))
        text("$minus₹486.00").assertIsDisplayed()
        assertEquals(neutral, amountColor("$minus₹486.00"))
    }

    @Test
    fun `an income is a plus signed positive amount`() {
        show(model(title = "Salary", amount = Paise(12_000_000), semantic = MoneySemantic.Income))
        text("+₹1,20,000.00").assertIsDisplayed()
        assertEquals(positive, amountColor("+₹1,20,000.00"))
    }

    @Test
    fun `a transfer is an unsigned neutral amount`() {
        show(model(title = "To savings", amount = Paise(500_000), semantic = MoneySemantic.Transfer))
        text("₹5,000.00").assertIsDisplayed()
        assertEquals(neutral, amountColor("₹5,000.00"))
    }

    @Test
    fun `only an expense, an income or a transfer can be a transaction`() {
        listOf(MoneySemantic.Balance, MoneySemantic.Net, MoneySemantic.Budget(BudgetState.OVER)).forEach { semantic ->
            val failure = runCatching { model(semantic = semantic) }.exceptionOrNull()
            assertTrue("$semantic: $failure", failure is IllegalArgumentException)
        }
    }

    @Test
    fun `a transaction amount is a magnitude, never negative`() {
        val failure = runCatching { model(amount = Paise(-1)) }.exceptionOrNull()
        assertTrue("$failure", failure is IllegalArgumentException)
    }

    @Test
    fun `the tile is decorative, so no image is announced`() {
        show(model())
        compose
            .onAllNodes(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image), useUnmergedTree = true)
            .assertCountEquals(0)
        assertFalse(
            "the tile spoke: ${spoken()}",
            spoken().any { it == "restaurant" || it == "Dining out" },
        )
    }

    @Test
    fun `needs review shows the chip with its words`() {
        show(model(needsReview = true))
        text("Needs review").assertIsDisplayed()
    }

    @Test
    fun `no chip without the flag`() {
        show(model(needsReview = false))
        compose.onAllNodes(hasText("Needs review"), useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `tags are text chips`() {
        show(model(tags = listOf("Travel", "Work")))
        text("Travel").assertIsDisplayed()
        text("Work").assertIsDisplayed()
    }

    @Test
    fun `the whole row is one clickable node`() {
        var taps = 0
        show(model(onClick = { taps++ }))
        compose.onAllNodes(hasClickAction(), useUnmergedTree = true).assertCountEquals(1)
        row().assertHasClickAction()
        assertEquals(Role.Button, row().fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role))
        row().performClick()
        assertEquals(1, taps)
    }

    @Test
    fun `a row with no onClick has no click action`() {
        show(model(onClick = null))
        row().assertHasNoClickAction()
        compose.onAllNodes(hasClickAction(), useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `the row speaks title, category, account, amount and chip once each, in that order`() {
        show(model(needsReview = true))
        assertEquals(
            listOf("Swiggy, Dining out, HDFC card, Expense, ₹486.00, Needs review"),
            spoken(),
        )
    }

    @Test
    fun `the tags are spoken between the account and the amount`() {
        show(model(tags = listOf("Travel", "Work"), semantic = MoneySemantic.Income))
        assertEquals(
            listOf("Swiggy, Dining out, HDFC card, Travel, Work, Income, ₹486.00"),
            spoken(),
        )
    }

    @Test
    fun `a transfer with no category speaks no empty part`() {
        show(model(title = "To savings", category = null, semantic = MoneySemantic.Transfer))
        assertEquals(listOf("To savings, HDFC card, Transfer, ₹486.00"), spoken())
    }

    @Test
    fun `a tappable row is at least 56dp tall, above the 48dp touch target`() {
        show(model(onClick = {}))
        val bounds = row().fetchSemanticsNode().boundsInRoot.inDp()
        assertTrue("height ${bounds.height}", bounds.height >= 56f - 0.5f)
        val touch = row().fetchSemanticsNode().touchBoundsInRoot.inDp()
        assertTrue("touch height ${touch.height}", touch.height >= 48f - 0.5f)
        assertTrue("touch width ${touch.width}", touch.width >= 48f - 0.5f)
    }

    @Test
    fun `below the stacking scale the meta stays on one line, and the full text is still spoken`() {
        // The Salary row of the golden: an income amount wide enough to leave the meta too little room.
        show(
            model(
                category = "Income",
                account = "Everyday",
                amount = Paise(12_000_000),
                semantic = MoneySemantic.Income,
            ),
            widthDp = 300,
        )
        assertEquals(1, text("Income · Everyday").layout().lineCount)
        // One line may cut the visible text, so the spoken description must still carry all of it.
        assertTrue(spoken().joinToString(), "Income, Everyday" in spoken().joinToString())
    }

    @Test
    fun `at 200 percent the meta wraps whole instead of being cut`() {
        val account = "Joint household savings account"
        show(model(category = "Dining out", account = account), fontScale = 2f, widthDp = 320)
        val meta = "Dining out · $account"
        val layout = text(meta).layout()
        assertTrue("lines ${layout.lineCount}", layout.lineCount > 1)
        assertEquals(meta.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertFalse("the meta was ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
    }

    @Test
    fun `at 200 percent a long title wraps whole`() {
        val title = "Annual maintenance contract for the air conditioner"
        show(model(title = title), fontScale = 2f, widthDp = 320)
        val layout = text(title).layout()
        assertTrue("lines ${layout.lineCount}", layout.lineCount > 1)
        assertEquals(title.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertFalse("the title was ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
    }

    @Test
    fun `the largest amount is shown in full`() {
        show(model(amount = Paise.MAX_AMOUNT, needsReview = true))
        text("$minus₹1,00,00,00,00,000.00").layout().assertShownInFull("at 100%")
    }

    @Test
    fun `at 200 percent a large amount is shown in full on a narrow window`() {
        show(model(amount = Paise(100_000_000_000), needsReview = true), fontScale = 2f, widthDp = 320)
        text("$minus₹1,00,00,00,000.00").layout().assertShownInFull("at 200%")
    }

    @Test
    fun `at 200 percent the largest amount wraps whole and is never cut`() {
        show(model(amount = Paise.MAX_AMOUNT, needsReview = true), fontScale = 2f, widthDp = 320)
        val figure = "$minus₹1,00,00,00,00,000.00"
        val layout = text(figure).layout()
        assertEquals(figure.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertFalse("the amount was ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
        assertTrue(
            "a line is wider than its box",
            (0 until layout.lineCount).all { layout.getLineRight(it) <= layout.size.width + 0.5f },
        )
    }

    @Test
    fun `at 200 percent many tags wrap inside the row`() {
        val tags = listOf("Travel", "Work", "Reimbursable", "Family holiday", "Tax")
        show(model(tags = tags), fontScale = 2f, widthDp = 320)
        val rowBounds = row().fetchSemanticsNode().boundsInRoot.inDp()
        tags.forEach { tag ->
            val node = text(tag)
            node.assertIsDisplayed()
            val bounds = node.fetchSemanticsNode().boundsInRoot.inDp()
            assertTrue("$tag right ${bounds.right} beyond ${rowBounds.right}", bounds.right <= rowBounds.right + 0.5f)
            node.layout().assertShownInFull("tag chip")
        }
    }
}
