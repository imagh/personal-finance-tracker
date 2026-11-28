package dev.imagh.pft.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dev.imagh.pft.app.DevHeaderActions
import dev.imagh.pft.app.devRoutes
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.LocalTabBarClearance
import dev.imagh.pft.app.ui.home.HomeRoute
import dev.imagh.pft.app.ui.more.MoreRoute
import dev.imagh.pft.app.ui.setup.SetupRoute
import dev.imagh.pft.app.ui.theme.PftMotion
import java.time.Clock

/**
 * The app's navigation host and its floating tab bar (Cobalt plan D8). It starts on Setup for a fresh
 * install ([hasIdentity] false) and on the app proper otherwise, so a later launch never shows Setup.
 *
 * **The graph.** The root graph starts on `SETUP` or on `MAIN`, a nested graph holding the four tab
 * graphs: `home_tab` (Home, and the debug catalogue), `transactions_tab` (the month list), `accounts_tab`
 * (the accounts list and its form) and `more_tab` (More, the categories and tags lists and their forms).
 * Each tab graph starts on its root screen. The tabs and their order are [TAB_ORDER]; slice D inserts
 * Budgets there. The screens several tabs reach are direct children of `MAIN`: the transaction form
 * (C4b, see [transactionEditRoute]) now, search later.
 *
 * **Tabs.** Tapping a tab switches to its graph with its own back stack saved and restored
 * ([selectTab]), so each tab keeps where the user was (the Transactions tab its selected month). Tapping
 * the active tab pops it to its root. The
 * bar shows only while the current destination is a tab root: it is hidden on Setup, forms, and the
 * lists under More, which keep their Back button. The floating bar covers the bottom of a tab root, so
 * the host tells the lists how much through `LocalTabBarClearance` (read from the bar's measured size).
 *
 * **Back.** Back on a tab root other than Home goes to Home; Back on Home leaves the app. Home is the
 * base of every tab switch (`popUpTo(HOME)`), so no tab can pile up under another.
 *
 * - **Setup to the app** pops Setup off the back stack (`popUpTo(SETUP) { inclusive = true }`), so Back
 *   on Home leaves the app and never returns to Setup.
 * - **Transitions** are the design system's `PftMotion.navEnter/navExit/navPopEnter/navPopExit`, for
 *   every route.
 * - **Predictive back** is enabled by the manifest's `enableOnBackInvokedCallback`; the host's own
 *   back handling drives the pop transition.
 *
 * - **Accounts to the account form** is a push with an optional `id` argument (none: create). The form
 *   pops on save. If the id is unknown, deleted or malformed the form pops too, after leaving a flag
 *   on the accounts entry's saved state, and the list says "That account no longer exists".
 *
 * - **More to Categories** (and **Tags**) is a push; **Categories to the category form** is a push with
 *   optional `id`, `kind` and `parent` arguments (see [categoryRoutes]): the add button passes the kind
 *   on show, a row tap passes the id. The form pops on save; an unknown, deleted or malformed argument
 *   pops it too, with the list saying "That category no longer exists".
 *
 * - **Tags to the tag form** is a push with an optional `id` argument (none: create), like accounts (see
 *   [tagRoutes]). The form pops on save; an unknown, deleted or malformed id pops it too, with the list
 *   saying "That tag no longer exists".
 *
 * - **Transactions to the transaction form** is a push (see [transactionsTabRoot], [transactionEditRoute]): the
 *   "+" opens a new one, a row tap opens that entry for editing, with optional `id` and `type` arguments. The
 *   form hides the bar (it is not a tab root) and pops on save; an unknown, deleted, itemised or malformed id
 *   pops it too, with the tab saying "That transaction no longer exists".
 *
 * Debug builds add the design catalogue as a route in the Home tab, reached from a Home header action
 * (`devRoutes()`, `DevHeaderActions`); release adds nothing. There are no deep links: nothing
 * outside the app can navigate it.
 *
 * @param services the open object graph the screens' ViewModels are built over.
 * @param hasIdentity whether setup has already happened (from `LaunchState.Ready`).
 * @param navController the controller; tests pass their own to drive a tab switch the bar does not
 *   offer (from a form, where the bar is hidden).
 * @param clock gives the clock for each read of the Transactions tab: its instant is "now" and its zone is the
 *   user's. Production asks the system for both every time, so a device zone change shows on the next read;
 *   a test passes a fixed one.
 */
