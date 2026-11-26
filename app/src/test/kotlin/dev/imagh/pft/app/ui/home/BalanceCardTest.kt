package dev.imagh.pft.app.ui.home

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.assertShownInFull
import dev.imagh.pft.app.ui.common.layout
import dev.imagh.pft.app.ui.common.layoutResult
import dev.imagh.pft.app.ui.theme.BrandColors
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * `BalanceCard` (Cobalt plan K6, D4, D6): the month's spend as a hero on the brand gradient, the
 * words "Spent this month", "In" and "Out", the two decorative ghost slabs above it and, only when a
 * budget is given, the meter. Fake data only. Pixels are the goldens' job (`HomeScreenshotTest`);
 * the pixel probes here pin which token is drawn where, from the real draw pass.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class BalanceCardTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val minus = "−"
    private var density = 1f
    private var brand: BrandColors? = null
    private var composeView: View? = null
    private var shown by mutableStateOf(card())

    private fun onCard() = checkNotNull(brand).onCard

    private fun card(
        expense: Paise = Paise(4_825_040),
        income: Paise = Paise(12_000_000),
        budget: BudgetMeter? = null,
    ) = BalanceCardModel("Household · November", expense, income, budget)

    private val budget = BudgetMeter(0.64f, "64% of ₹75,000.00 budget · 5 days left")

    private fun show(
        model: BalanceCardModel = card(),
        fontScale: Float = 1f,
        widthDp: Int = 393,
    ) {
        shown = model
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    density = LocalDensity.current.density
                    brand = PftTheme.brand
                    composeView = LocalView.current
                    Box(Modifier.width(widthDp.dp)) { BalanceCard(shown, Modifier.testTag("card")) }
                }
            }
        }
    }

    private fun change(model: BalanceCardModel) {
        compose.runOnUiThread { shown = model }
        compose.waitForIdle()
    }

    private fun text(value: String) = compose.onNode(hasText(value), useUnmergedTree = true)

    private fun Rect.inDp() = Rect(left / density, top / density, right / density, bottom / density)

    private fun bounds(tag: String) =
        compose
            .onNodeWithTag(tag)
            .fetchSemanticsNode()
            .boundsInRoot
            .inDp()

    private fun boundsOf(figure: String) =
        compose
            .onNode(hasText(figure))
            .fetchSemanticsNode()
            .boundsInRoot
            .inDp()

    private fun pixelAtDp(
        xDp: Float,
        yDp: Float,
    ): Color {
        var argb = 0
        compose.runOnUiThread {
            argb = composeView!!.drawToBitmap().getPixel((xDp * density).toInt(), (yDp * density).toInt())
        }
        return Color(argb)
    }

    private fun distance(
        a: Color,
        b: Color,
    ) = abs(a.red - b.red) + abs(a.green - b.green) + abs(a.blue - b.blue)

    private fun assertDrawn(
        expected: Color,
        actual: Color,
        what: String,
    ) = assertTrue("$what: expected $expected but was $actual", distance(expected, actual) <= 3f / 255f)

    private fun allTextLayouts() =
        compose
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .map { it.layoutResult() }

    // --- the hero ---------------------------------------------------------------------------------

    @Test
    fun `the hero is the month's spend as an expense, spoken like any MoneyText`() {
        show()
        text("$minus₹48,250.40").assertIsDisplayed()
        compose.onAllNodes(hasContentDescription("Expense, ₹48,250.40")).assertCountEquals(1)
    }

    @Test
    fun `the hero is drawn in onCard with its decimals faded to 0_68 alpha`() {
        show()
        val layout = text("$minus₹48,250.40").layout()
        assertEquals(onCard(), layout.layoutInput.style.color)
        val span =
            layout.layoutInput.text.spanStyles
                .single()
        assertEquals(
            ".40",
            layout.layoutInput.text.text
                .substring(span.start, span.end),
        )
        assertEquals(onCard().copy(alpha = 0.68f), span.item.color)
    }

    // --- words and solid colour -------------------------------------------------------------------

    @Test
    fun `the title, the caption and In and Out are words`() {
        show()
        text("Household · November").assertIsDisplayed()
        text("Spent this month").assertIsDisplayed()
        text("In").assertIsDisplayed()
        text("Out").assertIsDisplayed()
    }

    @Test
    fun `every text on the card is solid onCard, hierarchy comes from size and weight`() {
        show(card(budget = budget))
        val layouts = allTextLayouts()
        assertTrue("expected 8 or more texts: ${layouts.size}", layouts.size >= 8)
        layouts.forEach {
            assertEquals("'${it.layoutInput.text}' is not solid onCard", onCard(), it.layoutInput.style.color)
        }
    }

    @Test
    fun `In and Out show their amounts without a sign, the words carry the meaning`() {
        show()
        text("₹1,20,000.00").assertIsDisplayed()
        text("₹48,250.40").assertIsDisplayed()
    }

    @Test
    fun `In and Out each speak once, as their word and their figure`() {
        show()
        val spoken = { figure: String ->
            compose
                .onNode(hasText(figure))
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                .orEmpty()
                .map { it.text }
        }
        assertEquals(listOf("In", "₹1,20,000.00"), spoken("₹1,20,000.00"))
        assertEquals(listOf("Out", "₹48,250.40"), spoken("₹48,250.40"))
    }

    @Test
    fun `a zero month still draws a hero, In and Out`() {
        show(card(expense = Paise.ZERO, income = Paise.ZERO))
        text("$minus₹0.00").assertIsDisplayed()
        compose.onAllNodes(hasText("₹0.00")).assertCountEquals(2)
    }

    // --- the meter (hidden until slice D gives a budget, D6) --------------------------------------

    @Test
    fun `without a budget there is no meter label and the card is shorter than with one`() {
        show()
        compose.onAllNodes(hasText("budget", substring = true)).assertCountEquals(0)
        val without = bounds("card").height
        change(card(budget = budget))
        text(budget.label).assertIsDisplayed()
        val with = bounds("card").height
        assertTrue("a meter adds height: $without vs $with", with >= without + 16f)
    }

    @Test
    fun `the meter label is spoken once and the bar itself says nothing`() {
        show(card(budget = budget))
        compose.onAllNodes(hasText(budget.label)).assertCountEquals(1)
        val rangeInfo =
            compose
                .onAllNodes(
                    SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo),
                    useUnmergedTree = true,
                ).fetchSemanticsNodes()
        assertTrue("the bar must not duplicate the label's words: $rangeInfo", rangeInfo.isEmpty())
    }

    @Test
    fun `the meter fill covers the given fraction of its track`() {
        show(card(budget = budget))
        val label = boundsOf(budget.label)
        val card = bounds("card")
        val y = label.top - 8.5f
        val inset = 20f + 18f // the gutter and the card padding
        val track = card.width - 2 * inset
        val left = card.left + inset
        assertDrawn(onCard(), pixelAtDp(left + track * 0.60f, y), "inside the 64% fill")
        val rest = pixelAtDp(left + track * 0.70f, y)
        assertTrue("past the fill the track is not the fill colour: $rest", distance(onCard(), rest) > 0.1f)
    }

    // --- decorative parts -------------------------------------------------------------------------

    @Test
    fun `the ghosts, the icons and the bar carry no semantics, only the hero has a description`() {
        show(card(budget = budget))
        val nodes =
            compose
                .onAllNodes(
                    SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription),
                    useUnmergedTree = true,
                ).fetchSemanticsNodes()
        assertEquals(
            listOf("Expense, ₹48,250.40"),
            nodes.flatMap { it.config[SemanticsProperties.ContentDescription] },
        )
        val roles =
            compose
                .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Role), useUnmergedTree = true)
                .fetchSemanticsNodes()
        assertTrue("no part of the card is a button or an image: $roles", roles.isEmpty())
    }

    @Test
    fun `the ghosts peek above the card, the far one in cardGhost2 and the near one in cardGhost`() {
        show()
        val card = bounds("card")
        val x = card.center.x
        assertDrawn(checkNotNull(brand).cardGhost2, pixelAtDp(x, card.top + 3f), "the far ghost")
        assertDrawn(checkNotNull(brand).cardGhost, pixelAtDp(x, card.top + 11f), "the near ghost")
    }

    @Test
    fun `the card is the brand gradient, nearer cardStart at the top left and nearer cardEnd at the bottom right`() {
        show()
        val colours = checkNotNull(brand)
        val card = bounds("card")
        val top = card.top + 15f // below the ghosts
        val start = pixelAtDp(card.left + 20f + 8f, top + 28f)
        val end = pixelAtDp(card.right - 20f - 8f, card.bottom - 8f)
        assertTrue(
            "top left $start is not nearer cardStart ${colours.cardStart} than cardEnd ${colours.cardEnd}",
            distance(start, colours.cardStart) < distance(start, colours.cardEnd),
        )
        assertTrue(
            "bottom right $end is not nearer cardEnd ${colours.cardEnd} than cardStart ${colours.cardStart}",
            distance(end, colours.cardEnd) < distance(end, colours.cardStart),
        )
    }

    // --- the gradient line and the ring (the mockup's 135deg gradient with stops at 0, 55% and 100%) ----------

    /** The gradient card's rectangle in dp: inside the gutter and below the ghosts. */
    private fun cardRect(): Rect {
        val tag = bounds("card")
        return Rect(tag.left + 20f, tag.top + 15f, tag.right - 20f, tag.bottom)
    }

    /** Where a point of the card is on the 135 degree line, 0 at the start corner and 1 at the end corner. */
    private fun Rect.lineFraction(
        x: Float,
        y: Float,
    ) = 0.5f + ((x - center.x) + (y - center.y)) / (width + height)

    /** The gradient colour at [t]: cardStart at 0, cardMid at 0.55, cardEnd at 1, in sRGB. */
    private fun gradientAt(t: Float): Color {
        val c = checkNotNull(brand)
        val (from, to, f) =
            if (t <=
                0.55f
            ) {
                Triple(c.cardStart, c.cardMid, t / 0.55f)
            } else {
                Triple(c.cardMid, c.cardEnd, (t - 0.55f) / 0.45f)
            }
        return Color(
            red = from.red + (to.red - from.red) * f,
            green = from.green + (to.green - from.green) * f,
            blue = from.blue + (to.blue - from.blue) * f,
        )
    }

    private fun assertChannels(
        expected: Color,
        actual: Color,
        what: String,
    ) {
        val worst =
            maxOf(
                abs(expected.red - actual.red),
                abs(expected.green - actual.green),
                abs(
                    expected.blue - actual.blue,
                ),
            )
        assertTrue("$what: expected $expected but was $actual (off by ${worst * 255f} levels)", worst <= 2f / 255f)
    }

    @Test
    fun `the gradient line is a true 135 degrees line through the card centre`() {
        val (start, end) = cardGradientLine(width = 300f, height = 100f)
        assertEquals(Offset(50f, -50f), start)
        assertEquals(Offset(250f, 150f), end)
        val (s2, e2) = cardGradientLine(width = 317f, height = 163f)
        assertEquals("a 45 degree direction", e2.x - s2.x, e2.y - s2.y, 0.001f)
        assertEquals("its length is (w + h) / sqrt 2", (317f + 163f) / sqrt(2f), (e2 - s2).getDistance(), 0.01f)
        assertEquals("through the centre", Offset(158.5f, 81.5f), Offset((s2.x + e2.x) / 2, (s2.y + e2.y) / 2))
    }

    @Test
    fun `the colour 55 percent along the line is cardMid, so the middle stop sits at 55 and not at half`() {
        show()
        val card = cardRect()
        val a = 90f
        val y = card.center.y + 0.05f * (card.width + card.height) - a
        assertEquals(0.55f, card.lineFraction(card.center.x + a, y), 0.001f)
        assertChannels(checkNotNull(brand).cardMid, pixelAtDp(card.center.x + a, y), "55% along the line")
    }

    @Test
    fun `the colour 30 percent along the line follows the 0 to 55 percent ramp`() {
        show()
        val card = cardRect()
        val a = -29f
        val y = card.center.y - 0.2f * (card.width + card.height) - a
        assertEquals(0.3f, card.lineFraction(card.center.x + a, y), 0.001f)
        assertChannels(gradientAt(0.3f), pixelAtDp(card.center.x + a, y), "30% along the line")
    }

    @Test
    fun `the start side is the plain gradient, the ring never reaches where the line is under its minimum`() {
        show()
        val card = cardRect()
        // Along the top and left edges, 6dp in (clear of the text padding and, from 24dp, of the rounded corners).
        val probes =
            (24..(card.width.toInt() - 24) step 4).map { Offset(card.left + it, card.top + 6f) } +
                (24..(card.height.toInt() - 24) step 4).map { Offset(card.left + 6f, card.top + it) }
        val start = probes.filter { card.lineFraction(it.x, it.y) < RING_MIN_LINE_FRACTION }
        assertTrue("the probes cover the start side: ${start.size}", start.size >= 20)
        start.forEach {
            assertChannels(
                gradientAt(card.lineFraction(it.x, it.y)),
                pixelAtDp(it.x, it.y),
                "start side at $it",
            )
        }
    }

    /** The ring's centre: 68dp in from the right edge and 88dp down, as the mockup's circle is placed. */
    private fun ringCentre(card: Rect) = Offset(card.right - 68f, card.top + 88f)

    @Test
    fun `the ring is the gradient lightened by onCard at 8 percent on its stroke, and plain in its hole`() {
        show()
        val card = cardRect()
        val centre = ringCentre(card)
        // Radius 114 is the middle of the 28dp stroke (100 to 128); both probes are clear of the text.
        val onStroke = Offset(centre.x - 81f, centre.y - 80f)
        val inHole = Offset(centre.x + 30f, centre.y - 60f) // radius 67
        val white = checkNotNull(brand).onCard
        val plain = gradientAt(card.lineFraction(onStroke.x, onStroke.y))
        val lightened =
            Color(
                red = plain.red + (white.red - plain.red) * 0.08f,
                green = plain.green + (white.green - plain.green) * 0.08f,
                blue = plain.blue + (white.blue - plain.blue) * 0.08f,
            )
        val stroke = pixelAtDp(onStroke.x, onStroke.y)
        assertChannels(lightened, stroke, "on the ring stroke")
        val lift = (stroke.red - plain.red) + (stroke.green - plain.green) + (stroke.blue - plain.blue)
        assertTrue("the ring stroke $stroke should be visibly lighter than the plain gradient $plain", lift >= 0.06f)
        assertChannels(
            gradientAt(card.lineFraction(inHole.x, inHole.y)),
            pixelAtDp(inHole.x, inHole.y),
            "inside the ring",
        )
    }

    @Test
    fun `the ring is clipped by the card and does not show in the gutter beside it`() {
        show()
        val card = cardRect()
        val centre = ringCentre(card)
        // The circle's stroke reaches 60dp past the card's right edge. 10dp out and 70dp above the centre is on the
        // stroke (radius 105); 10dp out and 20dp below it is in the hole. Clipped, both are the same screen colour.
        val onStroke = pixelAtDp(card.right + 10f, centre.y - 70f)
        val inHole = pixelAtDp(card.right + 10f, centre.y + 20f)
        assertChannels(inHole, onStroke, "the gutter beside the ring")
    }

    // --- layout -----------------------------------------------------------------------------------

    @Test
    fun `In and Out sit side by side when both fit`() {
        show()
        val inRow = boundsOf("₹1,20,000.00")
        val out = boundsOf("₹48,250.40")
        assertEquals("same line", inRow.top, out.top, 1f)
        assertTrue("In is left of Out", inRow.right <= out.left)
    }

    @Test
    fun `In and Out stack when they do not fit side by side, at 200 percent`() {
        show(fontScale = 2f, widthDp = 320)
        val inRow = boundsOf("₹1,20,000.00")
        val out = boundsOf("₹48,250.40")
        assertTrue("Out is under In: ${inRow.bottom} vs ${out.top}", out.top >= inRow.bottom - 0.5f)
    }

    @Test
    fun `a large spend is shown in full at 200 percent on a 320dp window`() {
        // 100 crore: 17 characters. The card has 244dp inside the gutter and padding, so the largest amount
        // cannot fit one line there (see the next test); this is the largest that does.
        show(card(expense = Paise(100_000_000_000), income = Paise(100_000_000_000)), fontScale = 2f, widthDp = 320)
        text("$minus₹1,00,00,00,000.00").layout().assertShownInFull("the hero at 200% on 320dp")
    }

    @Test
    fun `at 200 percent on 320dp the largest spend wraps whole and is never cut`() {
        show(card(expense = Paise.MAX_AMOUNT, income = Paise.MAX_AMOUNT), fontScale = 2f, widthDp = 320)
        val figure = "$minus₹1,00,00,00,00,000.00"
        val layout = text(figure).layout()
        assertEquals("a hero at its smallest step wraps", 12f, layout.layoutInput.style.fontSize.value, 0f)
        assertEquals(figure.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertFalse("ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
    }

    @Test
    fun `the largest spend is shown in full at 100 percent`() {
        show(card(expense = Paise.MAX_AMOUNT, income = Paise.MAX_AMOUNT))
        text("$minus₹1,00,00,00,00,000.00").layout().assertShownInFull("the hero at 100%")
    }

    @Test
    fun `at 200 percent the largest In and Out amounts wrap whole and are never cut`() {
        show(card(expense = Paise.MAX_AMOUNT, income = Paise.MAX_AMOUNT), fontScale = 2f, widthDp = 320)
        val figure = "₹1,00,00,00,00,000.00"
        val layouts =
            compose
                .onAllNodes(hasText(figure), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .map { it.layoutResult() }
        assertEquals("In and Out", 2, layouts.size)
        layouts.forEach { layout ->
            assertEquals(figure.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
            assertFalse("ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
            assertTrue(
                "a line is wider than its box",
                (0 until layout.lineCount).all { layout.getLineRight(it) <= layout.size.width + 0.5f },
            )
        }
    }

    @Test
    fun `at 200 percent a long title and meter label wrap whole`() {
        val label = "64% of ₹75,000.00 budget · 5 days left in the household month"
        show(card(budget = BudgetMeter(0.64f, label)), fontScale = 2f, widthDp = 320)
        val layout = text(label).layout()
        assertEquals(label.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertFalse("ellipsized", (0 until layout.lineCount).any { layout.isLineEllipsized(it) })
    }

    @Test
    fun `the card fills the width it is given, inset by the gutter`() {
        show()
        val card = bounds("card")
        assertEquals(393f, card.width, 0.5f)
        // the gradient card itself starts at the gutter: left of it is the screen background, not a brand colour
        val outside = pixelAtDp(10f, card.top + 40f)
        assertTrue("the gutter is not drawn on: $outside", distance(outside, checkNotNull(brand).cardStart) > 0.1f)
    }

    // --- the model --------------------------------------------------------------------------------

    @Test
    fun `a negative spend is rejected, an amount here is a magnitude`() {
        assertThrows(IllegalArgumentException::class.java) { card(expense = Paise(-1)) }
    }

    @Test
    fun `a negative income is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { card(income = Paise(-1)) }
    }

    @Test
    fun `a meter fraction outside 0 to 1 or not a number is rejected, the ends are allowed`() {
        listOf(-0.01f, 1.01f, Float.NaN, Float.POSITIVE_INFINITY).forEach {
            assertThrows("fraction $it", IllegalArgumentException::class.java) { BudgetMeter(it, "x") }
        }
        assertEquals(0f, BudgetMeter(0f, "x").fraction, 0f)
        assertEquals(1f, BudgetMeter(1f, "x").fraction, 0f)
    }
}
