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
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.testing.awaitMain

/** The number of rows in [table], read straight from the database (the persisted outcome, not a mock). */
internal fun PftDatabase.rowCount(table: String): Long =
    openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table").use { cursor ->
        cursor.moveToFirst()
        cursor.getLong(0)
    }

/** The one line Home's placeholder says: it is on screen exactly when Home is. */
internal const val HOME_PLACEHOLDER =
    "Manage your accounts from the Accounts tab. Transactions arrive in the next update"

/**
 * True once Home is on screen **and** its profile has loaded (the greeting, "Good morning" and its
 * siblings, only draws after `HomeViewModel` has read the identity). The placeholder alone is drawn
 * before that read finishes, so a test that tore the database down on seeing it could close the
 * connection pool under an in-flight read, and the error would surface in the next test.
 */
internal fun SemanticsNodeInteractionsProvider.homeLoaded(): Boolean =
    onAllNodesWithText(HOME_PLACEHOLDER).fetchSemanticsNodes().isNotEmpty() &&
        onAllNodesWithText("Good ", substring = true).fetchSemanticsNodes().isNotEmpty()

/** Waits (driving the main looper, no sleeps beyond [awaitMain]'s) until [homeLoaded]. */
internal fun ComposeTestRule.awaitHomeLoaded() =
    awaitMain {
        waitForIdle()
        homeLoaded()
    }

/**
 * True once the More tab's root is on screen **and** its attribution subtitle ("<name> · <phone>") has
 * loaded: `MoreViewModel` reads the identity after the rows are drawn, so the same teardown race as
 * [homeLoaded] applies. The "Categories" and "Tags" rows are More's own; a " · " text appears only once
 * the identity read has finished.
 */
internal fun SemanticsNodeInteractionsProvider.moreLoaded(): Boolean =
    onAllNodesWithText("Categories").fetchSemanticsNodes().isNotEmpty() &&
        onAllNodesWithText("Tags").fetchSemanticsNodes().isNotEmpty() &&
        onAllNodesWithText(" · ", substring = true).fetchSemanticsNodes().isNotEmpty()

/** Waits until [moreLoaded]. */
internal fun ComposeTestRule.awaitMoreLoaded() =
    awaitMain {
        waitForIdle()
        moreLoaded()
    }

/** Taps the More tab and waits until More's identity read has finished (see [moreLoaded]). */
internal fun ComposeTestRule.openMore() {
    tab("More").performClick()
    awaitMoreLoaded()
}

/** Matches the tab bar's tabs (`Role.Tab`). */
internal val isTab = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)

/** The tab named [label]: the active one speaks its visible text, an inactive one its content description. */
internal fun SemanticsNodeInteractionsProvider.tab(label: String): SemanticsNodeInteraction =
    onNode(isTab and (hasText(label) or hasContentDescription(label)))

/** How many tabs the bar shows right now (zero when it is hidden). */
internal fun SemanticsNodeInteractionsProvider.tabCount(): Int = onAllNodes(isTab).fetchSemanticsNodes().size

/** The labels of every tab, left to right, as TalkBack would speak them. */
internal fun SemanticsNodeInteractionsProvider.tabLabels(): List<String> =
    onAllNodes(isTab)
        .fetchSemanticsNodes()
        .sortedBy { it.boundsInRoot.left }
        .map { it.spokenName() }

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