@Composable
fun PftNavHost(
    services: AppServices,
    hasIdentity: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    clock: () -> Clock = { Clock.systemDefaultZone() },
) {
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val selected = TAB_ORDER.firstOrNull { tab -> destination.isIn(tab.graph) }
    val barShown = destination?.route in TAB_ORDER.map { it.root }

    var barHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    // The bar's box includes the system navigation inset, which the lists' scaffold already pads by.
    val navInset = WindowInsets.navigationBars.getBottom(density)
    // Kept while the bar is hidden: only tab roots read it, and zeroing it would drop their add button
    // mid-transition as a form slides in over them.
    val clearance = with(density) { (barHeight - navInset).coerceAtLeast(0).toDp() }

    Box(modifier) {
        CompositionLocalProvider(LocalTabBarClearance provides clearance) {
            PftNavGraph(navController, services, hasIdentity, clock)
        }
        if (barShown && selected != null) {
            PftTabBar(
                TAB_ORDER,
                selected,
                onSelect = navController::selectTab,
                modifier = Modifier.align(Alignment.BottomCenter).onSizeChanged { barHeight = it.height },
            )
        }
    }
}

/** The navigation graph itself; see [PftNavHost] for the shape and the behaviour. */
@Composable
private fun PftNavGraph(
    navController: NavHostController,
    services: AppServices,
    hasIdentity: Boolean,
    clock: () -> Clock,
) {
    NavHost(
        navController = navController,
        startDestination = if (hasIdentity) Routes.MAIN else Routes.SETUP,
        enterTransition = { PftMotion.navEnter },
        exitTransition = { PftMotion.navExit },
        popEnterTransition = { PftMotion.navPopEnter },
        popExitTransition = { PftMotion.navPopExit },
    ) {
        composable(Routes.SETUP) {
            SetupRoute(
                services,
                onDone = {
                    navController.navigate(Routes.MAIN) { popUpTo(Routes.SETUP) { inclusive = true } }
                },
            )
        }
        navigation(route = Routes.MAIN, startDestination = Tab.HOME.graph) {
            navigation(route = Tab.HOME.graph, startDestination = Tab.HOME.root) {
                composable(Tab.HOME.root) {
                    HomeRoute(services, actions = { DevHeaderActions(navController::navigate) })
                }
                devRoutes()
            }
            navigation(route = Tab.TRANSACTIONS.graph, startDestination = Tab.TRANSACTIONS.root) {
                transactionsTabRoot(navController, services, clock)
            }
            navigation(route = Tab.ACCOUNTS.graph, startDestination = Tab.ACCOUNTS.root) {
                accountRoutes(navController, services)
            }
            navigation(route = Tab.MORE.graph, startDestination = Tab.MORE.root) {
                composable(Tab.MORE.root) {
                    MoreRoute(
                        services,
                        onOpenCategories = { navController.navigate(Routes.CATEGORIES) },
                        onOpenTags = { navController.navigate(Routes.TAGS) },
                    )
                }
                categoryRoutes(navController, services)
                tagRoutes(navController, services)
            }
            transactionEditRoute(navController, services, clock)
        }
    }
}

/**
 * Switches to [tab] (plan D8). From another tab it navigates to the tab's graph, popping up to Home's root
 * screen with the current tab's stack saved and the target's restored, so a tab comes back as it was left;
 * `launchSingleTop` stops a second copy of a graph piling up. From the tab itself it pops to the tab's
 * root screen.
 */
internal fun NavController.selectTab(tab: Tab) {
    if (currentDestination.isIn(tab.graph)) {
        popBackStack(tab.root, inclusive = false)
        return
    }
    navigate(tab.graph) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Whether this destination is, or sits inside, the graph [graph]. */
private fun NavDestination?.isIn(graph: String): Boolean = this?.hierarchy?.any { it.route == graph } == true
