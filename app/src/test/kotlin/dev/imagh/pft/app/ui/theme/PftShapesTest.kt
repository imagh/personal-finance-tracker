package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The corner radii that `PftTheme` gives M3 components, and the soft-square glyph shape (§3.3). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftShapesTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val oneToOne = Density(1f)

    /** Corner radius in dp (density 1) of a [CornerBasedShape] drawn in a [side] x [side] box. */
    private fun Shape.radius(side: Float = 100f): Float {
        val corners = this as CornerBasedShape
        val size = Size(side, side)
        val all = listOf(corners.topStart, corners.topEnd, corners.bottomStart, corners.bottomEnd)
        val radii = all.map { it.toPx(size, oneToOne) }.distinct()
        assertEquals("all four corners are equal", 1, radii.size)
        return radii.single()
    }

    private fun themedShapes(): Shapes {
        lateinit var shapes: Shapes
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { shapes = MaterialTheme.shapes } }
        compose.waitForIdle()
        return shapes
    }

    @Test
    fun `M3 components get the specified corner radii`() {
        val shapes = themedShapes()
        assertEquals(6f, shapes.extraSmall.radius())
        assertEquals(10f, shapes.small.radius())
        assertEquals(16f, shapes.medium.radius())
        assertEquals(24f, shapes.large.radius())
        assertEquals(32f, shapes.extraLarge.radius())
    }

    @Test
    fun `the glyph badge is a soft square whose corners scale with its size`() {
        lateinit var glyph: Shape
        compose.setContent { PftTheme(ThemePrefs(dynamicColor = false)) { glyph = PftTheme.shapes.glyph } }
        compose.waitForIdle()

        assertEquals(12f, glyph.radius(side = 40f))
        assertEquals(16.8f, glyph.radius(side = 56f), 0.001f)
        assertTrue("softer than a small chip", glyph.radius(side = 40f) > 10f)
    }
}
