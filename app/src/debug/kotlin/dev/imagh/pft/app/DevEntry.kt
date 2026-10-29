package dev.imagh.pft.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * What `MainActivity` shows until A5 replaces it with the navigation host. In debug builds this
 * becomes the design catalogue (DS2 onwards); nothing here ships in release, whose twin is an
 * empty surface.
 */
@Composable
fun DevContent() {
    Box(Modifier.fillMaxSize())
}
