package dev.imagh.pft.app.ui.nav

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import dev.imagh.pft.app.data.PftDatabase

/** The number of rows in [table], read straight from the database (the persisted outcome, not a mock). */
internal fun PftDatabase.rowCount(table: String): Long =
    openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table").use { cursor ->
        cursor.moveToFirst()
        cursor.getLong(0)
    }

/** The one line Home's placeholder says: it is on screen exactly when Home is. */
internal const val HOME_PLACEHOLDER =
    "Manage your accounts from the Accounts tab. Transactions arrive in the next update"

/** Matches the tab bar's tabs (`Role.Tab`). */
internal val isTab = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)

/** The tab named [label]: the active one speaks its visible text, an inactive one its content description. */
internal fun SemanticsNodeInteractionsProvider.tab(label: String): SemanticsNodeInteraction =
    onNode(isTab and (hasText(label) or hasContentDescription(label)))

/** How many tabs the bar shows right now (zero when it is hidden). */
internal fun SemanticsNodeInteractionsProvider.tabCount(): Int = onAllNodes(isTab).fetchSemanticsNodes().size

/** The labels of the tabs that are selected, as TalkBack would speak them. */
internal fun SemanticsNodeInteractionsProvider.selectedTabs(): List<String> =
    onAllNodes(isTab)
        .fetchSemanticsNodes()
        .filter { it.config.getOrNull(SemanticsProperties.Selected) == true }
        .map { it.spokenName() }

private fun SemanticsNode.spokenName(): String =
    (
        config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } +
            config.getOrNull(SemanticsProperties.ContentDescription).orEmpty()
    ).single()
