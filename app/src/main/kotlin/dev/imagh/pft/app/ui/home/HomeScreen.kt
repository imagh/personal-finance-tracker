package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import dev.imagh.pft.app.ui.common.HeaderIconButton
import dev.imagh.pft.app.ui.theme.PftSymbol

/**
 * The Home placeholder, drawn from [state] alone: a "Home" header whose subtitle says who is using
 * this phone ("<name> · <phone>", so the owner can see attribution), and one honest line saying the
 * real content is not there yet. C4 replaces it. It holds no state and knows no ViewModel, so tests
 * render it from fake data; [HomeRoute] wires the real one.
 *
 * @param state what to show; the subtitle appears once [HomeUiState.loaded].
 * @param onOpenMore called when the header's More button is tapped (plan D9). It is always there,
 *   even before the identity has loaded; C4 moves it into the bottom navigation bar.
 * @param actions the caller's extra header actions (only `HeaderIconButton`s, UI-14), drawn before
 *   More; the debug build puts its design-catalogue button here, release passes nothing.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onOpenMore: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val moreLabel = stringResource(R.string.home_more)
    CollapsingHeaderScaffold(
        title = stringResource(R.string.home_title),
        modifier = modifier,
        subtitle =
            if (state.loaded) stringResource(R.string.home_subtitle, state.userName, state.deviceLabel) else null,
        actions = {
            // The caller's actions (the debug catalogue button, which brings its own spacer) come first
            // and More is last, so both end up at the end of the row; the leading spacer takes the
            // free width, and the two weighted spacers sum when the caller adds one.
            Spacer(Modifier.weight(1f))
            actions()
            HeaderIconButton(PftSymbol.OVERFLOW, moreLabel, onOpenMore)
        },
    ) {
        item { EmptyState(stringResource(R.string.home_placeholder)) }
    }
}

/**
 * Home wired to its [HomeViewModel]: the only place that touches the ViewModel.
 *
 * @param services the open object graph the identity is read through.
 * @param onOpenMore opens the More screen, as for [HomeScreen].
 * @param actions the header actions, as for [HomeScreen].
 */
@Composable
fun HomeRoute(
    services: AppServices,
    onOpenMore: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val factory = remember(services) { HomeViewModel.factory(services) }
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state, onOpenMore, modifier, actions)
}
