package dev.imagh.pft.app

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.pressScale
import kotlin.math.roundToInt

/**
 * The catalogue's motion page: a press target for `pressScale` and one track per spring token.
 * Tap a track to send its dot to the far end and back, to feel the four springs side by side
 * (judge them on a release-like build, not a debug one; plan §10, §11).
 */
@Composable
internal fun MotionSection() {
    val motion = PftTheme.motion
    Column(Modifier.padding(PftTheme.spacing.gutter), verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.lg)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .pressScale()
                .background(MaterialTheme.colorScheme.secondaryContainer, PftTheme.shapes.glyph)
                .clickable {},
            contentAlignment = Alignment.Center,
        ) { Text("Press me (scales to 0.97 with snappy)") }
        SpringTrack("snappy", "0.30 s, bounce 0.10", motion.snappy)
        SpringTrack("smooth", "0.45 s, bounce 0", motion.smooth)
        SpringTrack("gentle", "0.60 s, bounce 0", motion.gentle)
        SpringTrack("bouncy", "0.50 s, bounce 0.25", motion.bouncy)
    }
}

@Composable
private fun SpringTrack(
    name: String,
    detail: String,
    spring: SpringSpec<Float>,
) {
    var far by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(if (far) 1f else 0f, spring, label = name)
    Column(Modifier.fillMaxWidth().testTag("spring-$name").clickable { far = !far }) {
        Text("$name: $detail", style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.fillMaxWidth().height(40.dp).padding(top = 4.dp)) {
            Box(
                Modifier
                    .offset { IntOffset((progress * TRACK_TRAVEL.dp.roundToPx()).roundToInt(), 0) }
                    // After the offset, so the node's reported bounds include the movement.
                    .testTag("dot-$name")
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            )
        }
    }
}

private const val TRACK_TRAVEL = 240
