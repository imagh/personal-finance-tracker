package dev.imagh.pft.app.ui.nav

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.transactions.TransactionEditNavigation
import dev.imagh.pft.app.ui.transactions.TransactionEditRequest
import dev.imagh.pft.app.ui.transactions.TransactionEditRoute
import dev.imagh.pft.app.ui.transactions.TransactionsNavigation
import dev.imagh.pft.app.ui.transactions.TransactionsRoute
import dev.imagh.pft.ledger.query.LedgerEntry
import java.time.Clock

/**
 * The Transactions tab's root (slice C D7). The "+" opens a new transaction and a row tap opens the edit form for
 * that entry (a transfer by its outgoing leg's id; the form finds both legs). If the form finds its id unknown,
 * deleted or malformed it pops after leaving a flag on this entry's saved state, and the tab says "That
 * transaction no longer exists".
 */
internal fun NavGraphBuilder.transactionsTabRoot(
    navController: NavController,
    services: AppServices,
    clock: () -> Clock,
) {
    composable(Routes.TRANSACTIONS) { entry ->
        val notFound by entry.savedStateHandle
            .getStateFlow(TRANSACTION_NOT_FOUND, false)
            .collectAsStateWithLifecycle()
        TransactionsRoute(
            services,
            clock,
            TransactionsNavigation(
                onAdd = { navController.navigate(Routes.transactionEdit()) },
                onOpen = { navController.navigate(Routes.transactionEdit(id = idOf(it))) },
            ),
            notFound = notFound,
            onNotFoundShown = { entry.savedStateHandle[TRANSACTION_NOT_FOUND] = false },
        )
    }
}

/**
 * The transaction form, a direct child of `MAIN` so any tab can push it (slice C D7). `id` (none: create) and
 * `type` (a preset for a create) are untrusted text, judged by the ViewModel. The form pops on save; if the
 * transaction does not exist it pops too, after leaving a flag on the entry underneath.
 */
internal fun NavGraphBuilder.transactionEditRoute(
    navController: NavController,
    services: AppServices,
    clock: () -> Clock,
) {
    composable(
        Routes.TRANSACTION_EDIT,
        arguments =
            listOf(Routes.ARG_ID, Routes.ARG_TYPE).map { name ->
                navArgument(name) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            },
    ) { entry ->
        TransactionEditRoute(
            services,
            clock,
            TransactionEditRequest(
                id = entry.arguments?.getString(Routes.ARG_ID),
                type = entry.arguments?.getString(Routes.ARG_TYPE),
            ),
            TransactionEditNavigation(
                onDone = { navController.popBackStack() },
                onNotFound = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(TRANSACTION_NOT_FOUND, true)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
            ),
        )
    }
}

/** The id the edit form is opened with for [entry]: the transaction's, or a transfer's outgoing leg's. */
private fun idOf(entry: LedgerEntry): String =
    when (entry) {
        is LedgerEntry.Single -> entry.transaction.meta.id.value
        is LedgerEntry.Transfer -> entry.outgoing.meta.id.value
    }

/** The saved-state flag the transaction form leaves on the entry underneath when its transaction does not exist. */
private const val TRANSACTION_NOT_FOUND = "transaction_not_found"
