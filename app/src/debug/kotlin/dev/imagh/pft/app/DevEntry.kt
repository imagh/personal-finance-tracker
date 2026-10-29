package dev.imagh.pft.app

import androidx.compose.runtime.Composable

/**
 * What `MainActivity` shows until A5 replaces it with the navigation host. In debug builds this is
 * the design catalogue ([DesignCatalog]); nothing here ships in release, whose twin is an empty
 * surface.
 */
@Composable
fun DevContent() {
    DesignCatalog()
}
