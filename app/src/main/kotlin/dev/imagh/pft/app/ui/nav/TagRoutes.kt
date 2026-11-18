package dev.imagh.pft.app.ui.nav

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.tags.TagEditRoute
import dev.imagh.pft.app.ui.tags.TagsNavigation
import dev.imagh.pft.app.ui.tags.TagsRoute

/**
 * The tags list and the tag form (plan D13). The form takes an optional `id` argument (none: create) and
 * pops on save. If the id is unknown, deleted or malformed the form pops too, after leaving a flag on the
 * tags entry's saved state, and the list says "That tag no longer exists".
 */
internal fun NavGraphBuilder.tagRoutes(
    navController: NavController,
    services: AppServices,
) {
    composable(Routes.TAGS) { entry ->
        val notFound by entry.savedStateHandle
            .getStateFlow(TAG_NOT_FOUND, false)
            .collectAsStateWithLifecycle()
        TagsRoute(
            services,
            TagsNavigation(
                back = { navController.popBackStack() },
                add = { navController.navigate(Routes.tagEdit()) },
                edit = { navController.navigate(Routes.tagEdit(it.value)) },
            ),
            notFound = notFound,
            onNotFoundShown = { entry.savedStateHandle[TAG_NOT_FOUND] = false },
        )
    }
    composable(
        Routes.TAG_EDIT,
        arguments =
            listOf(
                navArgument(Routes.ARG_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
    ) { entry ->
        TagEditRoute(
            services,
            // Untrusted: only compared as text against stored ids, never trusted to be one.
            tagId = entry.arguments?.getString(Routes.ARG_ID),
            onDone = { navController.popBackStack() },
            onNotFound = {
                navController.previousBackStackEntry?.savedStateHandle?.set(TAG_NOT_FOUND, true)
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() },
        )
    }
}

/** The saved-state flag the tag form leaves on the tags entry when its tag does not exist. */
private const val TAG_NOT_FOUND = "tag_not_found"
