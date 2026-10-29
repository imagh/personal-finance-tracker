package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs

/**
 * The thumb-zone action bar (UI-7): one full-width primary button, or a [secondary] and a [primary]
 * button side by side with the primary on the end side. It sits at the bottom of a screen, where
 * the thumb rests, and is used by every form (Save), setup (Continue) and detail screen
 * (Edit/Delete); `CollapsingHeaderScaffold` takes it as its `bottomBar`.
 *
 * The primary is at least 56dp tall (the 48dp touch-target floor with room to spare). The bar pads
 * itself by `WindowInsets.safeDrawing` at the bottom and sides, which includes the keyboard, so a
 * Save button rides above the IME and clears the navigation bar. The buttons are plain M3
 * [Button] (primary) and [OutlinedButton] (secondary), styled by the theme.
 *
 * @param primary the main action, filled, on the end side.
 * @param secondary an optional lesser action (Cancel, Delete), outlined, on the start side.
 */
@Composable
fun BottomActionBar(
    primary: BottomBarAction,
    modifier: Modifier = Modifier,
    secondary: BottomBarAction? = null,
) {
    val spacing = PftTheme.spacing
    Surface(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
        Row(
            Modifier
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                    ),
                ).padding(horizontal = spacing.gutter, vertical = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // Both buttons share the width equally; a lone primary takes all of it.
            val buttonModifier = Modifier.weight(1f).heightIn(min = spacing.huge + spacing.sm)
            if (secondary != null) {
                OutlinedButton(secondary.onClick, buttonModifier, enabled = secondary.enabled) { Text(secondary.label) }
            }
            Button(primary.onClick, buttonModifier, enabled = primary.enabled) { Text(primary.label) }
        }
    }
}

/** The bar's variants with fake labels: the single source for the preview, the catalogue and the goldens. */
@Composable
internal fun BottomActionBarSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.lg)) {
        BottomActionBar(primary = BottomBarAction("Save") {})
        BottomActionBar(primary = BottomBarAction("Save") {}, secondary = BottomBarAction("Cancel") {})
        BottomActionBar(primary = BottomBarAction("Continue", enabled = false) {})
    }
}

@PreviewLightDark
@Composable
internal fun BottomActionBarPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface { BottomActionBarSamples() } }
}
