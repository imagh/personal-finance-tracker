package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import dev.imagh.pft.app.ui.common.MAX_GLOW_ALPHA
import kotlin.math.atan2
import kotlin.math.cbrt
import kotlin.math.pow

/**
 * Every colour role of an M3 [ColorScheme], by name. `ColorScheme` has no `equals`, so the theme
 * tests compare these maps, and the contrast tests iterate over the surface roles in them.
 */
internal fun ColorScheme.roles(): Map<String, Color> =
    linkedMapOf(
        "primary" to primary,
        "onPrimary" to onPrimary,
        "primaryContainer" to primaryContainer,
        "onPrimaryContainer" to onPrimaryContainer,
        "inversePrimary" to inversePrimary,
        "secondary" to secondary,
        "onSecondary" to onSecondary,
        "secondaryContainer" to secondaryContainer,
        "onSecondaryContainer" to onSecondaryContainer,
        "tertiary" to tertiary,
        "onTertiary" to onTertiary,
        "tertiaryContainer" to tertiaryContainer,
        "onTertiaryContainer" to onTertiaryContainer,
        "background" to background,
        "onBackground" to onBackground,
        "surface" to surface,
        "onSurface" to onSurface,
        "surfaceVariant" to surfaceVariant,
        "onSurfaceVariant" to onSurfaceVariant,
        "surfaceTint" to surfaceTint,
        "inverseSurface" to inverseSurface,
        "inverseOnSurface" to inverseOnSurface,
        "error" to error,
        "onError" to onError,
        "errorContainer" to errorContainer,
        "onErrorContainer" to onErrorContainer,
        "outline" to outline,
        "outlineVariant" to outlineVariant,
        "scrim" to scrim,
        "surfaceBright" to surfaceBright,
        "surfaceDim" to surfaceDim,
        "surfaceContainer" to surfaceContainer,
        "surfaceContainerHigh" to surfaceContainerHigh,
        "surfaceContainerHighest" to surfaceContainerHighest,
        "surfaceContainerLow" to surfaceContainerLow,
        "surfaceContainerLowest" to surfaceContainerLowest,
    )

/**
 * The roles money text can sit on: `background`, `surface`, `surfaceDim`/`surfaceBright` and the
 * five `surfaceContainer*` steps (light tones 87-100, dark tones 4-24, plan §3.3).
 * `surfaceVariant` is excluded on purpose: it is a tone-30 fill (dark) for input tracks and
 * outlines whose text is `onSurfaceVariant`, not a surface amounts are drawn on.
 */
internal fun ColorScheme.surfaceRoles(): Map<String, Color> =
    roles().filterKeys {
        it in
            setOf(
                "background",
                "surface",
                "surfaceBright",
                "surfaceDim",
                "surfaceContainer",
                "surfaceContainerHigh",
                "surfaceContainerHighest",
                "surfaceContainerLow",
                "surfaceContainerLowest",
            )
    }

/**
 * The CIELAB hue angle of [color] in degrees, 0 until 360 (`atan2(b*, a*)`, D65 white point).
 * Pure JVM, no colour library: the accent-safety rule (UI-11) is stated in this space because it
 * takes a few lines to compute here.
 */
internal fun labHue(color: Color): Double {
    fun linear(c: Float): Double {
        val v = c.toDouble()
        return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    val r = linear(color.red)
    val g = linear(color.green)
    val b = linear(color.blue)
    val x = (0.4124564 * r + 0.3575761 * g + 0.1804375 * b) / 0.95047
    val y = 0.2126729 * r + 0.7151522 * g + 0.0721750 * b
    val z = (0.0193339 * r + 0.1191920 * g + 0.9503041 * b) / 1.08883

    fun f(t: Double) = if (t > 216.0 / 24389.0) cbrt(t) else (24389.0 / 27.0 * t + 16.0) / 116.0
    val a = 500.0 * (f(x) - f(y))
    val bStar = 200.0 * (f(y) - f(z))
    val degrees = Math.toDegrees(atan2(bStar, a))
    return if (degrees < 0) degrees + 360.0 else degrees
}

/** The shortest angular distance between two hue angles, 0 to 180 degrees. */
internal fun hueDistance(
    a: Double,
    b: Double,
): Double {
    val d = kotlin.math.abs(a - b) % 360.0
    return if (d > 180.0) 360.0 - d else d
}

/**
 * How far, in tones (CIE L*), any colour the window can be repainted with may sit from the window's
 * resource colour (Graphite's canvas) without a visible first-frame flash (plan §3.3, UI-18).
 */
internal const val WINDOW_FLASH_MAX_TONE_DELTA = 8.0

/** The tone (CIE L*, 0 black to 100 white) of [color]; a function of its luminance alone. */
internal fun toneOf(color: Color): Double {
    val y = color.luminance().toDouble()
    val f = if (y > 216.0 / 24389.0) cbrt(y) else (24389.0 / 27.0 * y + 16.0) / 116.0
    return 116.0 * f - 16.0
}

/** The neutral grey of CIE L* [tone], i.e. what M3 tonal palettes call "tone N" at zero chroma. */
internal fun greyAtTone(tone: Int): Color {
    val fy = (tone + 16.0) / 116.0
    val y = if (tone > 8) fy * fy * fy else tone / 903.2962962962963
    val encoded = if (y <= 0.0031308) 12.92 * y else 1.055 * y.pow(1.0 / 2.4) - 0.055
    return Color(encoded.toFloat(), encoded.toFloat(), encoded.toFloat())
}

/** The tones a surface role can take: light 87 (surfaceDim) to 100, dark 0 to 24 (surfaceBright). */
internal fun sweptTones(dark: Boolean): IntRange = if (dark) 0..24 else 87..100

/** The WCAG 2.x contrast ratio of two opaque colours, 1 to 21 (relative luminance, as [luminance] computes it). */
internal fun contrast(
    a: Color,
    b: Color,
): Double {
    val l1 = a.luminance().toDouble()
    val l2 = b.luminance().toDouble()
    return (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
}

/**
 * The strongest the header glow gets (DS6d): [PaletteColors.wash] at `MAX_GLOW_ALPHA` composited over
 * [canvas], which is what the title, actions and money sit on at the top of the header. Every other
 * point of the glow is weaker, so it is between this colour and the canvas.
 */
internal fun washTop(
    wash: Color,
    canvas: Color,
): Color = wash.copy(alpha = MAX_GLOW_ALPHA).compositeOver(canvas)
