package dev.imagh.pft.app.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.PI

/**
 * Converts a SwiftUI-style perceptual spring, a ([durationMillis], [bounce]) pair, to a Compose
 * [SpringSpec] with mass 1:
 *
 * - `stiffness = (2π / duration in seconds)²`
 * - `dampingRatio = 1 − bounce`
 *
 * A [bounce] of 0 is critically damped (no overshoot); larger values overshoot more.
 *
 * @param durationMillis the spring's perceptual duration; must be greater than 0.
 * @param bounce 0 (no bounce) up to but excluding 1; negative values and 1 or more are rejected.
 * @throws IllegalArgumentException if [durationMillis] is not positive or [bounce] is outside
 *   `0 <= bounce < 1` (this includes NaN).
 */
fun <T> iosSpring(
    durationMillis: Int,
    bounce: Float,
    visibilityThreshold: T? = null,
): SpringSpec<T> {
    require(durationMillis > 0) { "duration must be positive" }
    require(bounce >= 0f && bounce < 1f) { "bounce must be in [0, 1)" }
    val omega = (2.0 * PI / (durationMillis / MILLIS_PER_SECOND)).toFloat()
    return spring(
        dampingRatio = 1f - bounce,
        stiffness = omega * omega,
        visibilityThreshold = visibilityThreshold,
    )
}

private const val MILLIS_PER_SECOND = 1000.0

/**
 * The motion tokens (UI-10): four springs defined the way SwiftUI defines them, the navigation
 * transitions built from them, and the press feedback scale. Read through `PftTheme.motion`.
 *
 * The springs are `SpringSpec<Float>`, which fits `animate*AsState`, `graphicsLayer` and the M3
 * scroll-snap spec. For another type, build the same spring with [iosSpring] and the matching
 * `(duration, bounce)` listed on each token.
 *
 * Reduced motion: Compose honours the system animator-duration scale, so animations jump to their
 * end state. No logic may wait for an animation to finish.
 */
object PftMotion {
    private const val SNAPPY_MS = 300
    private const val SNAPPY_BOUNCE = 0.10f
    private const val SMOOTH_MS = 450
    private const val GENTLE_MS = 600
    private const val BOUNCY_MS = 500
    private const val BOUNCY_BOUNCE = 0.25f

    /** Slide distance of a screen push or pop, as a fraction of the width. */
    private const val NAV_SLIDE_FRACTION = 4

    /** While pressed, [pressScale] shrinks its target to this fraction of its size. */
    const val PRESSED_SCALE = 0.97f

    /** (0.30 s, bounce 0.10): press feedback, chips, keypad digits, toggles. */
    val snappy: SpringSpec<Float> = iosSpring(SNAPPY_MS, SNAPPY_BOUNCE)

    /** (0.45 s, bounce 0): screen transitions, header snap, progress fill, rolling digits. */
    val smooth: SpringSpec<Float> = iosSpring(SMOOTH_MS, 0f)

    /** (0.60 s, bounce 0): container transforms (FAB to add screen, row to detail). */
    val gentle: SpringSpec<Float> = iosSpring(GENTLE_MS, 0f)

    /** (0.50 s, bounce 0.25): rare celebration only (goal reached). */
    val bouncy: SpringSpec<Float> = iosSpring(BOUNCY_MS, BOUNCY_BOUNCE)

    /**
     * `smooth` as a spring over `IntOffset`, for `slideIn*`/`slideOut*` transitions (rolling digits,
     * screen pushes): the same (0.45 s, bounce 0) feel as [smooth], in the type a slide needs.
     */
    val smoothOffset: SpringSpec<IntOffset> = iosSpring(SMOOTH_MS, 0f)

    /** Push, the incoming screen: slides in from the end by 25% of the width while fading in. */
    val navEnter: EnterTransition =
        slideInHorizontally(smoothOffset) { it / NAV_SLIDE_FRACTION } + fadeIn(smooth)

    /** Push, the outgoing screen: slides out toward the start by 25% of the width while fading out. */
    val navExit: ExitTransition =
        slideOutHorizontally(smoothOffset) { -it / NAV_SLIDE_FRACTION } + fadeOut(smooth)

    /** Pop, the screen coming back: the reverse of [navExit]. */
    val navPopEnter: EnterTransition =
        slideInHorizontally(smoothOffset) { -it / NAV_SLIDE_FRACTION } + fadeIn(smooth)

    /** Pop, the screen leaving: the reverse of [navEnter]. */
    val navPopExit: ExitTransition =
        slideOutHorizontally(smoothOffset) { it / NAV_SLIDE_FRACTION } + fadeOut(smooth)
}

/**
 * Press feedback: scales the element to [PftMotion.PRESSED_SCALE] (0.97) with the `snappy` spring
 * while a pointer is down on it, and back to 1.0 on release or cancel. Put it on rows, cards,
 * glyph tiles and keypad keys, before `clickable` so the M3 ripple stays. It only observes
 * pointers (it never consumes them), so it does not change what a tap does, and a drag that turns
 * into a scroll releases the press.
 */
fun Modifier.pressScale(): Modifier =
    composed {
        val scale = remember { Animatable(1f) }
        val scope = rememberCoroutineScope()
        Modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }.pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    scope.launch { scale.animateTo(PftMotion.PRESSED_SCALE, PftMotion.snappy) }
                    waitForUpOrCancellation()
                    scope.launch { scale.animateTo(1f, PftMotion.snappy) }
                }
            }
    }
