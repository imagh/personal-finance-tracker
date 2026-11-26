package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.common.CollapsingHeaderScaffold
import dev.imagh.pft.app.ui.common.EmptyState

/**
 * The Home placeholder: a "Home" header and one honest line saying the real content is not there yet.
 * C4 replaces it. It holds no state and knows no ViewModel. The attribution line ("<name> · <phone>")
 * is not here: it is the More tab's subtitle (plan D8), and More is reached from the tab bar, so Home
 * has no More button. It reads no identity either: K8's greeting brings the user's name back.
 *
 * @param actions the caller's extra header actions (only `HeaderIconButton`s, UI-14); the debug build
 *   puts its design-catalogue button here, release passes nothing.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CollapsingHeaderScaffold(
        title = stringResource(R.string.home_title),
        modifier = modifier,
        actions = actions,
    ) {
        // K8's LazyColumn must read LocalTabBarClearance for space below its last item; this one centred line
        // cannot reach the bar.
        item { EmptyState(stringResource(R.string.home_placeholder)) }
    }
}
