package dev.imagh.pft.app.ui.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs

/**
 * `PftTabBar` with each of the four tabs selected in turn: the single source for the preview and the
 * goldens `tab-bar-*` (`TabBarScreenshotTest`). Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun TabBarSamples() {
    Column {
        TAB_ORDER.forEach { selected -> PftTabBar(TAB_ORDER, selected, onSelect = {}) }
    }
}

@PreviewLightDark
@Composable
internal fun TabBarPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { TabBarSamples() } }
}
