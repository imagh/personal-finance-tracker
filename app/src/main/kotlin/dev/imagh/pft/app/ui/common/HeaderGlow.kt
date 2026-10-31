package dev.imagh.pft.app.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import dev.imagh.pft.app.ui.theme.PftTheme
import kotlin.math.max

/**
 * The strongest the header glow ever gets: 72% of the wash colour over the canvas, at the top edge.
 * This is the mockup's top gradient stop. The contrast tests sweep the title and money colours
 * against the wash composited at exactly this strength, so it is a cap, not a default.
 */
internal const val MAX_GLOW_ALPHA = 0.72f

/** The mockup's middle gradient stop: where, as a fraction of the glow's reach, it is [MID_GLOW_ALPHA]. */
internal const val MID_GLOW_STOP = 0.38f

/** The glow's strength at [MID_GLOW_STOP] of its reach (mockup: the wash at 28%). */
internal const val MID_GLOW_ALPHA = 0.28f

/**
 * How strong the glow is [distance] px from the header's top-centre point, for a glow that reaches
 * [end] px (the mockup's radial gradient, with the wash fading to nothing at [end]):
 * [MAX_GLOW_ALPHA] at 0, about [MID_GLOW_ALPHA] at [MID_GLOW_STOP] of [end], and **exactly 0 at and
 * beyond [end]**. It never increases with distance, and a glow with no room (`end <= 0`) is 0
 * everywhere. Pure, so the tests pin the shape and the draw code takes its gradient stops from it.
 */
internal fun glowAlpha(
    distance: Float,
    end: Float,
): Float {
    if (end <= 0f || distance >= end) return 0f
    val t = max(distance, 0f) / end
    return if (t <= MID_GLOW_STOP) {
        MAX_GLOW_ALPHA + (MID_GLOW_ALPHA - MAX_GLOW_ALPHA) * (t / MID_GLOW_STOP)
    } else {
        MID_GLOW_ALPHA * (1f - (t - MID_GLOW_STOP) / (1f - MID_GLOW_STOP))
    }
}

/** The share of the header's height a Standard header's glow reaches (it has no hero to stay clear of). */
private const val STANDARD_GLOW_REACH = 0.6f

/**
 * How far, in px, the glow reaches down from the header's top edge. With a hero it stops
 * `PftTheme.spacing.xl` (24dp) above [heroTop], so **the hero row is never tinted** (money is never
 * drawn on the glow); it is clamped at 0 when the hero is too high for any glow. With no hero (a
 * Standard header) it is 60% of [bodyHeight].
 *
 * @param heroTop the hero's top edge in px from the header's top, or null when there is no hero.
 * @param bodyHeight the header's height in px.
 */
internal fun Density.glowEnd(
    heroTop: Float?,
    bodyHeight: Float,
): Float =
    if (heroTop == null) {
        max(0f, bodyHeight) * STANDARD_GLOW_REACH
    } else {
        max(0f, heroTop - PftTheme.spacing.xl.toPx())
    }

/**
 * The share of the glow left at this scroll position: `1 - collapsedFraction`, so the 64dp bar is
 * plain canvas. A fraction outside 0..1 (an overscroll frame) is clamped.
 */
internal fun glowStrength(collapsedFraction: Float): Float = (1f - collapsedFraction).coerceIn(0f, 1f)

/**
 * Draws the header glow behind this header: a radial gradient of [glow] centred on the header's
 * top-centre point, strongest ([MAX_GLOW_ALPHA]) there and fading to nothing at [glowEnd], with its
 * gradient stops taken from [glowAlpha] and its whole strength scaled by [glowStrength]. Because it
 * is a circle on the top edge, everything at or below [glowEnd]'s depth is untouched: **the hero
 * row is never tinted**.
 *
 * A null [glow] (the default: `PftTheme.surfaces.glow`) adds nothing at all. Put it after the
 * header's canvas background.
 *
 * [heroTop] and [collapsedFraction] are read **in the draw phase only**, never in composition, so a
 * scroll frame redraws the gradient but does not recompose the header (the DS4 rule).
 *
 * @param glow the wash colour, or null for no glow.
 * @param heroTop the hero's top edge in px from the header's top, or null when there is no hero.
 * @param collapsedFraction how far the header has collapsed, 0 (expanded) to 1 (a plain bar).
 */
internal fun Modifier.headerGlow(
    glow: Color?,
    heroTop: () -> Float?,
    collapsedFraction: () -> Float,
): Modifier =
    if (glow == null) {
        this
    } else {
        drawBehind {
            val strength = glowStrength(collapsedFraction())
            val end = glowEnd(heroTop(), size.height)
            if (strength > 0f && end > 0f) {
                val stops =
                    GLOW_STOPS.map { stop -> stop to glow.copy(alpha = glowAlpha(stop * end, end) * strength) }
                drawRect(
                    Brush.radialGradient(
                        colorStops = stops.toTypedArray(),
                        center = Offset(size.width / 2f, 0f),
                        radius = end,
                    ),
                )
            }
        }
    }

/** The gradient's stops as fractions of the reach: the top, the mockup's middle stop, and the end. */
private val GLOW_STOPS = listOf(0f, MID_GLOW_STOP, 1f)

/**
 * The stored hero top that means "this header has no hero". Negative infinity, because a real top
 * is an integer position that goes negative as the header collapses, so no finite value is safe,
 * and (unlike `NaN`) it compares equal to itself, so an unchanged state does not invalidate the draw.
 */
internal const val NO_HERO_TOP = Float.NEGATIVE_INFINITY

/** The hero top a header stored: null for [NO_HERO_TOP], else its top edge in px (negative mid-collapse). */
internal fun Float.asHeroTop(): Float? = takeIf { it != NO_HERO_TOP }
