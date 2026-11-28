package dev.imagh.pft.app.ui.home

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.layout
import dev.imagh.pft.app.ui.theme.BrandColors
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/**
 * `QuickActionRow` (Cobalt plan K6, D6): the row renders exactly the actions it is given, each a
 * button named by its text with a target of at least 48dp, the primary one filled with `brand`. It
 * never decides which actions exist: Budgets simply is not passed until slice D. Fake data only.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class QuickActionRowTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = 1f
    private var brand: BrandColors? = null
    private var onSurface = Color.Unspecified
    private var composeView: View? = null
    private val clicks = mutableListOf<String>()

    private fun action(
        label: String,
        primary: Boolean = false,
    ) = QuickAction(label, PftSymbol.ADD, { clicks += label }, primary)

    private val three = listOf(action("Expense", primary = true), action("Income"), action("Transfer"))
    private val four = three + action("Budgets")

    private fun show(
        actions: List<QuickAction>,
        fontScale: Float = 1f,
        widthDp: Int = 393,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    density = LocalDensity.current.density
                    brand = PftTheme.brand
                    onSurface = MaterialTheme.colorScheme.onSurface
                    composeView = LocalView.current
                    Box(Modifier.width(widthDp.dp)) { QuickActionRow(actions) }
                }
            }
        }
    }

    private val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    private fun buttons() = compose.onAllNodes(isButton).fetchSemanticsNodes()

    private fun Rect.inDp() = Rect(left / density, top / density, right / density, bottom / density)

    private fun distance(
        a: Color,
        b: Color,
    ) = abs(a.red - b.red) + abs(a.green - b.green) + abs(a.blue - b.blue)

    /** The tile's colour just inside its top edge, in the middle: above the icon, below the top. */
    private fun tileColour(label: String): Color {
        val bounds = compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
        var argb = 0
        compose.runOnUiThread {
            argb = composeView!!.drawToBitmap().getPixel(bounds.center.x.toInt(), (bounds.top + 3f * density).toInt())
        }
        return Color(argb)
    }

    private fun assertTile(
        label: String,
        expected: Color,
    ) {
        val actual = tileColour(label)
        assertTrue("$label tile is $actual, expected $expected", distance(actual, expected) <= 3f / 255f)
    }

    // --- exactly what it is given -----------------------------------------------------------------

    @Test
    fun `three actions render three buttons, in the order given, and no Budgets`() {
        show(three)
        val labels =
            buttons().map {
                it.config
                    .getOrNull(SemanticsProperties.Text)
                    .orEmpty()
                    .map { t -> t.text }
            }
        assertEquals(listOf(listOf("Expense"), listOf("Income"), listOf("Transfer")), labels)
        compose.onAllNodes(hasText("Budgets")).assertCountEquals(0)
    }

    @Test
    fun `four actions render four buttons, so a later slice adds Budgets by passing it`() {
        show(four)
        assertEquals(4, buttons().size)
        compose.onNodeWithText("Budgets").assertHasClickAction()
    }

    @Test
    fun `an empty list renders no button`() {
        show(emptyList())
        assertEquals(0, buttons().size)
    }

    @Test
    fun `each action is a button named by its text alone, its icon is decorative`() {
        show(three)
        assertEquals("the three buttons", 3, buttons().size)
        buttons().forEach { node ->
            assertEquals(
                1,
                node.config
                    .getOrNull(SemanticsProperties.Text)
                    .orEmpty()
                    .size,
            )
            assertEquals(null, node.config.getOrNull(SemanticsProperties.ContentDescription))
        }
        compose.onNodeWithText("Income").assertHasClickAction()
    }

    @Test
    fun `tapping an action runs its own onClick once and no other`() {
        show(four)
        compose.onNodeWithText("Income").performClick()
        assertEquals(listOf("Income"), clicks)
        compose.onNodeWithText("Budgets").performClick()
        assertEquals(listOf("Income", "Budgets"), clicks)
    }

    // --- targets ----------------------------------------------------------------------------------

    @Test
    fun `every button is drawn at least 48dp wide and tall`() {
        show(four)
        assertEquals("the four buttons", 4, buttons().size)
        buttons().forEach {
            val bounds = it.boundsInRoot.inDp()
            assertTrue("height ${bounds.height}", bounds.height >= 48f - 0.5f)
            assertTrue("width ${bounds.width}", bounds.width >= 48f - 0.5f)
        }
    }

    // --- the primary fill -------------------------------------------------------------------------

    @Test
    fun `the primary action is filled with brand and the others with brandSoft`() {
        show(three)
        val colours = checkNotNull(brand)
        assertTile("Expense", colours.brand)
        assertTile("Income", colours.brandSoft)
        assertTile("Transfer", colours.brandSoft)
    }

    @Test
    fun `the icon is onBrand on the filled tile and onBrandSoft on the soft one`() {
        val colours =
            BrandColors(
                brand = Color.Red,
                onBrand = Color.White,
                brandSoft = Color.Blue,
                onBrandSoft = Color.Yellow,
                cardStart = Color.Black,
                cardMid = Color.Black,
                cardEnd = Color.Black,
                onCard = Color.White,
                cardGhost = Color.Gray,
                cardGhost2 = Color.Gray,
                nav = Color.Black,
            )
        val primary = QuickActionColors(fill = Color.Red, icon = Color.White)
        val soft = QuickActionColors(fill = Color.Blue, icon = Color.Yellow)
        assertEquals(primary, quickActionColors(colours, primary = true))
        assertEquals(soft, quickActionColors(colours, primary = false))
    }

    @Test
    fun `the label is app text in onSurface, never the brand colour`() {
        show(three)
        assertEquals(
            onSurface,
            compose
                .onNodeWithText("Income")
                .layout()
                .layoutInput.style.color,
        )
    }

    // --- large text -------------------------------------------------------------------------------

    @Test
    fun `at 100 percent the actions share one line`() {
        show(four)
        val tops = buttons().map { it.boundsInRoot.top }
        assertEquals("the four buttons", 4, tops.size)
        assertTrue("one line: $tops", tops.all { abs(it - tops.first()) < 1f })
    }

    @Test
    fun `at 200 percent on 320dp the actions wrap onto more lines and every label is whole on one line`() {
        show(four, fontScale = 2f, widthDp = 320)
        assertEquals("the four buttons", 4, buttons().size)
        val tops = buttons().map { it.boundsInRoot.top }.distinct()
        assertTrue("expected more than one line of actions: $tops", tops.size > 1)
        listOf("Expense", "Income", "Transfer", "Budgets").forEach {
            // Not assertShownInFull: its line-right test is relative to the text box, which a centred line leaves.
            val layout = compose.onNodeWithText(it).layout()
            assertEquals("'$it' is on one line", 1, layout.lineCount)
            assertEquals("'$it' is whole", it.length, layout.getLineEnd(0, visibleEnd = false))
            assertTrue("'$it' is ellipsized", !layout.isLineEllipsized(0))
        }
        buttons().forEach { node ->
            val bounds = node.boundsInRoot.inDp()
            assertTrue("inside the window: $bounds", bounds.left >= 0f && bounds.right <= 320f + 0.5f)
        }
    }

    @Test
    fun `a long label wraps by words and is never cut`() {
        val label = "Pay the landlord this month"
        show(listOf(action(label), action("Income")), fontScale = 2f, widthDp = 320)
        val layout = compose.onNodeWithText(label).layout()
        assertEquals(label.length, layout.getLineEnd(layout.lineCount - 1, visibleEnd = false))
        assertTrue("ellipsized", (0 until layout.lineCount).none { layout.isLineEllipsized(it) })
    }
}
