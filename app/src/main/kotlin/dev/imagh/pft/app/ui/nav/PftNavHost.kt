package dev.imagh.pft.app.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.imagh.pft.app.DevHeaderActions
import dev.imagh.pft.app.devRoutes
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.home.HomeRoute
import dev.imagh.pft.app.ui.more.MoreScreen
import dev.imagh.pft.app.ui.setup.SetupRoute
import dev.imagh.pft.app.ui.theme.PftMotion

/**
 * The app's navigation host. It starts on Setup for a fresh install ([hasIdentity] false) and on Home
 * otherwise, so a later launch never shows Setup.
 *
 * - **Setup to Home** pops Setup off the back stack (`popUpTo(SETUP) { inclusive = true }`), so Back
 *   on Home leaves the app and never returns to Setup.
 * - **Transitions** are the design system's `PftMotion.navEnter/navExit/navPopEnter/navPopExit`, for
 *   every route.
 * - **Predictive back** is enabled by the manifest's `enableOnBackInvokedCallback`; the host's own
 *   back handling drives the pop transition.
 *
 * - **Home to More to Accounts or Categories** are plain pushes (`navigate`); Back pops one screen, so
 *   Back from either list returns to More and from More to Home. Home's header More button is the entry point
 *   until C4's bottom navigation bar replaces it (plan D9).
 *
 * - **Accounts to the account form** is a push with an optional `id` argument (none: create). The form
 *   pops on save. If the id is unknown, deleted or malformed the form pops too, after leaving a flag
 *   on the accounts entry's saved state, and the list says "That account no longer exists".
 *
 * - **Categories to the category form** is a push with optional `id`, `kind` and `parent` arguments (see
 *   [categoryRoutes]): the add button passes the kind on show, a row tap passes the id. The form pops on
 *   save; an unknown, deleted or malformed argument pops it too, with the list saying "That category no
 *   longer exists".
 *
 * Debug builds add the design catalogue as a route, reached from a Home header action
 * (`devRoutes()`, `DevHeaderActions`); release adds nothing. There are no deep links: nothing
 * outside the app can navigate it.
 *
 * @param services the open object graph the screens' ViewModels are built over.
 * @param hasIdentity whether setup has already happened (from `LaunchState.Ready`).
 */
@Composable
fun PftNavHost(
    services: AppServices,
    hasIdentity: Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (hasIdentity) Routes.HOME else Routes.SETUP,
        modifier = modifier,
        enterTransition = { PftMotion.navEnter },
        exitTransition = { PftMotion.navExit },
        popEnterTransition = { PftMotion.navPopEnter },
        popExitTransition = { PftMotion.navPopExit },
    ) {
        composable(Routes.SETUP) {
            SetupRoute(
                services,
                onDone = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.SETUP) { inclusive = true } }
                },
            )
        }
        composable(Routes.HOME) {
            HomeRoute(
                services,
                onOpenMore = { navController.navigate(Routes.MORE) },
                actions = { DevHeaderActions(navController::navigate) },
            )
        }
        composable(Routes.MORE) {
            MoreScreen(
                onBack = { navController.popBackStack() },
                onOpenAccounts = { navController.navigate(Routes.ACCOUNTS) },
                onOpenCategories = { navController.navigate(Routes.CATEGORIES) },
            )
        }
        accountRoutes(navController, services)
        categoryRoutes(navController, services)
        devRoutes()
    }
}
