package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Colour-scheme resolution against the real platform dynamic colours (UI-8). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ThemeResolutionTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val dynamicPrefs = ThemePrefs(dynamicColor = true)

    @Test
    fun `default prefs turn dynamic colour off`() {
        assertFalse(ThemePrefs.Default.dynamicColor)
        assertFalse(ThemePrefs().dynamicColor)
    }

    @Test
    fun `dynamic light resolves to the system dynamic light scheme`() {
        assertEquals(
            dynamicLightColorScheme(context).roles(),
            resolveColorScheme(dynamicPrefs, dark = false, context = context).roles(),
        )
    }

    @Test
    fun `dynamic dark resolves to the system dynamic dark scheme`() {
        assertEquals(
            dynamicDarkColorScheme(context).roles(),
            resolveColorScheme(dynamicPrefs, dark = true, context = context).roles(),
        )
    }

    @Test
    fun `dynamic off resolves the Graphite scheme in each mode`() {
        val light = resolveColorScheme(ThemePrefs.Default, dark = false, context = context)
        val dark = resolveColorScheme(ThemePrefs.Default, dark = true, context = context)
        assertEquals(GraphitePalette.light.scheme.roles(), light.roles())
        assertEquals(GraphitePalette.dark.scheme.roles(), dark.roles())
    }

    @Test
    fun `the dynamic scheme is distinguishable from the Graphite scheme`() {
        // Guards the tests above: if Robolectric's system palette equalled Graphite they could not
        // tell the two branches apart.
        assertNotEquals(
            GraphitePalette.light.scheme.primary,
            resolveColorScheme(dynamicPrefs, dark = false, context = context).primary,
        )
    }

    @Test
    fun `Graphite primary is the owner-approved platinum`() {
        // The mockup's `primary` for Graphite (plan §3.1), pinned over the generated role.
        assertEquals(Color(0xFF3B4350), GraphitePalette.light.scheme.primary)
        assertEquals(Color(0xFFC9CED8), GraphitePalette.dark.scheme.primary)
        assertEquals(Color(0xFF3B4350), resolveColorScheme(ThemePrefs.Default, dark = false, context = context).primary)
        assertEquals(Color(0xFFC9CED8), resolveColorScheme(ThemePrefs.Default, dark = true, context = context).primary)
    }

    @Test
    fun `light and dark palettes are chosen independently`() {
        val prefs = ThemePrefs(lightPalette = ThemePalette.IVORY, darkPalette = ThemePalette.MIDNIGHT)
        assertEquals(
            ThemePalette.IVORY
                .colors(dark = false)
                .scheme
                .roles(),
            resolveColorScheme(prefs, dark = false, context = context).roles(),
        )
        assertEquals(
            ThemePalette.MIDNIGHT
                .colors(dark = true)
                .scheme
                .roles(),
            resolveColorScheme(prefs, dark = true, context = context).roles(),
        )
    }

    @Test
    fun `every palette resolves to its own scheme in each mode`() {
        for (palette in ThemePalette.entries) {
            val prefs = ThemePrefs(lightPalette = palette, darkPalette = palette)
            for (dark in listOf(false, true)) {
                assertEquals(
                    "$palette dark=$dark",
                    palette.colors(dark).scheme.roles(),
                    resolveColorScheme(prefs, dark, context).roles(),
                )
            }
        }
    }

    @Test
    fun `dynamic on ignores the palette`() {
        for (palette in ThemePalette.entries) {
            val prefs = ThemePrefs(dynamicColor = true, lightPalette = palette, darkPalette = palette)
            assertEquals(
                "$palette light",
                dynamicLightColorScheme(context).roles(),
                resolveColorScheme(prefs, dark = false, context = context).roles(),
            )
            assertEquals(
                "$palette dark",
                dynamicDarkColorScheme(context).roles(),
                resolveColorScheme(prefs, dark = true, context = context).roles(),
            )
        }
    }

    @Test
    fun `money colours are identical under every palette and under dynamic colour`() {
        var prefs by mutableStateOf(ThemePrefs(mode = ThemeMode.LIGHT))
        lateinit var money: MoneyColors
        compose.setContent { PftTheme(prefs) { money = PftTheme.money } }
        for (mode in listOf(ThemeMode.LIGHT, ThemeMode.DARK)) {
            val expected = moneyColors(dark = mode == ThemeMode.DARK)
            for (palette in ThemePalette.entries) {
                prefs = ThemePrefs(mode = mode, lightPalette = palette, darkPalette = palette)
                compose.waitForIdle()
                assertEquals("$palette $mode", expected, money)
            }
            prefs = ThemePrefs(mode = mode, dynamicColor = true)
            compose.waitForIdle()
            assertEquals("dynamic $mode", expected, money)
        }
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

    @Test
    fun `dynamic canvas and island are derived from the dynamic scheme's surface containers`() {
        val light = dynamicLightColorScheme(context)
        val dark = dynamicDarkColorScheme(context)
        val lightColors = resolvePaletteColors(dynamicPrefs, dark = false, context = context)
        val darkColors = resolvePaletteColors(dynamicPrefs, dark = true, context = context)
        assertEquals(light.surfaceContainerHigh, lightColors.canvas)
        assertEquals(light.surfaceContainerLow, lightColors.island)
        assertEquals(dark.surfaceContainerLowest, darkColors.canvas)
        assertEquals(dark.surfaceContainerLow, darkColors.island)
        assertEquals(light.roles(), lightColors.scheme.roles())
        assertEquals(dark.roles(), darkColors.scheme.roles())
    }

    @Test
    fun `the dynamic wash is the dynamic scheme's primary container`() {
        val light = dynamicLightColorScheme(context)
        val dark = dynamicDarkColorScheme(context)
        assertEquals(light.primaryContainer, resolvePaletteColors(dynamicPrefs, dark = false, context = context).wash)
        assertEquals(dark.primaryContainer, resolvePaletteColors(dynamicPrefs, dark = true, context = context).wash)
    }

    @Test
    fun `dynamic off resolves the palette's own wash`() {
        val prefs = ThemePrefs(lightPalette = ThemePalette.IVORY, darkPalette = ThemePalette.MIDNIGHT)
        assertEquals(ThemePalette.IVORY.colors(dark = false).wash, resolvePaletteColors(prefs, false, context).wash)
        assertEquals(ThemePalette.MIDNIGHT.colors(dark = true).wash, resolvePaletteColors(prefs, true, context).wash)
    }

    @Test
    fun `dynamic off resolves the palette's own canvas and island`() {
        val prefs = ThemePrefs(lightPalette = ThemePalette.IVORY, darkPalette = ThemePalette.MIDNIGHT)
        assertEquals(ThemePalette.IVORY.colors(dark = false).canvas, resolvePaletteColors(prefs, false, context).canvas)
        assertEquals(
            ThemePalette.MIDNIGHT.colors(dark = true).island,
            resolvePaletteColors(prefs, true, context).island,
        )
    }

    @Test
    fun `the derived dynamic canvas, island and surface stay near the window colour and inside the swept band`() {
        // First-frame flash (UI-18): the window starts as Graphite's canvas, whatever dynamic colour derives.
        for (dark in listOf(false, true)) {
            val colors = resolvePaletteColors(dynamicPrefs, dark, context)
            val window = GraphitePalette.let { if (dark) it.dark else it.light }.canvas
            val low = greyAtTone(sweptTones(dark).first).luminance()
            val high = greyAtTone(sweptTones(dark).last).luminance()
            val derived =
                mapOf("canvas" to colors.canvas, "island" to colors.island, "surface" to colors.scheme.surface)
            for ((name, color) in derived) {
                val delta = kotlin.math.abs(toneOf(color) - toneOf(window))
                assertTrue(
                    "dynamic dark=$dark $name is $delta tones from the window colour",
                    delta <= WINDOW_FLASH_MAX_TONE_DELTA,
                )
                assertTrue(
                    "dynamic dark=$dark $name luminance ${color.luminance()} is outside the swept band",
                    color.luminance() in (low - 0.002f)..(high + 0.002f),
                )
            }
        }
    }

    @Test
    fun `PftTheme provides the surfaces for the palette, mode and background style`() {
        var prefs by mutableStateOf(ThemePrefs(mode = ThemeMode.LIGHT, lightPalette = ThemePalette.IVORY))
        lateinit var surfaces: PftSurfaces
        compose.setContent { PftTheme(prefs) { surfaces = PftTheme.surfaces } }
        for (style in BackgroundStyle.entries) {
            prefs = prefs.copy(background = style)
            compose.waitForIdle()
            assertEquals(pftSurfaces(IvoryPalette.light, style, dark = false), surfaces)
        }
        prefs =
            ThemePrefs(mode = ThemeMode.DARK, darkPalette = ThemePalette.MIDNIGHT, background = BackgroundStyle.TONAL)
        compose.waitForIdle()
        assertEquals(pftSurfaces(MidnightPalette.dark, BackgroundStyle.TONAL, dark = true), surfaces)
    }

    @Test
    fun `dynamic colour with TONAL surfaces uses the derived canvas and island`() {
        var surfaces: PftSurfaces? = null
        compose.setContent {
            PftTheme(ThemePrefs(mode = ThemeMode.LIGHT, dynamicColor = true, background = BackgroundStyle.TONAL)) {
                surfaces = PftTheme.surfaces
            }
        }
        compose.waitForIdle()
        val scheme = dynamicLightColorScheme(context)
        assertEquals(scheme.surfaceContainerHigh, surfaces?.canvas)
        assertEquals(scheme.surfaceContainerLow, surfaces?.island)
    }
}
