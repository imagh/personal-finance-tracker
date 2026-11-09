package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs

/**
 * An `EmptyState` with a glyph and an action (an empty entity list) above one with only a message
 * (a search with no results), fake text only. The glyph is a placeholder tile until DS3's symbols
 * exist. The single source for the preview, the debug catalogue and the goldens. Release builds
 * drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun EmptyStateSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        EmptyState(
            "No accounts yet. Add the first one to start tracking.",
            glyph = { GlyphPlaceholder() },
            actionLabel = "Add account",
            onAction = {},
        )
        EmptyState("Nothing matches your search.")
    }
}

@Composable
private fun GlyphPlaceholder() {
    val side = PftTheme.spacing.huge + PftTheme.spacing.lg
    Surface(Modifier.size(side), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                "₹",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun EmptyStatePreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { EmptyStateSamples() } }
}
