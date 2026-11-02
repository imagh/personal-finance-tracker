package dev.imagh.pft.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** `SyncWindowBackground` on a real launched activity's window (UI-18, plan §3.3). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class WindowBackgroundTest {
    @get:Rule
    val compose = HostedComposeRule()

    private tailrec fun Context.activity(): Activity =
        if (this is Activity) this else (this as ContextWrapper).baseContext.activity()

    private fun Window.backgroundColour(): Int {
        val background = decorView.background
        assertTrue("the window background is $background, not a ColorDrawable", background is ColorDrawable)
        return (background as ColorDrawable).color
    }

    @Test
    fun `the window follows the canvas - FLAT paints the surface, TONAL the canvas`() {
        var prefs by mutableStateOf(ThemePrefs(lightPalette = ThemePalette.IVORY, background = BackgroundStyle.FLAT))
        lateinit var window: Window
        compose.setContent {
            window = LocalContext.current.activity().window
            PftTheme(prefs) { SyncWindowBackground(window) }
        }
        compose.waitForIdle()
        assertEquals(
            IvoryPalette.light.scheme.surface
                .toArgb(),
            window.backgroundColour(),
        )

        prefs = prefs.copy(background = BackgroundStyle.TONAL)
        compose.waitForIdle()
        assertEquals(IvoryPalette.light.canvas.toArgb(), window.backgroundColour())
    }

    @Test
    fun `changing the palette repaints the window`() {
        var prefs by mutableStateOf(ThemePrefs(background = BackgroundStyle.TONAL))
        lateinit var window: Window
        compose.setContent {
            window = LocalContext.current.activity().window
            PftTheme(prefs) { SyncWindowBackground(window) }
        }
        compose.waitForIdle()
        assertEquals(GraphitePalette.light.canvas.toArgb(), window.backgroundColour())

        prefs = prefs.copy(lightPalette = ThemePalette.AUBERGINE)
        compose.waitForIdle()
        assertEquals(AuberginePalette.light.canvas.toArgb(), window.backgroundColour())
    }
}
