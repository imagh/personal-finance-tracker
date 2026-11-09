package dev.imagh.pft.app.ui.theme

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.math.PI

/** The motion tokens and the SwiftUI-style spring conversion (plan §3.3, UI-10). Pure JVM. */
class PftMotionTest {
    private val tolerance = 0.5f

    @Test
    fun `iosSpring converts a smooth 450ms no-bounce spring to stiffness 194_9 and critical damping`() {
        val spec = iosSpring<Float>(450, 0f)
        assertEquals(194.9f, spec.stiffness, tolerance)
        assertEquals(1f, spec.dampingRatio, 0f)
    }

    @Test
    fun `iosSpring maps bounce to a damping ratio of one minus bounce`() {
        val spec = iosSpring<Float>(500, 0.25f)
        assertEquals(0.75f, spec.dampingRatio, 1e-6f)
        assertEquals(157.9f, spec.stiffness, tolerance)
    }

    @Test
    fun `iosSpring stiffness is the angular frequency squared with mass 1`() {
        val seconds = 0.3f
        val omega = (2 * PI / seconds).toFloat()
        assertEquals(omega * omega, iosSpring<Float>(300, 0.1f).stiffness, 0.01f)
    }

    @Test
    fun `iosSpring rejects a negative bounce`() {
        assertThrows(IllegalArgumentException::class.java) { iosSpring<Float>(300, -0.01f) }
    }

    @Test
    fun `iosSpring rejects a bounce of 1 or more`() {
        assertThrows(IllegalArgumentException::class.java) { iosSpring<Float>(300, 1f) }
        assertThrows(IllegalArgumentException::class.java) { iosSpring<Float>(300, 1.5f) }
    }

    @Test
    fun `iosSpring rejects a duration of zero or less`() {
        assertThrows(IllegalArgumentException::class.java) { iosSpring<Float>(0, 0f) }
        assertThrows(IllegalArgumentException::class.java) { iosSpring<Float>(-100, 0f) }
    }

    @Test
    fun `iosSpring rejects a NaN bounce`() {
        assertThrows(IllegalArgumentException::class.java) { iosSpring<Float>(300, Float.NaN) }
    }

    @Test
    fun `the four tokens equal their specified stiffness and damping`() {
        assertEquals(438.6f, PftMotion.snappy.stiffness, tolerance)
        assertEquals(0.90f, PftMotion.snappy.dampingRatio, 1e-6f)

        assertEquals(194.9f, PftMotion.smooth.stiffness, tolerance)
        assertEquals(1.00f, PftMotion.smooth.dampingRatio, 1e-6f)

        assertEquals(109.7f, PftMotion.gentle.stiffness, tolerance)
        assertEquals(1.00f, PftMotion.gentle.dampingRatio, 1e-6f)

        assertEquals(157.9f, PftMotion.bouncy.stiffness, tolerance)
        assertEquals(0.75f, PftMotion.bouncy.dampingRatio, 1e-6f)
    }

    @Test
    fun `smoothOffset is the smooth spring for slide transitions`() {
        assertEquals(PftMotion.smooth.stiffness, PftMotion.smoothOffset.stiffness, 1e-3f)
        assertEquals(PftMotion.smooth.dampingRatio, PftMotion.smoothOffset.dampingRatio, 0f)
    }

    @Test
    fun `the pressed scale is 0_97`() {
        assertEquals(0.97f, PftMotion.PRESSED_SCALE, 0f)
    }
}
