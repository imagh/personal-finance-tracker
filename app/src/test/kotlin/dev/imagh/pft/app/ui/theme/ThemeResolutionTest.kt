package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Colour-scheme resolution against the real platform dynamic colours (UI-8). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ThemeResolutionTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `default prefs turn dynamic colour off`() {
        assertFalse(ThemePrefs.Default.dynamicColor)
        assertFalse(ThemePrefs().dynamicColor)
    }

    @Test
    fun `dynamic light resolves to the system dynamic light scheme`() {
        assertEquals(
            dynamicLightColorScheme(context).roles(),
            resolveColorScheme(dark = false, dynamic = true, context = context).roles(),
        )
    }

    @Test
    fun `dynamic dark resolves to the system dynamic dark scheme`() {
        assertEquals(
            dynamicDarkColorScheme(context).roles(),
            resolveColorScheme(dark = true, dynamic = true, context = context).roles(),
        )
    }

    @Test
    fun `dynamic off resolves the Graphite scheme in each mode`() {
        val light = resolveColorScheme(dark = false, dynamic = false, context = context)
        val dark = resolveColorScheme(dark = true, dynamic = false, context = context)
        assertEquals(GraphitePalette.light.scheme.roles(), light.roles())
        assertEquals(GraphitePalette.dark.scheme.roles(), dark.roles())
    }

    @Test
    fun `the dynamic scheme is distinguishable from the Graphite scheme`() {
        // Guards the tests above: if Robolectric's system palette equalled Graphite they could not
        // tell the two branches apart.
        assertNotEquals(
            GraphitePalette.light.scheme.primary,
            resolveColorScheme(dark = false, dynamic = true, context = context).primary,
        )
    }

    @Test
    fun `Graphite primary is the owner-approved platinum`() {
        // The mockup's `primary` for Graphite (plan §3.1), pinned over the generated role.
        assertEquals(Color(0xFF3B4350), GraphitePalette.light.scheme.primary)
        assertEquals(Color(0xFFC9CED8), GraphitePalette.dark.scheme.primary)
        assertEquals(Color(0xFF3B4350), resolveColorScheme(dark = false, dynamic = false, context = context).primary)
        assertEquals(Color(0xFFC9CED8), resolveColorScheme(dark = true, dynamic = false, context = context).primary)
    }

    @Test
    fun `money colours are the fixed owner-reviewed tokens in both themes`() {
        val light = moneyColors(dark = false)
        assertEquals(Color(0xFF13653C), light.positive)
        assertEquals(Color(0xFFA3261D), light.negative)
        assertEquals(Color(0xFF734900), light.warning)
        val dark = moneyColors(dark = true)
        assertEquals(Color(0xFF6FD99E), dark.positive)
        assertEquals(Color(0xFFFF8F85), dark.negative)
        assertEquals(Color(0xFFF2C14E), dark.warning)
    }
}
