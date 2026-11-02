package dev.imagh.pft.app.ui.common

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.view.drawToBitmap
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.BackgroundStyle
import dev.imagh.pft.app.ui.theme.IvoryPalette
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePalette
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** What colour `BottomActionBar` paints behind its buttons (DS6c): the canvas when TONAL, the surface when FLAT. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class BottomActionBarSurfaceTest {
    @get:Rule
    val compose = HostedComposeRule()

    private var composeView: View? = null

    /** The bar's colour in the gutter left of its button, read from the real draw pass. */
    private fun barPixel(style: BackgroundStyle): Color {
        compose.setContent {
            composeView = LocalView.current
            PftTheme(ThemePrefs(lightPalette = ThemePalette.IVORY, background = style)) {
                Box(Modifier.fillMaxSize()) {
                    BottomActionBar(
                        primary = BottomBarAction("Save") {},
                        modifier = Modifier.align(Alignment.BottomCenter).testTag("bar"),
                    )
                }
            }
        }
        compose.waitForIdle()
        val bar = compose.onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
        var argb = 0
        compose.runOnUiThread {
            argb = composeView!!.drawToBitmap().getPixel((bar.left + 4f).toInt(), bar.center.y.toInt())
        }
        return Color(argb)
    }

    @Test
    fun `TONAL paints the bar with the canvas`() {
        assertEquals(IvoryPalette.light.canvas, barPixel(BackgroundStyle.TONAL))
    }

    @Test
    fun `FLAT paints the bar with the surface`() {
        assertEquals(IvoryPalette.light.scheme.surface, barPixel(BackgroundStyle.FLAT))
    }
}
