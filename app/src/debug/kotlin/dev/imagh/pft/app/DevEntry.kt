package dev.imagh.pft.app

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.imagh.pft.app.ui.common.HeaderIconButton
import dev.imagh.pft.app.ui.theme.PftSymbol

/** The debug-only route of the design catalogue. Not in `Routes`, so release has no trace of it. */
private const val DEV_CATALOG = "dev/catalog"

/**
 * Debug builds only: the developer routes of the navigation graph. Today that is the design
 * catalogue ([DesignCatalog]) at `dev/catalog`, opened from Home by [DevHeaderActions]. The release
 * twin adds nothing, so no developer tooling ships.
 */
fun NavGraphBuilder.devRoutes() {
    composable(DEV_CATALOG) { DesignCatalog() }
}

/**
 * Debug builds only: Home's extra header action, a "Design catalogue" button at the end of the
 * header row that calls [navigate] with the catalogue route. The release twin draws nothing.
 *
 * @param navigate opens a route in the navigation host.
 */
@Composable
fun RowScope.DevHeaderActions(navigate: (String) -> Unit) {
    Spacer(Modifier.weight(1f))
    HeaderIconButton(PftSymbol.SETTINGS, "Design catalogue") { navigate(DEV_CATALOG) }
}
