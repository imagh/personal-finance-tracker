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
            HomeRoute(services, actions = { DevHeaderActions(navController::navigate) })
        }
        devRoutes()
    }
}
