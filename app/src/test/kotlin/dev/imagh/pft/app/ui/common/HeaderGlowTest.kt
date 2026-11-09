package dev.imagh.pft.app.ui.common

import androidx.compose.ui.unit.Density
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** The pure rules of the header glow (plan §3.4, UI-13): how strong it is, where it ends, how collapse dims it. */
class HeaderGlowTest {
    private val end = 600f

    @Test
    fun `the glow is at its cap at the top edge`() {
        assertEquals(MAX_GLOW_ALPHA, glowAlpha(0f, end), 0f)
        assertEquals(0.72f, MAX_GLOW_ALPHA, 0f)
    }

    @Test
    fun `the glow never gets stronger with distance`() {
        val samples = (0..200).map { glowAlpha(it / 200f * end * 1.2f, end) }
        samples.zipWithNext().forEachIndexed { i, (near, far) ->
            assertTrue(far <= near, "sample ${i + 1} ($far) is stronger than sample $i ($near)")
        }
    }

    @Test
    fun `the glow never exceeds the cap and is never negative`() {
        for (i in -10..260) {
            val alpha = glowAlpha(i / 200f * end, end)
            assertTrue(alpha <= MAX_GLOW_ALPHA, "alpha $alpha at sample $i is above the cap")
            assertTrue(alpha >= 0f, "alpha $alpha at sample $i is negative")
        }
    }

    @Test
    fun `the glow is exactly zero at and beyond its end`() {
        assertEquals(0f, glowAlpha(end, end), 0f)
        assertEquals(0f, glowAlpha(end + 0.001f, end), 0f)
        assertEquals(0f, glowAlpha(end * 10f, end), 0f)
    }

    @Test
    fun `the glow follows the mockup stops - 0_72 at the top, about 0_28 at 38 percent`() {
        assertEquals(0.28f, glowAlpha(0.38f * end, end), 0.005f)
        assertEquals(0.5f, glowAlpha(0.19f * end, end), 0.005f)
    }

    @Test
    fun `a glow with no room draws nothing`() {
        assertEquals(0f, glowAlpha(0f, 0f), 0f)
        assertEquals(0f, glowAlpha(5f, -3f), 0f)
    }

    @Test
    fun `with a hero the glow ends at least 24dp above it`() {
        for (scale in listOf(1f, 2.625f, 3f)) {
            val density = Density(scale)
            val clearance = 24f * scale
            for (heroTop in listOf(100f, 400f, 900f)) {
                val glowEnd = with(density) { glowEnd(heroTop, bodyHeight = 1000f) }
                assertTrue(glowEnd <= heroTop - clearance, "glowEnd $glowEnd is within 24dp of the hero at $heroTop")
                assertEquals(heroTop - clearance, glowEnd, 0.001f)
            }
        }
    }

    @Test
    fun `a hero near the top gives no glow rather than a negative radius`() {
        val density = Density(3f)
        assertEquals(0f, with(density) { glowEnd(heroTop = 10f, bodyHeight = 1000f) }, 0f)
        assertEquals(0f, with(density) { glowEnd(heroTop = 72f, bodyHeight = 1000f) }, 0f)
        assertEquals(0f, with(density) { glowEnd(heroTop = -5f, bodyHeight = 1000f) }, 0f)
    }

    @Test
    fun `a hero top of minus one pixel mid-collapse is still a hero, so it gives no glow, not the Standard reach`() {
        // The hero's top goes negative as the header collapses, so no real position may mean "no hero".
        val density = Density(3f)
        val stored = -1f
        assertEquals(-1f, stored.asHeroTop())
        assertEquals(0f, with(density) { glowEnd(stored.asHeroTop(), bodyHeight = 1000f) }, 0f)
    }

    @Test
    fun `the no-hero marker reads as no hero, and no position equals it`() {
        assertEquals(null, NO_HERO_TOP.asHeroTop())
        for (position in listOf(-1000f, -1f, 0f, 1f, 500f)) assertEquals(position, position.asHeroTop())
    }

    @Test
    fun `without a hero the glow ends 60 percent down the header`() {
        assertEquals(600f, with(Density(3f)) { glowEnd(heroTop = null, bodyHeight = 1000f) }, 0.001f)
        assertEquals(0f, with(Density(3f)) { glowEnd(heroTop = null, bodyHeight = 0f) }, 0f)
    }

    @Test
    fun `collapsing dims the glow to nothing`() {
        assertEquals(1f, glowStrength(collapsedFraction = 0f), 0f)
        assertEquals(0.5f, glowStrength(collapsedFraction = 0.5f), 0f)
        assertEquals(0f, glowStrength(collapsedFraction = 1f), 0f)
        // A fraction outside 0..1 (an overscroll frame) still gives a valid strength.
        assertEquals(0f, glowStrength(collapsedFraction = 1.3f), 0f)
        assertEquals(1f, glowStrength(collapsedFraction = -0.2f), 0f)
    }

    @Test
    fun `a collapsed header's strongest glow is zero`() {
        assertEquals(0f, glowAlpha(0f, end) * glowStrength(1f), 0f)
    }
}
