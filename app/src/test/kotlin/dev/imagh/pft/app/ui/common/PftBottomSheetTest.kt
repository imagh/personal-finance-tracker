package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.sqrt

/**
 * [PftBottomSheet] in a real compose host: it shows its title (as a heading) and content, Back
 * dismisses it, the content's `close` hides it and reports the dismissal once, and it is drawn with
 * the theme's extra-large corner radius (read from the pixels, not from the declared shape).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class PftBottomSheetTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var open by mutableStateOf(true)
    private var dismissals = 0
    private var themeRadiusPx = 0f

    private fun show(content: @Composable (close: () -> Unit) -> Unit = { Text("Sheet body") }) =
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                val density = LocalDensity.current
                themeRadiusPx =
                    (MaterialTheme.shapes.extraLarge as CornerBasedShape).topStart.toPx(Size(1_000f, 1_000f), density)
                Box(Modifier.fillMaxSize())
                if (open) {
                    PftBottomSheet(
                        title = "Pick one",
                        onDismiss = {
                            dismissals++
                            open = false
                        },
                    ) { close -> content(close) }
                }
            }
        }

    @Test
    fun `the sheet shows its title as a heading above its content`() {
        show()

        compose.onNodeWithText("Pick one").assertIsDisplayed()
        compose.onNodeWithText("Sheet body").assertIsDisplayed()
        compose.onNodeWithText("Pick one").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    }

    @Test
    fun `Back dismisses the sheet`() {
        show()

        Espresso.pressBack()
        compose.waitForIdle()

        compose.onNodeWithText("Sheet body").assertDoesNotExist()
        dismissals shouldBe 1
    }

    @Test
    fun `closing from the content hides the sheet and reports one dismissal`() {
        show { close -> TextButton(onClick = close) { Text("Done") } }

        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        compose.onNodeWithText("Done").assertDoesNotExist()
        dismissals shouldBe 1
    }

    @Test
    fun `closing twice while the sheet is still sliding out reports one dismissal`() {
        show { close -> TextButton(onClick = close) { Text("Done") } }

        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Done").performClick()
        compose.onNodeWithText("Done").performClick()
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()

        compose.onNodeWithText("Done").assertDoesNotExist()
        dismissals shouldBe 1
    }

    @Test
    fun `the sheet's top corners use the theme's extra-large radius`() {
        show()
        compose.waitForIdle()

        val bitmap = compose.drawSheetWindow(containing = "Sheet body")
        val opaque = { x: Int, y: Int -> (bitmap.getPixel(x, y) ushr ALPHA_SHIFT) == OPAQUE }

        // The sheet starts at the first opaque row down the middle; above it only the scrim shows.
        val top = (0 until bitmap.height).first { opaque(bitmap.width / 2, it) }
        val radius = themeRadiusPx
        val rowsBelowTop = ROWS_BELOW_TOP
        // Where a circle of that radius puts the sheet's left edge, rowsBelowTop pixels below the top.
        val edge = (radius - sqrt(radius * radius - (radius - rowsBelowTop) * (radius - rowsBelowTop))).toInt()

        opaque(edge - MARGIN_PX, top + rowsBelowTop) shouldBe false
        opaque(edge + MARGIN_PX, top + rowsBelowTop) shouldBe true
    }

    private companion object {
        const val ALPHA_SHIFT = 24
        const val OPAQUE = 0xFF
        const val ROWS_BELOW_TOP = 10
        const val MARGIN_PX = 6
    }
}
