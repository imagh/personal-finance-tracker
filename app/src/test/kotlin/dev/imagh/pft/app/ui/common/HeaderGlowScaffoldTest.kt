package dev.imagh.pft.app.ui.common

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.down
import androidx.compose.ui.test.moveBy
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.GraphitePalette
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/**
 * The header glow (DS6d, UI-13) in real pixels: `CollapsingHeaderScaffold` on a 393x851dp window,
 * native graphics, read back with `View.drawToBitmap()` (the DS6c recipe; `captureToImage` times
 * out here). Graphite light, so the tint is visibly darker than the canvas. Splitting these off
 * `CollapsingHeaderScaffoldTest` keeps that class under detekt's size limit.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class HeaderGlowScaffoldTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = Density(1f)
    private var composeView: View? = null
    private val barHeight = 64f
    private val expandedHeight = 851f * 0.38f

    private val glowOn = ThemePrefs(dynamicColor = false, headerGlow = true)
    private val glowOff = ThemePrefs(dynamicColor = false)
    private val canvas = GraphitePalette.light.canvas

    /** The wash at the glow's cap over the canvas: what the very top-centre pixel should be, within rounding. */
    private val washTop =
        GraphitePalette.light.wash
            .copy(alpha = MAX_GLOW_ALPHA)
            .compositeOver(canvas)

    private val sampleHero: @Composable () -> Unit = { Text("₹48,250", Modifier.testTag("hero-text")) }

    private fun tag(tag: String) = compose.onNodeWithTag(tag)

    private fun SemanticsNodeInteraction.bounds() = fetchSemanticsNode().boundsInRoot

    private fun frames(count: Int) =
        repeat(count) {
            compose.mainClock.advanceTimeByFrame()
            compose.waitForIdle()
        }

    private fun show(
        style: HeaderStyle,
        prefs: ThemePrefs,
        hero: (@Composable () -> Unit)? = null,
    ) {
        compose.setContent {
            density = LocalDensity.current
            composeView = LocalView.current
            PftTheme(prefs) {
                CollapsingHeaderScaffold(
                    title = "Budgets",
                    style = style,
                    hero = hero,
                    heroCaption = if (hero != null) "spent this month" else null,
                ) {
                    items(60) { index ->
                        Box(Modifier.fillMaxWidth().height(56.dp).testTag("item-$index")) { Text("Row $index") }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
    }

    private fun swipeListUp() {
        val pixels = 400f * density.density
        tag(HeaderTags.LIST).performTouchInput { swipeUp(startY = bottom - 40f, endY = bottom - 40f - pixels) }
        frames(120)
    }

    /** Drags the list up by [distanceDp] and holds (no release), leaving the header part-way collapsed. */
    private fun dragListAndHold(distanceDp: Float) {
        val pixels = distanceDp * density.density
        tag(HeaderTags.LIST).performTouchInput {
            down(center)
            val total = pixels + viewConfiguration.touchSlop
            repeat(STEPS) { moveBy(Offset(0f, -total / STEPS), delayMillis = 100) }
        }
        frames(2)
    }

    /** Moves the held finger [distanceDp] further up, one separate gesture step, and lets the frames run. */
    private fun moveHeldFingerUp(distanceDp: Float) {
        val pixels = distanceDp * density.density
        tag(HeaderTags.LIST).performTouchInput { moveBy(Offset(0f, -pixels), delayMillis = 100) }
        frames(2)
    }

    /** The colour the real draw pass put at ([x], [y]) in window pixels. */
    private fun pixelAt(
        x: Int,
        y: Int,
    ): Color {
        var argb = 0
        compose.runOnUiThread { argb = composeView!!.drawToBitmap().getPixel(x, y) }
        return Color(argb)
    }

    private fun headerWidthPx() = tag(HeaderTags.HEADER).bounds().width.toInt()

    /** The top-centre pixel of the header, two rows down so the very first row's anti-aliasing is out of it. */
    private fun topCentre() = pixelAt(headerWidthPx() / 2, 2)

    private fun assertNear(
        expected: Color,
        actual: Color,
        message: String,
    ) {
        val tolerance = 4 / 255f
        val near =
            abs(expected.red - actual.red) <= tolerance &&
                abs(expected.green - actual.green) <= tolerance &&
                abs(expected.blue - actual.blue) <= tolerance
        assertTrue("$message: expected about $expected, was $actual", near)
    }

    @Test
    fun `glow on, an expanded Dashboard header is tinted at its top-centre`() {
        show(HeaderStyle.Dashboard, glowOn, sampleHero)
        val pixel = topCentre()
        assertNotEquals(canvas, pixel)
        assertNear(washTop, pixel, "top-centre")
    }

    private fun assertHeroRowIsCanvas(expectedCanvas: Color) {
        show(HeaderStyle.Dashboard, glowOn, sampleHero)
        val heroTop = tag(HeaderTags.HERO).bounds().top.toInt()
        val header = tag(HeaderTags.HEADER).bounds()
        val row = (header.left.toInt() until header.right.toInt()).map { x -> pixelAt(x, heroTop) }
        assertTrue("hero row is ${row.size} pixels wide", row.size > 1000)
        assertEquals("every hero-row pixel is the canvas", setOf(expectedCanvas), row.toSet())
        // The glow really is on: the top-centre pixel is not the canvas.
        assertNotEquals(expectedCanvas, topCentre())
    }

    @Test
    fun `glow on, every pixel on the hero's top edge is plain canvas, across the whole width`() =
        assertHeroRowIsCanvas(canvas)

    @Test
    @Config(qualifiers = "+night")
    fun `glow on in dark, every pixel on the hero's top edge is plain canvas, across the whole width`() =
        assertHeroRowIsCanvas(GraphitePalette.dark.canvas)

    @Test
    fun `glow on, the tint fades with depth and is gone by the hero`() {
        show(HeaderStyle.Dashboard, glowOn, sampleHero)
        val x = headerWidthPx() / 2
        val heroTop = tag(HeaderTags.HERO).bounds().top.toInt()
        val shallow = pixelAt(x, heroTop / 8)
        val deep = pixelAt(x, heroTop / 2)
        // In light Graphite the wash is darker than the canvas, so a stronger tint means a lower red.
        assertTrue("shallow $shallow is not between the top tint $washTop and the canvas", shallow.red > washTop.red)
        assertTrue("deep $deep is not nearer the canvas than shallow $shallow", deep.red > shallow.red)
        assertTrue("deep $deep is past the canvas", deep.red <= canvas.red)
    }

    @Test
    fun `glow on, a collapsed header's top-centre pixel is plain canvas`() {
        show(HeaderStyle.Dashboard, glowOn, sampleHero)
        swipeListUp()
        assertEquals(barHeight, tag(HeaderTags.HEADER).bounds().height / density.density, 0.5f)
        assertEquals(canvas, topCentre())
    }

    @Test
    fun `glow on, half way through the collapse the tint is weaker than expanded and stronger than collapsed`() {
        show(HeaderStyle.Dashboard, glowOn, sampleHero)
        val expanded = topCentre()
        dragListAndHold((expandedHeight - barHeight) / 2f)
        val half = topCentre()
        assertTrue("half-collapsed $half is as tinted as expanded $expanded", half.red > expanded.red)
        assertTrue("half-collapsed $half is plain canvas", half.red < canvas.red)
        tag(HeaderTags.LIST).performTouchInput { up() }
    }

    @Test
    fun `glow off, the top-centre pixel is plain canvas`() {
        show(HeaderStyle.Dashboard, glowOff, sampleHero)
        assertEquals(canvas, topCentre())
    }

    @Test
    fun `glow on, a Standard header is tinted at the top and plain canvas below 60 percent of its height`() {
        show(HeaderStyle.Standard, glowOn)
        assertNear(washTop, topCentre(), "top-centre")
        val header = tag(HeaderTags.HEADER).bounds()
        val belowGlow = (header.height * 0.6f).toInt() + 2
        // Across the width, clear of the title in the middle: the glow is a circle, far out of it here.
        val row = (0 until header.right.toInt() step 8).map { x -> pixelAt(x, belowGlow) }
        assertEquals(setOf(canvas), row.toSet())
    }

    /**
     * How many recompositions the recomposer has run so far. Slot lambdas cannot be probed: the
     * header's parts are skippable, so a probe inside one never reruns when the header body does.
     */
    private fun recompositions() = Recomposer.runningRecomposers.value.sumOf { it.changeCount }

    @Test
    fun `scrolling a glowing header redraws the glow but never recomposes anything`() {
        show(HeaderStyle.Dashboard, glowOn, sampleHero)
        val atRest = recompositions()
        val restingTint = topCentre()

        // Leaving the expanded phase swaps which parts exist (the title and the lower band), so it
        // recomposes once. That also proves the probe is live.
        dragListAndHold(20f)
        val inMotion = recompositions()
        assertTrue("the probe never counted the phase change ($atRest, then $inMotion)", inMotion > atRest)

        val tints = mutableListOf(topCentre())
        val heights = mutableListOf(tag(HeaderTags.HEADER).bounds().height)
        repeat(4) {
            moveHeldFingerUp(30f)
            tints += topCentre()
            heights += tag(HeaderTags.HEADER).bounds().height
        }
        assertEquals("scroll frames recomposed the glowing header", inMotion, recompositions())

        // The header really did move and the glow really was redrawn, each step weaker than the last.
        assertTrue("header did not shrink: $heights", heights.zipWithNext().all { (a, b) -> b < a })
        assertTrue("tint did not fade: $restingTint, $tints", tints.zipWithNext().all { (a, b) -> b.red >= a.red })
        assertTrue("tint did not weaken overall: $restingTint, $tints", tints.last().red > restingTint.red)
        tag(HeaderTags.LIST).performTouchInput { up() }
    }

    private companion object {
        const val STEPS = 10
    }
}
