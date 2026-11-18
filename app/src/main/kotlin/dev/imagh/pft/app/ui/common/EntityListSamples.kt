package dev.imagh.pft.app.ui.common

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs

private fun sampleAction(label: String) = RowAction(label) {}

private fun sampleRow(
    title: String,
    supporting: String?,
    glyph: RowGlyph,
    indent: Boolean = false,
    vararg actions: String,
) = EntityRow(
    key = title,
    title = title,
    supporting = supporting,
    glyph = glyph,
    indent = indent,
    actions = actions.map(::sampleAction),
)

private fun glyph(
    symbol: PftSymbol,
    colour: String,
    label: String,
) = RowGlyph(GlyphSource.Symbol(symbol), colour, label)

/**
 * An `EntityListScreen` with fake rows: three active rows, one of them indented under its parent,
 * each with a badge and an overflow menu, and two archived rows under the collapsed "Archived (2)"
 * header. The single source for the preview and the goldens. Release builds drop it (R8; see
 * `verifyReleaseShrink`).
 */
@Composable
internal fun EntityListSample() {
    val snackbars = remember { SnackbarHostState() }
    EntityListScreen(
        title = "Sample list",
        active =
            listOf(
                sampleRow(
                    "Everyday",
                    "Savings",
                    glyph(PftSymbol.ACCOUNT_BALANCE, "blue", "Bank"),
                    false,
                    "Archive",
                    "Delete",
                ),
                sampleRow(
                    "Everyday card",
                    "Card",
                    glyph(PftSymbol.CREDIT_CARD, "violet", "Card"),
                    true,
                    "Archive",
                    "Delete",
                ),
                sampleRow("Wallet", null, glyph(PftSymbol.PAYMENTS, "emerald", "Cash"), false, "Archive", "Delete"),
            ),
        archived =
            listOf(
                sampleRow(
                    "Old card",
                    "Card",
                    glyph(PftSymbol.CREDIT_CARD, "violet", "Card"),
                    false,
                    "Unarchive",
                    "Delete",
                ),
                sampleRow(
                    "Old wallet",
                    null,
                    glyph(PftSymbol.ACCOUNT_BALANCE_WALLET, "teal", "Wallet"),
                    false,
                    "Unarchive",
                ),
            ),
        emptyMessage = "Nothing here yet",
        snackbarHost = { SnackbarHost(snackbars) },
        onBack = {},
        add = RowAction("Add sample") {},
    )
}

/** An `EntityListScreen` with no rows at all: the empty message and its add button. Same use as [EntityListSample]. */
@Composable
internal fun EntityListEmptySample() {
    val snackbars = remember { SnackbarHostState() }
    EntityListScreen(
        title = "Sample list",
        active = emptyList(),
        archived = emptyList(),
        emptyMessage = "Nothing here yet. Add the first one.",
        snackbarHost = { SnackbarHost(snackbars) },
        onBack = {},
        add = RowAction("Add sample") {},
    )
}

@PreviewLightDark
@Composable
internal fun EntityListPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { EntityListSample() } }
}

@PreviewLightDark
@Composable
internal fun EntityListEmptyPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { EntityListEmptySample() } }
}
