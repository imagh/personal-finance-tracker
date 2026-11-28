@file:Suppress("MagicNumber") // sample amounts are fake literals by definition

package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.money.Paise
import java.time.LocalDate

/** The fixed "today" of the samples, so the day headers and the goldens never depend on the clock. */
private val SampleToday: LocalDate = LocalDate.of(2026, 11, 25)

private val Swiggy =
    TransactionRowModel(
        title = "Swiggy",
        glyph = GlyphSource.Symbol(PftSymbol.RESTAURANT),
        colourKey = "coral",
        category = "Dining out",
        account = "HDFC card",
        amount = Paise(48_600),
        semantic = MoneySemantic.Expense,
        onClick = {},
    )

private val BigBasket =
    Swiggy.copy(
        title = "BigBasket",
        glyph = GlyphSource.Symbol(PftSymbol.GROCERY),
        colourKey = "emerald",
        category = "Groceries",
        account = "UPI",
        amount = Paise(231_450),
    )

private val Salary =
    Swiggy.copy(
        title = "Salary",
        glyph = GlyphSource.Symbol(PftSymbol.WORK),
        colourKey = "blue",
        category = "Income",
        account = "Everyday",
        amount = Paise(12_000_000),
        semantic = MoneySemantic.Income,
    )

private val Uber =
    Swiggy.copy(
        title = "Uber",
        glyph = GlyphSource.Symbol(PftSymbol.DIRECTIONS_CAR),
        colourKey = "violet",
        category = "Transport",
        account = "UPI",
        amount = Paise(31_200),
    )

private val Rent =
    Swiggy.copy(
        title = "Rent",
        glyph = GlyphSource.Symbol(PftSymbol.HOME),
        colourKey = "teal",
        category = "Housing",
        account = "Everyday",
        amount = Paise(2_800_000),
    )

private val Transfer =
    Rent.copy(
        title = "To savings",
        glyph = GlyphSource.Symbol(PftSymbol.TRANSFER),
        category = null,
        amount = Paise(500_000),
        semantic = MoneySemantic.Transfer,
    )

private val NeedsReview =
    Swiggy.copy(
        title = "BESCOM",
        glyph = GlyphSource.Symbol(PftSymbol.BOLT),
        colourKey = "amber",
        category = "Bills",
        account = "Wallet",
        amount = Paise(184_200),
        needsReview = true,
    )

private val Tagged =
    Swiggy.copy(
        title = "Goa trip hotel",
        glyph = GlyphSource.Symbol(PftSymbol.BED),
        colourKey = "violet",
        category = "Travel",
        amount = Paise(1_850_000),
        tags = listOf("Holiday", "Reimbursable"),
    )

/**
 * `TransactionRow` with fake data: an expense, an income, a transfer (no category), an unconfirmed
 * auto-logged entry with the "Needs review" chip, and a row with tag chips, in one island. The single
 * source for the preview, the debug catalogue and the goldens. Release builds drop it (R8; see
 * `verifyReleaseShrink`).
 */
@Composable
internal fun TransactionRowSamples() {
    GroupedSection {
        listOf(Swiggy, Salary, Transfer, NeedsReview, Tagged).forEach { model -> row { TransactionRow(model) } }
    }
}

/**
 * `DayGroup` with fake data: Today with a negative net total, Yesterday with a positive one, and an
 * older date with no total. The single source for the preview, the debug catalogue and the goldens.
 * Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun DayGroupSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm)) {
        DayGroup(SampleToday, SampleToday, listOf(Swiggy, BigBasket), total = Paise(-280_050))
        DayGroup(SampleToday.minusDays(1), SampleToday, listOf(Salary, Uber), total = Paise(11_968_800))
        DayGroup(SampleToday.minusDays(5), SampleToday, listOf(Rent))
    }
}

@PreviewLightDark
@Composable
internal fun TransactionRowPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { TransactionRowSamples() } }
}

@PreviewLightDark
@Composable
internal fun DayGroupPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { DayGroupSamples() } }
}
