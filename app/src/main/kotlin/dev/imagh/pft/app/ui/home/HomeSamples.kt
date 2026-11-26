@file:Suppress("MagicNumber") // sample amounts are fake literals by definition

package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise

private val SampleCard =
    BalanceCardModel(
        title = "Household · November",
        expense = Paise(4_825_040),
        income = Paise(12_000_000),
    )

private val SampleBudget = BudgetMeter(fraction = 0.64f, label = "64% of ₹75,000.00 budget · 5 days left")

private val SampleActions =
    listOf(
        QuickAction("Expense", PftSymbol.ADD, onClick = {}, primary = true),
        QuickAction("Income", PftSymbol.SOUTH_WEST, onClick = {}),
        QuickAction("Transfer", PftSymbol.TRANSFER, onClick = {}),
    )

/**
 * `BalanceCard` with fake data, twice: as Home shows it in M1a (no budget, so no meter) and as it
 * will with a budget (slice D). The single source for the preview, the debug catalogue and the
 * goldens. Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun BalanceCardSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        BalanceCard(SampleCard)
        BalanceCard(SampleCard.copy(budget = SampleBudget))
    }
}

/**
 * `QuickActionRow` with fake data, twice: the three actions Home shows in M1a and the four it will
 * show once Budgets exists (slice D). The single source for the preview, the debug catalogue and the
 * goldens. Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun QuickActionRowSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        QuickActionRow(SampleActions)
        QuickActionRow(SampleActions + QuickAction("Budgets", PftSymbol.SAVINGS, onClick = {}))
    }
}

/**
 * `GreetingHeader` with fake names, twice: a two-word name in the morning and a long one in the evening
 * (the name truncates; the initials stay). The single source for the preview, the debug catalogue and the
 * goldens. Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun GreetingHeaderSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        GreetingHeader(Greeting.MORNING, "Akash Gupta")
        GreetingHeader(Greeting.EVENING, "Priyadarshini Venkataraghavan Subramaniam")
    }
}

@PreviewLightDark
@Composable
internal fun GreetingHeaderPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { GreetingHeaderSamples() } }
}

@PreviewLightDark
@Composable
internal fun BalanceCardPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { BalanceCardSamples() } }
}

@PreviewLightDark
@Composable
internal fun QuickActionRowPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { QuickActionRowSamples() } }
}
