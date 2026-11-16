package dev.imagh.pft.app.ui.nav

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.accounts.AccountEditRoute
import dev.imagh.pft.app.ui.accounts.AccountsNavigation
import dev.imagh.pft.app.ui.accounts.AccountsRoute

/**
 * The accounts list and the account form (plan D13). The form takes an optional `id` argument (none:
 * create) and pops on save. If the id is unknown, deleted or malformed the form pops too, after leaving
 * a flag on the accounts entry's saved state, and the list says "That account no longer exists".
 */
internal fun NavGraphBuilder.accountRoutes(
    navController: NavController,
    services: AppServices,
) {
    composable(Routes.ACCOUNTS) { entry ->
        val notFound by entry.savedStateHandle
            .getStateFlow(ACCOUNT_NOT_FOUND, false)
            .collectAsStateWithLifecycle()
        AccountsRoute(
            services,
            AccountsNavigation(
                back = { navController.popBackStack() },
                add = { navController.navigate(Routes.accountEdit()) },
                edit = { navController.navigate(Routes.accountEdit(it.value)) },
            ),
            notFound = notFound,
            onNotFoundShown = { entry.savedStateHandle[ACCOUNT_NOT_FOUND] = false },
        )
    }
    composable(
        Routes.ACCOUNT_EDIT,
        arguments =
            listOf(
                navArgument(Routes.ARG_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
    ) { entry ->
        AccountEditRoute(
            services,
            // Untrusted: only compared as text against stored ids, never trusted to be one.
            accountId = entry.arguments?.getString(Routes.ARG_ID),
            onDone = { navController.popBackStack() },
            onNotFound = {
                navController.previousBackStackEntry?.savedStateHandle?.set(ACCOUNT_NOT_FOUND, true)
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() },
        )
    }
}

/** The saved-state flag the account form leaves on the accounts entry when its account does not exist. */
private const val ACCOUNT_NOT_FOUND = "account_not_found"
