@file:Suppress("MagicNumber") // sample amounts are fake literals by definition

package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise

/**
 * `MoneyText` in every size and semantic with fake amounts: a hero expense, the longest possible
 * hero (the ledger maximum, which shrinks to fit), a large income and a column of row amounts. The
 * single source for the preview, the debug catalogue and the goldens (also at font scale 2.0).
 * Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun MoneyTextSamples() {
    val spacing = PftTheme.spacing
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Caption("Hero")
        MoneyText(Paise(4_825_040), MoneySemantic.Expense, size = MoneySize.Hero)
        Caption("Hero, the largest amount")
        MoneyText(Paise.MAX_AMOUNT, MoneySemantic.Income, size = MoneySize.Hero)
        Caption("Large")
        MoneyText(Paise(12_500_000), MoneySemantic.Income, size = MoneySize.Large)
        Caption("Rows")
        AmountRow("Groceries", Paise(25_000), MoneySemantic.Expense)
        AmountRow("Salary", Paise(8_500_000), MoneySemantic.Income)
        AmountRow("To savings", Paise(1_000_000), MoneySemantic.Transfer)
        AmountRow("Savings", Paise(12_345_678), MoneySemantic.Balance)
        AmountRow("Credit card", Paise(-4_500_000), MoneySemantic.Balance)
        AmountRow("Dining, under", Paise(150_000), MoneySemantic.Budget(BudgetState.UNDER))
        AmountRow("Fuel, near limit", Paise(790_000), MoneySemantic.Budget(BudgetState.AT_THRESHOLD))
        AmountRow("Travel, over", Paise(910_000), MoneySemantic.Budget(BudgetState.OVER))
    }
}

@Composable
private fun Caption(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
}

@Composable
private fun AmountRow(
    label: String,
    amount: Paise,
    semantic: MoneySemantic,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        MoneyText(amount, semantic)
    }
}

@PreviewLightDark
@Composable
internal fun MoneyTextPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { MoneyTextSamples() } }
}
