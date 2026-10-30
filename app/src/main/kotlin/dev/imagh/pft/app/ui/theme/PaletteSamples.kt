package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The four palettes' role swatches side by side, in [dark] or light, whatever theme surrounds
 * them: each column is drawn from its own palette's colours, so one page compares every option.
 * Each column shows `surface` with its text colours, a `surfaceContainer` island with its
 * `outlineVariant` hairline, and the `primary` and `primaryContainer` fills with their text. The
 * single source for the preview and the goldens. Release builds drop it (R8; see
 * `verifyReleaseShrink`).
 */
@Composable
internal fun PaletteSwatches(dark: Boolean) {
    val spacing = PftTheme.spacing
    Row(
        Modifier.fillMaxWidth().padding(spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        ThemePalette.entries.forEach { palette ->
            PaletteColumn(palette, palette.colors(dark).scheme, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PaletteColumn(
    palette: ThemePalette,
    scheme: ColorScheme,
    modifier: Modifier,
) {
    val spacing = PftTheme.spacing
    val shape = RoundedCornerShape(spacing.md)
    Column(
        modifier
            .testTag("swatches-${palette.name}")
            .background(scheme.surface, shape)
            .border(BorderStroke(1.dp, scheme.outlineVariant), shape)
            .padding(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            palette.displayName,
            color = scheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text("Text", color = scheme.onSurface, fontSize = 11.sp)
        Text("Variant", color = scheme.onSurfaceVariant, fontSize = 11.sp)
        Swatch("Island", scheme.onSurface, scheme.surfaceContainer, scheme.outlineVariant)
        Swatch("Primary", scheme.onPrimary, scheme.primary, scheme.primary)
        Swatch("Tinted", scheme.onPrimaryContainer, scheme.primaryContainer, scheme.primaryContainer)
        Swatch("Bright", scheme.onSurface, scheme.surfaceBright, scheme.outlineVariant)
        Swatch("Dim", scheme.onSurface, scheme.surfaceDim, scheme.outlineVariant)
    }
}

/** One labelled fill: [label] in [content] on [container], edged by [edge]. */
@Composable
private fun Swatch(
    label: String,
    content: Color,
    container: Color,
    edge: Color,
) {
    val shape = RoundedCornerShape(PftTheme.spacing.sm)
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(container, shape)
            .border(BorderStroke(1.dp, edge), shape)
            .padding(PftTheme.spacing.sm),
    ) {
        Text(label, color = content, fontSize = 11.sp)
    }
}

@PreviewLightDark
@Composable
internal fun PaletteSwatchesPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) {
        Surface { PaletteSwatches(dark = isSystemInDarkTheme()) }
    }
}
