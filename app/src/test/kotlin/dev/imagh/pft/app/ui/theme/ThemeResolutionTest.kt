package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    fun `dynamic off resolves to the brand schemes`() {
        val light = resolveColorScheme(dark = false, dynamic = false, context = context)
        val dark = resolveColorScheme(dark = true, dynamic = false, context = context)
        assertEquals(BrandColors.light.roles(), light.roles())
        assertEquals(BrandColors.dark.roles(), dark.roles())
    }

    @Test
    fun `the dynamic scheme is distinguishable from the brand scheme`() {
        // Guards the tests above: if Robolectric's system palette equalled the brand palette they
        // could not tell the two branches apart.
        assertNotEquals(
            BrandColors.light.primary,
            resolveColorScheme(dark = false, dynamic = true, context = context).primary,
        )
    }

    @Test
    fun `brand fallback is the owner-approved ink indigo`() {
        // Seed #4A55B8 (plan §12 Q5); tone 40 of its tonal-spot palette is the light primary.
        assertEquals(Color(0xFF4954B7), BrandColors.light.primary)
        assertEquals(Color(0xFFBDC2FF), BrandColors.dark.primary)
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
