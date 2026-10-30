package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.theme.PftSymbol

/** The drawn size of a header icon (the Material Symbols optical size). */
private val HEADER_ICON_SIZE = 24.dp

/**
 * The one control a header's `actions` row may hold (plan §3.5, UI-14): an icon-only button for a
 * [PftSymbol], such as back, search, filter or overflow. It is a deliberate component, not a
 * restyle wrapper: it enforces the owner's rule that a header action has **no text**, **always
 * has a TalkBack label**, and is **always a registry symbol** (so there is one icon family). Named
 * consumers: the Home search and filter actions, every list's back, budget detail's overflow,
 * settings' back, and the scaffold's own period stepper.
 *
 * **Accessibility.** The button is one node, announced as a button named [label]. The icon is
 * 24dp inside M3's `IconButton`, which Compose gives a touch target of at least 48dp x 48dp without
 * changing the drawn size. The icon is tinted `onSurfaceVariant`, which the contrast sweep
 * (`ColorContrastTest`) keeps at least 4.5:1 on the surface and on the header's glow wash.
 *
 * **Overflow menus.** A `DropdownMenu` needs a `Box` anchor next to its button, which is why the
 * scaffold's `actions` stays a slot; put this button inside that `Box`.
 *
 * @param symbol the registry symbol to draw.
 * @param label what TalkBack speaks (for example "Search"); never blank, because an unlabelled
 *   icon button is unusable with a screen reader.
 * @param onClick called when the button is tapped.
 * @throws IllegalArgumentException if [label] is blank.
 */
@Composable
fun HeaderIconButton(
    symbol: PftSymbol,
    label: String,
    onClick: () -> Unit,
) {
    require(label.isNotBlank()) { "A header icon button needs a non-blank label for TalkBack." }
    IconButton(onClick) {
        Icon(
            painterResource(symbol.res),
            contentDescription = label,
            modifier = Modifier.size(HEADER_ICON_SIZE),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
