package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** One screenful of the token sheet: [slug] names its screenshot golden. */
internal class TokenSheetPage(
    val slug: String,
    val content: @Composable () -> Unit,
)

/**
 * The token sheet, split into pages that each fit one 393x851dp screen at 200% font scale, so
 * every page can be a golden screenshot.
 */
internal val TokenSheetPages: List<TokenSheetPage> =
    listOf(
        TokenSheetPage("type-display-headline") { TypeScale("Type scale: display, headline", DisplayAndHeadline) },
        TokenSheetPage("type-title-body-label") { TypeScale("Type scale: title, body, label", TitleBodyLabel) },
        TokenSheetPage("amounts") { AmountStylesSection() },
        TokenSheetPage("shapes-spacing") { ShapesAndSpacing() },
        TokenSheetPage("money") { MoneyTokensSection() },
    )

/** A page's content inside the sheet's standard padding. */
@Composable
internal fun TokenSheetPageFrame(
    page: TokenSheetPage,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(horizontal = PftTheme.spacing.gutter, vertical = PftTheme.spacing.lg)) { page.content() }
}
