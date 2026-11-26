package dev.imagh.pft.app.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.theme.PftTheme

/** The pill's minimum height (plan D8); it grows with large text rather than clip it. */
private val BAR_HEIGHT = 62.dp

/** The size of a tab's tap target (and the active pill's minimum height): the accessibility floor. */
private val TARGET = 48.dp

/** The drawn size of a tab's icon. */
private val ICON_SIZE = 24.dp

/** The shadow under the floating pill. */
private val BAR_SHADOW = 8.dp

/**
 * The floating tab bar (plan D8): a full-pill bar in `brand.nav`, inset by the gutter and the
 * navigation-bar inset, over a fade into the canvas so rows scrolling under it stay legible. The
 * [selected] tab is a `brand` pill with its icon and a SemiBold label in `onBrand`, with `md` (12dp) padding
 * each side and `sm` (8dp) between icon and label; the others are icon-only in
 * `onSurfaceVariant`. The bar is drawn at the size it needs: place it at the bottom of a `Box`, and let
 * the host read its height for `LocalTabBarClearance`.
 *
 * Accessibility. The tabs are one `selectableGroup`; each is `Role.Tab` with `selected` semantics and
 * a target of at least 48dp. The active tab's spoken name is its visible label; an inactive tab has no
 * text, so its content description carries the name, and its icon is decorative. Nothing is read twice.
 *
 * Stateless: it does not know about navigation. Tapping the already selected tab calls [onSelect]
 * too, so the host can pop that tab to its root.
 *
 * @param tabs the tabs, in the order drawn ([TAB_ORDER]).
 * @param selected the active tab.
 * @param onSelect called with the tapped tab.
 */
@Composable
internal fun PftTabBar(
    tabs: List<Tab>,
    selected: Tab,
    onSelect: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = PftTheme.spacing
    val canvas = PftTheme.surfaces.canvas
    Box(
        modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, canvas)))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = spacing.gutter, end = spacing.gutter, top = spacing.lg, bottom = spacing.md),
    ) {
        Surface(
            Modifier.fillMaxWidth(),
            shape = PftTheme.shapes.tabBar,
            color = PftTheme.brand.nav,
            shadowElevation = BAR_SHADOW,
        ) {
            Row(
                Modifier
                    .selectableGroup()
                    .heightIn(min = BAR_HEIGHT)
                    .padding(horizontal = spacing.xs, vertical = (BAR_HEIGHT - TARGET) / 2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    // Inactive tabs share what the active one leaves; the active one is as wide as its label.
                    val width = if (tab == selected) Modifier else Modifier.weight(1f)
                    TabItem(tab, active = tab == selected, onClick = { onSelect(tab) }, modifier = width)
                }
            }
        }
    }
}

/** One tab: an icon, and beside it the label on the active tab only. */
@Composable
private fun TabItem(
    tab: Tab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(tab.label)
    val brand = PftTheme.brand
    val tint = if (active) brand.onBrand else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier
            .heightIn(min = TARGET)
            .widthIn(min = TARGET)
            .clip(PftTheme.shapes.tabBar)
            .background(if (active) brand.brand else Color.Transparent)
            .selectable(selected = active, role = Role.Tab, onClick = onClick)
            // The inactive tab has no text: the content description is its name. The active tab speaks its label.
            .semantics { if (!active) contentDescription = label }
            .padding(horizontal = if (active) PftTheme.spacing.md else PftTheme.spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(tab.icon.res), contentDescription = null, modifier = Modifier.size(ICON_SIZE), tint = tint)
        if (active) {
            Spacer(Modifier.size(PftTheme.spacing.sm))
            Text(
                label,
                color = tint,
                // SemiBold (600): the heaviest weight the bundled sans families declare. The global style stays.
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
