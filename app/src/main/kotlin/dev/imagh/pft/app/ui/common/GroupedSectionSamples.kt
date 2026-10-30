@file:Suppress("MagicNumber") // sample amounts are fake literals by definition

package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise

/**
 * Two `GroupedSection`s with fake data: an accounts island of `ListItem`s ending in a `MoneyText`
 * (one balance negative), and a facts island with a `StatusChip` and a plain non-clickable row. The
 * single source for the preview, the debug catalogue and the goldens. Release builds drop it (R8;
 * see `verifyReleaseShrink`).
 */
@Composable
internal fun GroupedSectionSamples() {
    val colors = GroupedSectionDefaults.listItemColors()
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        GroupedSection {
            row(onClick = {}) {
                ListItem(
                    headlineContent = { Text("Everyday account") },
                    supportingContent = { Text("Savings") },
                    trailingContent = { MoneyText(Paise(12_345_678), MoneySemantic.Balance) },
                    colors = colors,
                )
            }
            row(onClick = {}) {
                ListItem(
                    headlineContent = { Text("Wallet") },
                    supportingContent = { Text("Cash") },
                    trailingContent = { MoneyText(Paise(240_000), MoneySemantic.Balance) },
                    colors = colors,
                )
            }
            row(onClick = {}) {
                ListItem(
                    headlineContent = { Text("Credit card") },
                    supportingContent = { Text("Card") },
                    trailingContent = { MoneyText(Paise(-4_500_000), MoneySemantic.Balance) },
                    colors = colors,
                )
            }
        }
        GroupedSection {
            row {
                ListItem(
                    headlineContent = { Text("Status") },
                    trailingContent = { StatusChip("Unverified", MoneyTone.Warning) },
                    colors = colors,
                )
            }
            row {
                ListItem(
                    headlineContent = { Text("Period") },
                    trailingContent = { Text("Monthly, from the 1st") },
                    colors = colors,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun GroupedSectionPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { GroupedSectionSamples() } }
}
