package dev.imagh.pft.app.ui.common

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.moveBy
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.up
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.HeroSizeSteps
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
 * `CollapsingHeaderScaffold` through real nested scroll on a 393x851dp window (plan §3.4, §5.3,
 * UI-3, UI-7), with the clock stepped by hand. Geometry is read from semantics bounds in dp. Native
 * graphics (`@GraphicsMode(NATIVE)`) gives text its real width: with the legacy mode every glyph is
 * about a pixel wide, which would hide wrapping at large font scales.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class CollapsingHeaderScaffoldTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var density = Density(1f)
    private val windowWidth = 393f
    private val windowHeight = 851f
    private val barHeight = 64f
    private val expandedHeight = 851f * 0.38f

    private fun Float.dp() = this / density.density

    private fun Rect.inDp() = Rect(left.dp(), top.dp(), right.dp(), bottom.dp())

    private fun SemanticsNodeInteraction.bounds() = fetchSemanticsNode().boundsInRoot.inDp()

    private fun tag(tag: String) = compose.onNodeWithTag(tag)

    private fun frames(count: Int) =
        repeat(count) {
            compose.mainClock.advanceTimeByFrame()
            compose.waitForIdle()
        }

    private fun settle() = frames(120)

    private fun headerHeight() = tag(HeaderTags.HEADER).bounds().height

    private fun show(
        style: HeaderStyle = HeaderStyle.Standard,
        itemCount: Int = 60,
        subtitle: String? = null,
        hero: (@Composable () -> Unit)? = null,
        period: HeaderPeriod? = null,
        captioned: Boolean = true,
        fontScale: Float = 1f,
        actions: @Composable RowScope.() -> Unit = {},
    ) {
        compose.setContent {
            density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    CollapsingHeaderScaffold(
                        title = "Budgets",
                        style = style,
                        subtitle = subtitle,
                        hero = hero,
                        heroCaption = if (hero != null && captioned) "spent this month" else null,
                        period = period,
                        actions = actions,
                    ) {
                        items(itemCount) { index ->
                            Box(Modifier.fillMaxWidth().height(56.dp).testTag("item-$index")) { Text("Row $index") }
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.mainClock.autoAdvance = false
    }

    private val sampleHero: @Composable () -> Unit = { Text("₹48,250", Modifier.testTag("hero-text")) }

    private fun swipeListUp() {
        val pixels = 400f * density.density
        tag(HeaderTags.LIST).performTouchInput { swipeUp(startY = bottom - 40f, endY = bottom - 40f - pixels) }
        settle()
    }

    private fun dragList(
        distanceDp: Float,
        release: Boolean = true,
    ) {
        val pixels = distanceDp * density.density
        tag(HeaderTags.LIST).performTouchInput {
            down(center)
            val total = pixels + viewConfiguration.touchSlop
            val steps = 10
            repeat(steps) {
                moveBy(Offset(0f, -total / steps), delayMillis = 100)
            }
            if (release) {
                advanceEventTime(300) // hold still, so the release carries no fling velocity
                up()
            }
        }
        frames(2)
    }

    // --- expanded and collapsed -----------------------------------------------------------

    @Test
    fun `expanded, content starts at least 35 percent down and the large title is shown`() {
        show()
        assertTrue(tag("item-0").bounds().top >= windowHeight * 0.35f)
        assertEquals(expandedHeight, headerHeight(), 1f)
        tag(HeaderTags.TITLE_LARGE).assertIsDisplayed()
        tag(HeaderTags.TITLE_BAR).assertDoesNotExist()
    }

    @Test
    fun `swiping the content up 400dp collapses the header to a 64dp bar showing the small title`() {
        show()
        swipeListUp()
        assertEquals(barHeight, headerHeight(), 0.5f)
        tag(HeaderTags.TITLE_BAR).assertIsDisplayed()
        tag(HeaderTags.TITLE_LARGE).assertDoesNotExist()
    }

    @Test
    fun `collapsing the header pulls the first row up to sit directly under the bar`() {
        show()
        dragList(expandedHeight - barHeight)
        settle()
        assertEquals(barHeight, headerHeight(), 0.5f)
        assertEquals(barHeight, tag("item-0").bounds().top, 1f)
    }

    // --- snap -----------------------------------------------------------------------------

    @Test
    fun `dragging 30 percent of the collapse range and releasing settles back to expanded`() {
        show()
        val range = expandedHeight - barHeight
        dragList(range * 0.30f)
        assertTrue("mid-drag the header is partly collapsed", headerHeight() < expandedHeight - 1f)
        settle()
        assertEquals(expandedHeight, headerHeight(), 0.5f)
    }

    @Test
    fun `dragging 70 percent of the collapse range and releasing settles collapsed`() {
        show()
        val range = expandedHeight - barHeight
        dragList(range * 0.70f)
        assertTrue("mid-drag the header is partly collapsed", headerHeight() > barHeight + 1f)
        settle()
        assertEquals(barHeight, headerHeight(), 0.5f)
    }

    @Test
    fun `half way through a collapse both titles are on screen and cross-fade`() {
        show()
        dragList((expandedHeight - barHeight) * 0.5f, release = false)
        tag(HeaderTags.TITLE_LARGE).assertExists()
        tag(HeaderTags.TITLE_BAR).assertExists()
    }

    @Test
    fun `content shorter than the viewport keeps the header expanded after a swipe`() {
        show(itemCount = 3)
        tag(HeaderTags.LIST).performTouchInput { swipeUp() }
        settle()
        assertEquals(expandedHeight, headerHeight(), 0.5f)
        tag(HeaderTags.TITLE_LARGE).assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w393dp-h500dp-xxhdpi")
    fun `in a window shorter than 560dp the header starts collapsed and a swipe down does not expand it`() {
        show()
        assertEquals(barHeight, headerHeight(), 0.5f)
        tag(HeaderTags.TITLE_BAR).assertIsDisplayed()
        tag(HeaderTags.TITLE_LARGE).assertDoesNotExist()

        tag(HeaderTags.LIST).performTouchInput { swipeDown() }
        settle()
        assertEquals(barHeight, headerHeight(), 0.5f)
        tag(HeaderTags.TITLE_BAR).assertIsDisplayed()
    }

    // --- type (UI-3, Q10) -----------------------------------------------------------------

    private fun SemanticsNodeInteraction.textStyleFamily(): FontFamily? {
        val results = mutableListOf<TextLayoutResult>()
        fetchSemanticsNode()
            .config
            .getOrNull(SemanticsActions.GetTextLayoutResult)
            ?.action
            ?.invoke(results)
        return results
            .single()
            .layoutInput.style.fontFamily
    }

    private fun SemanticsNodeInteraction.fontSizeSp(): Float {
        val results = mutableListOf<TextLayoutResult>()
        fetchSemanticsNode()
            .config
            .getOrNull(SemanticsActions.GetTextLayoutResult)
            ?.action
            ?.invoke(results)
        return results
            .single()
            .layoutInput.style.fontSize.value
    }

    private fun FontFamily?.isNewsreader() =
        (this as? FontListFontFamily)?.fonts.orEmpty().let { fonts ->
            fonts.isNotEmpty() && fonts.all { it is ResourceFont && it.resId == R.font.newsreader }
        }

    @Test
    fun `on a Dashboard header the hero uses the display family and the title uses the text family`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero)
        assertTrue(tag("hero-text").textStyleFamily().isNewsreader())
        assertEquals(FontFamily.Default, tag(HeaderTags.TITLE_LARGE).textStyleFamily())
    }

    @Test
    fun `a Dashboard title is titleLarge`() {
        show(style = HeaderStyle.Dashboard)
        assertEquals(22f, tag(HeaderTags.TITLE_LARGE).fontSizeSp(), 0f)
    }

    @Test
    fun `a Standard title is headlineLarge`() {
        show(style = HeaderStyle.Standard)
        assertEquals(32f, tag(HeaderTags.TITLE_LARGE).fontSizeSp(), 0f)
    }

    @Test
    fun `every title and the subtitle use the sans text family, never the serif (Q10)`() {
        show(style = HeaderStyle.Standard, subtitle = "October - 3 days left")
        assertEquals(FontFamily.Default, tag(HeaderTags.TITLE_LARGE).textStyleFamily())
        assertEquals(FontFamily.Default, tag(HeaderTags.SUBTITLE).textStyleFamily())
        swipeListUp()
        assertEquals(FontFamily.Default, tag(HeaderTags.TITLE_BAR).textStyleFamily())
        assertEquals(22f, tag(HeaderTags.TITLE_BAR).fontSizeSp(), 0f)
    }

    @Test
    fun `the collapsed bar title is sans on a Dashboard header too`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero)
        swipeListUp()
        assertEquals(FontFamily.Default, tag(HeaderTags.TITLE_BAR).textStyleFamily())
    }

    // --- header layout rule (Q10) ---------------------------------------------------------

    private fun assertCentredAndReadOnly(tag: String) {
        val bounds = tag(tag).bounds()
        assertEquals(windowWidth / 2f, bounds.center.x, 1f)
        tag(tag).assertHasNoClickAction()
    }

    @Test
    fun `Standard expanded, the title is centred horizontally, read-only and in the middle of the header`() {
        show(style = HeaderStyle.Standard)
        assertCentredAndReadOnly(HeaderTags.TITLE_LARGE)
        assertEquals(expandedHeight / 2f, tag(HeaderTags.TITLE_LARGE).bounds().center.y, 2f)
    }

    @Test
    fun `Standard expanded, an optional subtitle sits under the title`() {
        show(style = HeaderStyle.Standard, subtitle = "October - 3 days left")
        val title = tag(HeaderTags.TITLE_LARGE).bounds()
        val subtitle = tag(HeaderTags.SUBTITLE).bounds()
        assertTrue("subtitle top ${subtitle.top} below title bottom ${title.bottom}", subtitle.top >= title.bottom - 1f)
        assertEquals(windowWidth / 2f, subtitle.center.x, 1f)
    }

    @Test
    fun `Dashboard expanded, the title is centred and read-only, about 12 percent down`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = HeaderPeriod({}, {}))
        assertCentredAndReadOnly(HeaderTags.TITLE_LARGE)
        assertEquals(expandedHeight * 0.12f, tag(HeaderTags.TITLE_LARGE).bounds().top, 2f)
    }

    @Test
    fun `Dashboard expanded, the title ends at least 24dp above the hero`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = HeaderPeriod({}, {}))
        val gap = tag(HeaderTags.HERO).bounds().top - tag(HeaderTags.TITLE_LARGE).bounds().bottom
        assertTrue("gap was $gap", gap >= 24f)
    }

    @Test
    fun `Dashboard expanded at 200 percent font scale with a subtitle, the title still ends 24dp above the hero`() {
        show(
            style = HeaderStyle.Dashboard,
            subtitle = "October - 3 days left",
            hero = sampleHero,
            period = HeaderPeriod({}, {}),
            fontScale = 2f,
        )
        val gap = tag(HeaderTags.HERO).bounds().top - tag(HeaderTags.SUBTITLE).bounds().bottom
        assertTrue("gap was $gap", gap >= 24f)
        assertTrue(tag(HeaderTags.HERO).bounds().top - tag(HeaderTags.TITLE_LARGE).bounds().bottom >= 24f)
    }

    /** The real ledger maximum (Rs 1e11) as a hero `MoneyText`: the longest figure a hero can ever show. */
    private val longHero: @Composable () -> Unit = {
        MoneyText(Paise.MAX_AMOUNT, MoneySemantic.Expense, Modifier.testTag("hero-text"), size = MoneySize.Hero)
    }

    private fun assertLongHeroShrinksToFit() {
        val hero = tag("hero-text")
        val result = hero.layout()
        result.assertShownInFull()
        val size = result.layoutInput.style.fontSize.value
        assertTrue("hero was not shrunk: $size", size < 48f)
        assertTrue("size $size is not one of the steps", size in HeroSizeSteps.map { it.value })
        val gutter = 20f
        val bounds = hero.bounds()
        val width = tag(HeaderTags.HEADER).bounds().width
        assertTrue("hero ${bounds.right} runs past the right gutter", bounds.right <= width - gutter + 0.5f)
        assertTrue("hero ${bounds.left} starts before the left gutter", bounds.left >= gutter - 0.5f)
        // The title still ends 24dp above the hero, and the stepper is still below it.
        assertTrue(bounds.top - tag(HeaderTags.TITLE_LARGE).bounds().bottom >= 24f)
        assertTrue(bounds.bottom <= tag(HeaderTags.PERIOD_STEPPER).bounds().top + 1f)
    }

    @Test
    fun `Dashboard expanded at 200 percent font scale, the longest possible hero shrinks to fit the header`() {
        show(
            style = HeaderStyle.Dashboard,
            subtitle = "October - 3 days left",
            hero = longHero,
            period = HeaderPeriod({}, {}),
            fontScale = 2f,
        )
        assertLongHeroShrinksToFit()
    }

    @Test
    @Config(qualifiers = "w320dp-h851dp-xxhdpi")
    fun `on a 320dp window at 200 percent font scale the longest possible hero still shrinks to fit`() {
        show(style = HeaderStyle.Dashboard, hero = longHero, period = HeaderPeriod({}, {}), fontScale = 2f)
        assertLongHeroShrinksToFit()
    }

    @Test
    fun `Dashboard expanded, the hero, caption and period stepper are in the lower half of the header`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = HeaderPeriod({}, {}))
        val stepper = tag(HeaderTags.PERIOD_STEPPER).bounds()
        assertTrue(
            "stepper bottom ${stepper.bottom}",
            stepper.bottom > expandedHeight / 2f && stepper.bottom <= expandedHeight,
        )
        assertTrue(tag(HeaderTags.HERO).bounds().top > expandedHeight / 2f - 40f)
        assertTrue(tag(HeaderTags.HERO).bounds().bottom <= stepper.top + 1f)
        compose.onNodeWithText("spent this month").assertIsDisplayed()
    }

    @Test
    fun `Dashboard collapsed, the bar shows the title and the hero is gone`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = HeaderPeriod({}, {}))
        swipeListUp()
        assertEquals(barHeight, headerHeight(), 0.5f)
        assertCentredAndReadOnly(HeaderTags.TITLE_BAR)
        tag(HeaderTags.HERO).assertDoesNotExist()
        tag(HeaderTags.PERIOD_STEPPER).assertDoesNotExist()
        assertTrue(tag(HeaderTags.TITLE_BAR).bounds().bottom <= barHeight)
    }

    @Test
    fun `Standard collapsed, the bar shows the title centred`() {
        show(style = HeaderStyle.Standard)
        swipeListUp()
        assertCentredAndReadOnly(HeaderTags.TITLE_BAR)
    }

    @Test
    fun `a hero on a Standard header is not shown`() {
        show(style = HeaderStyle.Standard, hero = sampleHero)
        tag(HeaderTags.HERO).assertDoesNotExist()
        tag(HeaderTags.LOWER_BAND).assertDoesNotExist()
    }

    @Test
    fun `actions sit in the top row and stay when collapsed`() {
        show(actions = { IconButton(onClick = {}, Modifier.testTag("search")) { Text("S") } })
        assertTrue(tag("search").bounds().bottom <= barHeight + 1f)
        swipeListUp()
        assertTrue(tag("search").bounds().bottom <= barHeight + 1f)
        tag("search").assertIsDisplayed()
    }

    // --- period stepping ------------------------------------------------------------------

    private class Steps {
        var previous by mutableIntStateOf(0)
        var next by mutableIntStateOf(0)
        val period = HeaderPeriod(onPrevious = { previous++ }, onNext = { next++ })
    }

    @Test
    fun `swiping right on a Dashboard header steps to the previous period once`() {
        val steps = Steps()
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = steps.period)
        tag(HeaderTags.HEADER).performTouchInput { swipeRight() }
        frames(2)
        assertEquals(1, steps.previous)
        assertEquals(0, steps.next)
    }

    @Test
    fun `swiping left on a Dashboard header steps to the next period once`() {
        val steps = Steps()
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = steps.period)
        tag(HeaderTags.HEADER).performTouchInput { swipeLeft() }
        frames(2)
        assertEquals(0, steps.previous)
        assertEquals(1, steps.next)
    }

    @Test
    fun `a vertical drag on the header does not step the period`() {
        val steps = Steps()
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = steps.period)
        tag(HeaderTags.HEADER).performTouchInput { swipeUp() }
        frames(2)
        assertEquals(0, steps.previous)
        assertEquals(0, steps.next)
    }

    @Test
    fun `a collapsed header does not step the period on a sideways swipe`() {
        val steps = Steps()
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = steps.period)
        swipeListUp()
        tag(HeaderTags.HEADER).performTouchInput { swipeLeft() }
        frames(2)
        assertEquals(0, steps.next)
        assertEquals(0, steps.previous)
    }

    @Test
    fun `a Dashboard hero without a caption or period shows no caption row`() {
        show(style = HeaderStyle.Dashboard, hero = { Text("₹1", Modifier.testTag("hero-text")) }, captioned = false)
        tag(HeaderTags.HERO).assertIsDisplayed()
        tag(HeaderTags.PERIOD_STEPPER).assertDoesNotExist()
    }

    @Test
    fun `a period without a hero draws no stepper and a swipe does not step it`() {
        val steps = Steps()
        show(style = HeaderStyle.Dashboard, period = steps.period)
        tag(HeaderTags.PERIOD_STEPPER).assertDoesNotExist()
        tag(HeaderTags.HEADER).performTouchInput { swipeLeft() }
        frames(2)
        assertEquals(0, steps.next)
    }

    @Test
    fun `the stepper buttons step the period`() {
        val steps = Steps()
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = steps.period)
        compose.onNodeWithContentDescription("Previous period").performClick()
        compose.onNodeWithContentDescription("Next period").performClick()
        compose.onNodeWithContentDescription("Next period").performClick()
        assertEquals(1, steps.previous)
        assertEquals(2, steps.next)
    }

    private val isImage = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image)
    private val hasTextNode = SemanticsMatcher.keyIsDefined(SemanticsProperties.Text)

    private fun SemanticsNodeInteractionCollection.contentDescriptions() =
        fetchSemanticsNodes().map { it.config.getOrNull(SemanticsProperties.ContentDescription)?.single() }

    private fun SemanticsNodeInteractionCollection.assertTouchTargetsAtLeast48dp() =
        fetchSemanticsNodes().forEach { node ->
            val touch = node.touchBoundsInRoot.inDp()
            assertTrue(
                "touch target is ${touch.width} x ${touch.height}dp",
                touch.width >= 47.5f && touch.height >= 47.5f,
            )
        }

    @Test
    fun `the period stepper buttons are 48dp symbols named Previous period and Next period, with no text`() {
        show(style = HeaderStyle.Dashboard, hero = sampleHero, period = HeaderPeriod({}, {}))
        val inStepper = hasAnyAncestor(hasTestTag(HeaderTags.PERIOD_STEPPER))

        val buttons = compose.onAllNodes(hasClickAction() and inStepper)
        assertEquals(listOf("Previous period", "Next period"), buttons.contentDescriptions())
        buttons.assertTouchTargetsAtLeast48dp()
        // A symbol is an Icon (an Image node); a text glyph is not.
        compose.onAllNodes(isImage and inStepper, useUnmergedTree = true).assertCountEquals(2)
        compose.onAllNodes(hasTextNode and inStepper, useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun `a horizontal swipe does nothing without a period, and not on a Standard header`() {
        val steps = Steps()
        show(style = HeaderStyle.Standard, period = steps.period)
        tag(HeaderTags.HEADER).performTouchInput { swipeLeft() }
        frames(2)
        assertEquals(0, steps.next)
        tag(HeaderTags.PERIOD_STEPPER).assertDoesNotExist()
    }

    // --- slots ----------------------------------------------------------------------------

    @Test
    fun `the bottom bar and the floating action button are shown and the last row is not hidden by the bar`() {
        compose.setContent {
            density = LocalDensity.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                CollapsingHeaderScaffold(
                    title = "Budgets",
                    bottomBar = { BottomActionBar(primary = BottomBarAction("Save") {}) },
                    floatingActionButton = { Text("FAB", Modifier.testTag("fab")) },
                ) {
                    items(listOf("a", "b", "c")) { Text(it, Modifier.testTag("row-$it")) }
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Save").assertIsDisplayed()
        tag("fab").assertIsDisplayed()
        tag("row-c").assertIsDisplayed()
        assertTrue(tag("row-c").bounds().bottom <= compose.onNodeWithText("Save").bounds().top)
    }

    // --- window insets (R1) ---------------------------------------------------------------

    private lateinit var hostView: View

    /** Dispatches [dp] of bottom inset of the given [type] to the Compose host, as the system would. */
    private fun dispatchBottomInset(
        type: Int,
        dp: Float,
    ) {
        val px = (dp * density.density).toInt()
        val insets =
            WindowInsetsCompat
                .Builder()
                .setInsets(
                    type,
                    androidx.core.graphics.Insets
                        .of(0, 0, 0, px),
                ).setVisible(type, true)
                .build()
        compose.runOnUiThread {
            androidx.core.view.ViewCompat
                .dispatchApplyWindowInsets(hostView, insets)
        }
        compose.waitForIdle()
    }

    private fun showWithInsetsHost(
        bottomBar: (@Composable () -> Unit)?,
        fab: Boolean = false,
    ) {
        compose.setContent {
            density = LocalDensity.current
            hostView = LocalView.current
            PftTheme(ThemePrefs(dynamicColor = false)) {
                CollapsingHeaderScaffold(
                    title = "Budgets",
                    bottomBar = bottomBar,
                    floatingActionButton = { if (fab) Text("FAB", Modifier.testTag("fab")) },
                ) {
                    items(
                        60,
                    ) { index ->
                        Box(
                            Modifier.fillMaxWidth().height(56.dp).testTag("item-$index"),
                        ) { Text("Row $index") }
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun `with no bottom bar the navigation bar inset keeps the last row and the FAB clear of the bar`() {
        showWithInsetsHost(bottomBar = null, fab = true)
        dispatchBottomInset(
            WindowInsetsCompat.Type
                .navigationBars(),
            dp = 48f,
        )

        tag(HeaderTags.LIST).performScrollToIndex(59)
        compose.waitForIdle()
        assertTrue(
            "last row bottom ${tag("item-59").bounds().bottom}",
            tag("item-59").bounds().bottom <= windowHeight - 48f + 0.5f,
        )
        assertTrue("FAB bottom ${tag("fab").bounds().bottom}", tag("fab").bounds().bottom <= windowHeight - 48f + 0.5f)
    }

    @Test
    fun `a bottom bar rides above the keyboard`() {
        showWithInsetsHost(bottomBar = { BottomActionBar(primary = BottomBarAction("Save") {}) })
        dispatchBottomInset(
            WindowInsetsCompat.Type
                .ime(),
            dp = 300f,
        )

        val save = compose.onNodeWithText("Save").bounds()
        assertTrue("Save bottom ${save.bottom}", save.bottom <= windowHeight - 300f + 0.5f)
    }
}
