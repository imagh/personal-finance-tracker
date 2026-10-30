package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import dev.imagh.pft.app.ui.theme.GlyphSize
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.pressScale

/** The symbol's share of the badge side (24dp in the 40dp row badge). */
private const val SYMBOL_FRACTION = 0.6f

/** The emoji's font size as a share of the badge side. */
private const val EMOJI_FRACTION = 0.5f

/** The letter fallback's font size as a share of the badge side. */
private const val LETTER_FRACTION = 0.45f

/**
 * The category (or account-type) badge: a soft-square tile in a palette colour holding a registry
 * symbol, one emoji, or a letter fallback ([GlyphSource]). It is the only way a category icon is
 * drawn, so every list, picker and detail looks the same. Named consumers: the account type badge
 * (B2), the category picker and edit screen (B4), transaction rows (C4), budget rows (D4) and
 * recurring rows (E4).
 *
 * **Accessibility.** The badge is one image node announced as [name], the category's own name:
 * never the icon key, the emoji's Unicode name or the fallback letter. Its colours come from
 * `PftTheme.palette` for the current light or dark theme, content at least 4.5:1 on container.
 * Colour is decoration only; the symbol, emoji or letter carries the identity.
 *
 * **Size.** Fixed dp ([GlyphSize]), not scaled by the font scale. An emoji is drawn in its own
 * colours with the system emoji font and clipped to one line, so a long sequence never spills out.
 * When [onClick] is set the badge is a button and shrinks slightly while pressed. Compose gives any
 * clickable a touch target of at least 48dp (`ViewConfiguration.minimumTouchTargetSize`) without
 * changing the drawn size, so the 28dp chip and 40dp row badges are still easy to hit.
 *
 * @param source what to draw; build it from a stored key with [GlyphSource.fromIconKey].
 * @param name the category's name, spoken by assistive technology.
 * @param colourKey the palette name stored in `category.colour`; null or unknown gets slate.
 * @param size the badge size.
 * @param onClick makes the badge tappable (for example in a picker); null leaves it inert.
 */
@Composable
fun CategoryGlyph(
    source: GlyphSource,
    name: String,
    colourKey: String?,
    modifier: Modifier = Modifier,
    size: GlyphSize = GlyphSize.Row,
    onClick: (() -> Unit)? = null,
) {
    val colors = PftTheme.palette[colourKey]
    val tappable = onClick != null
    Box(
        modifier
            .size(size.dp)
            .then(if (tappable) Modifier.pressScale() else Modifier)
            .clip(PftTheme.shapes.glyph)
            .background(colors.container)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            // One node that speaks the name; the symbol, emoji or letter inside is decoration for it.
            .clearAndSetSemantics {
                contentDescription = name
                role = Role.Image
            },
        contentAlignment = Alignment.Center,
    ) {
        when (source) {
            is GlyphSource.Symbol ->
                Icon(
                    painterResource(source.symbol.res),
                    contentDescription = null,
                    modifier = Modifier.size(size.dp * SYMBOL_FRACTION),
                    tint = colors.content,
                )
            is GlyphSource.Emoji ->
                Text(
                    source.text,
                    fontSize = fixedSp(size, EMOJI_FRACTION),
                    lineHeight = fixedSp(size, EMOJI_FRACTION),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                )
            is GlyphSource.Letter ->
                Text(
                    source.letter.toString(),
                    color = colors.content,
                    fontSize = fixedSp(size, LETTER_FRACTION),
                    lineHeight = fixedSp(size, LETTER_FRACTION),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                )
        }
    }
}

/**
 * A size in sp equal to [fraction] of the badge side in dp
 * whatever the font scale, so the glyph never outgrows its badge.
 */
@Composable
private fun fixedSp(
    size: GlyphSize,
    fraction: Float,
): TextUnit = with(LocalDensity.current) { (size.dp * fraction).toSp() }
