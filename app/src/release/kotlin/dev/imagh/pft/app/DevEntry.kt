package dev.imagh.pft.app

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder

/**
 * Release twin of the debug developer routes: adds nothing, so no developer tooling ships. The
 * design catalogue lives in `src/debug` only (`ReleaseHasNoCatalogueTest`).
 */
@Suppress("UnusedReceiverParameter")
fun NavGraphBuilder.devRoutes() = Unit

/**
 * Release twin of Home's debug header action: draws nothing.
 *
 * @param navigate unused in release.
 */
@Suppress("UnusedParameter", "UnusedReceiverParameter")
@Composable
fun RowScope.DevHeaderActions(navigate: (String) -> Unit) = Unit
