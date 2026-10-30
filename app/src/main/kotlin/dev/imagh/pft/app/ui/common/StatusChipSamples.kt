package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs

/**
 * Every [MoneyTone] as a `StatusChip` with the labels the app will use, fake data only: the single
 * source for the preview, the debug catalogue and the goldens. Release builds drop it (R8; see
 * `verifyReleaseShrink`).
 */
@Composable
internal fun StatusChipSamples() {
    val spacing = PftTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            StatusChip("Unverified", MoneyTone.Warning)
            StatusChip("80%", MoneyTone.Warning)
            StatusChip("Overdue", MoneyTone.Negative)
            StatusChip("Over", MoneyTone.Negative)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            StatusChip("Under", MoneyTone.Positive)
            StatusChip("Paused", MoneyTone.Neutral)
            StatusChip("Cancelled", MoneyTone.Neutral)
        }
    }
}

@PreviewLightDark
@Composable
internal fun StatusChipPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface { StatusChipSamples() } }
}
