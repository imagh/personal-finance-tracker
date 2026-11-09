package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.CollapsingHeaderScaffold
import dev.imagh.pft.app.ui.common.EmptyState

/**
 * The Home placeholder, drawn from [state] alone: a "Home" header whose subtitle says who is using
 * this phone ("<name> · <phone>", so the owner can see attribution), and one honest line saying the
 * real content is not there yet. C4 replaces it. It holds no state and knows no ViewModel, so tests
 * render it from fake data; [HomeRoute] wires the real one.
 *
 * @param state what to show; the subtitle appears once [HomeUiState.loaded].
 * @param actions the header's secondary actions (only `HeaderIconButton`s, UI-14); the debug build
 *   puts its design-catalogue button here, release passes nothing.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CollapsingHeaderScaffold(
        title = stringResource(R.string.home_title),
        modifier = modifier,
        subtitle =
            if (state.loaded) stringResource(R.string.home_subtitle, state.userName, state.deviceLabel) else null,
        actions = actions,
    ) {
        item { EmptyState(stringResource(R.string.home_placeholder)) }
    }
}

/**
 * Home wired to its [HomeViewModel]: the only place that touches the ViewModel.
 *
 * @param services the open object graph the identity is read through.
 * @param actions the header actions, as for [HomeScreen].
 */
@Composable
fun HomeRoute(
    services: AppServices,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val factory = remember(services) { HomeViewModel.factory(services) }
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state, modifier, actions)
}
