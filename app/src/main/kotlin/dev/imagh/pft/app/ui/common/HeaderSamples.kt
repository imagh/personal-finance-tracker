package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise

/** A fake month total: Rs 48,250.40. */
private const val SAMPLE_SPEND_PAISE = 4_825_040L

private val SampleRows =
    listOf("Groceries", "Fuel", "Rent", "Electricity", "Phone", "Dining out", "Transport", "Health", "Gifts", "Savings")

/** Fake list rows, repeated so the content is long enough to scroll and collapse the header. */
private fun LazyListScope.sampleRows() {
    items(SampleRows + SampleRows + SampleRows) { name ->
        ListItem(headlineContent = { Text(name) }, supportingContent = { Text("Sample row") })
    }
}

/**
 * A `Standard` header screen with fake data: the single source for the preview, the debug
 * catalogue and the header goldens.
 */
@Composable
internal fun StandardHeaderSample() {
    CollapsingHeaderScaffold(
        title = "Budgets",
        subtitle = "October - 3 days left - 4 active",
        actions = {
            TextButton(onClick = {}) { Text("Back") }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {}) { Text("Search") }
        },
    ) { sampleRows() }
}

/** A `Dashboard` header screen with fake data (a `MoneyText` hero, caption and period stepper). */
@Composable
internal fun DashboardHeaderSample() {
    CollapsingHeaderScaffold(
        title = "October 2026",
        style = HeaderStyle.Dashboard,
        hero = { MoneyText(Paise(SAMPLE_SPEND_PAISE), MoneySemantic.Expense, size = MoneySize.Hero) },
        heroCaption = "spent this month",
        period = HeaderPeriod(onPrevious = {}, onNext = {}),
        actions = {
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {}) { Text("Filter") }
        },
    ) { sampleRows() }
}

@PreviewLightDark
@Composable
internal fun StandardHeaderPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface { StandardHeaderSample() } }
}

@PreviewLightDark
@Composable
internal fun DashboardHeaderPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface { DashboardHeaderSample() } }
}
