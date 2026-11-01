package dev.imagh.pft.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.app.DevContent
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.ui.startup.DataUnavailableCause
import dev.imagh.pft.app.ui.startup.DataUnavailableScreen

/**
 * The root of everything the activity shows: it asks [container] to open the database (through
 * [LaunchViewModel], off the main thread) and draws what the answer calls for.
 *
 * - [LaunchState.Opening]: nothing. The window background already equals the canvas colour, so there
 *   is no flash while the database opens.
 * - [LaunchState.KeyUnavailable] and [LaunchState.IdentityCorrupt]: the fail-closed
 *   [DataUnavailableScreen] with a fixed code. Nothing is created or overwritten.
 * - [LaunchState.Ready]: the app. Until the navigation host arrives (A5f) that is [DevContent].
 *
 * The caller provides the theme and the surface (`MainActivity`).
 */
@Composable
fun PftRoot(
    container: AppContainer,
    modifier: Modifier = Modifier,
) {
    val factory = remember(container) { LaunchViewModel.factory(container) }
    val launch: LaunchViewModel = viewModel(factory = factory)
    val state by launch.state.collectAsStateWithLifecycle()
    when (val current = state) {
        LaunchState.Opening -> Unit
        is LaunchState.KeyUnavailable ->
            DataUnavailableScreen(DataUnavailableCause.KeyUnavailable(current.reason), modifier)
        LaunchState.IdentityCorrupt ->
            DataUnavailableScreen(DataUnavailableCause.IdentityCorrupt, modifier)
        is LaunchState.Ready -> DevContent()
    }
}
