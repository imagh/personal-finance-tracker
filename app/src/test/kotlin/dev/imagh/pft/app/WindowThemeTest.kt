package dev.imagh.pft.app

import android.app.Activity
import android.graphics.Color
import android.view.WindowManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import dev.imagh.pft.app.ui.theme.GraphitePalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The window theme (`values` / `values-night`) matches the default (Graphite) surface, so a dark launch never
 * flashes a light window before Compose draws, and the activity is edge-to-edge (UI-4, SEC-2).
 *
 * Under Robolectric the only observable trace of `enableEdgeToEdge()` is the bar icon appearance
 * (the tests below; they fail without the call). `Window.setDecorFitsSystemWindows(false)` has no
 * public getter, and the display-cutout mode that `EdgeToEdgeApi28` sets is not applied on API 30+,
 * so "content is laid out behind the bars" is verified on the emulator, not here.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class WindowThemeTest {
    private fun launch(): Activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()

    /** The `android:windowBackground` colour of the activity's theme (what the window paints first). */
    private fun Activity.windowBackgroundColor(): Int {
        val attrs = theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        try {
            return attrs.getColor(0, Color.MAGENTA)
        } finally {
            attrs.recycle()
        }
    }

    @Test
    @Config(qualifiers = "+night")
    fun `dark system paints the dark surface behind the first frame`() {
        assertEquals(
            GraphitePalette.dark.scheme.surface
                .toArgb(),
            launch().windowBackgroundColor(),
        )
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun `light system paints the light surface behind the first frame`() {
        assertEquals(
            GraphitePalette.light.scheme.surface
                .toArgb(),
            launch().windowBackgroundColor(),
        )
    }

    @Test
    @Config(qualifiers = "+night")
    fun `system bars are transparent for edge-to-edge`() {
        val window = launch().window
        assertEquals(Color.TRANSPARENT, window.statusBarColor)
        assertEquals(Color.TRANSPARENT, window.navigationBarColor)
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun `edge-to-edge styles dark bar icons on the light surface`() {
        // enableEdgeToEdge() derives the icon appearance from the system mode; a theme alone cannot.
        val window = launch().window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        assertTrue(controller.isAppearanceLightStatusBars)
        assertTrue(controller.isAppearanceLightNavigationBars)
    }

    @Test
    @Config(qualifiers = "+night")
    fun `edge-to-edge styles light bar icons on the dark surface`() {
        val window = launch().window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        assertFalse(controller.isAppearanceLightStatusBars)
        assertFalse(controller.isAppearanceLightNavigationBars)
    }

    @Test
    fun `FLAG_SECURE is still set`() {
        val flags = launch().window.attributes.flags
        assertTrue("FLAG_SECURE must survive the theme work", flags and WindowManager.LayoutParams.FLAG_SECURE != 0)
    }
}
