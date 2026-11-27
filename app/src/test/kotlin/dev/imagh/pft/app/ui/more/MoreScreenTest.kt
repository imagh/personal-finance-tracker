package dev.imagh.pft.app.ui.more

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.common.HeaderTags
import dev.imagh.pft.app.ui.common.LocalTabBarClearance
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The More tab's root (Cobalt plan D8): a titled list of Categories and Tags (Accounts is a tab of its
 * own), no Back button, and the attribution line "<name> · <phone>" as the header subtitle once the
 * identity has loaded. Rendered from fake state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class MoreScreenTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val taps = mutableListOf<String>()

    private companion object {
        val CLEARANCE = 220.dp
        const val WINDOW_HEIGHT = 400f
    }

    private var density = 1f

    private fun show(
        state: MoreUiState = MoreUiState(userName = "Akash", deviceLabel = "Pixel 8", loaded = true),
        tabBarClearance: Dp = 0.dp,
        fontScale: Float = 1f,
    ) = compose.setContent {
        CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                density = LocalDensity.current.density
                CompositionLocalProvider(LocalTabBarClearance provides tabBarClearance) {
                    MoreScreen(
                        state,
                        onOpenCategories = { taps += "categories" },
                        onOpenTags = { taps += "tags" },
                    )
                }
            }
        }
    }

    @Test
    fun `it shows the More title and a Categories row that opens categories`() {
        show()

        compose.onAllNodesWithText("More").fetchSemanticsNodes().isNotEmpty() shouldBe true
        compose.onNodeWithText("Categories").assertIsDisplayed().performClick()

        taps shouldBe listOf("categories")
    }

    @Test
    fun `it has a Tags row that opens tags, below Categories`() {
        show()

        val categories =
            compose
                .onNodeWithText("Categories")
                .fetchSemanticsNode()
                .boundsInRoot.top
        val tags =
            compose
                .onNodeWithText("Tags")
                .assertIsDisplayed()
                .fetchSemanticsNode()
                .boundsInRoot.top
        (tags > categories) shouldBe true

        compose.onNodeWithText("Tags").performClick()

        taps shouldBe listOf("tags")
    }

    @Test
    @Config(qualifiers = "w393dp-h400dp-xxhdpi")
    fun `the last row scrolls clear of the tab bar's clearance, which the bar would otherwise cover`() {
        show(tabBarClearance = CLEARANCE, fontScale = 2f)
        // Swipe up twice: the list scrolls as far as it can, which is the end (or nowhere, if it cannot).
        repeat(2) { compose.onNodeWithTag(HeaderTags.LIST).performTouchInput { swipeUp() } }
        compose.waitForIdle()

        val bottom =
            compose
                .onNodeWithText("Tags")
                .fetchSemanticsNode()
                .boundsInRoot.bottom
        val barTop = (WINDOW_HEIGHT - CLEARANCE.value) * density
        assertTrue("the Tags row ends at $bottom, under the bar that starts at $barTop", bottom <= barTop + 1f)
    }

    @Test
    fun `there is no Accounts row, because Accounts is a tab`() {
        show()

        compose.onAllNodesWithText("Accounts").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `there is no Back button, because More is a tab root`() {
        show()

        compose.onAllNodesWithContentDescription("Back").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `a loaded identity shows the attribution line as the subtitle`() {
        show()

        compose.onNodeWithText("Akash · Pixel 8").assertIsDisplayed()
    }

    @Test
    fun `before the identity is read there is no subtitle`() {
        show(MoreUiState())

        compose.onAllNodesWithText("·", substring = true).fetchSemanticsNodes().size shouldBe 0
        compose.onNodeWithText("Categories").assertIsDisplayed()
    }
}
