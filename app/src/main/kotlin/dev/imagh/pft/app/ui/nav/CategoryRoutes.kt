package dev.imagh.pft.app.ui.nav

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.categories.CategoriesNavigation
import dev.imagh.pft.app.ui.categories.CategoriesRoute
import dev.imagh.pft.app.ui.categories.CategoryEditRequest
import dev.imagh.pft.app.ui.categories.CategoryEditRoute

/**
 * The categories list and the category form (plan D13). The form takes three optional arguments, all
 * ids or enum names and all untrusted: `id` (edit that category; none: create), and, when creating,
 * `kind` (the kind's enum name; none: Expense) and `parent` (a sub-category's parent id). The list's add
 * button passes the kind on show; a row tap passes the category's id. The form pops on save. If an
 * argument is unknown, deleted or malformed the form pops too, after leaving a flag on the categories
 * entry's saved state, and the list says "That category no longer exists".
 */
internal fun NavGraphBuilder.categoryRoutes(
    navController: NavController,
    services: AppServices,
) {
    composable(Routes.CATEGORIES) { entry ->
        val notFound by entry.savedStateHandle
            .getStateFlow(CATEGORY_NOT_FOUND, false)
            .collectAsStateWithLifecycle()
        CategoriesRoute(
            services,
            CategoriesNavigation(
                back = { navController.popBackStack() },
                add = { kind -> navController.navigate(Routes.categoryEdit(kind = kind)) },
                edit = { navController.navigate(Routes.categoryEdit(id = it.value)) },
            ),
            notFound = notFound,
            onNotFoundShown = { entry.savedStateHandle[CATEGORY_NOT_FOUND] = false },
        )
    }
    composable(
        Routes.CATEGORY_EDIT,
        arguments =
            listOf(Routes.ARG_ID, Routes.ARG_PARENT, Routes.ARG_KIND).map { name ->
                navArgument(name) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            },
    ) { entry ->
        val arguments = entry.arguments
        CategoryEditRoute(
            services,
            // Untrusted: only compared as text against stored ids and enum names, never trusted to be one.
            CategoryEditRequest(
                categoryId = arguments?.getString(Routes.ARG_ID),
                parentId = arguments?.getString(Routes.ARG_PARENT),
                kindName = arguments?.getString(Routes.ARG_KIND),
            ),
            onDone = { navController.popBackStack() },
            onNotFound = {
                navController.previousBackStackEntry?.savedStateHandle?.set(CATEGORY_NOT_FOUND, true)
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() },
        )
    }
}

/** The saved-state flag the category form leaves on the categories entry when what it was asked for does not exist. */
private const val CATEGORY_NOT_FOUND = "category_not_found"
