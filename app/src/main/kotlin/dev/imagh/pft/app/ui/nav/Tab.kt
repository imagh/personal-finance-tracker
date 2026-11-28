package dev.imagh.pft.app.ui.nav

import androidx.annotation.StringRes
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftSymbol

/**
 * A tab of the bottom bar (Cobalt plan D8). Each tab owns a nested navigation graph ([graph]) whose
 * start destination is its root screen ([root]), so a tab keeps its own back stack.
 *
 * The enum's declaration order means nothing: the bar's order is [TAB_ORDER] alone.
 *
 * @property graph the tab's nested graph route (what a tap navigates to).
 * @property root the tab's root screen route (where re-tapping the active tab returns to).
 * @property icon the tab's symbol; the only thing an inactive tab draws.
 * @property label the tab's name: shown beside the icon when active, spoken when inactive.
 */
internal enum class Tab(
    val graph: String,
    val root: String,
    val icon: PftSymbol,
    @StringRes val label: Int,
) {
    /** The dashboard; where the app starts and where Back from another tab goes. */
    HOME(Routes.HOME_TAB, Routes.HOME, PftSymbol.HOME, R.string.tab_home),

    /** The month list: the month strip, the totals banner and the entries by day. */
    TRANSACTIONS(
        Routes.TRANSACTIONS_TAB,
        Routes.TRANSACTIONS,
        PftSymbol.RECEIPT_LONG,
        R.string.tab_transactions,
    ),

    /** The accounts list. */
    ACCOUNTS(Routes.ACCOUNTS_TAB, Routes.ACCOUNTS, PftSymbol.ACCOUNT_BALANCE_WALLET, R.string.tab_accounts),

    /** Categories, tags and (later) settings. */
    MORE(Routes.MORE_TAB, Routes.MORE, PftSymbol.MORE_HORIZ, R.string.tab_more),
}

/**
 * The bar's tabs, left to right: the one place the order is written. The M1a end state is Home,
 * Transactions, Budgets, Accounts, More (plan D8): Transactions is at position 2 since slice C (C4a) and slice D
 * inserts Budgets at position 3, an insert here, never a move. [Tab.HOME] stays first, because Back from
 * another tab goes to the first tab's root and the graph starts there.
 */
internal val TAB_ORDER: List<Tab> = listOf(Tab.HOME, Tab.TRANSACTIONS, Tab.ACCOUNTS, Tab.MORE)
