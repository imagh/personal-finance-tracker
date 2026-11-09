package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.CategoryPalette
import dev.imagh.pft.app.ui.theme.GlyphSize
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs

/** Badges per row in the symbol grid: seven 40dp badges and gaps fit a 393dp screen with its gutters. */
private const val SYMBOLS_PER_ROW = 7

/**
 * Every [PftSymbol] on a badge of the palette colour [colourKey]: the icon registry at a glance.
 * Fake names only (the key). One of the three goldens' sources for symbols; it also feeds the
 * preview and the debug catalogue. Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun CategoryGlyphSymbolSamples(colourKey: String) {
    val spacing = PftTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        PftSymbol.entries.chunked(SYMBOLS_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                row.forEach { CategoryGlyph(GlyphSource.Symbol(it), it.key, colourKey) }
            }
        }
    }
}

/**
 * The non-symbol sources and the three sizes, fake data only: emoji (single, flag, ZWJ family),
 * letters in all 12 hues, a letter for a name with no icon, and the fallbacks for a malformed and
 * an unknown icon key. The single source for the preview, the debug catalogue and the goldens.
 */
@Composable
internal fun CategoryGlyphSamples() {
    val spacing = PftTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        SampleRow("Sizes") {
            GlyphSize.entries.forEach {
                CategoryGlyph(
                    GlyphSource.Symbol(PftSymbol.SAVINGS),
                    "Savings",
                    "emerald",
                    size = it,
                )
            }
        }
        SampleRow("Emoji") {
            listOf("🍔", "🇮🇳", "👨‍👩‍👧‍👦", "☕").forEach { CategoryGlyph(GlyphSource.Emoji(it), "Emoji", "coral") }
        }
        SampleRow("Letters in every hue") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                CategoryPalette.keys.chunked(SYMBOLS_PER_ROW).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        row.forEach { CategoryGlyph(GlyphSource.letterFor(it), it, it) }
                    }
                }
            }
        }
        SampleRow("Fallbacks: no icon, malformed key, unknown key, unknown hue") {
            CategoryGlyph(GlyphSource.fromIconKey(null, "Pets"), "Pets", "olive")
            CategoryGlyph(GlyphSource.fromIconKey("sym:../x", "Rent"), "Rent", "indigo")
            CategoryGlyph(GlyphSource.fromIconKey("sym:added_later", "Gifts"), "Gifts", "rose")
            CategoryGlyph(GlyphSource.fromIconKey("emoji:a b", "Tea"), "Tea", "no-such-hue")
        }
    }
}

@Composable
private fun SampleRow(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) { content() }
    }
}

@PreviewLightDark
@Composable
internal fun CategoryGlyphPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) {
        Surface(color = PftTheme.surfaces.canvas) {
            Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
                CategoryGlyphSamples()
                CategoryGlyphSymbolSamples("teal")
            }
        }
    }
}
