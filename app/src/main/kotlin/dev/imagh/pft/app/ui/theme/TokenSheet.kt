package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark

/**
 * Every design token on one sheet: the type scale, the amount styles, the shapes, the spacing
 * ruler and the money colours on the surfaces they may be drawn on. It is the single source for
 * the Studio preview, the debug design catalogue and the screenshot goldens (which capture it page
 * by page, see [TokenSheetPages]), so the three always show the same thing. Put it inside
 * [PftTheme] and a scroll container; it uses fake figures only.
 */
@Composable
internal fun TokenSheet(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        TokenSheetPages.forEach { page -> TokenSheetPageFrame(page) }
    }
}

@PreviewLightDark
@Composable
internal fun TokenSheetPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) {
        Surface(color = PftTheme.surfaces.canvas) { TokenSheet() }
    }
}
