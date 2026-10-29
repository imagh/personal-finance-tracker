package dev.imagh.pft.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Release twin of the debug design catalogue entry: an empty surface, so no developer tooling
 * ships. `MainActivity` shows this until A5 replaces it with the navigation host.
 */
@Composable
fun DevContent() {
    Box(Modifier.fillMaxSize())
}
