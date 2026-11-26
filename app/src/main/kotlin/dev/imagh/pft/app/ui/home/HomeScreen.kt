package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.imagh.pft.R
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.ui.common.EmptyState
import dev.imagh.pft.app.ui.common.LocalTabBarClearance
import java.time.Clock

/**
 * Home (plan D9): a plain list that starts with the [GreetingHeader] and, for now, the one honest line
 * saying the dashboard body is not wired yet (slice C brings the balance card, quick actions and recent
 * transactions). Home has no title bar and no More button (More is a tab), and the attribution line
 * ("<name> · <phone>") is the More tab's subtitle (plan D8).
 *
 * The header is drawn only once [HomeUiState.loaded], so nothing flashes before the profile is read.
 * Under the floating tab bar, `LocalTabBarClearance` adds the same space below the last item.
 *
 * Stateless, drawn from [state] alone; [HomeRoute] wires the ViewModel.
 *
 * @param state the user's name and whether it has loaded.
 * @param clock the clock and zone that choose the greeting (a test injects a fixed one).
 * @param actions the caller's extra header actions (only `HeaderIconButton`s, UI-14); the debug build
 *   puts its design-catalogue button here, release passes nothing.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    clock: Clock = Clock.systemDefaultZone(),
    actions: @Composable RowScope.() -> Unit = {},
) {
    val greeting = remember(clock) { greetingAt(clock) }
    val barClearance = LocalTabBarClearance.current
    LazyColumn(modifier, contentPadding = WindowInsets.safeDrawing.asPaddingValues()) {
        if (state.loaded) item { GreetingHeader(greeting, state.userName, actions = actions) }
        item { EmptyState(stringResource(R.string.home_placeholder)) }
        // Room below the last item for the floating tab bar (zero, and so no item, wherever it is not drawn).
        if (barClearance > 0.dp) item { Spacer(Modifier.height(barClearance)) }
    }
}

/**
 * Home wired to its [HomeViewModel]: the only place that touches the ViewModel.
 *
 * @param services the open object graph the identity is read through.
 * @param actions the caller's extra header actions, passed on to [HomeScreen].
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
    HomeScreen(state, modifier, actions = actions)
}
